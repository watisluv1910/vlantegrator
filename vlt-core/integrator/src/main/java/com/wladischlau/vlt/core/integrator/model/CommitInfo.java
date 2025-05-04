package com.wladischlau.vlt.core.integrator.model;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record CommitInfo(String hash, String author, String message, OffsetDateTime date) {}