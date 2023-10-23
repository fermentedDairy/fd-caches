package org.fermented.dairy.caches.rest.controller;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.fermented.dairy.caches.interceptors.annotations.CacheDelete;
import org.fermented.dairy.caches.interceptors.annotations.CacheLoad;
import org.fermented.dairy.caches.interceptors.annotations.CachedType;
import org.fermented.dairy.caches.rest.entity.records.ConfigOverriddenCacheRecord;
import org.fermented.dairy.caches.rest.entity.records.DefaultCacheRecord;
import org.fermented.dairy.caches.rest.entity.records.DisabledCacheRecord;
import org.fermented.dairy.caches.rest.entity.records.NamedCacheRecord;

@ApplicationScoped
public class DataService {

    private static final Map<UUID, DefaultCacheRecord> defaultRecords = new HashMap<>();

    @CacheDelete
    @CachedType(DefaultCacheRecord.class)
    public void addDefaultCacheRecord(final DefaultCacheRecord dataRecord) {
        defaultRecords.put(dataRecord.id(), dataRecord);
    }

    @CacheLoad
    @CachedType(DefaultCacheRecord.class)
    public Optional<DefaultCacheRecord> getDefault(final UUID id) {
        return Optional.ofNullable(defaultRecords.get(id));
    }

    public void putNamedCacheRecord(final NamedCacheRecord dataRecord) {

    }

    public Optional<NamedCacheRecord> getNamedCacheRecord(final UUID id) {
        return Optional.empty();
    }

    public void putConfigOverriddenCacheRecord(final ConfigOverriddenCacheRecord dataRecord) {
    }

    public Optional<ConfigOverriddenCacheRecord> getConfigOverriddenCacheRecord(final UUID id) {
        return Optional.empty();
    }

    public void putDisabledCacheRecord(final DisabledCacheRecord dataRecord) {

    }

    public Optional<DisabledCacheRecord> getDisabledCacheRecord(final UUID id) {
        return Optional.empty();
    }
}
