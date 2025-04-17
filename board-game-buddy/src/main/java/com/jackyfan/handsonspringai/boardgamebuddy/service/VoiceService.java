package com.jackyfan.handsonspringai.boardgamebuddy.service;

import org.springframework.core.io.Resource;

public interface VoiceService {

    String transcribe(Resource audioFileResource);

    Resource textToSpeech(String text);

}
