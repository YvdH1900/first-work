<template>
  <div class="ai-assistant">
    <!-- 悬浮球 -->
    <div
      v-if="!showChat"
      class="ai-float-ball"
      @click="toggleChat"
      title="点击打开AI助手"
    >
      <svg class="ai-icon" viewBox="0 0 1024 1024" width="28" height="28">
        <path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z" fill="currentColor"/>
        <path d="M512 140c-205.4 0-372 166.6-372 372s166.6 372 372 372 372-166.6 372-372-166.6-372-372-372zm-230 372c0-44.2 35.8-80 80-80s80 35.8 80 80-35.8 80-80 80-80-35.8-80-80zm460 0c0-44.2-35.8-80-80-80s-80 35.8-80 80 35.8 80 80 80 80-35.8 80-80z" fill="currentColor"/>
      </svg>
      <span class="ball-text">AI</span>
      <div class="pulse-ring"></div>
    </div>

    <!-- 对话窗口 -->
    <transition name="chat-slide">
      <div v-if="showChat" class="ai-chat-window">
        <!-- 窗口头部 -->
        <div class="chat-header">
          <div class="header-left">
            <svg class="header-icon" viewBox="0 0 1024 1024" width="20" height="20">
              <path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z" fill="currentColor"/>
              <path d="M512 140c-205.4 0-372 166.6-372 372s166.6 372 372 372 372-166.6 372-372-166.6-372-372-372zm-230 372c0-44.2 35.8-80 80-80s80 35.8 80 80-35.8 80-80 80-80-35.8-80-80zm460 0c0-44.2-35.8-80-80-80s-80 35.8-80 80 35.8 80 80 80 80-35.8 80-80z" fill="currentColor"/>
            </svg>
            <span>AI经营助手</span>
          </div>
          <div class="header-right">
            <span class="close-btn" @click="toggleChat">
              <svg viewBox="0 0 1024 1024" width="16" height="16">
                <path d="M557.3 512l221.4-221.4c12.5-12.5 12.5-32.8 0-45.3s-32.8-12.5-45.3 0L512 466.7 290.6 245.3c-12.5-12.5-32.8-12.5-45.3 0s-12.5 32.8 0 45.3L466.7 512 245.3 733.4c-12.5 12.5-12.5 32.8 0 45.3 6.2 6.2 14.4 9.4 22.6 9.4s16.4-3.1 22.6-9.4L512 557.3l221.4 221.4c6.2 6.2 14.4 9.4 22.6 9.4s16.4-3.1 22.6-9.4c12.5-12.5 12.5-32.8 0-45.3L557.3 512z" fill="currentColor"/>
              </svg>
            </span>
          </div>
        </div>

        <!-- 会话管理栏 -->
        <div class="session-bar">
          <div class="session-bar-left">
            <span class="session-btn" @click="showSessionList = !showSessionList" title="历史会话">
              <svg viewBox="0 0 1024 1024" width="14" height="14">
                <path d="M170.666667 298.666667h682.666666a42.666667 42.666667 0 1 0 0-85.333334H170.666667a42.666667 42.666667 0 0 0 0 85.333334z m0 256h682.666666a42.666667 42.666667 0 1 0 0-85.333334H170.666667a42.666667 42.666667 0 0 0 0 85.333334z m0 256h682.666666a42.666667 42.666667 0 1 0 0-85.333334H170.666667a42.666667 42.666667 0 1 0 0 85.333334z" fill="currentColor"/>
              </svg>
              <span>会话</span>
            </span>
            <span class="new-session-btn-inline" @click="createNewSession" title="新建会话">
              <svg viewBox="0 0 1024 1024" width="14" height="14">
                <path d="M512 85.333333c-235.733333 0-426.666667 190.933333-426.666667 426.666667s190.933333 426.666667 426.666667 426.666667 426.666667-190.933333 426.666667-426.666667S747.733333 85.333333 512 85.333333z m170.666667 384h-128v128c0 23.466667-19.2 42.666667-42.666667 42.666667s-42.666667-19.2-42.666667-42.666667v-128h-128c-23.466667 0-42.666667-19.2-42.666667-42.666667s19.2-42.666667 42.666667-42.666667h128v-128c0-23.466667 19.2-42.666667 42.666667-42.666667s42.666667 19.2 42.666667 42.666667v128h128c23.466667 0 42.666667 19.2 42.666667 42.666667s-19.2 42.666667-42.666667 42.666667z" fill="currentColor"/>
              </svg>
              <span>新建</span>
            </span>
          </div>
          <div class="current-session-name" v-if="currentSessionName">
            {{ currentSessionName }}
          </div>
        </div>

        <!-- 会话列表面板 -->
        <div v-if="showSessionList" class="session-panel">
          <div class="session-list">
            <div
              v-for="session in sessions"
              :key="session.sessionId"
              :class="['session-item', { active: session.sessionId === sessionId }]"
              @click="switchSession(session.sessionId)"
            >
              <span class="session-name">{{ session.sessionName }}</span>
              <span class="delete-btn" @click.stop="deleteSession(session.sessionId)">×</span>
            </div>
            <div v-if="sessions.length === 0" class="empty-tip">暂无历史会话</div>
          </div>
        </div>

        <!-- 消息列表 -->
        <div class="chat-messages" ref="messageContainer">
          <div
            v-for="(msg, index) in messages"
            :key="index"
            :class="['message', msg.type === 'user' ? 'user-message' : 'ai-message']"
          >
            <div v-if="msg.type === 'ai'" class="avatar ai-avatar">
              <svg viewBox="0 0 1024 1024" width="24" height="24">
                <path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z" fill="currentColor"/>
                <path d="M512 140c-205.4 0-372 166.6-372 372s166.6 372 372 372 372-166.6 372-372-166.6-372-372-372zm-230 372c0-44.2 35.8-80 80-80s80 35.8 80 80-35.8 80-80 80-80-35.8-80-80zm460 0c0-44.2-35.8-80-80-80s-80 35.8-80 80 35.8 80 80 80 80-35.8 80-80z" fill="currentColor"/>
              </svg>
            </div>
            <div class="message-content">
              <div :class="['message-bubble', msg.type === 'user' ? 'user-bubble' : 'ai-bubble']">
                {{ msg.content }}
              </div>
            </div>
            <div v-if="msg.type === 'user'" class="avatar user-avatar">
              <svg viewBox="0 0 1024 1024" width="24" height="24">
                <path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z" fill="currentColor"/>
                <path d="M512 140c-205.4 0-372 166.6-372 372s166.6 372 372 372 372-166.6 372-372-166.6-372-372-372zm-230 372c0-44.2 35.8-80 80-80s80 35.8 80 80-35.8 80-80 80-80-35.8-80-80zm460 0c0-44.2-35.8-80-80-80s-80 35.8-80 80 35.8 80 80 80 80-35.8 80-80z" fill="currentColor"/>
              </svg>
            </div>
          </div>
          
          <!-- 加载提示 -->
          <div v-if="loading" class="message ai-message">
            <div class="avatar ai-avatar">
              <svg viewBox="0 0 1024 1024" width="24" height="24">
                <path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z" fill="currentColor"/>
                <path d="M512 140c-205.4 0-372 166.6-372 372s166.6 372 372 372 372-166.6 372-372-166.6-372-372-372zm-230 372c0-44.2 35.8-80 80-80s80 35.8 80 80-35.8 80-80 80-80-35.8-80-80zm460 0c0-44.2-35.8-80-80-80s-80 35.8-80 80 35.8 80 80 80 80-35.8 80-80z" fill="currentColor"/>
              </svg>
            </div>
            <div class="message-content">
              <div class="message-bubble ai-bubble loading-bubble">
                <span class="loading-dot"></span>
                <span class="loading-dot"></span>
                <span class="loading-dot"></span>
              </div>
            </div>
          </div>
        </div>

        <!-- 快捷问题 -->
        <div class="quick-questions">
          <span class="quick-label">快捷提问：</span>
          <div class="quick-tags">
            <span class="quick-tag" @click="sendQuickMessage('今天营业额多少？')">今日营收</span>
            <span class="quick-tag" @click="sendQuickMessage('哪些菜品销量最好？')">热销菜品</span>
            <span class="quick-tag" @click="sendQuickMessage('本月订单趋势如何？')">订单趋势</span>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="chat-input">
          <input
            v-model="inputMessage"
            class="input-box"
            placeholder="输入您的问题..."
            @keyup.enter="sendMessage"
          />
          <button class="send-btn" @click="sendMessage" :disabled="!inputMessage.trim()">
            <svg viewBox="0 0 1024 1024" width="18" height="18">
              <path d="M931.4 498.4L115.5 17.3c-32.1-18.9-72.3 4.3-72.3 41.6v192.6c0 17.7 9.6 33.9 25.1 42.4l670.9 372.5c30.3 16.8 30.3 60.2 0 77L68.3 915.8c-15.5 8.6-25.1 24.8-25.1 42.4v192.6c0 37.3 40.2 60.5 72.3 41.6l815.9-481.1c32.1-18.8 32.1-65.1 0-83.9z" fill="currentColor"/>
            </svg>
          </button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import { sendAIQuestion, getAIHistory, getAISessions, deleteAISession } from '@/api/ai'

export default {
  name: 'AIAssistant',
  data() {
    return {
      showChat: false,
      inputMessage: '',
      messages: [
        {
          type: 'ai',
          content: '您好！我是AI经营助手，可以帮您查询经营数据、分析销售趋势、提供优化建议。请问有什么可以帮您的？'
        }
      ],
      loading: false,
      userId: 1,
      sessionId: '',
      sessions: [],
      showSessionList: false
    }
  },
  computed: {
    currentSessionName() {
      const session = this.sessions.find(s => s.sessionId === this.sessionId)
      return session ? session.sessionName : ''
    }
  },
  methods: {
    generateSessionId() {
      return 'session_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
    },
    async toggleChat() {
      this.showChat = !this.showChat
      if (this.showChat) {
        await this.loadSessions()
      }
    },
    async loadSessions() {
      try {
        const response = await getAISessions({ userId: this.userId })
        this.sessions = response.data.data || []
        if (this.sessions.length > 0) {
          this.sessionId = this.sessions[0].sessionId
          await this.loadHistory(this.sessionId)
        } else {
          this.sessionId = this.generateSessionId()
        }
      } catch (error) {
        console.error('加载会话列表失败', error)
        this.sessionId = this.generateSessionId()
      }
    },
    async loadHistory(sessionId) {
      try {
        const response = await getAIHistory({ userId: this.userId, sessionId })
        const history = response.data.data || []
        if (history.length > 0) {
          this.messages = history.map(msg => ({
            type: msg.role === 'user' ? 'user' : 'ai',
            content: msg.content
          }))
        } else {
          this.messages = [
            {
              type: 'ai',
              content: '您好！我是AI经营助手，可以帮您查询经营数据、分析销售趋势、提供优化建议。请问有什么可以帮您的？'
            }
          ]
        }
      } catch (error) {
        console.error('加载历史对话失败', error)
      }
    },
    async switchSession(sessionId) {
      this.sessionId = sessionId
      await this.loadHistory(sessionId)
      this.showSessionList = false
    },
    async createNewSession() {
      const newSessionId = this.generateSessionId()
      const now = new Date()
      const dateStr = `${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`
      this.sessions.unshift({
        sessionId: newSessionId,
        sessionName: `新会话 ${dateStr}`
      })
      this.sessionId = newSessionId
      this.messages = [
        {
          type: 'ai',
          content: '您好！我是AI经营助手，可以帮您查询经营数据、分析销售趋势、提供优化建议。请问有什么可以帮您的？'
        }
      ]
      this.showSessionList = false
    },
    async deleteSession(sessionId) {
      try {
        await deleteAISession({ userId: this.userId, sessionId })
        if (this.sessionId === sessionId) {
          await this.loadSessions()
        } else {
          this.sessions = this.sessions.filter(s => s.sessionId !== sessionId)
        }
      } catch (error) {
        console.error('删除会话失败', error)
      }
    },
    async sendMessage() {
      if (!this.inputMessage.trim()) return
      
      const userMessage = this.inputMessage.trim()
      this.messages.push({ type: 'user', content: userMessage })
      this.inputMessage = ''
      this.loading = true
      
      this.$nextTick(() => {
        this.scrollToBottom()
      })
      
      try {
        const historyMessages = this.messages.slice(-20)
        
        const response = await sendAIQuestion({ 
          question: userMessage,
          userId: this.userId,
          sessionId: this.sessionId,
          history: historyMessages.map(msg => ({
            role: msg.type === 'user' ? 'user' : 'assistant',
            content: msg.content
          }))
        })
        const answer = response.data.data.answer
        this.messages.push({ type: 'ai', content: answer })
      } catch (error) {
        this.messages.push({ 
          type: 'ai', 
          content: '抱歉，服务暂时不可用，请稍后再试。' 
        })
      } finally {
        this.loading = false
        this.$nextTick(() => {
          this.scrollToBottom()
        })
      }
    },
    sendQuickMessage(msg) {
      this.inputMessage = msg
      this.sendMessage()
    },
    scrollToBottom() {
      const container = this.$refs.messageContainer
      if (container) {
        container.scrollTop = container.scrollHeight
      }
    }
  }
}
</script>

<style scoped>
.ai-assistant {
  position: fixed;
  bottom: 30px;
  right: 30px;
  z-index: 9999;
}

.ai-float-ball {
  position: relative;
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  animation: float 3s ease-in-out infinite;
}

.ai-float-ball:hover {
  transform: scale(1.1);
  box-shadow: 0 8px 28px rgba(102, 126, 234, 0.7);
}

.ai-icon {
  margin-bottom: 2px;
}

.ball-text {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.pulse-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  border: 2px solid rgba(102, 126, 234, 0.6);
  animation: pulse 2s ease-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-8px);
  }
}

@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 1;
  }
  100% {
    transform: scale(1.5);
    opacity: 0;
  }
}

.chat-slide-enter-active,
.chat-slide-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.chat-slide-enter {
  transform: translateY(20px) scale(0.95);
  opacity: 0;
}

.chat-slide-leave-to {
  transform: translateY(20px) scale(0.95);
  opacity: 0;
}

.ai-chat-window {
  width: 400px;
  height: 560px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  margin-bottom: 10px;
}

.chat-header {
  padding: 18px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 600;
}

.header-icon {
  opacity: 0.9;
}

.close-btn {
  cursor: pointer;
  opacity: 0.8;
  transition: opacity 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 50%;
}

.close-btn:hover {
  opacity: 1;
  background: rgba(255, 255, 255, 0.2);
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: linear-gradient(180deg, #f8f9fa 0%, #f0f2f5 100%);
}

.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #c1c9d4;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #a8b3c1;
}

.message {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.user-message {
  flex-direction: row-reverse;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.ai-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.user-avatar {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  color: white;
}

.message-content {
  flex: 1;
  max-width: 80%;
}

.user-message .message-content {
  display: flex;
  justify-content: flex-end;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.6;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.ai-bubble {
  border-top-left-radius: 4px;
  background: white;
  color: #333;
}

.user-bubble {
  border-top-right-radius: 4px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.loading-bubble {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px 20px;
}

.loading-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #667eea;
  animation: loading 1.4s ease-in-out infinite;
}

.loading-dot:nth-child(1) {
  animation-delay: 0s;
}

.loading-dot:nth-child(2) {
  animation-delay: 0.2s;
}

.loading-dot:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes loading {
  0%, 80%, 100% {
    transform: scale(0.6);
    opacity: 0.4;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.quick-questions {
  padding: 12px 20px;
  background: white;
  border-top: 1px solid #f0f2f5;
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.quick-label {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
}

.quick-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.quick-tag {
  padding: 6px 12px;
  background: #f0f2f5;
  border-radius: 16px;
  font-size: 12px;
  color: #667eea;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.quick-tag:hover {
  background: #667eea;
  color: white;
}

.chat-input {
  padding: 16px 20px;
  background: white;
  border-top: 1px solid #f0f2f5;
  display: flex;
  gap: 12px;
  align-items: center;
}

.input-box {
  flex: 1;
  padding: 10px 16px;
  border: 1px solid #e4e7ed;
  border-radius: 24px;
  font-size: 14px;
  outline: none;
  transition: all 0.2s;
}

.input-box:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.send-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  flex-shrink: 0;
}

.send-btn:hover:not(:disabled) {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.session-bar {
  padding: 8px 16px;
  background: #f8f9fa;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.session-bar-left {
  display: flex;
  gap: 8px;
}

.session-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  font-size: 12px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.session-btn:hover {
  background: #f0f4ff;
  border-color: #667eea;
  color: #667eea;
}

.new-session-btn-inline {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  font-size: 12px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.new-session-btn-inline:hover {
  background: #f0f4ff;
  border-color: #667eea;
  color: #667eea;
}

.current-session-name {
  font-size: 12px;
  color: #999;
  max-width: 180px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.session-panel {
  position: absolute;
  top: 96px;
  left: 16px;
  right: 16px;
  max-height: 200px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 50;
  overflow: hidden;
  animation: slideDown 0.2s ease-out;
}

.session-list {
  max-height: 200px;
  overflow-y: auto;
  padding: 8px 0;
}

.session-list::-webkit-scrollbar {
  width: 6px;
}

.session-list::-webkit-scrollbar-thumb {
  background: #e0e0e0;
  border-radius: 3px;
}

.session-list::-webkit-scrollbar-thumb:hover {
  background: #c0c0c0;
}

.session-item {
  padding: 12px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.session-item:hover {
  background: #f5f7ff;
}

.session-item:hover .delete-btn {
  opacity: 1;
}

.session-item.active {
  background: linear-gradient(90deg, #f0f4ff 0%, #fafbff 100%);
  color: #667eea;
}

.session-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
}

.session-name {
  font-size: 13px;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  padding-right: 8px;
}

.session-time {
  font-size: 11px;
  color: #999;
  margin-right: 8px;
}

.delete-btn {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 18px;
  cursor: pointer;
  border-radius: 50%;
  transition: all 0.2s;
  opacity: 0;
  flex-shrink: 0;
}

.delete-btn:hover {
  background: #fee;
  color: #f56c6c;
  transform: scale(1.1);
}

.empty-tip {
  padding: 40px 20px;
  text-align: center;
  color: #999;
  font-size: 13px;
}

.empty-tip::before {
  content: '💬';
  display: block;
  font-size: 32px;
  margin-bottom: 8px;
}
</style>
