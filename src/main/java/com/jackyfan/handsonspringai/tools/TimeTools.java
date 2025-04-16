package com.jackyfan.handsonspringai.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class TimeTools {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(TimeTools.class);

    @Tool(name = "getCurrentTime",
            description = "Get the current time in the specified time zone.")  //
    public String getCurrentTime(String timeZone) {
        LOGGER.info("Getting the current time in {}", timeZone);
        LocalDateTime now = LocalDateTime.now(ZoneId.of(timeZone));
        return now.toString();
    }

}
