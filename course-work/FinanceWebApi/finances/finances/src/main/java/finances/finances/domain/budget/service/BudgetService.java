package finances.finances.domain.budget.service;
import finances.finances.domain.expense.repository.ExpenseRepository;
import finances.finances.dtos.BudgetFilterRequest;
import finances.finances.dtos.BudgetRequest;
import finances.finances.dtos.BudgetResponse;
import finances.finances.domain.budget.entity.Budget;
import finances.finances.domain.budget.repository.BudgetRepository;
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
import finances.finances.domain.user.entity.User;
import finances.finances.security.SecurityUtils;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final SecurityUtils securityUtils;

    @Async("taskExecutor")
    public CompletableFuture<BudgetResponse> create(BudgetRequest request) {
        User currentUser = securityUtils.getCurrentUser();
        Budget budget = new Budget();
        applyRequest(budget, request);
        budget.setUser(currentUser);
        budget.setCreatedAt(LocalDateTime.now());
        return CompletableFuture.completedFuture(toResponse(budgetRepository.save(budget)));
    }

    @Async("taskExecutor")
    public CompletableFuture<Page<BudgetResponse>> findAll(BudgetFilterRequest filter) {
        Integer userId = securityUtils.currentUserIsAdmin()
            ? null
            : securityUtils.getCurrentUser().getId();

        Specification<Budget> spec = Specification.allOf(
            BaseSpecification.joinEqual("user", "id", userId),
            BaseSpecification.equal("currency", filter.getCurrency()),
            BaseSpecification.equal("isRecurring", filter.getIsRecurring()),
            BaseSpecification.between("value", filter.getMinOriginalAmount(), filter.getMaxOriginalAmount())
        );

        Sort sort = filter.getSortDir().equalsIgnoreCase("asc")
            ? Sort.by(filter.getSortBy()).ascending()
            : Sort.by(filter.getSortBy()).descending();

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);

        return CompletableFuture.completedFuture(
            budgetRepository.findAll(spec, pageable).map(this::toResponse)
        );
    }

    @Async("taskExecutor")
    public CompletableFuture<BudgetResponse> findById(Integer id) {
        Budget budget = getOrThrow(id);
        securityUtils.assertOwnerOrAdmin(budget.getUser().getId());
        return CompletableFuture.completedFuture(toResponse(budget));
    }

    @Async("taskExecutor")
    public CompletableFuture<BudgetResponse> update(Integer id, BudgetRequest request) {
        Budget budget = getOrThrow(id);
        securityUtils.assertOwnerOrAdmin(budget.getUser().getId());
        applyRequest(budget, request);
        return CompletableFuture.completedFuture(toResponse(budgetRepository.save(budget)));
    }

    @Async("taskExecutor")
    public CompletableFuture<Void> delete(Integer id) {
        Budget budget = getOrThrow(id);
        securityUtils.assertOwnerOrAdmin(budget.getUser().getId());
        budgetRepository.deleteByIdDirect(id);
        return CompletableFuture.completedFuture(null);
    }

    private void applyRequest(Budget budget, BudgetRequest request) {
        budget.setName(request.getName());
        budget.setValue(request.getOriginalAmount()); // DTO uses originalAmount, entity stores as value
        budget.setCurrency(request.getCurrency());
        budget.setIsRecurring(request.getIsRecurring());
    }

    private Budget getOrThrow(Integer id) {
        return budgetRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Budget not found with id: " + id));
    }

    private BudgetResponse toResponse(Budget budget) {
        BudgetResponse response = new BudgetResponse();
        response.setId(budget.getId());
        response.setName(budget.getName());
        response.setOriginalAmount(budget.getValue());
        response.setCurrency(budget.getCurrency());
        response.setIsRecurring(budget.getIsRecurring());
        response.setCreatedAt(budget.getCreatedAt());
        response.setUserId(budget.getUser().getId());

        Double spentAmount = expenseRepository.sumAmountByBudgetId(budget.getId());
        double remaining = budget.getValue() - spentAmount;
        double percent = budget.getValue() > 0 ? (spentAmount / budget.getValue()) * 100.0 : 0.0;
        boolean overBudget = spentAmount > budget.getValue();

        response.setSpentAmount(spentAmount);
        response.setRemainingAmount(remaining);
        response.setSpentPercent(Math.round(percent * 100.0) / 100.0);
        response.setOverBudget(overBudget);

        if (overBudget) {
            response.setWarning(String.format(
                "Budget exceeded by %.2f %s",
                Math.abs(remaining), budget.getCurrency().name()));
        } else if (percent >= 90) {
            response.setWarning(String.format(
                "Warning: %.1f%% of budget used. Only %.2f %s remaining",
                percent, remaining, budget.getCurrency().name()));
        } else if (percent >= 75) {
            response.setWarning(String.format(
                "%.1f%% of budget used. %.2f %s remaining",
                percent, remaining, budget.getCurrency().name()));
        } else {
            response.setWarning(null);
        }

        return response;
    }
}
