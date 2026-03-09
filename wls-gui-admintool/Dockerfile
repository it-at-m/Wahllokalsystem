# For documentation see https://github.com/sclorg/nginx-container
FROM registry.access.redhat.com/ubi9/nginx-124:9.7-1772598627@sha256:09b0ed759c163fed2883fb2d44cf39b0f5bef0dc1b3bc504c7c254d3647b4425

# Copy built web application
COPY dist .

# Copy custom nginx configurations
COPY docker/nginx/*.conf "${NGINX_DEFAULT_CONF_PATH}"

# Start the web server
CMD nginx -g "daemon off;"