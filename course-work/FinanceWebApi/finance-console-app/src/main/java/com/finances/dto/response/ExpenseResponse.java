package com.finances.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponse {
    private Integer id;
    private Integer expenseCategoryId;
    private String expenseCategoryType;
    private Integer budgetId;
    private Double amount;
    private Boolean isRecurring;
    private LocalDateTime expenseDate;
    private String description;
}
