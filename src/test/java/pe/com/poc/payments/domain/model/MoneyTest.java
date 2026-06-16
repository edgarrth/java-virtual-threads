package pe.com.poc.payments.domain.model;
import org.junit.jupiter.api.Test;import java.math.BigDecimal;import static org.junit.jupiter.api.Assertions.*;
class MoneyTest { @Test void rejectsNegativeAmount(){ assertThrows(IllegalArgumentException.class, () -> new Money(new BigDecimal("-1"), "PEN")); } }
