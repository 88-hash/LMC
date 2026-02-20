-- True second-level category schema migration
-- Database: leyi_snack_db

CREATE TABLE IF NOT EXISTS `category1` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `sort` int DEFAULT 0,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category1_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `category2` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint NOT NULL,
  `name` varchar(50) NOT NULL,
  `sort` int DEFAULT 0,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_category2_parent` (`parent_id`),
  UNIQUE KEY `uk_category2_parent_name` (`parent_id`, `name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `goods` ADD COLUMN IF NOT EXISTS `category2_id` bigint DEFAULT NULL COMMENT '二级分类ID';
ALTER TABLE `goods` ADD COLUMN IF NOT EXISTS `category_id` bigint DEFAULT NULL COMMENT '兼容旧字段';

UPDATE `goods`
SET `category2_id` = `category_id`
WHERE `category2_id` IS NULL AND `category_id` IS NOT NULL;

UPDATE `goods`
SET `category_id` = `category2_id`
WHERE `category_id` IS NULL AND `category2_id` IS NOT NULL;
