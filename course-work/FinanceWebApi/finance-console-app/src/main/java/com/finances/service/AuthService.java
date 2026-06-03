package com.finances.service;

import com.finances.dto.request.AuthRequest;
import com.finances.dto.response.AuthResponse;
import java.io.IOException;

public class AuthService {
    private final ApiClient apiClient;
    private AuthResponse currentSession;

    public AuthService(ApiClient apiClient) {
        this.apiClient = apiClient;
        restoreSession();
    }

    public boolean login(String username, String password) {
        try {
            AuthRequest request = new AuthRequest(username, password);
            AuthResponse response = apiClient.post("/api/auth/login", request, AuthResponse.class);

            if (response != null && response.getToken() != null) {
                currentSession = response;
                apiClient.setAuthToken(response.getToken());
                SessionManager.saveToken(response.getToken());
                SessionManager.saveUserInfo(username);
                return true;
            }
            return false;
        } catch (IOException e) {
            System.err.println("Login failed: " + e.getMessage());
            return false;
        }
    }

    public void logout() {
        currentSession = null;
        apiClient.clearAuthToken();
        SessionManager.clearSession();
    }

    public boolean isLoggedIn() {
        return currentSession != null || SessionManager.hasValidSession();
    }

    private void restoreSession() {
        String token = SessionManager.loadToken();
        if (token != null && !token.isEmpty()) {
            apiClient.setAuthToken(token);
            currentSession = new AuthResponse();
            currentSession.setToken(token);
        }
    }

    public String getCurrentUsername() {
        return SessionManager.loadUserInfo();
    }
}