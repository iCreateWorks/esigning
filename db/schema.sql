DROP TABLE IF EXISTS `doc_esign_device`;
DROP TABLE IF EXISTS `doc_esign_master`;

ALTER DATABASE database_name 
CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

CREATE TABLE `doc_esign_master` (
  `id` int NOT NULL AUTO_INCREMENT,
  `doc_uuid` varchar(36) COLLATE utf8mb4_general_ci NOT NULL,
  `context_id` varchar(36) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `doc_type` varchar(30) COLLATE utf8mb4_general_ci NOT NULL,
  `doc_path` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `pdf_doc_path` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `doc_template_code` varchar(30) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `encrypted` tinyint(1) DEFAULT '0',
  `upload_date` timestamp NULL DEFAULT NULL,
  `doc_content` mediumtext COLLATE utf8mb4_general_ci,
  `doc_sign_date` datetime DEFAULT NULL,
  `time_zone_id` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `locale_pref` varchar(10) COLLATE utf8mb4_general_ci DEFAULT 'en-US',
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_doc_esign_master_1` (`doc_uuid`,`doc_type`)
) ENGINE=InnoDB AUTO_INCREMENT=2429 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

ALTER TABLE doc_esign_master 
CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

CREATE TABLE `doc_esign_device` (
  `id` int NOT NULL AUTO_INCREMENT,
  `device_id` varchar(45) COLLATE utf8mb4_general_ci NOT NULL,
  `doc_id` int NOT NULL,
  `platform` varchar(30) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `manufacturer` varchar(30) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `model` varchar(30) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `os_version` varchar(30) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `ip_address` varchar(30) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `user_agent` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_doc_esign_master_to_doc_esign_device1` (`doc_id`),
  CONSTRAINT `fk_doc_esign_master_to_doc_esign_device1` FOREIGN KEY (`doc_id`) REFERENCES `doc_esign_master` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=2429 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

ALTER TABLE doc_esign_device 
CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;




