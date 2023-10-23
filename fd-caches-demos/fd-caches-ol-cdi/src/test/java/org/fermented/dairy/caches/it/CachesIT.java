package org.fermented.dairy.caches.it;

import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.endpoint.CitrusEndpoints;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.junit.jupiter.CitrusSupport;
import com.consol.citrus.testng.TestNGCitrusSupport;
import org.fermented.dairy.caches.rest.entity.records.DefaultCacheRecord;
import org.fermented.dairy.caches.rest.entity.rto.Link;
import org.fermented.dairy.caches.rest.entity.rto.data.PutRecordResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.microshed.testing.jupiter.MicroShedTest;
import org.microshed.testing.testcontainers.ApplicationContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.fermented.dairy.caches.it.utils.RestUtils.validateGet;
import static org.fermented.dairy.caches.it.utils.RestUtils.validateGetList;
import static org.fermented.dairy.caches.it.utils.RestUtils.validatePut;

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
    void beforeEach() {
        applicationUrl = app.getApplicationURL();
        client = CitrusEndpoints.http()
                .client()
                .requestUrl(applicationUrl)
                .build();
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
}
