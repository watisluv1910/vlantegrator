package com.wladischlau.vlt.core.integrator.repository;

import com.wladischlau.vlt.adapters.common.AdapterType;
import com.wladischlau.vlt.core.integrator.mapper.ModelMapper;
import com.wladischlau.vlt.core.jooq.vlt_repo.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.wladischlau.vlt.core.jooq.vlt_repo.Tables.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class VltRepository {

    private final DSLContext ctx;
    private final ModelMapper modelMapper;

    public void upsertAdapter(AdapterType adapter) {
        var pojo = modelMapper.toJooq(adapter);

        ctx.insertInto(VLT_ADAPTER)
                .set(ctx.newRecord(VLT_ADAPTER, pojo))
                .onConflictOnConstraint(Keys.VLT_ADAPTER_NAME_KEY)
                .doUpdate()
                .set(VLT_ADAPTER.DISPLAY_NAME, pojo.displayName())
                .set(VLT_ADAPTER.DESCRIPTION, pojo.description())
                .set(VLT_ADAPTER.CLAZZ, pojo.clazz())
                .set(VLT_ADAPTER.TYPE, pojo.type())
                .set(VLT_ADAPTER.DIRECTION, pojo.direction())
                .set(VLT_ADAPTER.CHANNEL_KIND, pojo.channelKind())
                .execute();
    }
}
