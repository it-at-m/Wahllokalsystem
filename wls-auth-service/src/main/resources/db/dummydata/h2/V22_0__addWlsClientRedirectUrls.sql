UPDATE OAUTH2_REGISTERED_CLIENT
SET REDIRECT_URIS = concat(REDIRECT_URIS, ',https://gui.wls.host.docker.internal:58083/login/oauth2/code/sso')
WHERE CLIENT_ID = 'wls';
