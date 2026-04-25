package kz.dietrix.mealplan.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.dietrix.auth.entity.User;
import kz.dietrix.common.exception.ResourceNotFoundException;
import kz.dietrix.common.service.OpenAiService;
import kz.dietrix.mealplan.dto.*;
import kz.dietrix.mealplan.entity.*;
import kz.dietrix.mealplan.repository.*;
import kz.dietrix.pantry.service.PantryService;
import kz.dietrix.recipes.entity.Recipe;
import kz.dietrix.recipes.entity.RecipeIngredient;
import kz.dietrix.recipes.repository.RecipeRepository;
import kz.dietrix.userprofile.dto.UserPreferenceDto;
import kz.dietrix.userprofile.dto.UserTargetsDto;
import kz.dietrix.userprofile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MealPlanService {

    private final MealPlanRepository mealPlanRepository;
    private final MealPlanMealRepository mealPlanMealRepository;
    private final ShoppingListItemRepository shoppingListItemRepository;
    private final RecipeRepository recipeRepository;
    private final UserProfileService userProfileService;
    private final PantryService pantryService;
    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper;

    /** Self-reference (lazy) so calls to internal @Transactional helpers go through the Spring proxy. */
    @Autowired
    @Lazy
    private MealPlanService self;

    // ─── Get today's plan ─────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public MealPlanDto getCurrentPlan() {
        User user = userProfileService.getCurrentUser();
        List<MealPlan> activePlans = mealPlanRepository.findActiveByUserId(user.getId());
        if (activePlans.isEmpty()) {
            throw new ResourceNotFoundException("No active meal plan found. Generate one first.");
        }
        return toDto(activePlans.get(0));  // latest by createdAt DESC
    }

    @Transactional(readOnly = true)
    public MealPlanDto getPlanById(Long planId) {
        User user = userProfileService.getCurrentUser();
        MealPlan plan = mealPlanRepository.findByIdAndUserId(planId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("MealPlan", "id", planId));
        return toDto(plan);
    }

    // ─── Generate daily plan ──────────────────────────────────────────────────

    /**
     * NOT @Transactional: the OpenAI HTTP call (10–90s) must run OUTSIDE any DB
     * transaction, otherwise it holds a Hikari connection for its full duration
     * and quickly exhausts the pool under concurrent load.
     * DB writes are delegated to {@link #persistGeneratedDailyPlan} via the self-proxy.
     */
    public MealPlanDto generateMealPlan(GenerateMealPlanRequest request) {
        User user = userProfileService.getCurrentUser();
        UserTargetsDto targets = userProfileService.getTargets();
        UserPreferenceDto prefs = userProfileService.getPreferences();

        // 1. Build prompts (no DB connection held).
        String systemPrompt = buildSystemPrompt();
        String userMessage = buildUserMessage(targets, prefs, request);

        // 2. Long-running external call — strictly outside any transaction.
        String aiResponse = openAiService.chat(systemPrompt, userMessage, 3000);

        // 3. Short transactional persist.
        return self.persistGeneratedDailyPlan(user, targets, aiResponse);
    }

    @Transactional
    public MealPlanDto persistGeneratedDailyPlan(User user, UserTargetsDto targets, String aiResponse) {
        LocalDate today = LocalDate.now();

        // Archive all existing active plans for today
        List<MealPlan> existing = mealPlanRepository.findByUserIdAndDate(user.getId(), today);
        existing.forEach(plan -> {
            plan.setStatus(MealPlan.MealPlanStatus.ARCHIVED);
            mealPlanRepository.save(plan);
        });


        // Create plan entity (date = today)
        MealPlan plan = MealPlan.builder()
                .user(user)
                .weekStartDate(today)
                .weekEndDate(today)
                .status(MealPlan.MealPlanStatus.ACTIVE)
                .build();

        // Parse AI → 1 day with 3 meals
        parseDailyPlanFromAI(aiResponse, plan, user, today);

        // Normalize macros to user's daily targets so the dashboard never reports a deficit.
        normalizeToDailyTargets(plan, targets);

        plan = mealPlanRepository.save(plan);

        // Shopping list from the 3 meals
        generateShoppingList(plan);

        log.info("Daily meal plan generated for user: {} ({})", user.getEmail(), today);
        return toDto(plan);
    }

    // ─── Complete meal ────────────────────────────────────────────────────────

    @Transactional
    public MealPlanMealDto completeMeal(Long planId, Long mealId) {
        User user = userProfileService.getCurrentUser();
        mealPlanRepository.findByIdAndUserId(planId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("MealPlan", "id", planId));

        MealPlanMeal meal = mealPlanMealRepository.findByIdAndPlanId(mealId, planId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal", "id", mealId));

        meal.setCompleted(true);
        mealPlanMealRepository.save(meal);
        log.info("Meal {} marked as completed in plan {}", mealId, planId);
        return toMealDto(meal);
    }

    @Transactional
    public MealPlanMealDto uncompleteMeal(Long planId, Long mealId) {
        User user = userProfileService.getCurrentUser();
        mealPlanRepository.findByIdAndUserId(planId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("MealPlan", "id", planId));

        MealPlanMeal meal = mealPlanMealRepository.findByIdAndPlanId(mealId, planId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal", "id", mealId));

        meal.setCompleted(false);
        mealPlanMealRepository.save(meal);
        log.info("Meal {} marked as uncompleted in plan {}", mealId, planId);
        return toMealDto(meal);
    }

    @Transactional
    public MealPlanMealDto toggleMeal(Long planId, Long mealId) {
        User user = userProfileService.getCurrentUser();
        mealPlanRepository.findByIdAndUserId(planId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("MealPlan", "id", planId));

        MealPlanMeal meal = mealPlanMealRepository.findByIdAndPlanId(mealId, planId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal", "id", mealId));

        meal.setCompleted(!meal.isCompleted());
        mealPlanMealRepository.save(meal);
        log.info("Meal {} toggled to {} in plan {}", mealId, meal.isCompleted() ? "completed" : "uncompleted", planId);
        return toMealDto(meal);
    }

    @Transactional
    public MealPlanMealDto updateMeal(Long planId, Long mealId, kz.dietrix.mealplan.dto.UpdateMealRequest request) {
        User user = userProfileService.getCurrentUser();
        mealPlanRepository.findByIdAndUserId(planId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("MealPlan", "id", planId));

        MealPlanMeal meal = mealPlanMealRepository.findByIdAndPlanId(mealId, planId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal", "id", mealId));

        if (request.getRecipeId() != null) {
            Recipe newRecipe = recipeRepository.findById(request.getRecipeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", request.getRecipeId()));
            meal.setRecipe(newRecipe);
            log.info("Meal {} in plan {} swapped to recipe '{}'", mealId, planId, newRecipe.getTitle());
        }

        if (request.getCompleted() != null) {
            meal.setCompleted(request.getCompleted());
        }

        mealPlanMealRepository.save(meal);
        return toMealDto(meal);
    }

    // ─── Add meal ─────────────────────────────────────────────────────────────

    @Transactional
    public MealPlanMealDto addMeal(Long planId, kz.dietrix.mealplan.dto.AddMealRequest request) {
        User user = userProfileService.getCurrentUser();
        MealPlan plan = mealPlanRepository.findByIdAndUserId(planId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("MealPlan", "id", planId));

        Recipe recipe = recipeRepository.findById(request.getRecipeId())
                .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", request.getRecipeId()));

        String mealTypeStr = request.getMealType() != null ? request.getMealType().toUpperCase() : "MAIN";
        if (mealTypeStr.equals("LUNCH") || mealTypeStr.equals("DINNER")) {
            mealTypeStr = "MAIN";
        }
        MealPlanMeal.MealType mealType;
        try {
            mealType = MealPlanMeal.MealType.valueOf(mealTypeStr);
        } catch (IllegalArgumentException e) {
            mealType = MealPlanMeal.MealType.MAIN;
        }

        // Get the first (only) day of the plan
        MealPlanDay day = plan.getDays().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No day found in meal plan"));

        MealPlanMeal meal = MealPlanMeal.builder()
                .mealPlanDay(day)
                .mealType(mealType)
                .recipe(recipe)
                .completed(false)
                .build();

        mealPlanMealRepository.save(meal);
        log.info("Meal added to plan {}: recipe '{}' as {}", planId, recipe.getTitle(), mealType);
        return toMealDto(meal);
    }

    // ─── Delete meal ──────────────────────────────────────────────────────────

    @Transactional
    public void deleteMeal(Long planId, Long mealId) {
        User user = userProfileService.getCurrentUser();
        mealPlanRepository.findByIdAndUserId(planId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("MealPlan", "id", planId));

        MealPlanMeal meal = mealPlanMealRepository.findByIdAndPlanId(mealId, planId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal", "id", mealId));

        mealPlanMealRepository.delete(meal);
        log.info("Meal {} removed from plan {}", mealId, planId);
    }

    // ─── Generate single meal ─────────────────────────────────────────────────

    /**
     * NOT @Transactional: the OpenAI HTTP call must run outside any DB transaction
     * to avoid holding a Hikari connection while the external call is in flight.
     */
    public MealPlanMealDto generateMeal(Long planId, kz.dietrix.mealplan.dto.GenerateMealRequest request) {
        User user = userProfileService.getCurrentUser();
        // Validate ownership in a short read-only tx
        MealPlan plan = self.loadOwnedPlan(user.getId(), planId);

        UserTargetsDto targets = userProfileService.getTargets();
        UserPreferenceDto prefs = userProfileService.getPreferences();

        // Determine meal type
        String mealTypeStr = request.getMealType() != null ? request.getMealType().toUpperCase() : "MAIN";
        if (mealTypeStr.equals("LUNCH") || mealTypeStr.equals("DINNER")) {
            mealTypeStr = "MAIN";
        }
        MealPlanMeal.MealType mealType;
        try {
            mealType = MealPlanMeal.MealType.valueOf(mealTypeStr);
        } catch (IllegalArgumentException e) {
            mealType = MealPlanMeal.MealType.MAIN;
        }

        // Build AI prompt for single recipe
        String systemPrompt = buildSingleRecipePrompt();
        String userMessage = buildSingleRecipeUserMessage(targets, prefs, request, mealType);

        // External call — strictly outside a transaction.
        String aiResponse = openAiService.chat(systemPrompt, userMessage, 2000);

        // Short transactional persist.
        return self.persistGeneratedSingleMeal(user, plan.getId(), mealType, aiResponse);
    }

    @Transactional(readOnly = true)
    public MealPlan loadOwnedPlan(Long userId, Long planId) {
        return mealPlanRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("MealPlan", "id", planId));
    }

    @Transactional
    public MealPlanMealDto persistGeneratedSingleMeal(User user, Long planId,
                                                      MealPlanMeal.MealType mealType, String aiResponse) {
        MealPlan plan = mealPlanRepository.findByIdAndUserId(planId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("MealPlan", "id", planId));

        Recipe recipe = createRecipeFromAIResponse(aiResponse, user);
        recipe = recipeRepository.save(recipe);

        MealPlanDay day = plan.getDays().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No day found in meal plan"));

        MealPlanMeal meal = MealPlanMeal.builder()
                .mealPlanDay(day)
                .mealType(mealType)
                .recipe(recipe)
                .completed(false)
                .build();

        mealPlanMealRepository.save(meal);
        log.info("AI-generated meal added to plan {}: '{}' as {}", planId, recipe.getTitle(), mealType);
        return toMealDto(meal);
    }

    private String buildSingleRecipePrompt() {
        return """
                You are a nutritionist API. Return ONLY valid JSON, nothing else.
                Generate exactly 1 recipe.
                
                STRICT JSON SCHEMA:
                {
                  "title": "string",
                  "description": "string (1 sentence)",
                  "instructions": "string (2-3 numbered steps)",
                  "calories": number,
                  "protein": number,
                  "carbs": number,
                  "fat": number,
                  "cookTimeMinutes": number,
                  "cuisine": "string",
                  "ingredients": [
                    { "name": "string", "amount": "string", "unit": "string" }
                  ]
                }
                
                RULES:
                - 3-5 ingredients per recipe
                - All number fields must be integers
                - No trailing commas, no comments, no markdown
                """;
    }

    private String buildSingleRecipeUserMessage(UserTargetsDto targets, UserPreferenceDto prefs,
                                                 kz.dietrix.mealplan.dto.GenerateMealRequest request,
                                                 MealPlanMeal.MealType mealType) {
        StringBuilder sb = new StringBuilder();
        sb.append("Generate 1 recipe for: ").append(mealType.name()).append("\n");

        sb.append("\n== USER ==\n");
        sb.append("- Gender: ").append(targets.getGender()).append(", Age: ").append(targets.getAge()).append("\n");
        sb.append("- Weight: ").append(targets.getWeightKg()).append(" kg, Height: ").append(targets.getHeightCm()).append(" cm\n");
        sb.append("- Goal: ").append(targets.getGoal()).append("\n");
        sb.append("- Daily calories: ").append(targets.getDailyCalories()).append(" kcal\n");

        if (request.getCuisine() != null && !request.getCuisine().isBlank()) {
            sb.append("- Cuisine: ").append(request.getCuisine()).append("\n");
        }
        if (request.getMaxCalories() != null) {
            sb.append("- Max calories for this meal: ").append(request.getMaxCalories()).append("\n");
        }
        if (prefs.getAllergies() != null && !prefs.getAllergies().isEmpty()) {
            sb.append("- AVOID (allergies): ").append(prefs.getAllergies()).append("\n");
        }
        if (prefs.getDietType() != null) {
            sb.append("- Diet: ").append(prefs.getDietType()).append("\n");
        }
        if (request.isUsePantry()) {
            List<String> pantryItems = pantryService.getPantryIngredientNames();
            if (!pantryItems.isEmpty()) {
                sb.append("- Use ingredients: ").append(pantryItems).append("\n");
            }
        }
        if (request.getAdditionalNotes() != null) {
            sb.append("- Notes: ").append(request.getAdditionalNotes()).append("\n");
        }
        return sb.toString();
    }

    private Recipe createRecipeFromAIResponse(String aiResponse, User user) {
        try {
            String json = aiResponse.trim();
            if (json.startsWith("```")) {
                json = json.replaceAll("```json\\s*", "").replaceAll("```\\s*", "");
            }
            JsonNode node = objectMapper.readTree(json);
            String title = node.path("title").asText("AI Generated Meal");

            Optional<Recipe> existing = recipeRepository.findByTitle(title);
            if (existing.isPresent()) {
                return existing.get();
            }

            return createRecipeFromNode(node, user);
        } catch (Exception e) {
            log.error("Failed to parse AI single recipe: {}", e.getMessage());
            return Recipe.builder()
                    .title("AI Meal " + System.currentTimeMillis())
                    .description("Generated recipe")
                    .instructions(aiResponse)
                    .calories(400)
                    .generated(true)
                    .generatedForUser(user)
                    .build();
        }
    }

    // ─── Shopping list ────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ShoppingListItemDto> getShoppingList(Long planId) {
        User user = userProfileService.getCurrentUser();
        mealPlanRepository.findByIdAndUserId(planId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("MealPlan", "id", planId));

        return shoppingListItemRepository.findByMealPlanId(planId).stream()
                .map(this::toShoppingDto)
                .toList();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  AI prompt & parsing (DAILY)
    // ═══════════════════════════════════════════════════════════════════════════

    private String buildSystemPrompt() {
        return """
                You are a nutritionist API. Return ONLY valid JSON, nothing else.

                STRICT JSON SCHEMA (follow exactly):
                {
                  "meals": [
                    {
                      "mealType": "BREAKFAST",
                      "recipe": {
                        "title": "string",
                        "description": "string (1 sentence)",
                        "instructions": "string (2-3 short steps)",
                        "calories": number,
                        "protein": number,
                        "carbs": number,
                        "fat": number,
                        "cookTimeMinutes": number,
                        "cuisine": "string",
                        "ingredients": [
                          { "name": "string", "amount": "string", "unit": "string" }
                        ]
                      }
                    }
                  ]
                }

                RULES:
                - "meals" array must contain EXACTLY 3 objects with mealType: "BREAKFAST", "MAIN", "MAIN"
                - First MAIN = lunch, second MAIN = dinner. Both use mealType "MAIN"
                - Each recipe must have 3-5 ingredients
                - All number fields must be integers

                CRITICAL — NUTRITION TARGETS:
                - The SUM of calories across the 3 meals MUST equal the user's daily calorie target ±3%.
                - The SUM of protein, carbs and fat across the 3 meals MUST match the daily macro targets ±5%.
                - Suggested split: BREAKFAST ≈ 25% of daily kcal, LUNCH ≈ 40%, DINNER ≈ 35%.
                - Adjust portion sizes (grams in ingredients) to hit these totals — do NOT serve tiny portions.
                - If unsure, ROUND UP rather than DOWN. Better slightly over than under.

                FORMATTING:
                - No trailing commas, no comments, no markdown, no code fences.
                """;
    }

    private String buildUserMessage(UserTargetsDto targets, UserPreferenceDto prefs,
                                     GenerateMealPlanRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("Generate a daily meal plan (breakfast, lunch, dinner) for today.\n");

        // User profile
        sb.append("\n== USER PROFILE ==\n");
        sb.append("- Gender: ").append(targets.getGender()).append("\n");
        sb.append("- Age: ").append(targets.getAge()).append("\n");
        sb.append("- Weight: ").append(targets.getWeightKg()).append(" kg\n");
        sb.append("- Height: ").append(targets.getHeightCm()).append(" cm\n");
        sb.append("- BMI: ").append(targets.getBmi()).append(" (").append(targets.getBmiCategory()).append(")\n");
        sb.append("- Goal: ").append(targets.getGoal()).append("\n");

        // Targets — explicit per-meal split so AI can't undershoot
        int dailyKcal = targets.getDailyCalories();
        int kcalBreakfast = (int) Math.round(dailyKcal * 0.25);
        int kcalLunch     = (int) Math.round(dailyKcal * 0.40);
        int kcalDinner    = dailyKcal - kcalBreakfast - kcalLunch;

        int p = targets.getProteinGrams();
        int c = targets.getCarbsGrams();
        int f = targets.getFatGrams();

        sb.append("\n== DAILY TARGETS (sum of all 3 meals) ==\n");
        sb.append("- Calories: ").append(dailyKcal).append(" kcal  (REQUIRED total ±3%)\n");
        sb.append("- Protein:  ").append(p).append(" g\n");
        sb.append("- Carbs:    ").append(c).append(" g\n");
        sb.append("- Fat:      ").append(f).append(" g\n");

        sb.append("\n== PER-MEAL CALORIE TARGETS ==\n");
        sb.append("- BREAKFAST: ~").append(kcalBreakfast).append(" kcal\n");
        sb.append("- LUNCH (1st MAIN): ~").append(kcalLunch).append(" kcal\n");
        sb.append("- DINNER (2nd MAIN): ~").append(kcalDinner).append(" kcal\n");

        // Preferences
        sb.append("\n== PREFERENCES ==\n");
        if (request.getCuisine() != null && !request.getCuisine().isBlank()) {
            sb.append("- TODAY's preferred cuisine: ").append(request.getCuisine()).append("\n");
        }
        if (prefs.getAllergies() != null && !prefs.getAllergies().isEmpty()) {
            sb.append("- MUST AVOID (allergies): ").append(prefs.getAllergies()).append("\n");
        }
        if (prefs.getDislikedFoods() != null && !prefs.getDislikedFoods().isEmpty()) {
            sb.append("- Avoid foods: ").append(prefs.getDislikedFoods()).append("\n");
        }
        if (prefs.getDietType() != null) {
            sb.append("- Diet type: ").append(prefs.getDietType()).append("\n");
        }
        if (prefs.getCuisinePreferences() != null && !prefs.getCuisinePreferences().isEmpty()) {
            sb.append("- General cuisine preferences: ").append(prefs.getCuisinePreferences()).append("\n");
        }

        if (request.isUsePantry()) {
            List<String> pantryItems = pantryService.getPantryIngredientNames();
            if (!pantryItems.isEmpty()) {
                sb.append("- Available pantry ingredients: ").append(pantryItems).append("\n");
            }
        }

        if (request.getAdditionalNotes() != null) {
            sb.append("- Notes: ").append(request.getAdditionalNotes()).append("\n");
        }

        return sb.toString();
    }

    private void parseDailyPlanFromAI(String aiResponse, MealPlan plan, User user, LocalDate date) {
        try {
            String json = aiResponse.trim();
            if (json.startsWith("```")) {
                json = json.replaceAll("```json\\s*", "").replaceAll("```\\s*", "");
            }

            JsonNode root = objectMapper.readTree(json);
            JsonNode mealsNode = root.path("meals");

            if (!mealsNode.isArray() || mealsNode.isEmpty()) {
                log.warn("AI returned no 'meals' array. Raw response: {}", aiResponse);
                createDefaultDay(plan, date, user);
                return;
            }

            MealPlanDay day = MealPlanDay.builder()
                    .mealPlan(plan)
                    .date(date)
                    .dayOfWeek(date.getDayOfWeek())
                    .build();

            for (JsonNode mealNode : mealsNode) {
                Recipe recipe = createRecipeFromNode(mealNode.path("recipe"), user);
                recipe = recipeRepository.save(recipe);

                String mealTypeStr = mealNode.path("mealType").asText("MAIN").toUpperCase();
                if (mealTypeStr.equals("LUNCH") || mealTypeStr.equals("DINNER")) {
                    mealTypeStr = "MAIN";
                }
                MealPlanMeal.MealType mealType;
                try {
                    mealType = MealPlanMeal.MealType.valueOf(mealTypeStr);
                } catch (IllegalArgumentException e) {
                    mealType = MealPlanMeal.MealType.MAIN;
                }

                MealPlanMeal meal = MealPlanMeal.builder()
                        .mealPlanDay(day)
                        .mealType(mealType)
                        .recipe(recipe)
                        .completed(false)
                        .build();
                day.getMeals().add(meal);
            }

            plan.getDays().add(day);
        } catch (Exception e) {
            log.error("Failed to parse AI daily meal plan: {}\n--- Raw AI response ---\n{}\n-----------------------",
                    e.getMessage(), aiResponse);
            createDefaultDay(plan, date, user);
        }
    }

    private Recipe createRecipeFromNode(JsonNode node, User user) {
        String title = node.path("title").asText("AI Meal");

        // Return existing recipe if title already exists
        Optional<Recipe> existing = recipeRepository.findByTitle(title);
        if (existing.isPresent()) {
            return existing.get();
        }

        Recipe recipe = Recipe.builder()
                .title(title)
                .description(node.path("description").asText(""))
                .instructions(node.path("instructions").asText(""))
                .calories(node.path("calories").asInt(400))
                .protein(node.path("protein").asInt(25))
                .carbs(node.path("carbs").asInt(45))
                .fat(node.path("fat").asInt(15))
                .cookTimeMinutes(node.path("cookTimeMinutes").asInt(30))
                .cuisine(node.path("cuisine").asText(null))
                .mealType(node.path("mealType").asText(null))
                .generated(true)
                .generatedForUser(user)
                .build();

        JsonNode ingredientsNode = node.path("ingredients");
        if (ingredientsNode.isArray()) {
            for (JsonNode ing : ingredientsNode) {
                RecipeIngredient ingredient = RecipeIngredient.builder()
                        .name(ing.path("name").asText())
                        .amount(ing.path("amount").asText())
                        .unit(ing.path("unit").asText())
                        .build();
                recipe.addIngredient(ingredient);
            }
        }
        return recipe;
    }

    private void createDefaultDay(MealPlan plan, LocalDate date, User user) {
        MealPlanDay day = MealPlanDay.builder()
                .mealPlan(plan)
                .date(date)
                .dayOfWeek(date.getDayOfWeek())
                .build();

        // Provide minimal placeholder recipes so the UI is never empty after a failed AI call.
        // The user can swap or regenerate them later.
        addPlaceholder(day, user, MealPlanMeal.MealType.BREAKFAST,
                "Завтрак", 400, 20, 50, 12);
        addPlaceholder(day, user, MealPlanMeal.MealType.MAIN,
                "Обед",    600, 35, 70, 18);
        addPlaceholder(day, user, MealPlanMeal.MealType.MAIN,
                "Ужин",    500, 30, 55, 15);

        plan.getDays().add(day);
    }

    private void addPlaceholder(MealPlanDay day, User user, MealPlanMeal.MealType type,
                                String title, int kcal, int p, int c, int f) {
        Recipe recipe = recipeRepository.findByTitle(title).orElseGet(() -> {
            Recipe r = Recipe.builder()
                    .title(title)
                    .description("Заполнитель — AI не смог сгенерировать рецепт. Замените или сгенерируйте новый.")
                    .instructions("—")
                    .calories(kcal).protein(p).carbs(c).fat(f)
                    .cookTimeMinutes(20)
                    .generated(true)
                    .generatedForUser(user)
                    .build();
            return recipeRepository.save(r);
        });
        MealPlanMeal meal = MealPlanMeal.builder()
                .mealPlanDay(day)
                .mealType(type)
                .recipe(recipe)
                .completed(false)
                .build();
        day.getMeals().add(meal);
    }

    /**
     * Ensures the SUM of calories/macros across all meals matches the user's daily targets
     * within ±5%. If AI undershot or overshot, scales each recipe proportionally and persists
     * the updated values. Only touches recipes that were generated for THIS user (does not
     * mutate shared/public recipes).
     */
    private void normalizeToDailyTargets(MealPlan plan, UserTargetsDto targets) {
        int targetKcal = targets.getDailyCalories();
        if (targetKcal <= 0) return;

        List<MealPlanMeal> meals = plan.getDays().stream()
                .flatMap(d -> d.getMeals().stream())
                .filter(m -> m.getRecipe() != null)
                .toList();
        if (meals.isEmpty()) return;

        int sumKcal = meals.stream()
                .mapToInt(m -> nz(m.getRecipe().getCalories()))
                .sum();
        if (sumKcal <= 0) return;

        double diffPct = Math.abs(sumKcal - targetKcal) * 100.0 / targetKcal;
        if (diffPct <= 5.0) {
            log.debug("Plan calories {}/{} kcal within ±5% — no scaling needed", sumKcal, targetKcal);
            return;
        }

        double factor = (double) targetKcal / sumKcal;
        log.info("Normalizing plan macros: AI gave {} kcal, target {} kcal → scale by {}",
                sumKcal, targetKcal, String.format("%.3f", factor));

        for (MealPlanMeal m : meals) {
            Recipe r = m.getRecipe();
            // Skip recipes that aren't ours (don't mutate seeded/public recipes)
            if (!r.isGenerated()) continue;

            r.setCalories(scale(r.getCalories(), factor));
            r.setProtein(scale(r.getProtein(),   factor));
            r.setCarbs(scale(r.getCarbs(),       factor));
            r.setFat(scale(r.getFat(),           factor));
            recipeRepository.save(r);
        }
    }

    private static int scale(Integer value, double factor) {
        if (value == null) return 0;
        return (int) Math.round(value * factor);
    }

    private static int nz(Integer value) {
        return value == null ? 0 : value;
    }

    private void generateShoppingList(MealPlan plan) {
        Map<String, ShoppingListItem> itemMap = new LinkedHashMap<>();

        for (MealPlanDay day : plan.getDays()) {
            for (MealPlanMeal meal : day.getMeals()) {
                if (meal.getRecipe() != null) {
                    for (RecipeIngredient ing : meal.getRecipe().getIngredients()) {
                        String key = ing.getName().toLowerCase();
                        if (!itemMap.containsKey(key)) {
                            itemMap.put(key, ShoppingListItem.builder()
                                    .mealPlan(plan)
                                    .name(ing.getName())
                                    .amount(ing.getAmount())
                                    .unit(ing.getUnit())
                                    .purchased(false)
                                    .build());
                        }
                    }
                }
            }
        }

        plan.getShoppingList().addAll(itemMap.values());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Mapping
    // ═══════════════════════════════════════════════════════════════════════════

    private MealPlanDto toDto(MealPlan plan) {
        long total = mealPlanMealRepository.countByPlanId(plan.getId());
        long completed = mealPlanMealRepository.countCompletedByPlanId(plan.getId());

        // Flatten: plan → single day → meals
        List<MealPlanMealDto> mealDtos = plan.getDays().stream()
                .flatMap(day -> day.getMeals().stream())
                .map(this::toMealDto)
                .toList();

        int totalCalories = mealDtos.stream()
                .filter(m -> m.getCalories() != null)
                .mapToInt(MealPlanMealDto::getCalories)
                .sum();

        return MealPlanDto.builder()
                .id(plan.getId())
                .date(plan.getWeekStartDate())
                .status(plan.getStatus().name())
                .totalMeals((int) total)
                .completedMeals((int) completed)
                .totalCalories(totalCalories)
                .meals(mealDtos)
                .build();
    }

    private MealPlanMealDto toMealDto(MealPlanMeal meal) {
        return MealPlanMealDto.builder()
                .id(meal.getId())
                .mealType(meal.getMealType().name())
                .recipeId(meal.getRecipe() != null ? meal.getRecipe().getId() : null)
                .recipeTitle(meal.getRecipe() != null ? meal.getRecipe().getTitle() : null)
                .calories(meal.getRecipe() != null ? meal.getRecipe().getCalories() : null)
                .protein(meal.getRecipe() != null ? meal.getRecipe().getProtein() : null)
                .carbs(meal.getRecipe() != null ? meal.getRecipe().getCarbs() : null)
                .fat(meal.getRecipe() != null ? meal.getRecipe().getFat() : null)
                .completed(meal.isCompleted())
                .build();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  History & Calendar
    // ═══════════════════════════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public List<MealPlanDto> getHistory(LocalDate from, LocalDate to) {
        User user = userProfileService.getCurrentUser();
        if (from == null) from = LocalDate.now().minusDays(30);
        if (to == null) to = LocalDate.now();
        return mealPlanRepository.findByUserIdAndDateRange(user.getId(), from, to)
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public MealPlanDto getPlanByDate(LocalDate date) {
        User user = userProfileService.getCurrentUser();
        List<kz.dietrix.mealplan.entity.MealPlan> list =
                mealPlanRepository.findByUserIdAndDateRange(user.getId(), date, date);
        if (list.isEmpty()) {
            throw new ResourceNotFoundException("No meal plan for " + date);
        }
        return toDto(list.get(0));
    }

    @Transactional(readOnly = true)
    public kz.dietrix.mealplan.dto.MealPlanCalendarDto getCalendar(LocalDate from, LocalDate to) {
        User user = userProfileService.getCurrentUser();
        if (from == null) from = LocalDate.now().withDayOfMonth(1);
        if (to == null)   to = from.plusMonths(1).minusDays(1);

        List<kz.dietrix.mealplan.entity.MealPlan> plans =
                mealPlanRepository.findByUserIdAndDateRange(user.getId(), from, to);

        // Group by date, take latest plan per date
        Map<LocalDate, kz.dietrix.mealplan.entity.MealPlan> byDate = new LinkedHashMap<>();
        for (kz.dietrix.mealplan.entity.MealPlan p : plans) {
            byDate.putIfAbsent(p.getWeekStartDate(), p); // first wins (already DESC by createdAt)
        }

        List<kz.dietrix.mealplan.dto.MealPlanCalendarDto.DayEntry> days = new ArrayList<>();
        LocalDate cur = from;
        while (!cur.isAfter(to)) {
            kz.dietrix.mealplan.entity.MealPlan p = byDate.get(cur);
            if (p == null) {
                days.add(kz.dietrix.mealplan.dto.MealPlanCalendarDto.DayEntry.builder()
                        .date(cur).planId(null)
                        .totalMeals(0).completedMeals(0)
                        .status("EMPTY").emoji("❌").build());
            } else {
                long total = mealPlanMealRepository.countByPlanId(p.getId());
                long done  = mealPlanMealRepository.countCompletedByPlanId(p.getId());
                String status; String emoji;
                if (total == 0) { status = "EMPTY"; emoji = "❌"; }
                else if (done == total) { status = "FULL"; emoji = "✅"; }
                else if (done > 0) { status = "PARTIAL"; emoji = "⚠️"; }
                else { status = "EMPTY"; emoji = "❌"; }
                days.add(kz.dietrix.mealplan.dto.MealPlanCalendarDto.DayEntry.builder()
                        .date(cur).planId(p.getId())
                        .totalMeals((int) total).completedMeals((int) done)
                        .status(status).emoji(emoji).build());
            }
            cur = cur.plusDays(1);
        }

        return kz.dietrix.mealplan.dto.MealPlanCalendarDto.builder()
                .from(from).to(to).days(days).build();
    }

    @Transactional
    public MealPlanDto duplicateYesterday() {
        User user = userProfileService.getCurrentUser();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        List<kz.dietrix.mealplan.entity.MealPlan> ylist =
                mealPlanRepository.findByUserIdAndDateRange(user.getId(), yesterday, yesterday);
        if (ylist.isEmpty()) {
            throw new ResourceNotFoundException("No plan to duplicate from " + yesterday);
        }
        kz.dietrix.mealplan.entity.MealPlan src = ylist.get(0);

        // Archive existing today plans
        mealPlanRepository.findByUserIdAndDate(user.getId(), today).forEach(p -> {
            p.setStatus(kz.dietrix.mealplan.entity.MealPlan.MealPlanStatus.ARCHIVED);
            mealPlanRepository.save(p);
        });

        kz.dietrix.mealplan.entity.MealPlan copy = kz.dietrix.mealplan.entity.MealPlan.builder()
                .user(user)
                .weekStartDate(today)
                .weekEndDate(today)
                .status(kz.dietrix.mealplan.entity.MealPlan.MealPlanStatus.ACTIVE)
                .build();

        for (kz.dietrix.mealplan.entity.MealPlanDay srcDay : src.getDays()) {
            kz.dietrix.mealplan.entity.MealPlanDay newDay = kz.dietrix.mealplan.entity.MealPlanDay.builder()
                    .mealPlan(copy)
                    .date(today)
                    .dayOfWeek(today.getDayOfWeek())
                    .build();
            for (kz.dietrix.mealplan.entity.MealPlanMeal m : srcDay.getMeals()) {
                kz.dietrix.mealplan.entity.MealPlanMeal nm = kz.dietrix.mealplan.entity.MealPlanMeal.builder()
                        .mealPlanDay(newDay)
                        .recipe(m.getRecipe())
                        .mealType(m.getMealType())
                        .completed(false)
                        .build();
                newDay.getMeals().add(nm);
            }
            copy.getDays().add(newDay);
        }
        generateShoppingList(copy);
        copy = mealPlanRepository.save(copy);
        return toDto(copy);
    }

    @Transactional(readOnly = true)
    public int sumCompletedCaloriesForDate(Long userId, LocalDate date) {
        List<kz.dietrix.mealplan.entity.MealPlan> list =
                mealPlanRepository.findByUserIdAndDateRange(userId, date, date);
        if (list.isEmpty()) return 0;
        return list.get(0).getDays().stream()
                .flatMap(d -> d.getMeals().stream())
                .filter(MealPlanMeal::isCompleted)
                .filter(m -> m.getRecipe() != null && m.getRecipe().getCalories() != null)
                .mapToInt(m -> m.getRecipe().getCalories())
                .sum();
    }

    @Transactional(readOnly = true)
    public int[] sumCompletedMacrosForDate(Long userId, LocalDate date) {
        List<kz.dietrix.mealplan.entity.MealPlan> list =
                mealPlanRepository.findByUserIdAndDateRange(userId, date, date);
        if (list.isEmpty()) return new int[]{0, 0, 0};
        int p = 0, c = 0, f = 0;
        for (kz.dietrix.mealplan.entity.MealPlanDay d : list.get(0).getDays()) {
            for (kz.dietrix.mealplan.entity.MealPlanMeal m : d.getMeals()) {
                if (!m.isCompleted() || m.getRecipe() == null) continue;
                if (m.getRecipe().getProtein() != null) p += m.getRecipe().getProtein();
                if (m.getRecipe().getCarbs()   != null) c += m.getRecipe().getCarbs();
                if (m.getRecipe().getFat()     != null) f += m.getRecipe().getFat();
            }
        }
        return new int[]{p, c, f};
    }

    /** Streak = consecutive days (ending today) where a plan exists with ≥1 completed meal. */
    @Transactional(readOnly = true)
    public int computeStreak(Long userId) {
        LocalDate cur = LocalDate.now();
        int streak = 0;
        for (int i = 0; i < 365; i++) {
            List<kz.dietrix.mealplan.entity.MealPlan> list =
                    mealPlanRepository.findByUserIdAndDateRange(userId, cur, cur);
            if (list.isEmpty()) break;
            long done = mealPlanMealRepository.countCompletedByPlanId(list.get(0).getId());
            if (done == 0) break;
            streak++;
            cur = cur.minusDays(1);
        }
        return streak;
    }

    private ShoppingListItemDto toShoppingDto(ShoppingListItem item) {
        return ShoppingListItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .amount(item.getAmount())
                .unit(item.getUnit())
                .purchased(item.isPurchased())
                .build();
    }
}
