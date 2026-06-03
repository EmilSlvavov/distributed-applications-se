package finances.finances.dtos;
import finances.finances.enums.ExpenseType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Schema(description = "Payload for creating or updating an expense category")
public class ExpenseCategoryRequest {

    @NotNull(message = "Expense type is required")
    @Schema(description = "Type of expense this category tracks", example = "FOOD")
    private ExpenseType expenseType;

    @NotNull(message = "Category budget is required")
    @Positive(message = "Category budget must be positive")
    @Schema(description = "Budget allocated for this category", example = "800.00")
    private Double categoryBudget;

    @NotNull(message = "User ID is required")
    @Schema(description = "ID of the owning user", example = "1")
    private Integer userId;
}