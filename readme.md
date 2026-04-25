# 🍱 外卖速递 - Sky-Take-Out
> 基于 Spring Boot + Vue3 开发的前后端分离外卖平台，实现用户点餐、商家管理全流程业务

---

## 📌 项目简介
该项目是一套完整的外卖业务系统，分为**用户端**与**商家端**两大模块，覆盖从商品浏览、下单支付到订单处理、数据统计的全流程场景，适合学习企业级Java全栈开发规范。

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

### 前端
| 技术 | 用途 |
| :--- | :--- |
| Vue 3 | 前端框架 |
| Element Plus | UI组件库 |
| Axios | HTTP请求 |

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

---

## 🚀 快速启动
### 环境准备
- JDK 11+
- Maven 3.6+
- MySQL 8.x
- Redis 5.x+
- RabbitMQ 3.x+
- Node.js 16+

## 🚀 后端部署

### 环境准备
- JDK 11+
- Maven 3.6+
- MySQL 8.x
- Redis 5.x+
- RabbitMQ 3.x+

### 部署步骤
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

# 4. 启动项目
mvn clean install
mvn spring-boot:run
```
### 📂 项目结构
```text
sky-take-out
├── sky-common              # 公共模块（工具类、异常处理、常量定义）
├── sky-pojo                # 实体类模块（DTO、VO、Entity）
├── sky-server              # 后端主模块（Controller、Service、Mapper）
│   └── src
│       └── main
│           ├── java/com/sky
│           │   ├── config/RabbitMQConfiguration.java  # RabbitMQ延迟队列配置
│           │   ├── mq/OrderTaskProducer.java          # 消息生产者
│           │   └── mq/OrderTaskConsumer.java          # 消息消费者
│           └── resources   # 配置文件、Mapper XML
├── sql                     # 数据库脚本文件
└── README.md               # 项目说明文档
```