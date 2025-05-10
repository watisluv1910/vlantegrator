package com.wladischlau.vlt.core.integrator.utils;

import com.wladischlau.vlt.core.integrator.model.RouteCacheData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class VersionBucket extends LinkedHashMap<String, RouteCacheData> {

    private final int maxSize;

    public VersionBucket(int maxSize) {
        super(maxSize + 1, .75f, false);
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<String, RouteCacheData> eldest) {
        return size() > maxSize;
    }

    /**
     * Удаляет все пары ключ-значение, которые были вставлены позже пары с ключом {@code boundaryHash}.
     *
     * @param boundaryHash граничное значение.
     *
     * @return количество удалённых пар.
     */
    public int dropNewerThan(String boundaryHash) {
        if (!containsKey(boundaryHash)) {
            return 0; // Версии с таким хэшем не существует
        }

        boolean afterBoundary = false;
        List<String> toRemove = new ArrayList<>();

        for (var key : keySet()) {
            if (afterBoundary) {
                toRemove.add(key);
            } else if (key.equals(boundaryHash)) {
                afterBoundary = true;
            }
        }

        toRemove.forEach(this::remove);
        return toRemove.size();
    }
}