# For documentation see https://github.com/sclorg/nginx-container
FROM registry.access.redhat.com/ubi9/nginx-124:9.7-1776195488@sha256:544f8607059bfb57ad487f05db404fed9c07918313ef88adb932bc3c4411d8bb

# Copy built web application
COPY dist .

# Copy custom nginx configurations
COPY docker/nginx/default_conf/*.conf "${NGINX_DEFAULT_CONF_PATH}"
COPY docker/nginx/conf/*.conf "${NGINX_CONFIGURATION_PATH}"

# Start the web server
CMD nginx -g "daemon off;"