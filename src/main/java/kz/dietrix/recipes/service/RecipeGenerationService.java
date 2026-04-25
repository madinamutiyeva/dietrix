package kz.dietrix.recipes.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.dietrix.auth.entity.User;
import kz.dietrix.common.service.OpenAiService;
import kz.dietrix.mealplan.entity.MealPlan;
import kz.dietrix.mealplan.entity.MealPlanDay;
import kz.dietrix.mealplan.entity.MealPlanMeal;
import kz.dietrix.mealplan.repository.MealPlanMealRepository;
import kz.dietrix.mealplan.repository.MealPlanRepository;
import kz.dietrix.notification.NotificationService;
import kz.dietrix.notification.NotificationType;
import kz.dietrix.pantry.service.PantryService;
import kz.dietrix.recipes.dto.GenerateRecipeRequest;
import kz.dietrix.recipes.entity.Recipe;
import kz.dietrix.recipes.entity.RecipeIngredient;
import kz.dietrix.recipes.repository.RecipeRepository;
import kz.dietrix.userprofile.dto.UserPreferenceDto;
import kz.dietrix.userprofile.dto.UserTargetsDto;
import kz.dietrix.userprofile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeGenerationService {

    private final OpenAiService openAiService;
    private final RecipeRepository recipeRepository;
    private final UserProfileService userProfileService;
    private final PantryService pantryService;
    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;
    private final MealPlanRepository mealPlanRepository;
    private final MealPlanMealRepository mealPlanMealRepository;

    /**
     * NOTE: intentionally NOT annotated with @Transactional.
     * The OpenAI HTTP call inside (10–60s) would otherwise hold a JDBC connection
     * for its full duration, exhausting the Hikari pool under concurrent load.
     * Each repository.save() below runs in its own short-lived Spring Data transaction.
     */
    public Recipe generateRecipe(GenerateRecipeRequest request) {
        User user = userProfileService.getCurrentUser();
        UserTargetsDto targets = userProfileService.getTargets();
        UserPreferenceDto prefs = userProfileService.getPreferences();

        String systemPrompt = buildSystemPrompt();
        String userMessage = buildUserMessage(request, targets, prefs, user);

        String aiResponse = openAiService.chat(systemPrompt, userMessage);
        Recipe recipe = parseRecipeFromAI(aiResponse, user);

        recipe = recipeRepository.save(recipe);
        log.info("Recipe generated: '{}' for user: {}", recipe.getTitle(), user.getEmail());

        // Optionally attach to today's plan
        if (request.isAddToPlan()) {
            attachToTodayPlan(user, recipe, request.getMealType());
        }

        notificationService.createNotification(
                user,
                NotificationType.RECIPE_READY,
                "Рецепт готов!",
                "AI создал для вас: " + recipe.getTitle()
        );

        return recipe;
    }

    /** Attach a recipe to today's active plan as a new meal. Creates a plan/day if missing. */
    private void attachToTodayPlan(User user, Recipe recipe, String mealTypeStr) {
        LocalDate today = LocalDate.now();

        MealPlan plan = mealPlanRepository.findByUserIdAndDate(user.getId(), today)
                .stream().findFirst()
                .orElseGet(() -> {
                    MealPlan p = MealPlan.builder()
                            .user(user)
                            .weekStartDate(today)
                            .weekEndDate(today)
                            .status(MealPlan.MealPlanStatus.ACTIVE)
                            .build();
                    return mealPlanRepository.save(p);
                });

        MealPlanDay day = plan.getDays().stream().findFirst().orElseGet(() -> {
            MealPlanDay d = MealPlanDay.builder()
                    .mealPlan(plan)
                    .date(today)
                    .dayOfWeek(today.getDayOfWeek())
                    .build();
            plan.getDays().add(d);
            mealPlanRepository.save(plan);
            return d;
        });

        MealPlanMeal.MealType mt = mapMealType(mealTypeStr);

        MealPlanMeal meal = MealPlanMeal.builder()
                .mealPlanDay(day)
                .mealType(mt)
                .recipe(recipe)
                .completed(false)
                .build();
        mealPlanMealRepository.save(meal);
        log.info("Generated recipe '{}' attached to today's plan {} as {}",
                recipe.getTitle(), plan.getId(), mt);
    }

    private MealPlanMeal.MealType mapMealType(String raw) {
        if (raw == null) return MealPlanMeal.MealType.MAIN;
        String s = raw.trim().toUpperCase();
        if (s.equals("LUNCH") || s.equals("DINNER")) return MealPlanMeal.MealType.MAIN;
        try {
            return MealPlanMeal.MealType.valueOf(s);
        } catch (IllegalArgumentException e) {
            return MealPlanMeal.MealType.MAIN;
        }
    }

    private String buildSystemPrompt() {
        return """
                You are a nutritionist API. Return ONLY valid JSON, nothing else.
                
                STRICT JSON SCHEMA (follow exactly):
                {
                  "title": "string",
                  "description": "string (1 sentence)",
                  "instructions": "string (numbered steps)",
                  "calories": number,
                  "protein": number,
                  "carbs": number,
                  "fat": number,
                  "cookTimeMinutes": number,
                  "cuisine": "string",
                  "mealType": "string",
                  "dietType": "string",
                  "ingredients": [
                    { "name": "string", "amount": "string", "unit": "string" }
                  ]
                }
                
                RULES:
                - All number fields must be integers
                - 4-8 ingredients per recipe
                - No trailing commas, no comments, no markdown
                """;
    }

    private String buildUserMessage(GenerateRecipeRequest request, UserTargetsDto targets,
                                     UserPreferenceDto prefs, User user) {
        StringBuilder sb = new StringBuilder();
        sb.append("Generate a recipe for the following user:\n");

        // User profile data
        sb.append("\n== USER PROFILE ==\n");
        sb.append("- Gender: ").append(targets.getGender()).append("\n");
        sb.append("- Age: ").append(targets.getAge()).append("\n");
        sb.append("- Weight: ").append(targets.getWeightKg()).append(" kg\n");
        sb.append("- Height: ").append(targets.getHeightCm()).append(" cm\n");
        sb.append("- BMI: ").append(targets.getBmi()).append(" (").append(targets.getBmiCategory()).append(")\n");
        sb.append("- Activity level: ").append(targets.getActivityLevel()).append("\n");
        sb.append("- Goal: ").append(targets.getGoal()).append("\n");

        // Recipe requirements
        sb.append("\n== RECIPE REQUIREMENTS ==\n");
        if (request.getMealType() != null) {
            sb.append("- Meal type: ").append(request.getMealType()).append("\n");
        }
        if (request.getCuisine() != null) {
            sb.append("- Cuisine: ").append(request.getCuisine()).append("\n");
        }

        int maxCal = request.getMaxCalories() != null ? request.getMaxCalories() : targets.getDailyCalories() / 3;
        sb.append("- Maximum calories: ").append(maxCal).append("\n");
        sb.append("- Target protein: ~").append(targets.getProteinGrams() / 3).append("g\n");

        if (prefs.getAllergies() != null && !prefs.getAllergies().isEmpty()) {
            sb.append("- MUST AVOID (allergies): ").append(prefs.getAllergies()).append("\n");
        }
        if (prefs.getDislikedFoods() != null && !prefs.getDislikedFoods().isEmpty()) {
            sb.append("- Avoid these foods: ").append(prefs.getDislikedFoods()).append("\n");
        }
        if (prefs.getDietType() != null) {
            sb.append("- Diet type: ").append(prefs.getDietType()).append("\n");
        }

        if (request.isUsePantry()) {
            List<String> pantryItems = pantryService.getPantryIngredientNames();
            if (!pantryItems.isEmpty()) {
                sb.append("- Prefer using these available ingredients: ").append(pantryItems).append("\n");
            }
        }

        if (request.getAdditionalNotes() != null) {
            sb.append("- Additional notes: ").append(request.getAdditionalNotes()).append("\n");
        }

        return sb.toString();
    }

    private Recipe parseRecipeFromAI(String aiResponse, User user) {
        try {
            // Clean response - remove markdown code blocks if present
            String json = aiResponse.trim();
            if (json.startsWith("```")) {
                json = json.replaceAll("```json\\s*", "").replaceAll("```\\s*", "");
            }

            JsonNode node = objectMapper.readTree(json);
            String title = node.path("title").asText("AI Generated Recipe");

            // Return existing recipe if title already exists
            java.util.Optional<Recipe> existing = recipeRepository.findByTitle(title);
            if (existing.isPresent()) {
                return existing.get();
            }

            Recipe recipe = Recipe.builder()
                    .title(title)
                    .description(node.path("description").asText(""))
                    .instructions(node.path("instructions").asText(""))
                    .calories(node.path("calories").asInt(0))
                    .protein(node.path("protein").asInt(0))
                    .carbs(node.path("carbs").asInt(0))
                    .fat(node.path("fat").asInt(0))
                    .cookTimeMinutes(node.path("cookTimeMinutes").asInt(0))
                    .cuisine(node.path("cuisine").asText(null))
                    .mealType(node.path("mealType").asText(null))
                    .dietType(node.path("dietType").asText(null))
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
        } catch (Exception e) {
            log.error("Failed to parse AI recipe response: {}", e.getMessage());
            // Return a basic recipe with the raw response as instructions
            return Recipe.builder()
                    .title("AI Generated Recipe")
                    .description("Generated recipe")
                    .instructions(aiResponse)
                    .generated(true)
                    .generatedForUser(user)
                    .build();
        }
    }
}

