package com.jackyfan.handsonspringai.boardgamebuddy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.assertj.core.api.Assertions;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import java.nio.charset.Charset;

@EnableWireMock(@ConfigureWireMock(baseUrlProperties = "openai.base.url"))
@SpringBootTest(properties = "spring.ai.openai.base-url=${openai.base.url}")
public class SpringAiBoardGameServiceWireMockTests {
    @Value("classpath:/test-openai-response.json")
    Resource responseResource;

    @Autowired
    ChatClient chatClient;

    @Autowired
    GameRulesService gameRulesService;

    @Autowired
    VectorStore vectorStore;

    @BeforeEach
    public void setup() throws Exception {
        var cannedResponse = responseResource.getContentAsString(Charset.defaultCharset());
        var mapper = new ObjectMapper();
        var responseNode = mapper.readTree(cannedResponse);
        WireMock.stubFor(WireMock.post("/v1/chat/completions")
                .willReturn(ResponseDefinitionBuilder.okForJson(responseNode)));
    }
    @Test
    public void testAskQuestionQuestion() {
        var boardGameService =
                new SpringAiBoardGameService(chatClient,gameRulesService,vectorStore);
        var answer =
                boardGameService.askQuestion(
                        new Question("测试","What is the capital of France?"));
        Assertions.assertThat(answer).isNotNull();
        Assertions.assertThat(answer.answer()).isEqualTo("Paris");
    }

}
