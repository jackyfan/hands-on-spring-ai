package com.jackyfan.handsonspringai.vectorstoreloader;

public record GameTitle(String title) {
    public String getNormalizedTitle() {
        return title.toLowerCase().replaceAll(" ", "_");
    }
}
