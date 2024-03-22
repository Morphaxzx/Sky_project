package com.sky.service.impl;

import com.github.pagehelper.util.StringUtil;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserLoginMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFAutoFilter;
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
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private UserLoginMapper userLoginMapper;

    @Autowired
    private WorkspaceService workspaceService;

    @Override
    public TurnoverReportVO turnOverQuery(LocalDate begin, LocalDate end) {

        List<LocalDate> myDateList = new ArrayList<>();
        while (!begin.equals(end))
        {
            myDateList.add(begin);
            begin  = begin.plusDays(1);
        }

        List<Double> myTurnoverList = new ArrayList<>();
        for (LocalDate date : myDateList) {
            LocalDateTime l = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime r = LocalDateTime.of(date, LocalTime.MAX);
            HashMap map = new HashMap<>();
            map.put("beginTime",l);
            map.put("endTime",r);
            map.put("status", Orders.COMPLETED);
            Double amount = reportMapper.queryAmoutByTime(map);
            amount= amount==null?0.0:amount;
            myTurnoverList.add(amount);


        }


        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(myDateList,","))
                .turnoverList(StringUtils.join(myTurnoverList,","))
                .build();
    }


    @Override
    public UserReportVO userQuery(LocalDate begin, LocalDate end) {

        List<LocalDate> myDateList = new ArrayList<>();
        while (!begin.equals(end))
        {
            myDateList.add(begin);
            begin  = begin.plusDays(1);
        }

        List<Integer> myTotalUserList = new ArrayList<>();
        List<Integer> myNewUserList = new ArrayList<>();
        for (LocalDate date : myDateList) {
            LocalDateTime l = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime r = LocalDateTime.of(date, LocalTime.MAX);
            HashMap map = new HashMap<>();
            map.put("endTime",r);
            map.put("status", null);
            Integer total = userLoginMapper.queryUserByTime(map);
            map.put("beginTime",l);
            Integer newUser = userLoginMapper.queryUserByTime(map);
            newUser= newUser==null?0:newUser;
            myTotalUserList.add(total);
            myNewUserList.add(newUser);


        }



        return UserReportVO.builder()
                .dateList(StringUtils.join(myDateList,","))
                .totalUserList(StringUtils.join(myTotalUserList,","))
                .newUserList(StringUtils.join(myNewUserList,","))
                .build();

    }


    @Override
    public OrderReportVO orderQuery(LocalDate begin, LocalDate end) {
        List<LocalDate> myDateList = new ArrayList<>();
        while (!begin.equals(end))
        {
            myDateList.add(begin);
            begin  = begin.plusDays(1);
        }

        List<Integer> myOrderCountList = new ArrayList<>();
        List<Integer> myvalidOrderCountList = new ArrayList<>();
        for (LocalDate date : myDateList) {
            LocalDateTime l = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime r = LocalDateTime.of(date, LocalTime.MAX);
            HashMap map = new HashMap<>();
            map.put("endTime",r);
            map.put("status", null);
            map.put("beginTime",l);
            Integer total = reportMapper.queryOrderByTime(map);
            map.put("status", Orders.COMPLETED);
            Integer vaild = reportMapper.queryOrderByTime(map);
            vaild= vaild==null?0:vaild;
            myOrderCountList.add(total);
            myvalidOrderCountList.add(vaild);
        }

        Integer mytotalOrderCount = myOrderCountList.stream().reduce(Integer::sum).get();
        Integer myvalidOrderCount = myvalidOrderCountList.stream().reduce(Integer::sum).get();

        double myrate=0.0;
        if(mytotalOrderCount!=0)
            myrate=myvalidOrderCount.doubleValue()/mytotalOrderCount;

        return OrderReportVO.builder()
                .dateList(StringUtils.join(myDateList,","))
                .orderCompletionRate(myrate)
                .totalOrderCount(mytotalOrderCount)
                .validOrderCount(myvalidOrderCount)
                .validOrderCountList(StringUtils.join(myvalidOrderCountList,","))
                .orderCountList(StringUtils.join(myOrderCountList,","))
                .build();
    }

    @Override
    public SalesTop10ReportVO GetTop10(LocalDate begin, LocalDate end) {
        LocalDateTime l = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime r = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> salestop10 = reportMapper.getTop10(l,r);
        List<String> namelist = salestop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numberlist = salestop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(namelist,","))
                .numberList(StringUtils.join(numberlist,","))
                .build();
    }


    @Override
    public void export(HttpServletResponse httpServletResponse) {
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(LocalDate.now().plusDays(-30), LocalTime.MIN),
                LocalDateTime.of(LocalDate.now().plusDays(-1), LocalTime.MAX));

        InputStream in = this.getClass().getClassLoader().getResourceAsStream("Template/运营数据报表模板.xlsx");
        try {
            XSSFWorkbook excel = new XSSFWorkbook(in);
            XSSFSheet sheet1 = excel.getSheet("Sheet1");
            sheet1.getRow(1).getCell(1).setCellValue("时间：" + LocalDate.now().plusDays(-30)+" 到："+LocalDate.now().plusDays(-1));


            sheet1.getRow(3).getCell(2).setCellValue(businessData.getTurnover());
            sheet1.getRow(3).getCell(4).setCellValue(businessData.getOrderCompletionRate());
            sheet1.getRow(3).getCell(6).setCellValue(businessData.getNewUsers());
            sheet1.getRow(4).getCell(2).setCellValue(businessData.getValidOrderCount());
            sheet1.getRow(4).getCell(4).setCellValue(businessData.getUnitPrice());


            for(int i =0;i<30;i++)
            {
                LocalDate date = LocalDate.now().plusDays(-30).plusDays(i);
                BusinessDataVO businessData1 = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                XSSFRow row = sheet1.getRow(7 + i);

                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData1.getTurnover());
                row.getCell(3).setCellValue(businessData1.getValidOrderCount());
                row.getCell(4).setCellValue(businessData1.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData1.getUnitPrice());
                row.getCell(6).setCellValue(businessData1.getNewUsers());

            }

            ServletOutputStream  out = httpServletResponse.getOutputStream();
            excel.write(out);


            out.close();
            excel.close();



        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
