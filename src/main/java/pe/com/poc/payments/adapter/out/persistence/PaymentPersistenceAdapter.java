package pe.com.poc.payments.adapter.out.persistence;
import org.springframework.stereotype.Component;import pe.com.poc.payments.application.port.out.PaymentRepositoryPort;import pe.com.poc.payments.domain.model.*;import java.util.Optional;
@Component
public class PaymentPersistenceAdapter implements PaymentRepositoryPort {
 private final SpringDataPaymentJpaRepository repo; public PaymentPersistenceAdapter(SpringDataPaymentJpaRepository repo){this.repo=repo;}
 public Optional<PaymentOrder> findByIdempotencyKey(String k){return repo.findByIdempotencyKey(k).map(this::toDomain);} public PaymentOrder save(PaymentOrder p){return toDomain(repo.save(toEntity(p)));}
 private PaymentOrderEntity toEntity(PaymentOrder p){var e=new PaymentOrderEntity(); e.id=p.id();e.merchantId=p.merchantId();e.customerId=p.customerId();e.amount=p.money().amount();e.currency=p.money().currency();e.status=p.status().name();e.idempotencyKey=p.idempotencyKey();e.authorizationCode=p.authorizationCode();e.declineReason=p.declineReason();e.createdAt=p.createdAt();e.updatedAt=p.updatedAt();return e;}
 private PaymentOrder toDomain(PaymentOrderEntity e){return PaymentOrder.rehydrate(e.id,e.merchantId,e.customerId,new Money(e.amount,e.currency),e.idempotencyKey,PaymentStatus.valueOf(e.status),e.authorizationCode,e.declineReason,e.createdAt,e.updatedAt);}
}
