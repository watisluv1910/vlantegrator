<#--noinspection ALL-->
spring.application.name=${route.name}

management.endpoints.web.exposure.include=health,prometheus
management.metrics.export.prometheus.enabled=true
management.metrics.distribution.percentiles-histogram."[http.server.requests]"=true
management.metrics.tags.application=${route.name}

logging.level.root=INFO