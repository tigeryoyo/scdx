/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 50633
Source Host           : localhost:3306
Source Database       : scdx

Target Server Type    : MYSQL
Target Server Version : 50633
File Encoding         : 65001

Date: 2017-12-22 10:50:51
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for domain_one
-- ----------------------------
DROP TABLE IF EXISTS `domain_one`;
CREATE TABLE `domain_one` (
  `uuid` varchar(64) NOT NULL,
  `url` varchar(64) NOT NULL,
  `name` varchar(32) NOT NULL default '其他',
  `column` varchar(32) default '',
  `type` varchar(32) default '',
  `rank` varchar(32) NOT NULL default '无',
  `incidence` varchar(32) default '',
  `weight` int(11) NOT NULL default '0',
  `maintenance_status` tinyint(1) NOT NULL default '0',
  `is_father` tinyint(1) NOT NULL default '0',
  `update_time` datetime NOT NULL,
  PRIMARY KEY  (`uuid`),
  UNIQUE KEY `url` (`url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of domain_one
-- ----------------------------

-- ----------------------------
-- Table structure for domain_two
-- ----------------------------
DROP TABLE IF EXISTS `domain_two`;
CREATE TABLE `domain_two` (
  `uuid` varchar(64) NOT NULL default '',
  `url` varchar(64) NOT NULL,
  `name` varchar(32) NOT NULL default '其他',
  `column` varchar(32) default '',
  `type` varchar(32) default NULL,
  `rank` varchar(32) NOT NULL default '无',
  `incidence` varchar(32) default '',
  `weight` int(11) NOT NULL default '0',
  `maintenance_status` tinyint(1) NOT NULL default '0',
  `father_uuid` varchar(64) NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY  (`uuid`),
  UNIQUE KEY `url` (`url`),
  KEY `father_uuid` (`father_uuid`),
  CONSTRAINT `father_uuid` FOREIGN KEY (`father_uuid`) REFERENCES `domain_one` (`uuid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of domain_two
-- ----------------------------

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
) ENGINE=InnoDB AUTO_INCREMENT=80 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of power
-- ----------------------------
INSERT INTO `power` VALUES ('1', '获取用户真实姓名', '/getCurrentUserTrueName');
INSERT INTO `power` VALUES ('2', '获取用户id', '/getCurrentUserId');
INSERT INTO `power` VALUES ('3', '创建专题', '/topic/create');
INSERT INTO `power` VALUES ('4', '删除专题', '/topic/delete');
INSERT INTO `power` VALUES ('5', '查询自有的所有专题', '/topic/queryOwnTopic');
INSERT INTO `power` VALUES ('6', '查询自有的所有专题分页', '/topic/queryOwnTopicCount');
INSERT INTO `power` VALUES ('7', '查询所有专题', '/topic/queryAllTopic');
INSERT INTO `power` VALUES ('8', '查询所有专题分页', '/topic/queryAllTopicCount');
INSERT INTO `power` VALUES ('9', '上传原始文件', '/extfile/upload');
INSERT INTO `power` VALUES ('10', '读取上传文件属性行', '/extfile/checkExtfile');
INSERT INTO `power` VALUES ('11', '根据时间范围查找基础文件', '/extfile/queryExtfilesByTimeRange');
INSERT INTO `power` VALUES ('12', '根据时间范围聚类', '/extfile/miningByTimeRange');
INSERT INTO `power` VALUES ('13', '根据id范围聚类', '/extfile/miningByExtfileIds');
INSERT INTO `power` VALUES ('14', '根据resultId查找结果，返回前台显示的list', '/result/getDisplayResultById');
INSERT INTO `power` VALUES ('15', '根据时间范围查找操作结果', '/result/queryResultByTimeRange');
INSERT INTO `power` VALUES ('16', '根据索引合并聚类结果中的某些类', '/result/combineResultItemsByIndices');
INSERT INTO `power` VALUES ('17', '根据索引删除聚类结果中的某些类', '/result/deleteResultItemsByIndices');
INSERT INTO `power` VALUES ('18', ' 重置结果，撤销对聚类结果的二次操作', '/result/resetResultById');
INSERT INTO `power` VALUES ('19', '删除索引为index的类簇内的指定数据集', '/result/deleteClusterItemsByIndices');
INSERT INTO `power` VALUES ('20', '根据resultId下载结果', '/result/downloadResultById');
INSERT INTO `power` VALUES ('21', '上传标准数据文件', '/stdfile/upload');
INSERT INTO `power` VALUES ('22', '根据标准数据id下载标准数据', '/stdfile/downloadStdfileByStdfileId');
INSERT INTO `power` VALUES ('23', '根据标准数据id下载报告数据', '/stdfile/downloadAbstractByStdfileId');
INSERT INTO `power` VALUES ('24', '根据时间范围查找标准数据文件', '/stdfile/queryStdfilesByTimeRange');
INSERT INTO `power` VALUES ('25', '分析标准数据文件', '/stdfile/analyzeByStdfileId');
INSERT INTO `power` VALUES ('26', '对准数据一个类统计出图', '/stdfile/statisticSingleSet');
INSERT INTO `power` VALUES ('27', '添加新用户', '/user/insertUser');
INSERT INTO `power` VALUES ('28', '根据用户id删除用户', '/user/deleteUserById');
INSERT INTO `power` VALUES ('29', '更改用户基本信息', '/user/updateUser');
INSERT INTO `power` VALUES ('30', '更改用户密码', '/user/updatePassword');
INSERT INTO `power` VALUES ('31', '查询当前用户信息', '/user/selectCurrentUser');
INSERT INTO `power` VALUES ('32', '查询所有用户信息', '/user/selectUserInfor');
INSERT INTO `power` VALUES ('33', '查询用户个数', '/user/selectUserCount');
INSERT INTO `power` VALUES ('34', '设置算法和粒度选择', '/user/setAlgorithmAndGranularity');
INSERT INTO `power` VALUES ('35', ' 获取当前用户的算法选择和粒度选择，显示到页面', '/user/getAlgorithmAndGranularity');
INSERT INTO `power` VALUES ('36', '查询所有的停用词', '/stopword/selectAllStopword');
INSERT INTO `power` VALUES ('37', '查询所有的停用词分页', '/stopword/selectStopwordCount');
INSERT INTO `power` VALUES ('38', '根据条件查询停用词', '/stopword/selectByCondition');
INSERT INTO `power` VALUES ('39', '根据id删除停用词', '/stopword/deleteStopwordById');
INSERT INTO `power` VALUES ('40', '插入停用词', '/stopword/insertStopwords');
INSERT INTO `power` VALUES ('41', '分页查询所有一级域名以及其对应的二级域名', '/domain/selectDomain');
INSERT INTO `power` VALUES ('42', '分页查询计算总共有多少页', '/domain/selectDomainCount');
INSERT INTO `power` VALUES ('43', '根据一级域名id查询一级域名以及其对应的二级域名信息', '/domain/selectDomainOneById');
INSERT INTO `power` VALUES ('44', '添加域名信息', '/domain/addDomain');
INSERT INTO `power` VALUES ('45', '根据二级域名id查询二级域名以及其对应的一级域名信息', '/domain/selectDomainTwoById');
INSERT INTO `power` VALUES ('46', '根据输入条件搜索满足条件的一级域名以及其对应的二级域名', '/domain/searchDomainOne');
INSERT INTO `power` VALUES ('47', '根据输入条件搜索满足条件的一级域名的个数 用于分页', '/domain/searchDomainOneCount');
INSERT INTO `power` VALUES ('48', '根据uuid删除一级域名 会有级联删除的效果，删除其对应的二级域名', '/domain/deleteDomainOne');
INSERT INTO `power` VALUES ('49', '根据uuid删除二级域名', '/domain/deleteDomainTwo');
INSERT INTO `power` VALUES ('50', '根据所给定信息更新一句域名信息', '/domain/updateDomainOne');
INSERT INTO `power` VALUES ('51', '根据所给定二级域名信息更新二级域名信息', '/domain/updateDomainTwo');
INSERT INTO `power` VALUES ('52', '上传域名信息excel文件 并根据文件内容更新或插入域名信息', '/domain/uploadDomainExcel');
INSERT INTO `power` VALUES ('53', '导出domain信息', '/domain/exportDomain');
INSERT INTO `power` VALUES ('54', '查询所有权重', '/weight/selectAllWeight');
INSERT INTO `power` VALUES ('55', '查询所有权重分页', '/weight/selectWeightCount');
INSERT INTO `power` VALUES ('56', '根据条件查询权重', '/weight/selectByCondition');
INSERT INTO `power` VALUES ('57', '插入权重', '/weight/insertWeight');
INSERT INTO `power` VALUES ('58', '删除权重', '/weight/deleteWeight');
INSERT INTO `power` VALUES ('59', '更新权重', '/weight/updateWeight');
INSERT INTO `power` VALUES ('60', '查询所有类型', '/sourceType/selectAllSourceType');
INSERT INTO `power` VALUES ('61', '查询所有类型分页', '/sourceType/selectSourceTypeCount');
INSERT INTO `power` VALUES ('62', '根据名字查询类型', '/sourceType/selectSourceTypeByName');
INSERT INTO `power` VALUES ('63', '根据id删除类型', '/sourceType/deleteSourceTypeById');
INSERT INTO `power` VALUES ('64', '插入类型', '/sourceType/insertSourceType');
INSERT INTO `power` VALUES ('65', '更新类型', '/sourceType/updateSourceType');
INSERT INTO `power` VALUES ('66', '上传容器测试文件', '/AlgorithmContainer/upload');
INSERT INTO `power` VALUES ('67', '下载容器结果文件', '/AlgorithmContainer/downloadResult');
INSERT INTO `power` VALUES ('68', '容器KMeans聚类', '/AlgorithmContainer/ClusterByKmeans');
INSERT INTO `power` VALUES ('69', '容器Canopy聚类', '/AlgorithmContainer/ClusterByCanopy');
INSERT INTO `power` VALUES ('70', '容器DBScan聚类', '/AlgorithmContainer/ClusterByDBScan');
INSERT INTO `power` VALUES ('71', '插入权限', '/power/insertPower');
INSERT INTO `power` VALUES ('72', '删除权限', '/power/deletePower');
INSERT INTO `power` VALUES ('73', '查询所有权限', '/power/selectAllPower');
INSERT INTO `power` VALUES ('74', '查询所有权限分页', '/power/selectPowerCount');
INSERT INTO `power` VALUES ('75', '分页，查询权限', '/power/selectPowerInfor');
INSERT INTO `power` VALUES ('76', '根据角色id查询角色所有权限', '/power/selectPowerByRoleId');
INSERT INTO `power` VALUES ('77', '改变角色权限', '/power/changeRolePower');
INSERT INTO `power` VALUES ('78', '查找所有角色', '/role/selectAllRole');
INSERT INTO `power` VALUES ('79', '修改权限信息', '/power/changePowerInfor');
INSERT INTO `power` VALUES ('80', '修改一级域名维护状态', '/domain/changeOneStatus');
INSERT INTO `power` VALUES ('81', '修改二级域名维护状态', '/domain/changeTwoStatus');
INSERT INTO `power` VALUES ('82', '合并类型', '/sourceType/mergedSourceType');

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
) ENGINE=InnoDB AUTO_INCREMENT=393 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role_power
-- ----------------------------
INSERT INTO `role_power` VALUES ('1', '1', '1');
INSERT INTO `role_power` VALUES ('2', '1', '2');
INSERT INTO `role_power` VALUES ('3', '1', '3');
INSERT INTO `role_power` VALUES ('4', '1', '4');
INSERT INTO `role_power` VALUES ('5', '1', '5');
INSERT INTO `role_power` VALUES ('6', '1', '6');
INSERT INTO `role_power` VALUES ('7', '1', '7');
INSERT INTO `role_power` VALUES ('8', '1', '8');
INSERT INTO `role_power` VALUES ('9', '1', '9');
INSERT INTO `role_power` VALUES ('10', '1', '10');
INSERT INTO `role_power` VALUES ('11', '1', '11');
INSERT INTO `role_power` VALUES ('12', '1', '12');
INSERT INTO `role_power` VALUES ('13', '1', '13');
INSERT INTO `role_power` VALUES ('14', '1', '14');
INSERT INTO `role_power` VALUES ('15', '1', '15');
INSERT INTO `role_power` VALUES ('16', '1', '16');
INSERT INTO `role_power` VALUES ('17', '1', '17');
INSERT INTO `role_power` VALUES ('18', '1', '18');
INSERT INTO `role_power` VALUES ('19', '1', '19');
INSERT INTO `role_power` VALUES ('20', '1', '20');
INSERT INTO `role_power` VALUES ('21', '1', '21');
INSERT INTO `role_power` VALUES ('22', '1', '22');
INSERT INTO `role_power` VALUES ('23', '1', '23');
INSERT INTO `role_power` VALUES ('24', '1', '24');
INSERT INTO `role_power` VALUES ('25', '1', '25');
INSERT INTO `role_power` VALUES ('26', '1', '26');
INSERT INTO `role_power` VALUES ('27', '1', '27');
INSERT INTO `role_power` VALUES ('28', '1', '28');
INSERT INTO `role_power` VALUES ('29', '1', '29');
INSERT INTO `role_power` VALUES ('30', '1', '30');
INSERT INTO `role_power` VALUES ('31', '1', '31');
INSERT INTO `role_power` VALUES ('32', '1', '32');
INSERT INTO `role_power` VALUES ('33', '1', '33');
INSERT INTO `role_power` VALUES ('34', '1', '34');
INSERT INTO `role_power` VALUES ('35', '1', '35');
INSERT INTO `role_power` VALUES ('36', '1', '36');
INSERT INTO `role_power` VALUES ('37', '1', '37');
INSERT INTO `role_power` VALUES ('38', '1', '38');
INSERT INTO `role_power` VALUES ('39', '1', '39');
INSERT INTO `role_power` VALUES ('40', '1', '40');
INSERT INTO `role_power` VALUES ('41', '1', '41');
INSERT INTO `role_power` VALUES ('42', '1', '42');
INSERT INTO `role_power` VALUES ('43', '1', '43');
INSERT INTO `role_power` VALUES ('44', '1', '44');
INSERT INTO `role_power` VALUES ('45', '1', '45');
INSERT INTO `role_power` VALUES ('46', '1', '46');
INSERT INTO `role_power` VALUES ('47', '1', '47');
INSERT INTO `role_power` VALUES ('48', '1', '48');
INSERT INTO `role_power` VALUES ('49', '1', '49');
INSERT INTO `role_power` VALUES ('50', '1', '50');
INSERT INTO `role_power` VALUES ('51', '1', '51');
INSERT INTO `role_power` VALUES ('52', '1', '52');
INSERT INTO `role_power` VALUES ('53', '1', '53');
INSERT INTO `role_power` VALUES ('54', '1', '54');
INSERT INTO `role_power` VALUES ('55', '1', '55');
INSERT INTO `role_power` VALUES ('56', '1', '56');
INSERT INTO `role_power` VALUES ('57', '1', '57');
INSERT INTO `role_power` VALUES ('58', '1', '58');
INSERT INTO `role_power` VALUES ('59', '1', '59');
INSERT INTO `role_power` VALUES ('60', '1', '60');
INSERT INTO `role_power` VALUES ('61', '1', '61');
INSERT INTO `role_power` VALUES ('62', '1', '62');
INSERT INTO `role_power` VALUES ('63', '1', '63');
INSERT INTO `role_power` VALUES ('64', '1', '64');
INSERT INTO `role_power` VALUES ('65', '1', '65');
INSERT INTO `role_power` VALUES ('66', '1', '66');
INSERT INTO `role_power` VALUES ('67', '1', '67');
INSERT INTO `role_power` VALUES ('68', '1', '68');
INSERT INTO `role_power` VALUES ('69', '1', '69');
INSERT INTO `role_power` VALUES ('70', '1', '70');
INSERT INTO `role_power` VALUES ('71', '1', '71');
INSERT INTO `role_power` VALUES ('72', '1', '72');
INSERT INTO `role_power` VALUES ('73', '1', '73');
INSERT INTO `role_power` VALUES ('74', '1', '74');
INSERT INTO `role_power` VALUES ('75', '1', '75');
INSERT INTO `role_power` VALUES ('76', '1', '76');
INSERT INTO `role_power` VALUES ('77', '1', '77');
INSERT INTO `role_power` VALUES ('78', '1', '78');
INSERT INTO `role_power` VALUES ('79', '2', '1');
INSERT INTO `role_power` VALUES ('80', '2', '2');
INSERT INTO `role_power` VALUES ('81', '2', '3');
INSERT INTO `role_power` VALUES ('82', '2', '4');
INSERT INTO `role_power` VALUES ('83', '2', '5');
INSERT INTO `role_power` VALUES ('84', '2', '6');
INSERT INTO `role_power` VALUES ('85', '2', '7');
INSERT INTO `role_power` VALUES ('86', '2', '8');
INSERT INTO `role_power` VALUES ('87', '2', '9');
INSERT INTO `role_power` VALUES ('88', '2', '10');
INSERT INTO `role_power` VALUES ('89', '2', '11');
INSERT INTO `role_power` VALUES ('90', '2', '12');
INSERT INTO `role_power` VALUES ('91', '2', '13');
INSERT INTO `role_power` VALUES ('92', '2', '14');
INSERT INTO `role_power` VALUES ('93', '2', '15');
INSERT INTO `role_power` VALUES ('94', '2', '16');
INSERT INTO `role_power` VALUES ('95', '2', '17');
INSERT INTO `role_power` VALUES ('96', '2', '18');
INSERT INTO `role_power` VALUES ('97', '2', '19');
INSERT INTO `role_power` VALUES ('98', '2', '20');
INSERT INTO `role_power` VALUES ('99', '2', '21');
INSERT INTO `role_power` VALUES ('100', '2', '22');
INSERT INTO `role_power` VALUES ('101', '2', '23');
INSERT INTO `role_power` VALUES ('102', '2', '24');
INSERT INTO `role_power` VALUES ('103', '2', '25');
INSERT INTO `role_power` VALUES ('104', '2', '26');
INSERT INTO `role_power` VALUES ('105', '2', '27');
INSERT INTO `role_power` VALUES ('106', '2', '28');
INSERT INTO `role_power` VALUES ('107', '2', '29');
INSERT INTO `role_power` VALUES ('108', '2', '30');
INSERT INTO `role_power` VALUES ('109', '2', '31');
INSERT INTO `role_power` VALUES ('110', '2', '32');
INSERT INTO `role_power` VALUES ('111', '2', '33');
INSERT INTO `role_power` VALUES ('112', '2', '34');
INSERT INTO `role_power` VALUES ('113', '2', '35');
INSERT INTO `role_power` VALUES ('114', '2', '36');
INSERT INTO `role_power` VALUES ('115', '2', '37');
INSERT INTO `role_power` VALUES ('116', '2', '38');
INSERT INTO `role_power` VALUES ('117', '2', '39');
INSERT INTO `role_power` VALUES ('118', '2', '40');
INSERT INTO `role_power` VALUES ('119', '2', '41');
INSERT INTO `role_power` VALUES ('120', '2', '42');
INSERT INTO `role_power` VALUES ('121', '2', '43');
INSERT INTO `role_power` VALUES ('122', '2', '44');
INSERT INTO `role_power` VALUES ('123', '2', '45');
INSERT INTO `role_power` VALUES ('124', '2', '46');
INSERT INTO `role_power` VALUES ('125', '2', '47');
INSERT INTO `role_power` VALUES ('126', '2', '48');
INSERT INTO `role_power` VALUES ('127', '2', '49');
INSERT INTO `role_power` VALUES ('128', '2', '50');
INSERT INTO `role_power` VALUES ('129', '2', '51');
INSERT INTO `role_power` VALUES ('130', '2', '52');
INSERT INTO `role_power` VALUES ('131', '2', '53');
INSERT INTO `role_power` VALUES ('132', '2', '54');
INSERT INTO `role_power` VALUES ('133', '2', '55');
INSERT INTO `role_power` VALUES ('134', '2', '56');
INSERT INTO `role_power` VALUES ('135', '2', '57');
INSERT INTO `role_power` VALUES ('136', '2', '58');
INSERT INTO `role_power` VALUES ('137', '2', '59');
INSERT INTO `role_power` VALUES ('138', '2', '60');
INSERT INTO `role_power` VALUES ('139', '2', '61');
INSERT INTO `role_power` VALUES ('140', '2', '62');
INSERT INTO `role_power` VALUES ('141', '2', '63');
INSERT INTO `role_power` VALUES ('142', '2', '64');
INSERT INTO `role_power` VALUES ('143', '2', '65');
INSERT INTO `role_power` VALUES ('144', '2', '66');
INSERT INTO `role_power` VALUES ('145', '2', '67');
INSERT INTO `role_power` VALUES ('146', '2', '68');
INSERT INTO `role_power` VALUES ('147', '2', '69');
INSERT INTO `role_power` VALUES ('148', '2', '70');
INSERT INTO `role_power` VALUES ('149', '2', '73');
INSERT INTO `role_power` VALUES ('150', '2', '74');
INSERT INTO `role_power` VALUES ('151', '2', '75');
INSERT INTO `role_power` VALUES ('152', '2', '76');
INSERT INTO `role_power` VALUES ('153', '2', '77');
INSERT INTO `role_power` VALUES ('154', '2', '78');
INSERT INTO `role_power` VALUES ('334', '3', '1');
INSERT INTO `role_power` VALUES ('335', '3', '2');
INSERT INTO `role_power` VALUES ('336', '3', '3');
INSERT INTO `role_power` VALUES ('337', '3', '4');
INSERT INTO `role_power` VALUES ('338', '3', '5');
INSERT INTO `role_power` VALUES ('339', '3', '6');
INSERT INTO `role_power` VALUES ('340', '3', '9');
INSERT INTO `role_power` VALUES ('341', '3', '10');
INSERT INTO `role_power` VALUES ('342', '3', '11');
INSERT INTO `role_power` VALUES ('343', '3', '12');
INSERT INTO `role_power` VALUES ('344', '3', '13');
INSERT INTO `role_power` VALUES ('345', '3', '14');
INSERT INTO `role_power` VALUES ('346', '3', '15');
INSERT INTO `role_power` VALUES ('347', '3', '16');
INSERT INTO `role_power` VALUES ('348', '3', '17');
INSERT INTO `role_power` VALUES ('349', '3', '18');
INSERT INTO `role_power` VALUES ('350', '3', '19');
INSERT INTO `role_power` VALUES ('351', '3', '20');
INSERT INTO `role_power` VALUES ('352', '3', '21');
INSERT INTO `role_power` VALUES ('353', '3', '22');
INSERT INTO `role_power` VALUES ('354', '3', '23');
INSERT INTO `role_power` VALUES ('355', '3', '24');
INSERT INTO `role_power` VALUES ('356', '3', '25');
INSERT INTO `role_power` VALUES ('357', '3', '26');
INSERT INTO `role_power` VALUES ('358', '3', '29');
INSERT INTO `role_power` VALUES ('359', '3', '30');
INSERT INTO `role_power` VALUES ('360', '3', '31');
INSERT INTO `role_power` VALUES ('361', '3', '34');
INSERT INTO `role_power` VALUES ('362', '3', '35');
INSERT INTO `role_power` VALUES ('363', '3', '36');
INSERT INTO `role_power` VALUES ('364', '3', '37');
INSERT INTO `role_power` VALUES ('365', '3', '38');
INSERT INTO `role_power` VALUES ('366', '3', '39');
INSERT INTO `role_power` VALUES ('367', '3', '40');
INSERT INTO `role_power` VALUES ('368', '3', '41');
INSERT INTO `role_power` VALUES ('369', '3', '42');
INSERT INTO `role_power` VALUES ('370', '3', '43');
INSERT INTO `role_power` VALUES ('371', '3', '44');
INSERT INTO `role_power` VALUES ('372', '3', '45');
INSERT INTO `role_power` VALUES ('373', '3', '46');
INSERT INTO `role_power` VALUES ('374', '3', '47');
INSERT INTO `role_power` VALUES ('375', '3', '48');
INSERT INTO `role_power` VALUES ('376', '3', '49');
INSERT INTO `role_power` VALUES ('377', '3', '50');
INSERT INTO `role_power` VALUES ('378', '3', '51');
INSERT INTO `role_power` VALUES ('379', '3', '52');
INSERT INTO `role_power` VALUES ('380', '3', '53');
INSERT INTO `role_power` VALUES ('381', '3', '54');
INSERT INTO `role_power` VALUES ('382', '3', '55');
INSERT INTO `role_power` VALUES ('383', '3', '56');
INSERT INTO `role_power` VALUES ('384', '3', '60');
INSERT INTO `role_power` VALUES ('385', '3', '61');
INSERT INTO `role_power` VALUES ('386', '3', '62');
INSERT INTO `role_power` VALUES ('387', '3', '66');
INSERT INTO `role_power` VALUES ('388', '3', '67');
INSERT INTO `role_power` VALUES ('389', '3', '68');
INSERT INTO `role_power` VALUES ('390', '3', '69');
INSERT INTO `role_power` VALUES ('391', '3', '70');
INSERT INTO `role_power` VALUES ('392', '1', '79');
INSERT INTO `role_power` VALUES ('393', '1', '80');
INSERT INTO `role_power` VALUES ('394', '1', '81');
INSERT INTO `role_power` VALUES ('473', '1', '82');
INSERT INTO `role_power` VALUES ('550', '2', '80');
INSERT INTO `role_power` VALUES ('551', '2', '81');
INSERT INTO `role_power` VALUES ('552', '2', '82');

-- ----------------------------
-- Table structure for source_type
-- ----------------------------
DROP TABLE IF EXISTS `source_type`;
CREATE TABLE `source_type` (
  `id` int(11) unsigned NOT NULL auto_increment,
  `name` varchar(32) collate utf8_bin NOT NULL default '''''' COMMENT '类型名称',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `index_source_type` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of source_type
-- ----------------------------
INSERT INTO `source_type` VALUES ('2', '微博');
INSERT INTO `source_type` VALUES ('1', '新闻');

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
  `datatime` varchar(1024) COLLATE utf8_bin DEFAULT NULL COMMENT '标准数据文件内数据的时间list',
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of stopword
-- ----------------------------
INSERT INTO `stopword` VALUES ('1', 'qwe', 'admin', '2017-10-30 13:36:20');

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
-- Records of topic
-- ----------------------------

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
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'huster', 'huster', '155@qq.com', '15527081139', '开发者', '2017-11-21', '1', '1');
INSERT INTO `user` VALUES ('6', 'admin', 'admini', '15527081139@163.com', '15527081139', '管理员', '2017-11-01', '1', '1');
INSERT INTO `user` VALUES ('8', 'user01', 'user01', '4242@qq.com', '15527081139', 'user', '2017-11-21', '1', '1');

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
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES ('1', '1', '1');
INSERT INTO `user_role` VALUES ('6', '6', '2');
INSERT INTO `user_role` VALUES ('8', '8', '3');

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
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='权重表';

-- ----------------------------
-- Records of weight
-- ----------------------------
INSERT INTO `weight` VALUES ('1', '新闻', '1');
INSERT INTO `weight` VALUES ('2', '报纸', '1');
INSERT INTO `weight` VALUES ('3', '论坛', '20');
INSERT INTO `weight` VALUES ('4', '问答', '1');
INSERT INTO `weight` VALUES ('5', '博客', '5');
INSERT INTO `weight` VALUES ('6', '中央', '100');
INSERT INTO `weight` VALUES ('7', '省级', '20');
INSERT INTO `weight` VALUES ('8', '其他', '1');
INSERT INTO `weight` VALUES ('9', '视频', '4');
INSERT INTO `weight` VALUES ('10', '微博', '1');
INSERT INTO `weight` VALUES ('11', '微信', '5');
INSERT INTO `weight` VALUES ('12', '贴吧', '1');
INSERT INTO `weight` VALUES ('14', '手机', '5');
