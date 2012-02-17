CREATE TABLE IF NOT EXISTS `authdb_users` (
  `id` int(4) NOT NULL auto_increment,
  `regdate` timestamp DEFAULT NOW(),
  `username` varchar(40) NOT NULL,
  `password` varchar(128) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;
