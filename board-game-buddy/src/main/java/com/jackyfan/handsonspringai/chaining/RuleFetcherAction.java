package com.jackyfan.handsonspringai.chaining;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class RuleFetcherAction implements Action {
    private static final Logger LOGGER =
            Logger.getLogger(RuleFetcherAction.class.getName());
    private final ChatClient chatClient;
    private final String rulesFilePath;

    public RuleFetcherAction(ChatClient.Builder chatClientBuilder,
                             @Value("classpath:/promptTemplates/rulesFetcher.st")
                             Resource systemMessageTemplate,
                             @Value("${boardgame.rules.path}")
                             String rulesFilePath) {
        this.chatClient = chatClientBuilder.defaultSystem(systemMessageTemplate).build();
        this.rulesFilePath = rulesFilePath;
    }

    @Override
    public String act(String input) {
        LOGGER.info("Fetching rules for:" + input);
        var rulesFile = chatClient.prompt()
                .user(text ->
                        text.text(input))
                .call()
                .entity(RulesFile.class);
        if (rulesFile.successful()) {
            String rulesContent = loadRules(rulesFile.filename());
            if (rulesContent != null) {
                return rulesContent;
            }
        }
        throw new ActionFailedException("Unable to fetch rules for the specified\n" +
                "game.");
    }

    private String loadRules(String filename) {
        return new TikaDocumentReader("classpath:/"+rulesFilePath + "/" + filename)
                .get()
                .get(0)
                .getText();
    }

    private record RulesFile(boolean successful, String filename) {
    }

}
