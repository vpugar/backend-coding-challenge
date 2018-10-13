package com.engagetech.expenses.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "exchange_rate")
@Data
@NoArgsConstructor
public final class ExchangeRate implements WithId {

    @Id
    @GeneratedValue(
            strategy = IDENTITY
    )
    private Long id;


}
