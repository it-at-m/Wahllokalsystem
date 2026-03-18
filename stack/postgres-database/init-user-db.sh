#!/usr/bin/env bash
set -e

# add user for wls-broadcast-service
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
  CREATE USER wls_broadcast_service PASSWORD 'secret';
  CREATE DATABASE wls_broadcast_service;
  GRANT ALL PRIVILEGES ON DATABASE wls_broadcast_service TO wls_broadcast_service;
  \c wls_broadcast_service;
  GRANT ALL ON SCHEMA public TO wls_broadcast_service;
EOSQL

# add user for wls-briefwahl-service
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
  CREATE USER wls_briefwahl_service PASSWORD 'secret';
  CREATE DATABASE wls_briefwahl_service;
  GRANT ALL PRIVILEGES ON DATABASE wls_briefwahl_service TO wls_briefwahl_service;
  \c wls_briefwahl_service;
  GRANT ALL ON SCHEMA public TO wls_briefwahl_service;
EOSQL

# add user for wls-wahlvorbereitung-service
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
  CREATE USER wls_wahlvorbereitung_service PASSWORD 'secret';
  CREATE DATABASE wls_wahlvorbereitung_service;
  GRANT ALL PRIVILEGES ON DATABASE wls_wahlvorbereitung_service TO wls_wahlvorbereitung_service;
  \c wls_wahlvorbereitung_service;
  GRANT ALL ON SCHEMA public TO wls_wahlvorbereitung_service;
EOSQL

# add user for wls-infomanagement-service
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
  CREATE USER wls_infomanagement_service PASSWORD 'secret';
  CREATE DATABASE wls_infomanagement_service;
  GRANT ALL PRIVILEGES ON DATABASE wls_infomanagement_service TO wls_infomanagement_service;
  \c wls_infomanagement_service;
  GRANT ALL ON SCHEMA public TO wls_infomanagement_service;
EOSQL

# add user for wls-eai-service
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
  CREATE USER wls_eai_service PASSWORD 'secret';
  CREATE DATABASE wls_eai_service;
  GRANT ALL PRIVILEGES ON DATABASE wls_eai_service TO wls_eai_service;
  \c wls_eai_service;
  GRANT ALL ON SCHEMA public TO wls_eai_service;
EOSQL

# add user for wls-basisdaten-service
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
  CREATE USER wls_basisdaten_service PASSWORD 'secret';
  CREATE DATABASE wls_basisdaten_service;
  GRANT ALL PRIVILEGES ON DATABASE wls_basisdaten_service TO wls_basisdaten_service;
  \c wls_basisdaten_service;
  GRANT ALL ON SCHEMA public TO wls_basisdaten_service;
EOSQL

# add user for wls-monitoring-service
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
  CREATE USER wls_monitoring_service PASSWORD 'secret';
  CREATE DATABASE wls_monitoring_service;
  GRANT ALL PRIVILEGES ON DATABASE wls_monitoring_service TO wls_monitoring_service;
  \c wls_monitoring_service;
  GRANT ALL ON SCHEMA public TO wls_monitoring_service;
EOSQL

# add user for wls-wahlvorstand-service
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
  CREATE USER wls_wahlvorstand_service PASSWORD 'secret';
  CREATE DATABASE wls_wahlvorstand_service;
  GRANT ALL PRIVILEGES ON DATABASE wls_wahlvorstand_service TO wls_wahlvorstand_service;
  \c wls_wahlvorstand_service;
  GRANT ALL ON SCHEMA public TO wls_wahlvorstand_service;
EOSQL

# add user for wls-ergebnismeldung-service
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
  CREATE USER wls_ergebnismeldung_service PASSWORD 'secret';
  CREATE DATABASE wls_ergebnismeldung_service;
  GRANT ALL PRIVILEGES ON DATABASE wls_ergebnismeldung_service TO wls_ergebnismeldung_service;
  \c wls_ergebnismeldung_service;
  GRANT ALL ON SCHEMA public TO wls_ergebnismeldung_service;
EOSQL

# add user for wls-auth-service
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
  CREATE USER wls_auth_service PASSWORD 'secret';
  CREATE DATABASE wls_auth_service;
  GRANT ALL PRIVILEGES ON DATABASE wls_auth_service TO wls_auth_service;
  \c wls_auth_service;
  GRANT ALL ON SCHEMA public TO wls_auth_service;
EOSQL

# add user for wls-vorfaelleundvorkommnisse-service
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
  CREATE USER wls_vorfaelleundvorkommnisse_service PASSWORD 'secret';
  CREATE DATABASE wls_vorfaelleundvorkommnisse_service;
  GRANT ALL PRIVILEGES ON DATABASE wls_vorfaelleundvorkommnisse_service TO wls_vorfaelleundvorkommnisse_service;
  \c wls_vorfaelleundvorkommnisse_service;
  GRANT ALL ON SCHEMA public TO wls_vorfaelleundvorkommnisse_service;
EOSQL

# add user for wls-admin-service
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
  CREATE USER wls_admin_service PASSWORD 'secret';
  CREATE DATABASE wls_admin_service;
  GRANT ALL PRIVILEGES ON DATABASE wls_admin_service TO wls_admin_service;
  \c wls_admin_service;
  GRANT ALL ON SCHEMA public TO wls_admin_service;
EOSQL
