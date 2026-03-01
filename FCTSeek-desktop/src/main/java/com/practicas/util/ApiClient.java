package com.practicas.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import javafx.application.Platform;

/**
 * Cliente HTTP centralizado para comunicación con el backend REST.
 */
public class ApiClient {

    //private static final String BASE_URL = "http://IpDelBackEnd:8080/api";
    private static final String BASE_URL = "http://localhost:8080/api";
    private static final Gson GSON = new GsonBuilder().create();
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // ─── GET ────────────────────────────────────────────────────────────

    public static <T> T get(String endpoint, Class<T> clazz) throws ApiException {
        HttpResponse<String> resp = send(buildRequest(endpoint, "GET", null));
        return GSON.fromJson(resp.body(), clazz);
    }

    public static <T> List<T> getList(String endpoint, Class<T> clazz) throws ApiException {
        HttpResponse<String> resp = send(buildRequest(endpoint, "GET", null));
        Type type = TypeToken.getParameterized(List.class, clazz).getType();
        return GSON.fromJson(resp.body(), type);
    }

    // ─── POST ───────────────────────────────────────────────────────────

    public static <T> T post(String endpoint, Object body, Class<T> clazz) throws ApiException {
        HttpResponse<String> resp = send(buildRequest(endpoint, "POST", body));
        if (resp.body() == null || resp.body().isBlank()) return null;
        return GSON.fromJson(resp.body(), clazz);
    }

    public static String postRaw(String endpoint, Object body) throws ApiException {
        HttpResponse<String> resp = send(buildRequest(endpoint, "POST", body));
        return resp.body();
    }

    // ─── POST sin auth (login/register) ─────────────────────────────────

    public static String postPublic(String endpoint, Object body) throws ApiException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(body)))
                .timeout(Duration.ofSeconds(15))
                .build();
        HttpResponse<String> resp = sendRaw(request);
        return resp.body();
    }

    // ─── PUT ────────────────────────────────────────────────────────────

    public static <T> T put(String endpoint, Object body, Class<T> clazz) throws ApiException {
        HttpResponse<String> resp = send(buildRequest(endpoint, "PUT", body));
        if (resp.body() == null || resp.body().isBlank()) return null;
        return GSON.fromJson(resp.body(), clazz);
    }

    // ─── DELETE ─────────────────────────────────────────────────────────

    public static void delete(String endpoint) throws ApiException {
        send(buildRequest(endpoint, "DELETE", null));
    }

    // ─── Async helpers (ejecutan callback en JavaFX thread) ─────────────

    public static <T> void getAsync(String endpoint, Class<T> clazz, Consumer<T> onSuccess, Consumer<String> onError) {
        CompletableFuture.supplyAsync(() -> {
            try { return get(endpoint, clazz); }
            catch (Exception e) { Platform.runLater(() -> onError.accept(e.getMessage())); return null; }
        }).thenAccept(result -> { if (result != null) Platform.runLater(() -> onSuccess.accept(result)); });
    }

    public static <T> void getListAsync(String endpoint, Class<T> clazz, Consumer<List<T>> onSuccess, Consumer<String> onError) {
        CompletableFuture.supplyAsync(() -> {
            try { return getList(endpoint, clazz); }
            catch (Exception e) { Platform.runLater(() -> onError.accept(e.getMessage())); return null; }
        }).thenAccept(result -> { if (result != null) Platform.runLater(() -> onSuccess.accept(result)); });
    }

    // ─── Internos ───────────────────────────────────────────────────────

    private static HttpRequest buildRequest(String endpoint, String method, Object body) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(15));

        // Adjuntar JWT si hay sesión activa
        String token = Session.get().getToken();
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }

        switch (method) {
            case "POST" -> builder.POST(body != null
                    ? HttpRequest.BodyPublishers.ofString(GSON.toJson(body))
                    : HttpRequest.BodyPublishers.noBody());
            case "PUT" -> builder.PUT(body != null
                    ? HttpRequest.BodyPublishers.ofString(GSON.toJson(body))
                    : HttpRequest.BodyPublishers.noBody());
            case "DELETE" -> builder.DELETE();
            default -> builder.GET();
        }

        return builder.build();
    }

    private static HttpResponse<String> send(HttpRequest request) throws ApiException {
        return sendRaw(request);
    }

    private static HttpResponse<String> sendRaw(HttpRequest request) throws ApiException {
        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            int code = response.statusCode();

            if (code >= 200 && code < 300) {
                return response;
            }

            // Intentar extraer mensaje de error del JSON
            String errorMsg = "Error " + code;
            try {
                JsonElement el = JsonParser.parseString(response.body());
                if (el.isJsonObject()) {
                    var obj = el.getAsJsonObject();
                    if (obj.has("message")) errorMsg = obj.get("message").getAsString();
                    else if (obj.has("error")) errorMsg = obj.get("error").getAsString();
                }
            } catch (Exception ignored) {}

            throw new ApiException(errorMsg, code);
        } catch (ApiException ae) {
            throw ae;
        } catch (java.net.ConnectException e) {
            throw new ApiException("No se pudo conectar con el servidor. ¿Está el backend corriendo en " + BASE_URL + "?", 0);
        } catch (Exception e) {
            throw new ApiException("Error de red: " + e.getMessage(), 0);
        }
    }

    // ─── Gson público para parsear manualmente ──────────────────────────

    public static Gson gson() { return GSON; }

    // ─── Excepción personalizada ────────────────────────────────────────

    public static class ApiException extends Exception {
        private final int statusCode;

        public ApiException(String message, int statusCode) {
            super(message);
            this.statusCode = statusCode;
        }

        public int getStatusCode() { return statusCode; }
    }
}
