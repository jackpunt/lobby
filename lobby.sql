-- MySQL dump 10.13  Distrib 8.0.30, for macos12 (x86_64)
--
-- Host: 127.0.0.1    Database: lobby
-- ------------------------------------------------------
-- Server version	8.0.30

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account_info`
--

DROP TABLE IF EXISTS `account_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account_info` (
  `id` bigint NOT NULL,
  `version` int DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_account_info__user_id` FOREIGN KEY (`id`) REFERENCES `jhi_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='extension to User (user is the owner of a stable of horses)\\nAccount Type indicates the payment properties and the League user is in.\\nmaybe this could fold into User.role ?';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_info`
--

LOCK TABLES `account_info` WRITE;
/*!40000 ALTER TABLE `account_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `asset`
--

DROP TABLE IF EXISTS `asset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asset` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `version` int DEFAULT NULL,
  `name` varchar(45) DEFAULT NULL COMMENT 'display name',
  `main` bit(1) DEFAULT NULL COMMENT 'IPlayer - can be player.main_jar',
  `auto` bit(1) DEFAULT NULL COMMENT 'bot is full-auto',
  `path` varchar(255) DEFAULT NULL COMMENT 'url to asset (class or document/resource)\\njar-path ! fqcn.of.asset.class\\njar-path ! path/inside/jar/document\\nURL=getenv(“ASSETBASE”)+path/to/release.jar ! user.supplied.Player',
  `include` varchar(255) DEFAULT NULL COMMENT 'comma-separated list of asset Ids',
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_asset__user_id` (`user_id`),
  CONSTRAINT `fk_asset__user_id` FOREIGN KEY (`user_id`) REFERENCES `jhi_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Assets owned by a member/user; (the horses) a virtual file-system?';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asset`
--

LOCK TABLES `asset` WRITE;
/*!40000 ALTER TABLE `asset` DISABLE KEYS */;
INSERT INTO `asset` VALUES (1,0,'FooPlayer1',_binary '',_binary '\0','jpeck/local/userAI/1.0-SNAPSHOT/userAI-1.0-SNAPSHOT.jar!userAI.player1.FooPlayer',NULL,2),(2,0,'FooPlayer2',_binary '',_binary '\0','jpeck/local/userAI/1.0-SNAPSHOT/userAI-1.0-SNAPSHOT.jar!userAI.player2.FooPlayer2',NULL,3),(3,0,'NormalAssetForMember1',_binary '',_binary '\0','1/fooPlayer-1.0.0.RELEASE.jar!userAI.player1.FooPlayer',NULL,2),(4,0,'HexGaia-DeleteThisRecord',_binary '\0',_binary '\0','4/hexagen-1.0.0.RELEASE.jar!gamma.player.Simple_Gaia',NULL,4),(5,0,'RiskPlayer2',_binary '',_binary '','2/riskPlayer-1.0.0.RC1.jar!userAI.player2.risk.RiskPlayer2',NULL,3);
/*!40000 ALTER TABLE `asset` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `DATABASECHANGELOG`
--

DROP TABLE IF EXISTS `DATABASECHANGELOG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `DATABASECHANGELOG` (
  `ID` varchar(255) NOT NULL,
  `AUTHOR` varchar(255) NOT NULL,
  `FILENAME` varchar(255) NOT NULL,
  `DATEEXECUTED` datetime NOT NULL,
  `ORDEREXECUTED` int NOT NULL,
  `EXECTYPE` varchar(10) NOT NULL,
  `MD5SUM` varchar(35) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `COMMENTS` varchar(255) DEFAULT NULL,
  `TAG` varchar(255) DEFAULT NULL,
  `LIQUIBASE` varchar(20) DEFAULT NULL,
  `CONTEXTS` varchar(255) DEFAULT NULL,
  `LABELS` varchar(255) DEFAULT NULL,
  `DEPLOYMENT_ID` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `DATABASECHANGELOG`
--

LOCK TABLES `DATABASECHANGELOG` WRITE;
/*!40000 ALTER TABLE `DATABASECHANGELOG` DISABLE KEYS */;
INSERT INTO `DATABASECHANGELOG` VALUES ('00000000000001','jhipster','config/liquibase/changelog/00000000000000_initial_schema.xml','2022-09-14 18:09:46',1,'EXECUTED','8:10b75a7a08f2b426a7225645705381c0','createTable tableName=jhi_user; createTable tableName=jhi_authority; createTable tableName=jhi_user_authority; addPrimaryKey tableName=jhi_user_authority; createTable tableName=jhi_persistent_token; addForeignKeyConstraint baseTableName=jhi_user_a...','',NULL,'4.12.0',NULL,NULL,'3204186021'),('20220809214418-1','jhipster','config/liquibase/changelog/20220809214418_added_entity_AccountInfo.xml','2022-09-14 18:09:46',2,'EXECUTED','8:2aee871f391d11ae90239311e2bcd25e','createTable tableName=account_info','',NULL,'4.12.0',NULL,NULL,'3204186021'),('20220809214418-1-data','jhipster','config/liquibase/changelog/20220809214418_added_entity_AccountInfo.xml','2022-09-14 18:09:46',3,'EXECUTED','8:d10d07692c10a73c977fa5505e92550b','loadData tableName=account_info','',NULL,'4.12.0','faker',NULL,'3204186021'),('20220809214518-1','jhipster','config/liquibase/changelog/20220809214518_added_entity_Asset.xml','2022-09-14 18:09:46',4,'EXECUTED','8:d924824ccd0e20b1f746b0c1b3b5d67c','createTable tableName=asset','',NULL,'4.12.0',NULL,NULL,'3204186021'),('20220809214518-1-data','jhipster','config/liquibase/changelog/20220809214518_added_entity_Asset.xml','2022-09-14 18:09:46',5,'EXECUTED','8:ba8a37731bea9a3ee8d36098fdd5f6a6','loadData tableName=asset','',NULL,'4.12.0','faker',NULL,'3204186021'),('20220809214618-1','jhipster','config/liquibase/changelog/20220809214618_added_entity_GameClass.xml','2022-09-14 18:09:46',6,'EXECUTED','8:2e4809fa7eb8310583217edb8019d259','createTable tableName=game_class; dropDefaultValue columnName=updated, tableName=game_class','',NULL,'4.12.0',NULL,NULL,'3204186021'),('20220809214618-1-data','jhipster','config/liquibase/changelog/20220809214618_added_entity_GameClass.xml','2022-09-14 18:09:46',7,'EXECUTED','8:b0ab2e78689f43809d8995c615185e2c','loadData tableName=game_class','',NULL,'4.12.0','faker',NULL,'3204186021'),('20220809214718-1','jhipster','config/liquibase/changelog/20220809214718_added_entity_GameInst.xml','2022-09-14 18:09:46',8,'EXECUTED','8:4d274978489d5d620a04952b3590faef','createTable tableName=game_inst; dropDefaultValue columnName=created, tableName=game_inst; dropDefaultValue columnName=started, tableName=game_inst; dropDefaultValue columnName=finished, tableName=game_inst; dropDefaultValue columnName=updated, ta...','',NULL,'4.12.0',NULL,NULL,'3204186021'),('20220809214718-1-data','jhipster','config/liquibase/changelog/20220809214718_added_entity_GameInst.xml','2022-09-14 18:09:46',9,'EXECUTED','8:88b94b38b55ceb8f54b3047c348b419b','loadData tableName=game_inst','',NULL,'4.12.0','faker',NULL,'3204186021'),('20220809214818-1','jhipster','config/liquibase/changelog/20220809214818_added_entity_GameInstProps.xml','2022-09-14 18:09:46',10,'EXECUTED','8:e9e90049a32e4e1eb9df159f16aef268','createTable tableName=game_inst_props; dropDefaultValue columnName=updated, tableName=game_inst_props','',NULL,'4.12.0',NULL,NULL,'3204186021'),('20220809214818-1-data','jhipster','config/liquibase/changelog/20220809214818_added_entity_GameInstProps.xml','2022-09-14 18:09:46',11,'EXECUTED','8:56112ced6b10732c969a1d39c58fe696','loadData tableName=game_inst_props','',NULL,'4.12.0','faker',NULL,'3204186021'),('20220809214918-1','jhipster','config/liquibase/changelog/20220809214918_added_entity_GamePlayer.xml','2022-09-14 18:09:46',12,'EXECUTED','8:fcaa3ea5c0f59d47cb80843a937d89d5','createTable tableName=game_player','',NULL,'4.12.0',NULL,NULL,'3204186021'),('20220809214918-1-data','jhipster','config/liquibase/changelog/20220809214918_added_entity_GamePlayer.xml','2022-09-14 18:09:46',13,'EXECUTED','8:ef2db935c49c980e42c97dcd96313bd8','loadData tableName=game_player','',NULL,'4.12.0','faker',NULL,'3204186021'),('20220809215018-1','jhipster','config/liquibase/changelog/20220809215018_added_entity_MemberGameProps.xml','2022-09-14 18:09:46',14,'EXECUTED','8:ff5005d20740f4bc4d5043bc6cfc5a9c','createTable tableName=member_game_props','',NULL,'4.12.0',NULL,NULL,'3204186021'),('20220809215018-1-data','jhipster','config/liquibase/changelog/20220809215018_added_entity_MemberGameProps.xml','2022-09-14 18:09:46',15,'EXECUTED','8:04682c433b48ab81679a07f067e9db97','loadData tableName=member_game_props','',NULL,'4.12.0','faker',NULL,'3204186021'),('20220809215118-1','jhipster','config/liquibase/changelog/20220809215118_added_entity_Player.xml','2022-09-14 18:09:46',16,'EXECUTED','8:dbddebe169420f2968e97670e9269427','createTable tableName=player; dropDefaultValue columnName=score_time, tableName=player; dropDefaultValue columnName=rank_time, tableName=player','',NULL,'4.12.0',NULL,NULL,'3204186021'),('20220809215118-1-data','jhipster','config/liquibase/changelog/20220809215118_added_entity_Player.xml','2022-09-14 18:09:46',17,'EXECUTED','8:45da220ffb0fed613066ee504affb6a1','loadData tableName=player','',NULL,'4.12.0','faker',NULL,'3204186021'),('20220809214418-2','jhipster','config/liquibase/changelog/20220809214418_added_entity_constraints_AccountInfo.xml','2022-09-14 18:09:46',18,'EXECUTED','8:0e0a33df2839f78903dd8b4f54cbc2d1','addForeignKeyConstraint baseTableName=account_info, constraintName=fk_account_info__user_id, referencedTableName=jhi_user','',NULL,'4.12.0',NULL,NULL,'3204186021'),('20220809214518-2','jhipster','config/liquibase/changelog/20220809214518_added_entity_constraints_Asset.xml','2022-09-14 18:09:46',19,'EXECUTED','8:9477dc53075a6ec2f4a9be030184423d','addForeignKeyConstraint baseTableName=asset, constraintName=fk_asset__user_id, referencedTableName=jhi_user','',NULL,'4.12.0',NULL,NULL,'3204186021'),('20220809214718-2','jhipster','config/liquibase/changelog/20220809214718_added_entity_constraints_GameInst.xml','2022-09-14 18:09:46',20,'EXECUTED','8:e846737149e8e4018e22b2b20cac0d75','addForeignKeyConstraint baseTableName=game_inst, constraintName=fk_game_inst__playera_id, referencedTableName=player; addForeignKeyConstraint baseTableName=game_inst, constraintName=fk_game_inst__playerb_id, referencedTableName=player; addForeignK...','',NULL,'4.12.0',NULL,NULL,'3204186021'),('20220809214818-2','jhipster','config/liquibase/changelog/20220809214818_added_entity_constraints_GameInstProps.xml','2022-09-14 18:09:46',21,'EXECUTED','8:147864d183480f142e23573772212161','addForeignKeyConstraint baseTableName=game_inst_props, constraintName=fk_game_inst_props__game_inst_id, referencedTableName=game_inst','',NULL,'4.12.0',NULL,NULL,'3204186021'),('20220809214918-2','jhipster','config/liquibase/changelog/20220809214918_added_entity_constraints_GamePlayer.xml','2022-09-14 18:09:46',22,'EXECUTED','8:29dc9f9d58a8c144eeb7bb2887544747','addForeignKeyConstraint baseTableName=game_player, constraintName=fk_game_player__game_inst_id, referencedTableName=game_inst; addForeignKeyConstraint baseTableName=game_player, constraintName=fk_game_player__player_id, referencedTableName=player','',NULL,'4.12.0',NULL,NULL,'3204186021'),('20220809215018-2','jhipster','config/liquibase/changelog/20220809215018_added_entity_constraints_MemberGameProps.xml','2022-09-14 18:09:46',23,'EXECUTED','8:70dcedeb83ceae0b425b53b231e2dced','addForeignKeyConstraint baseTableName=member_game_props, constraintName=fk_member_game_props__user_id, referencedTableName=jhi_user; addForeignKeyConstraint baseTableName=member_game_props, constraintName=fk_member_game_props__game_class_id, refer...','',NULL,'4.12.0',NULL,NULL,'3204186021'),('20220809215118-2','jhipster','config/liquibase/changelog/20220809215118_added_entity_constraints_Player.xml','2022-09-14 18:09:46',24,'EXECUTED','8:5afb147b592ed5495ceca0b5195b6c1c','addForeignKeyConstraint baseTableName=player, constraintName=fk_player__game_class_id, referencedTableName=game_class; addForeignKeyConstraint baseTableName=player, constraintName=fk_player__main_jar_id, referencedTableName=asset; addForeignKeyCon...','',NULL,'4.12.0',NULL,NULL,'3204186021');
/*!40000 ALTER TABLE `DATABASECHANGELOG` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `DATABASECHANGELOGLOCK`
--

DROP TABLE IF EXISTS `DATABASECHANGELOGLOCK`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `DATABASECHANGELOGLOCK` (
  `ID` int NOT NULL,
  `LOCKED` bit(1) NOT NULL,
  `LOCKGRANTED` datetime DEFAULT NULL,
  `LOCKEDBY` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `DATABASECHANGELOGLOCK`
--

LOCK TABLES `DATABASECHANGELOGLOCK` WRITE;
/*!40000 ALTER TABLE `DATABASECHANGELOGLOCK` DISABLE KEYS */;
INSERT INTO `DATABASECHANGELOGLOCK` VALUES (1,_binary '\0',NULL,NULL);
/*!40000 ALTER TABLE `DATABASECHANGELOGLOCK` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_class`
--

DROP TABLE IF EXISTS `game_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `game_class` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `version` int DEFAULT NULL,
  `name` varchar(45) NOT NULL COMMENT 'a unique name for this GameClass',
  `revision` varchar(24) DEFAULT NULL COMMENT 'major.minor.patch.TYPE [semver]',
  `launcher_path` varchar(255) DEFAULT NULL COMMENT 'jar-path ! fqcn.of.launcher\\nURL=getenv(“GAMEBASE”)+path/to/release.jar ! fqcn.launcher',
  `game_path` varchar(255) DEFAULT NULL COMMENT 'jar-path ! fqcn.of.game\\nURL=getenv(“GAMEBASE”)+path/to/release.jar ! pkg.main',
  `docs_path` varchar(255) DEFAULT NULL COMMENT 'doc-path/to/index.html\\nURL=getenv(“GAMEBASE”)+path/to/release.jar ! doc/path/index.html',
  `prop_names` varchar(255) DEFAULT NULL COMMENT 'a comma-separated string of property names for this GameClass\\nonly these prop_names can appear in the game_props.json associated with this game_class',
  `updated` datetime(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Which Game engine/jar to play.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_class`
--

LOCK TABLES `game_class` WRITE;
/*!40000 ALTER TABLE `game_class` DISABLE KEYS */;
INSERT INTO `game_class` VALUES (1,0,'Hexagen0a','0.0.0.A','gamma/gamma-main/1.0-SNAPSHOT/gamma-main-1.0-SNAPSHOT.jar!gamma.main.GammaLauncher','gamma/gamma-si/1.0-SNAPSHOT/gamma-si-1.0-SNAPSHOT.jar!gamma.games.hexagen.Hexagen','/docs/for/Hexagen0a',NULL,'2017-02-26 12:01:46.000000'),(2,3,'Hexagen0b','0.0.0.B','gamma/gamma-main/1.0-SNAPSHOT/gamma-main-1.0-SNAPSHOT.jar!gamma.main.GammaLauncher','gamma/gamma-si/1.0-SNAPSHOT/gamma-si-1.0-SNAPSHOT.jar!gamma.games.hexagen.Hexagen','/docs/for/Hexagen0b',NULL,'2017-02-26 12:01:46.000000'),(3,1,'HexagenJar0a','0.0.0.A','gamma/gamma-main/1.0-SNAPSHOT/gamma-main-1.0-SNAPSHOT.jar!gamma.main.GammaLauncher','gamma/gamma-si/1.0-SNAPSHOT/gamma-si-1.0-SNAPSHOT.jar!gamma.games.hexagen.Hexagen','docs/Hexagen',NULL,'2017-02-26 12:01:46.000000'),(4,0,'AiRisk','0.0.0.A','file:/c:/Data/Programs/java/airisk/target/classes!com.thegraid.AiRiskLauncher','file:/c:/Data/Programs/java/airisk/target/classes!com.thegraid.AiRisk','docs/AiRisk','self_attack','2017-01-29 12:12:49.000000');
/*!40000 ALTER TABLE `game_class` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_inst`
--

DROP TABLE IF EXISTS `game_inst`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `game_inst` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `version` int DEFAULT NULL,
  `game_name` varchar(64) DEFAULT NULL,
  `host_url` varchar(64) DEFAULT NULL,
  `passcode` varchar(64) DEFAULT NULL,
  `created` datetime(6) NOT NULL,
  `started` datetime(6),
  `finished` datetime(6),
  `updated` datetime(6) NOT NULL,
  `score_a` int DEFAULT NULL,
  `score_b` int DEFAULT NULL,
  `ticks` int DEFAULT NULL,
  `playera_id` bigint DEFAULT NULL,
  `playerb_id` bigint DEFAULT NULL,
  `game_class_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_game_inst__playera_id` (`playera_id`),
  KEY `fk_game_inst__playerb_id` (`playerb_id`),
  KEY `fk_game_inst__game_class_id` (`game_class_id`),
  CONSTRAINT `fk_game_inst__game_class_id` FOREIGN KEY (`game_class_id`) REFERENCES `game_class` (`id`),
  CONSTRAINT `fk_game_inst__playera_id` FOREIGN KEY (`playera_id`) REFERENCES `player` (`id`),
  CONSTRAINT `fk_game_inst__playerb_id` FOREIGN KEY (`playerb_id`) REFERENCES `player` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=169 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Instance of a Game.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_inst`
--

LOCK TABLES `game_inst` WRITE;
/*!40000 ALTER TABLE `game_inst` DISABLE KEYS */;
INSERT INTO `game_inst` VALUES (2,1070,'Hex1',NULL,NULL,'2010-06-20 10:38:31.000000',NULL,NULL,'2017-01-29 12:08:27.000000',NULL,NULL,NULL,1,2,2),(3,0,'Player2s-vs-Player1s-3',NULL,NULL,'2010-12-03 16:55:46.000000',NULL,NULL,'2017-01-29 12:08:27.000000',NULL,NULL,NULL,2,1,1),(88,1,'OpenGame1-88',NULL,NULL,'2010-12-13 18:24:10.000000',NULL,NULL,'2017-01-29 12:08:27.000000',NULL,NULL,NULL,1,NULL,1),(89,4,'Player2b-vs-Player2s-89',NULL,NULL,'2010-12-13 21:06:41.000000',NULL,NULL,'2017-01-29 12:08:27.000000',NULL,NULL,NULL,4,2,1),(92,4,'Player2b-vs-Player2b-92',NULL,NULL,'2010-12-14 14:32:46.000000',NULL,NULL,'2017-01-29 12:08:27.000000',NULL,NULL,NULL,1,4,1),(94,0,'Player2b-vs-*Open*',NULL,NULL,'2010-12-14 14:34:53.000000',NULL,NULL,'2017-01-29 12:08:27.000000',NULL,NULL,NULL,4,NULL,1),(96,1,'OpenGame-96',NULL,NULL,'2010-12-14 14:41:32.000000',NULL,NULL,'2017-01-29 12:08:27.000000',NULL,NULL,NULL,2,4,1),(97,1,'OpenGame-97',NULL,'MyCode','2010-12-14 14:42:48.000000',NULL,NULL,'2017-01-29 12:08:27.000000',NULL,NULL,NULL,4,2,1),(100,1,'OpenName',NULL,NULL,'2010-12-14 16:01:09.000000',NULL,NULL,'2017-01-29 12:08:27.000000',NULL,NULL,NULL,4,2,1),(148,24,'Game-148-is-READY','game5.thegraid.com:8445',NULL,'2011-01-12 13:54:42.000000',NULL,NULL,'2022-08-08 11:21:08.000000',NULL,NULL,NULL,4,1,1),(149,1,'Player2b-vs-Player1s',NULL,NULL,'2011-01-12 13:56:42.000000',NULL,NULL,'2017-01-29 12:08:27.000000',NULL,NULL,NULL,4,1,1),(152,1,'Player2b-vs-Player1s',NULL,NULL,'2011-01-12 14:04:42.000000',NULL,NULL,'2017-01-29 12:08:27.000000',NULL,NULL,NULL,4,1,1),(153,2,'Player1s-vs-Player2b','game4.thegraid.com:8444',NULL,'2011-01-12 14:14:42.000000','2012-10-02 22:11:59.000000',NULL,'2019-01-14 20:30:13.000000',NULL,NULL,NULL,1,4,1),(154,610,'Player1s-vs-Player2b','game5.thegraid.com:8445',NULL,'2011-01-12 14:24:00.000000',NULL,NULL,'2022-08-16 12:41:00.000000',NULL,NULL,NULL,1,4,1),(155,1,'Player2b-vs-Player1s',NULL,NULL,'2011-01-12 14:34:42.000000',NULL,NULL,'2017-01-29 12:08:27.000000',NULL,NULL,NULL,4,1,1),(156,2,'Player2s-vs-*Open*',NULL,NULL,'2011-01-12 14:44:42.000000',NULL,NULL,'2017-01-29 12:08:27.000000',NULL,NULL,NULL,2,4,1),(157,3,'Player2s-vs-Player2b',NULL,NULL,'2011-01-12 14:54:42.000000',NULL,NULL,'2017-01-29 12:08:27.000000',NULL,NULL,NULL,2,4,1),(158,1,'Player2s-vs-Player2b',NULL,NULL,'2011-01-12 15:04:42.000000',NULL,NULL,'2017-01-29 12:08:27.000000',NULL,NULL,NULL,2,4,1),(159,1,'Player2b-vs-Player2s',NULL,NULL,'2011-01-12 15:14:42.000000',NULL,NULL,'2017-01-29 12:08:27.000000',NULL,NULL,NULL,4,2,1),(160,2,'A2RiskPlayer-vs-A2RiskPlayer',NULL,NULL,'2011-01-12 15:24:42.000000',NULL,NULL,'2017-01-29 12:08:27.000000',NULL,NULL,NULL,5,5,4),(161,2,'A2RiskPlayer-vs-A2RiskPlayer',NULL,NULL,'2011-01-12 15:34:42.000000',NULL,NULL,'2017-01-29 12:08:27.000000',NULL,NULL,NULL,5,5,4),(162,1,'Player2b-vs-Player1s',NULL,NULL,'2011-01-12 15:44:42.000000',NULL,NULL,'2017-01-29 12:08:27.000000',NULL,NULL,NULL,4,1,1),(163,0,'Player2b-vs-Player1s',NULL,NULL,'2011-01-12 16:50:34.000000',NULL,NULL,'2017-01-29 12:08:27.000000',NULL,NULL,NULL,4,1,1),(164,1,'Player2b-vs-Player1s',NULL,NULL,'2011-01-12 16:55:19.000000',NULL,NULL,'2017-01-29 12:08:27.000000',NULL,NULL,NULL,4,1,1),(165,0,'Player2s-vs-*Open*',NULL,NULL,'2012-05-29 22:54:42.000000',NULL,NULL,'2017-01-29 12:08:27.000000',NULL,NULL,NULL,2,NULL,1),(166,0,'Player2b-vs-*Open*',NULL,NULL,'2016-12-13 12:34:53.000000',NULL,NULL,'2017-01-29 12:08:27.000000',NULL,NULL,NULL,4,NULL,1),(167,0,'newgame',NULL,NULL,'2016-12-13 12:35:12.000000',NULL,NULL,'2017-01-29 12:08:27.000000',NULL,NULL,NULL,4,NULL,1),(168,0,'newPlayer2sGame',NULL,NULL,'2016-12-17 11:08:21.000000',NULL,NULL,'2017-01-29 12:08:27.000000',NULL,NULL,NULL,2,NULL,1);
/*!40000 ALTER TABLE `game_inst` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_inst_props`
--

DROP TABLE IF EXISTS `game_inst_props`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `game_inst_props` (
  `id` bigint NOT NULL,
  `version` int DEFAULT NULL,
  `seed` bigint DEFAULT NULL,
  `map_name` varchar(45) DEFAULT NULL COMMENT 'NULL means use normal/standard',
  `map_size` int DEFAULT NULL COMMENT 'NULL means not-specified',
  `npc_count` int DEFAULT NULL COMMENT 'NULL means not-specified',
  `json_props` varchar(255) DEFAULT NULL COMMENT 'json form of game-specific properties',
  `updated` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_game_inst_props__game_inst_id` FOREIGN KEY (`id`) REFERENCES `game_inst` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='the final negotiated properties for GameInst';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_inst_props`
--

LOCK TABLES `game_inst_props` WRITE;
/*!40000 ALTER TABLE `game_inst_props` DISABLE KEYS */;
INSERT INTO `game_inst_props` VALUES (92,0,NULL,NULL,NULL,NULL,NULL,'2017-01-29 12:11:21.000000'),(148,7,555,NULL,NULL,NULL,NULL,'2017-01-29 12:11:21.000000'),(149,2,1234,NULL,666,NULL,NULL,'2017-01-29 12:11:21.000000'),(152,24,24,NULL,223,NULL,NULL,'2017-01-29 12:11:21.000000'),(153,2,55,NULL,NULL,NULL,NULL,'2017-01-29 12:11:21.000000'),(154,9,9999,NULL,444,NULL,NULL,'2017-01-29 12:11:21.000000'),(155,0,NULL,NULL,NULL,NULL,NULL,'2017-01-29 12:11:21.000000'),(156,1,234,NULL,6,NULL,NULL,'2017-12-28 15:13:03.000000'),(157,1,23,NULL,222,NULL,NULL,'2017-01-29 12:11:21.000000'),(158,3,23,NULL,NULL,NULL,NULL,'2017-01-29 12:11:21.000000'),(159,1,NULL,NULL,22,NULL,NULL,'2017-11-23 21:24:35.000000'),(160,10,9,'standard',1,NULL,'{ \"self_attack\" : 1 }','2017-01-29 12:11:21.000000'),(161,124,124,'standard',1,NULL,'{ \"self_attack\" : 0 }','2017-01-29 12:11:21.000000'),(162,0,NULL,NULL,NULL,NULL,NULL,'2017-01-29 12:11:21.000000'),(164,0,NULL,NULL,NULL,NULL,NULL,'2017-01-29 12:11:21.000000');
/*!40000 ALTER TABLE `game_inst_props` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_player`
--

DROP TABLE IF EXISTS `game_player`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `game_player` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `version` int DEFAULT NULL,
  `role` varchar(4) NOT NULL COMMENT 'assigned in Lobby',
  `ready` int NOT NULL COMMENT 'ack''d version; initial -1',
  `game_inst_id` bigint DEFAULT NULL,
  `player_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_game_player__game_inst_id` (`game_inst_id`),
  KEY `fk_game_player__player_id` (`player_id`),
  CONSTRAINT `fk_game_player__game_inst_id` FOREIGN KEY (`game_inst_id`) REFERENCES `game_inst` (`id`),
  CONSTRAINT `fk_game_player__player_id` FOREIGN KEY (`player_id`) REFERENCES `player` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=138 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='one of 2 Players (A or B) in a GameInst';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_player`
--

LOCK TABLES `game_player` WRITE;
/*!40000 ALTER TABLE `game_player` DISABLE KEYS */;
INSERT INTO `game_player` VALUES (26,3,'A',-1,2,1),(27,4,'B',-1,2,2),(28,0,'A',-1,3,2),(36,0,'A',-1,94,4),(38,0,'A',-1,96,2),(39,0,'A',-1,97,4),(42,0,'A',-1,100,4),(99,4,'A',7,148,4),(101,4,'A',-2,149,4),(106,14,'A',24,152,4),(107,26,'B',24,152,1),(108,2,'A',2,153,1),(109,6,'A',4,154,1),(110,8,'B',9,154,4),(111,0,'A',-1,155,4),(112,0,'A',-1,89,4),(113,0,'B',-1,89,2),(114,3,'B',2,153,4),(115,0,'A',-1,88,1),(116,0,'A',-1,92,1),(117,0,'B',-1,92,4),(118,0,'B',-1,96,4),(119,0,'A',-1,156,2),(120,0,'A',-1,157,2),(121,0,'B',-1,157,4),(122,0,'A',-1,158,2),(123,0,'A',-1,159,4),(124,0,'A',-1,160,5),(125,0,'A',-1,161,5),(126,2,'B',1,159,2),(127,0,'B',-1,100,2),(128,4,'B',3,158,4),(129,1,'B',-2,156,4),(130,0,'B',-1,97,2),(131,0,'A',-1,164,4),(132,4,'B',7,148,1),(134,0,'A',-1,165,2),(135,0,'A',-1,166,4),(136,0,'A',-1,167,4),(137,0,'A',-1,168,2);
/*!40000 ALTER TABLE `game_player` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jhi_authority`
--

DROP TABLE IF EXISTS `jhi_authority`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jhi_authority` (
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jhi_authority`
--

LOCK TABLES `jhi_authority` WRITE;
/*!40000 ALTER TABLE `jhi_authority` DISABLE KEYS */;
INSERT INTO `jhi_authority` VALUES ('ROLE_ADMIN'),('ROLE_USER');
/*!40000 ALTER TABLE `jhi_authority` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jhi_persistent_token`
--

DROP TABLE IF EXISTS `jhi_persistent_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jhi_persistent_token` (
  `series` varchar(20) NOT NULL,
  `user_id` bigint DEFAULT NULL,
  `token_value` varchar(20) NOT NULL,
  `token_date` date DEFAULT NULL,
  `ip_address` varchar(39) DEFAULT NULL,
  `user_agent` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`series`),
  KEY `fk_user_persistent_token` (`user_id`),
  CONSTRAINT `fk_user_persistent_token` FOREIGN KEY (`user_id`) REFERENCES `jhi_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jhi_persistent_token`
--

LOCK TABLES `jhi_persistent_token` WRITE;
/*!40000 ALTER TABLE `jhi_persistent_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `jhi_persistent_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jhi_user`
--

DROP TABLE IF EXISTS `jhi_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jhi_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `login` varchar(50) NOT NULL,
  `password_hash` varchar(60) NOT NULL,
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `email` varchar(191) DEFAULT NULL,
  `image_url` varchar(256) DEFAULT NULL,
  `activated` bit(1) NOT NULL,
  `lang_key` varchar(10) DEFAULT NULL,
  `activation_key` varchar(20) DEFAULT NULL,
  `reset_key` varchar(20) DEFAULT NULL,
  `created_by` varchar(50) NOT NULL,
  `created_date` timestamp NULL,
  `reset_date` timestamp NULL DEFAULT NULL,
  `last_modified_by` varchar(50) DEFAULT NULL,
  `last_modified_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_user_login` (`login`),
  UNIQUE KEY `ux_user_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jhi_user`
--

LOCK TABLES `jhi_user` WRITE;
/*!40000 ALTER TABLE `jhi_user` DISABLE KEYS */;
INSERT INTO `jhi_user` VALUES (1,'admin','$2a$10$gSAhZrxMllrbgj/kkK9UceBPpChGWJA7SYIb1Mqo.n5aNLq1/oRrC','Administrator','Administrator','admin@localhost','',_binary '','en',NULL,NULL,'system',NULL,NULL,'system',NULL),(2,'user1','$2a$10$VEjxo0jq2YG9Rbk2HmX9S.k1uZBGYUHdUcid3g/vfiEl7lwWgOH/K','Alice','User','user@localhost','',_binary '','en',NULL,NULL,'system',NULL,NULL,'system',NULL),(3,'user2','$2a$10$DzJe6TsyRi4ngB2za5U0SeT1ZrSoGplHpjReYXeuC2TKeIBfYs9na','Bob','User','user2@test.com',NULL,_binary '','en',NULL,NULL,'anonymousUser','2022-08-24 04:43:49',NULL,'anonymousUser','2022-08-24 04:43:49'),(4,'user4','$2a$10$xw/djvZenarPcNxgOjr5BeU7w2IDIlGsJSXpaz2nhPfGDmd7jZjAO','Charlie','User','user4@test.com',NULL,_binary '','en',NULL,NULL,'anonymousUser','2022-08-24 04:44:20',NULL,'anonymousUser','2022-08-24 04:44:20');
/*!40000 ALTER TABLE `jhi_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jhi_user_authority`
--

DROP TABLE IF EXISTS `jhi_user_authority`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jhi_user_authority` (
  `user_id` bigint NOT NULL,
  `authority_name` varchar(50) NOT NULL,
  PRIMARY KEY (`user_id`,`authority_name`),
  KEY `fk_authority_name` (`authority_name`),
  CONSTRAINT `fk_authority_name` FOREIGN KEY (`authority_name`) REFERENCES `jhi_authority` (`name`),
  CONSTRAINT `fk_user_id` FOREIGN KEY (`user_id`) REFERENCES `jhi_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jhi_user_authority`
--

LOCK TABLES `jhi_user_authority` WRITE;
/*!40000 ALTER TABLE `jhi_user_authority` DISABLE KEYS */;
INSERT INTO `jhi_user_authority` VALUES (1,'ROLE_ADMIN'),(1,'ROLE_USER'),(2,'ROLE_USER'),(3,'ROLE_USER'),(4,'ROLE_USER');
/*!40000 ALTER TABLE `jhi_user_authority` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `member_game_props`
--

DROP TABLE IF EXISTS `member_game_props`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member_game_props` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `version` int DEFAULT NULL,
  `seed` bigint DEFAULT NULL,
  `map_name` varchar(45) DEFAULT NULL COMMENT 'NULL means use normal/standard',
  `map_size` int DEFAULT NULL COMMENT 'NULL means not-specified',
  `npc_count` int DEFAULT NULL COMMENT 'NULL means not-specified',
  `json_props` varchar(255) DEFAULT NULL COMMENT 'json form of game-specific properties',
  `config_name` varchar(45) DEFAULT NULL COMMENT 'MyGameConfig',
  `user_id` bigint DEFAULT NULL,
  `game_class_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_member_game_props__user_id` (`user_id`),
  KEY `fk_member_game_props__game_class_id` (`game_class_id`),
  CONSTRAINT `fk_member_game_props__game_class_id` FOREIGN KEY (`game_class_id`) REFERENCES `game_class` (`id`),
  CONSTRAINT `fk_member_game_props__user_id` FOREIGN KEY (`user_id`) REFERENCES `jhi_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='member/player proposes properties for a new GameInst.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member_game_props`
--

LOCK TABLES `member_game_props` WRITE;
/*!40000 ALTER TABLE `member_game_props` DISABLE KEYS */;
/*!40000 ALTER TABLE `member_game_props` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `player`
--

DROP TABLE IF EXISTS `player`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `player` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `version` int DEFAULT NULL,
  `name` varchar(64) DEFAULT NULL COMMENT 'display name, as set by the owning Member.',
  `jhi_rank` int DEFAULT NULL COMMENT 'NULL until ranked',
  `score` int DEFAULT NULL COMMENT 'initial 0',
  `score_time` datetime(6),
  `rank_time` datetime(6),
  `display_client` varchar(64) DEFAULT NULL COMMENT 'URL path fragment to download display client from graid server.\\nProbably redo as reference to display_client table entry or an asset entry.',
  `game_class_id` bigint DEFAULT NULL,
  `main_jar_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_player__game_class_id` (`game_class_id`),
  KEY `fk_player__main_jar_id` (`main_jar_id`),
  KEY `fk_player__user_id` (`user_id`),
  CONSTRAINT `fk_player__game_class_id` FOREIGN KEY (`game_class_id`) REFERENCES `game_class` (`id`),
  CONSTRAINT `fk_player__main_jar_id` FOREIGN KEY (`main_jar_id`) REFERENCES `asset` (`id`),
  CONSTRAINT `fk_player__user_id` FOREIGN KEY (`user_id`) REFERENCES `jhi_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='A Member-owned PlayerAI [Asset] with a displayClient [Asset]\\na virtual player (the horse in a horse-race)';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `player`
--

LOCK TABLES `player` WRITE;
/*!40000 ALTER TABLE `player` DISABLE KEYS */;
INSERT INTO `player` VALUES (1,2,'Player1s',3,0,'2010-11-12 00:00:00.000000','2010-11-12 00:00:00.000000','https://game4.thegraid.com:8445/map',1,1,2),(2,0,'Player2s',2,0,NULL,NULL,'https://game4.thegraid.com:8445/map',1,2,3),(3,0,'Player2x',1,0,NULL,NULL,'https://game4.thegraid.com:8445/map',2,2,3),(4,0,'Player2b',1,500,'2010-12-01 00:00:00.000000','2010-12-01 00:00:00.000000','https://game4.thegraid.com:8445/map',1,2,3),(5,0,'A2RiskPlayer',1,0,NULL,NULL,'airisk/riskPlayer.html',4,5,3),(6,0,'ANewHexPlayer',15,0,NULL,NULL,'gammaFlex/GammaFlex.html',1,2,3),(7,0,'TempPlayer',16,0,NULL,NULL,'gammaFlex/GammaFlex.html',1,1,2);
/*!40000 ALTER TABLE `player` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-11-04 16:15:38
