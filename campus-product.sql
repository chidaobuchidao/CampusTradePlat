-- =============================================
-- 校园二手交易平台 数据库建表脚本
-- 数据库: MySQL 5.7+
-- 字符集: utf8mb4
-- =============================================

CREATE DATABASE IF NOT EXISTS `campus-product` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `campus-product`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 用户表
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id`           int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户编号',
  `user_account` varchar(50)  DEFAULT NULL COMMENT '用户账号',
  `user_name`    varchar(50)  DEFAULT NULL COMMENT '用户昵称',
  `user_pwd`     varchar(100) DEFAULT NULL COMMENT '用户密码(MD5加密)',
  `user_avatar`  varchar(255) DEFAULT NULL COMMENT '用户头像URL',
  `user_email`   varchar(50)  DEFAULT NULL COMMENT '用户邮箱',
  `signature`    varchar(200) DEFAULT ''    COMMENT '个人签名',
  `user_role`    int(11)      DEFAULT NULL  COMMENT '用户角色(1:管理员 2:普通用户)',
  `is_login`     tinyint(1)   DEFAULT 0     COMMENT '封禁状态(0:正常 1:封禁)',
  `is_word`      tinyint(1)   DEFAULT 0     COMMENT '禁言状态(0:正常 1:禁言)',
  `create_time`  datetime     DEFAULT NULL  COMMENT '注册时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 默认管理员账号 admin / 123456
INSERT INTO `user` VALUES (1, 'admin', '管理员', '14e1b600b1fd579f47433b88e8d85291', NULL, 'admin@example.com', '', 1, 0, 0, NOW());

-- ----------------------------
-- 商品类别表
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id`       int(11)      NOT NULL AUTO_INCREMENT COMMENT '类别ID',
  `name`     varchar(255) DEFAULT NULL COMMENT '类别名称',
  `is_use`   tinyint(1)   DEFAULT 1    COMMENT '是否启用(0:禁用 1:启用)',
  `parent_id` int(11)     DEFAULT NULL COMMENT '父类别ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品类别表';

-- ----------------------------
-- 商品表
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id`          int(11)        NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name`        varchar(255)   DEFAULT NULL COMMENT '商品名称',
  `detail`      longtext       DEFAULT NULL COMMENT '商品描述',
  `cover_list`  longtext       DEFAULT NULL COMMENT '封面图列表(逗号分隔)',
  `old_level`   int(2)         DEFAULT NULL COMMENT '新旧程度(1:全新 2:几乎全新 3:有使用痕迹 4:较旧)',
  `category_id` int(11)        DEFAULT NULL COMMENT '所属分类ID',
  `user_id`     int(11)        DEFAULT NULL COMMENT '发布者用户ID',
  `inventory`   int(11)        DEFAULT 1    COMMENT '库存数量',
  `price`       decimal(10,2)  DEFAULT NULL COMMENT '价格',
  `is_bargain`  tinyint(1)     DEFAULT 0    COMMENT '是否支持砍价(0:否 1:是)',
  `create_time` datetime       DEFAULT NULL COMMENT '发布时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ----------------------------
-- 订单表
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id`           int(11)        NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `code`         varchar(255)   DEFAULT NULL COMMENT '订单号(UUID)',
  `user_id`      int(11)        DEFAULT NULL COMMENT '买家用户ID',
  `product_id`   int(11)        DEFAULT NULL COMMENT '商品ID',
  `price`        decimal(10,2)  DEFAULT NULL COMMENT '订单价格',
  `detail`       varchar(255)   DEFAULT NULL COMMENT '订单备注',
  `address`      varchar(255)   DEFAULT NULL COMMENT '收货地址',
  `phone`        varchar(20)    DEFAULT NULL COMMENT '联系电话',
  `remark`       varchar(255)   DEFAULT NULL COMMENT '附加备注',
  `trade_status` tinyint(1)     DEFAULT 0    COMMENT '交易状态(兼容旧逻辑)',
  `status`       int(11)        DEFAULT 0    COMMENT '订单状态(0:待支付 1:已支付 2:已发货 3:已收货 4:已完成 5:已取消)',
  `create_time`  datetime       DEFAULT NULL COMMENT '下单时间',
  `pay_time`     datetime       DEFAULT NULL COMMENT '付款时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ----------------------------
-- 评价/评论表
-- ----------------------------
DROP TABLE IF EXISTS `evaluations`;
CREATE TABLE `evaluations` (
  `id`           int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `parent_id`    int(11)      DEFAULT NULL COMMENT '父级评论ID(NULL为顶级评论)',
  `commenter_id` int(11)      DEFAULT NULL COMMENT '评论者用户ID',
  `replier_id`   int(11)      DEFAULT NULL COMMENT '被回复者用户ID',
  `content_type` varchar(100) DEFAULT NULL COMMENT '内容类型(PRODUCT/USER/FORUM等)',
  `content_id`   int(11)      DEFAULT NULL COMMENT '关联内容ID',
  `content`      varchar(500) DEFAULT NULL COMMENT '评论内容',
  `upvote_list`  longtext     DEFAULT NULL COMMENT '点赞用户ID列表(逗号分隔)',
  `rating`       int(11)      DEFAULT 5    COMMENT '评分(1-5,仅USER类型)',
  `order_id`     int(11)      DEFAULT NULL COMMENT '关联订单ID(仅USER类型)',
  `create_time`  datetime     DEFAULT NULL COMMENT '评论时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价/评论表';

-- ----------------------------
-- 消息表
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id`          int(11)      NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `user_id`     int(11)      DEFAULT NULL COMMENT '接收者ID(兼容旧字段)',
  `sender_id`   int(11)      DEFAULT NULL COMMENT '发送者用户ID',
  `receiver_id` int(11)      DEFAULT NULL COMMENT '接收者用户ID',
  `product_id`  int(11)      DEFAULT NULL COMMENT '关联商品ID',
  `content`     varchar(255) DEFAULT NULL COMMENT '消息内容',
  `is_read`     tinyint(1)   DEFAULT 0    COMMENT '是否已读(0:未读 1:已读)',
  `create_time` datetime     DEFAULT NULL COMMENT '发送时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';

-- ----------------------------
-- 互动行为表(收藏/浏览)
-- ----------------------------
DROP TABLE IF EXISTS `interaction`;
CREATE TABLE `interaction` (
  `id`          int(11)    NOT NULL AUTO_INCREMENT COMMENT '互动ID',
  `user_id`     int(11)    DEFAULT NULL COMMENT '用户ID',
  `product_id`  int(11)    DEFAULT NULL COMMENT '商品ID',
  `action_type` int(2)     DEFAULT NULL COMMENT '行为类型(1:收藏 2:浏览)',
  `action_time` datetime   DEFAULT NULL COMMENT '行为时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='互动行为表';

-- ----------------------------
-- 收货地址表
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `id`          int(11)      NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id`     int(11)      DEFAULT NULL COMMENT '用户ID',
  `name`        varchar(50)  DEFAULT NULL COMMENT '收件人姓名',
  `phone`       varchar(20)  DEFAULT NULL COMMENT '联系电话',
  `address`     varchar(255) DEFAULT NULL COMMENT '详细地址',
  `is_default`  int(11)      DEFAULT 0    COMMENT '是否默认地址(0:否 1:是)',
  `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- ----------------------------
-- 论坛帖子表
-- ----------------------------
DROP TABLE IF EXISTS `forum_post`;
CREATE TABLE `forum_post` (
  `id`         int(11)      NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `title`      varchar(255) DEFAULT NULL COMMENT '帖子标题',
  `content`    longtext     DEFAULT NULL COMMENT '帖子内容',
  `user_id`    int(11)      DEFAULT NULL COMMENT '发帖用户ID',
  `category`   varchar(100) DEFAULT NULL COMMENT '帖子分类',
  `view_count` int(11)      DEFAULT 0    COMMENT '浏览次数',
  `create_time` datetime    DEFAULT NULL COMMENT '发帖时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛帖子表';

-- ----------------------------
-- 操作日志表
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `id`          int(11)      NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id`     int(11)      DEFAULT NULL COMMENT '操作用户ID',
  `content`     varchar(255) DEFAULT NULL COMMENT '操作描述',
  `create_time` datetime     DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

SET FOREIGN_KEY_CHECKS = 1;
