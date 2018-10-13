package com.engagetech.expenses.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "currency")
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "shortName")
public final class Currency implements WithId, Cloneable {

    @Id
    @GeneratedValue(
            strategy = IDENTITY
    )
    private Long id;

    @Column(name = "short_name", nullable = false, updatable = false)
    private String shortName;

    @Column(nullable = false)
    private int scale;

    public Currency(String shortName, int scale) {
        this.shortName = shortName;
        this.scale = scale;
    }

    @Override
    public Currency clone() {
        try {
            return (Currency) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Cannot clone Currency", e);
        }
    }

    @Override
    public String toString() {
        return shortName;
    }
}
