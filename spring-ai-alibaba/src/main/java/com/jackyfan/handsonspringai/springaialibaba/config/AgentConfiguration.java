package com.jackyfan.handsonspringai.springaialibaba.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.hook.hip.HumanInTheLoopHook;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.jackyfan.handsonspringai.springaialibaba.interceptor.LogToolInterceptor;
import com.jackyfan.handsonspringai.springaialibaba.tools.FileReadTool;
import com.jackyfan.handsonspringai.springaialibaba.tools.FileWriteTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AgentConfiguration {
    private final ChatModel chatModel;

    public AgentConfiguration(ChatModel chatModel) {
        this.chatModel = chatModel;
    }


    @Bean
    public ReactAgent reactAgent() throws GraphStateException {
        return ReactAgent.builder()
                .name("agent")
                .description("This is a react agent")
                .model(chatModel)
                .saver(new MemorySaver())
                .tools(
                        new FileReadTool().toolCallback(),
                        new FileWriteTool().toolCallback()
                )
                .hooks(HumanInTheLoopHook.builder()
                        .approvalOn("file_write", "Write File should be approved")
                        .build())
                .interceptors(new LogToolInterceptor())
                .build();
    }

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder.defaultSystem("你将作为一名 Spring-AI-Alibaba 的专家，对于用户的使用需求作出解答")
                .build();
    }
}
