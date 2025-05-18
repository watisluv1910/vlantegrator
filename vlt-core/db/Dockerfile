FROM alpine:3.21.3 AS build

ARG DB_SCHEMA
ARG DB_USER
ARG DB_PASSWORD

COPY schema.sql .

RUN apk add --no-cache bash \
 && sed  "s/\$DB_SCHEMA/${DB_SCHEMA}/g" schema.sql > schema_ready.sql

FROM postgres:17.4

RUN rm -rf /var/lib/postgresql/data/*
VOLUME /var/lib/postgresql/data

COPY --from=build schema_ready.sql /docker-entrypoint-initdb.d/10_schema.sql