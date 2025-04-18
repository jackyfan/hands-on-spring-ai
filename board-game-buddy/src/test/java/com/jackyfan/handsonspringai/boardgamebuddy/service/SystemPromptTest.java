package com.jackyfan.handsonspringai.boardgamebuddy.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SystemPromptTest {
    private static final Logger log = LoggerFactory.getLogger(SystemPromptTest.class);
    @Autowired
    private ChatClient.Builder chatClientBuilder;
    private ChatClient chatClient;

    @BeforeEach
    public void setup() {
        this.chatClient = chatClientBuilder.build();
    }

    @Test
    public void testSystemPrompt() {
        MovieReviews movieReviews = chatClient
                .prompt()
                .system("""
                        Classify movie reviews as positive, neutral or negative. Return
                        valid JSON.
                        """)
                .user("""
                        Review: "Her" is a disturbing study revealing the direction
                        humanity is headed if AI is allowed to keep evolving,
                        unchecked. It's so disturbing I couldn't watch it.

                        JSON Response:
                        """)
                .call()
                .entity(MovieReviews.class);

        log.info("size:{}", movieReviews.movie_reviews().length);
        log.info("name:{},sentiment:{}", movieReviews.movie_reviews()[0].name(), movieReviews.movie_reviews()[0].sentiment());
    }

    record MovieReviews(Movie[] movie_reviews) {
        enum Sentiment {
            POSITIVE, NEUTRAL, NEGATIVE
        }

        record Movie(Sentiment sentiment, String name) {
        }
    }

    //Implementation of Section 2.3.2: Role prompting
    @Test
    public void pt_role_prompting_1() {
        String travelSuggestions = chatClient
                .prompt()
                .system("""
                        I want you to act as a travel guide. I will write to you
                        about my location and you will suggest 3 places to visit near
                        me. In some cases, I will also give you the type of places I
                        will visit.
                        """)
                .user("""
                        My suggestion: "I am in Amsterdam and I want to visit only museums."
                        Travel Suggestions:
                        """)
                .call()
                .content();
        log.info(travelSuggestions);
    }

    // Implementation of Section 2.3.2: Role prompting with style instructions
    @Test
    public void pt_role_prompting_2() {
        String humorousTravelSuggestions = chatClient
                .prompt()
                .system("""
                        I want you to act as a travel guide. I will write to you about
                        my location and you will suggest 3 places to visit near me in
                        a humorous style.
                        """)
                .user("""
                        My suggestion: "I am in Amsterdam and I want to visit only museums."
                        Travel Suggestions:
                        """)
                .call()
                .content();
        log.info(humorousTravelSuggestions);
    }

    // Implementation of Section 2.3.3: Contextual prompting
    @Test
    public void pt_contextual_prompting() {
        String articleSuggestions = chatClient
                .prompt()
                .user(u -> u.text("""
                                Suggest 3 topics to write an article about with a few lines of
                                description of what this article should contain.

                                Context: {context}
                                """)
                        .param("context", "You are writing for a blog about retro 80's arcade video games."))
                .call()
                .content();
        log.info(articleSuggestions);
    }

    // Implementation of Section 2.4: Step-back prompting
    public void pt_step_back_prompting() {
        // Set common options for the chat client

        // First get high-level concepts
        String stepBack = chatClient
                .prompt("""
                        Based on popular first-person shooter action games, what are
                        5 fictional key settings that contribute to a challenging and
                        engaging level storyline in a first-person shooter video game?
                        """)
                .call()
                .content();

        // Then use those concepts in the main task
        String story = chatClient
                .prompt()
                .user(u -> u.text("""
                                Write a one paragraph storyline for a new level of a first-
                                person shooter video game that is challenging and engaging.

                                Context: {step-back}
                                """)
                        .param("step-back", stepBack))
                .call()
                .content();
    }


    // Implementation of Section 2.7: Tree of Thoughts (ToT) - Game solving example
    @Test
    public void pt_tree_of_thoughts_game() {
        // Step 1: Generate multiple initial moves
        String initialMoves = chatClient
                .prompt("""
                        You are playing a game of chess. The board is in the starting position.
                        Generate 3 different possible opening moves. For each move:
                        1. Describe the move in algebraic notation
                        2. Explain the strategic thinking behind this move
                        3. Rate the move's strength from 1-10
                        """)
                .options(ChatOptions.builder()
                        .temperature(0.7)
                        .build())
                .call()
                .content();

        // Step 2: Evaluate and select the most promising move
        String bestMove = chatClient
                .prompt()
                .user(u -> u.text("""
                        Analyze these opening moves and select the strongest one:
                        {moves}

                        Explain your reasoning step by step, considering:
                        1. Position control
                        2. Development potential
                        3. Long-term strategic advantage

                        Then select the single best move.
                        """).param("moves", initialMoves))
                .call()
                .content();

        // Step 3: Explore future game states from the best move
        String gameProjection = chatClient
                .prompt()
                .user(u -> u.text("""
                        Based on this selected opening move:
                        {best_move}

                        Project the next 3 moves for both players. For each potential branch:
                        1. Describe the move and counter-move
                        2. Evaluate the resulting position
                        3. Identify the most promising continuation

                        Finally, determine the most advantageous sequence of moves.
                        """).param("best_move", bestMove))
                .call()
                .content();
    }

    // Implementation of Section 2.8: Automatic Prompt Engineering
    @Test
    public void pt_automatic_prompt_engineering() {
        // Generate variants of the same request
        String orderVariants = chatClient
                .prompt("""
                        We have a band merchandise t-shirt webshop, and to train a
                        chatbot we need various ways to order: "One Metallica t-shirt
                        size S". Generate 10 variants, with the same semantics but keep
                        the same meaning.
                        """)
                .options(ChatOptions.builder()
                        .temperature(1.0)  // High temperature for creativity
                        .build())
                .call()
                .content();
        log.info(orderVariants);
        // Evaluate and select the best variant
        String output = chatClient
                .prompt()
                .user(u -> u.text("""
                        Please perform BLEU (Bilingual Evaluation Understudy) evaluation on the following variants:
                        ----
                        {variants}
                        ----

                        Select the instruction candidate with the highest evaluation score.
                        """).param("variants", orderVariants))
                .call()
                .content();
        log.info(output);
    }
}
