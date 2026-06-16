package pe.com.poc.payments.adapter.out.persistence;
import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
interface SpringDataPaymentJpaRepository extends JpaRepository<PaymentOrderEntity, UUID> { Optional<PaymentOrderEntity> findByIdempotencyKey(String idempotencyKey); }
