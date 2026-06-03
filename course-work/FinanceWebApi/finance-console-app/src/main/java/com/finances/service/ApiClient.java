package com.finances.service;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import java.io.IOException;
import java.lang.reflect.Type;
import com.finances.exception.ApiException;
import com.finances.exception.ProblemDetail;

public class ApiClient {
    private final String baseUrl;
    private final OkHttpClient httpClient;
    private final Gson gson;
    private String authToken;

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = new OkHttpClient();
        this.gson = createGsonWithJavaTimeSupport();
        this.authToken = null;
    }

    private Gson createGsonWithJavaTimeSupport() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Converters.registerAll(gsonBuilder);
        return gsonBuilder.create();
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    public void clearAuthToken() {
        this.authToken = null;
    }

    public String getAuthToken() {
        return authToken;
    }

    public <T> T post(String endpoint, Object body, Class<T> responseClass) throws IOException {
        String jsonBody = gson.toJson(body);
        RequestBody requestBody = RequestBody.create(jsonBody, MediaType.parse("application/json"));

        Request.Builder requestBuilder = new Request.Builder()
            .url(baseUrl + endpoint)
            .post(requestBody);

        if (authToken != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = requestBuilder.build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) { handleError(response); }
            String responseBody = response.body().string();
            return gson.fromJson(responseBody, responseClass);
        }
    }

    public <T> T get(String endpoint, Class<T> responseClass) throws IOException {
        Request.Builder requestBuilder = new Request.Builder()
            .url(baseUrl + endpoint)
            .get();

        if (authToken != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = requestBuilder.build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) { handleError(response); }
            String responseBody = response.body().string();
            return gson.fromJson(responseBody, responseClass);
        }
    }

    public <T> T getWithType(String endpoint, Type type) throws IOException {
        Request.Builder requestBuilder = new Request.Builder()
            .url(baseUrl + endpoint)
            .get();

        if (authToken != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = requestBuilder.build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) { handleError(response); }
            String responseBody = response.body().string();
            return gson.fromJson(responseBody, type);
        }
    }

    public <T> T put(String endpoint, Object body, Class<T> responseClass) throws IOException {
        String jsonBody = gson.toJson(body);
        RequestBody requestBody = RequestBody.create(jsonBody, MediaType.parse("application/json"));

        Request.Builder requestBuilder = new Request.Builder()
            .url(baseUrl + endpoint)
            .put(requestBody);

        if (authToken != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = requestBuilder.build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) { handleError(response); }
            String responseBody = response.body().string();
            return gson.fromJson(responseBody, responseClass);
        }
    }

    public <T> T patch(String endpoint, Object body, Class<T> responseClass) throws IOException {
        String jsonBody = gson.toJson(body);
        RequestBody requestBody = RequestBody.create(jsonBody, MediaType.parse("application/json"));

        Request.Builder requestBuilder = new Request.Builder()
            .url(baseUrl + endpoint)
            .patch(requestBody);

        if (authToken != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = requestBuilder.build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) { handleError(response); }
            String responseBody = response.body().string();
            return gson.fromJson(responseBody, responseClass);
        }
    }

    public void delete(String endpoint) throws IOException {
        Request.Builder requestBuilder = new Request.Builder()
            .url(baseUrl + endpoint)
            .delete();

        if (authToken != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = requestBuilder.build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) { handleError(response); }
        }
    }

    private void handleError(Response response) throws IOException {
        String body = response.body() != null ? response.body().string() : "";
        int status = response.code();
        try {
            ProblemDetail problem = gson.fromJson(body, ProblemDetail.class);
            if (problem != null && problem.getDetail() != null) {
                throw new ApiException(problem);
            }
        } catch (ApiException e) {
            throw e;
        } catch (Exception ignored) {}
        throw new ApiException(status, "API Error: " + status + " " + response.message());
    }
    public Gson getGson() {
        return gson;
    }
}
