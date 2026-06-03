package com.finances.service;

import com.finances.dto.request.ChangePasswordRequest;
import com.finances.dto.request.UserRequest;
import com.finances.dto.response.UserResponse;
import com.finances.dto.response.PagedResponse;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;

public class UserService {
    private final ApiClient apiClient;

    public UserService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public UserResponse createUser(String username, String password, String role) {
        try {
            UserRequest request = new UserRequest(username, password, role);
            return apiClient.post("/api/users", request, UserResponse.class);
        } catch (IOException e) {
            System.err.println("Create user failed: " + e.getMessage());
            return null;
        }
    }

    public PagedResponse<UserResponse> getAllUsers(int page, int pageSize) {
        try {
            // Fixed: size matches backend UserFilterRequest field name
            String endpoint = String.format("/api/users?page=%d&size=%d&sortBy=createdAt&sortDir=desc",
                page, pageSize);
            Type type = new TypeToken<PagedResponse<UserResponse>>(){}.getType();
            return apiClient.getWithType(endpoint, type);
        } catch (IOException e) {
            System.err.println("Get users failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Search users with optional filters, sorting and pagination.
     * All parameters are optional — pass null to skip a filter.
     * sortBy options: username, role, createdAt
     * sortDir options: asc, desc
     */
    public PagedResponse<UserResponse> searchUsers(
        String username, String role, Boolean isActive,
        String sortBy, String sortDir,
        int page, int pageSize) {
        try {
            StringBuilder endpoint = new StringBuilder(
                String.format("/api/users?page=%d&size=%d", page, pageSize));

            if (username != null && !username.isEmpty())
                endpoint.append("&username=").append(username);
            if (role != null && !role.isEmpty())
                endpoint.append("&role=").append(role.toUpperCase());
            if (isActive != null)
                endpoint.append("&isActive=").append(isActive);
            if (sortBy != null && !sortBy.isEmpty())
                endpoint.append("&sortBy=").append(sortBy);
            if (sortDir != null && !sortDir.isEmpty())
                endpoint.append("&sortDir=").append(sortDir);

            Type type = new TypeToken<PagedResponse<UserResponse>>(){}.getType();
            return apiClient.getWithType(endpoint.toString(), type);
        } catch (IOException e) {
            System.err.println("Search users failed: " + e.getMessage());
            return null;
        }
    }

    public UserResponse getCurrentUser() {
        try {
            return apiClient.get("/api/auth/me", UserResponse.class);
        } catch (IOException e) {
            System.err.println("Get current user failed: " + e.getMessage());
            return null;
        }
    }

    public boolean changePassword(Integer userId, String currentPassword, String newPassword) {
        try {
            ChangePasswordRequest request = new ChangePasswordRequest(currentPassword, newPassword);
            apiClient.patch("/api/users/" + userId + "/change-password", request, UserResponse.class);
            return true;
        } catch (IOException e) {
            System.err.println("Change password failed: " + e.getMessage());
            return false;
        }
    }

    public UserResponse getUserById(Integer id) {
        try {
            return apiClient.get("/api/users/" + id, UserResponse.class);
        } catch (IOException e) {
            System.err.println("Get user failed: " + e.getMessage());
            return null;
        }
    }

    public UserResponse updateUser(Integer id, String username, String password, String role) {
        try {
            UserRequest request = new UserRequest(username, password, role);
            return apiClient.put("/api/users/" + id, request, UserResponse.class);
        } catch (IOException e) {
            System.err.println("Update user failed: " + e.getMessage());
            return null;
        }
    }

    public boolean deleteUser(Integer id) {
        try {
            apiClient.delete("/api/users/" + id);
            return true;
        } catch (IOException e) {
            System.err.println("Delete user failed: " + e.getMessage());
            return false;
        }
    }
}