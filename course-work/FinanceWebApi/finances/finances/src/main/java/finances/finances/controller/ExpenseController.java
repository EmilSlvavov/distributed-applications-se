package finances.finances.controller;
import finances.finances.domain.expense.service.ExpenseService;
import finances.finances.dtos.ExpenseFilterRequest;
import finances.finances.dtos.ExpenseRequest;
import finances.finances.dtos.ExpenseResponse;
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
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
@Tag(name = "Expenses", description = "Manage individual expenses")
@SecurityRequirement(name = "BearerAuth")
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping
    @Operation(summary = "Get all expenses", description = "Returns a list of all expenses")
    @ApiResponse(responseCode = "200", description = "List of expenses returned successfully")
    public ResponseEntity<Page<ExpenseResponse>> getAllExpenses(@ModelAttribute ExpenseFilterRequest filter) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(expenseService.findAll(filter).get());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get expense by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Expense found",
            content = @Content(schema = @Schema(implementation = ExpenseResponse.class))),
        @ApiResponse(responseCode = "404", description = "Expense not found", content = @Content)
    })
    public ResponseEntity<ExpenseResponse> getExpenseById(
        @Parameter(description = "Expense ID", required = true) @PathVariable Integer id) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(expenseService.findById(id).get());
    }

    @PostMapping
    @Operation(summary = "Create a new expense")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Expense created",
            content = @Content(schema = @Schema(implementation = ExpenseResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
        @ApiResponse(responseCode = "404", description = "Referenced expense category not found", content = @Content)
    })
    public ResponseEntity<ExpenseResponse> createExpense(@Valid @RequestBody ExpenseRequest request) throws ExecutionException, InterruptedException {
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseService.create(request).get());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing expense")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Expense updated",
            content = @Content(schema = @Schema(implementation = ExpenseResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
        @ApiResponse(responseCode = "404", description = "Expense or expense category not found", content = @Content)
    })
    public ResponseEntity<ExpenseResponse> updateExpense(
        @Parameter(description = "Expense ID", required = true) @PathVariable Integer id,
        @Valid @RequestBody ExpenseRequest request) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(expenseService.update(id, request).get());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an expense")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Expense deleted"),
        @ApiResponse(responseCode = "404", description = "Expense not found", content = @Content)
    })
    public ResponseEntity<Void> deleteExpense(
        @Parameter(description = "Expense ID", required = true) @PathVariable Integer id)
        throws ExecutionException, InterruptedException {
        expenseService.delete(id).get();
        return ResponseEntity.noContent().build();
    }
}