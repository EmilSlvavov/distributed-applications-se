package com.finances.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseCategoryResponse {
    private Integer id;
    private String expenseType;
    private Double categoryBudget;
    private Integer userId;
    private LocalDateTime createdAt;
    private Double totalSpent;
    private Double spentPercent;
}
