package com.jackyfan.handsonspringai.boardgamebuddy.controller;

import com.jackyfan.handsonspringai.boardgamebuddy.domain.Answer;
import com.jackyfan.handsonspringai.boardgamebuddy.domain.Question;
import com.jackyfan.handsonspringai.boardgamebuddy.service.BoardGameService;
import com.jackyfan.handsonspringai.boardgamebuddy.service.VoiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/ai")
public class ChatController {
    @Qualifier("springAiBoardGameService")
    private final BoardGameService boardGameService;
    private final VoiceService voiceService;

    public ChatController(BoardGameService boardGameService, VoiceService voiceService) {
        this.boardGameService = boardGameService;
        this.voiceService = voiceService;
    }

    @PostMapping("/ask")
    public Answer chat(@RequestHeader(name = "X_AI_CONVERSATION_ID",
            defaultValue = "default") String conversationId, @RequestBody @Valid Question question) {
        return boardGameService.askQuestion(question, conversationId);
    }


    @PostMapping(path = "/audioAsk", produces = "application/json")
    public Answer audioAsk(
            @RequestHeader(name = "X_AI_CONVERSATION_ID",
                    defaultValue = "default") String conversationId,
            @RequestParam("audio") MultipartFile audioBlob,
            @RequestParam("gameTitle") String gameTitle) {
        String transcription = voiceService.transcribe(audioBlob.getResource());
        Question transcribedQuestion = new Question(gameTitle, transcription);
        return boardGameService.askQuestion(transcribedQuestion, conversationId);
    }

    @PostMapping(path = "/audioAsk", produces = "audio/mpeg")
    public Resource audioAskAudioResponse(
            @RequestHeader(name = "X_AI_CONVERSATION_ID",
                    defaultValue = "default") String conversationId,
            @RequestParam("audio") MultipartFile blob,
            @RequestParam("gameTitle") String game) {
        String transcription = voiceService.transcribe(blob.getResource());
        Question transcribedQuestion = new Question(game, transcription);
        Answer answer = boardGameService.askQuestion(transcribedQuestion, conversationId);
        return voiceService.textToSpeech(answer.answer());
    }

    @PostMapping(path = "/visionAsk",
            produces = "application/json",
            consumes = "multipart/form-data")
    public Answer visionAsk(
            @RequestHeader(name = "X_AI_CONVERSATION_ID",
                    defaultValue = "default") String conversationId,
            @RequestPart("image") MultipartFile image,
            @RequestPart("gameTitle") String game,
            @RequestPart("question") String questionIn) {

        Resource imageResource = image.getResource();
        String imageContentType = image.getContentType();

        Question question = new Question(game, questionIn);
        return boardGameService.askQuestion(question, imageResource, imageContentType, conversationId);
    }
}
