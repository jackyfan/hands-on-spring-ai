package com.jackyfan.handsonspringai.controller;


import com.jackyfan.handsonspringai.tools.TimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RequestMapping("/ai")
@RestController
public class GetTimeController {

    private static final String CURRENT_TIME_TEMPLATE =
            "What is the current time in {city}?";  //

    private final ChatClient chatClient;

    public GetTimeController(ChatClient.Builder chatClientBuilder, TimeTools timeTools) {
        this.chatClient = chatClientBuilder.defaultTools(timeTools).build();
    }

    @GetMapping(path = "/time", params = "city")
    public String getTime(@RequestParam("city") String city) {
        return chatClient.prompt()
                .user(userSpec -> {
                    userSpec
                            .text(CURRENT_TIME_TEMPLATE)
                            .param("city", city);   //
                })
                .call()
                .content();
    }

}
