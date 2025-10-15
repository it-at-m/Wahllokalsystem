# Dockerfiles may only contain a FROM and the application data.
# For Java applications use /ubi9/openjdk-11-runtime or /ubi9/openjdk-17-runtime as Base Image, for documentation 
# please see https://access.redhat.com/documentation/en-us/red_hat_jboss_middleware_for_openshift/3/html/red_hat_java_s2i_for_openshift/
# All other variations must be approved by KM8

FROM registry.access.redhat.com/ubi9/openjdk-17-runtime:latest@sha256:6f1b8a5cddbd33ed4727caee5543b81723f81f9f96ed441ad8d19acd9a22c81c

COPY target/*.jar /deployments/application.jar
