package com.wladischlau.vlt.adapters.common;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.boot.jackson.JsonComponentModule;

public class AdapterUtils {

    public static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    public static final ObjectMapper configMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JsonComponentModule());
}
