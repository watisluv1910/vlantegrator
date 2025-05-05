package com.wladischlau.vlt.core.integrator.mapper;

import com.wladischlau.vlt.core.integrator.model.Adapter;
import com.wladischlau.vlt.core.integrator.rest.dto.AdapterDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CentralConfig.class)
public interface DtoMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "displayName", source = "displayName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "className", source = "clazz")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "direction", expression = "java(src.direction().name())")
    @Mapping(target = "channelKind", expression = "java(src.channelKind().name())")
    AdapterDto toDto(Adapter src);
}
