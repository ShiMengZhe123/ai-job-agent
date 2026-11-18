
package com.aijob.agent.service.impl;

import com.aijob.agent.common.ErrorCode;
import com.aijob.agent.config.FileStorageConfig;
import com.aijob.agent.dto.ResumeResponse;
import com.aijob.agent.entity.Resume;
import com.aijob.agent.exception.BusinessException;
import com.aijob.agent.parser.ResumeParser;
import com.aijob.agent.parser.ResumeParserFactory;
import com.aijob.agent.repository.ResumeRepository;
import com.aijob.agent.service.ResumeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * 简历业务实现类
 *
 * 主要功能：
 * 1. 上传 PDF / DOCX 简历
 * 2. 验证文件格式和大小
 * 3. 保存简历文件到本地
 * 4. 提取 PDF / DOCX 中的文本
 * 5. 保存简历信息到 MySQL
 * 6. 查询简历列表和详情
 * 7. 删除简历及其对应文件
 */
@Service
public class ResumeServiceImpl implements ResumeService {

    private static final Logger log =
            LoggerFactory.getLogger(ResumeServiceImpl.class);

    /**
     * 文件大小限制：10MB
     */
    private static final long MAX_FILE_SIZE =
            10L * 1024 * 1024;

    private final ResumeRepository resumeRepository;

    private final ResumeParserFactory parserFactory;

    private final FileStorageConfig fileStorageConfig;

    /**
     * 构造方法注入依赖
     */
    public ResumeServiceImpl(
            ResumeRepository resumeRepository,
            ResumeParserFactory parserFactory,
            FileStorageConfig fileStorageConfig) {

        this.resumeRepository = resumeRepository;
        this.parserFactory = parserFactory;
        this.fileStorageConfig = fileStorageConfig;
    }

    /**
     * 上传、解析并保存简历
     */
    @Override
    public ResumeResponse uploadResume(MultipartFile file) {

        // ============================
        // 1. 校验上传文件
        // ============================

        if (file == null || file.isEmpty()) {
            throw new BusinessException(
                    ErrorCode.FILE_UPLOAD_ERROR
            );
        }

        // 文件大小检查
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(
                    1001,
                    "文件大小不能超过10MB"
            );
        }

        // 获取原始文件名
        String originalName = file.getOriginalFilename();

        if (originalName == null || originalName.isBlank()) {
            throw new BusinessException(
                    1001,
                    "上传文件名不能为空"
            );
        }

        // 防止文件名中携带客户端路径
        originalName = originalName.replace('\\', '/');

        originalName = originalName.substring(
                originalName.lastIndexOf('/') + 1
        );

        if (originalName.isBlank()) {
            throw new BusinessException(
                    1001,
                    "上传文件名不能为空"
            );
        }

        if (originalName.length() > 255) {
            throw new BusinessException(
                    1001,
                    "文件名长度不能超过255个字符"
            );
        }

        // ============================
        // 2. 检查文件扩展名
        // ============================

        String lowerName =
                originalName.toLowerCase(Locale.ROOT);

        String extension;

        if (lowerName.endsWith(".pdf")) {

            extension = ".pdf";

        } else if (lowerName.endsWith(".docx")) {

            extension = ".docx";

        } else {

            throw new BusinessException(
                    ErrorCode.FILE_TYPE_ERROR
            );
        }

        // ============================
        // 3. 获取上传目录
        // ============================

        Path directory = fileStorageConfig
                .getUploadPath()
                .toAbsolutePath()
                .normalize();

        // 使用 UUID 避免同名文件覆盖
        String storedFileName =
                UUID.randomUUID() + extension;

        // 构建最终保存路径
        Path target = directory
                .resolve(storedFileName)
                .normalize();

        // 防止文件路径越界
        if (!target.startsWith(directory)) {
            throw new BusinessException(
                    1001,
                    "非法文件保存路径"
            );
        }

        // ============================
        // 4. 开始处理文件
        // ============================

        try {

            // 创建上传目录
            Files.createDirectories(directory);

            // ============================
            // 5. 保存文件到本地
            // ============================

            /*
             * 重要修复：
             *
             * Files.copy(InputStream, Path)
             * 不能使用 COPY_ATTRIBUTES。
             *
             * 直接复制输入流即可。
             */
            try (InputStream input = file.getInputStream()) {

                Files.copy(input, target);

            }

            log.info(
                    "简历文件保存成功：{}",
                    target
            );

            // ============================
            // 6. 验证真实文件格式
            // ============================

            validateFileSignature(
                    target,
                    extension
            );

            // ============================
            // 7. 选择解析器
            // ============================

            ResumeParser parser =
                    parserFactory.getParser(originalName);

            // ============================
            // 8. 提取简历文字
            // ============================

            String content = parser.parse(target);

            if (content == null || content.isBlank()) {

                throw new BusinessException(
                        ErrorCode.FILE_PARSE_ERROR
                );
            }

            log.info(
                    "简历解析成功，文件名={}，文本长度={}",
                    originalName,
                    content.length()
            );

            // ============================
            // 9. 创建 Resume 实体
            // ============================

            Resume resume = new Resume();

            resume.setFileName(originalName);

            resume.setFilePath(target.toString());

            resume.setFileType(
                    extension.substring(1)
            );

            resume.setContent(content);

            /*
             * 技能信息后续由 AI 提取。
             * 当前阶段保持 null。
             */
            resume.setSkills(null);

            // ============================
            // 10. 保存到 MySQL
            // ============================

            Resume saved =
                    resumeRepository.saveAndFlush(resume);

            log.info(
                    "简历数据库保存成功，ID={}，文件名={}",
                    saved.getId(),
                    originalName
            );

            // ============================
            // 11. 返回上传结果
            // ============================

            return ResumeResponse.fromEntity(saved);

        } catch (Exception e) {

            // ============================
            // 12. 失败时清理上传文件
            // ============================

            try {

                Files.deleteIfExists(target);

            } catch (IOException cleanupError) {

                log.warn(
                        "清理上传文件失败：{}",
                        target,
                        cleanupError
                );
            }

            // 已经是业务异常，直接继续抛出
            if (e instanceof BusinessException) {

                throw (BusinessException) e;
            }

            // 记录真正的异常原因
            log.error(
                    "简历上传处理失败，文件名={}",
                    originalName,
                    e
            );

            throw new BusinessException(
                    1003,
                    "简历处理失败，请确认文件格式及数据库状态"
            );
        }
    }

    /**
     * 验证文件头
     *
     * PDF 文件头：%PDF-
     * DOCX 文件头：PK ZIP 格式
     */
    private void validateFileSignature(
            Path path,
            String extension) throws IOException {

        byte[] header = new byte[5];

        // 读取文件头前 5 字节
        try (InputStream input = Files.newInputStream(path)) {

            int offset = 0;

            while (offset < header.length) {

                int count = input.read(
                        header,
                        offset,
                        header.length - offset
                );

                if (count == -1) {
                    break;
                }

                offset += count;
            }

            if (offset < header.length) {

                throw new IOException(
                        "文件内容不完整，无法识别格式"
                );
            }
        }

        // ============================
        // PDF 文件验证
        // ============================

        if (".pdf".equals(extension)) {

            boolean validPdf =
                    header[0] == '%' &&
                            header[1] == 'P' &&
                            header[2] == 'D' &&
                            header[3] == 'F' &&
                            header[4] == '-';

            if (!validPdf) {

                throw new IOException(
                        "文件内容不是有效的PDF格式"
                );
            }
        }

        // ============================
        // DOCX 文件验证
        // ============================

        else if (".docx".equals(extension)) {

            boolean validDocx =
                    header[0] == 'P' &&
                            header[1] == 'K' &&
                            header[2] == 3 &&
                            header[3] == 4;

            if (!validDocx) {

                throw new IOException(
                        "文件内容不是有效的DOCX格式"
                );
            }
        }
    }

    /**
     * 查询全部简历
     */
    @Override
    public List<ResumeResponse> listResumes() {

        return resumeRepository.findAll()
                .stream()
                .map(ResumeResponse::fromEntity)
                .toList();
    }

    /**
     * 根据 ID 查询简历
     */
    @Override
    public ResumeResponse getResumeById(Long id) {

        Resume resume = resumeRepository
                .findById(id)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.RESUME_NOT_FOUND
                        )
                );

        return ResumeResponse.fromEntity(resume);
    }

    /**
     * 删除简历
     */
    @Override
    public void deleteResume(Long id) {

        // ============================
        // 1. 查询数据库记录
        // ============================

        Resume resume = resumeRepository
                .findById(id)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.RESUME_NOT_FOUND
                        )
                );

        String filePath = resume.getFilePath();

        // ============================
        // 2. 删除数据库记录
        // ============================

        resumeRepository.delete(resume);

        resumeRepository.flush();

        // ============================
        // 3. 删除对应文件
        // ============================

        if (filePath != null && !filePath.isBlank()) {

            try {

                Path directory = fileStorageConfig
                        .getUploadPath()
                        .toAbsolutePath()
                        .normalize();

                Path path = Paths.get(filePath)
                        .toAbsolutePath()
                        .normalize();

                // 仅允许删除上传目录内的文件
                if (path.startsWith(directory)) {

                    Files.deleteIfExists(path);

                } else {

                    log.warn(
                            "文件不在上传目录，跳过删除：{}",
                            path
                    );
                }

            } catch (IOException e) {

                log.warn(
                        "数据库记录已删除，但本地文件清理失败",
                        e
                );
            }
        }

        log.info(
                "简历删除成功，ID={}",
                id
        );
    }
}