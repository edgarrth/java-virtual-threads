package pe.com.poc.payments.application.port.out;
import pe.com.poc.payments.domain.model.PaymentOrder;
public interface InternalChecksPort {
  FraudAssessment assessFraud(PaymentOrder order); LedgerReservation reserveLedger(PaymentOrder order); LimitsValidation validateLimits(PaymentOrder order); AcquirerAuthorization authorize(PaymentOrder order);
  record FraudAssessment(boolean highRisk, int score, String reason) {}
  record LedgerReservation(boolean reserved, String reservationId) {}
  record LimitsValidation(boolean allowed, String reason) {}
  record AcquirerAuthorization(boolean authorized, String authorizationCode, String reason) {}
}
