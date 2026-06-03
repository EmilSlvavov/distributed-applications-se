package finances.finances.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Filter, pagination and sort parameters for expenses")
public class ExpenseFilterRequest {

    @Schema(description = "Filter by category ID")
    private Integer categoryId;

    @Schema(description = "Filter by budget ID")
    private Integer budgetId;

    @Schema(description = "Filter by recurring flag")
    private Boolean isRecurring;

    @Schema(description = "Filter by minimum amount")
    private Double minAmount;

    @Schema(description = "Filter by maximum amount")
    private Double maxAmount;

    @Schema(description = "Filter from this date")
    private LocalDateTime dateFrom;

    @Schema(description = "Filter to this date")
    private LocalDateTime dateTo;

    @Schema(description = "Search in description")
    private String description;

    private int page = 0;
    private int size = 10;
    private String sortBy = "expenseDate";
    private String sortDir = "desc";
}