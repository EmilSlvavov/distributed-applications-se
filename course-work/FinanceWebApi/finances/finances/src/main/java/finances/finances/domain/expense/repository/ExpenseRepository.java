package finances.finances.domain.expense.repository;

import finances.finances.domain.expense.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ExpenseRepository extends JpaRepository<Expense, Integer>, JpaSpecificationExecutor<Expense> {
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.budget.id = :budgetId")
    Double sumAmountByBudgetId(@Param("budgetId") Integer budgetId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Expense e WHERE e.id = :id")
    void deleteByIdDirect(@Param("id") Integer id);
}
