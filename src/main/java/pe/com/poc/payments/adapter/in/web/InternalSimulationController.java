package pe.com.poc.payments.adapter.in.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/internal/v1/payment-checks")
public class InternalSimulationController {

    @PostMapping("/fraud-assessments")
    Map<String, Object> fraud(@RequestBody InternalPaymentRequest request) throws InterruptedException {
        Thread.sleep(650);
        int score = request.amount().compareTo(new BigDecimal("5000")) > 0 ? 82 : 18;
        return Map.of(
                "highRisk", score > 70,
                "score", score,
                "reason", score > 70 ? "AMOUNT_RISK" : "LOW_RISK"
        );
    }

    @PostMapping("/ledger-reservations")
    Map<String, Object> ledger(@RequestBody InternalPaymentRequest request) throws InterruptedException {
        Thread.sleep(900);
        return Map.of("reserved", true, "reservationId", "rsv-" + UUID.randomUUID());
    }

    @PostMapping("/limits-validations")
    Map<String, Object> limits(@RequestBody InternalPaymentRequest request) throws InterruptedException {
        Thread.sleep(500);
        boolean allowed = request.amount().compareTo(new BigDecimal("1500")) <= 0;
        return Map.of(
                "allowed", allowed,
                "reason", allowed ? "WITHIN_LIMIT" : "DAILY_LIMIT_EXCEEDED"
        );
    }

    @PostMapping("/acquirer-authorizations")
    Map<String, Object> auth(@RequestBody InternalPaymentRequest request) throws InterruptedException {
        Thread.sleep(700);
        return Map.of(
                "authorized", true,
                "authorizationCode", "AUTH-" + System.currentTimeMillis(),
                "reason", "AUTHORIZED"
        );
    }

    public record InternalPaymentRequest(
            String paymentId,
            String merchantId,
            String customerId,
            BigDecimal amount,
            String currency
    ) {
    }
}
