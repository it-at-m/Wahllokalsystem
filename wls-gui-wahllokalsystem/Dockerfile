# For documentation see https://github.com/sclorg/nginx-container
FROM registry.access.redhat.com/ubi9/nginx-124:9.7-1776735087@sha256:a82017a45fece95f30e4e7a88401b583998dcc282eefdce9513269e498ee8711

# Copy built web application
COPY dist .

# Copy custom nginx configurations
COPY docker/nginx/default_conf/*.conf "${NGINX_DEFAULT_CONF_PATH}"
COPY docker/nginx/conf/*.conf "${NGINX_CONFIGURATION_PATH}"

# Start the web server
CMD nginx -g "daemon off;"