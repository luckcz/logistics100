package cn.logistics.dao;

import cn.logistics.entity.LogisticsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LogisticsMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(LogisticsEntity record);

    int updateByPrimaryKeySelective(LogisticsEntity record);

    List<LogisticsEntity> findByOrderId(Long orderId);

    int updateStatusByOrderId(@Param("status") String status,@Param("orderId") Long orderId);

    int deleteByOrderId(Long orderId);

    List<LogisticsEntity> findByNumber(String number);

    LogisticsEntity findByNumberAndExpressCompanCode(@Param("number") String number,@Param("company") String company);

    int updateByNumber(LogisticsEntity record);

}