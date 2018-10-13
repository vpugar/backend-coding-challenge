package com.engagetech.expenses.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

import static com.engagetech.expenses.util.Constants.STRING_MAX_LENGTH;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "expense")
@Data
@NoArgsConstructor
public final class Expense implements WithId {

    @Id
    @GeneratedValue(
            strategy = IDENTITY
    )
    private Long id;

    @Column(name = "expense_date", nullable = false, updatable = false)
    private LocalDate date;

    @Column(nullable = false, updatable = false, length = STRING_MAX_LENGTH)
    private String reason;

    @Embedded
    private VatData vatData;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, updatable = false)
    private User user;

    @Setter(AccessLevel.NONE)
    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    public void setUser(User user) {
        this.user = user;
        userId = user.getId();
    }
}
