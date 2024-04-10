-- Switch to pluggable database
alter session set container=XEPDB1;

-- produces ignorable error message `user name 'WLS_BROADCAST_SERVICE' conflicts with another ...`
-- when the user already exists

-- add user for wls-broadcast-service
CREATE USER wls_broadcast_service IDENTIFIED BY secret QUOTA UNLIMITED ON USERS;
GRANT CONNECT, RESOURCE, CREATE SESSION TO wls_broadcast_service;