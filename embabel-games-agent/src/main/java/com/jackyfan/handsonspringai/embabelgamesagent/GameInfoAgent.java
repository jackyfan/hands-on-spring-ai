package com.jackyfan.handsonspringai.embabelgamesagent;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.PromptRunner;

import com.embabel.agent.domain.io.UserInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.stringtemplate.v4.ST;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

@Agent(name = "GameInfoAgent",description = "An agent that helps users answer questions " +
        "about board games, including mechanics and player counts.",version = "1.0.0")
public class GameInfoAgent {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(GameInfoAgent.class);
    private final String rulesFilePath;
    @Value("classpath:/promptTemplates/mechanicsDeterminer.st")
    Resource mechanicsDeterminerPromptTemplate;

    @Value("classpath:/promptTemplates/playerCount.st")
    Resource playerCountPromptTemplate;

    @Value("classpath:/promptTemplates/rulesFetcher.st")
    Resource rulesFetcherPromptTemplate;

    public GameInfoAgent(
            @Value("${boardgame.rules.path}")
            String rulesFilePath) {
        this.rulesFilePath = rulesFilePath;
    }
    @Action
    @AchievesGoal(description = "Game mechanics have been determined.")
    public GameMechanics determineGameMechanics(GameRules gameRules) {
        LOGGER.info("Determining mechanics from rules for: {}",
                gameRules.gameTitle());
        var prompt = promptResourceToString(mechanicsDeterminerPromptTemplate,
                Map.of("gameRules", gameRules.rulesText()));
        return PromptRunner.usingLlm()
                .createObject(prompt, GameMechanics.class);
    }

    @Action
    @AchievesGoal(description = "Player count has been determined.")
    public PlayerCount determinePlayerCount(GameRules gameRules) {
        LOGGER.info("Determining player count from rules for: {}",
                gameRules.gameTitle());
        var prompt = promptResourceToString(playerCountPromptTemplate,
                Map.of("gameRules", gameRules.rulesText()));
        return PromptRunner.usingLlm()
                .createObject(prompt, PlayerCount.class);
    }

    @Action
    public GameRules getGameRules(GameTitle gameTitle, RulesFile rulesFile) {
        LOGGER.info("Getting game rules for: " + gameTitle.gameTitle()
                + " from file: " + rulesFile.filename());
        if (rulesFile.successful()) {
            String rulesContent = new TikaDocumentReader("classpath:/"+rulesFilePath + "/" + rulesFile.filename())
                            .get()
                            .get(0)
                            .getText();
            if (rulesContent != null) {
                return new GameRules(gameTitle.gameTitle(), rulesContent);
            }
        }
        throw new ActionFailedException(
                "Unable to fetch rules for the specified game.");
    }

    @Action
    public RulesFile getGameRulesFilename(GameTitle gameTitle) {
        LOGGER.info("Getting game rules filename for: " + gameTitle.gameTitle());
        var prompt = promptResourceToString(rulesFetcherPromptTemplate,
                Map.of("gameTitle", gameTitle.gameTitle()));
        return PromptRunner.usingLlm()
                .createObject(prompt, RulesFile.class);
    }

    @Value("classpath:/promptTemplates/determineTitle.st")
    Resource determineTitlePromptTemplate;
    @Action
    public GameTitle extractGameTitle(UserInput userInput) {
        LOGGER.info("Extracting game title from user input");
        var prompt = promptResourceToString(determineTitlePromptTemplate,
                Map.of("userInput", userInput.getContent()));
        return PromptRunner.usingLlm()
                .createObject(prompt, GameTitle.class);
    }

    private String promptResourceToString(Resource resource, Map<String, String> params) {
        try {
            var promptString = resource.getContentAsString(Charset.defaultCharset());
            var stringTemplate = new ST(promptString, '{', '}');
            params.forEach(stringTemplate::add);
            return stringTemplate.render();
        } catch (IOException e) {
            LOGGER.error("Error reading prompt resource: {}", resource.getFilename(), e);
            return "";
        }
    }
}
