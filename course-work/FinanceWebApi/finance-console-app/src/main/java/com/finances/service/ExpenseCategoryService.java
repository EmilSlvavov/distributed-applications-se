package com.finances.service;

import com.finances.dto.request.ExpenseCategoryRequest;
import com.finances.dto.response.ExpenseCategoryResponse;
import com.finances.dto.response.PagedResponse;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;

public class ExpenseCategoryService {
    private final ApiClient apiClient;

    public ExpenseCategoryService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ExpenseCategoryResponse createCategory(String expenseType, Double categoryBudget, Integer userId) {
        try {
            ExpenseCategoryRequest request = new ExpenseCategoryRequest(expenseType, categoryBudget, userId);
            return apiClient.post("/api/expense-categories", request, ExpenseCategoryResponse.class);
        } catch (IOException e) {
            System.err.println("Create category failed: " + e.getMessage());
            return null;
        }
    }

    public PagedResponse<ExpenseCategoryResponse> getAllCategories(int page, int pageSize) {
        try {
            String endpoint = String.format("/api/expense-categories?page=%d&size=%d", page, pageSize);
            Type type = new TypeToken<PagedResponse<ExpenseCategoryResponse>>(){}.getType();
            return apiClient.getWithType(endpoint, type);
        } catch (IOException e) {
            System.err.println("Get categories failed: " + e.getMessage());
            return null;
        }
    }

    public PagedResponse<ExpenseCategoryResponse> searchCategories(
        String expenseType,
        Double minBudget, Double maxBudget,
        String sortBy, String sortDir,
        int page, int pageSize) {
        try {
            StringBuilder endpoint = new StringBuilder(
                String.format("/api/expense-categories?page=%d&size=%d", page, pageSize));

            if (expenseType != null && !expenseType.isEmpty())
                endpoint.append("&expenseType=").append(expenseType.toUpperCase());
            if (minBudget != null)
                endpoint.append("&minBudget=").append(minBudget);
            if (maxBudget != null)
                endpoint.append("&maxBudget=").append(maxBudget);
            if (sortBy != null && !sortBy.isEmpty())
                endpoint.append("&sortBy=").append(sortBy);
            if (sortDir != null && !sortDir.isEmpty())
                endpoint.append("&sortDir=").append(sortDir);

            Type type = new TypeToken<PagedResponse<ExpenseCategoryResponse>>(){}.getType();
            return apiClient.getWithType(endpoint.toString(), type);
        } catch (IOException e) {
            System.err.println("Search categories failed: " + e.getMessage());
            return null;
        }
    }

    public ExpenseCategoryResponse getCategoryById(Integer id) {
        try {
            return apiClient.get("/api/expense-categories/" + id, ExpenseCategoryResponse.class);
        } catch (IOException e) {
            System.err.println("Get category failed: " + e.getMessage());
            return null;
        }
    }

    public ExpenseCategoryResponse updateCategory(Integer id, String expenseType,
        Double categoryBudget, Integer userId) {
        try {
            ExpenseCategoryRequest request = new ExpenseCategoryRequest(expenseType, categoryBudget, userId);
            return apiClient.put("/api/expense-categories/" + id, request, ExpenseCategoryResponse.class);
        } catch (IOException e) {
            System.err.println("Update category failed: " + e.getMessage());
            return null;
        }
    }

    public boolean deleteCategory(Integer id) {
        try {
            apiClient.delete("/api/expense-categories/" + id);
            return true;
        } catch (IOException e) {
            System.err.println("Delete category failed: " + e.getMessage());
            return false;
        }
    }
}