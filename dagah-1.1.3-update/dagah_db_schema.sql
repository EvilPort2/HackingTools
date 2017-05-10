-- MySQL dump 10.13  Distrib 5.7.12, for osx10.9 (x86_64)
--
-- Host: 192.168.33.10    Database: shevirah
-- ------------------------------------------------------
-- Server version	5.5.5-10.1.19-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `attack_attributes`
--

DROP TABLE IF EXISTS `attack_attributes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `attack_attributes` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Attribute` varchar(45) NOT NULL,
  `Value` varchar(4500) NOT NULL,
  `AttackID` int(11) NOT NULL,
  `UserID` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `attacks`
--

DROP TABLE IF EXISTS `attacks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `attacks` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Label` varchar(255) NOT NULL,
  `UserID` int(11) NOT NULL,
  `Created` datetime DEFAULT NULL,
  `XML` varchar(5000) DEFAULT NULL COMMENT 'XML for attack portion of dagga command',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ID_UNIQUE` (`ID`),
  UNIQUE KEY `Label_UNIQUE` (`Label`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `campaign_attributes`
--

DROP TABLE IF EXISTS `campaign_attributes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `campaign_attributes` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Attribute` varchar(45) NOT NULL,
  `Value` varchar(4500) NOT NULL,
  `CampaignID` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `campaign_results`
--

DROP TABLE IF EXISTS `campaign_results`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `campaign_results` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `CampaignRunID` int(11) NOT NULL,
  `Number` varchar(45) DEFAULT NULL,
  `Name` varchar(255) DEFAULT NULL,
  `UserID` int(11) DEFAULT NULL,
  `ResultsString` text,
  `RunTime` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `CampaignRunID` (`CampaignRunID`),
  KEY `Number` (`Number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `campaign_runs`
--

DROP TABLE IF EXISTS `campaign_runs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `campaign_runs` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `UserID` int(11) NOT NULL,
  `CampaignID` int(11) NOT NULL,
  `Date` datetime DEFAULT NULL,
  `PhoneGroup` varchar(45) NOT NULL,
  `Result` varchar(500) DEFAULT NULL,
  `RunTime` datetime NOT NULL,
  `RunOutput` varchar(5000) DEFAULT NULL,
  `RunDirectory` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `campaigns`
--

DROP TABLE IF EXISTS `campaigns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `campaigns` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(255) NOT NULL,
  `Created` datetime NOT NULL,
  `UserID` int(11) NOT NULL,
  `LastRun` datetime DEFAULT NULL,
  `PhoneGroup` varchar(45) DEFAULT NULL,
  `XML` varchar(5000) DEFAULT NULL COMMENT 'XML for campaign to use in dagga engine',
  `StagingOutput` varchar(5000) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ID_UNIQUE` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `configuration`
--

DROP TABLE IF EXISTS `configuration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `configuration` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `UserID` int(11) NOT NULL,
  `KeyText` varchar(500) NOT NULL,
  `ValueText` varchar(500) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `curLicense`
--

DROP TABLE IF EXISTS `curLicense`;
/*!50001 DROP VIEW IF EXISTS `curLicense`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `curLicense` AS SELECT 
 1 AS `licType`,
 1 AS `licEdition`,
 1 AS `purchaseDT`*/;
SET character_set_client = @saved_cs_client;

DROP TABLE IF EXISTS `license_attribs`;

--
-- Table structure for table `license_attribs`
--
CREATE TABLE IF NOT EXISTS `license_attribs` (
  `createDT` bigint(20) NOT NULL,
  `licID` varchar(64) COLLATE utf8_bin NOT NULL,
  `licType` varchar(16) COLLATE utf8_bin NOT NULL,
  `licEdition` varchar(16) COLLATE utf8_bin NOT NULL,
  `licSeatNum` int(11) NOT NULL,
  `licTargets` int(11) NOT NULL,
  `licAttacks` int(11) NOT NULL,
  `licAgent` tinyint(1) NOT NULL,
  `licClientSide` tinyint(1) NOT NULL,
  `licMsgApp` tinyint(1) NOT NULL,
  `licExpDT` bigint(20) NOT NULL,
  `licOrg` varchar(62) COLLATE utf8_bin NOT NULL,
  `purchaseDT` datetime DEFAULT NULL, 
  PRIMARY KEY (`licID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Table structure for table `linkcampaignsattacks`
--

DROP TABLE IF EXISTS `linkcampaignsattacks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `linkcampaignsattacks` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `CampaignID` int(11) NOT NULL,
  `AttackID` int(11) NOT NULL,
  `Notes` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ID_UNIQUE` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `linkcampainphone`
--

DROP TABLE IF EXISTS `linkcampainphone`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `linkcampainphone` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `PhoneID` int(11) NOT NULL,
  `CampaignID` int(11) NOT NULL,
  `Notes` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ID_UNIQUE` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;

CREATE TABLE `version` (
  `version` varchar(10),
  PRIMARY KEY (`version`),
  UNIQUE KEY `version_UNIQUE` (`version`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `new_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notifications` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Comment` varchar(255) NOT NULL,
  `Time` datetime NOT NULL,
  `Type` varchar(30) NOT NULL,
  `UserID` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `phonebook`
--

DROP TABLE IF EXISTS `phonebook`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `phonebook` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Number` varchar(45) DEFAULT NULL,
  `Name` varchar (255) DEFAULT NULL,
  `UserID` int(11) NOT NULL,
  `PhoneGroup` varchar(100) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ID_UNIQUE` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `serveradmin`
--

DROP TABLE IF EXISTS `serveradmin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `serveradmin` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `RealName` varchar(45) NOT NULL,
  `Username` varchar(45) NOT NULL,
  `Password` varchar(45) NOT NULL,
  `Company` varchar(45) DEFAULT NULL,
  `LastLogin` datetime DEFAULT NULL,
  `Admin` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `User` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `License` varchar(45) DEFAULT NULL,
  `Acceptance` datetime DEFAULT NULL COMMENT 'License Acceptance Date',
  `ServerAdmin` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Username_UNIQUE` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `RealName` varchar(45) NOT NULL,
  `Username` varchar(45) NOT NULL,
  `Password` varchar(255) NOT NULL,
  `Company` varchar(45) DEFAULT NULL,
  `LastLogin` datetime DEFAULT NULL,
  `Admin` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `User` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `License` varchar(45) DEFAULT NULL,
  `Acceptance` datetime DEFAULT NULL COMMENT 'License Acceptance date',
  `ServerAdmin` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Username_UNIQUE` (`Username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Final view structure for view `curLicense`
--

/*!50001 DROP VIEW IF EXISTS `curLicense`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`192.168.%.%` SQL SECURITY DEFINER */
/*!50001 VIEW `curLicense` AS select 1 AS `licType`,`license_attribs`.`licEdition` AS `licEdition`,`license_attribs`.`purchaseDT` AS `purchaseDT` from `license_attribs` order by `license_attribs`.`purchaseDT` desc */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-11-29  0:18:23

DROP TABLE IF EXISTS `sms_templates`;

CREATE TABLE IF NOT EXISTS `sms_templates` (
  `id` int(11) NOT NULL,
  `text` varchar(1000) NOT NULL,
  `userId` int(11) NOT NULL,
  `created` datetime DEFAULT CURRENT_TIMESTAMP,
  `XML` varchar(5000) DEFAULT NULL COMMENT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Indexes for table `sms_templates`
--
ALTER TABLE `sms_templates`
  ADD PRIMARY KEY (`id`);
--
-- AUTO_INCREMENT for table `sms_templates`
--
ALTER TABLE `sms_templates`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
