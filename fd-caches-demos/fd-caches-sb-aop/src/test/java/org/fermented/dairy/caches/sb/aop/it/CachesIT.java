package org.fermented.dairy.caches.sb.aop.it;

import org.fermented.dairy.caches.sb.aop.rest.entity.records.ConfigOverriddenCacheRecord;
import org.fermented.dairy.caches.sb.aop.rest.entity.records.DefaultCacheRecord;
import org.fermented.dairy.caches.sb.aop.rest.entity.records.DisabledCacheRecord;
import org.fermented.dairy.caches.sb.aop.rest.entity.records.NamedCacheRecord;
import org.fermented.dairy.caches.sb.aop.rest.entity.rto.Link;
import org.fermented.dairy.caches.sb.aop.rest.entity.rto.data.PutRecordResponse;
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
            .withAppContextRoot("/fd-caches-sb-aop/api")
            .withReadinessPath("/fd-caches-sb-aop/api/actuator/health");

    private String applicationUrl;

    @BeforeEach
    void beforeEach() {
        applicationUrl = app.getApplicationURL();

        validateDelete(applicationUrl, "{}", Map.of(
                        "internal.default.cache", "Purged",
                        "configuredCache", "Purged",
                        "namedCache",  "Purged"),
                Map.class,
                "caches", "providers");
    }

    @Test
    @DisplayName("GET all provider names")
    void getAllProviderNames() {
        validateGetList(applicationUrl,
                List.of("internal.default.cache", "configuredCache", "namedCache"),
                String.class,
                "caches", "providers");
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
                "data", "default"
        );

        validateGet(applicationUrl,
                DefaultCacheRecord.builder().id(id).value("PutTest").build(),
                DefaultCacheRecord.class,
                "data", "default", id.toString()
        );

        validateGetList(applicationUrl,
                List.of(DefaultCacheRecord.class.getCanonicalName()),
                String.class,
                "caches", "providers", "internal.default.cache"
        );

        validateGetList(applicationUrl,
                List.of(id),
                UUID.class,
                "caches", "providers", "internal.default.cache", "caches", DefaultCacheRecord.class.getCanonicalName(), "keys"
        );

        validateGet(applicationUrl,
                DefaultCacheRecord.builder().id(id).value("PutTest").build(),
                DefaultCacheRecord.class,
                "caches", "providers", "internal.default.cache", "caches", DefaultCacheRecord.class.getCanonicalName(), "keys", id.toString()
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
                "data", "default"
        );

        validateGet(applicationUrl,
                DefaultCacheRecord.builder().id(id).value("PutTest").build(),
                DefaultCacheRecord.class,
                "data", "default", id.toString()
        );

        validateGetList(applicationUrl,
                List.of(DefaultCacheRecord.class.getCanonicalName()),
                String.class,
                "caches", "providers", "internal.default.cache"
        );

        validateGetList(applicationUrl,
                List.of(id),
                UUID.class,
                "caches", "providers", "internal.default.cache", "caches", DefaultCacheRecord.class.getCanonicalName(), "keys"
        );

        validateGet(applicationUrl,
                DefaultCacheRecord.builder().id(id).value("PutTest").build(),
                DefaultCacheRecord.class,
                "caches", "providers", "internal.default.cache", "caches", DefaultCacheRecord.class.getCanonicalName(), "keys", id.toString()
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
                "data", "default"
        );

        validateGetList(applicationUrl,
                List.of(),
                String.class,
                "caches", "providers", "internal.default.cache", "caches", DefaultCacheRecord.class.getCanonicalName(), "keys"
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
                "data", "named"
        );

        validateGet(applicationUrl,
                NamedCacheRecord.builder().id(id).value("PutTest").build(),
                NamedCacheRecord.class,
                "data", "named", id.toString()
        );

        validateGetList(applicationUrl,
                List.of("namedCacheRecords"),
                String.class,
                "caches", "providers", "namedCache"
        );

        validateGetList(applicationUrl,
                List.of(id),
                UUID.class,
                "caches", "providers", "namedCache", "caches", "namedCacheRecords", "keys"
        );

        validateGet(applicationUrl,
                NamedCacheRecord.builder().id(id).value("PutTest").build(),
                NamedCacheRecord.class,
                "caches", "providers", "namedCache", "caches", "namedCacheRecords", "keys", id.toString()
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
                "data", "named"
        );

        validateGet(applicationUrl,
                NamedCacheRecord.builder().id(id).value("PutTest").build(),
                NamedCacheRecord.class,
                "data", "named", id.toString()
        );



        validateGetList(applicationUrl,
                List.of("namedCacheRecords"),
                String.class,
                "caches", "providers", "namedCache"
        );

        validateGetList(applicationUrl,
                List.of(id),
                UUID.class,
                "caches", "providers", "namedCache", "caches", "namedCacheRecords", "keys"
        );

        validateGet(applicationUrl,
                NamedCacheRecord.builder().id(id).value("PutTest").build(),
                NamedCacheRecord.class,
                "caches", "providers", "namedCache", "caches", "namedCacheRecords", "keys", id.toString()
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
                "data", "named"
        );

        validateGetList(applicationUrl,
                List.of(),
                String.class,
                "caches", "providers", "namedCache", "caches", "namedCacheRecords", "keys"
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
                "data", "overridden"
        );

        validateGet(applicationUrl,
                ConfigOverriddenCacheRecord.builder().id(id).value("PutTest").build(),
                ConfigOverriddenCacheRecord.class,
                "data", "overridden", id.toString()
        );

        validateGetList(applicationUrl,
                List.of("configuredCacheRec"),
                String.class,
                "caches", "providers", "configuredCache"
        );

        validateGetList(applicationUrl,
                List.of(id),
                UUID.class,
                "caches", "providers", "configuredCache", "caches", "configuredCacheRec", "keys"
        );

        validateGet(applicationUrl,
                ConfigOverriddenCacheRecord.builder().id(id).value("PutTest").build(),
                ConfigOverriddenCacheRecord.class,
                "caches", "providers", "configuredCache", "caches", "configuredCacheRec", "keys", id.toString()
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
                "data", "overridden"
        );

        validateGet(applicationUrl,
                ConfigOverriddenCacheRecord.builder().id(id).value("PutTest").build(),
                ConfigOverriddenCacheRecord.class,
                "data", "overridden", id.toString()
        );

        validateGetList(applicationUrl,
                List.of("configuredCacheRec"),
                String.class,
                "caches", "providers", "configuredCache"
        );

        validateGetList(applicationUrl,
                List.of(id),
                UUID.class,
                "caches", "providers", "configuredCache", "caches", "configuredCacheRec", "keys"
        );

        validateGet(applicationUrl,
                ConfigOverriddenCacheRecord.builder().id(id).value("PutTest").build(),
                ConfigOverriddenCacheRecord.class,
                "caches", "providers", "configuredCache", "caches", "configuredCacheRec", "keys", id.toString()
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
                "data", "overridden"
        );

        validateGetList(applicationUrl,
                List.of(),
                String.class,
                "caches", "providers", "configuredCache", "caches", "configuredCacheRec", "keys"
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
                "data", "disabled"
        );

        validateGet(applicationUrl,
                DisabledCacheRecord.builder().id(id).value("PutTest").build(),
                DisabledCacheRecord.class,
                "data", "disabled", id.toString()
        );

        validateGetList(applicationUrl,
                List.of(),
                String.class,
                "caches", "providers", "configuredCache"
        );

        validateGetList(applicationUrl,
                List.of(),
                String.class,
                "caches", "providers", "namedCache"
        );

        validateGetList(applicationUrl,
                List.of(),
                String.class,
                "caches", "providers", "internal.default.cache"
        );

    }

}
