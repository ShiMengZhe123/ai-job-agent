
package com.aijob.agent.parser;

import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * 根据简历文件扩展名选择解析器。
 */
@Component
public class ResumeParserFactory {

    private final PdfResumeParser pdfResumeParser;
    private final WordResumeParser wordResumeParser;

    public ResumeParserFactory(
            PdfResumeParser pdfResumeParser,
            WordResumeParser wordResumeParser) {

        this.pdfResumeParser = pdfResumeParser;
        this.wordResumeParser = wordResumeParser;
    }

    public ResumeParser getParser(String fileName) {

        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException(
                    "文件名不能为空"
            );
        }

        String name = fileName.toLowerCase(Locale.ROOT);

        if (name.endsWith(".pdf")) {
            return pdfResumeParser;
        }

        if (name.endsWith(".docx")) {
            return wordResumeParser;
        }

        throw new IllegalArgumentException(
                "不支持的简历文件类型，仅支持 PDF 和 DOCX"
        );
    }
}