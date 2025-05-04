package com.wladischlau.vlt.core.integrator.mapper;

import com.wladischlau.vlt.adapters.common.AdapterType;
import com.wladischlau.vlt.core.jooq.vlt_repo.enums.AdapterDirection;
import com.wladischlau.vlt.core.jooq.vlt_repo.enums.ChannelKind;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltAdapter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CentralConfig.class, imports = {ChannelKind.class, AdapterDirection.class})
public interface ModelMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "displayName", source = "displayName")
    @Mapping(target = "class_", expression = "java(src.toAdapterClassName())")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "direction", expression = "java(AdapterDirection.lookupLiteral(src.getDirection().name()))")
    @Mapping(target = "channelKind", expression = "java(ChannelKind.lookupLiteral(src.getChannelKind().name()))")
    VltAdapter toJooq(AdapterType src);
}
