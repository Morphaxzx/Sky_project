package com.sky.service;


import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


public interface ReportService {
    TurnoverReportVO turnOverQuery(LocalDate begin, LocalDate end);

    UserReportVO userQuery(LocalDate begin, LocalDate end);

    OrderReportVO orderQuery(LocalDate begin, LocalDate end);

    SalesTop10ReportVO GetTop10(LocalDate begin, LocalDate end);
}
