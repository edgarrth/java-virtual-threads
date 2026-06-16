create table payment_orders (
  id uuid primary key,
  merchant_id varchar(80) not null,
  customer_id varchar(80) not null,
  amount numeric(14,2) not null,
  currency varchar(3) not null,
  status varchar(30) not null,
  idempotency_key varchar(120) not null unique,
  authorization_code varchar(80),
  decline_reason varchar(300),
  created_at timestamptz not null,
  updated_at timestamptz not null
);
create table payment_outbox_events (
  id uuid primary key,
  aggregate_id uuid not null,
  event_type varchar(120) not null,
  payload jsonb not null,
  created_at timestamptz not null,
  published_at timestamptz
);
