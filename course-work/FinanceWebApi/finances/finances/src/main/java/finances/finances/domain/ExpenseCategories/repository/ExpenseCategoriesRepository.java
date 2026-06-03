package finances.finances.domain.ExpenseCategories.repository;

import finances.finances.domain.ExpenseCategories.entity.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ExpenseCategoriesRepository extends JpaRepository<ExpenseCategory, Integer>, JpaSpecificationExecutor<ExpenseCategory> {
    @Modifying
    @Transactional
    @Query("DELETE FROM ExpenseCategory ec WHERE ec.id = :id")
    void deleteByIdDirect(@Param("id") Integer id);
}
