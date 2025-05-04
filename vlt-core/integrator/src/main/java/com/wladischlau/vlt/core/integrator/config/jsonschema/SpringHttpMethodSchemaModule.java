package com.wladischlau.vlt.core.integrator.config.jsonschema;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.victools.jsonschema.generator.CustomDefinition;
import com.github.victools.jsonschema.generator.Module;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import org.springframework.http.HttpMethod;

import java.util.stream.Stream;

public class SpringHttpMethodSchemaModule implements Module {

    @Override
    public void applyToConfigBuilder(SchemaGeneratorConfigBuilder builder) {
        builder.forTypesInGeneral()
                .withCustomDefinitionProvider((type, ctx) -> {
                    if (HttpMethod.class.isAssignableFrom(type.getErasedType())) {
                        ObjectNode def = ctx.getGeneratorConfig().createObjectNode();
                        def.put("type", "string");
                        ArrayNode enumNode = def.putArray("enum");
                        Stream.of(HttpMethod.values())
                                .map(HttpMethod::name)
                                .forEach(enumNode::add);
                        return new CustomDefinition(def);
                    }
                    return null; // Остальные типы по-умолчанию
                });
    }
}