/*
 Navicat Premium Data Transfer

 Source Server         : vrmlocal
 Source Server Type    : MySQL
 Source Server Version : 80033
 Source Host           : localhost:3306
 Source Schema         : art_dev

 Target Server Type    : MySQL
 Target Server Version : 80033
 File Encoding         : 65001

 Date: 27/06/2023 20:11:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for access
-- ----------------------------
DROP TABLE IF EXISTS `access`;
CREATE TABLE `access`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `access` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 39 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for atg_model
-- ----------------------------
DROP TABLE IF EXISTS `atg_model`;
CREATE TABLE `atg_model`  (
  `modelID` int NOT NULL AUTO_INCREMENT,
  `modelIdentifier` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `modelName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `modelContent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `modelClass` enum('A','B','C') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `projectID` int NULL DEFAULT NULL,
  PRIMARY KEY (`modelID`) USING BTREE,
  INDEX `model_id`(`modelIdentifier` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 977 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for atg_project
-- ----------------------------
DROP TABLE IF EXISTS `atg_project`;
CREATE TABLE `atg_project`  (
  `projectID` int NOT NULL AUTO_INCREMENT,
  `projectIdentifier` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `projectName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `projectContent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `projectClass` enum('A','B','C') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`projectID`) USING BTREE,
  INDEX `model_id`(`projectIdentifier` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 123 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for atg_requirement
-- ----------------------------
DROP TABLE IF EXISTS `atg_requirement`;
CREATE TABLE `atg_requirement`  (
  `requirementID` int NOT NULL AUTO_INCREMENT,
  `requirementIdentifier` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `requirementName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `requirementContent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `requirementCondition` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `requirementInput` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `requirementOutput` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `modelID` int NULL DEFAULT NULL,
  `rowRequirementID` int NULL DEFAULT NULL,
  PRIMARY KEY (`requirementID`) USING BTREE,
  INDEX `model`(`modelID` ASC) USING BTREE,
  INDEX `RowRequirement`(`rowRequirementID` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24216 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for atg_rowrequirement
-- ----------------------------
DROP TABLE IF EXISTS `atg_rowrequirement`;
CREATE TABLE `atg_rowrequirement`  (
  `rowRequirementID` int NOT NULL AUTO_INCREMENT,
  `rowRequirementIdentifier` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `rowRequirementName` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `rowRequirementContent` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `modelID` int NULL DEFAULT NULL,
  `rowReruirementModel` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
  PRIMARY KEY (`rowRequirementID`) USING BTREE,
  INDEX `model`(`modelID` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6782 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for atg_testcase
-- ----------------------------
DROP TABLE IF EXISTS `atg_testcase`;
CREATE TABLE `atg_testcase`  (
  `testcaseID` int NOT NULL AUTO_INCREMENT,
  `requirementID` int NULL DEFAULT NULL,
  `testcaseCondition` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `testcaseInput` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `testcaseOutput` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `testcaseType` enum('auto','manmade','reg_auto','reg_manmade') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `testcaseEvaluate` enum('EQUAL','NOT EQU','MORE THAN','LESS THAN') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `testcaseInputMaptoString` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `testcaseOutputMaptoString` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`testcaseID`) USING BTREE,
  INDEX `testcase_requirementid`(`requirementID` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 44701 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for atg_type
-- ----------------------------
DROP TABLE IF EXISTS `atg_type`;
CREATE TABLE `atg_type`  (
  `typeID` int NOT NULL AUTO_INCREMENT,
  `typeName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `typeRange` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `modelID` int NULL DEFAULT NULL,
  `typeSize` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `typeRowName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`typeID`) USING BTREE,
  INDEX `model`(`modelID` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8349 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for atg_variable
-- ----------------------------
DROP TABLE IF EXISTS `atg_variable`;
CREATE TABLE `atg_variable`  (
  `variableID` int NOT NULL AUTO_INCREMENT,
  `variableName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `variableComment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `typeID` int NULL DEFAULT NULL,
  `modelID` int NULL DEFAULT NULL,
  PRIMARY KEY (`variableID`) USING BTREE,
  INDEX `variables_type`(`typeID` ASC) USING BTREE,
  INDEX `variablestable_ibfk_2`(`modelID` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23342 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for role_access
-- ----------------------------
DROP TABLE IF EXISTS `role_access`;
CREATE TABLE `role_access`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `roleId` int NULL DEFAULT NULL,
  `accessId` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12315 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL,
  `role` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL,
  `createTime` bigint NULL DEFAULT NULL,
  `updateTime` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `用户名唯一索引`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for vrm_conceptlibrary
-- ----------------------------
DROP TABLE IF EXISTS `vrm_conceptlibrary`;
CREATE TABLE `vrm_conceptlibrary`  (
  `conceptId` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `conceptName` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `conceptDatatype` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `conceptRange` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL,
  `conceptValue` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL,
  `conceptAccuracy` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL,
  `conceptDependencyModeClass` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL,
  `conceptType` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `conceptDescription` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL,
  `systemId` int NULL DEFAULT NULL,
  PRIMARY KEY (`conceptId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1372 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for vrm_mode
-- ----------------------------
DROP TABLE IF EXISTS `vrm_mode`;
CREATE TABLE `vrm_mode`  (
  `modeId` int NOT NULL AUTO_INCREMENT,
  `modeName` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `initialStatus` int NULL DEFAULT NULL,
  `finalStatus` int NULL DEFAULT NULL,
  `value` int NOT NULL,
  `modeClassId` int NULL DEFAULT NULL,
  `modeClassName` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `modeDescription` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL,
  `systemId` int NOT NULL,
  PRIMARY KEY (`modeId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 518 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for vrm_modeclass
-- ----------------------------
DROP TABLE IF EXISTS `vrm_modeclass`;
CREATE TABLE `vrm_modeclass`  (
  `modeClassId` int NOT NULL AUTO_INCREMENT,
  `modeClassName` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `modeClassDescription` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL,
  `systemId` int NULL DEFAULT NULL,
  PRIMARY KEY (`modeClassId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 128 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for vrm_naturallanguagerequirement
-- ----------------------------
DROP TABLE IF EXISTS `vrm_naturallanguagerequirement`;
CREATE TABLE `vrm_naturallanguagerequirement`  (
  `reqId` int NOT NULL AUTO_INCREMENT,
  `systemId` int NOT NULL,
  `reqContent` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL,
  `reqExcelId` int NULL DEFAULT NULL,
  PRIMARY KEY (`reqId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1264 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for vrm_propernoun
-- ----------------------------
DROP TABLE IF EXISTS `vrm_propernoun`;
CREATE TABLE `vrm_propernoun`  (
  `properNounId` int NOT NULL AUTO_INCREMENT,
  `properNounName` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `properNounDescription` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL,
  `systemId` int NULL DEFAULT NULL,
  PRIMARY KEY (`properNounId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for vrm_standardrequirement
-- ----------------------------
DROP TABLE IF EXISTS `vrm_standardrequirement`;
CREATE TABLE `vrm_standardrequirement`  (
  `standardRequirementId` int NOT NULL AUTO_INCREMENT,
  `naturalLanguageReqId` int NOT NULL,
  `standardReqVariable` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `standardReqFunction` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `standardReqValue` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `standardReqCondition` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL,
  `standardReqEvent` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL,
  `standardReqContent` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `templateType` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `mode` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT '',
  `systemId` int NOT NULL,
  PRIMARY KEY (`standardRequirementId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1310 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for vrm_statemachine
-- ----------------------------
DROP TABLE IF EXISTS `vrm_statemachine`;
CREATE TABLE `vrm_statemachine`  (
  `stateMachineId` int NOT NULL AUTO_INCREMENT,
  `sourceState` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `endState` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `event` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `dependencyModeClass` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL,
  `systemId` int NOT NULL,
  `dependencyModeClassId` int NULL DEFAULT NULL,
  PRIMARY KEY (`stateMachineId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 461 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for vrm_systemproject
-- ----------------------------
DROP TABLE IF EXISTS `vrm_systemproject`;
CREATE TABLE `vrm_systemproject`  (
  `systemId` int NOT NULL AUTO_INCREMENT,
  `systemName` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `systemDecription` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL,
  PRIMARY KEY (`systemId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 56 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for vrm_type
-- ----------------------------
DROP TABLE IF EXISTS `vrm_type`;
CREATE TABLE `vrm_type`  (
  `typeId` int NOT NULL AUTO_INCREMENT,
  `typeName` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `dataType` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `typeRange` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL,
  `typeAccuracy` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL,
  `systemId` int NULL DEFAULT NULL,
  PRIMARY KEY (`typeId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 532 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
