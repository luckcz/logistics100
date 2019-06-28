package cn.logistics.service.impl;
import cn.logistics.dao.LogisticsMapper;
import cn.logistics.entity.LogisticsEntity;
import cn.logistics.enums.LogisticEnum;
import cn.logistics.service.LogisticsService;
import cn.logistics.utils.HttpRequestUtil;
import cn.logistics.utils.KuaiDi100Util;
import cn.logistics.vo.LastResult;
import cn.logistics.vo.ParamBody;
import cn.logistics.vo.PollResult;
import cn.logistics.vo.TaskRequest;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Transactional
public class LogisticsServiceImpl implements LogisticsService {
    @Autowired
    private LogisticsMapper logisticsMapper ;
    @Override
    public String subscribleLogistics(TaskRequest req, long orderId) {
        Gson gson = new Gson() ;
        //回调url
        String url = LogisticEnum.LOGISTIC_CALLBACK_URL.getValue();
        req.getParameters().put("callbackurl", url+orderId);
        req.setKey(LogisticEnum.LOGISTIC_KEY.getValue());
        Map<String, String> p = new HashMap<String, String>();
        p.put("schema", "json");
        p.put("param", gson.toJson(req));
        log.info("物流信息订阅开始,订单号:【{}】,入参为：【{}】", orderId, p);
        try {
            String result = HttpRequestUtil.post(LogisticEnum.LOGISTIC_POLL_URL.getValue(),p);
            PollResult pollResult = gson.fromJson(result,PollResult.class);
            if (pollResult.getResult()=="false") {
                //代表请求出问题了
                    log.error("订阅物流信息失败，订单号为:【{}】,失败原因为：【{}】", orderId, pollResult.getMessage());
                    return gson.toJson(pollResult);
            } else {
                //同步订单的快递单号，快递公司code，修改订单状态为已发货
                List<LogisticsEntity> result_list = logisticsMapper.findByOrderId(orderId);
                if (null != result_list && result_list.size() > 0) {
                    //那么进行修改订单状态
                    logisticsMapper.updateStatusByOrderId("已发货",orderId);
                }else{
                    //保存订单信息
                    LogisticsEntity entity = new LogisticsEntity();
                    entity.setExpressCompanCode(req.getCompany());
                    entity.setLogisticsNo(req.getNumber());
                    entity.setOrderId(orderId);
                    logisticsMapper.insertSelective(entity);
                }
                log.info("订阅物流信息成功，订单号为:【{}】,物流单号为:【{}】", orderId, req.getNumber());
                return "订阅成功.........";
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return gson.toJson(p);
    }

    @Override
    public String getLogistics(long orderId) {
        Gson gson = new Gson();
        List<LogisticsEntity> data = logisticsMapper.findByOrderId(orderId);
        return gson.toJson(data);
    }

    @Override
    public String addData() {
        LogisticsEntity entity = new LogisticsEntity();
        entity.setOrderId(111111L);
        entity.setExpressCompan("ems");
        entity.setExpressCompanCode("邮政");
        entity.setLogisticsNo("2134657834");
        entity.setStatus(1);
        entity.setIsCheck(1);
        entity.setData("快递已经送到亲的手上了");
        logisticsMapper.insertSelective(entity);
        log.info("物流更新，新的数据："+entity.toString());
        return "success";
    }

    @Override
    public String subscribleLogisticsPost(String company,String number) {
        return KuaiDi100Util.subscribe(company,number);
    }

    @Override
    public String rollbackKuaiDi100(HttpServletRequest request, HttpServletResponse response){
        Gson gson = new Gson();
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("result",false);
        map.put("returnCode","500");
        map.put("message","保存失败");
        String param = request.getParameter("param");
        log.info("物流更新回调开始，入参为：param【{}】",param);
        /**
         * 取出需要的返回值，然后进行业务操作
         */
        ParamBody paramBody = gson.fromJson(param,ParamBody.class);
        log.info("使用Gson对象解析之后的参数是：paramBody【{}】",paramBody);
        LastResult lastResult = paramBody.getLastResult();
        List<LogisticsEntity> list = logisticsMapper.findByNumber(lastResult.getNu());
        LogisticsEntity entity = new LogisticsEntity();
        entity.setLogisticsNo(lastResult.getNu());
        entity.setIsCheck(lastResult.getIscheck());
        entity.setStatus(lastResult.getState());
        entity.setExpressCompanCode(lastResult.getCom());
        entity.setData(gson.toJson(lastResult.getData()));
        if(null != list && list.size() > 0){
            //代表以前有物流信息，那么直接进行更新操作
            logisticsMapper.updateByNumber(entity);
            log.info("回调成功修改了数据 LogisticsEntity【{}】",entity);
        }else{
            //代表以前没有，那么进行新增操作
            logisticsMapper.insertSelective(entity);
            log.info("回调成功保存了数据 LogisticsEntity【{}】",entity);
        }
        //首先判断此订单是否签收完成
        //state 快递单当前签收状态，包括0在途中、1已揽收、2疑难、3已签收、4退签、5同城派送中、6退回、7转单等7个状态
        if(1 == lastResult.getIscheck() && 3 == lastResult.getState()){
            //此时代表签收完成了，将此数据放入到redis缓存中
        }
        // 处理快递结果
        map.put("result",true);
        map.put("returnCode","200");
        map.put("message","保存成功");
        //这里必须返回，否则认为失败，过30分钟又会重复推送。
        //response.getWriter().print(JSONObject.parseObject(param));
        return gson.toJson(param);
    }
}
