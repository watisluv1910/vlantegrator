package com.wladischlau.vlt.adapters.config;

import com.wladischlau.vlt.adapters.common.AdapterConfig;
import jakarta.validation.constraints.Size;

import java.util.List;

public record DividerAdapterConfig(@Size(min = 2) List<String> subFlowChannels) implements AdapterConfig {}