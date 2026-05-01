# 🍱 智营外卖 - Sky-RAG-Take-Out
> 基于 Spring Boot + Vue 2 开发的前后端分离外卖平台，集成AI智能经营助手，实现用户点餐、商家管理、数据分析全流程业务

---

##  项目简介
该项目是一套完整的外卖业务系统，分为**用户端**与**商家端**两大模块，覆盖从商品浏览、下单支付到订单处理、数据统计、AI智能分析的全流程场景，适合学习企业级Java全栈开发规范。

---

## 🛠️ 技术栈
### 后端
| 技术 | 用途 |
| :--- | :--- |
| Java 11/17 | 开发语言 |
| Spring Boot 2.x | 项目核心框架 |
| MyBatis Plus | ORM持久层框架 |
| MySQL 8.x | 业务数据库 |
| Redis | 缓存与分布式锁 |
| RabbitMQ | 消息队列（延迟队列实现订单超时自动取消） |
| JWT | 用户身份认证 |
| OkHttp | HTTP客户端（调用Python AI服务） |

### AI服务
| 技术 | 用途 |
| :--- | :--- |
| Python 3.8+ | AI分析服务开发语言 |
| Flask | 轻量级Web框架 |
| OpenAI SDK | 调用通义千问API |
| 通义千问 | 大语言模型（AI对话生成） |

### 前端
| 技术 | 用途 |
| :--- | :--- |
| Vue 2 | 前端框架 |
| TypeScript | 类型安全 |
| Element UI | UI组件库 |
| Axios | HTTP请求 |
| ECharts | 数据可视化 |

---

## ✨ 核心功能
### 用户端
- 微信授权登录
- 商品分类浏览与详情查看
- 购物车管理（增/删/改）
- 下单与模拟支付
- 订单查询与评价

### 商家端
- 员工权限管理
- 菜品/套餐CRUD与上下架
- 订单处理（接单/派送/完成）
- 营业状态设置
- 销量与营业额数据统计

### AI智能助手
- 悬浮球交互界面，随时唤出
- 实时经营数据查询（营业额、订单、菜品销量等）
- 经营趋势分析与优化建议
- 多轮对话，支持上下文理解
- 会话管理（新建/切换/删除）
- 对话历史持久化存储

---

## 🚀 快速启动
### 环境准备
- JDK 11+
- Maven 3.6+
- MySQL 8.x
- Redis 5.x+
- RabbitMQ 3.x+
- Node.js 16+
- Python 3.8+（AI服务）

### 后端部署

```bash
# 1. 克隆项目到本地
git clone https://github.com/YvdH1900/first-work.git
cd sky-take-out

# 2. 导入数据库脚本
# 在 MySQL 中创建数据库 sky_take_out
# 执行项目目录下 sql 文件

# 3. 添加配置文件
# 编辑application-dev.yml
# 配置数据库、 Redis、OSS、百度AK、微信小程序 连接信息：
sky:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    host: localhost
    port: 3306
    database: sky_take_out
    username: ！！！
    password: ！！！
  alioss:
    endpoint: oss-cn-beijing.aliyuncs.com
    access-key-id: ！！！！
    access-key-secret: ！！！！
    bucket-name: ！！！
  redis:
    host: localhost
    port: 6379
    database: 0
  wechat:
    appid: ！！！！
    secret: ！！！
  shop:
    address: 北京市海淀区
  baidu:
    ak: ！！！！
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
  ai:
    python-service-url: http://localhost:5000

# 4. 启动项目
mvn clean install
mvn spring-boot:run
```

### 前端部署

```bash
# 1. 进入前端项目目录
cd project-sky-admin-vue-ts

# 2. 安装依赖
npm install --legacy-peer-deps

# 3. 启动开发服务器
npm run serve

# 4. 访问 http://localhost:8888
```

### AI服务部署

```bash
# 1. 进入AI服务目录
cd sky-ai

# 2. 创建虚拟环境（推荐）
python -m venv venv
source venv/bin/activate  # Linux/Mac
# 或
venv\Scripts\activate     # Windows

# 3. 安装依赖
pip install -r requirements.txt

# 4. 配置API密钥
# 复制 .env.example 为 .env
# 填入通义千问API密钥
cp .env.example .env

# 5. 启动AI服务
python app.py

# 6. 访问 http://localhost:5000
```

### 📂 项目结构
```text
sky-take-out
├── project-sky-admin-vue-ts  # 商家管理端前端（Vue2 + TypeScript）
│   ├── src/
│   │   ├── api/              # API接口
│   │   ├── views/            # 页面组件
│   │   ├── components/       # 公共组件
│   │   ├── store/            # Vuex状态管理
│   │   └── router.ts         # 路由配置
│   └── package.json
├── sky-common                # 公共模块（工具类、异常处理、常量定义）
├── sky-pojo                  # 实体类模块（DTO、VO、Entity）
├── sky-server                # 后端主模块（Controller、Service、Mapper）
│   └── src
│       └── main
│           ├── java/com/sky
│           │   ├── config/RabbitMQConfiguration.java  # RabbitMQ延迟队列配置
│           │   ├── mq/OrderTaskProducer.java          # 消息生产者
│           │   └── mq/OrderTaskConsumer.java          # 消息消费者
│           └── resources   # 配置文件、Mapper XML
├── sky-ai                    # AI智能分析服务（Python + Flask）
│   ├── app.py                # Flask主应用
│   ├── qwen_client.py        # 通义千问API客户端
│   ├── requirements.txt      # Python依赖
│   └── .env.example          # 环境变量模板
├── sql                       # 数据库脚本文件
└── README.md                 # 项目说明文档
```

---

##  更新日志

### v1.2.0
- ✅ 集成AI智能经营助手（悬浮球交互界面）
- ✅ 实现轻量级RAG架构，基于实时经营数据进行AI分析
- ✅ 支持多轮对话，对话历史持久化存储
- ✅ 会话管理功能（新建/切换/删除会话）
- ✅ 接入通义千问大模型API
- ✅ Java + Python跨语言服务通信

### v1.1.0
- ✅ 集成RabbitMQ延迟队列，替代定时任务实现订单超时自动取消
- ✅ 实现消息重试与失败队列机制，保障消息可靠消费
- ✅ 修复订单取消后仍可支付的Bug
- ✅ 添加商家管理端前端项目（Vue2 + TypeScript）

### v1.0.0
- ✅ 用户端核心功能（登录、点餐、下单、支付）
- ✅ 商家端核心功能（菜品管理、订单处理、数据统计）
- ✅ Redis缓存优化
- ✅ JWT身份认证