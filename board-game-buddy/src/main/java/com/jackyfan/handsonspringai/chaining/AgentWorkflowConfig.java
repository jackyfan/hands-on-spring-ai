package com.jackyfan.handsonspringai.chaining;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AgentWorkflowConfig {

    @Bean
    public Chain chain(RuleFetcherAction ruleFetcherAction,
                       MechanicsDeterminerAction mechanicsDeterminerAction) {
        return new Chain(List.of(ruleFetcherAction, mechanicsDeterminerAction));
    }
}
