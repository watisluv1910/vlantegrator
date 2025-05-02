package com.wladischlau.vlt.adapters.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.slf4j.event.Level;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Optional;

public class Slf4jLogLevelDeserializer extends JsonDeserializer<Level> {

    @Override
    public Level deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String level = p.getText();
        return Optional.ofNullable(level)
                .filter(StringUtils::hasText)
                .map(String::toUpperCase)
                .map(Level::valueOf)
                .orElseThrow(() -> new IOException("Некорректное значение уровня логирования: " + level));
    }
}