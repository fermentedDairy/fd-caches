package org.fermented.dairy.caches.sb.aop.it;

import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.endpoint.CitrusEndpoints;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.junit.jupiter.CitrusSupport;
import com.consol.citrus.testng.TestNGCitrusSupport;
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

import static org.fermented.dairy.caches.sb.aop.it.utils.RestUtils.*;


@MicroShedTest
@CitrusSupport
public class CachesIT extends TestNGCitrusSupport {

    @Container
    public static ApplicationContainer app = new ApplicationContainer()
            .withAppContextRoot("/fd-caches-ol-cdi")
            .withReadinessPath("/health/ready");

    private String applicationUrl;

    private HttpClient client;

    @BeforeEach
    void beforeEach(@CitrusResource final TestContext context) {
        applicationUrl = app.getApplicationURL();
        client = CitrusEndpoints.http()
                .client()
                .requestUrl(applicationUrl)
                .build();

        validateDelete(client, context, "api/caches/providers", "{}", Map.of(
                "internal.default.cache", "Purged",
                "configuredCache", "Purged",
                "namedCache",  "Purged"));
    }

    @Test
    @CitrusTest
    @DisplayName("GET all provider names")
    void getAllProviderNames(@CitrusResource final TestContext context) {
        validateGetList(client, context, "api/caches/providers", List.of("internal.default.cache", "configuredCache", "namedCache"));
    }


    @Test
    @CitrusTest
    @DisplayName("PUT record and fetch from default cache, validate fetched object and verify presence in cache")
    void putRecordAndFetchFromDefaultCache(@CitrusResource final TestContext context) {
        final UUID id = UUID.randomUUID();
        validatePut(client, context, "api/data/default",
                DefaultCacheRecord.builder().id(id).value("PutTest").build(),
                PutRecordResponse.builder().id(id).links(
                        Set.of(
                                Link.builder()
                                        .rel("default")
                                        .href("fd-caches-ol-cdi/api/data/default/%s".formatted(id.toString()))
                                        .type("GET")
                                        .build()
                        )
                ).build()
        );

        validateGet(client, context, "api/data/default/%s".formatted(id.toString()),
                DefaultCacheRecord.builder().id(id).value("PutTest").build()
        );

        validateGetList(client, context, "api/caches/providers/internal.default.cache",
                List.of(DefaultCacheRecord.class.getCanonicalName())
        );

        validateGetList(client, context,
                "api/caches/providers/internal.default.cache/caches/%s/keys"
                        .formatted(DefaultCacheRecord.class.getCanonicalName()),
                List.of(id)
        );

        validateGet(client, context,
                "api/caches/providers/internal.default.cache/caches/%s/keys/%s"
                        .formatted(
                                DefaultCacheRecord.class.getCanonicalName(),
                                id),
                DefaultCacheRecord.builder().id(id).value("PutTest").build()
        );

    }

    @Test
    @CitrusTest
    @DisplayName("PUT record and fetch from default cache, validate fetched object and verify presence in cache then delete and verify")
    void putRecordFetchDeleteFromDefaultCache(@CitrusResource final TestContext context) {
        final UUID id = UUID.randomUUID();
        validatePut(client, context, "api/data/default",
                DefaultCacheRecord.builder().id(id).value("PutTest").build(),
                PutRecordResponse.builder().id(id).links(
                        Set.of(
                                Link.builder()
                                        .rel("default")
                                        .href("fd-caches-ol-cdi/api/data/default/%s".formatted(id.toString()))
                                        .type("GET")
                                        .build()
                        )
                ).build()
        );

        validateGet(client, context, "api/data/default/%s".formatted(id.toString()),
                DefaultCacheRecord.builder().id(id).value("PutTest").build()
        );

        validateGetList(client, context, "api/caches/providers/internal.default.cache",
                List.of(DefaultCacheRecord.class.getCanonicalName())
        );

        validateGetList(client, context,
                "api/caches/providers/internal.default.cache/caches/%s/keys"
                        .formatted(DefaultCacheRecord.class.getCanonicalName()),
                List.of(id)
        );

        validateGet(client, context,
                "api/caches/providers/internal.default.cache/caches/%s/keys/%s"
                        .formatted(
                                DefaultCacheRecord.class.getCanonicalName(),
                                id),
                DefaultCacheRecord.builder().id(id).value("PutTest").build()
        );

        validatePut(client, context, "api/data/default",
                DefaultCacheRecord.builder().id(id).value("PutTest2").build(),
                PutRecordResponse.builder().id(id).links(
                        Set.of(
                                Link.builder()
                                        .rel("default")
                                        .href("fd-caches-ol-cdi/api/data/default/%s".formatted(id.toString()))
                                        .type("GET")
                                        .build()
                        )
                ).build()
        );

        validateGetList(client, context,
                "api/caches/providers/internal.default.cache/caches/%s/keys"
                        .formatted(DefaultCacheRecord.class.getCanonicalName()),
                List.of()
        );

    }

    @Test
    @CitrusTest
    @DisplayName("PUT record and fetch from named cache, validate fetched object and verify presence in cache")
    void putRecordAndFetchFromNamedCache(@CitrusResource final TestContext context) {
        final UUID id = UUID.randomUUID();
        validatePut(client, context, "api/data/named",
                NamedCacheRecord.builder().id(id).value("PutTest").build(),
                PutRecordResponse.builder().id(id).links(
                        Set.of(
                                Link.builder()
                                        .rel("named")
                                        .href("fd-caches-ol-cdi/api/data/named/%s".formatted(id.toString()))
                                        .type("GET")
                                        .build()
                        )
                ).build()
        );

        validateGet(client, context, "api/data/named/%s".formatted(id.toString()),
                NamedCacheRecord.builder().id(id).value("PutTest").build()
        );

        validateGetList(client, context, "api/caches/providers/namedCache",
                List.of("namedCacheRecords")
        );

        validateGetList(client, context,
                "api/caches/providers/namedCache/caches/%s/keys"
                        .formatted("namedCacheRecords"),
                List.of(id)
        );

        validateGet(client, context,
                "api/caches/providers/namedCache/caches/%s/keys/%s"
                        .formatted(
                                "namedCacheRecords",
                                id),
                DefaultCacheRecord.builder().id(id).value("PutTest").build()
        );

    }

    @Test
    @CitrusTest
    @DisplayName("PUT record and fetch from named cache, validate fetched object and verify presence in cache then delete and verify")
    void putRecordFetchDeleteFromNamedCache(@CitrusResource final TestContext context) {
        final UUID id = UUID.randomUUID();
        validatePut(client, context, "api/data/named",
                NamedCacheRecord.builder().id(id).value("PutTest").build(),
                PutRecordResponse.builder().id(id).links(
                        Set.of(
                                Link.builder()
                                        .rel("named")
                                        .href("fd-caches-ol-cdi/api/data/named/%s".formatted(id.toString()))
                                        .type("GET")
                                        .build()
                        )
                ).build()
        );

        validateGet(client, context, "api/data/named/%s".formatted(id.toString()),
                NamedCacheRecord.builder().id(id).value("PutTest").build()
        );

        validateGetList(client, context, "api/caches/providers/namedCache",
                List.of("namedCacheRecords")
        );

        validateGetList(client, context,
                "api/caches/providers/namedCache/caches/%s/keys"
                        .formatted("namedCacheRecords"),
                List.of(id)
        );

        validateGet(client, context,
                "api/caches/providers/namedCache/caches/%s/keys/%s"
                        .formatted(
                                "namedCacheRecords",
                                id),
                DefaultCacheRecord.builder().id(id).value("PutTest").build()
        );

        validatePut(client, context, "api/data/named",
                NamedCacheRecord.builder().id(id).value("PutTest2").build(),
                PutRecordResponse.builder().id(id).links(
                        Set.of(
                                Link.builder()
                                        .rel("named")
                                        .href("fd-caches-ol-cdi/api/data/named/%s".formatted(id.toString()))
                                        .type("GET")
                                        .build()
                        )
                ).build()
        );

        validateGetList(client, context,
                "api/caches/providers/namedCache/caches/%s/keys"
                        .formatted("namedCacheRecords"),
                List.of()
        );

    }

    @Test
    @CitrusTest
    @DisplayName("PUT record and fetch from configured cache, validate fetched object and verify presence in cache")
    void putRecordAndFetchFromOverriddenCache(@CitrusResource final TestContext context) {
        final UUID id = UUID.randomUUID();
        validatePut(client, context, "api/data/overridden",
                ConfigOverriddenCacheRecord.builder().id(id).value("PutTest").build(),
                PutRecordResponse.builder().id(id).links(
                        Set.of(
                                Link.builder()
                                        .rel("overridden")
                                        .href("fd-caches-ol-cdi/api/data/overridden/%s".formatted(id.toString()))
                                        .type("GET")
                                        .build()
                        )
                ).build()
        );

        validateGet(client, context, "api/data/overridden/%s".formatted(id.toString()),
                ConfigOverriddenCacheRecord.builder().id(id).value("PutTest").build()
        );

        validateGetList(client, context, "api/caches/providers/configuredCache",
                List.of("configuredCacheRec")
        );

        validateGetList(client, context,
                "api/caches/providers/configuredCache/caches/%s/keys"
                        .formatted("configuredCacheRec"),
                List.of(id)
        );

        validateGet(client, context,
                "api/caches/providers/configuredCache/caches/%s/keys/%s"
                        .formatted(
                                "configuredCacheRec",
                                id),
                ConfigOverriddenCacheRecord.builder().id(id).value("PutTest").build()
        );

    }

    @Test
    @CitrusTest
    @DisplayName("PUT record and fetch from named cache, validate fetched object and verify presence in cache then delete and verify")
    void putRecordFetchDeleteFromConfiguredCache(@CitrusResource final TestContext context) {
        final UUID id = UUID.randomUUID();
        validatePut(client, context, "api/data/overridden",
                ConfigOverriddenCacheRecord.builder().id(id).value("PutTest").build(),
                PutRecordResponse.builder().id(id).links(
                        Set.of(
                                Link.builder()
                                        .rel("overridden")
                                        .href("fd-caches-ol-cdi/api/data/overridden/%s".formatted(id.toString()))
                                        .type("GET")
                                        .build()
                        )
                ).build()
        );

        validateGet(client, context, "api/data/overridden/%s".formatted(id.toString()),
                ConfigOverriddenCacheRecord.builder().id(id).value("PutTest").build()
        );

        validateGetList(client, context, "api/caches/providers/configuredCache",
                List.of("configuredCacheRec")
        );

        validateGetList(client, context,
                "api/caches/providers/configuredCache/caches/%s/keys"
                        .formatted("configuredCacheRec"),
                List.of(id)
        );

        validateGet(client, context,
                "api/caches/providers/configuredCache/caches/%s/keys/%s"
                        .formatted(
                                "configuredCacheRec",
                                id),
                ConfigOverriddenCacheRecord.builder().id(id).value("PutTest").build()
        );

        validatePut(client, context, "api/data/overridden",
                ConfigOverriddenCacheRecord.builder().id(id).value("PutTest2").build(),
                PutRecordResponse.builder().id(id).links(
                        Set.of(
                                Link.builder()
                                        .rel("overridden")
                                        .href("fd-caches-ol-cdi/api/data/overridden/%s".formatted(id.toString()))
                                        .type("GET")
                                        .build()
                        )
                ).build()
        );

        validateGetList(client, context,
                "api/caches/providers/configuredCache/caches/%s/keys"
                        .formatted("configuredCacheRec"),
                List.of()
        );

    }

    @Test
    @CitrusTest
    @DisplayName("PUT record and fetch record, cache disabled in config")
    void putRecordAndFetchCacheDisabled(@CitrusResource final TestContext context) {
        final UUID id = UUID.randomUUID();
        validatePut(client, context, "api/data/disabled",
                DisabledCacheRecord.builder().id(id).value("PutTest").build(),
                PutRecordResponse.builder().id(id).links(
                        Set.of(
                                Link.builder()
                                        .rel("disabled")
                                        .href("fd-caches-ol-cdi/api/data/disabled/%s".formatted(id.toString()))
                                        .type("GET")
                                        .build()
                        )
                ).build()
        );

        validateGet(client, context, "api/data/disabled/%s".formatted(id.toString()),
                DisabledCacheRecord.builder().id(id).value("PutTest").build()
        );

        validateGetList(client, context, "api/caches/providers/configuredCache",
                List.of()
        );

        validateGetList(client, context, "api/caches/providers/namedCache",
                List.of()
        );

        validateGetList(client, context, "api/caches/providers/internal.default.cache",
                List.of()
        );

    }

}
