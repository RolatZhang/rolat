/*<span style="color:rgb(51,204,0);">//请勿手工写入数据 供remember-me功能使用</span>
CREATE TABLE `persistent_logins` (  
  `username` varchar(64) NOT NULL,  
  `series` varchar(64) NOT NULL,  
  `token` varchar(64) NOT NULL,  
  `last_used` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  
  PRIMARY KEY (`series`)  
)  */