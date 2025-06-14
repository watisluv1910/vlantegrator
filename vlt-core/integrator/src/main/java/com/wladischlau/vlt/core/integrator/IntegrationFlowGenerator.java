package com.wladischlau.vlt.core.integrator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wladischlau.vlt.adapters.DividerAdapter;
import com.wladischlau.vlt.core.integrator.model.RouteDefinition;
import com.wladischlau.vlt.core.integrator.service.BranchExtractor;
import com.wladischlau.vlt.core.integrator.model.FlowDefinition;
import com.wladischlau.vlt.core.integrator.model.Node;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.javapoet.AnnotationSpec;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.CodeBlock;
import org.springframework.javapoet.JavaFile;
import org.springframework.javapoet.MethodSpec;
import org.springframework.javapoet.TypeSpec;
import org.springframework.stereotype.Service;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.wladischlau.vlt.adapters.common.AdapterUtils.configMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class IntegrationFlowGenerator {

    private final BranchExtractor branchExtractor;

    public void generateFlowConfig(RouteDefinition routeDefinition, Path outputDir) {
        var enableIntegrationClass = ClassName.get("org.springframework.integration.config", "EnableIntegration");

        var configClassBuilder = TypeSpec.classBuilder("IntegrationFlowConfig")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Configuration.class)
                .addAnnotation(AnnotationSpec.builder(enableIntegrationClass).build())
                .addAnnotation(RequiredArgsConstructor.class);

        var flows = toIntegrationFlows(routeDefinition);

        for (int i = 0, flowsSize = flows.size(); i < flowsSize; i++) {
            try {
                var flowBean = generateFlowBean(flows.get(i), i);
                configClassBuilder.addMethod(flowBean);
            } catch (JsonProcessingException | RuntimeException e) {
                var msg = MessageFormat.format("Ошибка при генерации бина потока [flow: {0}]: {1}",
                                               flows.get(i).getChannel(), e.getMessage());
                log.error(msg, e);
                throw new RuntimeException(msg, e); // Завершение генерации с ошибкой
            }
        }

        var flowConfigClass = configClassBuilder.build();
        var javaFile = JavaFile.builder("com.wladischlau.vlt.route", flowConfigClass).build();
        try {
            javaFile.writeTo(outputDir);
        } catch (IOException e) {
            var msg = MessageFormat.format("Ошибка при записи файла конфигурации потоков маршрута: {0}",
                                           e.getMessage());
            log.error(msg, e);
            throw new UncheckedIOException(msg, e);
        }
    }

    private MethodSpec generateFlowBean(FlowDefinition flow, int idx) throws JsonProcessingException {
        var integrationFlowClass = ClassName.get("org.springframework.integration.dsl", "IntegrationFlow");

        var flowBuilder = CodeBlock.builder();
        int stepCounter = 1;
        if (flow.isSubFlow()) {
            flowBuilder.addStatement("var step$L = $T.from($S)", stepCounter, integrationFlowClass, flow.getChannel());

            for (var node : flow.getNodes()) {
                ++stepCounter;
                performGenerationStep(node, flowBuilder, stepCounter);
            }
        } else {
            var start = flow.getNodes().getFirst();
            var adapterClass = getClassFromFullyQualifiedName(start.adapter().clazz());
            var adapterClassName = ClassName.get(adapterClass);
            var configJson = configMapper.writeValueAsString(start.config());
            flowBuilder.addStatement("var step$LAdapter = new $T($S)", stepCounter, adapterClassName, configJson);
            flowBuilder.addStatement("var step$1L = step$1LAdapter.start()", stepCounter);

            for (var node : flow.getNodes().subList(1, flow.getNodes().size())) {
                ++stepCounter;
                performGenerationStep(node, flowBuilder, stepCounter);
            }
        }

        if (!flow.getSubflowChannels().isEmpty()) {
            ++stepCounter;
            var adapterClass = ClassName.get(DividerAdapter.class);
            var configJson = configMapper.writeValueAsString(Map.of("subFlowChannels", flow.getSubflowChannels()));
            flowBuilder.addStatement("var step$LAdapter = new $T($S)", stepCounter, adapterClass, configJson);
            flowBuilder.addStatement("var step$1L = step$1LAdapter.apply(step$2L)", stepCounter, stepCounter - 1);
        }

        flowBuilder.addStatement("return step$L.get()", stepCounter);

        var flowBeanName = "integrationFlow" + (flow.isSubFlow() ? "Branch_" : "_") + idx;
        var beanClass = ClassName.get("org.springframework.context.annotation", "Bean");
        return MethodSpec.methodBuilder(flowBeanName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(beanClass).build())
                .returns(ClassName.get("org.springframework.integration.dsl", "IntegrationFlow"))
                .addCode(flowBuilder.build())
                .build();
    }

    private void performGenerationStep(Node node, CodeBlock.Builder flowBuilder, int stepCounter) throws JsonProcessingException {
        var currAdapterClass = getClassFromFullyQualifiedName(node.adapter().clazz());
        var currAdapterClassName = ClassName.get(currAdapterClass);
        var currAdapterConfigJson = configMapper.writeValueAsString(node.config());
        flowBuilder.addStatement("var step$LAdapter = new $T($S)",
                                 stepCounter, currAdapterClassName, currAdapterConfigJson);
        flowBuilder.addStatement("var step$1L = step$1LAdapter.apply(step$2L)", stepCounter, stepCounter - 1);
    }

    private List<FlowDefinition> toIntegrationFlows(RouteDefinition routeDefinition) {
        var branches = branchExtractor.extractBranches(routeDefinition);
        var flows = branches.stream()
                .map(branch -> FlowDefinition.builder()
                        .channel(generateUniqueChannelName(branch.getNodes().getFirst()))
                        .parent(branch.getParent())
                        .nodes(branch.getNodes())
                        .subflowChannels(new HashSet<>())
                        .build())
                .toList();

        // Вычисление каналов дочерних flow
        flows.forEach(flow -> flows.stream()
                .filter(it -> !Objects.equals(it.getChannel(), flow.getChannel()) && it.isSubFlow())
                .filter(it -> it.getParent().equals(flow.getNodes().getLast()))
                .map(FlowDefinition::getChannel)
                .forEach(flow.getSubflowChannels()::add));

        return flows;
    }

    private String generateUniqueChannelName(@NotNull Node startNode) {
        return startNode.adapter().name() + "_" + UUID.randomUUID();
    }

    private Class<?> getClassFromFullyQualifiedName(String fullyQualifiedName) {
        try {
            return Class.forName(fullyQualifiedName);
        } catch (ClassNotFoundException e) {
            var msg = MessageFormat.format("Unable to find class [name: {0}]", fullyQualifiedName);
            log.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }
}