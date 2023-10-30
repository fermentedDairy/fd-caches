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

    private static final Map<UUID, DefaultCacheRecord> DEFAULT_RECORDS = new HashMap<>();

    private static final Map<UUID, NamedCacheRecord> NAMED_RECORDS = new HashMap<>();

    @CacheDelete
    @CachedType(DefaultCacheRecord.class)
    public void addDefaultCacheRecord(final DefaultCacheRecord dataRecord) {
        DEFAULT_RECORDS.put(dataRecord.id(), dataRecord);
    }

    @CacheLoad
    @CachedType(DefaultCacheRecord.class)
    public Optional<DefaultCacheRecord> getDefault(final UUID id) {
        return Optional.ofNullable(DEFAULT_RECORDS.get(id));
    }

    @CacheDelete
    @CachedType(NamedCacheRecord.class)
    public void addNamedCacheRecord(final NamedCacheRecord dataRecord) {
        NAMED_RECORDS.put(dataRecord.id(), dataRecord);
    }

    @CacheLoad
    @CachedType(NamedCacheRecord.class)
    public Optional<NamedCacheRecord> getNamedCacheRecord(final UUID id) {
        return Optional.ofNullable(NAMED_RECORDS.get(id));
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
