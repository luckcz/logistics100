package cn.logistics.service;

import cn.logistics.vo.TaskRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface LogisticsService {
    public String subscribleLogistics(TaskRequest req, long orderId);
    public String getLogistics(long orderId);
    public String addData();
    public String rollbackKuaiDi100(HttpServletRequest request, HttpServletResponse response);
    public String subscribleLogisticsPost(String company,String number);
}
