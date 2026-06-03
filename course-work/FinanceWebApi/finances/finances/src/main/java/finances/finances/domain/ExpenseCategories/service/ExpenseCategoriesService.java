package finances.finances.domain.ExpenseCategories.service;

import finances.finances.domain.ExpenseCategories.entity.ExpenseCategory;
import finances.finances.domain.ExpenseCategories.repository.ExpenseCategoriesRepository;
import finances.finances.domain.expense.repository.ExpenseRepository;
import finances.finances.domain.user.entity.User;
import finances.finances.domain.user.repository.UserRepository;
import finances.finances.dtos.ExpenseCategoryFilterRequest;
import finances.finances.dtos.ExpenseCategoryRequest;
import finances.finances.dtos.ExpenseCategoryResponse;
import finances.finances.security.SecurityUtils;
import finances.finances.specifications.BaseSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ExpenseCategoriesService {

    private final ExpenseCategoriesRepository expenseCategoryRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final ExpenseRepository expenseRepository;

    @Async("taskExecutor")
    public CompletableFuture<ExpenseCategoryResponse> create(ExpenseCategoryRequest request) {
        User targetUser;
        if (securityUtils.currentUserIsAdmin() && request.getUserId() != null) {
            targetUser = getUserOrThrow(request.getUserId());
        } else {
            targetUser = securityUtils.getCurrentUser();
        }

        ExpenseCategory category = new ExpenseCategory();
        applyRequest(category, request, targetUser);
        category.setCreatedAt(LocalDateTime.now());

        return CompletableFuture.completedFuture(
                toResponse(expenseCategoryRepository.save(category))
        );
    }

    @Async("taskExecutor")
    public CompletableFuture<Page<ExpenseCategoryResponse>> findAll(ExpenseCategoryFilterRequest filter) {
        Integer userId = securityUtils.currentUserIsAdmin()
                ? filter.getUserId()
                : securityUtils.getCurrentUser().getId();

        Specification<ExpenseCategory> spec = Specification.allOf(
                BaseSpecification.equal("expenseType", filter.getExpenseType()),
                BaseSpecification.joinEqual("user", "id", userId),
                BaseSpecification.between("categoryBudget", filter.getMinBudget(), filter.getMaxBudget())
        );

        Sort sort = filter.getSortDir().equalsIgnoreCase("asc")
                ? Sort.by(filter.getSortBy()).ascending()
                : Sort.by(filter.getSortBy()).descending();

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);

        return CompletableFuture.completedFuture(
                expenseCategoryRepository.findAll(spec, pageable).map(this::toResponse)
        );
    }

    @Async("taskExecutor")
    public CompletableFuture<ExpenseCategoryResponse> findById(Integer id) {
        ExpenseCategory category = getOrThrow(id);
        securityUtils.assertOwnerOrAdmin(category.getUser().getId());
        return CompletableFuture.completedFuture(toResponse(category));
    }

    @Async("taskExecutor")
    public CompletableFuture<ExpenseCategoryResponse> update(Integer id, ExpenseCategoryRequest request) {
        ExpenseCategory category = getOrThrow(id);
        securityUtils.assertOwnerOrAdmin(category.getUser().getId());

        User targetUser;
        if (securityUtils.currentUserIsAdmin() && request.getUserId() != null) {
            targetUser = getUserOrThrow(request.getUserId());
        } else {
            targetUser = category.getUser();
        }

        applyRequest(category, request, targetUser);
        return CompletableFuture.completedFuture(
                toResponse(expenseCategoryRepository.save(category))
        );
    }

    @Async("taskExecutor")
    public CompletableFuture<Void> delete(Integer id) {
        ExpenseCategory category = getOrThrow(id);
        securityUtils.assertOwnerOrAdmin(category.getUser().getId());

        // Delete all expenses in this category first
        // (bypassing cascade since we use direct JPQL queries)
        category.getExpenses()
            .forEach(expense -> expenseRepository.deleteByIdDirect(expense.getId()));

        expenseCategoryRepository.deleteByIdDirect(id);
        return CompletableFuture.completedFuture(null);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void applyRequest(ExpenseCategory category, ExpenseCategoryRequest request, User user) {
        category.setExpenseType(request.getExpenseType());
        category.setCategoryBudget(request.getCategoryBudget());
        category.setUser(user);
    }

    private ExpenseCategory getOrThrow(Integer id) {
        return expenseCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Expense category not found with id: " + id));
    }

    private User getUserOrThrow(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with id: " + userId));
    }

    private ExpenseCategoryResponse toResponse(ExpenseCategory category) {
        ExpenseCategoryResponse response = new ExpenseCategoryResponse();
        response.setId(category.getId());
        response.setExpenseType(category.getExpenseType());
        response.setCategoryBudget(category.getCategoryBudget());
        response.setUserId(category.getUser().getId());
        response.setCreatedAt(category.getCreatedAt());

        if (category.getExpenses() != null) {
            double totalSpent = category.getExpenses().stream()
                    .mapToDouble(e -> e.getAmount() == null ? 0.0 : e.getAmount())
                    .sum();
            response.setTotalSpent(totalSpent);

            if (category.getCategoryBudget() != null && category.getCategoryBudget() > 0) {
                response.setSpentPercent((totalSpent / category.getCategoryBudget()) * 100.0);
            }
        }

        return response;
    }
}