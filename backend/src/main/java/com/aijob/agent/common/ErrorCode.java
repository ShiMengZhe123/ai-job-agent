
package com.aijob.agent.common;

/**
 * 系统业务错误码
 */
public enum ErrorCode {

    SUCCESS(200, "操作成功"),

    PARAM_ERROR(400, "请求参数错误"),

    UNAUTHORIZED(401, "未授权访问"),

    NOT_FOUND(404, "资源不存在"),

    FILE_UPLOAD_ERROR(1001, "文件上传失败"),

    FILE_TYPE_ERROR(1002, "不支持的文件类型"),

    FILE_PARSE_ERROR(1003, "文件解析失败"),

    RESUME_NOT_FOUND(2001, "简历不存在"),

    JOB_NOT_FOUND(3001, "岗位不存在"),

    MATCH_ERROR(4001, "岗位匹配失败"),

    INTERVIEW_ERROR(5001, "模拟面试失败"),

    AI_SERVICE_ERROR(6001, "AI服务调用失败"),

    KNOWLEDGE_ERROR(7001, "知识库操作失败"),

    SYSTEM_ERROR(500, "系统内部错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}