package com.wladischlau.vlt.core.integrator.mapper;

import com.wladischlau.vlt.adapters.common.AdapterType;
import com.wladischlau.vlt.core.integrator.model.Adapter;
import com.wladischlau.vlt.core.jooq.vlt_repo.enums.AdapterDirection;
import com.wladischlau.vlt.core.jooq.vlt_repo.enums.ChannelKind;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltAdapter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = CentralConfig.class,
        imports = {ChannelKind.class, AdapterDirection.class, Class.class})
public interface ModelMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "displayName", source = "displayName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "clazz", source = "clazz")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "direction", expression = "java(AdapterDirection.lookupLiteral(src.direction().name()))")
    @Mapping(target = "channelKind", expression = "java(ChannelKind.lookupLiteral(src.channelKind().name()))")
    VltAdapter toJooq(Adapter src);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "displayName", source = "displayName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "clazz", source = "clazz")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "direction", expression = "java(AdapterType.Direction.valueOf(src.direction().getLiteral()))")
    @Mapping(target = "channelKind", expression = "java(AdapterType.ChannelKind.valueOf(src.channelKind().getLiteral()))")
    Adapter toModel(VltAdapter src);

    List<Adapter> toAdaptersFromJooq(List<VltAdapter> src);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "displayName", source = "displayName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "clazz", expression = "java(src.toAdapterClassName())")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "direction", source = "direction")
    @Mapping(target = "channelKind", source = "channelKind")
    Adapter toModel(AdapterType src);

    List<Adapter> toAdaptersFromType(List<AdapterType> src);
}
