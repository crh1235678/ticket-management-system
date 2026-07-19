# 🚄 Vaeee 车票预定管理系统

> 基于 Spring Boot 3 + Vue 3 的前后端分离多交通车票预定系统，支持航班、火车、巴士的统一票务管理与订单处理。

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.9-6DB33F?logo=spring-boot)
![Vue](https://img.shields.io/badge/Vue-3.5-4FC08D?logo=vue.js)
![Redis](https://img.shields.io/badge/Redis-8.8-DC382D?logo=redis)
![Sa-Token](https://img.shields.io/badge/Sa--Token-1.44-00BFFF)
![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.15-blue)
![License](https://img.shields.io/badge/License-MIT-green)

---

## 📋 项目简介

本项目是一个**多交通方式的车票预定管理系统**，用户可在线查询航班、火车、巴士信息，完成购票、支付、退票、改签等操作；管理员可对票务、用户、订单进行统一管理。

### ✨ 核心特性

| 特性 | 说明 |
|------|------|
| 🎫 **多交通统一管理** | 航班、火车、巴士统一建模，策略模式灵活扩展 |
| 🔐 **双角色权限** | 用户前台购票 + 管理员后台管理，Sa-Token 鉴权 |
| 💳 **完整交易闭环** | 购票 → 支付 → 退票 → 改签，资金流水全记录 |
| 🖼️ **图形验证码** | Kaptcha 生成 + Redis 缓存，5 分钟自动过期 |
| 🔄 **乐观锁防超卖** | 基于 @Version 乐观锁，解决高并发下库存超卖 |
| ⚡ **N+1 查询优化** | 批量查询 + HashMap 缓存，避免循环查库 |
| 💾 **Redis 多场景集成** | 验证码 / 登录 Token / 热门车次缓存 |
| 📱 **响应式前端** | Element Plus 组件库，适配 PC 与移动端 |

---

## 🏗️ 技术栈

### 后端

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.5.9 | 核心框架 |
| Spring Security | 3.x | 安全认证 |
| MyBatis-Plus | 3.5.15 | ORM 框架 |
| Sa-Token | 1.44.0 | 登录认证 + 权限控制 |
| **Redis** | **8.8.0** | **分布式缓存** |
| MySQL | 8.x | 关系型数据库 |
| Druid | 1.2.26 | 数据库连接池 |
| Knife4j | 4.4.0 | API 接口文档 |
| Kaptcha | 2.3.2 | 图形验证码 |
| Lombok | 1.18.32 | 代码简化 |

### 前端

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue 3 | 3.5.26 | 前端框架 |
| TypeScript | 5.9 | 类型安全 |
| Element Plus | 2.13.1 | UI 组件库 |
| Pinia | 3.0.4 | 状态管理 |
| Vue Router | 4.6.4 | 路由管理 |
| Axios | 1.13.4 | HTTP 请求封装 |
| Vite | 8.x | 构建工具 |

### 设计模式

- **策略模式** — 不同交通类型的差异化处理（订单查询、余票管理）
- **工厂模式** — 根据交通类型动态创建票务对象
- **模板方法** — 统一的订单处理流程封装

---

## 🚀 快速开始

### 环境要求

- **JDK** 17+
- **Maven** 3.8+
- **Node.js** 20+
- **MySQL** 8.0+
- **Redis** 8.8.0+

### 1️⃣ 克隆项目

```bash
git clone https://github.com/crh1235678/ticket-management-system.git
cd ticket-management-system
```

### 2️⃣ 数据库配置

创建数据库 `vaeee_db`，执行 SQL 脚本：

```sql
-- 详见 sql/ 目录下的初始化脚本
source sql/init.sql;
```

修改 `backend/src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/vaeee_db
    username: root
    password: 你的密码
  redis:
    host: 127.0.0.1
    port: 6379
```

### 3️⃣ 启动 Redis

```bash
cd /你的Redis目录
redis-server redis.conf
```

### 4️⃣ 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端启动在 `http://localhost:8081/vaeee`

接口文档访问：`http://localhost:8081/vaeee/doc.html`

### 5️⃣ 启动前端

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
│   │   ├── common/
│   │   │   ├── constant/             # 常量定义
│   │   │   ├── enums/                # 枚举定义
│   │   │   ├── exception/            # 全局异常处理
│   │   │   ├── result/               # 统一响应封装
│   │   │   └── util/                 # 工具类
│   │   │       ├── RedisCaptchaCache.java      # 验证码 Redis 缓存
│   │   │       ├── RedisTransportCache.java    # 车次 Redis 缓存（Hash）
│   │   │       ├── SaTokenUtil.java            # Sa-Token 工具类
│   │   │       └── Captcha.java                # 验证码生成
│   │   ├── config/                   # 配置类
│   │   ├── controller/               # 控制器层
│   │   ├── DTO/                      # 数据传输对象
│   │   ├── entity/                   # 实体类
│   │   │   └── transport/            # 交通票务实体
│   │   ├── factory/                  # 工厂模式
│   │   ├── mapper/                   # MyBatis-Plus Mapper
│   │   ├── service/
│   │   │   ├── impl/                 # Service 实现
│   │   │   ├── inner/                # 内部事务服务
│   │   │   └── cache/                # 查询缓存
│   │   ├── strategy/transport/       # 交通策略
│   │   └── VO/                       # 视图对象
│   └── pom.xml
├── frontend/                         # Vue 3 前端
│   ├── src/
│   │   ├── api/                      # API 接口封装
│   │   ├── components/               # 公共组件
│   │   ├── layouts/                  # 布局组件
│   │   ├── router/                   # 路由配置
│   │   ├── stores/                   # Pinia 状态管理
│   │   ├── views/                    # 页面视图
│   │   └── ...
│   └── package.json
└── README.md
```

---

## 🧠 技术亮点

### 1️⃣ Redis 多场景集成（四大改造）

项目围绕 Redis 做了四次递进式改造，覆盖了缓存三大经典场景：

| 改造 | Redis 数据结构 | TTL | 解决的问题 |
|------|---------------|-----|-----------|
| **验证码缓存** | String | 5 分钟 | 验证码从内存→Redis，重启不丢失 |
| **Sa-Token 登录态** | String（自动管理） | 3 天 | Token 跨实例共享，支持多部署 |
| **热门车次缓存** | Hash | 12 小时 | 订单查询不再反复查数据库 |
| **缓存一致性** | DEL 清除 | — | 改车次时删缓存，保证数据一致 |

**技术选型**：车次缓存使用 **Redis Hash** 而非 String（JSON），原因：
- Hash 在小字段场景下比 String 省 30-40% 内存
- 可单独读写某个字段（改票价只需更新一个字段）
- 代价是需要手动处理序列化/反序列化

**缓存策略**：采用 **Cache-Aside 模式**（先写 DB 再删缓存），而非先删缓存再写 DB，避免并发场景下的缓存不一致问题。

### 2️⃣ 策略模式 + 工厂模式解耦交通类型

```
TransportStrategy（接口）
    ├── FlightStrategy    ✈️ 航班策略
    ├── TrainStrategy     🚄 火车策略
    └── BusStrategy       🚌 巴士策略
```

新增交通类型只需实现 `TransportStrategy` 接口，**零侵入**，符合开闭原则。

### 3️⃣ N+1 查询优化

```java
// 优化前：每次循环查数据库（N+1次查询）
for (Order order : orders) {
    user = userService.getById(order.getUserId());   // N次查询
    transport = transportService.getById(order.getTicketId()); // N次查询
}

// 优化后：2次查询 + 内存组装
List<Long> userIds = orders.stream().map(Order::getUserId).distinct().toList();
Map<Long, User> userMap = userService.listByIds(userIds)  // 1次查询
        .stream().collect(Collectors.toMap(User::getId, u -> u));

TransportCache cache = new TransportCache();
for (TransportStrategy strategy : strategyManager.getAll()) {
    strategy.buildCache(orders, cache);  // 1次查询（含所有类型）
}
```

### 4️⃣ 乐观锁防超卖

订单表使用 `@Version` 注解实现乐观锁，更新时自动校验版本号：

```java
@Version
private Integer version;

// 更新时版本号自动 +1，若版本不匹配则更新失败（影响行数为0）
```

---

## 🔐 权限体系

基于 **Sa-Token** 实现双角色权限控制：

| 角色 | 权限范围 |
|------|----------|
| 👤 普通用户 | 前台购票、订单管理、个人信息管理 |
| 🛡️ 管理员 | 后台所有管理功能 |

- Token 通过 Header 传递，自定义名称 `vaeeetoken`
- 支持多设备同时登录
- 路由守卫 + 接口鉴权双重校验

---

## 🧩 核心功能模块

### 用户端（前台）

| 功能 | 描述 |
|------|------|
| 🔑 登录/注册 | 用户注册、登录、验证码校验 |
| 🔍 票务查询 | 按出发地/目的地/时间筛选三种交通工具 |
| 🛒 购票下单 | 选票 → 选座 → 创建订单 |
| 💰 支付 | 账户余额支付，实时扣款 |
| 🔄 退票 | 已支付订单退票，金额返还 |
| ✈️ 改签 | 同交通类型下改签其他班次 |
| 👤 个人中心 | 信息修改、订单历史 |
| 📰 文章浏览 | 平台资讯浏览 |

### 管理端（后台）

| 功能 | 描述 |
|------|------|
| 👥 用户管理 | 用户增删改查 |
| ✈️ 航班/火车/巴士管理 | 车次增删改查（改车次自动清除 Redis 缓存） |
| 📋 订单管理 | 订单查询与修改 |
| 📝 文章管理 | 资讯发布与管理 |

---

## 📸 界面预览

| 页面 | 功能 |
|------|------|
| 🏠 前台首页 | 多交通方式查询入口 |
| 🎫 购票页面 | 选票下单流程 |
| 📋 订单列表 | 订单状态跟踪 |
| 🛡️ 后台管理 | 系统管理面板 |

---

## 🗺️ 后续规划

- [ ] 单元测试覆盖（JUnit + Mockito）
- [ ] Docker 容器化部署
- [ ] 超时未支付订单自动取消（RabbitMQ 延迟队列）
- [ ] Redis 缓存穿透/击穿防护
- [ ] CI/CD 集成（GitHub Actions）

---

## 📄 许可证

本项目基于 MIT 许可证开源。

## 👨‍💻 作者

- **池榕皓** — [GitHub](https://github.com/crh1235678)
- 邮箱：3053695484@qq.com

---

> 💡 项目持续迭代中，欢迎提 Issue 或 PR！
