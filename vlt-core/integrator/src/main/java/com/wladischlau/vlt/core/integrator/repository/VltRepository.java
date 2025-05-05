package com.wladischlau.vlt.core.integrator.repository;

import com.wladischlau.vlt.core.integrator.model.Adapter;
import com.wladischlau.vlt.core.jooq.vlt_repo.Keys;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.daos.VltAdapterDao;
import com.wladischlau.vlt.core.jooq.vlt_repo.tables.pojos.VltAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.wladischlau.vlt.core.jooq.vlt_repo.Tables.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class VltRepository {

    private final DSLContext ctx;

    private final VltAdapterDao vltAdapterDao;

    public List<VltAdapter> findAllAdapters() {
        return vltAdapterDao.findAll();
    }

    public Optional<VltAdapter> findAdapterByName(String adapterName) {
        return ctx.selectFrom(VLT_ADAPTER)
                .where(VLT_ADAPTER.NAME.equalIgnoreCase(adapterName))
                .fetchOptionalInto(VltAdapter.class);
    }

    public void upsertAdapter(VltAdapter adapter) {
        ctx.insertInto(VLT_ADAPTER)
                .set(ctx.newRecord(VLT_ADAPTER, adapter))
                .onConflictOnConstraint(Keys.VLT_ADAPTER_NAME_KEY)
                .doUpdate()
                .set(VLT_ADAPTER.DISPLAY_NAME, adapter.displayName())
                .set(VLT_ADAPTER.DESCRIPTION, adapter.description())
                .set(VLT_ADAPTER.CLAZZ, adapter.clazz())
                .set(VLT_ADAPTER.TYPE, adapter.type())
                .set(VLT_ADAPTER.DIRECTION, adapter.direction())
                .set(VLT_ADAPTER.CHANNEL_KIND, adapter.channelKind())
                .execute();
    }
}
