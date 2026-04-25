package kz.dietrix.assistant.service;

import kz.dietrix.assistant.dto.ChatResponse;
import kz.dietrix.assistant.entity.ChatHistory;
import kz.dietrix.assistant.repository.ChatHistoryRepository;
import kz.dietrix.auth.entity.User;
import kz.dietrix.common.service.OpenAiService;
import kz.dietrix.userprofile.dto.UserPreferenceDto;
import kz.dietrix.userprofile.dto.UserProfileDto;
import kz.dietrix.userprofile.dto.UserTargetsDto;
import kz.dietrix.userprofile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssistantService {

    private final OpenAiService openAiService;
    private final ChatHistoryRepository chatHistoryRepository;
    private final UserProfileService userProfileService;

    /**
     * NOT @Transactional on purpose: the OpenAI HTTP call below would otherwise
     * hold a JDBC connection until completion, exhausting the Hikari pool.
     * Each chatHistoryRepository.save() runs in its own short Spring Data transaction.
     */
    public ChatResponse chat(String userMessage) {
        User user = userProfileService.getCurrentUser();

        // Save user message
        ChatHistory userChat = ChatHistory.builder()
                .user(user)
                .message(userMessage)
                .role("user")
                .build();
        chatHistoryRepository.save(userChat);

        // Build context
        String systemPrompt = buildSystemPrompt(user);

        // Get recent chat history (last 10 messages)
        List<ChatHistory> history = chatHistoryRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(0, 10));

        List<Map<String, String>> messages = new ArrayList<>();
        // Reverse to get chronological order
        for (int i = history.size() - 1; i >= 0; i--) {
            ChatHistory h = history.get(i);
            messages.add(Map.of("role", h.getRole(), "content", h.getMessage()));
        }

        // Call AI
        String aiResponse = openAiService.chatWithHistory(systemPrompt, messages);

        // Save assistant response
        ChatHistory assistantChat = ChatHistory.builder()
                .user(user)
                .message(aiResponse)
                .role("assistant")
                .build();
        chatHistoryRepository.save(assistantChat);

        log.info("AI chat completed for user: {}", user.getEmail());

        return ChatResponse.builder()
                .message(aiResponse)
                .role("assistant")
                .build();
    }

    private String buildSystemPrompt(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are Dietrix AI, a friendly and knowledgeable nutrition assistant. ");
        sb.append("Answer questions about nutrition, diet, recipes, and healthy eating. ");
        sb.append("Be concise but helpful. If unsure, recommend consulting a healthcare professional.\n\n");

        try {
            UserProfileDto profile = userProfileService.getProfile();
            UserTargetsDto targets = userProfileService.getTargets();
            UserPreferenceDto prefs = userProfileService.getPreferences();

            sb.append("User context:\n");
            if (profile.getGender() != null) sb.append("- Gender: ").append(profile.getGender()).append("\n");
            if (profile.getAge() != null) sb.append("- Age: ").append(profile.getAge()).append("\n");
            if (profile.getWeightKg() != null) sb.append("- Weight: ").append(profile.getWeightKg()).append(" kg\n");
            if (profile.getGoal() != null) sb.append("- Goal: ").append(profile.getGoal()).append("\n");
            sb.append("- Daily targets: ").append(targets.getDailyCalories()).append(" kcal, ");
            sb.append(targets.getProteinGrams()).append("g protein\n");
            if (prefs.getAllergies() != null && !prefs.getAllergies().isEmpty()) {
                sb.append("- Allergies: ").append(prefs.getAllergies()).append("\n");
            }
            if (prefs.getDietType() != null) {
                sb.append("- Diet: ").append(prefs.getDietType()).append("\n");
            }
        } catch (Exception e) {
            log.debug("Could not load user context for assistant: {}", e.getMessage());
        }

        return sb.toString();
    }
}

