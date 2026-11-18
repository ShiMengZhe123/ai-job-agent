# AI 智能求职助手（AI Job Agent）

基于 **Spring Boot + Spring AI + Ollama + Qwen3 4B + Vue 3** 开发的本地 AI 智能求职辅助系统。

## 📖 项目简介

本项目面向求职者，提供简历上传与解析、岗位管理、岗位智能匹配、AI 求职问答和模拟面试等功能。

系统采用前后端分离架构，通过 Ollama 在本地运行 Qwen3 4B 模型，并利用 Spring AI Tool Calling 调用真实数据库中的业务数据，无需付费云端模型 API。

## 🛠 技术栈

| 模块    | 技术                              |
| ----- | ------------------------------- |
| 后端    | Java 17、Spring Boot、Spring AI   |
| 数据持久化 | MySQL、Spring Data JPA、Hibernate |
| 文档解析  | Apache PDFBox、Apache POI        |
| AI 模型 | Ollama、Qwen3 4B                 |
| 前端    | Vue 3、Vite、Element Plus、Axios   |
| 项目管理  | Maven、Git、GitHub                |

## ✨ 核心功能

* **简历管理**：上传 PDF / DOCX 简历，提取文本并保存至 MySQL。
* **岗位管理**：新增、查询、更新和删除招聘岗位。
* **智能岗位匹配**：根据简历与岗位技能要求计算关键词匹配得分，展示匹配技能与缺失技能。
* **AI 智能助手**：支持中文对话，通过 Tool Calling 查询简历、岗位和匹配结果。
* **AI 模拟面试**：根据目标岗位自动生成面试题，评价候选人回答，完成三轮面试并保存记录。
* **可视化网页**：通过 Vue 3 前端操作主要业务功能。

## 📁 项目结构

```text
ai-job-agent/
├── backend/               # Spring Boot 后端
│   ├── src/
│   ├── pom.xml
│   └── mvnw
├── frontend/              # Vue 3 前端
│   ├── src/
│   ├── public/
│   ├── package.json
│   └── vite.config.js
└── README.md
```

## 🚀 本地运行

### 1. 环境要求

* JDK 17
