DROP TABLE IF EXISTS `version`;

CREATE TABLE IF NOT EXISTS `version` (
  `version` varchar(10),
  PRIMARY KEY (`version`),
  UNIQUE KEY `version_UNIQUE` (`version`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
INSERT INTO version (version) VALUES('1.1.3');
