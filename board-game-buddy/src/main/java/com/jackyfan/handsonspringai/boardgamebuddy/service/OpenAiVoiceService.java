package com.jackyfan.handsonspringai.boardgamebuddy.service;

import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.speech.SpeechModel;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class OpenAiVoiceService implements VoiceService {

    private final OpenAiAudioTranscriptionModel transcriptionModel;
    private final SpeechModel speechModel;

    public OpenAiVoiceService(
            OpenAiAudioTranscriptionModel transcriptionModel, SpeechModel speechModel) {
        this.transcriptionModel = transcriptionModel;
        this.speechModel = speechModel;
    }

    @Override
    public String transcribe(Resource audioFileResource) {
        return transcriptionModel.call(audioFileResource); //
    }

    @Override
    public Resource textToSpeech(String text) {
        OpenAiAudioSpeechOptions options = OpenAiAudioSpeechOptions.builder()
                .voice(OpenAiAudioApi.SpeechRequest.Voice.NOVA)
                .build();
        SpeechPrompt speechPrompt = new SpeechPrompt(text, options);
        SpeechResponse response = speechModel.call(speechPrompt);
        byte[] speechBytes = response.getResult().getOutput();
        return new ByteArrayResource(speechBytes);
    }

}
