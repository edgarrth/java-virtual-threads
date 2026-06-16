package pe.com.poc.payments.application.port.out;
import pe.com.poc.payments.domain.model.PaymentOrder;import java.util.Optional;
public interface PaymentRepositoryPort { Optional<PaymentOrder> findByIdempotencyKey(String idempotencyKey); PaymentOrder save(PaymentOrder paymentOrder); }
