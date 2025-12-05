package com.jackyfan.handsonspringai.chaining;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public class Router implements Action{
    private static final Logger LOGGER = LoggerFactory.getLogger(Router.class);
    private final Map<String, Chain> chains;
    private final ChatClient chatClient;

    public Router(Map<String, Chain> chains,
                  @Value("classpath:/promptTemplates/router.st")
                  Resource systemMessageTemplate,
                  ChatClient.Builder chatClientBuilder) {
        this.chains = chains;
        this.chatClient = chatClientBuilder.defaultSystem(systemMessageTemplate).build();
    }
    @Override
    public String act(String input) {
        Handler handler = chatClient.prompt()
                .user(text ->
                        text.text("Choose a handler for the following input: {userInput}")
                                .param("userInput",input))
                .call()
                .entity(Handler.class);
        LOGGER.info("Routing to {} for input: {}", handler.handlerName(), input);
        return chains.get(handler.handlerName()).act(input);
    }

    private record Handler(String handlerName) {}
}
