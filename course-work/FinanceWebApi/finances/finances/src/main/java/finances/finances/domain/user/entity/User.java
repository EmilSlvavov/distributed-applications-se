package finances.finances.domain.user.entity;

import finances.finances.domain.ExpenseCategories.entity.ExpenseCategory;
import finances.finances.enums.RoleType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.Column;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Unique login identifier — 50 chars covers any reasonable username
    @NotBlank
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    // BCrypt hashes are always 60 chars
    @NotBlank
    @Column(nullable = false, length = 60)
    private String password;

    // Longest expected value is "ADMIN" (5 chars) — 20 gives plenty of room
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 5)
    private RoleType role;

    @NotNull
    @Column(nullable = false)
    private Boolean isActive;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ExpenseCategory> categories = new ArrayList<>();
}
