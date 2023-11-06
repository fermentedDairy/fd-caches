package org.fermented.dairy.caches.rest.boundary;

import static org.fermented.dairy.caches.rest.URLS.APP_ROOT;
import static org.fermented.dairy.caches.rest.URLS.CONTEXT_ROOT;
import static org.fermented.dairy.caches.rest.URLS.DATA_ROOT;
import static org.fermented.dairy.caches.rest.URLS.generateURLfromParts;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.text.StringSubstitutor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponseSchema;
import org.fermented.dairy.caches.rest.controller.DataService;
import org.fermented.dairy.caches.rest.entity.records.ConfigOverriddenCacheRecord;
import org.fermented.dairy.caches.rest.entity.records.DefaultCacheRecord;
import org.fermented.dairy.caches.rest.entity.records.DisabledCacheRecord;
import org.fermented.dairy.caches.rest.entity.records.NamedCacheRecord;
import org.fermented.dairy.caches.rest.entity.rto.Link;
import org.fermented.dairy.caches.rest.entity.rto.data.PutRecordResponse;

/**
 * REST Boundary for data operations.
 */
@ApplicationScoped
@AllArgsConstructor(onConstructor = @__(@Inject))
@NoArgsConstructor
@Path(DATA_ROOT)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Log
public class DataRestService {

    private static final String DEFAULT_ID_PATH = "default/{id}";

    private static final String NAMED_ID_PATH = "named/{id}";

    private static final String OVERRIDDEN_ID_PATH = "overridden/{id}";

    private static final String DISABLED_ID_PATH = "disabled/{id}";

    private DataService dataService;

    @PUT
    @Path("/default")
    @APIResponse(
            responseCode = "201",
            description = "PUT a new default cache record",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @Operation(
            summary = "Gets a set of keys for a given cache and provider")
    @APIResponseSchema(PutRecordResponse.class)
    public PutRecordResponse addDefault(@NotNull final DefaultCacheRecord cacheRecord) {
        dataService.addDefaultCacheRecord(cacheRecord);
        return PutRecordResponse.builder()
                .id(cacheRecord.id())
                .link(
                        Link.builder()
                                .rel("default")
                                .href(StringSubstitutor.replace(
                                        generateURLfromParts(CONTEXT_ROOT, APP_ROOT, DATA_ROOT, DEFAULT_ID_PATH),
                                        Map.of("id", cacheRecord.id()),
                                        "{", "}"))
                                .type("GET")
                                .build()
                )
                .build();
    }

    @GET
    @Path(DEFAULT_ID_PATH)
    @APIResponse(
            responseCode = "200",
            description = "GET the DefaultCacheRecord requested",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @APIResponse(
            responseCode = "404",
            description = "If the ID is not present.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @Operation(
            summary = "Gets a set of keys for a given cache and provider")
    @APIResponseSchema(DefaultCacheRecord.class)
    public DefaultCacheRecord getDefault(@PathParam("id") UUID id) {
        return dataService.getDefault(id).orElseThrow(() -> new NotFoundException("No DefaultCacheRecord with id %s found".formatted(id)));
    }

    @PUT
    @Path("/named")
    @APIResponse(
            responseCode = "201",
            description = "PUT a new named cache record",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @Operation(
            summary = "Gets a set of keys for a given cache and provider")
    @APIResponseSchema(PutRecordResponse.class)
    public PutRecordResponse addNamed(@NotNull final NamedCacheRecord cacheRecord) {
        dataService.addNamedCacheRecord(cacheRecord);
        return PutRecordResponse.builder()
                .id(cacheRecord.id())
                .link(
                        Link.builder()
                                .rel("named")
                                .href(StringSubstitutor.replace(
                                        generateURLfromParts(CONTEXT_ROOT, APP_ROOT, DATA_ROOT, NAMED_ID_PATH),
                                        Map.of("id", cacheRecord.id()),
                                        "{", "}"))
                                .type("GET")
                                .build()
                )
                .build();
    }

    @GET
    @Path(NAMED_ID_PATH)
    @APIResponse(
            responseCode = "200",
            description = "GET the NamedCacheRecord requested",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @APIResponse(
            responseCode = "404",
            description = "If the ID is not present.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @Operation(
            summary = "Gets a set of keys for a given cache and provider")
    @APIResponseSchema(NamedCacheRecord.class)
    public NamedCacheRecord getNamed(@PathParam("id") final UUID id) {
        return dataService.getNamedCacheRecord(id)
                .orElseThrow(() -> new NotFoundException("No NamedCacheRecord with id %s found".formatted(id)));
    }

    @PUT
    @Path("/overridden")
    @APIResponse(
            responseCode = "201",
            description = "PUT a new overridden cache record",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @Operation(
            summary = "Gets a set of keys for a given cache and provider")
    @APIResponseSchema(PutRecordResponse.class)
    public PutRecordResponse addOverridden(@NotNull final ConfigOverriddenCacheRecord cacheRecord) {
        dataService.addConfigOverriddenCacheRecord(cacheRecord);
        return PutRecordResponse.builder()
                .id(cacheRecord.id())
                .link(
                        Link.builder()
                                .rel("overridden")
                                .href(StringSubstitutor.replace(
                                        generateURLfromParts(CONTEXT_ROOT, APP_ROOT, DATA_ROOT, OVERRIDDEN_ID_PATH),
                                        Map.of("id", cacheRecord.id()),
                                        "{", "}"))
                                .type("GET")
                                .build()
                )
                .build();
    }

    @GET
    @Path(OVERRIDDEN_ID_PATH)
    @APIResponse(
            responseCode = "200",
            description = "GET the ConfigOverriddenCacheRecord requested",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @APIResponse(
            responseCode = "404",
            description = "If the ID is not present.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @Operation(
            summary = "Gets a set of keys for a given cache and provider")
    @APIResponseSchema(ConfigOverriddenCacheRecord.class)
    public ConfigOverriddenCacheRecord getOverridden(@PathParam("id") final UUID id) {
        return dataService.getConfigOverriddenCacheRecord(id)
                .orElseThrow(() -> new NotFoundException("No ConfigOverriddenCacheRecord with id %s found".formatted(id)));
    }

    @PUT
    @Path("/disabled")
    @APIResponse(
            responseCode = "201",
            description = "PUT a new disabled cache record",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @Operation(
            summary = "Gets a set of keys for a given cache and provider")
    @APIResponseSchema(PutRecordResponse.class)
    public PutRecordResponse addDisabled(@NotNull final DisabledCacheRecord cacheRecord) {
        dataService.addDisabledCacheRecord(cacheRecord);
        return PutRecordResponse.builder()
                .id(cacheRecord.id())
                .link(
                        Link.builder()
                                .rel("disabled")
                                .href(StringSubstitutor.replace(
                                        generateURLfromParts(CONTEXT_ROOT, APP_ROOT, DATA_ROOT, DISABLED_ID_PATH),
                                        Map.of("id", cacheRecord.id()),
                                        "{", "}"))
                                .type("GET")
                                .build()
                )
                .build();
    }

    @GET
    @Path(DISABLED_ID_PATH)
    @APIResponse(
            responseCode = "200",
            description = "GET the DisabledCacheRecord requested",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @APIResponse(
            responseCode = "404",
            description = "If the ID is not present.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @Operation(
            summary = "Gets a set of keys for a given cache and provider")
    @APIResponseSchema(DisabledCacheRecord.class)
    public DisabledCacheRecord getDisabled(@PathParam("id") final UUID id) {
        return dataService.getDisabledCacheRecord(id)
                .orElseThrow(() -> new NotFoundException("No DisabledCacheRecord with id %s found".formatted(id)));
    }
}
