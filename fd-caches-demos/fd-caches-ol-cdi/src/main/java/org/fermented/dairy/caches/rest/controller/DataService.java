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

    private static final Map<UUID, ConfigOverriddenCacheRecord> CONFIG_RECORDS = new HashMap<>();

    private static final Map<UUID, DisabledCacheRecord> DISABLED_RECORDS = new HashMap<>();

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

    @CacheDelete
    @CachedType(ConfigOverriddenCacheRecord.class)
    public void addConfigOverriddenCacheRecord(final ConfigOverriddenCacheRecord dataRecord) {
        CONFIG_RECORDS.put(dataRecord.id(), dataRecord);
    }

    @CacheLoad
    @CachedType(ConfigOverriddenCacheRecord.class)
    public Optional<ConfigOverriddenCacheRecord> getConfigOverriddenCacheRecord(final UUID id) {
        return Optional.ofNullable(CONFIG_RECORDS.get(id));
    }

    @CacheDelete
    @CachedType(DisabledCacheRecord.class)
    public void addDisabledCacheRecord(final DisabledCacheRecord dataRecord) {
        DISABLED_RECORDS.put(dataRecord.id(), dataRecord);
    }

    @CacheLoad
    @CachedType(DisabledCacheRecord.class)
    public Optional<DisabledCacheRecord> getDisabledCacheRecord(final UUID id) {
        return Optional.ofNullable(DISABLED_RECORDS.get(id));
    }
}
