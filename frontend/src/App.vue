
<script setup>
import { ref } from 'vue'

import ChatView from './views/ChatView.vue'
import InterviewView from './views/InterviewView.vue'
import ResumeView from './views/ResumeView.vue'
import JobView from './views/JobView.vue'

import {
  ChatDotRound,
  User,
  Briefcase,
  Document,
  House
} from '@element-plus/icons-vue'

// 当前选中的页面
const activePage = ref('chat')

// 切换左侧菜单
function handleMenuSelect(index) {
  activePage.value = index
}
</script>

<template>
  <el-container class="app-shell">

    <!-- 左侧导航栏 -->
    <el-aside width="245px" class="sidebar">

      <!-- 系统 Logo -->
      <div class="brand">
        <div class="brand-icon">AI</div>

        <div>
          <h2>智能求职助手</h2>
          <p>Qwen3 4B · Ollama</p>
        </div>
      </div>

      <!-- 左侧功能菜单 -->
      <el-menu
          :default-active="activePage"
          @select="handleMenuSelect"
      >

        <!-- AI 智能助手 -->
        <el-menu-item index="chat">
          <el-icon>
            <ChatDotRound />
          </el-icon>
          <span>AI 智能助手</span>
        </el-menu-item>

        <!-- AI 模拟面试 -->
        <el-menu-item index="interview">
          <el-icon>
            <User />
          </el-icon>
          <span>AI 模拟面试</span>
        </el-menu-item>

        <!-- 简历管理 -->
        <el-menu-item index="resume">
          <el-icon>
            <Document />
          </el-icon>
          <span>简历管理</span>
        </el-menu-item>

        <!-- 岗位智能匹配 -->
        <el-menu-item index="job">
          <el-icon>
            <Briefcase />
          </el-icon>
          <span>岗位智能匹配</span>
        </el-menu-item>

      </el-menu>

      <!-- 侧边栏底部 -->
      <div class="sidebar-footer">
        <span class="status-dot"></span>
        本地 AI · 无需云端 API Key
      </div>

    </el-aside>

    <!-- 右侧主要内容 -->
    <el-container>

      <!-- 顶部导航栏 -->
      <el-header class="topbar">

        <div class="topbar-title">
          <el-icon>
            <House />
          </el-icon>
          <span>AI 智能求职助手</span>
        </div>

        <el-tag
            effect="plain"
            type="success"
        >
          Qwen3 4B
        </el-tag>

      </el-header>

      <!-- 页面主体 -->
      <el-main class="main-area">

        <!-- AI 聊天页面 -->
        <ChatView
            v-show="activePage === 'chat'"
        />

        <!-- AI 模拟面试页面 -->
        <InterviewView
            v-show="activePage === 'interview'"
        />

        <!-- 简历管理页面 -->
        <ResumeView
            v-if="activePage === 'resume'"
        />

        <!-- 岗位智能匹配页面 -->
        <JobView
            v-if="activePage === 'job'"
        />

      </el-main>

    </el-container>

  </el-container>
</template>

<style scoped>

/* 整体页面 */
.app-shell {
  min-height: 100vh;
  background: #f5f7fb;
}

/* 左侧导航栏 */
.sidebar {
  position: relative;
  background: #ffffff;
  border-right: 1px solid #e9edf3;
  min-height: 100vh;
}

/* 系统品牌区域 */
.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 28px 18px;
}

/* AI Logo */
.brand-icon {
  width: 42px;
  height: 42px;
  flex-shrink: 0;

  display: flex;
  align-items: center;
  justify-content: center;

  border-radius: 12px;
  background: #3568df;

  color: white;
  font-size: 17px;
  font-weight: 700;
}

/* 系统名称 */
.brand h2 {
  font-size: 17px;
  margin: 0 0 5px;
  color: #263b5d;
}

/* AI 模型说明 */
.brand p {
  font-size: 12px;
  color: #9aa4b4;
  margin: 0;
}

/* Element Plus 导航菜单 */
.sidebar .el-menu {
  border-right: none;
}

/* 底部运行说明 */
.sidebar-footer {
  position: absolute;
  bottom: 24px;
  left: 18px;

  font-size: 12px;
  color: #8490a2;
}

/* 运行状态圆点 */
.status-dot {
  display: inline-block;

  width: 8px;
  height: 8px;

  background: #67c23a;
  border-radius: 50%;

  margin-right: 6px;
}

/* 顶部导航栏 */
.topbar {
  height: 68px;

  display: flex;
  justify-content: space-between;
  align-items: center;

  background: white;
  border-bottom: 1px solid #e9edf3;

  padding: 0 30px;
}

/* 顶部标题 */
.topbar-title {
  display: flex;
  align-items: center;
  gap: 10px;

  font-weight: 600;
  color: #344767;
}

/* 主体内容 */
.main-area {
  padding: 32px;
  min-width: 0;
  background: #f5f7fb;
}

/* 小屏幕适配 */
@media (max-width: 700px) {

  .sidebar {
    width: 190px !important;
  }

  .brand {
    padding: 18px 10px;
  }

  .brand h2 {
    font-size: 14px;
  }

  .main-area {
    padding: 15px;
  }

  .topbar {
    padding: 0 15px;
  }

}

</style>