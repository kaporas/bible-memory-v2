package com.bible.scoring.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ocr")
@Getter
@Setter
public class OcrProperties {

    private Google google = new Google();

    @Getter
    @Setter
    public static class Google {
        private String apiKey;
    }
}
