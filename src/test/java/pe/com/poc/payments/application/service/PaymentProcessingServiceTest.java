package pe.com.poc.payments.application.service;

import org.junit.jupiter.api.Test;
import pe.com.poc.payments.application.port.in.ProcessPaymentUseCase.ProcessPaymentCommand;
import pe.com.poc.payments.application.port.out.InternalChecksPort;
import pe.com.poc.payments.application.port.out.PaymentEventPublisherPort;
import pe.com.poc.payments.application.port.out.PaymentRepositoryPort;
import pe.com.poc.payments.domain.model.PaymentOrder;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentProcessingServiceTest {

    @Test
    void processesParallelChecksOnVirtualThreadsAndApproves() {
        var repository = new InMemoryPaymentRepository();
        var checks = new ParallelChecks();
        var published = new AtomicBoolean(false);
        PaymentEventPublisherPort events = paymentOrder -> published.set(true);
        var service = new PaymentProcessingService(repository, checks, events);

        var result = service.process(new ProcessPaymentCommand(
                "mrc-001",
                "cus-001",
                new BigDecimal("120.50"),
                "PEN",
                "idem-001"
        ));

        assertEquals("APPROVED", result.status());
        assertNotNull(result.authorizationCode());
        assertTrue(checks.allChecksUsedVirtualThreads());
        assertTrue(published.get());
    }

    @Test
    void declinesByLimitWithoutCallingAcquirer() {
        var repository = new InMemoryPaymentRepository();
        var checks = new LimitRejectedChecks();
        var service = new PaymentProcessingService(repository, checks, paymentOrder -> { });

        var result = service.process(new ProcessPaymentCommand(
                "mrc-001",
                "cus-002",
                new BigDecimal("1600.00"),
                "PEN",
                "idem-002"
        ));

        assertEquals("DECLINED", result.status());
        assertEquals("Limit validation failed", result.declineReason());
        assertEquals(false, checks.acquirerCalled.get());
    }

    private static final class InMemoryPaymentRepository implements PaymentRepositoryPort {
        private final Map<String, PaymentOrder> byIdempotencyKey = new ConcurrentHashMap<>();

        @Override
        public Optional<PaymentOrder> findByIdempotencyKey(String idempotencyKey) {
            return Optional.ofNullable(byIdempotencyKey.get(idempotencyKey));
        }

        @Override
        public PaymentOrder save(PaymentOrder paymentOrder) {
            byIdempotencyKey.put(paymentOrder.idempotencyKey(), paymentOrder);
            return paymentOrder;
        }
    }

    private static final class ParallelChecks implements InternalChecksPort {
        private final CountDownLatch started = new CountDownLatch(3);
        private final AtomicBoolean fraudVirtual = new AtomicBoolean(false);
        private final AtomicBoolean ledgerVirtual = new AtomicBoolean(false);
        private final AtomicBoolean limitsVirtual = new AtomicBoolean(false);

        @Override
        public FraudAssessment assessFraud(PaymentOrder order) {
            fraudVirtual.set(Thread.currentThread().isVirtual());
            awaitAllParallelChecks();
            return new FraudAssessment(false, 18, "LOW_RISK");
        }

        @Override
        public LedgerReservation reserveLedger(PaymentOrder order) {
            ledgerVirtual.set(Thread.currentThread().isVirtual());
            awaitAllParallelChecks();
            return new LedgerReservation(true, "rsv-001");
        }

        @Override
        public LimitsValidation validateLimits(PaymentOrder order) {
            limitsVirtual.set(Thread.currentThread().isVirtual());
            awaitAllParallelChecks();
            return new LimitsValidation(true, "WITHIN_LIMIT");
        }

        @Override
        public AcquirerAuthorization authorize(PaymentOrder order) {
            return new AcquirerAuthorization(true, "AUTH-001", "AUTHORIZED");
        }

        boolean allChecksUsedVirtualThreads() {
            return fraudVirtual.get() && ledgerVirtual.get() && limitsVirtual.get();
        }

        private void awaitAllParallelChecks() {
            started.countDown();
            try {
                if (!started.await(2, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("Parallel checks did not start concurrently");
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while testing parallel execution", ex);
            }
        }
    }

    private static final class LimitRejectedChecks implements InternalChecksPort {
        private final AtomicBoolean acquirerCalled = new AtomicBoolean(false);

        @Override
        public FraudAssessment assessFraud(PaymentOrder order) {
            return new FraudAssessment(false, 18, "LOW_RISK");
        }

        @Override
        public LedgerReservation reserveLedger(PaymentOrder order) {
            return new LedgerReservation(true, "rsv-002");
        }

        @Override
        public LimitsValidation validateLimits(PaymentOrder order) {
            return new LimitsValidation(false, "DAILY_LIMIT_EXCEEDED");
        }

        @Override
        public AcquirerAuthorization authorize(PaymentOrder order) {
            acquirerCalled.set(true);
            return new AcquirerAuthorization(true, "AUTH-NOT-EXPECTED", "AUTHORIZED");
        }
    }
}
