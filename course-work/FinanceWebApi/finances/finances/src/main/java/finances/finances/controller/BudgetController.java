package finances.finances.controller;
import finances.finances.domain.budget.service.BudgetService;
import finances.finances.dtos.BudgetFilterRequest;
import finances.finances.dtos.BudgetRequest;
import finances.finances.dtos.BudgetResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@Tag(name = "Budgets", description = "Manage user budgets")
@SecurityRequirement(name = "BearerAuth")
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    @Operation(summary = "Get all budgets", description = "Returns a list of all budgets")
    @ApiResponse(responseCode = "200", description = "List of budgets returned successfully")
    public ResponseEntity<Page<BudgetResponse>> getAllBudgets(@ModelAttribute BudgetFilterRequest filter) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(budgetService.findAll(filter).get());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get budget by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Budget found",
            content = @Content(schema = @Schema(implementation = BudgetResponse.class))),
        @ApiResponse(responseCode = "404", description = "Budget not found", content = @Content)
    })
    public ResponseEntity<BudgetResponse> getBudgetById(
        @Parameter(description = "Budget ID", required = true) @PathVariable Integer id) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(budgetService.findById(id).get());
    }

    @PostMapping
    @Operation(summary = "Create a new budget")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Budget created",
            content = @Content(schema = @Schema(implementation = BudgetResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content)
    })
    public ResponseEntity<BudgetResponse> createBudget(@Valid @RequestBody BudgetRequest request) throws ExecutionException, InterruptedException {
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.create(request).get());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing budget")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Budget updated",
            content = @Content(schema = @Schema(implementation = BudgetResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
        @ApiResponse(responseCode = "404", description = "Budget not found", content = @Content)
    })
    public ResponseEntity<BudgetResponse> updateBudget(
        @Parameter(description = "Budget ID", required = true) @PathVariable Integer id,
        @Valid @RequestBody BudgetRequest request) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(budgetService.update(id, request).get());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a budget")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Budget deleted"),
        @ApiResponse(responseCode = "404", description = "Budget not found", content = @Content)
    })
    public ResponseEntity<Void> deleteBudget(
        @Parameter(description = "Budget ID", required = true) @PathVariable Integer id)
        throws ExecutionException, InterruptedException {
        budgetService.delete(id).get();
        return ResponseEntity.noContent().build();
    }
}