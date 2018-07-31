/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50722
Source Host           : localhost:3306
Source Database       : rolat

Target Server Type    : MYSQL
Target Server Version : 50722
File Encoding         : 65001

Date: 2018-06-01 11:33:16
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for menu
-- ----------------------------
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_date` datetime DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `icon` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `pid` bigint(20) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `sequence` int(11) DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `FKqcf9gem97gqa5qjm4d3elcqt5` (`pid`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of menu
-- ----------------------------
INSERT INTO `menu` VALUES ('1', null, null, '&#xe6b8;', '会员管理', null, null, '1', '1');
INSERT INTO `menu` VALUES ('2', null, null, '&#xe6a7;', '会员列表', '1', 'member-list.html', '3', '1');
INSERT INTO `menu` VALUES ('3', null, null, '&#xe6a7;', '会员删除', '1', 'member-del.html', '2', '1');
INSERT INTO `menu` VALUES ('4', null, null, '&#xe723;', '订单管理', null, null, '2', '1');
INSERT INTO `menu` VALUES ('5', null, null, '&#xe6a7;', '订单列表', '4', 'order-list.html', '1', '1');
INSERT INTO `menu` VALUES ('6', null, null, '&#xe696;', '系统管理', null, null, '3', '1');
INSERT INTO `menu` VALUES ('7', null, null, '&#xe6a7;', '图标字体', '6', 'unicode.html', '0', '1');
INSERT INTO `menu` VALUES ('8', null, null, '&#xe6a7;', '页面配置', '6', 'system/pageSetup', '1', '1');
INSERT INTO `menu` VALUES ('9', null, null, '&#xe6a7', '百度查询', '6', 'http://www.baidu.com', '2', '1');
INSERT INTO `menu` VALUES ('10', null, null, '&#xe6a7', '在线翻译', '6', 'http://fanyi.youdao.com/?mct=1&keyfrom=translate.m', '3', '1');
INSERT INTO `menu` VALUES ('11', null, null, '&#xe6a7', '系统管理', '6', 'http://localhost:8080/tmr_fp_ssh/systemFunction/toSystemFunction.do?viewId=8000700009', '4', '1');
INSERT INTO `menu` VALUES ('12', null, null, '&#xe6a7', 'layui_API', '6', 'http://www.layui.com/demo/form.html', '5', '1');
INSERT INTO `menu` VALUES ('13', null, null, '&#xe6a7', 'ztree_API', '6', 'http://www.treejs.cn/v3/api.php', '6', '1');
