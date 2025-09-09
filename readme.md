> **AI-OJ** 是一个融合大模型智能辅助与真实代码执行验证的下一代在线判题平台。创新性将代码沙箱注册为大模型 Tool，实现“生成 → 执行 → 反馈”闭环，有效缓解大模型“幻读”问题。支持题目智能推荐、AI题解生成、对话式解题辅导，适用于算法学习、编程竞赛、教学辅助等场景。
>

---

## 🏗️ 系统架构图（Mermaid）
![](https://cdn.nlark.com/yuque/__mermaid_v3/a08d72da534736e2599baacb7796ea57.svg)

> 📌 **架构说明**：
>
> + **API Gateway**：统一入口，负责鉴权、限流（用户级令牌桶）、路由
> + **AI 服务**：核心智能中枢，集成 Spring AI，支持 RAG、Tool 调用、对话记忆
> + **判题服务**：调用沙箱执行代码，资源隔离 + 并发控制
> + **代码沙箱**：基于 Docker Java API + SecurityManager，安全执行用户代码
> + **Redis**：分布式会话、限流计数、对话历史缓存
> + **RabbitMQ**：异步判题任务队列，削峰填谷
>

---

## 🚀 核心功能亮点
### ✅ 1. AI 智能辅助解题
+ 基于 Spring AI `ChatClient` 封装统一模型调用层，支持通义千问、DeepSeek、GPT 等一键切换
+ 通过 `@Tool` 注解将 **代码沙箱、PDF 生成、联网搜索** 注册为大模型可调用工具
+ 支持 MCP 协议（stdio / SSE）扩展外部工具能力

### ✅ 2. 真实执行验证，对抗“幻读”
+ AI 生成代码 → 自动调用沙箱执行测试用例 → 返回真实运行结果
+ 沙箱基于 Docker + Java SecurityManager + 字典树敏感词过滤，保障安全
+ 判题服务通过 `Semaphore` + Docker 资源限制防止过载

### ✅ 3. 编程题型专属 RAG 知识库
+ 构建覆盖 **动态规划、图论、贪心、回溯** 等算法的垂直知识库（托管于阿里云百炼）
+ 使用 `RetrievalAugmentationAdvisor` 实现元数据过滤 + 向量检索双路召回
+ 支持三大场景：
    - **AI题解**：按题目ID精准召回增强生成
    - **题目推荐**：混合检索（语义+标签+难度）
    - **空Query降级**：纯元数据排序推荐

### ✅ 4. 对话式交互与上下文记忆
+ 基于 `MessageChatMemoryAdvisor` + `ChatMemory` 实现多轮对话上下文管理
+ 会话历史持久化至 Redis，支持用户随时恢复对话
+ 结构化输出控制（JSON Schema / OutputParser）

### ✅ 5. 企业级稳定性保障
+ **网关层**：基于 Redis + Lua 实现用户粒度令牌桶限流（频率+总量）
+ **判题层**：Semaphore 控制并发 + Docker 资源隔离（CPU/MEM/Timeout）
+ **服务治理**：Dubbo + Nacos 实现服务注册发现、负载均衡、健康检查
+ **异步解耦**：RabbitMQ 处理高并发判题请求，避免阻塞

---

## 🧩 微服务模块说明
| 服务名称 | 职责描述 | 技术要点 |
| --- | --- | --- |
| **API Gateway** | 统一入口、路由、鉴权、限流 | Spring Cloud Gateway + Redis Lua |
| **用户服务** | 用户注册/登录、权限管理、调用配额 | Redis 分布式 Session |
| **题目服务** | 题目 CRUD、元数据管理、测试用例 | MyBatis Plus + MySQL |
| **AI 服务** | AI题解、题目推荐、AI解题大师、对话记忆、工具调用 | Spring AI + Dubbo + 百炼 RAG |
| **判题服务** | 接收代码 → 调用沙箱 → 返回判题结果 | Semaphore + Docker Java API |
| **代码沙箱** | 安全执行用户代码，限制系统调用、文件访问 | Docker + SecurityManager + TTY |
| **公共模块** | DTO、Feign/Dubbo 接口、异常类、工具类、AI Prompt 模板 | ai-oj-common-api |


---

## 🔐 安全与性能设计
### 🔒 安全机制
+ **沙箱安全**：
    - 使用 `Java SecurityManager` 限制文件/网络/反射操作
    - 敏感词过滤（字典树）拦截危险代码（如 `Runtime.exec`）
    - Docker 容器隔离 + TTY 交互传参，避免命令注入
+ **接口安全**：
    - JWT 鉴权 + 用户黑白名单
    - 网关层限流防刷（Redis Lua 令牌桶）

### ⚡ 性能优化
+ **AI 服务**：ChatClient 连接池 + Prompt 缓存
+ **判题服务**：异步队列（RabbitMQ） + 并发控制（Semaphore）
+ **知识库**：热门题解缓存（Redis） + 百炼向量检索优化
+ **会话**：Redis 存储对话历史，支持快速恢复

---

## 🛠️ 快速启动（简版）
### 前置依赖
+ JDK 17+  
+ Maven 3.8+  
+ Docker 20.10+  
+ Redis 6+  
+ RabbitMQ 3.11+  
+ Nacos 2.3+

### 启动步骤
```bash
# 1. 启动基础设施
docker-compose up -d redis rabbitmq nacos

# 2. 启动微服务（按顺序）

# 3. 访问前端
npm run dev  # Vue 前端项目
```

> 📄 详细部署文档见 `/docs/deployment.md`
>

---

## 📈 未来展望
+ ✅ 支持多模态题解（代码 + 图表 + 动画演示）
+ ✅ 接入 CodeLlama、StarCoder 等代码专用大模型
+ ✅ 实现“错题本AI复盘”、“学习路径推荐”功能
+ ✅ 支持 Kubernetes 集群化部署 + 自动扩缩容
+ ✅ 开放插件市场，支持第三方 Tool 接入

---

## 🤝 贡献与交流
欢迎提交 Issue / PR！  
技术交流群：`AI-OJ Dev Group`（请邮件联系 maintainer@ai-oj.dev）

---

## 📄 License
Apache 2.0 © 2025 AI-OJ Team





