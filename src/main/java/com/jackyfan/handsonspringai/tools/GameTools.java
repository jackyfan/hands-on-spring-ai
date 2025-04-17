package com.jackyfan.handsonspringai.tools;


import com.jackyfan.handsonspringai.domain.Game;
import com.jackyfan.handsonspringai.domain.GameComplexity;
import com.jackyfan.handsonspringai.domain.GameComplexityResponse;
import com.jackyfan.handsonspringai.service.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GameTools {

    private final GameRepository gameRepository;

    public GameTools(GameRepository gameRepository) {  //
        this.gameRepository = gameRepository;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GameTools.class);

    @Tool(name = "getGameComplexity",
            description = "Returns a game's complexity/difficulty " +
                    "given the game's title/name.")
    public GameComplexityResponse getGameComplexity(
            @ToolParam(description = "The title of the game") //
            String gameTitle) {
        String gameSlug = gameTitle    //
                .toLowerCase()
                .replace(" ", "_");

        LOGGER.info("Getting complexity for {} ({})",
                gameTitle, gameSlug);

        Optional<Game> gameOpt = gameRepository.findBySlug(gameSlug);


        Game game = gameOpt.orElseGet(() -> {  //
            LOGGER.warn("Game not found: {}", gameSlug);
            return new Game(
                    null,
                    gameSlug,
                    gameTitle,
                    GameComplexity.UNKNOWN.getValue());
        });
        return new GameComplexityResponse(  //
                game.title(), game.complexityEnum());
    }

}
