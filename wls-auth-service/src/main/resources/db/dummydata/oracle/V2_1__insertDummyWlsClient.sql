INSERT INTO OAUTH2_REGISTERED_CLIENT (ID, CLIENT_ID, CLIENT_ID_ISSUED_AT, CLIENT_SECRET,
                                      CLIENT_SECRET_EXPIRES_AT, CLIENT_NAME,
                                      CLIENT_AUTHENTICATION_METHODS, AUTHORIZATION_GRANT_TYPES,
                                      REDIRECT_URIS, POST_LOGOUT_REDIRECT_URIS, SCOPES,
                                      CLIENT_SETTINGS, TOKEN_SETTINGS)
VALUES ('80f12cff-1f7e-4355-8883-3e8cd36a80f0', 'wls', TIMESTAMP '2024-11-29 16:18:19.930112',
        '{bcrypt}$2a$10$zCvSE.rK0Hb6MqkeKHwHzOMhCB5FW12XHOWEyRgiwDsj6i2CfvDj2', null, 'wls-gui-wahllokal',
        'client_secret_basic', 'refresh_token,client_credentials,authorization_code',
        'http://127.0.0.1:8080/oauth2/authorized,http://localhost:8083/login/oauth2/code/sso',
        'http://localhost:39152/', 'openid',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":true}',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.x509-certificate-bound-access-tokens":false,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",300.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000],"settings.token.device-code-time-to-live":["java.time.Duration",300.000000000]}');
