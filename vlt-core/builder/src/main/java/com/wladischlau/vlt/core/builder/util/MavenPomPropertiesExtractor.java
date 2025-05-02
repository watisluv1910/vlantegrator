package com.wladischlau.vlt.core.builder.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
public class MavenPomPropertiesExtractor {

    private final Path pomFile;
    private final Map<String, Object> properties;

    public MavenPomPropertiesExtractor(Path pomFile) {
        this.pomFile = pomFile;
        this.properties = extractProperties(pomFile);
    }

    private static Map<String, Object> extractProperties(Path file) {
        var mvnReader = new MavenXpp3Reader();
        try (var fileReader = Files.newBufferedReader(file)) {
            var props = mvnReader.read(fileReader).getProperties();
            var result = new HashMap<String, Object>();
            props.stringPropertyNames().forEach(key -> result.put(key, props.getProperty(key)));
            return result;
        } catch (XmlPullParserException | IOException e) {
            log.error("Ошибка при обработке POM файла [path: {}]", file, e);
            throw new RuntimeException(e);
        }
    }
}