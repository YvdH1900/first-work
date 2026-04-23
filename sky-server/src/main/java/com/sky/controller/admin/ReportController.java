package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@RestController
@Slf4j
@RequestMapping("/admin/report")
public class ReportController {

    @Autowired
    private ReportService reportService;
    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnoverStatistics(
        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("营业额统计:{}到{}", begin, end);
        return Result.success(reportService.getTurnoverStatistics(begin, end));


    }
    //用户统计
    @GetMapping("/userStatistics")
    public Result<UserReportVO> userStatistics(
        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("用户统计:{}到{}", begin, end);
        return Result.success(reportService.getUserStatistics(begin, end));
    }
    //订单统计
    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> orderStatistics(
        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("订单统计:{}到{}", begin, end);
        return Result.success(reportService.getOrderStatistics(begin, end));
    }
    //销量统计
    @GetMapping("/top10")
    public Result<SalesTop10ReportVO> top10SalesStatistics(
        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("销量排行TOP10:{}到{}", begin, end);
        return Result.success(reportService.getSalesStatistics(begin, end));
    }
    //导出报表
    @GetMapping("/export")
    public void exportReport(HttpServletResponse response){
        log.info("导出报表");
        reportService.exportReport(response);
    }

}
