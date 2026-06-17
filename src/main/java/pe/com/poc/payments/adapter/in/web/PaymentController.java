package pe.com.poc.payments.adapter.in.web;
import jakarta.validation.Valid;import jakarta.validation.constraints.*;import org.springframework.http.*;import org.springframework.web.bind.annotation.*;import pe.com.poc.payments.application.port.in.ProcessPaymentUseCase;import java.math.BigDecimal;
@RestController @RequestMapping("/payments/v1/payment-orders")
public class PaymentController {
 private final ProcessPaymentUseCase useCase; public PaymentController(ProcessPaymentUseCase useCase){this.useCase=useCase;}
 @PostMapping public ResponseEntity<ProcessPaymentUseCase.PaymentResult> process(@RequestHeader("Idempotency-Key") String key,@Valid @RequestBody PaymentRequest request){
  var result=useCase.process(new ProcessPaymentUseCase.ProcessPaymentCommand(request.merchantId(),request.customerId(),request.amount(),request.currency(),key)); return ResponseEntity.status(HttpStatus.CREATED).body(result);
 }
 public record PaymentRequest(@NotBlank String merchantId,@NotBlank String customerId,@NotNull @DecimalMin("0.01") BigDecimal amount,@Pattern(regexp="[A-Z]{3}") String currency) {}
}
