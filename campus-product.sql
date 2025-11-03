/*
 Navicat Premium Dump SQL

 Source Server         : local-db-mysql
 Source Server Type    : MySQL
 Source Server Version : 50741 (5.7.41)
 Source Host           : localhost:3306
 Source Schema         : campus-product

 Target Server Type    : MySQL
 Target Server Version : 50741 (5.7.41)
 File Encoding         : 65001

 Date: 09/12/2024 16:09:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '商品类别ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '类别名',
  `is_use` tinyint(1) NULL DEFAULT NULL COMMENT '是否启用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '商品类别表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------

-- ----------------------------
-- Table structure for evaluations
-- ----------------------------
DROP TABLE IF EXISTS `evaluations`;
CREATE TABLE `evaluations`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` int(11) NULL DEFAULT NULL COMMENT '父级评论ID',
  `commenter_id` int(11) NULL DEFAULT NULL COMMENT '评论者ID',
  `replier_id` int(11) NULL DEFAULT NULL COMMENT '回复者ID',
  `content_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '内容类型',
  `content_id` int(11) NULL DEFAULT NULL COMMENT '内容ID',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '评论内容',
  `upvote_list` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '点赞列表，以\",\"分割',
  `create_time` datetime NULL DEFAULT NULL COMMENT '评论时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 147 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of evaluations
-- ----------------------------
INSERT INTO `evaluations` VALUES (4, NULL, 8, NULL, 'FOOD', 10, '我也遇到过这种问题', '1,7,8', '2024-05-23 11:33:01');
INSERT INTO `evaluations` VALUES (7, 4, 3, NULL, 'FOOD', 10, '大侠风范', '1,7,8,3', '2024-06-08 17:06:19');
INSERT INTO `evaluations` VALUES (8, 1, 3, 3, 'FOOD', 10, '一派胡言', '1,7,8', '2024-06-08 17:06:19');
INSERT INTO `evaluations` VALUES (13, 1, 3, 7, 'FOOD', 10, '这么神奇？', '1,8', '2024-06-08 17:06:19');
INSERT INTO `evaluations` VALUES (14, NULL, 8, 3, 'FOOD', 10, '大哥，我服了！', '1,8', '2024-06-08 17:06:19');
INSERT INTO `evaluations` VALUES (23, NULL, 3, NULL, 'FOOD', 10, '我的评论', '3', '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (27, 23, 3, 3, 'FOOD', 10, '什么事情', '3', '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (29, 14, 3, NULL, 'FOOD', 10, '先生。', '3', '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (40, NULL, 3, NULL, 'FOOD', 19, '测试评论啊', '3', '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (44, 40, 3, NULL, 'FOOD', 19, '回复', NULL, '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (45, 40, 3, 3, 'FOOD', 19, '回复', '3', '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (47, 40, 3, 3, 'FOOD', 19, '测试UU', '3', '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (49, NULL, 3, NULL, 'FOOD', 10, 'UUUIII', NULL, '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (50, 49, 3, NULL, 'FOOD', 10, '就是', NULL, '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (51, 4, 3, NULL, 'FOOD', 10, '就是', NULL, '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (52, 4, 3, 8, 'FOOD', 10, '哈哈哈', '3', '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (53, 49, 3, 3, 'FOOD', 10, '哈哈哈', '8', '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (54, NULL, 3, NULL, 'FOOD', 18, '测试', NULL, '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (55, 54, 3, NULL, 'FOOD', 18, 'niaho ', NULL, '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (58, 57, 3, NULL, 'FOOD', 19, '4324234', NULL, '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (59, 57, 3, 3, 'FOOD', 19, '54354354', NULL, '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (61, NULL, 3, NULL, 'FOOD', 19, '432432423', NULL, '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (83, NULL, 3, NULL, 'FOOD', 13, '健康', NULL, '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (84, NULL, 3, NULL, 'FOOD', 13, '丝滑', NULL, '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (85, NULL, 3, NULL, 'FOOD', 14, '哈哈哈哈哈', NULL, '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (87, NULL, 3, NULL, 'FOOD', 16, '？？？', NULL, '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (88, NULL, 3, NULL, 'FOOD', 16, '好好说话', NULL, '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (89, NULL, 3, NULL, 'FOOD', 19, '12121', '3', '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (90, 89, 3, NULL, 'FOOD', 19, '同意同意', NULL, '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (116, NULL, 9, NULL, 'FOOD', 3, '这是指标的评论！', NULL, '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (117, NULL, 9, NULL, 'FOOD', 5, '3232', NULL, '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (118, 117, 9, NULL, 'FOOD', 5, '4324324', NULL, '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (119, NULL, 9, NULL, 'FOOD', 12, '3342143', '9', '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (120, 119, 9, NULL, 'FOOD', 12, '43243243', '9', '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (121, 119, 9, 9, 'FOOD', 12, '432423432', '9', '2024-10-24 15:04:19');
INSERT INTO `evaluations` VALUES (122, NULL, 8, NULL, 'FOOD', 7, '好玩吗？？？', '10', '2024-11-13 23:02:43');
INSERT INTO `evaluations` VALUES (123, 122, 10, NULL, 'FOOD', 7, '真的好玩', '8', '2024-11-13 23:03:29');
INSERT INTO `evaluations` VALUES (124, 122, 8, 10, 'FOOD', 7, '什么时候去的？', '8', '2024-11-13 23:03:51');
INSERT INTO `evaluations` VALUES (127, 125, 3, 8, 'FOOD', 4, '真好啊', '8', '2024-11-14 15:46:07');
INSERT INTO `evaluations` VALUES (131, NULL, 8, NULL, 'FOOD', 6, '这里的风景怎么样？', NULL, '2024-11-16 19:02:12');
INSERT INTO `evaluations` VALUES (132, 131, 3, NULL, 'FOOD', 6, '真不错', NULL, '2024-11-16 19:02:46');
INSERT INTO `evaluations` VALUES (134, 133, 8, NULL, 'FOOD', 6, '我去过了', NULL, '2024-11-16 19:03:53');
INSERT INTO `evaluations` VALUES (135, NULL, 8, NULL, 'GOURMET', 6, '好不好做？', '8', '2024-11-27 21:53:37');
INSERT INTO `evaluations` VALUES (136, 135, 8, NULL, 'GOURMET', 6, '真的不错', NULL, '2024-11-27 21:53:48');
INSERT INTO `evaluations` VALUES (137, NULL, 8, NULL, 'GOURMET', 7, '4324324', NULL, '2024-11-28 14:30:04');
INSERT INTO `evaluations` VALUES (138, NULL, 8, NULL, 'GOURMET', 5, '4324324', '9', '2024-11-28 16:09:28');
INSERT INTO `evaluations` VALUES (139, NULL, 8, NULL, 'COOKBOOK', 4, '在一个小碗中，将适量生抽、老抽（少量，以免颜色过深）、冰糖、八角、桂皮、香叶和足够覆盖肉块的清水混合均匀，制成调味汁。\n将调味汁倒入砂锅中，确保液体能够没过肉块的一半至三分之二处。', NULL, '2024-11-30 20:49:59');
INSERT INTO `evaluations` VALUES (140, NULL, 8, NULL, 'COOKBOOK', 4, '真好吃', '8', '2024-11-30 20:50:04');
INSERT INTO `evaluations` VALUES (141, 140, 8, NULL, 'COOKBOOK', 4, '攒了赞了', '8', '2024-11-30 20:50:11');
INSERT INTO `evaluations` VALUES (142, NULL, 8, NULL, 'GOURMET', 4, '测试评论', NULL, '2024-12-04 18:10:37');
INSERT INTO `evaluations` VALUES (143, 138, 9, NULL, 'GOURMET', 5, '复刻了吗？', NULL, '2024-12-04 18:15:50');
INSERT INTO `evaluations` VALUES (145, NULL, 8, NULL, 'GOURMET', 11, '到底是谁发明的这个喝法！谢谢啊～ 真的又简单又好喝！以后谁还去奶茶店啊！ 红茶➕白糖炒一炒，炒到焦黄冒泡泡，加一点水煮开，最后倒牛奶就完成啦！（全程小火，糖炒的焦黄冒泡就赶紧加水！', '8', '2024-12-04 18:47:27');
INSERT INTO `evaluations` VALUES (146, 145, 8, NULL, 'GOURMET', 11, '！到底是谁发明的这个喝法！谢', '8', '2024-12-04 18:47:37');

-- ----------------------------
-- Table structure for interaction
-- ----------------------------
DROP TABLE IF EXISTS `interaction`;
CREATE TABLE `interaction`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '互动行为主键ID',
  `user_id` int(11) NULL DEFAULT NULL COMMENT '用户ID',
  `product_id` int(11) NULL DEFAULT NULL COMMENT '商品ID',
  `type` int(2) NULL DEFAULT NULL COMMENT '行为类型（1：收藏；2：浏览；3： 想要）',
  `create_time` datetime NULL DEFAULT NULL COMMENT '行为互动时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '互动行为信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of interaction
-- ----------------------------

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '消息表主键ID',
  `user_id` int(11) NULL DEFAULT NULL COMMENT '接收者用户ID',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '消息体',
  `is_read` tinyint(1) NULL DEFAULT NULL COMMENT '是否已经阅读',
  `create_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message
-- ----------------------------

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '行为日志主键ID',
  `user_id` int(11) NULL DEFAULT NULL COMMENT '用户ID',
  `detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '描述',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_log
-- ----------------------------

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单主键',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '订单号',
  `detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `product_id` int(11) NULL DEFAULT NULL COMMENT '商品ID',
  `buy_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '购买时的价格',
  `trade_status` tinyint(1) NULL DEFAULT NULL COMMENT '交易状态',
  `trade_time` datetime NULL DEFAULT NULL COMMENT '交易时间',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '订单信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '商品的ID，主键自增',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '商品名',
  `detail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '商品的简介',
  `cover_list` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '商品封面的列表',
  `old_level` int(2) NULL DEFAULT NULL COMMENT '新旧程度',
  `category_id` int(11) NULL DEFAULT NULL COMMENT '所属商品分类的ID',
  `user_id` int(11) NULL DEFAULT NULL COMMENT '发布者用户ID',
  `inventory` int(11) NULL DEFAULT NULL COMMENT '库存',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '价格',
  `is_bargain` tinyint(1) NULL DEFAULT NULL COMMENT '是否支持砍价',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '商品信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户编号',
  `user_account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户账号',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `user_pwd` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户密码',
  `user_avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户头像',
  `user_email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户邮箱',
  `user_role` int(11) NULL DEFAULT NULL COMMENT '用户角色',
  `is_login` tinyint(1) NULL DEFAULT NULL COMMENT '可登录状态(0：可用，1：不可用)',
  `is_word` tinyint(1) NULL DEFAULT NULL COMMENT '禁言状态(0：可用，1：不可用)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '用户注册时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', 'B站【程序员辰星】原创出品', '14e1b600b1fd579f47433b88e8d85291', '/api/book-manage-sys-api/v1.0/file/getFile?fileName=60c99f6Snipaste_2024-08-31_17-40-55.png', '1343243@qq.com', 1, 0, 0, '2024-12-04 12:53:05');
INSERT INTO `user` VALUES (2, 'zhangfan', '张帆', '14e1b600b1fd579f47433b88e8d85291', '/api/book-manage-sys-api/v1.0/file/getFile?fileName=ba728e7pic_6.jpg', '12311323@qq.com', 2, 0, 0, '2024-08-16 16:12:12');
INSERT INTO `user` VALUES (7, 'liran', '李冉', '14e1b600b1fd579f47433b88e8d85291', '/api/book-manage-sys-api/v1.0/file/getFile?fileName=5ee4cb8熊猫.png', '12434@qq.com', 1, 1, 1, '2024-10-19 06:39:12');
INSERT INTO `user` VALUES (8, 'lichen', '李琛', '14e1b600b1fd579f47433b88e8d85291', '/api/book-manage-sys-api/v1.0/file/getFile?fileName=109bac7pic_7.jpg', '12345@qq.com', 2, 0, 0, '2024-12-05 15:44:45');
INSERT INTO `user` VALUES (9, 'wangfangfang', '王芳芳', '14e1b600b1fd579f47433b88e8d85291', '/api/book-manage-sys-api/v1.0/file/getFile?fileName=b76542cpic_8.jpg', '1234@qq.com', 2, 0, 1, '2024-12-05 15:45:12');
INSERT INTO `user` VALUES (10, 'yuelang', '月朗', '14e1b600b1fd579f47433b88e8d85291', '/api/book-manage-sys-api/v1.0/file/getFile?fileName=c7568c2pic_5.jpg', '4532432@qq.com', 2, 0, 0, '2024-12-05 15:46:46');
INSERT INTO `user` VALUES (11, 'chenxin', '陈芯', '14e1b600b1fd579f47433b88e8d85291', '/api/book-manage-sys-api/v1.0/file/getFile?fileName=cdf71ffSnipaste_2024-11-29_18-22-44.png', '123432@qq.com', 2, 0, 0, '2024-12-05 15:47:08');
INSERT INTO `user` VALUES (12, 'hututu', '胡图图', '14e1b600b1fd579f47433b88e8d85291', '/api/book-manage-sys-api/v1.0/file/getFile?fileName=e694502Snipaste_2024-11-22_00-47-41.png', '4343@qq.com', 2, 0, 0, '2024-12-05 15:47:26');
INSERT INTO `user` VALUES (13, 'fanggui', '方贵', '14e1b600b1fd579f47433b88e8d85291', '/api/book-manage-sys-api/v1.0/file/getFile?fileName=3e28f7cSnipaste_2024-11-22_00-47-32.png', '43423@qq.com', 2, 0, 0, '2024-12-05 15:51:13');
INSERT INTO `user` VALUES (14, 'jiangxin', '江心', '14e1b600b1fd579f47433b88e8d85291', '/api/book-manage-sys-api/v1.0/file/getFile?fileName=e9a35efpic_7.jpg', '124343@qq.com', 2, 0, 0, '2024-12-05 15:54:50');
INSERT INTO `user` VALUES (15, 'moxin', '陌心', '14e1b600b1fd579f47433b88e8d85291', '/api/book-manage-sys-api/v1.0/file/getFile?fileName=be0e757Snipaste_2024-11-22_00-46-13.png', '654456@qq.com', 2, 1, 1, '2024-12-05 15:55:12');

SET FOREIGN_KEY_CHECKS = 1;
