package com.wladischlau.vlt.core.builder.util;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileUtil {

    public String computeRelativePath(Resource resource, String basePath) throws IOException {
        var url = resource.getURL().toString();
        int idx = url.indexOf(basePath);

        if (idx < 0)
            return resource.getFilename();

        var sub = url.substring(idx + basePath.length());

        while (sub.startsWith("/"))
            sub = sub.substring(1);

        return sub;
    }

    public boolean isJavaFile(Path path) {
        return Files.isRegularFile(path) && path.toString().endsWith(".java");
    }
}
