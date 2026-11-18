
package com.aijob.agent.parser;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Word DOCX 简历解析器。
 */
@Component
public class WordResumeParser implements ResumeParser {

    @Override
    public String parse(Path filePath) throws IOException {

        // 自动关闭输入流、Word 文档及提取器
        try (var input = Files.newInputStream(filePath);
             XWPFDocument document =
                     new XWPFDocument(input);
             XWPFWordExtractor extractor =
                     new XWPFWordExtractor(document)) {

            String text = extractor.getText();

            return normalizeText(text);
        }
    }

    private String normalizeText(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replace("\r\n", "\n")
                .replace('\r', '\n')
                .replace('\u00A0', ' ')
                .replaceAll("[\\t ]+", " ")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }
}