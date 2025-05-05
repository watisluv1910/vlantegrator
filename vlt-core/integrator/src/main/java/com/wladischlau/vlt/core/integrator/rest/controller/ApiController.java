package com.wladischlau.vlt.core.integrator.rest.controller;

import com.wladischlau.vlt.core.integrator.mapper.DtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import static com.wladischlau.vlt.core.commons.utils.MdcUtil.withMdc;

@Slf4j
@RequiredArgsConstructor
public abstract class ApiController {

    private static final String OPERATION_ID = "operationId";

    protected final DtoMapper dtoMapper;

    protected final <R> R logRequestProcessing(String operationId, Callable<R> processingAction,
                                               Object... requestIdKeyValues) {
        return withMdc(() -> logRequestProcessingInternal(processingAction, false),
                       concatIdentifiers(operationId, requestIdKeyValues));
    }

    protected final <R> R logRequestProcessingWithResponse(String operationId, Callable<R> processingAction,
                                                           Object... requestIdKeyValues) {
        return withMdc(() -> logRequestProcessingInternal(processingAction, true),
                       concatIdentifiers(operationId, requestIdKeyValues));
    }

    protected <R> R logRequestProcessingInternal(Callable<R> action, boolean logResponseOnDebug) throws Exception {
        log.info("received request...");
        var response = action.call();
        if (log.isDebugEnabled() && logResponseOnDebug) {
            log.debug("sending response: {}", response);
        } else {
            log.info("sending response...");
        }
        return response;
    }

    private Object[] concatIdentifiers(String operationId, Object[] requestIdKeyValues) {
        return Stream.concat(Stream.of(OPERATION_ID, operationId), Arrays.stream(requestIdKeyValues)).toArray();
    }
}