package finances.finances.dtos;

import finances.finances.enums.ExpenseType;
import lombok.Data;

@Data
public class ExpenseCategoryFilterRequest {
    private ExpenseType expenseType;
    private Integer userId;
    private Double minBudget;
    private Double maxBudget;

    private int page = 0;
    private int size = 10;
    private String sortBy = "createdAt";
    private String sortDir = "desc";
}