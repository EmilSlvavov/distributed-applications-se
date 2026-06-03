package finances.finances.dtos;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Payload for creating or updating an expense")
public class ExpenseRequest {

    @NotNull(message = "Category ID is required")
    @Schema(description = "ID of the expense category this expense belongs to", example = "1")
    private Integer expenseCategoryId;

    @NotNull(message = "Budget ID is required")
    @Schema(description = "ID of the budget this expense is deducted from", example = "2")
    private Integer budgetId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Schema(description = "Expense amount", example = "45.99")
    private Double amount;

    @NotNull(message = "Expense date is required")
    @Schema(description = "Date and time the expense occurred", example = "2024-06-01T12:30:00")
    private LocalDateTime expenseDate;

    @Schema(description = "Whether this is a recurring expense", example = "false")
    private Boolean isRecurring;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    @Schema(description = "Optional description", example = "Weekly grocery run")
    private String description;
}