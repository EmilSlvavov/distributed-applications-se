package finances.finances.domain.expense.entity;

import finances.finances.domain.ExpenseCategories.entity.ExpenseCategory;
import finances.finances.domain.budget.entity.Budget;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private ExpenseCategory expenseCategory;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Double amount;

    // Defaults to false in service if not provided — but once saved must not be null
    @NotNull
    @Column(nullable = false)
    private Boolean isRecurring;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime expenseDate;

    // Optional free-text note — 255 covers a full sentence comfortably
    @Column(length = 255)
    private String description;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget budget;
}

