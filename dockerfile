# Dockerfile
FROM quay.io/keycloak/keycloak:26.2.4

ENV KEYCLOAK_ADMIN=admin
ENV KEYCLOAK_ADMIN_PASSWORD=admin
ENV KC_HOSTNAME_STRICT=false

ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start-dev", "--features=account,admin"]

