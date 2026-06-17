package pe.com.poc.payments.application.port.in;
import java.math.BigDecimal;import java.util.UUID;
public interface ProcessPaymentUseCase {
  PaymentResult process(ProcessPaymentCommand command);
  record ProcessPaymentCommand(String merchantId,String customerId,BigDecimal amount,String currency,String idempotencyKey) {}
  record PaymentResult(UUID paymentId,String status,String authorizationCode,String declineReason) {}
}
