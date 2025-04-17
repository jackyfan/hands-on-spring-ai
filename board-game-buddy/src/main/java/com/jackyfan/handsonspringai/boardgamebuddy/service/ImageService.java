package com.jackyfan.handsonspringai.boardgamebuddy.service;

public interface ImageService {

    String generateImageForUrl(String instructions);

    byte[] generateImageForImageBytes(String instructions);

}
