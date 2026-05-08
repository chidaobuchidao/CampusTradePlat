# CampusTradePlat - 校园二手交易平台

<div align="center">

![Vue](https://img.shields.io/badge/Vue-2.6.11-4FC08D?style=for-the-badge&logo=vue.js)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.2.4-6DB33F?style=for-the-badge&logo=springboot)
![Java](https://img.shields.io/badge/Java-8-007396?style=for-the-badge&logo=java)
![MySQL](https://img.shields.io/badge/MySQL-5.7-4479A1?style=for-the-badge&logo=mysql)

校园二手物品交易平台，支持商品发布、在线聊天议价、订单管理、论坛交流等功能。

</div>

## 项目简介

基于 Spring Boot + Vue 2 的前后端分离校园二手交易平台。买家和卖家可以通过站内消息实时沟通、发送订单卡片、协商价格，完成从下单到评价的完整交易闭环。

## 功能特性

### 用户端

- **商品浏览与搜索** - 分类浏览、关键词搜索、个性化推荐（基于浏览历史）
- **商品详情** - 多图展示、新旧程度标记、砍价支持、收藏功能
- **站内聊天** - 买家卖家实时消息、订单卡片发送、修改价格、发货/收货确认
- **订单管理** - 完整订单状态机（待支付 → 已支付 → 已发货 → 已收货 → 已评价 → 已完成）
- **交易评价** - 买家对卖家评分（1-5星）+ 文字评价，展示在卖家个人主页
- **用户主页** - 个人信息、发布的商品列表、收到的评价与平均评分
- **个人中心** - 资料编辑、收货地址管理、收支数据统计（ECharts 图表）、订单统计、密码修改
- **论坛社区** - 发帖、浏览、评论互动

### 管理端

- 仪表盘数据概览（ECharts）
- 用户管理（封禁/解封、禁言）
- 商品管理
- 商品类别管理
- 评论管理

## 技术栈

| 层级 | 技术 | 说明 |
|------|------|------|
| 前端 | Vue 2.6 + Vue Router + Element UI | 单页应用，组件化开发 |
| 图表 | ECharts 4.8 | 收支统计、订单分布图表 |
| 富文本 | WangEditor 5 | 商品描述、论坛发帖 |
| 后端 | Spring Boot 2.2.4 + MyBatis | RESTful API，AOP 权限控制 |
| 数据库 | MySQL 5.7 | 10 张业务表 |
| 认证 | JWT + 自定义 @Protector 注解 | 基于 AOP 的接口鉴权 |

## 项目结构

```
campus-product-sys/          # 后端 (Spring Boot)
├── src/main/java/cn/kmbeast/
│   ├── controller/          # 接口层
│   ├── service/             # 业务层
│   ├── mapper/              # 数据访问层
│   ├── pojo/                # 实体、DTO、VO
│   ├── aop/                 # AOP 切面（权限、分页）
│   ├── context/             # ThreadLocal 用户上下文
│   └── Interceptor/         # JWT 拦截器
└── src/main/resources/
    ├── mapper/              # MyBatis XML
    └── application.yml      # 配置文件

campus-product-view/         # 前端 (Vue 2)
├── src/
│   ├── views/
│   │   ├── user/            # 用户端页面
│   │   ├── admin/           # 管理端页面
│   │   ├── login/           # 登录注册
│   │   └── register/
│   ├── components/          # 公共组件
│   ├── router/              # 路由配置
│   └── utils/               # 工具函数（请求封装、存储、插件）
└── package.json
```

## 快速开始

### 环境要求

- JDK 8
- Maven 3.x
- MySQL 5.7+
- Node.js 14+

### 数据库初始化

```bash
mysql -u root -p < campus-product.sql
```

默认管理员账号：`admin` / `123456`

### 后端启动

```bash
cd campus-product-sys
# 修改 application.yml 中的数据库连接信息
mvn spring-boot:run
```

后端默认运行在 `http://localhost:21090`，接口前缀 `/api/campus-product-sys/v1.0`

### 前端启动

```bash
cd campus-product-view
npm install
npm run dev
```

前端默认运行在 `http://localhost:8080`

## 开源协议

本项目基于 [MIT License](LICENSE) 开源，**仅供学习参考，严禁商用**。未经授权不得将本项目或其任何部分用于商业用途。

## 订单状态机

```
下单(0) → 付款(1) → 发货(2) → 收货(3) → 评价(4) → 完成(4)
  │                                                  │
  └──────────── 取消(5) ←────────────────────────────┘
```

| 状态 | 含义 | 操作方 |
|------|------|--------|
| 0 | 待支付 | 买家下单 |
| 1 | 已支付/待发货 | 买家付款 |
| 2 | 已发货/待收货 | 卖家发货 |
| 3 | 已收货/待评价 | 买家确认收货 |
| 4 | 交易完成 | 评价后自动完成 |
| 5 | 已取消 | 买卖双方可取消 |
