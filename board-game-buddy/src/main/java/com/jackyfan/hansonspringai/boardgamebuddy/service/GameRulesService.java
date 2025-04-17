package com.jackyfan.handsonspringai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;

@Service
public class GameRulesService {
    public String getRulesFor(String gameName) {
        try {
            String filename = String.format(
                    "classpath:/gameRules/%s.txt",
                    gameName.toLowerCase().replace(" ", "_")); //

            return new DefaultResourceLoader()
                    .getResource(filename)
                    .getContentAsString(Charset.defaultCharset()); //
        } catch (IOException e) {
            System.out.println("No rules found for game: " + gameName);
            return "No rules found for game: " + gameName;                                            //
        }
    }
}
