-- Switch to pluggable database
alter session set container=XEPDB1;

-- produces ignorable error message `user name 'WLS_BROADCAST_SERVICE' conflicts with another ...`
-- when the user already exists

-- add user for wls-broadcast-service
CREATE USER wls_broadcast_service IDENTIFIED BY secret QUOTA UNLIMITED ON USERS;
GRANT CONNECT, RESOURCE, CREATE SESSION TO wls_broadcast_service;

-- add user for wls-briefwahl-service
CREATE USER wls_briefwahl_service IDENTIFIED BY secret QUOTA UNLIMITED ON USERS;
GRANT CONNECT, RESOURCE, CREATE SESSION TO wls_briefwahl_service;

-- add user for wls-wahlvorbereitung-service
CREATE USER wls_wahlvorbereitung_service IDENTIFIED BY secret QUOTA UNLIMITED ON USERS;
GRANT CONNECT, RESOURCE, CREATE SESSION TO wls_wahlvorbereitung_service;

-- add user for wls-infomanagement-service
CREATE USER wls_infomanagement_service IDENTIFIED BY secret QUOTA UNLIMITED ON USERS;
GRANT CONNECT, RESOURCE, CREATE SESSION TO wls_infomanagement_service;

-- add user for wls-eai-service
CREATE USER wls_eai_service IDENTIFIED BY secret QUOTA UNLIMITED ON USERS;
GRANT CONNECT, RESOURCE, CREATE SESSION TO wls_eai_service;

-- add user for wls-basisdaten-service
CREATE USER wls_basisdaten_service IDENTIFIED BY secret QUOTA UNLIMITED ON USERS;
GRANT CONNECT, RESOURCE, CREATE SESSION TO wls_basisdaten_service;

-- add user for wls-monitoring-service
CREATE USER wls_monitoring_service IDENTIFIED BY secret QUOTA UNLIMITED ON USERS;
GRANT CONNECT, RESOURCE, CREATE SESSION TO wls_monitoring_service;

-- add user for wls-wahlvorstand-service
CREATE USER wls_wahlvorstand_service IDENTIFIED BY secret QUOTA UNLIMITED ON USERS;
GRANT CONNECT, RESOURCE, CREATE SESSION TO wls_wahlvorstand_service;

-- add user for wls-ergebnismeldung-service
CREATE USER wls_ergebnismeldung_service IDENTIFIED BY secret QUOTA UNLIMITED ON USERS;
GRANT CONNECT, RESOURCE, CREATE SESSION TO wls_ergebnismeldung_service;

-- add user for wls-auth-service
CREATE USER wls_auth_service IDENTIFIED BY secret QUOTA UNLIMITED ON USERS;
GRANT CONNECT, RESOURCE, CREATE SESSION TO wls_auth_service;