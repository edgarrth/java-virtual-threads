package pe.com.poc.payments.domain.service;

import org.junit.jupiter.api.Test;
import pe.com.poc.payments.application.port.out.InternalChecksPort.FraudAssessment;
import pe.com.poc.payments.application.port.out.InternalChecksPort.LedgerReservation;
import pe.com.poc.payments.application.port.out.InternalChecksPort.LimitsValidation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentDecisionPolicyTest {

    private final PaymentDecisionPolicy policy = new PaymentDecisionPolicy();

    @Test
    void authorizesWhenAllParallelChecksPass() {
        assertTrue(policy.canAuthorize(
                new FraudAssessment(false, 18, "LOW_RISK"),
                new LedgerReservation(true, "rsv-1"),
                new LimitsValidation(true, "WITHIN_LIMIT")
        ));
    }

    @Test
    void rejectsByLimitWhenFraudAndLedgerPass() {
        var fraud = new FraudAssessment(false, 18, "LOW_RISK");
        var ledger = new LedgerReservation(true, "rsv-1");
        var limits = new LimitsValidation(false, "DAILY_LIMIT_EXCEEDED");

        assertFalse(policy.canAuthorize(fraud, ledger, limits));
        assertEquals("Limit validation failed", policy.declineReason(fraud, ledger, limits));
    }
}
