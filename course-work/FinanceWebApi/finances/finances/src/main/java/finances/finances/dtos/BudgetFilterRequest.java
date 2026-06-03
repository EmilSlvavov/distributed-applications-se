package finances.finances.dtos;

import finances.finances.enums.CurrencyType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Filter, pagination and sort parameters for budgets")
public class BudgetFilterRequest {

    @Schema(description = "Filter by currency")
    private CurrencyType currency;

    @Schema(description = "Filter by recurring flag")
    private Boolean isRecurring;

    @Schema(description = "Filter by minimum original amount")
    private Double minOriginalAmount;

    @Schema(description = "Filter by maximum original amount")
    private Double maxOriginalAmount;

    private int page = 0;
    private int size = 10;
    private String sortBy = "createdAt";
    private String sortDir = "desc";
}
