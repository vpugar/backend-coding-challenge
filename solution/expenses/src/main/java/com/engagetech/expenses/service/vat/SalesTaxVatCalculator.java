package com.engagetech.expenses.service.vat;

import com.engagetech.expenses.model.VatData;
import com.engagetech.expenses.service.currency.DefaultCurrency;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Vat calculation algorithm with property defined tax rate:
 * app.expense.vat-calculator.sales-tax.percentage.
 */
@Service
public class SalesTaxVatCalculator implements VatCalculator {

    private static final BigDecimal PERCENTAGE_100 = BigDecimal.valueOf(100L);

    private final DefaultCurrency defaultCurrency;

    private final BigDecimal salesTaxPercentage;

    public SalesTaxVatCalculator(
            DefaultCurrency defaultCurrency,
            @Value("${app.expense.vat-calculator.sales-tax.percentage}") BigDecimal salesTaxPercentage) {
        this.salesTaxPercentage = salesTaxPercentage;
        this.defaultCurrency = defaultCurrency;
    }

    @Override
    public VatData calculate(@NonNull final BigDecimal value) {
        int scale = defaultCurrency.getTarget().getScale();
        BigDecimal vatRate = salesTaxPercentage;
        BigDecimal vatAmount = vatRate
                .multiply(value)
                .divide(PERCENTAGE_100)
                .setScale(scale, RoundingMode.HALF_UP);

        return new VatData(vatRate, vatAmount);
    }
}
