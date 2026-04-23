package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 获取营业额统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        // 1. 生成日期列表,包含begin和end
        List<LocalDate> dateList = new ArrayList();
        dateList.add(begin);
        while (!begin.equals(end)) {
            // 2. 计算日期列表
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        // 3. 计算营业额列表
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            // 4. 计算营业额
            Map map = new HashMap();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            // 5. 处理null值
            if (turnover == null) {
                turnover = 0.0;
            }
            turnoverList.add(turnover);
        }

        //返回营业额统计VO
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 获取用户统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        // 1. 生成日期列表,包含begin和end
        List<LocalDate> dateList = new ArrayList();
        dateList.add(begin);
        while (!begin.equals(end)) {
            // 2. 计算日期列表
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        List<Integer> userList = new ArrayList<>();//用户总量
        List<Integer> newUserList = new ArrayList<>();//新增用户
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            // 3. 计算用户数量
            Map map = new HashMap();
            map.put("endTime", endTime);
            Integer userCount = userMapper.countByMap(map);
            // 3. 计算新增用户数量
            map.put("beginTime", beginTime);
            Integer newUserCount = userMapper.countByMap(map);
            // 4. 处理null值
            if (userCount == null) {
                userCount = 0;
            }
            if (newUserCount == null) {
                newUserCount = 0;
            }

            userList.add(userCount);
            newUserList.add(newUserCount);
        }
        //返回用户统计VO
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(userList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();


    }

    // 获取订单统计
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        // 1. 生成日期列表,包含begin和end
        List<LocalDate> dateList = new ArrayList();
        dateList.add(begin);
        while (!begin.equals(end)) {
            // 2. 计算日期列表
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        List<Integer> orderList = new ArrayList<>();//每日订单数
        List<Integer> validOrderCountList = new ArrayList<>();//每日有效订单数
        // 3. 计算订单数量
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            // 4. 计算订单数量
            Map map = new HashMap();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            Integer orderCount = orderMapper.countByMap(map);
            map.put("status", Orders.COMPLETED);
            Integer validOrderCount = orderMapper.countByMap(map);

            // 5. 处理null值
            if (orderCount == null) {
                orderCount = 0;
            }
            if (validOrderCount == null) {
                validOrderCount = 0;
            }
            orderList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }
        //订单总数
        Integer totalOrderCount = orderList.stream().reduce(Integer::sum).get();
        //有效订单数
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        // 6. 计算订单完成率
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }
        //返回订单统计VO
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    // 获取销量统计
    @Override
    public SalesTop10ReportVO getSalesStatistics(LocalDate begin, LocalDate end) {

        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getSalesStatistics(beginTime, endTime);
        List<String> nameList = goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String top10Names = StringUtils.join(nameList, ",");
        List<Integer> numberList = goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String top10Numbers = StringUtils.join(numberList, ",");
        //返回销量统计VO
        return SalesTop10ReportVO.builder()
                .nameList(top10Names)
                .numberList(top10Numbers)
                .build();
    }

    // 导出报表
    @Override
    public void exportReport(HttpServletResponse response) {
       //查询数据库，获取营业数据
        LocalDate dateBegin=LocalDate.now().minusDays(30);
        LocalDate dateEnd=LocalDate.now().minusDays(1);

        //查询浏览数据
        BusinessDataVO businessDataVO= workspaceService.getBusinessData(LocalDateTime.of(dateBegin, LocalTime.MIN), LocalDateTime.of(dateEnd, LocalTime.MAX));

        //通过POI将数据写入Excel文件
        InputStream in= this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try{
            //创建工作簿
            XSSFWorkbook workbook = new XSSFWorkbook(in);
            //填充时间
            XSSFSheet sheet = workbook.getSheet("Sheet1");
            sheet.getRow(1).getCell(1).setCellValue("时间："+dateBegin+"至"+dateEnd);
            //获取第4行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());
            //第五行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());
            //填充明细
            for (int i=0;i<30;i++){
                LocalDate date=dateBegin.plusDays(i);
                //查某一天的营业数据
                BusinessDataVO businessDataVO1 = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                //获取某一行
                row = sheet.getRow(7+i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessDataVO1.getTurnover());
                row.getCell(3).setCellValue(businessDataVO1.getValidOrderCount());
                row.getCell(4).setCellValue(businessDataVO1.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessDataVO1.getUnitPrice());
                row.getCell(6).setCellValue(businessDataVO1.getNewUsers());
            }

            //通过输出流将Excel文件下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            workbook.write(out);
            //关闭工作簿
            workbook.close();
            //关闭输出流
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
