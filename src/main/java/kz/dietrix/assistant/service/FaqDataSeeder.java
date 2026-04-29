package kz.dietrix.assistant.service;

import jakarta.annotation.PostConstruct;
import kz.dietrix.assistant.entity.FaqItem;
import kz.dietrix.assistant.repository.FaqItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Seeds the {@code faq_items} table with default questions on first startup.
 * Runs once: if the table is non-empty (already seeded), it is a no-op, so it is safe
 * to keep enabled in production. New items added later by hand are NOT touched.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FaqDataSeeder {

    private final FaqItemRepository faqItemRepository;

    @PostConstruct
    @Transactional
    public void seed() {

        log.info("seeding default FAQ items…");

        List<FaqItem> items = List.of(

                // ═══════════════════════════════════════
                //  NUTRITION
                // ═══════════════════════════════════════

                faq("How many calories should I eat per day?",
                        "Your daily calorie needs depend on age, gender, weight, height, and activity level. "
                                + "Dietrix calculates this automatically based on your profile using the Mifflin-St Jeor equation.",
                        "nutrition"),

                faq("How much protein do I need?",
                        "Protein needs vary by goal: 0.8 g/kg for sedentary adults, 1.2–1.6 g/kg for active adults, "
                                + "1.6–2.2 g/kg for muscle building, and 1.6–2.4 g/kg for weight loss to preserve muscle. "
                                + "Dietrix calculates this based on your profile.",
                        "nutrition"),

                faq("What are macronutrients?",
                        "Macronutrients are protein, carbohydrates, and fat — the three nutrients your body needs "
                                + "in large amounts. Protein and carbs provide 4 kcal per gram, fat provides 9 kcal per gram. "
                                + "A balanced ratio is key for health and performance.",
                        "nutrition"),

                faq("What is the difference between simple and complex carbs?",
                        "Simple carbs (sugar, white bread, candy) are digested quickly and spike blood sugar. "
                                + "Complex carbs (oats, brown rice, sweet potato) digest slowly, provide steady energy, "
                                + "and are rich in fiber. Prefer complex carbs for most of your intake.",
                        "nutrition"),

                faq("How much water should I drink daily?",
                        "A general guideline is 30–35 ml per kg of body weight per day (roughly 2–3 liters). "
                                + "Needs increase with exercise, hot weather, and high-protein diets. "
                                + "Pale-yellow urine is a good sign of adequate hydration.",
                        "nutrition"),

                faq("What are the best sources of healthy fats?",
                        "Healthy fats include monounsaturated fats (olive oil, avocado, nuts) and omega-3 fatty acids "
                                + "(salmon, sardines, flaxseeds, walnuts). Limit saturated fat and avoid trans fats. "
                                + "Fat is essential for hormone production and vitamin absorption.",
                        "nutrition"),

                faq("Do I need to take vitamins or supplements?",
                        "Most nutrients should come from whole foods. However, vitamin D (especially in winter), "
                                + "B12 (for vegans), and omega-3 (if you don't eat fish) are commonly recommended supplements. "
                                + "Consult a doctor before starting any supplement routine.",
                        "nutrition"),

                faq("What should I eat before and after a workout?",
                        "Before (1–2 hours): carbs + protein — banana with peanut butter, oatmeal with berries. "
                                + "After (within 30–60 min): protein + carbs — chicken with rice, protein shake with fruit. "
                                + "This helps fuel performance and support muscle recovery.",
                        "nutrition"),

                faq("Is intermittent fasting safe?",
                        "For most healthy adults, intermittent fasting (e.g. 16:8) is considered safe and can help "
                                + "with weight management. However, it is not recommended for pregnant women, people with "
                                + "eating disorders, or certain medical conditions. Consult a doctor first.",
                        "nutrition"),

                faq("How much fiber do I need per day?",
                        "Adults should aim for 25–30 g of fiber daily. Good sources include vegetables, fruits, "
                                + "legumes, whole grains, and nuts. Fiber supports digestion, blood sugar control, "
                                + "and helps you feel full longer.",
                        "nutrition"),

                // ═══════════════════════════════════════
                //  DIET
                // ═══════════════════════════════════════

                faq("What is the Mediterranean diet?",
                        "The Mediterranean diet emphasizes fruits, vegetables, whole grains, fish, olive oil, and nuts. "
                                + "It limits red meat, processed food, and added sugar. It is one of the most researched "
                                + "and recommended dietary patterns for heart health and longevity.",
                        "diet"),

                faq("What is a keto diet and is it effective?",
                        "The ketogenic diet is a high-fat, very low-carb diet (typically under 50 g carbs/day) that "
                                + "puts the body into ketosis. It can be effective for short-term weight loss, but may be "
                                + "hard to sustain. Not recommended without medical supervision for some conditions.",
                        "diet"),

                faq("Is a vegan diet healthy?",
                        "A well-planned vegan diet can meet all nutritional needs. Key nutrients to watch: B12, iron, "
                                + "zinc, omega-3, calcium, and vitamin D. Include legumes, tofu, nuts, seeds, and fortified "
                                + "foods. Supplementing B12 is strongly recommended.",
                        "diet"),

                faq("What is the DASH diet?",
                        "DASH (Dietary Approaches to Stop Hypertension) focuses on fruits, vegetables, whole grains, "
                                + "lean protein, and low-fat dairy while limiting sodium, sugar, and saturated fat. "
                                + "It is clinically proven to lower blood pressure.",
                        "diet"),

                faq("How do I start a calorie deficit for weight loss?",
                        "A safe calorie deficit is 300–500 kcal below your TDEE (Total Daily Energy Expenditure). "
                                + "This leads to ~0.5 kg weight loss per week. Track your intake, prioritize protein, "
                                + "and avoid cutting calories too aggressively — it backfires.",
                        "diet"),

                faq("What is flexible dieting (IIFYM)?",
                        "IIFYM (If It Fits Your Macros) means eating any foods as long as you hit your daily "
                                + "protein, carb, and fat targets. It allows flexibility and reduces the feeling of "
                                + "restriction, making it easier to stick to long-term.",
                        "diet"),

                faq("Are cheat meals okay?",
                        "Occasional indulgences are fine and can help with adherence. The key is not turning a cheat meal "
                                + "into a cheat week. A better approach is the 80/20 rule — eat nutritious foods 80% of "
                                + "the time, enjoy treats 20%.",
                        "diet"),

                faq("How do I maintain weight after reaching my goal?",
                        "Gradually increase calories to your maintenance level (reverse dieting). Keep weighing yourself "
                                + "weekly, stay active, and maintain the healthy eating habits you built. Sudden diet stops "
                                + "often lead to rapid weight regain.",
                        "diet"),

                faq("What is a plant-based diet?",
                        "A plant-based diet focuses on foods derived from plants — vegetables, fruits, grains, legumes, "
                                + "nuts, and seeds. It doesn't necessarily exclude all animal products but minimizes them. "
                                + "Associated with lower risk of heart disease and type 2 diabetes.",
                        "diet"),

                faq("Is gluten-free diet necessary for everyone?",
                        "No. A gluten-free diet is medically necessary only for people with celiac disease or "
                                + "non-celiac gluten sensitivity. For others, whole grains containing gluten (wheat, barley, rye) "
                                + "are nutritious and beneficial for gut health.",
                        "diet"),

                // ═══════════════════════════════════════
                //  RECIPES
                // ═══════════════════════════════════════

                faq("What are some high-protein breakfast ideas?",
                        "Great options: Greek yogurt with nuts and berries (20 g protein), scrambled eggs with spinach "
                                + "and toast (25 g), overnight oats with protein powder (30 g), or cottage cheese pancakes (22 g). "
                                + "Aim for 25–40 g protein at breakfast.",
                        "recipes"),

                faq("What are healthy snack ideas under 200 calories?",
                        "Try: apple + 1 tbsp peanut butter (180 kcal), Greek yogurt + berries (130 kcal), "
                                + "2 boiled eggs (140 kcal), carrots + hummus (120 kcal), a handful of almonds (160 kcal), "
                                + "or air-popped popcorn 3 cups (90 kcal).",
                        "recipes"),

                faq("How do I make a balanced smoothie?",
                        "Formula: 1 cup liquid (milk, water) + 1 cup fruit (banana, berries) + 1 scoop protein "
                                + "(powder, yogurt) + 1 handful greens (spinach, kale) + 1 tbsp healthy fat (nut butter, "
                                + "chia seeds). Blend and enjoy — roughly 300–400 kcal.",
                        "recipes"),

                faq("What can I cook with chicken breast?",
                        "Chicken breast is incredibly versatile: stir-fry with vegetables, baked with herbs and lemon, "
                                + "sliced in salads, shredded for wraps, grilled for meal prep bowls, or simmered in curry. "
                                + "Tip: brine it for 30 min before cooking to keep it juicy.",
                        "recipes"),

                faq("What are quick meal prep lunch ideas?",
                        "Easy meal prep lunches: chicken + rice + roasted veggies, turkey meatballs + quinoa + salad, "
                                + "salmon + sweet potato + green beans, lentil soup with bread, or burrito bowls with beans "
                                + "and corn. Cook once on Sunday, eat all week.",
                        "recipes"),

                faq("How do I make overnight oats?",
                        "Mix ½ cup oats + ½ cup milk + ¼ cup yogurt + 1 tbsp chia seeds + sweetener to taste. "
                                + "Refrigerate overnight. In the morning add toppings: berries, banana, nuts, honey. "
                                + "~350 kcal and ready in 0 minutes.",
                        "recipes"),

                faq("What are healthy dinner ideas for weight loss?",
                        "Focus on protein + vegetables + small portion of complex carbs: grilled salmon + asparagus + "
                                + "quinoa, turkey stir-fry + broccoli + brown rice, baked cod + roasted sweet potato + salad, "
                                + "or shrimp zucchini noodles. Keep dinner around 400–600 kcal.",
                        "recipes"),

                faq("What are good vegetarian protein sources for cooking?",
                        "Top vegetarian proteins: tofu (8 g/100 g), tempeh (19 g/100 g), lentils (9 g/100 g), "
                                + "chickpeas (8 g/100 g), black beans (8 g/100 g), edamame (11 g/100 g), quinoa (4 g/100 g), "
                                + "and eggs (6 g each). Combine legumes + grains for complete protein.",
                        "recipes"),

                faq("How do I make a healthy salad that's actually filling?",
                        "Base: leafy greens. Add protein (chicken, tuna, eggs, chickpeas), complex carbs (quinoa, sweet "
                                + "potato, bread), healthy fat (avocado, nuts, olive oil dressing), and crunch (seeds, croutons). "
                                + "A filling salad should be 400–500 kcal.",
                        "recipes"),

                faq("What are easy post-workout meal ideas?",
                        "After training, aim for protein + carbs within 60 minutes: protein shake + banana, "
                                + "chicken wrap with veggies, tuna on rice cakes, Greek yogurt with granola, "
                                + "or eggs on toast with avocado. Target ~30 g protein and ~40 g carbs.",
                        "recipes"),

                // ═══════════════════════════════════════
                //  GENERAL
                // ═══════════════════════════════════════

                faq("How does Dietrix meal plan generation work?",
                        "Dietrix uses AI to generate a personalized 7-day meal plan based on your calorie targets, "
                                + "macronutrient goals, dietary preferences, allergies, and available pantry ingredients. "
                                + "You can regenerate any meal you don't like.",
                        "general"),

                faq("Can I customize my meal plan in Dietrix?",
                        "Yes! During onboarding you set dietary preferences, allergies, liked and disliked foods. "
                                + "The AI uses all of this to create personalized recipes. You can also update preferences "
                                + "anytime in Settings and regenerate your plan.",
                        "general"),

                faq("What is BMI and what does it mean?",
                        "BMI (Body Mass Index) is calculated as weight (kg) / height (m)². Normal range is 18.5–24.9. "
                                + "However, BMI does not distinguish between muscle and fat, so it is just one of many "
                                + "health indicators — not a complete picture.",
                        "general"),

                faq("How do I track my nutrition effectively?",
                        "Log your meals consistently — even imperfect tracking beats no tracking. Use Dietrix's pantry "
                                + "and meal plan features to stay on top of your intake. Focus on hitting protein and calorie "
                                + "targets first; micro-details matter less.",
                        "general"),

                faq("How often should I weigh myself?",
                        "Weigh yourself 1–3 times per week, at the same time (morning, after bathroom, before eating). "
                                + "Look at the weekly average, not daily fluctuations — water, salt, and digestion can cause "
                                + "±1–2 kg swings that are completely normal.",
                        "general"),

                faq("How much sleep do I need for good health?",
                        "Adults need 7–9 hours of quality sleep per night. Poor sleep increases hunger hormones (ghrelin), "
                                + "reduces willpower, and impairs recovery. Prioritize sleep just like you prioritize nutrition "
                                + "and exercise.",
                        "general"),

                faq("Is it okay to eat late at night?",
                        "Meal timing matters less than total daily intake. However, eating large meals right before bed "
                                + "can disrupt sleep quality. If you're hungry at night, choose a light snack — Greek yogurt, "
                                + "a small handful of nuts, or cottage cheese.",
                        "general"),

                faq("How do I read nutrition labels correctly?",
                        "Check: serving size first (everything is per serving), then calories, protein, total fat, "
                                + "saturated fat, carbs, fiber, sugar, and sodium. Compare similar products. "
                                + "Ingredients are listed in descending order by weight.",
                        "general"),

                faq("What are empty calories?",
                        "Empty calories come from foods high in energy but low in nutrients — sugary drinks, candy, "
                                + "chips, alcohol. They add calories without providing vitamins, minerals, or fiber. "
                                + "Minimize them and choose nutrient-dense alternatives.",
                        "general"),

                faq("How do I stay motivated on a healthy eating plan?",
                        "Set realistic goals, track progress (not just weight — energy, mood, strength), allow "
                                + "occasional treats, meal prep to remove daily decisions, and focus on consistency over "
                                + "perfection. Small sustainable changes beat extreme short-term diets every time.",
                        "general")
        );

        faqItemRepository.saveAll(items);
        log.info("Seeded {} FAQ items", items.size());
    }

    private static FaqItem faq(String question, String answer, String category) {
        return FaqItem.builder()
                .question(question)
                .answer(answer)
                .category(category)
                .build();
    }
}

