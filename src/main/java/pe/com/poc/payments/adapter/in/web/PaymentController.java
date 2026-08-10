package pe.com.poc.payments.adapter.in.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.com.poc.payments.application.port.in.ProcessPaymentUseCase;

import java.math.BigDecimal;

@RestController
@RequestMapping("/payments/v1/payment-orders")
public class PaymentController {

    private final ProcessPaymentUseCase useCase;

    public PaymentController(ProcessPaymentUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<ProcessPaymentUseCase.PaymentResult> process(
            @RequestHeader("Idempotency-Key") String key,
            @Valid @RequestBody PaymentRequest request
    ) {
        var result = useCase.process(new ProcessPaymentUseCase.ProcessPaymentCommand(
                request.merchantId(),
                request.customerId(),
                request.amount(),
                request.currency(),
                key
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    public record PaymentRequest(
            @NotBlank String merchantId,
            @NotBlank String customerId,
            @NotNull @DecimalMin("0.01") BigDecimal amount,
            @NotBlank @Pattern(regexp = "[A-Z]{3}") String currency
    ) {
    }
}
