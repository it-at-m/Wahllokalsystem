CREATE TABLE oauth2_authorization
(
    id                            VARCHAR(100) NOT NULL,
    registered_client_id          VARCHAR(100) NOT NULL,
    principal_name                VARCHAR(200) NOT NULL,
    authorization_grant_type      VARCHAR(100) NOT NULL,
    authorized_scopes             VARCHAR(1000) DEFAULT NULL,

    attributes                    VARCHAR(4000) DEFAULT NULL,
    state                         VARCHAR(500)  DEFAULT NULL,

    authorization_code_value      BLOB          DEFAULT NULL,
    authorization_code_issued_at  TIMESTAMP     DEFAULT NULL,
    authorization_code_expires_at TIMESTAMP     DEFAULT NULL,
    authorization_code_metadata   VARCHAR(2000) DEFAULT NULL,

    access_token_value            BLOB          DEFAULT NULL,
    access_token_issued_at        TIMESTAMP     DEFAULT NULL,
    access_token_expires_at       TIMESTAMP     DEFAULT NULL,
    access_token_metadata         VARCHAR(2000) DEFAULT NULL,
    access_token_type             VARCHAR(100)  DEFAULT NULL,
    access_token_scopes           VARCHAR(1000) DEFAULT NULL,

    oidc_id_token_value           BLOB          DEFAULT NULL,
    oidc_id_token_issued_at       TIMESTAMP     DEFAULT NULL,
    oidc_id_token_expires_at      TIMESTAMP     DEFAULT NULL,
    oidc_id_token_metadata        VARCHAR(2000) DEFAULT NULL,

    refresh_token_value           BLOB          DEFAULT NULL,
    refresh_token_issued_at       TIMESTAMP     DEFAULT NULL,
    refresh_token_expires_at      TIMESTAMP     DEFAULT NULL,
    refresh_token_metadata        VARCHAR(2000) DEFAULT NULL,

    user_code_value               BLOB          DEFAULT NULL,
    user_code_issued_at           TIMESTAMP     DEFAULT NULL,
    user_code_expires_at          TIMESTAMP     DEFAULT NULL,
    user_code_metadata            VARCHAR(2000) DEFAULT NULL,

    device_code_value             BLOB          DEFAULT NULL,
    device_code_issued_at         TIMESTAMP     DEFAULT NULL,
    device_code_expires_at        TIMESTAMP     DEFAULT NULL,
    device_code_metadata          VARCHAR(2000) DEFAULT NULL,

    PRIMARY KEY (id)
);

CREATE INDEX idx_oauth2_authorization_registered_client_id
    ON oauth2_authorization (registered_client_id);

CREATE INDEX idx_oauth2_authorization_principal_name
    ON oauth2_authorization (principal_name);