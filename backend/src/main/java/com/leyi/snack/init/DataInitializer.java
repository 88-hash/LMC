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
    public void run(String... args) throws Exception {
        // 0. 暴力重置表结构 (修复字段缺失问题)
        jdbcTemplate.execute("DROP TABLE IF EXISTS `order_item`");
        jdbcTemplate.execute("DROP TABLE IF EXISTS `orders`");

        // 1. 确保表结构存在 (兼容 `init.sql`)
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `orders` (" +
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

        jdbcTemplate.execute("DROP TABLE IF EXISTS `verify_log`"); // 强制重建
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `verify_log` (" +
                "  `id` bigint(20) NOT NULL AUTO_INCREMENT," +
                "  `order_id` bigint(20) NOT NULL," +
                "  `admin_id` bigint(20) NOT NULL," +
                "  `verify_time` datetime DEFAULT CURRENT_TIMESTAMP," +
                "  `action` varchar(32) DEFAULT NULL," +
                "  `remark` varchar(255) DEFAULT NULL," +
                "  PRIMARY KEY (`id`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

        // 2. 插入测试用户 (为了 userId=1 能通过)
        try {
            jdbcTemplate.execute("INSERT IGNORE INTO user (id, name, phone, password, created_at) " +
                    "VALUES (1, '测试店长', '13800000000', '123456', NOW())");
        } catch (Exception e) {
            // 忽略，可能表结构不同
        }

        // 3. 清空商品数据 (重新灌入)
        jdbcTemplate.execute("DELETE FROM goods");
        jdbcTemplate.execute("DELETE FROM category");

        String[] cateNames = {"快乐水", "膨化食品", "方便速食", "精品糖果", "肉肉零食", "网红新品"};
        Long[] cateIds = new Long[cateNames.length];

        for (int i = 0; i < cateNames.length; i++) {
            Category category = new Category();
            category.setName(cateNames[i]);
            category.setParentId(0L);
            category.setSort(i);
            categoryMapper.save(category);
            cateIds[i] = category.getId();
        }

        String[][] goodsData = {
            {"可口可乐 330ml", "3.00", "0", "https://placehold.co/400x400?text=Coke"},
            {"百事可乐 无糖", "3.00", "0", "https://placehold.co/400x400?text=Pepsi"},
            {"元气森林 白桃味", "5.50", "0", "https://placehold.co/400x400?text=Genki"},
            {"农夫山泉 矿泉水", "2.00", "0", "https://placehold.co/400x400?text=Water"},

            {"乐事薯片 原味", "7.50", "1", "https://placehold.co/400x400?text=Lays"},
            {"乐事薯片 黄瓜味", "7.50", "1", "https://placehold.co/400x400?text=Lays"},
            {"浪味仙 蔬菜味", "6.00", "1", "https://placehold.co/400x400?text=Snack"},
            {"上好佳 鲜虾片", "5.00", "1", "https://placehold.co/400x400?text=Snack"},

            {"康师傅 红烧牛肉面", "4.50", "2", "https://placehold.co/400x400?text=Noodle"},
            {"统一 老坛酸菜面", "4.50", "2", "https://placehold.co/400x400?text=Noodle"},
            {"自嗨锅 麻辣烫", "15.80", "2", "https://placehold.co/400x400?text=Hotpot"},
            {"螺霸王 螺蛳粉", "12.90", "2", "https://placehold.co/400x400?text=Luosifen"},

            {"阿尔卑斯 棒棒糖", "1.00", "3", "https://placehold.co/400x400?text=Candy"},
            {"大白兔 奶糖", "18.00", "3", "https://placehold.co/400x400?text=Candy"},
            {"德芙 巧克力", "12.50", "3", "https://placehold.co/400x400?text=Chocolate"},

            {"卫龙 大面筋", "3.50", "4", "https://placehold.co/400x400?text=Spicy"},
            {"无穷 鸡翅根", "8.90", "4", "https://placehold.co/400x400?text=Chicken"},
            {"百草味 鸭脖", "6.50", "4", "https://placehold.co/400x400?text=Duck"},

            {"魔芋爽 香辣味", "1.50", "5", "https://placehold.co/400x400?text=Konjac"},
            {"半熟芝士 蛋糕", "8.80", "5", "https://placehold.co/400x400?text=Cake"}
        };

        Random random = new Random();
        for (String[] g : goodsData) {
            Goods goods = new Goods();
            goods.setName(g[0]);
            goods.setPrice(new BigDecimal(g[1]));
            int cateIndex = Integer.parseInt(g[2]);
            goods.setCategoryId(cateIds[cateIndex]);
            goods.setImageUrl(g[3]);
            goods.setStock(random.nextInt(90) + 10);
            goods.setIsOnSale(1);
            goods.setBarCode("69" + (System.currentTimeMillis() + random.nextInt(1000)));
            goodsMapper.save(goods);
        }
    }
}
