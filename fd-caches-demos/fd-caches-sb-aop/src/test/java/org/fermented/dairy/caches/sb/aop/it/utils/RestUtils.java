package org.fermented.dairy.caches.sb.aop.it.utils;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.http.client.HttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class RestUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    public static <T> void validateGetList(final HttpClient client, final TestContext context, final String path, final List<T> expectedResult) {
        http().client(client)
                .send()
                .get(path)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .build()
                .doExecute(context);

        http().client(client)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .body(mapper.writeValueAsString(expectedResult))
                .build()
                .doExecute(context);

    }

    @SneakyThrows
    public static <T> void validateGet(final HttpClient client, final TestContext context, final String path, final T expectedResult) {
        http().client(client)
                .send()
                .get(path)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .build()
                .doExecute(context);

        http().client(client)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .body(mapper.writeValueAsString(expectedResult))
                .build()
                .doExecute(context);

    }

    @SneakyThrows
    public static <B, T> void validateDelete(final HttpClient client, final TestContext context, final String path, final B body, final T expectedResult) {
        http().client(client)
                .send()
                .delete(path)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(mapper.writeValueAsString(body))
                .build()
                .doExecute(context);

        http().client(client)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .body(mapper.writeValueAsString(expectedResult))
                .build()
                .doExecute(context);

    }

    @SneakyThrows
    public static <R, T> void validatePutList(final HttpClient client, final TestContext context, final String path, final R payload, final List<T> expectedResult) {
        http().client(client)
                .send()
                .put(path)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(mapper.writeValueAsString(payload))
                .build()
                .doExecute(context);

        http().client(client)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .body(mapper.writeValueAsString(expectedResult))
                .build()
                .doExecute(context);

    }

    @SneakyThrows
    public static <R, T> void validatePut(final HttpClient client, final TestContext context, final String path, final R payload, final T expectedResult) {
        http().client(client)
                .send()
                .put(path)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(mapper.writeValueAsString(payload))
                .build()
                .doExecute(context);

        http().client(client)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .body(mapper.writeValueAsString(expectedResult))
                .build()
                .doExecute(context);

    }
}
