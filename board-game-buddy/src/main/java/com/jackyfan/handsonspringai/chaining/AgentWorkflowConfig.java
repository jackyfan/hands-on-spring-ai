package com.jackyfan.handsonspringai.chaining;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AgentWorkflowConfig {

    @Bean
    public Chain mechanics(RuleFetcherAction ruleFetcherAction,
                       MechanicsDeterminerAction mechanicsDeterminerAction) {
        return new Chain(List.of(ruleFetcherAction, mechanicsDeterminerAction));
    }

    @Bean
    public Chain playerCount(
            RuleFetcherAction ruleFetcher,
            PlayerCountAction playerCountTask) {
        return new Chain(List.of(ruleFetcher, playerCountTask));
    }
}
