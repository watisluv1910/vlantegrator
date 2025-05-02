package com.wladischlau.vlt.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wladischlau.vlt.adapters.common.AbstractAdapter;
import com.wladischlau.vlt.adapters.common.AdapterConfig;
import com.wladischlau.vlt.adapters.common.AdapterType;
import com.wladischlau.vlt.adapters.common.OutboundAdapter;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.dsl.IntegrationFlowBuilder;
import org.springframework.integration.jdbc.JdbcOutboundGateway;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

import static com.wladischlau.vlt.adapters.JdbcOutboundGatewayAdapter.JdbcOutboundAdapterConfig;

@Slf4j
@Getter
public class JdbcOutboundGatewayAdapter extends AbstractAdapter<JdbcOutboundAdapterConfig> implements OutboundAdapter {

    private final DataSource dataSource;

    public JdbcOutboundGatewayAdapter(String configJson) {
        super(configJson, JdbcOutboundAdapterConfig.class);

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.jdbcUrl());
        hikariConfig.setUsername(config.jdbcUsername());
        hikariConfig.setPassword(config.jdbcPassword());
        this.dataSource = new HikariDataSource(hikariConfig);
    }

    @Override
    public IntegrationFlowBuilder apply(IntegrationFlowBuilder flow) {
        return flow.handle(new JdbcOutboundGateway(dataSource, config.query()));
    }

    @Override
    public AdapterType getType() {
        return AdapterType.JDBC_OUTBOUND;
    }

    public record JdbcOutboundAdapterConfig(
            @NotBlank String query,
            @NotBlank String jdbcUrl,
            @NotBlank String jdbcUsername,
            @NotBlank String jdbcPassword
    ) implements AdapterConfig {}
}