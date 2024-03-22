package com.sky.controller.admin;


import com.sky.dto.GoodsSalesDTO;
import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@RequestMapping("/admin/report")
@Api(tags = "数据统计相关接口")
@Slf4j
@RestController
public class ReportController {

    @Autowired
    private ReportService reportService;


    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<TurnoverReportVO> turnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end
            )
    {
        log.info("营业额统计");
        TurnoverReportVO res = reportService.turnOverQuery(begin,end);
        return Result.success(res);
    }


    @GetMapping("/userStatistics")
    @ApiOperation("用户统计")
    public Result<UserReportVO> userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end
    )
    {
        log.info("用户统计");
        UserReportVO res = reportService.userQuery(begin,end);
        return Result.success(res);
    }

    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计")
    public Result<OrderReportVO> orderStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end
    )
    {
        log.info("订单统计");
        OrderReportVO res = reportService.orderQuery(begin,end);
        return Result.success(res);
    }

    @GetMapping("/top10")
    @ApiOperation("top10")
    public Result<SalesTop10ReportVO> top10Statistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end
    )
    {
        log.info("top10");
        SalesTop10ReportVO res = reportService.GetTop10(begin,end);
        return Result.success(res);
    }

    @GetMapping("/export")
    @ApiOperation("导出数据")
    public void export(HttpServletResponse httpServletResponse)
    {
        log.info("导出数据");
        reportService.export(httpServletResponse);

    }


}
