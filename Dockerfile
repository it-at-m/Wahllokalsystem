# Dockerfiles may only contain a FROM and the application data.
# For Java applications use /ubi9/openjdk-11-runtime or /ubi9/openjdk-17-runtime as Base Image, for documentation 
# please see https://access.redhat.com/documentation/en-us/red_hat_jboss_middleware_for_openshift/3/html/red_hat_java_s2i_for_openshift/
# All other variations must be approved by KM8

FROM registry.access.redhat.com/ubi9/openjdk-17-runtime:latest@sha256:833a94be33a123477d433b0e3001dcf00d3a0e6ace5a4e80a64ccb7f04960cee

COPY target/*.jar /deployments/application.jar
