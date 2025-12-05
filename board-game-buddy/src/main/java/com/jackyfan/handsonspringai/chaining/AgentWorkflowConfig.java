package com.jackyfan.handsonspringai.chaining;

import com.jackyfan.handsonspringai.parallel.ParallelizerAction;
import com.jackyfan.handsonspringai.parallel.SummarizerAction;
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

    @Bean
    ParallelizerAction parallelAction(
                    PlayerCountAction playerCount,
            MechanicsDeterminerAction mechanicsDeterminer) {
        return new ParallelizerAction(
                List.of(playerCount, mechanicsDeterminer));
    }

    @Bean
    public Chain summarizerChain(
            RuleFetcherAction ruleFetcher,
            ParallelizerAction parallelizerAction,
            SummarizerAction summarizer) {
        return new Chain(
                List.of(ruleFetcher, parallelizerAction, summarizer));
    }
}
