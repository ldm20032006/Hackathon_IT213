package demo.btth_session13.config;

import demo.btth_session13.tool.McpHotelTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers MCP hotel tools with Spring AI's MCP Server auto-configuration.
 * The ToolCallbackProvider bean is picked up automatically and its tools
 * are advertised to any connecting MCP client (Claude Desktop, MCP Inspector, etc.).
 */
@Configuration
public class McpConfig {

    @Bean
    public ToolCallbackProvider mcpHotelToolCallbackProvider(McpHotelTools mcpHotelTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(mcpHotelTools)
                .build();
    }
}
