<template>
  <div class="chat-container">
    <div class="chat-header">
      <h2>智能助手</h2>
      <div class="status" :class="{ active: isTyping }">
        {{ isTyping ? '正在输入...' : '在线' }}
      </div>
    </div>

    <div class="chat-messages" ref="messagesContainer">
      <div
        v-for="(message, index) in messages"
        :key="index"
        class="message"
        :class="{
          'user-message': message.isUser,
          'ai-message': !message.isUser
        }"
        @mouseenter="message.ishover = true"
        @mouseleave="message.ishover = false"
      >
        <div class="message-content">
          <!-- 编辑和复制部分 -->
          <div v-show="message.ishover" class="message-actions">
            <el-icon @click.stop="copymsg(message)">
              <DocumentCopy />
            </el-icon>
            <el-icon v-if="!message.isUser" @click="editPlan(message)">
              <Edit />
            </el-icon>
            <el-icon v-if="message.isUser" @click="editmsg(message)">
              <Edit />
            </el-icon>
          </div>

          <div v-if="message.isUser">{{ message.content }}</div>
          <MarkdownPreview v-else :text="message.content" />
        </div>
        <div class="message-time">
          {{ message.time }}
        </div>
      </div>
    </div>

    <div class="chat-input">
      <textarea
        v-model="inputMessage"
        @keydown.enter.exact.prevent="sendMessage"
        placeholder="输入消息..."
        ref="textarea"
        @input="adjustTextareaHeight"
      ></textarea>
      <button
        @click="sendMessage"
        :disabled="!inputMessage.trim() || isSending"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="24"
          height="24"
          viewBox="0 0 24 24"
        >
          <path fill="currentColor" d="M3 20v-6l8-2l-8-2V4l19 8z" />
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup>
import DOMPurify from 'dompurify'
import { marked } from 'marked'
import MarkdownPreview from '@/components/MarkdownPreview.vue'
import { Edit, DocumentCopy } from '@element-plus/icons-vue'
import { ref, nextTick, onMounted } from 'vue'
import { gettext } from '@/api/chat.js'
import { defineOptions } from 'vue'
import Quill from 'quill'
import { useRouter } from 'vue-router'
import { useDocStore } from '@/stores'

defineOptions({
  name: 'chat'
})
const messages = ref([])
const inputMessage = ref('')
const isTyping = ref(false)
const isSending = ref(false)
const textarea = ref(null)
const messagesContainer = ref(null)
const router = useRouter()
const docStore = useDocStore()

// 调整输入框高度
const adjustTextareaHeight = () => {
  textarea.value.style.height = 'auto'
  textarea.value.style.height = textarea.value.scrollHeight + 'px'
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  })
}

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim() || isSending.value) return

  try {
    isSending.value = true
    const userMessage = inputMessage.value.trim()
    // 如果是编辑状态，更新最后一条消息

    // 添加用户消息
    messages.value.push({
      content: userMessage,
      isUser: true,
      time: new Date().toLocaleTimeString()
    })

    inputMessage.value = ''
    adjustTextareaHeight()
    scrollToBottom()

    // 显示AI正在输入状态
    isTyping.value = true

    // 调用接口（已适配新数据结构）
    const response = await gettext(userMessage)

    // 添加AI回复（根据实际接口返回结构调整）
    messages.value.push({
      content: response || '收到空响应', // 根据实际返回字段调整
      isUser: false,
      time: new Date().toLocaleTimeString()
    })
  } catch (error) {
    console.error('请求失败:', error)
    messages.value.push({
      content: `请求失败: ${error.message}`,
      isUser: false,
      time: new Date().toLocaleTimeString()
    })
  } finally {
    isSending.value = false
    isTyping.value = false
    scrollToBottom()
  }
}

// 初始化欢迎消息
onMounted(() => {
  messages.value.push({
    content: '您好！我是智能助手，有什么可以帮您的？',
    isUser: false,
    time: new Date().toLocaleTimeString()
  })
})
onMounted(() => {
  messages.value = messages.value.map(item => {
    return {
      ...item,
      ishover: false
    }
  })
})
const isMD = text => {
  const mdPatterns = [
    /^#{1,6}\s/, // 标题
    /(\*\*|__)(.*?)\1/, // 加粗/斜体
    /^[-*+]\s/, // 列表
    /^>\s/, // 引用
    /!$$.*?$$$.*?$|$$.*?$$$.*?$/, // 图片/链接
    /```[\s\S]*?```/g, // 代码块
    /`[^`]+`/ // 行内代码
  ]
  return mdPatterns.some(pattern => pattern.test(text))
}

const copymsg = async message => {
  try {
    let content = message.content
    if (isMD(content)) {
      // 使用 DOMPurify 过滤 XSS 内容
      const sanitized = DOMPurify.sanitize(content, {
        ALLOWED_TAGS: ['ul', 'ol', 'li', ...DOMPurify.defaults.ALLOWED_TAGS]
      })
      // 生成带样式的 HTML
      const html = `
        <!DOCTYPE html>
        <html>
          <head>
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/github-markdown-css/5.1.0/github-markdown.min.css">
          </head>
          <body class="markdown-body">${marked.parse(sanitized)}</body>
        </html>
      `
      // 写入富文本格式
      await navigator.clipboard.write([
        new ClipboardItem({
          'text/html': new Blob([html], { type: 'text/html' }),
          'text/plain': new Blob([sanitized], { type: 'text/plain' })
        })
      ])
    } else {
      // 纯文本复制
      await navigator.clipboard.writeText(content)
    }
    ElMessage.success('复制成功')
  } catch (err) {
    console.error('复制失败:', err)
    ElMessage.error(`复制失败: ${err.message}`)
  }
}

const editPlan = message => {
  let content = message.content
  if (isMD(message.content)) {
    marked.setOptions({
      gfm: true,
      breaks: true,
      xhtml: true, // 确保标签闭合
      sanitize: false
    })

    // 使用自定义HTML处理
    const sanitized = DOMPurify.sanitize(content, {
      ALLOWED_TAGS: ['ul', 'ol', 'li', 'p'],
      FORBID_ATTR: ['style', 'class']
    })

    // 转换为Quill兼容的Delta格式
    const quill = new Quill(document.createElement('div'))
    quill.clipboard.dangerouslyPasteHTML(marked.parse(sanitized))

    docStore.editingMessage = {
      ...message,
      content: quill.root.innerHTML // 获取Quill处理后的HTML
    }
  }
  router.push('/create/plan')
}
const editmsg = message => {
  inputMessage.value = message.content
  textarea.value.focus()
}
</script>

<style lang="scss" scoped>
@use "sass:color";
/* 保持原有样式不变 */
$primary-color: #cce2fb;
$background-color: #f5f7fb;
$text-color: #000;

.chat-container {
  width: 100%;
  height: 100%;
  background: white;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  margin: 0;
}

.chat-header {
  padding: 14px;
  background: $primary-color;
  border-radius: 12px 12px 0 0;
  color: #000000;
  display: flex;
  justify-content: center;
  align-items: center;

  h2 {
    margin: 0;
    font-weight: 600;
  }

  .status {
    font-size: 0.9rem;
    padding: 4px 8px;
    border-radius: 16px;
    background: rgba(255, 255, 255, 0.2);

    &.active {
      background: rgba(255, 255, 255, 0.3);
    }
  }
}

.chat-messages {
  flex: 1 1 auto;
  padding: 15px;
  overflow-y: auto;
  background: $background-color;
  display: flex;
  flex-direction: column;
  gap: 1rem;

  .message {
    position: relative;
    max-width: 70%;
    padding: 12px 16px;
    border-radius: 12px;
    transition: all 0.3s ease;
    color: #000;
    &-actions {
      position: absolute;
      bottom: 0px;
      right: 0px;
      display: flex;
      gap: 8px;
      background: rgba(255, 255, 255, 0.9);
      padding: 4px;
      border-radius: 4px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      z-index: 1;

      .el-icon {
        cursor: pointer;
        padding: 4px;
        border-radius: 4px;
        transition: all 0.2s;

        &:hover {
          background: rgba(0, 0, 0, 0.05);
        }
      }
    }
    &-content {
      font-size: 1rem;
      line-height: 1.4;
    }

    &-time {
      font-size: 0.75rem;
      color: rgba($text-color, 0.6);
      margin-top: 4px;
      text-align: right;
    }

    &.user-message {
      background: $primary-color;
      // color: white;
      align-self: flex-end;
      border-radius: 12px 12px 0 12px;

      .message-time {
        color: rgba(#000, 0.8);
      }
      .message-actions {
        right: auto;
        left: 8px;
      }
    }

    &.ai-message {
      background: white;
      align-self: flex-start;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
      border-radius: 12px 12px 12px 0;
    }
  }
}

.chat-input {
  flex-shrink: 0;
  border-top: 1px solid #eee;
  padding: 1rem;
  display: flex;
  gap: 1rem;
  align-items: flex-end;

  textarea {
    flex: 1;
    min-height: 44px;
    max-height: 150px;
    padding: 10px 16px;
    border: 2px solid #e0e0e0;
    border-radius: 8px;
    resize: none;
    font-size: 1rem;
    transition: all 0.3s ease;

    &:focus {
      outline: none;
      border-color: $primary-color;
      box-shadow: 0 0 0 3px rgba($primary-color, 0.1);
    }
  }

  button {
    height: 44px;
    width: 44px;
    border: none;
    background: $primary-color;
    color: white;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      background: color.adjust($primary-color, $lightness: -10%);
    }

    &:disabled {
      background: #b0bec5;
      cursor: not-allowed;
    }
  }
}
</style>
