package finances.finances.domain.budget.entity;

import finances.finances.domain.user.entity.User;
import finances.finances.enums.CurrencyType;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Double value;

    // ISO 4217 currency codes are always exactly 3 characters (EUR, USD, BGN)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private CurrencyType currency;

    @NotNull
    @Column(nullable = false)
    private Boolean isRecurring;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
