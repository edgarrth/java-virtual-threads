package pe.com.poc.payments.application.service;
import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;import pe.com.poc.payments.application.port.in.ProcessPaymentUseCase;import pe.com.poc.payments.application.port.out.*;import pe.com.poc.payments.domain.model.*;import pe.com.poc.payments.domain.service.PaymentDecisionPolicy;
import java.util.concurrent.*;
@Service
public class PaymentProcessingService implements ProcessPaymentUseCase {
 private final PaymentRepositoryPort repository; private final InternalChecksPort checks; private final PaymentEventPublisherPort events; private final PaymentDecisionPolicy policy = new PaymentDecisionPolicy();
 public PaymentProcessingService(PaymentRepositoryPort repository, InternalChecksPort checks, PaymentEventPublisherPort events){this.repository=repository;this.checks=checks;this.events=events;}
 @Override @Transactional
 public PaymentResult process(ProcessPaymentCommand command){
  var existing=repository.findByIdempotencyKey(command.idempotencyKey()); if(existing.isPresent()) return toResult(existing.get());
  var order=PaymentOrder.receive(command.merchantId(), command.customerId(), new Money(command.amount(), command.currency()), command.idempotencyKey());
  try(var executor=Executors.newVirtualThreadPerTaskExecutor()){
   Future<InternalChecksPort.FraudAssessment> fraud=executor.submit(() -> checks.assessFraud(order));
   Future<InternalChecksPort.LedgerReservation> ledger=executor.submit(() -> checks.reserveLedger(order));
   Future<InternalChecksPort.LimitsValidation> limits=executor.submit(() -> checks.validateLimits(order));
   var fraudResult=fraud.get(); var ledgerResult=ledger.get(); var limitsResult=limits.get();
   if(!policy.canAuthorize(fraudResult, ledgerResult, limitsResult)){ order.decline(policy.declineReason(fraudResult, ledgerResult, limitsResult)); }
   else { var auth=checks.authorize(order); if(auth.authorized()) order.approve(auth.authorizationCode()); else order.decline(auth.reason()); }
  } catch (InterruptedException e){ Thread.currentThread().interrupt(); order.decline("Interrupted while processing payment"); }
    catch (ExecutionException e){ order.decline("Internal orchestration error: "+e.getCause().getMessage()); }
  var saved=repository.save(order); events.publishPaymentProcessed(saved); return toResult(saved);
 }
 private PaymentResult toResult(PaymentOrder p){ return new PaymentResult(p.id(), p.status().name(), p.authorizationCode(), p.declineReason()); }
}
