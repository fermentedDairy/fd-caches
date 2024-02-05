package org.fermented.dairy.caches.sb.aop.rest.boundary;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.text.StringSubstitutor;
import org.fermented.dairy.caches.sb.aop.rest.controller.aspect.Logged;
import org.fermented.dairy.caches.sb.aop.rest.controller.service.DataService;
import org.fermented.dairy.caches.sb.aop.rest.entity.records.ConfigOverriddenCacheRecord;
import org.fermented.dairy.caches.sb.aop.rest.entity.records.DefaultCacheRecord;
import org.fermented.dairy.caches.sb.aop.rest.entity.records.DisabledCacheRecord;
import org.fermented.dairy.caches.sb.aop.rest.entity.records.NamedCacheRecord;
import org.fermented.dairy.caches.sb.aop.rest.entity.rto.data.Link;
import org.fermented.dairy.caches.sb.aop.rest.entity.rto.data.PutRecordResponse;
import org.fermented.dairy.caches.sb.aop.rest.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

import static org.fermented.dairy.caches.sb.aop.rest.utils.Urls.APP_ROOT;
import static org.fermented.dairy.caches.sb.aop.rest.utils.Urls.CONTEXT_ROOT;
import static org.fermented.dairy.caches.sb.aop.rest.utils.Urls.DATA_ROOT;
import static org.fermented.dairy.caches.sb.aop.rest.utils.Urls.generateUrlFromParts;

@AllArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("data")
@Log
public class DataRestController {

    private static final String DEFAULT_ID_PATH = "default/{id}";

    private static final String NAMED_ID_PATH = "named/{id}";

    private static final String OVERRIDDEN_ID_PATH = "overridden/{id}";

    private static final String DISABLED_ID_PATH = "disabled/{id}";

    private final DataService dataService;

    @PutMapping("default")
    @ApiResponse(
            responseCode = "201",
            description = "PUT a new default cache record",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @Operation(
            summary = "Gets a set of keys for a given cache and provider")
    @Schema(contentSchema = PutRecordResponse.class)
    @Logged
    public PutRecordResponse addDefault(@NotNull @RequestBody final DefaultCacheRecord cacheRecord) {
        dataService.addDefaultCacheRecord(cacheRecord);
        return PutRecordResponse.builder()
                .id(cacheRecord.id())
                .link(
                        Link.builder()
                                .rel("default")
                                .href(StringSubstitutor.replace(
                                        generateUrlFromParts(CONTEXT_ROOT, APP_ROOT, DATA_ROOT, DEFAULT_ID_PATH),
                                        Map.of("id", cacheRecord.id()),
                                        "{", "}"))
                                .type("GET")
                                .build()
                )
                .build();
    }

    @GetMapping(DEFAULT_ID_PATH)
    @ApiResponse(
            responseCode = "200",
            description = "GET the DefaultCacheRecord requested",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(
            responseCode = "404",
            description = "If the ID is not present.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @Operation(
            summary = "Gets a set of keys for a given cache and provider")
    @Schema(contentSchema = DefaultCacheRecord.class)
    public DefaultCacheRecord getDefault(@PathVariable("id") final UUID id) {
        return dataService.getDefault(id).orElseThrow(() -> new NotFoundException("No DefaultCacheRecord with id %s found".formatted(id)));
    }

    @SuppressWarnings("checkstyle:MissingJavadocMethod")//In openAPI
    @PutMapping("named")
    @ApiResponse(
            responseCode = "201",
            description = "PUT a new named cache record",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @Operation(
            summary = "Gets a set of keys for a given cache and provider")
    @Schema(contentSchema = PutRecordResponse.class)
    public PutRecordResponse addNamed(@NotNull @RequestBody final NamedCacheRecord cacheRecord) {
        dataService.addNamedCacheRecord(cacheRecord);
        return PutRecordResponse.builder()
                .id(cacheRecord.id())
                .link(
                        Link.builder()
                                .rel("named")
                                .href(StringSubstitutor.replace(
                                        generateUrlFromParts(CONTEXT_ROOT, APP_ROOT, DATA_ROOT, NAMED_ID_PATH),
                                        Map.of("id", cacheRecord.id()),
                                        "{", "}"))
                                .type("GET")
                                .build()
                )
                .build();
    }

    @GetMapping(NAMED_ID_PATH)
    @ApiResponse(
            responseCode = "200",
            description = "GET the NamedCacheRecord requested",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(
            responseCode = "404",
            description = "If the ID is not present.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @Operation(
            summary = "Gets a set of keys for a given cache and provider")
    @Schema(contentSchema = NamedCacheRecord.class)
    public NamedCacheRecord getNamed(@PathVariable("id") final UUID id) {
        return dataService.getNamedCacheRecord(id)
                .orElseThrow(() -> new NotFoundException("No NamedCacheRecord with id %s found".formatted(id)));
    }

    @SuppressWarnings("checkstyle:MissingJavadocMethod")//In openAPI
    @PutMapping("overridden")
    @ApiResponse(
            responseCode = "201",
            description = "PUT a new overridden cache record",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @Operation(
            summary = "Gets a set of keys for a given cache and provider")
    @Schema(contentSchema = PutRecordResponse.class)
    public PutRecordResponse addOverridden(@NotNull @RequestBody final ConfigOverriddenCacheRecord cacheRecord) {
        dataService.addConfigOverriddenCacheRecord(cacheRecord);
        return PutRecordResponse.builder()
                .id(cacheRecord.id())
                .link(
                        Link.builder()
                                .rel("overridden")
                                .href(StringSubstitutor.replace(
                                        generateUrlFromParts(CONTEXT_ROOT, APP_ROOT, DATA_ROOT, OVERRIDDEN_ID_PATH),
                                        Map.of("id", cacheRecord.id()),
                                        "{", "}"))
                                .type("GET")
                                .build()
                )
                .build();
    }

    @GetMapping(OVERRIDDEN_ID_PATH)
    @ApiResponse(
            responseCode = "200",
            description = "GET the ConfigOverriddenCacheRecord requested",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(
            responseCode = "404",
            description = "If the ID is not present.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @Operation(
            summary = "Gets a set of keys for a given cache and provider")
    @Schema(contentSchema = ConfigOverriddenCacheRecord.class)
    public ConfigOverriddenCacheRecord getOverridden(@PathVariable("id") final UUID id) {
        return dataService.getConfigOverriddenCacheRecord(id)
                .orElseThrow(() -> new NotFoundException("No ConfigOverriddenCacheRecord with id %s found".formatted(id)));
    }

    @SuppressWarnings("checkstyle:MissingJavadocMethod")//In openAPI
    @PutMapping("disabled")
    @ApiResponse(
            responseCode = "201",
            description = "PUT a new disabled cache record",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @Operation(
            summary = "Gets a set of keys for a given cache and provider")
    @Schema(contentSchema = PutRecordResponse.class)
    public PutRecordResponse addDisabled(@NotNull @RequestBody final DisabledCacheRecord cacheRecord) {
        dataService.addDisabledCacheRecord(cacheRecord);
        return PutRecordResponse.builder()
                .id(cacheRecord.id())
                .link(
                        Link.builder()
                                .rel("disabled")
                                .href(StringSubstitutor.replace(
                                        generateUrlFromParts(CONTEXT_ROOT, APP_ROOT, DATA_ROOT, DISABLED_ID_PATH),
                                        Map.of("id", cacheRecord.id()),
                                        "{", "}"))
                                .type("GET")
                                .build()
                )
                .build();
    }

    @GetMapping(DISABLED_ID_PATH)
    @ApiResponse(
            responseCode = "200",
            description = "GET the DisabledCacheRecord requested",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(
            responseCode = "404",
            description = "If the ID is not present.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @Operation(
            summary = "Gets a set of keys for a given cache and provider")
    @Schema(contentSchema = DisabledCacheRecord.class)
    public DisabledCacheRecord getDisabled(@PathVariable("id") final UUID id) {
        return dataService.getDisabledCacheRecord(id)
                .orElseThrow(() -> new NotFoundException("No DisabledCacheRecord with id %s found".formatted(id)));
    }
}
