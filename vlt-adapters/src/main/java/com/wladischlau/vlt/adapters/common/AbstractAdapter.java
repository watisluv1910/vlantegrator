package com.wladischlau.vlt.adapters.common;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.ConstraintViolationException;
import lombok.Getter;
import lombok.SneakyThrows;

import static com.wladischlau.vlt.adapters.common.AdapterUtils.VALIDATOR;

@Getter
public abstract non-sealed class AbstractAdapter<T> implements Adapter {

    protected final T config;

    private final Class<T> configClass;

    @SneakyThrows
    protected AbstractAdapter(String configJson, Class<T> configClass) {
        this.config = AdapterUtils.configMapper.readValue(configJson, configClass);
        validateConfig();

        this.configClass = configClass;
    }

    @Override
    public JsonNode getConfigSchema() {
        return null;
    }

    private void validateConfig() {
        var violations = VALIDATOR.validate(getConfig());
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
