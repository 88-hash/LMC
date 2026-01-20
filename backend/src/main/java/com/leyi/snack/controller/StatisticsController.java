package com.leyi.snack.controller;

import com.leyi.snack.common.Result;
import com.leyi.snack.entity.DashboardStatsVO;
import com.leyi.snack.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/stats")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/dashboard")
    public Result<DashboardStatsVO> dashboard() {
        return Result.success(statisticsService.getDashboardStats());
    }
}
