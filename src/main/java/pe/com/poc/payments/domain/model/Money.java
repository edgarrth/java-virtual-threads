package pe.com.poc.payments.domain.model;
import java.math.BigDecimal;
public record Money(BigDecimal amount, String currency) {
  public Money { if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException("amount must be positive"); if (currency == null || currency.length()!=3) throw new IllegalArgumentException("currency must be ISO-4217"); }
}
