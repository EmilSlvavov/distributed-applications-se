package com.finances.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetRequest {
    private String name;
    private Double originalAmount;
    private String currency; // CAD, CNY, EUR, GBP, JPY, MXN, NOK, NZD, RUB, TRY, USD
    private Boolean isRecurring;
}
