package org.fermented.dairy.caches.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import okhttp3.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RestUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final OkHttpClient client = new OkHttpClient().newBuilder()
            .build();

    @SneakyThrows
    public static <T> void validateGetList(final String applicationUrl, final List<T> expectedResult, final Class<T> expectedType, final String... pathParts) {

        String callUrl = applicationUrl + "/" + String.join("/", pathParts);
        Request request = new Request.Builder()
                .url(callUrl)
                .get()
                .addHeader("Accept", "application/json")
                .build();
        Response response = client.newCall(request).execute();

        assertTrue(response.isSuccessful(), "Successful response expected from %s".formatted(callUrl));

        List<T> result = mapper.readerForListOf(expectedType).readValue(response.body().string());
        assertEquals(expectedResult, result, "result is not expected");

    }

    @SneakyThrows
    public static <T> void validateGet(final String applicationUrl, final T expectedResult, final Class<T> expectedType, final String... pathParts) {

        String callUrl = applicationUrl + "/" + String.join("/", pathParts);
        Request request = new Request.Builder()
                .url(callUrl)
                .get()
                .addHeader("Accept", "application/json")
                .build();
        Response response = client.newCall(request).execute();

        assertTrue(response.isSuccessful(), "Successful response expected from %s".formatted(callUrl));

        T result = mapper.readValue(response.body().string(), expectedType);
        assertEquals(expectedResult, result, "result is not expected");

    }

    @SneakyThrows
    public static <B, T> void validateDelete(final String applicationUrl, final B body, final T expectedResult, final Class<T> expectedType, final String... pathParts) {

        String callUrl = applicationUrl + "/" + String.join("/", pathParts);
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(mapper.writeValueAsString(body), mediaType);
        Request request = new Request.Builder()
                .url(callUrl)
                .method("DELETE", requestBody)
                .addHeader("Accept", "application/json")
                .build();
        Response response = client.newCall(request).execute();


        assertTrue(response.isSuccessful(), "Successful response expected from %s".formatted(callUrl));

        T result = mapper.readValue(response.body().string(), expectedType);
        assertEquals(expectedResult, result, "result is not expected");

    }


    @SneakyThrows
    public static <R, T> void validatePut(final String applicationUrl, final R body, final T expectedResult, Class<T> expectedType, final String... pathParts) {
        String callUrl = applicationUrl + "/" + String.join("/", pathParts);
        MediaType mediaType = MediaType.parse("application/json");
        String payload = mapper.writeValueAsString(body);
        RequestBody requestBody = RequestBody.create(payload, mediaType);
        Request request = new Request.Builder()
                .url(callUrl)
                .method("PUT", requestBody)
                .addHeader("Accept", "application/json")
                .build();
        Response response = client.newCall(request).execute();


        assertTrue(response.isSuccessful(), "Successful response expected from %s, code: %s, payload: %s".formatted(
                callUrl,
                response.code(),
                payload));

        T result = mapper.readValue(response.body().string(), expectedType);
        assertEquals(expectedResult, result, "result is not expected");

    }
}
