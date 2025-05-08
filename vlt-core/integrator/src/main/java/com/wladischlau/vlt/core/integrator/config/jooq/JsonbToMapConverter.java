package com.wladischlau.vlt.core.integrator.config.jooq;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import org.jooq.JSONB;
import org.jooq.Converter;

import java.util.Map;

public final class JsonbToMapConverter implements Converter<JSONB, Map<String, Object>> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> TYPE = new TypeReference<>() {};

    @Override
    public Map<String, Object> from(JSONB databaseObject) {
        if (databaseObject == null) return null;

        try {
            return MAPPER.readValue(databaseObject.data(), TYPE);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot read JSONB", e);
        }
    }

    @Override
    public JSONB to(Map<String, Object> userObject) {
        if (userObject == null) return null;

        try {
            return JSONB.jsonb(MAPPER.writeValueAsString(userObject));
        } catch (Exception e) {
            throw new IllegalStateException("Cannot write JSONB", e);
        }
    }

    @Override
    public @NotNull Class<JSONB> fromType() {
        return JSONB.class;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public @NotNull Class<Map<String, Object>> toType() {
        return (Class<Map<String, Object>>) (Class) Map.class;
    }
}