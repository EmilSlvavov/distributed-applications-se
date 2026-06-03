package finances.finances.controller;
import finances.finances.domain.ExpenseCategories.service.ExpenseCategoriesService;
import finances.finances.dtos.ExpenseCategoryFilterRequest;
import finances.finances.dtos.ExpenseCategoryRequest;
import finances.finances.dtos.ExpenseCategoryResponse;
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
@RequestMapping("/api/expense-categories")
@RequiredArgsConstructor
@Tag(name = "Expense Categories", description = "Manage expense categories per user")
@SecurityRequirement(name = "BearerAuth")
public class ExpenseCategoryController {

    private final ExpenseCategoriesService expenseCategoriesService;

    @GetMapping
    @Operation(summary = "Get all expense categories", description = "Returns a list of all expense categories")
    @ApiResponse(responseCode = "200", description = "List of expense categories returned successfully")
    public ResponseEntity<Page<ExpenseCategoryResponse>> getAllCategories(@ModelAttribute ExpenseCategoryFilterRequest filter) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(expenseCategoriesService.findAll(filter).get());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get expense category by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Expense category found",
            content = @Content(schema = @Schema(implementation = ExpenseCategoryResponse.class))),
        @ApiResponse(responseCode = "404", description = "Expense category not found", content = @Content)
    })
    public ResponseEntity<ExpenseCategoryResponse> getCategoryById(
        @Parameter(description = "Expense category ID", required = true) @PathVariable Integer id) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(expenseCategoriesService.findById(id).get());
    }

    @PostMapping
    @Operation(summary = "Create a new expense category")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Expense category created",
            content = @Content(schema = @Schema(implementation = ExpenseCategoryResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
        @ApiResponse(responseCode = "404", description = "Referenced user not found", content = @Content)
    })
    public ResponseEntity<ExpenseCategoryResponse> createCategory(
        @Valid @RequestBody ExpenseCategoryRequest request) throws ExecutionException, InterruptedException {
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseCategoriesService.create(request).get());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing expense category")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Expense category updated",
            content = @Content(schema = @Schema(implementation = ExpenseCategoryResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
        @ApiResponse(responseCode = "404", description = "Expense category or user not found", content = @Content)
    })
    public ResponseEntity<ExpenseCategoryResponse> updateCategory(
        @Parameter(description = "Expense category ID", required = true) @PathVariable Integer id,
        @Valid @RequestBody ExpenseCategoryRequest request) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(expenseCategoriesService.update(id, request).get());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an expense category")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Expense category deleted"),
        @ApiResponse(responseCode = "404", description = "Expense category not found", content = @Content)
    })
    public ResponseEntity<Void> deleteCategory(
        @Parameter(description = "Expense category ID", required = true) @PathVariable Integer id)
        throws ExecutionException, InterruptedException {
        expenseCategoriesService.delete(id).get();
        return ResponseEntity.noContent().build();
    }
}