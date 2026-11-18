
package com.aijob.agent.parser;

import java.io.IOException;
import java.nio.file.Path;

/**
 * 简历文件解析器统一接口。
 *
 * PDF 和 Word 解析器都实现这个接口。
 */
public interface ResumeParser {

    /**
     * 从指定文件中提取文本。
     *
     * @param filePath 文件的完整路径
     * @return 提取后的简历文本
     */
    String parse(Path filePath) throws IOException;
}