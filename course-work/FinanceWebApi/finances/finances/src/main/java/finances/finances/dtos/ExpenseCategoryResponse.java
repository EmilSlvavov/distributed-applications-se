package finances.finances.dtos;
import finances.finances.enums.ExpenseType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Expense category details returned by the API")
public class ExpenseCategoryResponse {

    @Schema(description = "Category ID")
    private Integer id;

    @Schema(description = "Type of expense this category tracks")
    private ExpenseType expenseType;

    @Schema(description = "Budget allocated for this category")
    private Double categoryBudget;

    @Schema(description = "ID of the owning user")
    private Integer userId;

    @Schema(description = "Timestamp when the category was created")
    private LocalDateTime createdAt;

    @Schema(description = "Total amount spent across all expenses in this category")
    private Double totalSpent;

    @Schema(description = "Percentage of category budget spent (0–100)")
    private Double spentPercent;
}