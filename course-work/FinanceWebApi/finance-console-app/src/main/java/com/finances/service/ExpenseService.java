package com.finances.service;

import com.finances.dto.request.ExpenseRequest;
import com.finances.dto.response.ExpenseResponse;
import com.finances.dto.response.PagedResponse;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

public class ExpenseService {
    private final ApiClient apiClient;

    public ExpenseService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ExpenseResponse createExpense(Integer expenseCategoryId, Integer budgetId, Double amount,
        LocalDateTime expenseDate, Boolean isRecurring, String description) {
        try {
            ExpenseRequest request = new ExpenseRequest(expenseCategoryId, budgetId, amount, expenseDate, isRecurring, description);
            return apiClient.post("/api/expenses", request, ExpenseResponse.class);
        } catch (IOException e) {
            System.err.println("Create expense failed: " + e.getMessage());
            return null;
        }
    }

    public PagedResponse<ExpenseResponse> getAllExpenses(int page, int pageSize) {
        try {
            String endpoint = String.format("/api/expenses?page=%d&size=%d&sortBy=expenseDate&sortDir=DESC",
                page, pageSize);
            Type type = new TypeToken<PagedResponse<ExpenseResponse>>(){}.getType();
            return apiClient.getWithType(endpoint, type);
        } catch (IOException e) {
            System.err.println("Get expenses failed: " + e.getMessage());
            return null;
        }
    }

    public PagedResponse<ExpenseResponse> searchExpenses(String categoryType, Double minAmount, Double maxAmount, int page, int pageSize) {
        try {
            StringBuilder endpoint = new StringBuilder(
                String.format("/api/expenses?page=%d&size=%d", page, pageSize));

            if (categoryType != null && !categoryType.isEmpty()) {
                endpoint.append("&categoryType=").append(categoryType);
            }
            if (minAmount != null) {
                endpoint.append("&minAmount=").append(minAmount);
            }
            if (maxAmount != null) {
                endpoint.append("&maxAmount=").append(maxAmount);
            }

            Type type = new TypeToken<PagedResponse<ExpenseResponse>>(){}.getType();
            return apiClient.getWithType(endpoint.toString(), type);
        } catch (IOException e) {
            System.err.println("Search expenses failed: " + e.getMessage());
            return null;
        }
    }

    public ExpenseResponse getExpenseById(Integer id) {
        try {
            return apiClient.get("/api/expenses/" + id, ExpenseResponse.class);
        } catch (IOException e) {
            System.err.println("Get expense failed: " + e.getMessage());
            return null;
        }
    }

    public ExpenseResponse updateExpense(Integer id, Integer expenseCategoryId, Integer budgetId,
        Double amount, LocalDateTime expenseDate, Boolean isRecurring, String description) {
        try {
            ExpenseRequest request = new ExpenseRequest(expenseCategoryId, budgetId, amount, expenseDate, isRecurring, description);
            return apiClient.put("/api/expenses/" + id, request, ExpenseResponse.class);
        } catch (IOException e) {
            System.err.println("Update expense failed: " + e.getMessage());
            return null;
        }
    }

    public boolean deleteExpense(Integer id) {
        try {
            apiClient.delete("/api/expenses/" + id);
            return true;
        } catch (IOException e) {
            System.err.println("Delete expense failed: " + e.getMessage());
            return false;
        }
    }
}