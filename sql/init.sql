-- ============================================================
-- Vaeee 车票预定管理系统 - 数据库初始化脚本
-- 数据库: MySQL 8.0+
-- ============================================================

CREATE DATABASE IF NOT EXISTS `program` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `program`;

-- ============================================================
-- 1. 用户表
-- ============================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    `nickname`    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    `email`       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `avatar`      VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `balance`     DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '账户余额',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================================
-- 2. 管理员表
-- ============================================================
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
    `username`    VARCHAR(50)  NOT NULL COMMENT '管理员用户名',
    `password`    VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    `nickname`    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    `avatar`      VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `role`        VARCHAR(20)  NOT NULL DEFAULT 'ADMIN' COMMENT '角色',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- ============================================================
-- 3. 航班表
-- ============================================================
DROP TABLE IF EXISTS `flight`;
CREATE TABLE `flight` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '航班ID',
    `flight_number`   VARCHAR(20)  NOT NULL COMMENT '航班号',
    `origin`          VARCHAR(50)  NOT NULL COMMENT '出发地',
    `destination`     VARCHAR(50)  NOT NULL COMMENT '目的地',
    `departure_time`  DATETIME     NOT NULL COMMENT '出发时间',
    `arrival_time`    DATETIME     NOT NULL COMMENT '到达时间',
    `price`           DECIMAL(10,2) NOT NULL COMMENT '价格',
    `total_seats`     INT          NOT NULL DEFAULT 100 COMMENT '总座位数',
    `available_seats` INT          NOT NULL DEFAULT 100 COMMENT '可用座位数',
    `status`          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-停售，1-正常',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_origin_destination` (`origin`, `destination`),
    KEY `idx_departure_time` (`departure_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='航班表';

-- ============================================================
-- 4. 火车表
-- ============================================================
DROP TABLE IF EXISTS `train`;
CREATE TABLE `train` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '车次ID',
    `train_number`    VARCHAR(20)  NOT NULL COMMENT '车次号',
    `origin`          VARCHAR(50)  NOT NULL COMMENT '出发站',
    `destination`     VARCHAR(50)  NOT NULL COMMENT '到达站',
    `departure_time`  DATETIME     NOT NULL COMMENT '出发时间',
    `arrival_time`    DATETIME     NOT NULL COMMENT '到达时间',
    `price`           DECIMAL(10,2) NOT NULL COMMENT '价格',
    `total_seats`     INT          NOT NULL DEFAULT 500 COMMENT '总座位数',
    `available_seats` INT          NOT NULL DEFAULT 500 COMMENT '可用座位数',
    `status`          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-停售，1-正常',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_origin_destination` (`origin`, `destination`),
    KEY `idx_departure_time` (`departure_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='火车表';

-- ============================================================
-- 5. 巴士表
-- ============================================================
DROP TABLE IF EXISTS `bus`;
CREATE TABLE `bus` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '巴士ID',
    `bus_number`      VARCHAR(20)  NOT NULL COMMENT '巴士车次',
    `origin`          VARCHAR(50)  NOT NULL COMMENT '出发地',
    `destination`     VARCHAR(50)  NOT NULL COMMENT '目的地',
    `departure_time`  DATETIME     NOT NULL COMMENT '出发时间',
    `arrival_time`    DATETIME     NOT NULL COMMENT '到达时间',
    `price`           DECIMAL(10,2) NOT NULL COMMENT '价格',
    `total_seats`     INT          NOT NULL DEFAULT 40 COMMENT '总座位数',
    `available_seats` INT          NOT NULL DEFAULT 40 COMMENT '可用座位数',
    `status`          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-停售，1-正常',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_origin_destination` (`origin`, `destination`),
    KEY `idx_departure_time` (`departure_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='巴士表';

-- ============================================================
-- 6. 订单表（含乐观锁防超卖）
-- ============================================================
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `user_id`         BIGINT        NOT NULL COMMENT '用户ID',
    `transport_type`  VARCHAR(20)   NOT NULL COMMENT '交通类型：FLIGHT/TRAIN/BUS',
    `ticket_id`       BIGINT        NOT NULL COMMENT '票务ID（对应flight/train/bus的ID）',
    `total_price`     DECIMAL(10,2) NOT NULL COMMENT '总价',
    `seat_count`      INT           NOT NULL DEFAULT 1 COMMENT '购票数量',
    `status`          VARCHAR(20)   NOT NULL DEFAULT 'UNPAID' COMMENT '订单状态：UNPAID/PAID/CANCELLED',
    `version`         INT           NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `create_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_ticket_id` (`ticket_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ============================================================
-- 7. 交易流水表
-- ============================================================
DROP TABLE IF EXISTS `transaction`;
CREATE TABLE `transaction` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '流水ID',
    `user_id`     BIGINT        NOT NULL COMMENT '用户ID',
    `amount`      DECIMAL(10,2) NOT NULL COMMENT '交易金额',
    `type`        VARCHAR(20)   NOT NULL COMMENT '交易类型：RECHARGE/PURCHASE/REFUND/CHANGE',
    `pay_type`    VARCHAR(20)   NOT NULL DEFAULT 'BALANCE' COMMENT '支付方式：BALANCE',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易流水表';

-- ============================================================
-- 8. 文章表
-- ============================================================
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '文章ID',
    `title`       VARCHAR(100) NOT NULL COMMENT '文章标题',
    `content`     TEXT         NOT NULL COMMENT '文章内容',
    `summary`     VARCHAR(255) DEFAULT NULL COMMENT '文章摘要',
    `cover`       VARCHAR(255) DEFAULT NULL COMMENT '封面图',
    `type`        VARCHAR(20)  NOT NULL DEFAULT 'INFO' COMMENT '文章类型',
    `author`      VARCHAR(50)  DEFAULT NULL COMMENT '作者',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-草稿，1-发布',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

-- ============================================================
-- 初始化数据
-- ============================================================

-- 默认管理员（密码需BCrypt加密后设置，示例仅做占位）
INSERT INTO `admin` (`username`, `password`, `nickname`, `role`) VALUES
('admin', '$2a$10$your_bcrypt_hash_here', '系统管理员', 'ADMIN');

-- 示例航班数据
INSERT INTO `flight` (`flight_number`, `origin`, `destination`, `departure_time`, `arrival_time`, `price`, `total_seats`, `available_seats`) VALUES
('CA1234', '北京', '上海', '2026-07-16 08:00:00', '2026-07-16 10:30:00', 1200.00, 100, 100),
('MU5678', '上海', '广州', '2026-07-16 14:00:00', '2026-07-16 16:20:00', 980.00, 100, 100);

-- 示例火车数据
INSERT INTO `train` (`train_number`, `origin`, `destination`, `departure_time`, `arrival_time`, `price`, `total_seats`, `available_seats`) VALUES
('G123', '北京南', '上海虹桥', '2026-07-16 09:00:00', '2026-07-16 13:30:00', 550.00, 500, 500),
('D456', '杭州东', '南京南', '2026-07-16 10:00:00', '2026-07-16 12:00:00', 180.00, 500, 500);

-- 示例巴士数据
INSERT INTO `bus` (`bus_number`, `origin`, `destination`, `departure_time`, `arrival_time`, `price`, `total_seats`, `available_seats`) VALUES
('B001', '温州', '杭州', '2026-07-16 08:30:00', '2026-07-16 12:30:00', 120.00, 40, 40),
('B002', '宁波', '上海', '2026-07-16 13:00:00', '2026-07-16 16:00:00', 150.00, 40, 40);
