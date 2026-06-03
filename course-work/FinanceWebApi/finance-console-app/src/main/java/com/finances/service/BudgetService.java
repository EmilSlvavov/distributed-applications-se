package com.finances.service;

import com.finances.dto.request.BudgetRequest;
import com.finances.dto.response.BudgetResponse;
import com.finances.dto.response.PagedResponse;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;

public class BudgetService {
    private final ApiClient apiClient;

    public BudgetService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public BudgetResponse createBudget(String name, Double originalAmount, String currency, Boolean isRecurring) {
        try {
            BudgetRequest request = new BudgetRequest(name, originalAmount, currency, isRecurring);
            return apiClient.post("/api/budgets", request, BudgetResponse.class);
        } catch (IOException e) {
            System.err.println("Create budget failed: " + e.getMessage());
            return null;
        }
    }

    public PagedResponse<BudgetResponse> getAllBudgets(int page, int pageSize) {
        try {
            // Fixed: size and sortDir match backend BudgetFilterRequest field names
            String endpoint = String.format("/api/budgets?page=%d&size=%d&sortBy=createdAt&sortDir=desc",
                page, pageSize);
            Type type = new TypeToken<PagedResponse<BudgetResponse>>(){}.getType();
            return apiClient.getWithType(endpoint, type);
        } catch (IOException e) {
            System.err.println("Get budgets failed: " + e.getMessage());
            return null;
        }
    }

    public PagedResponse<BudgetResponse> searchBudgets(
        String currency, Boolean isRecurring,
        Double minOriginalAmount, Double maxOriginalAmount,
        String sortBy, String sortDir,
        int page, int pageSize) {
        try {
            StringBuilder endpoint = new StringBuilder(
                String.format("/api/budgets?page=%d&size=%d", page, pageSize));

            if (currency != null && !currency.isEmpty())
                endpoint.append("&currency=").append(currency.toUpperCase());
            if (isRecurring != null)
                endpoint.append("&isRecurring=").append(isRecurring);
            if (minOriginalAmount != null)
                endpoint.append("&minOriginalAmount=").append(minOriginalAmount);
            if (maxOriginalAmount != null)
                endpoint.append("&maxOriginalAmount=").append(maxOriginalAmount);
            if (sortBy != null && !sortBy.isEmpty())
                endpoint.append("&sortBy=").append(sortBy);
            if (sortDir != null && !sortDir.isEmpty())
                endpoint.append("&sortDir=").append(sortDir);

            Type type = new TypeToken<PagedResponse<BudgetResponse>>(){}.getType();
            return apiClient.getWithType(endpoint.toString(), type);
        } catch (IOException e) {
            System.err.println("Search budgets failed: " + e.getMessage());
            return null;
        }
    }

    public BudgetResponse getBudgetById(Integer id) {
        try {
            return apiClient.get("/api/budgets/" + id, BudgetResponse.class);
        } catch (IOException e) {
            System.err.println("Get budget failed: " + e.getMessage());
            return null;
        }
    }

    public BudgetResponse updateBudget(Integer id, String name, Double originalAmount, String currency, Boolean isRecurring) {
        try {
            BudgetRequest request = new BudgetRequest(name, originalAmount, currency, isRecurring);
            return apiClient.put("/api/budgets/" + id, request, BudgetResponse.class);
        } catch (IOException e) {
            System.err.println("Update budget failed: " + e.getMessage());
            return null;
        }
    }

    public boolean deleteBudget(Integer id) {
        try {
            apiClient.delete("/api/budgets/" + id);
            return true;
        } catch (IOException e) {
            System.err.println("Delete budget failed: " + e.getMessage());
            return false;
        }
    }
}