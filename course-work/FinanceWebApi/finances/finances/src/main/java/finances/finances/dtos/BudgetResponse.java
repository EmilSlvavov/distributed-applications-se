package finances.finances.dtos;

import finances.finances.enums.CurrencyType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Budget details returned by the API")
public class BudgetResponse {

    @Schema(description = "Budget ID")
    private Integer id;

    @Schema(description = "Budget name")
    private String name;

    @Schema(description = "The initial total amount the budget was set to")
    private Double originalAmount;

    @Schema(description = "Total amount spent so far across all linked expenses")
    private Double spentAmount;

    @Schema(description = "Amount remaining (originalAmount - spentAmount)")
    private Double remainingAmount;

    @Schema(description = "Percentage of budget spent (0-100)")
    private Double spentPercent;

    @Schema(description = "True if spentAmount exceeds originalAmount")
    private Boolean overBudget;

    @Schema(description = "Warning message when spending is high or over budget, null otherwise")
    private String warning;

    @Schema(description = "Currency code")
    private CurrencyType currency;

    @Schema(description = "Whether this budget auto-renews")
    private Boolean isRecurring;

    @Schema(description = "Timestamp when the budget was created")
    private LocalDateTime createdAt;

    @Schema(description = "ID of the user this budget belongs to")
    private Integer userId;
}