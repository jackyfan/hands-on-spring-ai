package com.jackyfan.handsonspringai.embabelgamesagent;

import com.embabel.agent.config.annotation.EnableAgentShell;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAgentShell
public class EmbabelGamesAgentApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmbabelGamesAgentApplication.class, args);
	}

}
