package demo.btth_session13.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoint for the hotel chatbot.
 * POST /api/chat  { "message": "...", "conversationId": "..." }
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatClient chatClient;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        if (request.message() == null || request.message().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new ChatResponse("Tin nhắn không được để trống.", request.conversationId()));
        }

        String conversationId = (request.conversationId() != null && !request.conversationId().isBlank())
                ? request.conversationId()
                : java.util.UUID.randomUUID().toString();

        String reply = chatClient.prompt()
                .user(request.message())
                .advisors(a -> a.param(MessageChatMemoryAdvisor.CONVERSATION_ID_KEY, conversationId))
                .call()
                .content();

        return ResponseEntity.ok(new ChatResponse(reply, conversationId));
    }

    public record ChatRequest(String message, String conversationId) {}

    public record ChatResponse(String reply, String conversationId) {}
}
