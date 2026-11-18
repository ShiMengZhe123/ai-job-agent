
package com.aijob.agent.parser;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;

/**
 * PDF 简历文本解析器。
 */
@Component
public class PdfResumeParser implements ResumeParser {

    @Override
    public String parse(Path filePath) throws IOException {

        // PDFBox 3.x 使用 Loader 加载 PDF 文件
        try (PDDocument document =
                     Loader.loadPDF(filePath.toFile())) {

            if (document.isEncrypted()) {
                throw new IOException(
                        "暂不支持加密的 PDF 简历"
                );
            }

            PDFTextStripper stripper =
                    new PDFTextStripper();

            // 按阅读顺序尽量整理文本
            stripper.setSortByPosition(true);

            String text = stripper.getText(document);

            return normalizeText(text);
        }
    }

    /**
     * 规范化文本中的换行和空格。
     */
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