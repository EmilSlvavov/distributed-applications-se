package com.finances.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseCategoryRequest {
    private String expenseType; // HOUSING, TRANSPORTATION, FOOD, HEALTHCARE, DEBT, ENTERTAINMENT, CLOTIHING_AND_PERSONAL_ITEMS, TRAVEL, PETS, SAVINGS
    private Double categoryBudget;
    private Integer userId;
}
