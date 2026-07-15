# 🚄 Vaeee 车票预定管理系统

> 基于 Spring Boot 3 + Vue 3 的前后端分离多交通车票预定系统，支持航班、火车、巴士的统一票务管理与订单处理。

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.9-6DB33F?logo=spring-boot)
![Vue](https://img.shields.io/badge/Vue-3.5-4FC08D?logo=vue.js)
![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.15-blue)
![Sa-Token](https://img.shields.io/badge/Sa--Token-1.44-00BFFF)
![License](https://img.shields.io/badge/License-MIT-green)

---

## 📋 项目简介

本项目是一个**多交通方式的车票预定管理系统**，用户可以在线查询航班、火车、巴士信息，完成购票、支付、退票、改签等操作；管理员可对票务、用户、订单进行统一管理。

### ✨ 核心特性

| 特性 | 说明 |
|------|------|
| 🎫 **多交通统一管理** | 航班、火车、巴士三种交通方式统一建模，策略模式灵活扩展 |
| 🔐 **双角色权限** | 用户前台购票 + 管理员后台管理，Sa-Token 鉴权 |
| 💳 **完整交易闭环** | 购票 → 支付 → 退票 → 改签，资金流水全记录 |
| 🖼️ **图形验证码** | 集成 Kaptcha 验证码，防止恶意刷接口 |
| 📄 **接口文档** | Knife4j (Swagger) 自动生成，可视化调试 |
| 🔄 **乐观锁防超卖** | 基于 @Version 乐观锁，解决高并发下库存超卖问题 |
| ⚡ **N+1 查询优化** | 批量查询 + HashMap 缓存，避免循环查库性能问题 |
| 📱 **响应式前端** | Element Plus 组件库，适配 PC 与移动端 |

---

## 🏗️ 技术栈

### 后端 (Backend)

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.5.9 | 核心框架 |
| Spring Security | 3.x | 安全认证 |
| MyBatis-Plus | 3.5.15 | ORM 框架，增强 CRUD |
| Sa-Token | 1.44.0 | 登录认证与权限控制 |
| MySQL | 8.x | 关系型数据库 |
| Druid | 1.2.26 | 数据库连接池 |
| Knife4j | 4.4.0 | API 接口文档 |
| PageHelper | 2.1.0 | 分页插件 |
| Kaptcha | 2.3.2 | 图形验证码 |
| Lombok | 1.18.32 | 代码简化 |

### 前端 (Frontend)

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue 3 | 3.5.26 | 前端框架 (Composition API) |
| TypeScript | 5.9 | 类型安全 |
| Element Plus | 2.13.1 | UI 组件库 |
| Pinia | 3.0.4 | 状态管理 |
| Vue Router | 4.6.4 | 路由管理 |
| Axios | 1.13.4 | HTTP 请求封装 |
| Vite | 8.x | 构建工具 |
| Less | 4.5.1 | CSS 预处理器 |

### 设计模式

- **策略模式 (Strategy)** — 不同交通类型的差异化处理（价格计算、余票查询等）
- **工厂模式 (Factory)** — 根据交通类型动态创建票务对象
- **模板方法 (Template)** — 统一的订单处理流程

---

## 🚀 快速开始

### 环境要求

- **JDK** 17+
- **Maven** 3.8+
- **Node.js** 20+ / 22+
- **MySQL** 8.0+
- **IDE** IntelliJ IDEA / VS Code

### 1️⃣ 克隆项目

```bash
git clone https://github.com/vaeee/ticket-management-system.git
cd ticket-management-system
```

### 2️⃣ 数据库配置

创建数据库 `program`，执行 SQL 脚本：

```sql
-- 详见 sql/ 目录下的初始化脚本
source sql/init.sql;
```

修改 `backend/src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/program
    username: root        # 改为你的数据库用户名
    password: 123456      # 改为你的数据库密码
```

### 3️⃣ 启动后端

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端启动在 `http://localhost:8081/vaeee`

接口文档访问：`http://localhost:8081/vaeee/doc.html`

### 4️⃣ 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端启动在 `http://localhost:5173`

---

## 📂 项目结构

```
ticket-management-system/
├── backend/                          # Spring Boot 后端
│   ├── src/main/java/com/example/demo/
│   │   ├── common/                   # 公共模块
│   │   │   ├── constant/             # 常量定义
│   │   │   ├── enums/                # 枚举（订单状态、交通类型、角色）
│   │   │   ├── exception/            # 全局异常处理
│   │   │   ├── result/               # 统一响应封装
│   │   │   └── util/                 # 工具类（验证码、Token、文件上传）
│   │   ├── config/                   # 配置类（跨域、Swagger、Sa-Token、MyBatis-Plus）
│   │   ├── controller/               # 控制器层
│   │   ├── DTO/                      # 数据传输对象
│   │   ├── entity/                   # 实体类
│   │   │   └── transport/            # 交通票务实体（基类+子类）
│   │   ├── factory/                  # 工厂模式
│   │   ├── mapper/                   # MyBatis-Plus Mapper
│   │   ├── service/                  # 业务逻辑层
│   │   │   ├── impl/                 # Service 实现
│   │   │   ├── inner/                # 内部事务服务
│   │   │   └── cache/                # 缓存服务
│   │   ├── strategy/                 # 策略模式
│   │   │   └── transport/            # 交通策略（Flight/Train/Bus）
│   │   └── VO/                       # 视图对象
│   └── pom.xml
├── frontend/                         # Vue 3 前端
│   ├── src/
│   │   ├── api/                      # API 接口封装
│   │   ├── assets/                   # 静态资源
│   │   ├── components/               # 公共组件
│   │   ├── layouts/                  # 布局组件
│   │   ├── router/                   # 路由配置（含路由守卫）
│   │   ├── stores/                   # Pinia 状态管理
│   │   ├── styles/                   # 样式文件
│   │   ├── types/                    # TypeScript 类型定义
│   │   ├── utils/                    # 工具函数
│   │   └── views/                    # 页面视图
│   │       ├── front/                # 前台页面
│   │       │   ├── order/            # 订票页面
│   │       │   ├── user-center/      # 用户中心
│   │       │   └── article/          # 文章展示
│   │       ├── manager/              # 后台管理页面
│   │       └── login/                # 登录页面
│   └── package.json
├── sql/                              # 数据库初始化脚本
│   └── init.sql
└── README.md
```

---

## 🧩 核心功能模块

### 🎯 用户端 (前台)

| 功能 | 描述 |
|------|------|
| 🔑 登录/注册 | 用户注册、登录、图形验证码校验 |
| 🔍 票务查询 | 按出发地、目的地、时间筛选航班/火车/巴士 |
| 🛒 购票下单 | 选择交通方式 → 选择座位 → 创建订单 |
| 💰 支付 | 账户余额支付，实时扣款 |
| 🔄 退票 | 已支付订单退票，金额返还账户 |
| ✈️ 改签 | 同交通类型下改签其他班次 |
| 👤 个人中心 | 个人信息修改、密码修改、订单历史 |
| 📰 文章浏览 | 浏览平台发布的资讯文章 |

### 🔧 管理端 (后台)

| 功能 | 描述 |
|------|------|
| 📊 仪表盘 | 系统概览、数据统计 |
| 👥 用户管理 | 用户信息的增删改查 |
| ✈️ 航班管理 | 航班信息的增删改查 |
| 🚄 火车管理 | 火车班次信息的增删改查 |
| 🚌 巴士管理 | 巴士线路信息的增删改查 |
| 📋 订单管理 | 所有订单的查询与修改 |
| 📝 文章管理 | 资讯文章的发布与管理 |

---

## 🔐 权限体系

基于 **Sa-Token** 实现双角色权限控制：

| 角色 | 权限范围 |
|------|----------|
| **👤 普通用户** | 前台购票、订单管理、个人信息管理 |
| **🛡️ 管理员** | 后台所有管理功能 |

- Token 通过 Header 传递，自定义 Token 名称 `vaeeetoken`
- 支持多设备同时登录
- 路由守卫 + 接口鉴权双重校验

---

## 🧠 架构亮点

### 1️⃣ 策略模式 + 工厂模式解耦交通类型

```
交通策略接口 (TransportStrategy)
    ├── FlightStrategy   ✈️
    ├── TrainStrategy    🚄
    └── BusStrategy      🚌
```

新增交通方式只需实现 `TransportStrategy` 接口，**开闭原则**，零侵入。

### 2️⃣ N+1 查询优化

通过批量查询 + HashMap 缓存，将 N+1 次数据库查询优化为 **2 次查询 + 内存组装**，大幅提升订单列表查询性能。

### 3️⃣ 乐观锁防超卖

订单表使用 `@Version` 乐观锁，高并发下防止库存超卖：

```java
@Version
private Integer version; // 更新时自动校验版本号
```

---

## 📸 界面预览

| 页面 | 截图 |
|------|------|
| 🏠 前台首页 | — |
| 🎫 购票页面 | — |
| 📋 订单列表 | — |
| 🛡️ 后台管理 | — |

> 截图待补充

---

## 🗺️ 后续规划

- [ ] 增加一等座/二等座座位分级
- [ ] 超时未支付订单自动取消
- [ ] 订单操作日志记录
- [ ] 用户和管理员 Token 分离
- [ ] 工厂模式 + 策略模式深度整合
- [ ] Docker 容器化部署
- [ ] 单元测试覆盖

---

## 📄 许可证

本项目基于 MIT 许可证开源。

## 👨‍💻 作者

- **vaeee** — [GitHub](https://github.com/vaeee)
- 邮箱：3053695484@qq.com

---

> 💡 本项目为毕业设计作品，如有问题或建议欢迎提 Issue 或 PR！
