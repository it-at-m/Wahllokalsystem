UPDATE OAUTH2_REGISTERED_CLIENT
SET REDIRECT_URIS = concat(REDIRECT_URIS,
                           ',http://host.docker.internal:8084/login/oauth2/code/sso')
WHERE CLIENT_ID = 'admingui';
