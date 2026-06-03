package com.finances.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseRequest {
    private Integer expenseCategoryId;
    private Integer budgetId;
    private Double amount;
    private LocalDateTime expenseDate;
    private Boolean isRecurring;
    private String description;
}
