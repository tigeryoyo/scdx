/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 50633
Source Host           : localhost:3306
Source Database       : scdx

Target Server Type    : MYSQL
Target Server Version : 50633
File Encoding         : 65001

Date: 2017-07-27 17:31:50
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for extfile
-- ----------------------------
DROP TABLE IF EXISTS `extfile`;
CREATE TABLE `extfile` (
  `extfile_id` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '泛数据文件id',
  `extfile_name` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '泛数据文件名称',
  `topic_id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '所属专题id',
  `source_type` varchar(32) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '泛数据类型：新闻、微博',
  `size` int(11) NOT NULL DEFAULT '0' COMMENT '文件大小',
  `line_number` int(11) NOT NULL DEFAULT '0' COMMENT '内容行数',
  `upload_time` datetime NOT NULL COMMENT '上传时间',
  `creator` varchar(32) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '上传人',
  PRIMARY KEY (`extfile_id`),
  KEY `file_fk_topic_id` (`topic_id`) USING BTREE,
  CONSTRAINT `file_fk_topic_id` FOREIGN KEY (`topic_id`) REFERENCES `topic` (`topic_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of extfile
-- ----------------------------

-- ----------------------------
-- Table structure for power
-- ----------------------------
DROP TABLE IF EXISTS `power`;
CREATE TABLE `power` (
  `power_id` int(11) NOT NULL AUTO_INCREMENT,
  `power_name` varchar(255) NOT NULL,
  `power_url` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`power_id`),
  UNIQUE KEY `unique_name` (`power_name`),
  KEY `unique_url` (`power_url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of power
-- ----------------------------

-- ----------------------------
-- Table structure for result
-- ----------------------------
DROP TABLE IF EXISTS `result`;
CREATE TABLE `result` (
  `res_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `res_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '泛数据文件名称',
  `topic_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `creator` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`res_id`),
  KEY `re_fk_issue_id` (`topic_id`),
  CONSTRAINT `re_fk_topic_id` FOREIGN KEY (`topic_id`) REFERENCES `topic` (`topic_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of result
-- ----------------------------

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `role_id` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(255) NOT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `unique_name` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES ('1', '开发者');
INSERT INTO `role` VALUES ('3', '用户');
INSERT INTO `role` VALUES ('2', '管理员');

-- ----------------------------
-- Table structure for role_power
-- ----------------------------
DROP TABLE IF EXISTS `role_power`;
CREATE TABLE `role_power` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) NOT NULL,
  `power_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `index_role_id` (`role_id`),
  KEY `index_power_id` (`power_id`),
  CONSTRAINT `rp_fk_power_id` FOREIGN KEY (`power_id`) REFERENCES `power` (`power_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `rp_fk_role_id` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role_power
-- ----------------------------

-- ----------------------------
-- Table structure for source_type
-- ----------------------------
DROP TABLE IF EXISTS `source_type`;
CREATE TABLE `source_type` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(32) COLLATE utf8_bin DEFAULT '''''' COMMENT '类型名称',
  PRIMARY KEY (`id`),
  KEY `index_source_type` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of source_type
-- ----------------------------

-- ----------------------------
-- Table structure for stdfile
-- ----------------------------
DROP TABLE IF EXISTS `stdfile`;
CREATE TABLE `stdfile` (
  `stdfile_id` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '泛数据文件id',
  `stdfile_name` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '泛数据文件名称',
  `topic_id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '事件的id',
  `size` int(11) NOT NULL COMMENT '文件大小',
  `line_number` int(11) NOT NULL COMMENT '内容行数',
  `upload_time` datetime NOT NULL COMMENT '上传时间',
  `creator` varchar(32) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '上传人',
  PRIMARY KEY (`stdfile_id`),
  KEY `file_fk_topic_id` (`topic_id`) USING BTREE,
  CONSTRAINT `stdfile_ibfk_1` FOREIGN KEY (`topic_id`) REFERENCES `topic` (`topic_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of stdfile
-- ----------------------------

-- ----------------------------
-- Table structure for stopword
-- ----------------------------
DROP TABLE IF EXISTS `stopword`;
CREATE TABLE `stopword` (
  `stopword_id` int(11) NOT NULL AUTO_INCREMENT,
  `word` varchar(40) NOT NULL DEFAULT '',
  `creator` varchar(32) NOT NULL DEFAULT '',
  `create_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`stopword_id`),
  UNIQUE KEY `word` (`word`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of stopword
-- ----------------------------

-- ----------------------------
-- Table structure for topic
-- ----------------------------
DROP TABLE IF EXISTS `topic`;
CREATE TABLE `topic` (
  `topic_id` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '专题id',
  `topic_name` varchar(32) COLLATE utf8_bin NOT NULL DEFAULT '''''' COMMENT '专题名称',
  `topic_type` varchar(32) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '专题类型',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `creator` varchar(32) COLLATE utf8_bin NOT NULL DEFAULT '''''' COMMENT '创建者',
  `last_operator` varchar(32) COLLATE utf8_bin NOT NULL DEFAULT '''''' COMMENT '最近一次操作人',
  `last_update_time` datetime NOT NULL COMMENT '最近一次更新时间',
  PRIMARY KEY (`topic_id`),
  KEY `index_name` (`topic_name`),
  KEY `index_creator` (`creator`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(32) NOT NULL,
  `password` varchar(32) NOT NULL,
  `email` varchar(32) DEFAULT NULL,
  `telphone` varchar(16) DEFAULT NULL,
  `true_name` varchar(32) NOT NULL,
  `create_date` date NOT NULL,
  `algorithm` int(11) NOT NULL DEFAULT '1',
  `granularity` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `unique_username` (`user_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'huster', 'huster', '424268503@qq.com', '15527081139', '开发者huster', '2017-07-25', '1', '1');
INSERT INTO `user` VALUES ('2', 'admin', 'admin', '15527081139@163.com', '15527081139', '管理员admin', '2017-07-25', '1', '1');
INSERT INTO `user` VALUES ('3', 'user01', 'user01', '135791033@qq.com', '15527081139', '用户01', '2017-07-25', '1', '1');

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `index_user_id` (`user_id`),
  KEY `index_role_id` (`role_id`),
  CONSTRAINT `ur_fk_role_id` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ur_fk_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES ('1', '1', '1');
INSERT INTO `user_role` VALUES ('2', '2', '2');
INSERT INTO `user_role` VALUES ('3', '3', '3');

-- ----------------------------
-- Table structure for weight
-- ----------------------------
DROP TABLE IF EXISTS `weight`;
CREATE TABLE `weight` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(32) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '类型名称',
  `weight` int(11) NOT NULL DEFAULT '0' COMMENT '网站权重',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='权重表';

-- ----------------------------
-- Records of weight
-- ----------------------------
