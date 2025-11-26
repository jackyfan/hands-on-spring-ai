package com.jackyfan.handsonspringai.boardgamebuddy;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TopSongsController {
    private final ChatClient chatClient;

    public TopSongsController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Value("classpath:/promptTemplates/topSongsTemplate.st")
    Resource topSongsPromptTemplate;


    @GetMapping(value = "/topSongs", produces = "application/json")
    public List<String> topSongs(@RequestParam("year") String year) {
        return chatClient.prompt().
                user(promptUserSpec ->
                        promptUserSpec.text(topSongsPromptTemplate).
                                param("year", year))
                .call().entity(new ParameterizedTypeReference<List<String>>() {});
    }
}
