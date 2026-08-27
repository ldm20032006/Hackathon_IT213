package demo.btth_session13.config;

import demo.btth_session13.tool.BookingTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    /**
     * Chat memory backed by JDBC so conversations survive restarts.
     * Falls back gracefully: if the schema does not exist yet on first run,
     * Spring AI will create it automatically (initialize-schema: always).
     */
    @Bean
    public ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .maxMessages(20)   // retain last 20 messages per conversation
                .build();
    }

    /**
     * Main ChatClient used by the REST chatbot endpoint.
     * Advisors wired in order:
     *   1. MessageChatMemoryAdvisor  – injects conversation history
     *   2. QuestionAnswerAdvisor     – retrieves relevant hotel docs from PGVector (RAG)
     * BookingTools are registered so the model can call them via Function Calling.
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder,
                                 ChatMemory chatMemory,
                                 VectorStore vectorStore,
                                 BookingTools bookingTools) {
        return builder
                .defaultSystem("""
                        Bạn là trợ lý ảo thân thiện của khách sạn LotusBay.
                        Bạn hỗ trợ khách hàng:
                        1. Tra cứu thông tin khách sạn (dịch vụ, chính sách, tiện ích, FAQ).
                        2. Đặt phòng trực tuyến khi khách cung cấp đủ thông tin.
                        Luôn trả lời bằng tiếng Việt, lịch sự và ngắn gọn.
                        Khi khách muốn đặt phòng, hãy hỏi: tên khách, ID phòng, ngày nhận và trả phòng,
                        rồi gọi tool bookRoom để hoàn tất.
                        """)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        QuestionAnswerAdvisor.builder(vectorStore).build()
                )
                .defaultTools(bookingTools)
                .build();
    }
}
