-- Dataset inicial: crea registros históricos para validar idempotencia y consultas manuales.
-- Flyway crea las tablas al iniciar la app. Este script se ejecuta al crear el contenedor Postgres.
-- Para esta PoC no inserta tablas porque las migraciones viven en src/main/resources/db/migration.
select 'payments dataset ready' as status;
