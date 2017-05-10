ALTER TABLE campaign_results ADD COLUMN `Name` varchar(255) DEFAULT NULL AFTER `Number`;
ALTER TABLE phonebook ADD COLUMN `Name` varchar(255) DEFAULT NULL AFTER `Number`;

DROP TABLE IF EXISTS `version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;

CREATE TABLE `version` (
  `version` varchar(10) DEFAULT '1.1.2',
  PRIMARY KEY (`version`),
  UNIQUE KEY `version_UNIQUE` (`version`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

