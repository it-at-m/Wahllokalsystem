# For documentation see https://github.com/sclorg/nginx-container
FROM registry.access.redhat.com/ubi9/nginx-124:9.8-1782419490@sha256:36e29ae4cc5778800882bf1f18525b0edfcde25354491b5fcdfa60b6cb42df86

# Copy built web application
COPY dist .

# Copy custom nginx configurations
COPY docker/nginx/default_conf/*.conf "${NGINX_DEFAULT_CONF_PATH}"
COPY docker/nginx/conf/*.conf "${NGINX_CONFIGURATION_PATH}"

# Start the web server
CMD nginx -g "daemon off;"