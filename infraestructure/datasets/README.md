# Datasets e insumos de prueba

## Requests
- `requests/payment-approved.json`: pago esperado como `APPROVED`.
- `requests/payment-declined-limit.json`: pago esperado como `DECLINED` por límite.

## PostgreSQL
`postgres/001_seed_payments.sql` se monta en Docker Compose. Las tablas reales se crean con Flyway desde `src/main/resources/db/migration` cuando inicia Spring Boot.

## Ejemplo curl
```bash
curl -X POST http://localhost:8080/payments/v1/payment-orders \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: demo-001" \
  -d @infraestructure/datasets/requests/payment-approved.json
```
