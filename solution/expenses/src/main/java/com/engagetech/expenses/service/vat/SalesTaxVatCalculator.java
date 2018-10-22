package com.engagetech.expenses.service.vat;

import com.engagetech.expenses.model.VatData;
import com.engagetech.expenses.service.currency.DefaultCurrency;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Vat calculation algorithm with property defined tax rate:
 * app.expense.vat-calculator.sales-tax.percentage.
 */
@Service
public class SalesTaxVatCalculator implements VatCalculator {

    private final DefaultCurrency defaultCurrency;

    private final BigDecimal salesTaxPercentage;

    private BigDecimal salesTaxPercentageFactor;

    public SalesTaxVatCalculator(
            DefaultCurrency defaultCurrency,
            @Value("${app.expense.vat-calculator.sales-tax.percentage}") BigDecimal salesTaxPercentage) {
        this.defaultCurrency = defaultCurrency;
        this.salesTaxPercentage = salesTaxPercentage;
    }

    @PostConstruct
    public void init() {
        BigDecimal salesTaxPercentageTmp = salesTaxPercentage
                .setScale(defaultCurrency.getTarget().getScale() * 2, RoundingMode.HALF_UP);
        salesTaxPercentageFactor = salesTaxPercentageTmp
                .divide(new BigDecimal("100.00"), defaultCurrency.getTarget().getScale() * 2, RoundingMode.HALF_UP)
                .add(BigDecimal.ONE);
    }

    @Override
    public VatData calculate(@NonNull final BigDecimal value) {
        int scale = defaultCurrency.getTarget().getScale();
        BigDecimal vatAmount = value.subtract(value
                .divide(salesTaxPercentageFactor, scale * 2, RoundingMode.HALF_UP))
                .setScale(scale, RoundingMode.HALF_UP);

        return new VatData(salesTaxPercentage, vatAmount);
    }
}
