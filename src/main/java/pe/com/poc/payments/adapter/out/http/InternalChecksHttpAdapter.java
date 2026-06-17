package pe.com.poc.payments.adapter.out.http;
import org.springframework.beans.factory.annotation.Value;import org.springframework.stereotype.Component;import org.springframework.web.client.RestClient;import pe.com.poc.payments.application.port.out.InternalChecksPort;import pe.com.poc.payments.domain.model.PaymentOrder;import java.math.BigDecimal;
@Component
public class InternalChecksHttpAdapter implements InternalChecksPort {
 private final RestClient restClient; public InternalChecksHttpAdapter(RestClient.Builder builder,@Value("${app.internal-base-url}") String baseUrl){this.restClient=builder.baseUrl(baseUrl).build();}
 public FraudAssessment assessFraud(PaymentOrder o){ return restClient.post().uri("/internal/v1/payment-checks/fraud-assessments").body(toReq(o)).retrieve().body(FraudAssessment.class); }
 public LedgerReservation reserveLedger(PaymentOrder o){ return restClient.post().uri("/internal/v1/payment-checks/ledger-reservations").body(toReq(o)).retrieve().body(LedgerReservation.class); }
 public LimitsValidation validateLimits(PaymentOrder o){ return restClient.post().uri("/internal/v1/payment-checks/limits-validations").body(toReq(o)).retrieve().body(LimitsValidation.class); }
 public AcquirerAuthorization authorize(PaymentOrder o){ return restClient.post().uri("/internal/v1/payment-checks/acquirer-authorizations").body(toReq(o)).retrieve().body(AcquirerAuthorization.class); }
 private InternalPaymentRequest toReq(PaymentOrder o){return new InternalPaymentRequest(o.id().toString(),o.merchantId(),o.customerId(),o.money().amount(),o.money().currency());}
 record InternalPaymentRequest(String paymentId,String merchantId,String customerId,BigDecimal amount,String currency) {}
}
