package com.engagetech.expenses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Matcher;
import org.hamcrest.number.BigDecimalCloseTo;
import org.hamcrest.number.IsCloseTo;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

public final class ExpensesApplicationJunitTestHelper {

    public static final double DEFAULT_PRECISION = 0.0000001;
    public static final long USER_ID = 1L;

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8);

    private ExpensesApplicationJunitTestHelper() {
        throw new UnsupportedOperationException("Cannot use constructor");
    }

    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        JavaTimeModule module = new JavaTimeModule();
        mapper.registerModule(module);

        return mapper.writeValueAsBytes(object);
    }

    public static Matcher<BigDecimal> closeToBigDecima(BigDecimal value) {
        return BigDecimalCloseTo.closeTo(value, new BigDecimal(DEFAULT_PRECISION));
    }

    /**
     * Hamcreast workarround as it resolves double from json data
     *
     * @param value
     * @return
     */
    public static Matcher<Double> closeToDouble(BigDecimal value) {
        return IsCloseTo.closeTo(value.doubleValue(), DEFAULT_PRECISION);
    }
}
