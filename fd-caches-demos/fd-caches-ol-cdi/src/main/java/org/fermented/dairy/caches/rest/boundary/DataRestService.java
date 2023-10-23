package org.fermented.dairy.caches.rest.boundary;

import static org.fermented.dairy.caches.rest.URLS.APP_ROOT;
import static org.fermented.dairy.caches.rest.URLS.CONTEXT_ROOT;
import static org.fermented.dairy.caches.rest.URLS.DATA_ROOT;
import static org.fermented.dairy.caches.rest.URLS.generateURLfromParts;

import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Map;
import java.util.UUID;
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
import org.fermented.dairy.caches.rest.entity.rto.data.DeleteRecordResponse;
import org.fermented.dairy.caches.rest.entity.rto.data.PutRecordResponse;

@Path(DATA_ROOT)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Log
public class DataRestService {

    public static final String DEFAULT_ID_PATH = "default/{id}";

    @Inject
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

    @DELETE
    @Path(DEFAULT_ID_PATH)
    public DeleteRecordResponse deleteDefault(@PathParam("id") final String id) {
        throw new NotFoundException("DeleteRecordResponse with id %s not found".formatted(id));
    }

    @PUT
    @Path("/named")
    public PutRecordResponse addNamed(@NotNull final NamedCacheRecord cacheRecord) {
        return null;
    }

    @GET
    @Path("/named/{id}")
    public NamedCacheRecord getNamed(@PathParam("id") final String id) {
        throw new NotFoundException("NamedCacheRecord with id: %s not found".formatted(id));
    }

    @DELETE
    @Path("/named/{id}")
    public DeleteRecordResponse deleteNamed(@PathParam("id") final String id) {
        throw new NotFoundException("NamedCacheRecord with id %s not found".formatted(id));
    }

    @PUT
    @Path("/overridden")
    public PutRecordResponse addOverridden(@NotNull final ConfigOverriddenCacheRecord cacheRecord) {
        return null;
    }

    @GET
    @Path("/overridden/{id}")
    public ConfigOverriddenCacheRecord getOverridden(@PathParam("id") final String id) {
        throw new NotFoundException("ConfigOverriddenCacheRecord with id: %s not found".formatted(id));
    }

    @DELETE
    @Path("/overridden/{id}")
    public ConfigOverriddenCacheRecord deleteOverridden(@PathParam("id") final String id) {
        throw new NotFoundException("NamedCacheRecord with id %s not found".formatted(id));
    }

    @PUT
    @Path("/disabled")
    public PutRecordResponse addDisabled(@NotNull final ConfigOverriddenCacheRecord cacheRecord) {
        return null;
    }

    @GET
    @Path("/disabled/{id}")
    public DisabledCacheRecord getDisabled(@PathParam("id") final String id) {
        throw new NotFoundException("DisabledCacheRecord with id: %s not found".formatted(id));
    }

    @DELETE
    @Path("/disabled/{id}")
    public DeleteRecordResponse deleteDisabled(@PathParam("id") final String id) {
        throw new NotFoundException("DisabledCacheRecord with id %s not found".formatted(id));
    }
}
