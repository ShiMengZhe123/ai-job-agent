
package com.aijob.agent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 简历文件存储配置
 */
@Configuration
public class FileStorageConfig {

    private final Path uploadPath;

    public FileStorageConfig(
            @Value("${app.file.upload-dir:uploads/resumes}")
            String uploadDir
    ) throws IOException {

        this.uploadPath = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();

        Files.createDirectories(this.uploadPath);
    }

    /**
     * 获取上传目录的完整路径
     */
    public Path getUploadPath() {
        return uploadPath;
    }
}