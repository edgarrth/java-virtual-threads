package pe.com.poc.payments.adapter.out.messaging;
import org.springframework.kafka.core.KafkaTemplate;import org.springframework.stereotype.Component;import pe.com.poc.payments.application.port.out.PaymentEventPublisherPort;import pe.com.poc.payments.domain.model.PaymentOrder;
@Component
public class KafkaPaymentEventPublisher implements PaymentEventPublisherPort {
 private final KafkaTemplate<String,String> kafka; public KafkaPaymentEventPublisher(KafkaTemplate<String,String> kafka){this.kafka=kafka;}
 public void publishPaymentProcessed(PaymentOrder p){ String payload = "{\"paymentId\":\""+p.id()+"\",\"status\":\""+p.status()+"\",\"amount\":"+p.money().amount()+",\"currency\":\""+p.money().currency()+"\"}"; kafka.send("payments.processed.v1", p.id().toString(), payload); }
}
