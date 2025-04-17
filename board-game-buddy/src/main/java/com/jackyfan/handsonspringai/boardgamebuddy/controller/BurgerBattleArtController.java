package com.jackyfan.handsonspringai.boardgamebuddy.controller;

import com.jackyfan.handsonspringai.boardgamebuddy.domain.Answer;
import com.jackyfan.handsonspringai.boardgamebuddy.domain.Question;
import com.jackyfan.handsonspringai.boardgamebuddy.service.BoardGameService;
import com.jackyfan.handsonspringai.boardgamebuddy.service.ImageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/ai")
@RestController
public class BurgerBattleArtController {
    @Qualifier("springAiBoardGameService")
    private final BoardGameService boardGameService;
    private final ImageService imageService;

    public BurgerBattleArtController(BoardGameService boardGameService,
                                     ImageService imageService) {  //
        this.boardGameService = boardGameService;
        this.imageService = imageService;
    }

    @GetMapping(path="/burgerBattleArt")
    public String burgerBattleArt(@RequestParam("burger") String burger) {
        String instructions = getImageInstructions(burger);
        return imageService.generateImageForUrl(instructions); //
    }

    @GetMapping(path="/burgerBattleArt", produces = "image/png")
    public byte[] burgerBattleArtImage(@RequestParam("burger") String burger) {
        String instructions = getImageInstructions(burger);
        return imageService.generateImageForImageBytes(instructions); //
    }

    private String getImageInstructions(String burger) {
        Question question = new Question(
                "Burger Battle",
                "What ingredients are on the " + burger + " burger?");
        Answer answer = boardGameService.askQuestion(
                question, "art_conversation");        //

        return "A burger called " + burger + " " +
                "with the following ingredients: " + answer.answer() + ". " +
                "Style the background to match the name of the burger."; //
    }

}
