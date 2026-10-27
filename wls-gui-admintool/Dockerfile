# For documentation see https://github.com/sclorg/nginx-container
FROM registry.access.redhat.com/ubi9/nginx-124:9.8-1793031939@sha256:d1eb117322c0374e286f73d6f67912c1171c605eb34cd5dd33005375747c77a5

# Copy built web application
COPY dist .

# Copy custom nginx configurations
COPY docker/nginx/default_conf/*.conf "${NGINX_DEFAULT_CONF_PATH}"
COPY docker/nginx/conf/*.conf "${NGINX_CONFIGURATION_PATH}"

# Start the web server
CMD nginx -g "daemon off;"