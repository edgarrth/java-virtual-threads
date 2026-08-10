package pe.com.poc.payments.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyTest {

    @Test
    void acceptsPositiveAmountAndIsoCurrency() {
        var money = new Money(new BigDecimal("120.50"), "PEN");

        assertEquals(new BigDecimal("120.50"), money.amount());
        assertEquals("PEN", money.currency());
    }

    @Test
    void rejectsNegativeAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> new Money(new BigDecimal("-1"), "PEN"));
    }

    @Test
    void rejectsInvalidCurrency() {
        assertThrows(IllegalArgumentException.class,
                () -> new Money(BigDecimal.ONE, "PE"));
    }
}
