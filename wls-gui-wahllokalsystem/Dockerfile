# For documentation see https://github.com/sclorg/nginx-container
FROM registry.access.redhat.com/ubi9/nginx-124:9.7-1778647245@sha256:5184bd24445e9586fc96362de0ba9325605e25f7e7ce6d1a7d576e00600607b3

# Copy built web application
COPY dist .

# Copy custom nginx configurations
COPY docker/nginx/default_conf/*.conf "${NGINX_DEFAULT_CONF_PATH}"
COPY docker/nginx/conf/*.conf "${NGINX_CONFIGURATION_PATH}"

# Start the web server
CMD nginx -g "daemon off;"