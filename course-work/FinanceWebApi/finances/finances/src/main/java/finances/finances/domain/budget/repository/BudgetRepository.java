package finances.finances.domain.budget.repository;

import finances.finances.domain.budget.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface BudgetRepository extends JpaRepository<Budget, Integer>, JpaSpecificationExecutor<Budget> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Budget b WHERE b.id = :id")
    void deleteByIdDirect(@Param("id") Integer id);
}
