FROM alpine AS build

ARG DB_SCHEMA=vlt_repo
ARG DB_USER=admin
ARG DB_PASSWORD=admin

COPY schema.sql .
COPY user.sql .

RUN apk add --no-cache bash \
 && sed  "s/\$DB_SCHEMA/${DB_SCHEMA}/g" schema.sql > schema_ready.sql \
 && sed  -e "s/\$DB_SCHEMA/${DB_SCHEMA}/g" \
         -e "s/\$DB_USER/${DB_USER}/g" \
         -e "s/\$DB_PASSWORD/${DB_PASSWORD}/g" user.sql >  user_ready.sql

FROM dolthub/dolt-sql-server:1.52.3

COPY --from=build schema_ready.sql /docker-entrypoint-initdb.d/10_schema.sql
COPY --from=build user_ready.sql   /docker-entrypoint-initdb.d/20_user.sql