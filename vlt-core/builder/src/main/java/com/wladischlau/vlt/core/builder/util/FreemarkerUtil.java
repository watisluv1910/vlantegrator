package com.wladischlau.vlt.core.builder.util;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

@Slf4j
@Service
public class FreemarkerUtil {

    public static final String TEMPLATES_PATH = "/route/templates";
    private Configuration config;

    @PostConstruct
    public void init() {
        config = new Configuration(Configuration.VERSION_2_3_34);
        config.setClassForTemplateLoading(this.getClass(), TEMPLATES_PATH);
        config.setDefaultEncoding(StandardCharsets.UTF_8.name());
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        config.setLogTemplateExceptions(true);
        config.setWrapUncheckedExceptions(true);
        config.setFallbackOnNullLoopVariable(false);
    }

    /**
     * Генерация файла на основе {@code .ftl} шаблона и модели данных.
     *
     * @param templateName имя шаблона (ex. {@code pom.ftl}).
     * @param model        модель данных.
     * @param outputPath   путь, куда сохранить сгенерированный файл.
     */
    public void generateFile(String templateName, Map<String, Object> model, Path outputPath) {
        try {
            var template = config.getTemplate(templateName);
            Files.createDirectories(outputPath.getParent());
            try (var writer = Files.newBufferedWriter(outputPath, CREATE, TRUNCATE_EXISTING, WRITE)) {
                template.process(model, writer);
                log.debug("Сгенерирован файл {} на основе шаблона {}", outputPath, templateName);
            }
        } catch (IOException | TemplateException e) {
            log.error("Ошибка генерации файла по шаблону {}: {}", templateName, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}