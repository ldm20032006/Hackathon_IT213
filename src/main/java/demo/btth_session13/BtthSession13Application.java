package demo.btth_session13;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * LotusBay Hotel Chatbot – Spring Boot + Spring AI entry point.
 *
 * Features:
 *  - RAG chatbot: hotel info Q&A via PGVector (Supabase) + QuestionAnswerAdvisor
 *  - Function Calling: AI-driven room booking via BookingTools
 *  - Chat memory: multi-turn conversations via JdbcChatMemory
 *  - MCP Server: getAvailableRooms / createRoom tools for external MCP clients
 */
@SpringBootApplication
public class BtthSession13Application {

    public static void main(String[] args) {
        SpringApplication.run(BtthSession13Application.class, args);
    }
}
