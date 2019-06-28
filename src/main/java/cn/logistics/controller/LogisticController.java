package cn.logistics.controller;

import cn.logistics.bean.UserBean;
import cn.logistics.service.LogisticsService;
import cn.logistics.vo.ParamBody;
import cn.logistics.vo.TaskRequest;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;

@RestController
@Slf4j
public class LogisticController {
    @Autowired
    private LogisticsService logisticsService ;

    @GetMapping("/getInfo")
    public String getInfo(){
        log.info("进入到测试回调接口中.......");
        UserBean userBean = null ;
        try{
            Class<UserBean> clazz = UserBean.class;
            userBean = clazz.newInstance();
            userBean.setId(1);
            userBean.setAge(10);
            Field userName = clazz.getDeclaredField("userName");
            userName.setAccessible(true);
            userName.set(userBean,"张三");
            log.info(userBean.toString());
            return userBean.toString();
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return null ;
    }

    @PostMapping("/testDataBase")
    public String testDataBase(){
       return logisticsService.addData();
    }

    /**
     * 订单订阅物流信息
     *
     * @param req
     * @return
     */
    @PostMapping("/api/logistics/subscrible/{orderId}")
    public String subscribleLogistics(@RequestBody TaskRequest req, @PathVariable long orderId) {
        log.info("订阅的订单编号为："+orderId);
        log.info("任务请求体："+req.toString());
        return logisticsService.subscribleLogistics(req, orderId);
    }

    /**
     * 根据订单号查询物流信息
     * @param orderId
     * @return
     */
    @GetMapping("/api/logistics/detail/{orderId}")
    public String getLogistics(@PathVariable long orderId) {
        return logisticsService.getLogistics(orderId);
    }

    @PostMapping("/rollbackKuaiDi100")
    public String rollbackKuaiDi100(@RequestBody ParamBody paramBody , HttpServletRequest request, HttpServletResponse response){
        request.setAttribute("param",new Gson().toJson(paramBody));
        return logisticsService.rollbackKuaiDi100(request,response);
    }
}
