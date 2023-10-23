FROM open-liberty:23.0.0.6-kernel-slim-java17-openj9

ARG VERSION=1.0
ARG REVISION=SNAPSHOT

LABEL \
  org.opencontainers.image.authors="fermentedDairy" \
  org.opencontainers.image.version="$VERSION" \
  org.opencontainers.image.revision="$REVISION" \
  vendor="Open Liberty" \
  name="fd-caches-ol-cdi" \
  version="$VERSION-$REVISION"

COPY --chown=1001:0 src/main/liberty/config /config/

RUN features.sh

COPY --chown=1001:0 target/fd-caches-ol-cdi.war /config/apps

RUN configure.sh