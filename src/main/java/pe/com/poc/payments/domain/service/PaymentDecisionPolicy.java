package pe.com.poc.payments.domain.service;
import pe.com.poc.payments.application.port.out.InternalChecksPort.*;
public class PaymentDecisionPolicy {
 public boolean canAuthorize(FraudAssessment fraud, LedgerReservation ledger, LimitsValidation limits){ return !fraud.highRisk() && ledger.reserved() && limits.allowed(); }
 public String declineReason(FraudAssessment fraud, LedgerReservation ledger, LimitsValidation limits){ if(fraud.highRisk()) return "Fraud risk rejected"; if(!ledger.reserved()) return "Ledger reservation failed"; if(!limits.allowed()) return "Limit validation failed"; return "Unknown decline"; }
}
