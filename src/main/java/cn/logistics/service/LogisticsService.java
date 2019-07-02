package cn.logistics.service;

import cn.logistics.vo.TaskRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface LogisticsService {
    String addData();
    String rollbackKuaiDi100(HttpServletRequest request, HttpServletResponse response);
    String subscribleLogisticsPost(String company,String number);
    String queryActuralLogisticsByNumAndCom(String company,String number);
    String testRedis();
    String queryLogisticsInfoByNumAndCom(String company, String number);
}
