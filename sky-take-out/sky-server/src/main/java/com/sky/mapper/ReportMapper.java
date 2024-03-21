package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Mapper
public interface ReportMapper {


    Double queryAmoutByTime(HashMap map);


    Integer queryOrderByTime(HashMap map);

    List<GoodsSalesDTO> getTop10(LocalDateTime begin, LocalDateTime end);
}
