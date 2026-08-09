CREATE TABLE oauth2_authorization
(
    id                            VARCHAR2(100)    NOT NULL,
    registered_client_id          VARCHAR2(100)    NOT NULL,
    principal_name                VARCHAR2(200)    NOT NULL,
    authorization_grant_type      VARCHAR2(100)    NOT NULL,
    authorized_scopes             VARCHAR2(4000),

    attributes                    VARCHAR2(4000),
    state                         VARCHAR2(500),

    authorization_code_value      VARCHAR2(4000),
    authorization_code_issued_at  TIMESTAMP,
    authorization_code_expires_at TIMESTAMP,
    authorization_code_metadata   VARCHAR2(4000),

    access_token_value            VARCHAR2(4000),
    access_token_issued_at        TIMESTAMP,
    access_token_expires_at       TIMESTAMP,
    access_token_metadata         VARCHAR2(4000),
    access_token_type             VARCHAR2(100),
    access_token_scopes           VARCHAR2(4000),

    oidc_id_token_value           VARCHAR2(4000),
    oidc_id_token_issued_at       TIMESTAMP,
    oidc_id_token_expires_at      TIMESTAMP,
    oidc_id_token_metadata        VARCHAR2(4000),

    refresh_token_value           VARCHAR2(4000),
    refresh_token_issued_at       TIMESTAMP,
    refresh_token_expires_at      TIMESTAMP,
    refresh_token_metadata        VARCHAR2(4000),

    user_code_value               VARCHAR2(4000),
    user_code_issued_at           TIMESTAMP,
    user_code_expires_at          TIMESTAMP,
    user_code_metadata            VARCHAR2(4000),

    device_code_value             VARCHAR2(4000),
    device_code_issued_at         TIMESTAMP,
    device_code_expires_at        TIMESTAMP,
    device_code_metadata          VARCHAR2(4000),

    CONSTRAINT pk_oauth2_authorization PRIMARY KEY (id)
);

CREATE INDEX idx_oauth2_authorization_registered_client_id
    ON oauth2_authorization (registered_client_id);

CREATE INDEX idx_oauth2_authorization_principal_name
    ON oauth2_authorization (principal_name);
