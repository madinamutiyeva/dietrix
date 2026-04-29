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
        long existing = faqItemRepository.count();
        if (existing > 0) {
            log.debug("FAQ table already has {} item(s) — skipping seed", existing);
            return;
        }

        log.info("FAQ table is empty — seeding default FAQ items…");

        List<FaqItem> items = List.of(
                faq("How many calories should I eat per day?",
                        "Your daily calorie needs depend on age, gender, weight, height, and activity level. " +
                                "Dietrix calculates this automatically based on your profile using the Mifflin-St Jeor equation.",
                        "nutrition"),
                faq("What is BMI and what does it mean?",
                        "BMI (Body Mass Index) is a measure of body fat based on height and weight. " +
                                "Normal BMI is 18.5–24.9. However, BMI does not account for muscle mass, " +
                                "so it is just one of many health indicators.",
                        "health"),
                faq("How much protein do I need?",
                        "Protein needs vary by goal: 1.6 g/kg for maintenance, 2.0 g/kg for weight loss " +
                                "(to preserve muscle), and 2.2 g/kg for muscle building. " +
                                "Dietrix calculates this based on your profile.",
                        "nutrition"),
                faq("What are macronutrients?",
                        "Macronutrients are protein, carbohydrates, and fat. Protein and carbs provide 4 calories " +
                                "per gram, while fat provides 9 calories per gram. A balanced intake is key for health.",
                        "nutrition"),
                faq("How does meal plan generation work?",
                        "Dietrix uses AI to generate a personalized 7-day meal plan based on your calorie targets, " +
                                "dietary preferences, allergies, and available pantry ingredients.",
                        "features"),
                faq("Can I customize my meal plan?",
                        "Yes! You can set your dietary preferences, allergies, liked and disliked foods during " +
                                "onboarding. The AI uses all of this to generate personalized recipes.",
                        "features"),
                faq("What should I eat before a workout?",
                        "A combination of carbs and protein 1–2 hours before exercise is ideal. Examples: " +
                                "banana with peanut butter, oatmeal with berries, or a small sandwich with lean protein.",
                        "nutrition"),
                faq("How much water should I drink daily?",
                        "A general guideline is 2–3 liters (8–12 cups) per day. Needs increase with exercise, " +
                                "hot weather, and higher body weight. Listen to your body and drink when thirsty.",
                        "health"),
                faq("Is intermittent fasting safe?",
                        "For most healthy adults, intermittent fasting (like 16:8) is safe. However, it is not " +
                                "recommended for pregnant women, people with eating disorders, or certain medical " +
                                "conditions. Consult a doctor.",
                        "nutrition"),
                faq("What is the Mediterranean diet?",
                        "The Mediterranean diet emphasizes fruits, vegetables, whole grains, fish, olive oil, and nuts. " +
                                "It is one of the most researched and recommended dietary patterns for overall health.",
                        "nutrition")
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

