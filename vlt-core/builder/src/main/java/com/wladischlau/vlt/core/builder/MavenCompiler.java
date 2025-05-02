package com.wladischlau.vlt.core.builder;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

import static java.nio.file.StandardOpenOption.*;

@Slf4j
public class MavenCompiler {

    public static final String BUILD_LOG = "build.log";

    private final Path mvnw;

    public MavenCompiler(Path mvnw) {
        this.mvnw = mvnw;
    }

    public void compile(Path dir) {
        if (!Files.exists(mvnw)) {
            var msg = MessageFormat.format("Файл mvnw не найден в папке маршрута [path: {0}]", dir);
            log.error(msg);
            throw new IllegalStateException(msg);
        }

        boolean wasSetExecutable = mvnw.toFile().setExecutable(true);
        if (!wasSetExecutable) {
            var msg = MessageFormat.format("Невозможно сделать mvnw файл исполняемым [path: {0}]",
                                           mvnw.toAbsolutePath());
            log.error(msg);
            throw new IllegalStateException(msg);
        }

        log.info("Начало сборки проекта в {}", dir);
        var buildLogFile = dir.resolve(BUILD_LOG);

        if (!runMavenCommand(dir, buildLogFile, "clean", "package", "-U", "-DskipTests")) {
            var msg = MessageFormat.format("Сборка маршрута завершена с ошибкой [path: {0}]", dir);
            log.error(msg);
            return;
        }

        log.info("Сборка проекта завершена успешно [path: {}]", dir);

        if (!runMavenCommand(dir, buildLogFile, "clean")) {
            var msg = MessageFormat.format("Очистка маршрута завершена с ошибкой [path: {0}]", dir);
            log.warn(msg);
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean runMavenCommand(Path dir, Path buildLogFile, String... args) {
        var cmd = new ArrayList<String>();
        cmd.add(mvnw.toAbsolutePath().toString());
        cmd.addAll(Arrays.asList(args));

        var pb = new ProcessBuilder(cmd);
        pb.directory(dir.toFile());
        pb.redirectErrorStream(true);

        log.debug("Запуск {} в директории {}", cmd, dir);

        try {
            var process = pb.start();
            try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                 var writer = Files.newBufferedWriter(buildLogFile, CREATE, APPEND)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.debug("[BUILD: {}] {}", dir, line);
                    writer.write(line);
                    writer.newLine();
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                var msg = MessageFormat.format("Выполнение команды {0} завершено с ошибкой [path: {1}, exitCode: {2}, buildLog: {3}]",
                                               cmd, dir, exitCode, buildLogFile);
                log.error(msg);
                return false;
            }
            return true;
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            var msg = MessageFormat.format("Ошибка при выполнении maven команды: {0} [path: {1}]", cmd, dir);
            log.error(msg, e);
            return false;
        }
    }
}