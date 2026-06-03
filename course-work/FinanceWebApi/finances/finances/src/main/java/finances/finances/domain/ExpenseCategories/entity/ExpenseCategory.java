package finances.finances.domain.ExpenseCategories.entity;

import finances.finances.domain.expense.entity.Expense;
import finances.finances.enums.ExpenseType;
import finances.finances.domain.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Positive;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ExpenseCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Stored as a string so DB rows are readable (FOOD, TRANSPORT etc.)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ExpenseType expenseType;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Double categoryBudget;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "expenseCategory",fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Expense> expenses = new ArrayList<>();
}
