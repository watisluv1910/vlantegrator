package com.wladischlau.vlt.adapters.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Optional;

public class SpringExpressionDeserializer extends JsonDeserializer<Expression> {

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();

    @Override
    public Expression deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String expr = p.getText();
        return Optional.ofNullable(expr)
                .filter(StringUtils::hasText)
                .map(PARSER::parseExpression)
                .orElseThrow(() -> new IllegalArgumentException("Неправильное SpEL-выражение: " + expr));
    }
}