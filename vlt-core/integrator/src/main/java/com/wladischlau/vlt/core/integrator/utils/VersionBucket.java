package com.wladischlau.vlt.core.integrator.utils;

import com.wladischlau.vlt.core.integrator.model.RouteCacheData;

import java.util.LinkedHashMap;
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
}