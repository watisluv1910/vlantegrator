package com.wladischlau.vlt.adapters.common;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public interface AdapterConfig {

    default Map<String, Class<?>> toFieldTypeMap() {
        var clazz = this.getClass();

        if (!clazz.isRecord()) {
            throw new IllegalArgumentException("Provided class is not a record: " + clazz.getName());
        }

        var components = clazz.getRecordComponents();
        return Arrays.stream(components).collect(toMap(RecordComponent::getName, RecordComponent::getType));
    }
}
