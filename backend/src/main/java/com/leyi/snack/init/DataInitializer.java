package com.leyi.snack.init;

import com.leyi.snack.entity.Category;
import com.leyi.snack.entity.Goods;
import com.leyi.snack.mapper.CategoryMapper;
import com.leyi.snack.mapper.GoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.net.URLEncoder;
import java.util.Random;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(String... args) {
        recreateOrderTables();
        ensureCategorySchema();
        seedBaseUser();
        seedCategoryAndGoods();
    }

    private void recreateOrderTables() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS `order_item`");
        jdbcTemplate.execute("DROP TABLE IF EXISTS `orders`");
        jdbcTemplate.execute("DROP TABLE IF EXISTS `order`");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `order` (" +
                "  `id` bigint(20) NOT NULL AUTO_INCREMENT," +
                "  `order_no` varchar(64) NOT NULL," +
                "  `user_id` bigint(20) NOT NULL," +
                "  `total_amount` decimal(10,2) NOT NULL," +
                "  `status` int(11) DEFAULT '0'," +
                "  `verify_code` varchar(32) DEFAULT NULL," +
                "  `remark` varchar(255) DEFAULT NULL," +
                "  `created_at` datetime DEFAULT CURRENT_TIMESTAMP," +
                "  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "  PRIMARY KEY (`id`)," +
                "  UNIQUE KEY `uk_order_no` (`order_no`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `order_item` (" +
                "  `id` bigint(20) NOT NULL AUTO_INCREMENT," +
                "  `order_id` bigint(20) NOT NULL," +
                "  `goods_id` bigint(20) NOT NULL," +
                "  `goods_name` varchar(128) NOT NULL," +
                "  `goods_image` varchar(255) DEFAULT NULL," +
                "  `price` decimal(10,2) NOT NULL," +
                "  `quantity` int(11) NOT NULL," +
                "  PRIMARY KEY (`id`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

        jdbcTemplate.execute("DROP TABLE IF EXISTS `verify_log`");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `verify_log` (" +
                "  `id` bigint(20) NOT NULL AUTO_INCREMENT," +
                "  `order_id` bigint(20) NOT NULL," +
                "  `admin_id` bigint(20) NOT NULL," +
                "  `verify_time` datetime DEFAULT CURRENT_TIMESTAMP," +
                "  `action` varchar(32) DEFAULT NULL," +
                "  `remark` varchar(255) DEFAULT NULL," +
                "  PRIMARY KEY (`id`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");
    }

    private void ensureCategorySchema() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `category1` (" +
                "  `id` bigint NOT NULL AUTO_INCREMENT," +
                "  `name` varchar(50) NOT NULL," +
                "  `sort` int DEFAULT 0," +
                "  `created_at` datetime DEFAULT CURRENT_TIMESTAMP," +
                "  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "  PRIMARY KEY (`id`)," +
                "  UNIQUE KEY `uk_category1_name` (`name`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `category2` (" +
                "  `id` bigint NOT NULL AUTO_INCREMENT," +
                "  `parent_id` bigint NOT NULL," +
                "  `name` varchar(50) NOT NULL," +
                "  `sort` int DEFAULT 0," +
                "  `created_at` datetime DEFAULT CURRENT_TIMESTAMP," +
                "  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "  PRIMARY KEY (`id`)," +
                "  KEY `idx_parent_id` (`parent_id`)," +
                "  UNIQUE KEY `uk_category2_parent_name` (`parent_id`, `name`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

        ensureGoodsTable();

        Integer hasCategory2Id = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns " +
                        "WHERE table_schema = DATABASE() AND table_name = 'goods' AND column_name = 'category2_id'",
                Integer.class
        );
        if (hasCategory2Id != null && hasCategory2Id == 0) {
            jdbcTemplate.execute("ALTER TABLE goods ADD COLUMN category2_id bigint DEFAULT NULL COMMENT '二级分类ID'");
        }

        Integer hasCategoryId = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns " +
                        "WHERE table_schema = DATABASE() AND table_name = 'goods' AND column_name = 'category_id'",
                Integer.class
        );
        if (hasCategoryId != null && hasCategoryId == 0) {
            jdbcTemplate.execute("ALTER TABLE goods ADD COLUMN category_id bigint DEFAULT NULL COMMENT '兼容旧分类字段'");
        }

        jdbcTemplate.execute("UPDATE goods SET category2_id = category_id WHERE category2_id IS NULL AND category_id IS NOT NULL");
        jdbcTemplate.execute("UPDATE goods SET category_id = category2_id WHERE category_id IS NULL AND category2_id IS NOT NULL");
    }

    private void ensureGoodsTable() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `goods` (" +
                "  `id` bigint NOT NULL AUTO_INCREMENT," +
                "  `category_id` bigint DEFAULT NULL," +
                "  `category2_id` bigint DEFAULT NULL," +
                "  `name` varchar(100) NOT NULL," +
                "  `price` decimal(10,2) NOT NULL," +
                "  `stock` int DEFAULT 0," +
                "  `bar_code` varchar(50) DEFAULT NULL," +
                "  `image_url` varchar(255) DEFAULT NULL," +
                "  `description` text," +
                "  `is_on_sale` tinyint DEFAULT 1," +
                "  `expire_date` date DEFAULT NULL," +
                "  `is_deleted` tinyint DEFAULT 0," +
                "  `created_at` datetime DEFAULT CURRENT_TIMESTAMP," +
                "  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "  PRIMARY KEY (`id`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");
    }

    private void seedBaseUser() {
        try {
            jdbcTemplate.execute("INSERT IGNORE INTO user (id, name, phone, password, created_at) " +
                    "VALUES (1, '测试店长', '13800000000', '123456', NOW())");
        } catch (Exception ignored) {
        }
    }

    private void seedCategoryAndGoods() {
        jdbcTemplate.execute("DELETE FROM goods");
        jdbcTemplate.execute("DELETE FROM category2");
        jdbcTemplate.execute("DELETE FROM category1");

        String[] level1Names = {
                "饮品", "膨化零食", "方便速食", "糖果巧克力", "肉类零食", "坚果果干"
        };

        String[][] level2Names = {
                {"苏打水", "汽水", "果汁", "茶饮", "功能饮"},
                {"薯片", "玉米脆", "米饼", "海苔脆", "辣条"},
                {"泡面", "粉丝", "自热饭", "速食汤", "即食面包"},
                {"软糖", "硬糖", "巧克力", "威化", "果冻"},
                {"牛肉干", "鸡胸肉", "鸭脖", "猪肉脯", "卤蛋"},
                {"综合坚果", "巴旦木", "开心果", "果干", "谷物棒"}
        };

        String[] variants = {"经典款", "青柠味", "海盐味", "蜜桃味", "香辣味", "无糖款", "轻享装", "分享装"};
        Random random = new Random(20260220L);
        long barCodeSeed = 6900000000000L;

        for (int i = 0; i < level1Names.length; i++) {
            Category level1 = new Category();
            level1.setName(level1Names[i]);
            level1.setSort(i + 1);
            categoryMapper.saveLevel1(level1);

            for (int j = 0; j < level2Names[i].length; j++) {
                Category level2 = new Category();
                level2.setParentId(level1.getId());
                level2.setName(level2Names[i][j]);
                level2.setSort(j + 1);
                categoryMapper.saveLevel2(level2);

                for (int k = 0; k < variants.length; k++) {
                    Goods goods = new Goods();
                    goods.setCategory2Id(level2.getId());
                    goods.setCategoryId(level2.getId());
                    goods.setName(level2Names[i][j] + " " + variants[k]);
                    goods.setPrice(buildPrice(i, j, k, random));
                    goods.setStock(28 + random.nextInt(180));
                    goods.setBarCode(String.valueOf(barCodeSeed++));
                    goods.setImageUrl(buildImage(level2Names[i][j], variants[k]));
                    goods.setDescription(buildDescription(level1Names[i], level2Names[i][j], variants[k]));
                    goods.setIsOnSale(1);
                    goods.setExpireDate(LocalDate.now().plusDays(90L + random.nextInt(180)));
                    goodsMapper.save(goods);
                }
            }
        }
    }

    private BigDecimal buildPrice(int i, int j, int k, Random random) {
        double base = 3.2 + (i * 0.85) + (j * 0.45) + (k * 0.35);
        double wave = random.nextDouble() * 1.8;
        return BigDecimal.valueOf(base + wave).setScale(2, RoundingMode.HALF_UP);
    }

    private String buildImage(String level2, String variant) {
        String text = URLEncoder.encode(level2 + " " + variant, StandardCharsets.UTF_8);
        return "https://placehold.co/400x400/FFF8D6/111111?text=" + text;
    }

    private String buildDescription(String level1, String level2, String variant) {
        return "【" + level1 + "-" + level2 + "】" + variant + "，口感扎实，适合追剧、通勤和办公室补给。";
    }
}

