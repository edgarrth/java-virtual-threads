package pe.com.poc.payments.application.port.out;
import pe.com.poc.payments.domain.model.PaymentOrder;
public interface PaymentEventPublisherPort { void publishPaymentProcessed(PaymentOrder paymentOrder); }
