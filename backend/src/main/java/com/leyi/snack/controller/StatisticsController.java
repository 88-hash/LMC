package com.leyi.snack.controller;

import com.leyi.snack.common.Result;
import com.leyi.snack.service.StatisticsService;
import com.leyi.snack.vo.DashboardFullVO;
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
    public Result<DashboardFullVO> getDashboard() {
        return Result.success(statisticsService.getDashboardStats());
    }
}