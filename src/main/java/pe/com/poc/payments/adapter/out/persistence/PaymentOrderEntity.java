package pe.com.poc.payments.adapter.out.persistence;
import jakarta.persistence.*;import java.math.BigDecimal;import java.time.OffsetDateTime;import java.util.UUID;
@Entity @Table(name="payment_orders")
class PaymentOrderEntity { @Id UUID id; String merchantId; String customerId; BigDecimal amount; String currency; String status; String idempotencyKey; String authorizationCode; String declineReason; OffsetDateTime createdAt; OffsetDateTime updatedAt; }
