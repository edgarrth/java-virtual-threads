package pe.com.poc.payments.domain.model;
import java.time.OffsetDateTime;import java.util.UUID;
public class PaymentOrder {
  private final UUID id; private final String merchantId; private final String customerId; private final Money money; private final String idempotencyKey;
  private PaymentStatus status; private String authorizationCode; private String declineReason; private final OffsetDateTime createdAt; private OffsetDateTime updatedAt;
  private PaymentOrder(UUID id,String merchantId,String customerId,Money money,String idempotencyKey,PaymentStatus status,String authorizationCode,String declineReason,OffsetDateTime createdAt,OffsetDateTime updatedAt){this.id=id;this.merchantId=merchantId;this.customerId=customerId;this.money=money;this.idempotencyKey=idempotencyKey;this.status=status;this.authorizationCode=authorizationCode;this.declineReason=declineReason;this.createdAt=createdAt;this.updatedAt=updatedAt;}
  public static PaymentOrder receive(String merchantId,String customerId,Money money,String idempotencyKey){return new PaymentOrder(UUID.randomUUID(),merchantId,customerId,money,idempotencyKey,PaymentStatus.RECEIVED,null,null,OffsetDateTime.now(),OffsetDateTime.now());}
  public static PaymentOrder rehydrate(UUID id,String merchantId,String customerId,Money money,String idempotencyKey,PaymentStatus status,String authorizationCode,String declineReason,OffsetDateTime createdAt,OffsetDateTime updatedAt){return new PaymentOrder(id,merchantId,customerId,money,idempotencyKey,status,authorizationCode,declineReason,createdAt,updatedAt);}
  public void approve(String code){this.status=PaymentStatus.APPROVED;this.authorizationCode=code;this.updatedAt=OffsetDateTime.now();}
  public void decline(String reason){this.status=PaymentStatus.DECLINED;this.declineReason=reason;this.updatedAt=OffsetDateTime.now();}
  public UUID id(){return id;} public String merchantId(){return merchantId;} public String customerId(){return customerId;} public Money money(){return money;} public String idempotencyKey(){return idempotencyKey;} public PaymentStatus status(){return status;} public String authorizationCode(){return authorizationCode;} public String declineReason(){return declineReason;} public OffsetDateTime createdAt(){return createdAt;} public OffsetDateTime updatedAt(){return updatedAt;}
}
