package org.fermented.dairy.caches.sb.aop.rest.controller.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.fermented.dairy.caches.annotations.CacheDelete;
import org.fermented.dairy.caches.annotations.CacheLoad;
import org.fermented.dairy.caches.annotations.CachedType;
import org.fermented.dairy.caches.sb.aop.rest.controller.aspect.Logged;
import org.fermented.dairy.caches.sb.aop.rest.entity.records.ConfigOverriddenCacheRecord;
import org.fermented.dairy.caches.sb.aop.rest.entity.records.DefaultCacheRecord;
import org.fermented.dairy.caches.sb.aop.rest.entity.records.DisabledCacheRecord;
import org.fermented.dairy.caches.sb.aop.rest.entity.records.NamedCacheRecord;
import org.springframework.stereotype.Service;

/**
 * Application Scoped class providing the intercepted caching methods.
 */
@Service
public class DataService {

    private static final Map<UUID, DefaultCacheRecord> DEFAULT_RECORDS = new HashMap<>();

    private static final Map<UUID, NamedCacheRecord> NAMED_RECORDS = new HashMap<>();

    private static final Map<UUID, ConfigOverriddenCacheRecord> CONFIG_RECORDS = new HashMap<>();

    private static final Map<UUID, DisabledCacheRecord> DISABLED_RECORDS = new HashMap<>();

    /**
     * Add a DefaultCacheRecord object and delete from cache.
     *
     * @param dataRecord The record
     */
    @CacheDelete
    @CachedType(DefaultCacheRecord.class)
    @Logged
    public void addDefaultCacheRecord(final DefaultCacheRecord dataRecord) {
        DEFAULT_RECORDS.put(dataRecord.id(), dataRecord);
    }

    /**
     * get DefaultCacheRecord and load result into cache (on miss) or fetch from cache (on Hit).
     *
     * @param id The id of the record
     * @return Optional containing record if present
     */
    @CacheLoad
    @CachedType(DefaultCacheRecord.class)
    @Logged
    public Optional<DefaultCacheRecord> getDefault(final UUID id) {
        return Optional.ofNullable(DEFAULT_RECORDS.get(id));
    }

    /**
     * Add a NamedCacheRecord object and delete from cache.
     *
     * @param dataRecord The record
     */
    @CacheDelete
    @CachedType(NamedCacheRecord.class)
    public void addNamedCacheRecord(final NamedCacheRecord dataRecord) {
        NAMED_RECORDS.put(dataRecord.id(), dataRecord);
    }

    /**
     * get NamedCacheRecord and load result into cache (on miss) or fetch from cache (on hit).
     *
     * @param id The id of the record
     * @return Optional containing record if present
     */
    @CacheLoad
    @CachedType(NamedCacheRecord.class)
    public Optional<NamedCacheRecord> getNamedCacheRecord(final UUID id) {
        return Optional.ofNullable(NAMED_RECORDS.get(id));
    }

    /**
     * Add a ConfigOverriddenCacheRecord object and delete from cache.
     *
     * @param dataRecord The record
     */
    @CacheDelete
    @CachedType(ConfigOverriddenCacheRecord.class)
    public void addConfigOverriddenCacheRecord(final ConfigOverriddenCacheRecord dataRecord) {
        CONFIG_RECORDS.put(dataRecord.id(), dataRecord);
    }

    /**
     * get ConfigOverriddenCacheRecord and load result into cache (on miss) or fetch from cache (on Hit).
     *
     * @param id The id of the record
     * @return Optional containing record if present
     */
    @CacheLoad
    @CachedType(ConfigOverriddenCacheRecord.class)
    public Optional<ConfigOverriddenCacheRecord> getConfigOverriddenCacheRecord(final UUID id) {
        return Optional.ofNullable(CONFIG_RECORDS.get(id));
    }

    /**
     * Add a DisabledCacheRecord object and delete from cache.
     *
     * @param dataRecord The record
     */
    @CacheDelete
    @CachedType(DisabledCacheRecord.class)
    public void addDisabledCacheRecord(final DisabledCacheRecord dataRecord) {
        DISABLED_RECORDS.put(dataRecord.id(), dataRecord);
    }

    /**
     * get ConfigOverriddenCacheRecord and load result into cache (on miss) or fetch from cache (on Hit).
     *
     * @param id The id of the record
     * @return Optional containing record if present
     */
    @CacheLoad
    @CachedType(DisabledCacheRecord.class)
    public Optional<DisabledCacheRecord> getDisabledCacheRecord(final UUID id) {
        return Optional.ofNullable(DISABLED_RECORDS.get(id));
    }
}

