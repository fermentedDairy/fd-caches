package org.fermented.dairy.caches.ol.cdi.it;

import org.fermented.dairy.caches.ol.cdi.rest.entity.records.ConfigOverriddenCacheRecord;
import org.fermented.dairy.caches.ol.cdi.rest.entity.records.DefaultCacheRecord;
import org.fermented.dairy.caches.ol.cdi.rest.entity.records.DisabledCacheRecord;
import org.fermented.dairy.caches.ol.cdi.rest.entity.records.NamedCacheRecord;
import org.fermented.dairy.caches.ol.cdi.rest.entity.rto.data.Link;
import org.fermented.dairy.caches.ol.cdi.rest.entity.rto.data.PutRecordResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.microshed.testing.jupiter.MicroShedTest;
import org.microshed.testing.testcontainers.ApplicationContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.fermented.dairy.caches.it.RestUtils.validateDelete;
import static org.fermented.dairy.caches.it.RestUtils.validateGet;
import static org.fermented.dairy.caches.it.RestUtils.validateGetList;
import static org.fermented.dairy.caches.it.RestUtils.validatePut;


@MicroShedTest
public class CachesIT {

    @Container
    public static ApplicationContainer app = new ApplicationContainer()
            .withAppContextRoot("/fd-caches-ol-cdi")
            .withReadinessPath("/health/ready");

    private String applicationUrl;

    @BeforeEach
    void beforeEach() {
        applicationUrl = app.getApplicationURL();

        validateDelete(applicationUrl, "{}", Map.of(
                "internal.default.cache", "Purged",
                "configuredCache", "Purged",
                "namedCache",  "Purged"),
                Map.class,
                "api", "caches", "providers");
    }

    @Test
    @DisplayName("GET all provider names")
    void getAllProviderNames() {
        validateGetList(applicationUrl,
                List.of("internal.default.cache", "configuredCache", "namedCache"),
                String.class,
                "api", "caches", "providers");
    }


    @Test
    @DisplayName("PUT record and fetch from default cache, validate fetched object and verify presence in cache")
    void putRecordAndFetchFromDefaultCache() {
        final UUID id = UUID.randomUUID();
        validatePut(applicationUrl,
                DefaultCacheRecord.builder().id(id).value("PutTest").build(),
                PutRecordResponse.builder().id(id).links(
                        Set.of(
                                Link.builder()
                                        .rel("default")
                                        .href("fd-caches-ol-cdi/api/data/default/%s".formatted(id.toString()))
                                        .type("GET")
                                        .build()
                        )
                ).build(),
                PutRecordResponse.class,
                "api", "data", "default"
        );

        validateGet(applicationUrl,
                DefaultCacheRecord.builder().id(id).value("PutTest").build(),
                DefaultCacheRecord.class,
                "api", "data", "default", id.toString()
        );

        validateGetList(applicationUrl,
                List.of(DefaultCacheRecord.class.getCanonicalName()),
                String.class,
                "api", "caches", "providers", "internal.default.cache"
        );

        validateGetList(applicationUrl,
                List.of(id),
                UUID.class,
                "api", "caches", "providers", "internal.default.cache", "caches", DefaultCacheRecord.class.getCanonicalName(), "keys"
        );

        validateGet(applicationUrl,
                DefaultCacheRecord.builder().id(id).value("PutTest").build(),
                DefaultCacheRecord.class,
                "api", "caches", "providers", "internal.default.cache", "caches", DefaultCacheRecord.class.getCanonicalName(), "keys", id.toString()
        );

    }

    @Test
    @DisplayName("PUT record and fetch from default cache, validate fetched object and verify presence in cache then delete and verify")
    void putRecordFetchDeleteFromDefaultCache() {
        final UUID id = UUID.randomUUID();
        validatePut(applicationUrl,
                DefaultCacheRecord.builder().id(id).value("PutTest").build(),
                PutRecordResponse.builder().id(id).links(
                        Set.of(
                                Link.builder()
                                        .rel("default")
                                        .href("fd-caches-ol-cdi/api/data/default/%s".formatted(id.toString()))
                                        .type("GET")
                                        .build()
                        )
                ).build(),
                PutRecordResponse.class,
                "api", "data", "default"
        );

        validateGet(applicationUrl,
                DefaultCacheRecord.builder().id(id).value("PutTest").build(),
                DefaultCacheRecord.class,
                "api", "data", "default", id.toString()
        );

        validateGetList(applicationUrl,
                List.of(DefaultCacheRecord.class.getCanonicalName()),
                String.class,
                "api", "caches", "providers", "internal.default.cache"
        );

        validateGetList(applicationUrl,
                List.of(id),
                UUID.class,
                "api", "caches", "providers", "internal.default.cache", "caches", DefaultCacheRecord.class.getCanonicalName(), "keys"
        );

        validateGet(applicationUrl,
                DefaultCacheRecord.builder().id(id).value("PutTest").build(),
                DefaultCacheRecord.class,
                "api", "caches", "providers", "internal.default.cache", "caches", DefaultCacheRecord.class.getCanonicalName(), "keys", id.toString()
        );

        validatePut(applicationUrl,
                DefaultCacheRecord.builder().id(id).value("PutTest2").build(),
                PutRecordResponse.builder().id(id).links(
                        Set.of(
                                Link.builder()
                                        .rel("default")
                                        .href("fd-caches-ol-cdi/api/data/default/%s".formatted(id.toString()))
                                        .type("GET")
                                        .build()
                        )
                ).build(),
                PutRecordResponse.class,
                "api", "data", "default"
        );

        validateGetList(applicationUrl,
                List.of(),
                String.class,
                "api", "caches", "providers", "internal.default.cache", "caches", DefaultCacheRecord.class.getCanonicalName(), "keys"
        );

    }

    @Test
    @DisplayName("PUT record and fetch from named cache, validate fetched object and verify presence in cache")
    void putRecordAndFetchFromNamedCache() {
        final UUID id = UUID.randomUUID();
        validatePut(applicationUrl,
                NamedCacheRecord.builder().id(id).value("PutTest").build(),
                PutRecordResponse.builder().id(id).links(
                        Set.of(
                                Link.builder()
                                        .rel("named")
                                        .href("fd-caches-ol-cdi/api/data/named/%s".formatted(id.toString()))
                                        .type("GET")
                                        .build()
                        )
                ).build(),
                PutRecordResponse.class,
                "api", "data", "named"
        );

        validateGet(applicationUrl,
                NamedCacheRecord.builder().id(id).value("PutTest").build(),
                NamedCacheRecord.class,
                "api", "data", "named", id.toString()
        );

        validateGetList(applicationUrl,
                List.of("namedCacheRecords"),
                String.class,
                "api", "caches", "providers", "namedCache"
        );

        validateGetList(applicationUrl,
                List.of(id),
                UUID.class,
                "api", "caches", "providers", "namedCache", "caches", "namedCacheRecords", "keys"
        );

        validateGet(applicationUrl,
                NamedCacheRecord.builder().id(id).value("PutTest").build(),
                NamedCacheRecord.class,
                "api", "caches", "providers", "namedCache", "caches", "namedCacheRecords", "keys", id.toString()
        );

    }

    @Test
    @DisplayName("PUT record and fetch from named cache, validate fetched object and verify presence in cache then delete and verify")
    void putRecordFetchDeleteFromNamedCache() {
        final UUID id = UUID.randomUUID();
        validatePut(applicationUrl,
                NamedCacheRecord.builder().id(id).value("PutTest").build(),
                PutRecordResponse.builder().id(id).links(
                        Set.of(
                                Link.builder()
                                        .rel("named")
                                        .href("fd-caches-ol-cdi/api/data/named/%s".formatted(id.toString()))
                                        .type("GET")
                                        .build()
                        )
                ).build(),
                PutRecordResponse.class,
                "api", "data", "named"
        );

        validateGet(applicationUrl,
                NamedCacheRecord.builder().id(id).value("PutTest").build(),
                NamedCacheRecord.class,
                "api", "data", "named", id.toString()
        );



        validateGetList(applicationUrl,
                List.of("namedCacheRecords"),
                String.class,
                "api", "caches", "providers", "namedCache"
        );

        validateGetList(applicationUrl,
                List.of(id),
                UUID.class,
                "api", "caches", "providers", "namedCache", "caches", "namedCacheRecords", "keys"
        );

        validateGet(applicationUrl,
                NamedCacheRecord.builder().id(id).value("PutTest").build(),
                NamedCacheRecord.class,
                "api", "caches", "providers", "namedCache", "caches", "namedCacheRecords", "keys", id.toString()
        );

        validatePut(applicationUrl,
                NamedCacheRecord.builder().id(id).value("PutTest2").build(),
                PutRecordResponse.builder().id(id).links(
                        Set.of(
                                Link.builder()
                                        .rel("named")
                                        .href("fd-caches-ol-cdi/api/data/named/%s".formatted(id.toString()))
                                        .type("GET")
                                        .build()
                        )
                ).build(),
                PutRecordResponse.class,
                "api", "data", "named"
        );

        validateGetList(applicationUrl,
                List.of(),
                String.class,
                "api", "caches", "providers", "namedCache", "caches", "namedCacheRecords", "keys"
        );

    }

    @Test
    @DisplayName("PUT record and fetch from configured cache, validate fetched object and verify presence in cache")
    void putRecordAndFetchFromOverriddenCache() {
        final UUID id = UUID.randomUUID();
        validatePut(applicationUrl,
                ConfigOverriddenCacheRecord.builder().id(id).value("PutTest").build(),
                PutRecordResponse.builder().id(id).links(
                        Set.of(
                                Link.builder()
                                        .rel("overridden")
                                        .href("fd-caches-ol-cdi/api/data/overridden/%s".formatted(id.toString()))
                                        .type("GET")
                                        .build()
                        )
                ).build(),
                PutRecordResponse.class,
                "api", "data", "overridden"
        );

        validateGet(applicationUrl,
                ConfigOverriddenCacheRecord.builder().id(id).value("PutTest").build(),
                ConfigOverriddenCacheRecord.class,
                "api", "data", "overridden", id.toString()
        );

        validateGetList(applicationUrl,
                List.of("configuredCacheRec"),
                String.class,
                "api", "caches", "providers", "configuredCache"
        );

        validateGetList(applicationUrl,
                List.of(id),
                UUID.class,
                "api", "caches", "providers", "configuredCache", "caches", "configuredCacheRec", "keys"
        );

        validateGet(applicationUrl,
                ConfigOverriddenCacheRecord.builder().id(id).value("PutTest").build(),
                ConfigOverriddenCacheRecord.class,
                "api", "caches", "providers", "configuredCache", "caches", "configuredCacheRec", "keys", id.toString()
        );

    }

    @Test
    @DisplayName("PUT record and fetch from named cache, validate fetched object and verify presence in cache then delete and verify")
    void putRecordFetchDeleteFromConfiguredCache() {
        final UUID id = UUID.randomUUID();
        validatePut(applicationUrl,
                ConfigOverriddenCacheRecord.builder().id(id).value("PutTest").build(),
                PutRecordResponse.builder().id(id).links(
                        Set.of(
                                Link.builder()
                                        .rel("overridden")
                                        .href("fd-caches-ol-cdi/api/data/overridden/%s".formatted(id.toString()))
                                        .type("GET")
                                        .build()
                        )
                ).build(),
                PutRecordResponse.class,
                "api", "data", "overridden"
        );

        validateGet(applicationUrl,
                ConfigOverriddenCacheRecord.builder().id(id).value("PutTest").build(),
                ConfigOverriddenCacheRecord.class,
                "api", "data", "overridden", id.toString()
        );

        validateGetList(applicationUrl,
                List.of("configuredCacheRec"),
                String.class,
                "api", "caches", "providers", "configuredCache"
        );

        validateGetList(applicationUrl,
                List.of(id),
                UUID.class,
                "api", "caches", "providers", "configuredCache", "caches", "configuredCacheRec", "keys"
        );

        validateGet(applicationUrl,
                ConfigOverriddenCacheRecord.builder().id(id).value("PutTest").build(),
                ConfigOverriddenCacheRecord.class,
                "api", "caches", "providers", "configuredCache", "caches", "configuredCacheRec", "keys", id.toString()
        );

        validatePut(applicationUrl,
                ConfigOverriddenCacheRecord.builder().id(id).value("PutTest2").build(),
                PutRecordResponse.builder().id(id).links(
                        Set.of(
                                Link.builder()
                                        .rel("overridden")
                                        .href("fd-caches-ol-cdi/api/data/overridden/%s".formatted(id.toString()))
                                        .type("GET")
                                        .build()
                        )
                ).build(),
                PutRecordResponse.class,
                "api", "data", "overridden"
        );

        validateGetList(applicationUrl,
                List.of(),
                String.class,
                "api", "caches", "providers", "configuredCache", "caches", "configuredCacheRec", "keys"
        );

    }

    @Test
    @DisplayName("PUT record and fetch record, cache disabled in config")
    void putRecordAndFetchCacheDisabled() {
        final UUID id = UUID.randomUUID();
        validatePut(applicationUrl,
                DisabledCacheRecord.builder().id(id).value("PutTest").build(),
                PutRecordResponse.builder().id(id).links(
                        Set.of(
                                Link.builder()
                                        .rel("disabled")
                                        .href("fd-caches-ol-cdi/api/data/disabled/%s".formatted(id.toString()))
                                        .type("GET")
                                        .build()
                        )
                ).build(),
                PutRecordResponse.class,
                "api", "data", "disabled"
        );

        validateGet(applicationUrl,
                DisabledCacheRecord.builder().id(id).value("PutTest").build(),
                DisabledCacheRecord.class,
                "api", "data", "disabled", id.toString()
        );

        validateGetList(applicationUrl,
                List.of(),
                String.class,
                "api", "caches", "providers", "configuredCache"
        );

        validateGetList(applicationUrl,
                List.of(),
                String.class,
                "api", "caches", "providers", "namedCache"
        );

        validateGetList(applicationUrl,
                List.of(),
                String.class,
                "api", "caches", "providers", "internal.default.cache"
        );

    }

}
