package cn.logistics.service.impl;
import cn.logistics.bean.UserBean;
import cn.logistics.dao.LogisticsMapper;
import cn.logistics.entity.LogisticsEntity;
import cn.logistics.enums.LogisticEnum;
import cn.logistics.service.LogisticsService;
import cn.logistics.utils.HttpRequestUtil;
import cn.logistics.utils.KuaiDi100Util;
import cn.logistics.utils.RedisUtil;
import cn.logistics.vo.*;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Transactional
public class LogisticsServiceImpl implements LogisticsService {
    @Autowired
    private LogisticsMapper logisticsMapper ;
    @Autowired
    private RedisUtil redisUtil ;

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
    public String queryActuralLogisticsByNumAndCom(String company, String number) {
        return KuaiDi100Util.query(number,company);
    }

    @Override
    public String testRedis() {
        /*redisUtil.set("testData","测试数据");
        String testData = (String)redisUtil.get("testData");
        UserBean bean = new UserBean();
        bean.setId(2);
        bean.setUserName("王五");
        bean.setAge(33);
        redisUtil.set("user2",bean,30);*/
        UserBean user = (UserBean)redisUtil.get("user2");
        if(null != user){
            log.info("查询的用户信息为【{}】",user.getUserName());
        }else{
            log.info("未查询到数据信息");
            return "未查询到数据信息";
        }
        return JSONObject.toJSONString(user);
    }

    @Override
    public String queryLogisticsInfoByNumAndCom(String company, String number) {
        Gson gson = new Gson();
        StringBuffer buff = new StringBuffer(number);
        buff.append(company);
        LogisticsEntity logisticsEntity = (LogisticsEntity)redisUtil.get(buff.toString());
        if(null != logisticsEntity){
            log.info("查询的物流信息为【{}】",logisticsEntity.toString());
            logisticsEntity.setDataObject(gson.fromJson(logisticsEntity.getData(), List.class));
            return gson.toJson(logisticsEntity);
        }
        //redis缓存中没有查询到对应的订单数据，那么可能没有签收
        logisticsEntity = logisticsMapper.findByNumberAndExpressCompanCode(number, company);
        if(null == logisticsEntity){
            return "查询无结果，请隔段时间再查";
        }else{
            //此时判断查询的结果是否超过两个月
            Date pushTime = logisticsEntity.getPushTime();
            long time = new Date().getTime() - pushTime.getTime();
            DecimalFormat df = new DecimalFormat("#.00");
            double day = time * 1.0 / 1000 / 60 / 60 / 24 ;
            log.info("查询的物流单号【{}】距离现在相差的毫秒数为【{}】距离时间为【{}】天",logisticsEntity.getLogisticsNo(),time,df.format(day));
            if(day > 60){
                return "此物流订单超过两个月，请选择其他物流查询平台！！！";
            }
        }
        logisticsEntity.setDataObject(gson.fromJson(logisticsEntity.getData(), List.class));
        return gson.toJson(logisticsEntity);
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
        LogisticsEntity result = logisticsMapper.findByNumberAndExpressCompanCode(lastResult.getNu(),lastResult.getCom());
        LogisticsEntity entity = new LogisticsEntity();
        entity.setLogisticsNo(lastResult.getNu());
        entity.setIsCheck(lastResult.getIscheck());
        entity.setStatus(lastResult.getState());
        entity.setExpressCompanCode(lastResult.getCom());
        entity.setExpressCompan(lastResult.getCom());
        entity.setData(gson.toJson(lastResult.getData()));
        if(null != result){
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
            //此时代表签收完成了，将此数据放入到redis缓存中，并且设置有效期为两个月
            StringBuffer key = new StringBuffer(entity.getLogisticsNo());
            key.append(entity.getExpressCompanCode());
            boolean flag = redisUtil.set(key.toString(), entity, 1 * 60 * 60 * 24 * 60);
            if(flag){
                log.info("快递编号为【{}】存放redis数据库中成功，存放数据为【{}】，数据有效期两个月",entity.getLogisticsNo(),entity);
            }else{
                log.error("快递编号为【{}】存放redis数据库中失败，存放数据为【{}】",entity.getLogisticsNo(),entity);
                map.put("message","快递编号为："+entity.getLogisticsNo()+"存放redis数据库中失败");
                return gson.toJson(map);
            }
        }
        // 处理快递结果
        map.put("result",true);
        map.put("returnCode","200");
        map.put("message","保存成功");
        //这里必须返回，否则认为失败，过30分钟又会重复推送。
        //response.getWriter().print(JSONObject.parseObject(param));
        return gson.toJson(map);
    }
}
