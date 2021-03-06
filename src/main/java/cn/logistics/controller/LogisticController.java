package cn.logistics.controller;

import cn.logistics.bean.UserBean;
import cn.logistics.service.LogisticsService;
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
     * @param
     * @return
     */
    @GetMapping("/api/logistics/subscribleActual")
    public String subscribleLogistics(@RequestParam("com") String company, @RequestParam("nu") String number) {
        log.info("订阅的运单编号为【{}】,快递公司编码为【{}】",number,company);
        return logisticsService.subscribleLogisticsPost(company,number);
    }

    /**
     * 根据快递单号和快递公司查询物流信息
     * @param company
     * @return
     */
    @GetMapping("/queryLogisticsInfoByNumAndCom")
    public String getLogistics(@RequestParam("com") String company, @RequestParam("nu") String number) {
        return logisticsService.queryLogisticsInfoByNumAndCom(company,number);
    }

    @PostMapping("/rollbackKuaiDi100")
    public String rollbackKuaiDi100(HttpServletRequest request, HttpServletResponse response){
        //request.setAttribute("param",new Gson().toJson(paramBody));
        return logisticsService.rollbackKuaiDi100(request,response);
    }

    @GetMapping("/queryActuralLogisticsByNumAndCom")
    public String queryActuralLogisticsByNumAndCom(@RequestParam("com") String company, @RequestParam("nu") String number){
        return logisticsService.queryActuralLogisticsByNumAndCom(company, number);
    }

    @GetMapping("/testRedis")
    public String testRedis(){
        return logisticsService.testRedis();
    }
}
