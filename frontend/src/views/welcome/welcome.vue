<template>
    <header class="header">
        <div class="logo">智教备课助手</div>
        <nav class="nav-main">
            <a href="#" class="nav-item">产品与服务</a>
            
            <!-- 服务角色下拉 -->
            <div class="dropdown" @mouseenter="showRoles = true" @mouseleave="showRoles = false">
                <a href="#" class="nav-item" >服务角色</a>
                <div v-show="showRoles" class="dropdown-menu">
                    <div >教育从事者</div>
                    <div href="#">学校管理者</div>
                    <div href="#">教师</div>
                </div>
            </div>

            <!-- 教育智库下拉 -->
            <div class="dropdown" @mouseenter="showEdu = true" @mouseleave="showEdu = false">
                <a href="#" class="nav-item">教育智库</a>
                <div v-show="showEdu" class="dropdown-menu">
                    <div href="#">智慧教案</div>
                    <div href="#">多媒体生成</div>
                    <div href="#">练习题库</div>
                    <div href="#">资源推荐</div>
                </div>
            </div>

            <a href="#" @click="goCreateplan" class="nav-item">进入备课</a>
            <a href="#" class="nav-item">关于我们</a>
            <a href="#" class="nav-item">联系我们</a>
        </nav>
    </header>
    <div class="banner">
        <div class="banner-area">
            <h1>智教备课助手：ai辅助备课</h1>
        <p>全面落实核心素养</p>
        </div>
        
    </div>
    <div class="content">
        <h2>智教备课助手</h2>
        <p>智教备课助手是一款基于大数据分析的在线教育平台，旨在通过数据驱动的方式，为教师提供个性化的教学服务。</p>
        <p>聚焦课堂教学，以四大核心价值助力教师轻松备授课：课堂教学资源（资源全、资源新、资源新），互动教学工具（多学科、情境化、趣味性），创新测试形式（趣味多元、素养导向），学情分析（个性化，具体化）</p>
        <img src="../../assets/智能备课.png" alt="">
        <h2>应用价值</h2>
        <ul>
            <li @click="scrollToSection('efficient')">高效备课</li>
            <li @click="scrollToSection('smart')">智能备课</li>
            <li @click="scrollToSection('personalized')">个性化教学</li>
            <li @click="scrollToSection('diagnosis')">查漏补缺</li>
        </ul>

        <div class="content-item" id="efficient">
  <div class="media-column">
    <img src="../../assets/高效备课.png" alt="备课工具界面：展示形状、表格、媒体等创作选项">
  </div>
  <div class="text-column">
    <h3>高效备课</h3>
    <div class="feature-tags">
      <span class="tag blue">新课标同步</span>
      <span class="tag green">课件创作</span>
      <span class="tag purple">资源管理</span>
    </div>
    <p>提供智能图表、思维导图等20+工具，支持：</p>
    <ul class="function-list">
      <li><el-icon><Document /></el-icon> 5分钟快速生成课件框架</li>
      <li><el-icon><DataAnalysis /></el-icon> 同步最新课程标准资源库</li>
      <li><el-icon><Share /></el-icon> 跨学科素材一键调用</li>
    </ul>
  </div>
</div>

        <!-- 智能备课模块 - 右图左文 -->
        <div class="content-item " id="smart">
        <div class="text-column">
            <h3>智能备课</h3>
            <div class="feature-tags">
            <span class="tag orange">AI教案优化</span>
            <span class="tag pink">互动课件</span>
            </div>
            <p>智能组件库支持：</p>
            <ul class="function-list">
            <li><el-icon><MagicStick /></el-icon> 根据学情自动调整教案难度</li>
            <li><el-icon><VideoPlay /></el-icon> 嵌入交互式测评组件</li>
            <li><el-icon><Present /></el-icon> 生成符合认知规律的PPT动画</li>
            </ul>
        </div>
        <div class="media-column">
            <img src="../../assets/智能备课2.png" alt="智能备课界面：展示AI建议和互动组件面板">
        </div>
        </div>


        <div class="content-item" id="personalized">
        <div class="media-column">
            <img src="../../assets/个性化教学.jpg" alt="分层教学界面：展示学生能力雷达图与推荐资源">
        </div>
        <div class="text-column">
            <h3>个性化教学</h3>
            <div class="feature-tags">
            <span class="tag teal">分层教学</span>
            <span class="tag purple">学习路径规划</span>
            </div>
            <p>智能引擎支持：</p>
            <ul class="function-list">
            <li><el-icon><User /></el-icon> 基于学情画像定制学习路径</li>
            <li><el-icon><PieChart /></el-icon> 动态调整教学资源难度层级</li>
            <li><el-icon><Connection /></el-icon> 多模态资源智能匹配(视频/题库/教具)</li>
            </ul>
        </div>
        </div>

        <!-- 查漏补缺模块 - 左图右文 -->
        <div class="content-item" id="diagnosis">
        <div class="media-column">
            <img src="../../assets/习题.png" alt="学情分析面板：显示知识点掌握度热力图">
        </div>
        <div class="text-column">
            <h3>查漏补缺</h3>
            <div class="feature-tags">
            <span class="tag red">薄弱点诊断</span>
            <span class="tag orange">靶向练习</span>
            </div>
            <p>智能诊断系统提供：</p>
            <ul class="function-list">
            <li><el-icon><Warning /></el-icon> 知识点掌握度热力图分析</li>
            <li><el-icon><Aim /></el-icon> 错题归因与同类题推荐</li>
            <li><el-icon><DataLine /></el-icon> 阶段性学习进展追踪报告</li>
            </ul>
        </div>
        </div>

        <h2>理论指引</h2>
        <p>智教备课助手通过大数据分析，为教师提供个性化的教学服务，帮助教师更好地理解学生的学习情况，从而更好地因材施教。</p>
       
        <h2>核心优势</h2>
        <div class="core-advantages">
            <div>
                
                <div>
                    <li>
                        <strong>课堂教学资源（资源全、资源新、资源新）</strong>
                        <p>
                            资源全：覆盖50+主流学科，覆盖100+课程标准
                            资源新：覆盖100+最新课程标准
                        </p>
                    </li>
                    <li>
                        <strong>互动教学工具（多学科、情境化、趣味性）</strong>
                        <p>
                            多学科：支持50+主流学科
                            情境化：支持50+情境化教学
                            趣味性：支持50+趣味性教学
                        </p>
                    </li>
                </div> 
            </div>
            <div>
                <li>
                    <strong>创新测试形式（趣味多元、素养导向）</strong>
                    <p>
                        趣味多元：支持50+趣味多元测试
                        素养导向：支持50+素养导向测试
                    </p>
                </li>
                <li>
                    <strong>学情分析（个性化，具体化）</strong>
                </li>
            </div>
            <div>
                <li>
                    <strong>个性化教学</strong>
                    <p>
                        基于学情画像定制学习路径
                        动态调整教学资源难度层级
                        多模态资源智能匹配(视频/题库/教具)
                    </p>
                </li>
                <li>
                    <strong>查漏补缺</strong>
                    <p>
                        知识点掌握度热力图分析
                        错题归因与同类题推荐
                        阶段性学习进展追踪报告
                    </p>
                </li>
            </div>
            <div>
                <li>
                    <strong>汇聚核心技术，规模化应用</strong>
                    <p>
                        
                        智教备课助手，以数据驱动的方式，为教师提供个性化的教学服务，理解学生的学习情况，从而更好地因材施教。
                    </p>
                    <strong>专业服务体系，全程保驾护航</strong>
                    <p>人工智能+教育的技术创新突破从未止步。数据汇聚技术，多模态交互，提高教学效率。</p>
                </li>
            </div>
        </div>
        <h2>研发产品</h2>
        

        <div class="product-section">
  <!-- 选项卡导航 -->
        <div class="product-tabs">
            <div class="tab active">智能备课系统</div>
            <div class="tab">教学硬件生态</div>
        </div>

        <!-- 产品展示区 -->
        <div class="product-grid">
            <!-- 单个产品卡片 -->
            <div class="product-card">
            <div class="preview-image">
                <img src="../../assets/智能备课2.png" alt="智慧PPT生成界面：展示多模态资源整合面板">
                <!-- <div class="hover-mask">
                <button class="demo-button">在线体验</button>
                </div> -->
            </div>
            <div class="product-info">
                <h3>智慧PPT</h3>
                <div class="feature-tags">
                <span>AI自动排版</span>
                <span>跨学科素材库</span>
                <span>动态模板</span>
                </div>
                <p class="description">支持生成符合新课标要求的教学课件，提供：</p>
                <ul class="function-list">
                <li>50+学科专属模板库</li>
                <li>智能动画时间轴配置</li>
                <li>课堂互动组件一键嵌入</li>
                </ul>
            </div>
            </div>

            <!-- 其他产品卡片（结构相同） -->
            <div class="product-card">
            <div class="preview-image">
                <img src="../../assets/智能助手.png" alt="AI助教交互界面：显示学情分析与资源推荐">
                <!-- <div class="hover-mask">
                <button class="demo-button">立即体验</button>
                </div> -->
            </div>
            <div class="product-info">
                <h3>AI教学助理</h3>
                <div class="feature-tags">
                <span class="tag blue">学情诊断</span>
                <span class="tag purple">资源匹配</span>
                <span class="tag teal">课堂反馈</span>
                </div>
                <ul class="function-list">
                <li><i class="analytics-icon"></i> 实时生成课堂互动热力图</li>
                <li><i class="recommend-icon"></i> 智能推荐分层练习题库</li>
                <li><i class="alert-icon"></i> 异常学情及时预警提示</li>
                </ul>
            </div>
            </div>

            <div class="product-card">
            <div class="preview-image">
                <img src="../../assets/教案.png" alt="三维教案编辑器：展示多模态资源整合面板">
                <!-- <div class="hover-mask">
                <button class="demo-button">案例库</button>
                </div> -->
            </div>
            <div class="product-info">
                <h3>智能教案工坊</h3>
                <div class="feature-tags">
                <span class="tag orange">新课标适配</span>
                <span class="tag pink">跨学科融合</span>
                </div>
                <ul class="function-list">
                <li>50+学科三维目标模板库</li>
                <li>AI生成教学环节时间轴</li>
                <li>教学资源智能语义匹配</li>
                </ul>
            </div>
            </div>

            <div class="product-card highlight">
            <div class="preview-image">
                <img src="../../assets/学情化分析.png" alt="知识掌握度热力图：显示班级薄弱知识点分布">
                <!-- <div class="hover-mask">
                <button class="demo-button">分析报告</button>
                </div> -->
            </div>
            <div class="product-info">
                <h3>学情显微镜</h3>
                <div class="feature-tags">
                <span class="tag red">实时诊断</span>
                <span class="tag yellow">预测分析</span>
                </div>
                <ul class="function-list">
                <li>知识点掌握度热力分布图</li>
                <li>LSTM神经网络成绩预测</li>
                <li>个性化补偿学习路径生成</li>
                </ul>
            </div>
            </div>
        </div>




        </div>


      
    </div>
    <footer class="site-footer">
        <div class="footer-content">
            <div class="footer-section">
                <h4>产品与服务</h4>
                <ul class="footer-links">
                    <li><a href="#">区域教育自治</a></li>
                    <li><a href="#">校园主阵地建设</a></li>
                    <li><a href="#">智慧课堂</a></li>
                </ul>
            </div>

            <div class="footer-section">
                <h4>教育智库</h4>
                <ul class="footer-links">
                    <li><a href="#">教学案例库</a></li>
                    <li><a href="#">AI课件生成</a></li>
                    <li><a href="#">学情分析系统</a></li>
                </ul>
            </div>

            <div class="footer-section contact-info">
                <h4>联系我们</h4>
                <p>服务热线：400-123-4567</p>
                <p>邮箱：contact@eduai.com</p>
                <div class="social-links">
                    <!-- <a href="#"><img src="@/assets/wechat.png" alt="微信"></a> -->
                    <!-- <a href="#"><img src="@/assets/weibo.png" alt="微博"></a> -->
                </div>
            </div>
        </div>
        
        <div class="footer-bottom">
            <p>Copyright ©2025 智教备课平台 All Rights Reserved</p>
            <div class="legal-links">
                <a href="#">隐私政策</a> | 
                <a href="#">服务条款</a> | 
                <a href="#">用户协议</a>
            </div>
        </div>
    </footer>
  </template>
  <script setup>
  import { Document, DataAnalysis, Share, MagicStick, VideoPlay, Present } from '@element-plus/icons-vue'
    import { ref,  } from 'vue'
    import { useRouter } from 'vue-router'
    const router=useRouter()
    const showRoles = ref(false) // 控制角色下拉菜单的显示
    const showEdu = ref(false) // 控制教育智库下拉菜单的显示
    

    const scrollToSection = (sectionId) => {
  const element = document.getElementById(sectionId);
  if (element) {
    const headerHeight = document.querySelector('.header').offsetHeight;
    const elementPosition = element.getBoundingClientRect().top;
    const offsetPosition = elementPosition + window.pageYOffset - headerHeight;

    window.scrollTo({
      top: offsetPosition,
      behavior: 'smooth'
    });
  }
};

const goCreateplan=()=>{
  router.push({path:'/create/plan'})
}
  </script>
  
  
<style scoped>
/* 基础样式重置 */
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
  list-style: none;
  text-decoration: none;
}

/* 全局配色方案 */
:root {
  --primary-blue: #1a5b9b;
  --secondary-blue: #4a90e2;
  --background-gray: #f5f7fa;
}

.header {
  background: #fff;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  position: relative;
}

/* 品牌标识 */
.branding {
  display: flex;
  align-items: center;
}

.main-brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  height: 32px;
  width: auto;
}

.divider {
  color: #ccc;
  font-weight: 300;
}

.sub-brand {
  font-size: 18px;
  color: #333;
  font-weight: 500;
}

/* 主导航 */
.nav-main {
  display: flex;
  gap: 40px;
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
}

.nav-item {
  color: #333;
  font-size: 16px;
  transition: color 0.3s;
  position: relative;
  padding: 8px 0;
}

.nav-item:hover {
  color: #1a5b9b;
}

/* 下拉菜单 */
.dropdown {
  position: relative;
}

.dropdown:hover .dropdown-menu {
  display: block;
}

.dropdown-menu {
  display: none;
  position: absolute;
  top: 100%;
  left: 0;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 4px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  min-width: 160px;
  padding: 8px 0;
  z-index: 10000;
}

/* 搜索图标 */
.search-icon {
  font-size: 20px;
  color: #666;
  cursor: pointer;
  transition: color 0.3s;
}

.search-icon:hover {
  color: #1a5b9b;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .nav-main {
    gap: 28px;
  }
  
  .nav-item {
    font-size: 14px;
  }
}

.banner-area {
  height: 600px; /* 增加高度留出呼吸空间 */
  background: 
  linear-gradient(
      105deg,
      rgba(206, 217, 238, 0.88) 0%,  /* 深蓝色调渐变 */
      rgba(202, 217, 244, 0.75) 25%,
      rgba(255,255,255,0.15) 60%
    ),
    url('../../assets/教师备课.png'); /* 图片右对齐 */
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 0 8%;
  /* position: relative; */
  overflow: hidden; /* 隐藏可能溢出的背景元素 */
}

/* 添加书本装饰元素 */
.banner-area::before {
  content: "";
  position: absolute;
  left: -50px;
  top: 30%;
  width: 200px;
  height: 300px;
  background: rgba(255,255,255,0.15); /* 透明书本装饰 */
  transform: rotate(-15deg);
  z-index: 1;
}



/* 主标题优化 */
.banner-area h1 {
  font-size: 42px;
  color: #2c5282;
  max-width: 500px;
  line-height: 1.4;
  position: relative;
  z-index: 3;
  text-shadow: 1px 1px 2px rgba(255,255,255,0.3); /* 增加文字层次感 */
}

/* 副标题优化 */
.banner-area p {
  color: #4a5568;
  font-size: 20px;
  max-width: 480px;
  margin-top: 20px;
  position: relative;
  z-index: 3;
}

/* 内容区域布局 */
.content {
  padding: 60px 8%;
  background: white;
}

h2 {
  color: var(--primary-blue);
  font-size: 28px;
  margin: 40px 0 30px;
  padding-bottom: 15px;
  border-bottom: 3px solid var(--secondary-blue);
  display: inline-block;
}

/* 应用价值模块 */
ul {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 30px;
  margin: 40px 0;
}

ul li {
  background: var(--background-gray);
  padding: 25px;
  border-radius: 10px;
  border-left: 4px solid var(--secondary-blue);
  transition: transform 0.3s;
}

ul li:hover {
  transform: translateY(-5px);
}

/* 图片布局调整 */
img {
  width: 100%;
  border-radius: 12px;
  margin: 30px 0;
  box-shadow: 0 6px 20px rgba(0,0,0,0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header {
    padding: 0 5%;
  }
  
  .nav-main {
    gap: 20px;
    flex-wrap: wrap;
    height: auto;
    padding: 10px 0;
  }
  
  .banner-area {
    height: 400px;
    padding: 0 5%;
  }
  
  .banner-area h1 {
    font-size: 28px;
  }
  
  ul {
    grid-template-columns: 1fr;
  }
}

/* 页脚深度优化 */
.site-footer {
  background: var(--primary-blue);
  padding: 60px 8% 30px;
}

.footer-content {
  grid-template-columns: 2fr 2fr 3fr;
  max-width: 1200px;
}

.footer-section h4 {
  color: var(--secondary-blue);
  font-size: 18px;
  margin-bottom: 25px;
}

.footer-links a {
  color: #cbd5e0;
  font-size: 14px;
}

.contact-info p {
  color: #e2e8f0;
  font-size: 14px;
}

.social-links img {
  width: 36px;
  margin-right: 20px;
}

.footer-bottom {
  border-color: rgba(255,255,255,0.1);
  margin-top: 50px;
}

.content h3 {
  scroll-margin-top: 80px; /* 补偿固定导航栏高度 */
  position: relative;
}

/* 添加点击反馈效果 */
ul li {
  cursor: pointer;
  transition: all 0.3s ease;
}

ul li:hover {
  background: #e3f2fd;
  box-shadow: 0 2px 8px rgba(25, 118, 210, 0.2);
}

/* 目标定位高亮动画 */
:target::before {
  content: "";
  position: absolute;
  left: -20px;
  top: 50%;
  width: 6px;
  height: 80%;
  background: #2196f3;
  transform: translateY(-50%);
  animation: highlight 1s ease;
}

@keyframes highlight {
  from { opacity: 0; transform: translateY(-50%) scaleY(0); }
  to { opacity: 1; transform: translateY(-50%) scaleY(1); }
}
.content-item {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  align-items: center;
  padding: 60px 0;
  border-bottom: 1px solid #eee;
}

/* 奇数项图片在左 */
.content-item:nth-child(odd) .media-column {
  order: 1;
}

/* 偶数项图片在右 */
.content-item.reverse .media-column {
  order: 2;
}

.media-column img {
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.1);
  transition: transform 0.3s;
}

.media-column img:hover {
  transform: translateY(-5px);
}

/* 功能标签样式 */
.feature-tags {
  margin: 20px 0;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.tag {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.9em;
}

.blue { background: #e3f2fd; color: #1976d2; }
.green { background: #e8f5e9; color: #388e3c; }
.purple { background: #f3e5f5; color: #9c27b0; }

/* 功能列表增强 */
.function-list li {
  padding: 12px;
  margin: 8px 0;
  background: #f8f9fa;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .content-item {
    grid-template-columns: 1fr;
    gap: 30px;
  }
  
  .media-column {
    order: -1 !important; /* 移动端统一图片在上方 */
  }
  
  .function-list li {
    font-size: 0.95em;
  }
}

/* 新增色系 */
.teal { background: #e0f2f1; color: #00695c; }
.red { background: #ffebee; color: #c62828; }

/* 增强图片交互 */
.media-column {
  position: relative;
}

.media-column::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  box-shadow: inset 0 0 30px rgba(0,0,0,0.1);
  pointer-events: none;
}

/* 核心优势容器 */
.core-advantages {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 30px;
  padding: 40px 0;
  background: linear-gradient(135deg, #f8f9ff 0%, #e3f2fd 100%);
}

/* 优势卡片 */
.core-advantages > div {
  background: rgba(255,255,255,0.95);
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 8px 32px rgba(26,91,155,0.1);
  transition: transform 0.3s cubic-bezier(0.4,0,0.2,1);
  border: 1px solid rgba(26,91,155,0.1);
}

.core-advantages > div:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px rgba(26,91,155,0.15);
}

/* 标题系统 */
.core-advantages h3 {
  color: var(--primary-blue);
  font-size: 24px;
  margin-bottom: 20px;
  position: relative;
  padding-left: 40px;
}

.core-advantages h3::before {
  content: "";
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 30px;
  height: 30px;
  background: url('data:image/svg+xml,<svg ...>') no-repeat; /* 插入SVG图标 */
}

/* 标签系统 */
.feature-tags {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin: 20px 0;
}

.tag {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  transition: all 0.3s;
}

/* 动态数据展示 */
.data-visual {
  position: relative;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  margin: 20px 0;
}

.data-visual::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent 50%, rgba(26,91,155,0.05));
  pointer-events: none;
}

/* 专业服务模块特殊样式 */
.pro-service {
  grid-column: 1 / -1;
  background: linear-gradient(135deg, #1a5b9b 0%, #4a90e2 100%);
  color: white;
}

.pro-service .tag { background: rgba(255,255,255,0.15); }

/* 图标动画 */
@keyframes iconFloat {
  0%,100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

.core-advantages li {
  padding: 15px;
  margin: 10px 0;
  background: #f8f9fa;
  border-left: 4px solid var(--secondary-blue);
  border-radius: 8px;
  transition: all 0.3s;
}

.core-advantages li:hover {
  background: #e3f2fd;
  transform: translateX(10px);
}

/* 响应式适配 */
@media (max-width: 768px) {
  .core-advantages {
    grid-template-columns: 1fr;
    padding: 20px;
  }
  
  .pro-service {
    background: #1a5b9b;
  }
}

.product-tabs {
  display: flex;
  gap: 30px;
  margin-bottom: 40px;
  border-bottom: 2px solid #eee;
}

.tab {
  padding: 12px 30px;
  cursor: pointer;
  color: #666;
  transition: all 0.3s;
}

.tab.active {
  color: #1a5b9b;
  border-bottom: 3px solid currentColor;
  transform: translateY(2px);
}

/* 产品网格布局 */
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
}

.product-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 6px 20px rgba(0,0,0,0.08);
  overflow: hidden;
  transition: transform 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
}

/* 图片预览交互 */
.preview-image {
  position: relative;
  background: #f8f9fa;
}

.hover-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(26,91,155,0.7);
  opacity: 0;
  transition: opacity 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-card:hover .hover-mask {
  opacity: 1;
}

.demo-button {
  padding: 12px 24px;
  background: #fff;
  color: #1a5b9b;
  border-radius: 24px;
  border: none;
  cursor: pointer;
}

/* 文字信息排版 */
.product-info {
  padding: 20px;
}

.feature-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin: 12px 0;
}

.feature-tags span {
  padding: 4px 12px;
  background: #e3f2fd;
  border-radius: 14px;
  font-size: 0.9em;
  color: #1a5b9b;
}

.function-list {
  padding-left: 20px;
  color: #666;
  line-height: 1.8;
}

.function-list li::marker {
  color: #4a90e2;
  font-size: 1.2em;
}

@media (max-width: 768px) {
  .product-grid {
    grid-template-columns: 1fr;
  }
  
  .tab {
    padding: 10px 20px;
    font-size: 0.9em;
  }
}

.core-advantages {
  display: grid;
  grid-template-columns: repeat(2, 1fr); /* 强制双列布局 */
  gap: 30px;
  padding: 40px 0;
}

.core-advantages > div {
  min-width: 480px; /* 保证双列显示 */
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 6px 20px rgba(26,91,155,0.1);
  transition: transform 0.3s;
}

/* 产品研发模块布局优化 */
.product-grid {
  grid-template-columns: repeat(2, minmax(400px, 1fr)); /* 双列固定布局 */
  gap: 30px;
}

.product-card {
  min-width: 400px;
  margin-bottom: 30px;
}

/* 移动端适配 */
@media (max-width: 1024px) {
  .core-advantages,
  .product-grid {
    grid-template-columns: 1fr;
  }
  
  .core-advantages > div,
  .product-card {
    min-width: auto;
    margin: 0 15px;
  }
}

/* 增强双列布局的视觉表现 */
.core-advantages > div:nth-child(odd) {
  border-right: 3px solid #e3f2fd;
  transform-origin: right center;
}

.core-advantages > div:nth-child(even) {
  border-left: 3px solid #e3f2fd;
  transform-origin: left center;
}

.product-card:nth-child(odd) .preview-image {
  border-right: 2px solid #1a5b9b;
}

.product-card:nth-child(even) .preview-image {
  border-left: 2px solid #1a5b9b;
}


.site-footer {
  background: linear-gradient(135deg, #1a5b9b 0%, #2c5282 100%); /* 蓝紫渐变 */
  padding: 40px 8% 20px; /* 减少上下间距 */
  color: rgba(255,255,255,0.9);
}

.footer-content {
  max-width: 1200px;
  height: 300px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 30px;
}

.footer-section h4 {
  color: #a3d4ff; /* 浅蓝标题 */
  font-size: 16px;
  margin-bottom: 15px;
}

.footer-bottom {
  margin-top: 30px;
  margin:auto;
    padding-top: 20px;
  border-top: 1px solid rgba(255,255,255,0.1);
}
.dropdown-menu {
  display: none;
  position: absolute;
  top: calc(100% + 5px); /* 增加间距 */
  left: 0;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  box-shadow: 0 6px 16px rgba(0,0,0,0.15);
  min-width: 160px;
  z-index: 2000; /* 提高层级 */
  opacity: 0;
  transform: translateY(-10px);
  transition: all 0.3s ease;
}

.dropdown:hover .dropdown-menu {
  display: block;
  opacity: 1;
  transform: translateY(0);
}

/* 下拉菜单容器 */
.dropdown {
  position: relative;
  display: inline-block;
  height: 100%;
}

/* 导航项基础样式 */
.nav-item {
  display: inline-flex;
  align-items: center;
  height: 100%;
  /* padding: 0 15px; */
  color: #333;
  font-size: 16px;
  transition: all 0.3s;
}

/* 下拉触发项特殊样式 */
.dropdown-trigger {
  position: relative;
  padding-right: 25px !important;
}

.dropdown-icon {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 12px;
  transition: transform 0.3s;
}

/* 下拉菜单面板 */
.dropdown-menu {
  position: absolute;
  top: calc(100% + 5px);
  left: 0;
  min-width: 160px;
  background: #fff;
  border-radius: 6px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
  padding: 8px 0;
  z-index: 2000;
  opacity: 0;
  transform: translateY(-10px);
  transition: all 0.3s ease;
}

.dropdown:hover .dropdown-menu {
  opacity: 1;
  transform: translateY(0);
}


.dropdown-item {
  display: block;
  padding: 10px 16px;
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  transition: all 0.3s;
}

.dropdown-item:hover {
  background-color: #f5f7fa;
  color: #1a5b9b;
}

/* 对齐修复 */
.nav-main {
  display: flex;
  align-items: center;
  height: 100%;
}

.nav-item, .dropdown-trigger {
  height: 64px;
  display: inline-flex;
  align-items: center;
}
</style>