package pe.com.poc.payments.adapter.in.web;
import org.springframework.web.bind.annotation.*;import java.math.BigDecimal;import java.util.*;
@RestController @RequestMapping("/internal/v1/payment-checks")
public class InternalSimulationController {
 @PostMapping("/fraud-assessments") Map<String,Object> fraud(@RequestBody InternalPaymentRequest r) throws InterruptedException { Thread.sleep(650); int score = r.amount().compareTo(new BigDecimal("900"))>0 ? 82 : 18; return Map.of("highRisk", score>70, "score", score, "reason", score>70?"AMOUNT_RISK":"LOW_RISK"); }
 @PostMapping("/ledger-reservations") Map<String,Object> ledger(@RequestBody InternalPaymentRequest r) throws InterruptedException { Thread.sleep(900); return Map.of("reserved", true, "reservationId", "rsv-"+UUID.randomUUID()); }
 @PostMapping("/limits-validations") Map<String,Object> limits(@RequestBody InternalPaymentRequest r) throws InterruptedException { Thread.sleep(500); boolean allowed = r.amount().compareTo(new BigDecimal("1500"))<=0; return Map.of("allowed", allowed, "reason", allowed?"WITHIN_LIMIT":"DAILY_LIMIT_EXCEEDED"); }
 @PostMapping("/acquirer-authorizations") Map<String,Object> auth(@RequestBody InternalPaymentRequest r) throws InterruptedException { Thread.sleep(700); return Map.of("authorized", true, "authorizationCode", "AUTH-"+System.currentTimeMillis(), "reason", "AUTHORIZED"); }
 public record InternalPaymentRequest(String paymentId,String merchantId,String customerId,BigDecimal amount,String currency) {}
}
