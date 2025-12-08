package com.jackyfan.handsonspringai.embabelgamesagent;

import com.embabel.agent.config.annotation.EnableAgents;
import com.embabel.agent.config.annotation.LoggingThemes;
import com.embabel.agent.config.annotation.McpServers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(
		basePackages = {
				"com.jackyfan.handsonspringai"
		}
)
@EnableAgents(
		loggingTheme = LoggingThemes.STAR_WARS,
		mcpServers = {McpServers.DOCKER_DESKTOP}
)
public class EmbabelGamesAgentApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmbabelGamesAgentApplication.class, args);
	}

}
