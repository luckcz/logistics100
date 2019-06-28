package cn.logistics.utils;

import cn.logistics.enums.LogisticEnum;
import cn.logistics.vo.PollResult;
import com.google.gson.Gson;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class KuaiDi100Util {
    /**
     * 实时查询请求地址
     */
    private static final String SYNQUERY_URL = "http://poll.kuaidi100.com/poll/query.do";
    //发布订阅模式
    public static String subscribe(String company,String number){
        String from = "";					//出发地城市
        String to = "";						//目的地城市
        String callbackurl = LogisticEnum.LOGISTIC_CALLBACK_URL.getValue();			//回调地址
        String salt = "";					//加密串
        int resultv2 = 1;					//行政区域解析
        int autoCom = 0;					//单号智能识别
        int interCom = 0;					//开启国际版
        String departureCountry = "";		//出发国
        String departureCom = "";			//出发国快递公司编码
        String destinationCountry = "";		//目的国
        String destinationCom = "";			//目的国快递公司编码
        String phone = "";					//手机号
        String result = subscribeData(company, number, from, to, callbackurl, salt, resultv2, autoCom, interCom, departureCountry, departureCom, destinationCountry, destinationCom, phone);
        log.info("订阅物流信息接口调用成功，订阅结果为：【{}】", result);
        return result;
    }

    public static String subscribeData(String company, String number, String from, String to, String callbackurl, String salt, int resultv2, int autoCom,
                                int interCom, String departureCountry, String departureCom, String destinationCountry, String destinationCom, String phone) {

        StringBuilder param = new StringBuilder("{");
        param.append("\"company\":\"").append(company).append("\"");
        param.append(",\"number\":\"").append(number).append("\"");
        param.append(",\"from\":\"").append(from).append("\"");
        param.append(",\"to\":\"").append(to).append("\"");
        param.append(",\"key\":\"").append(LogisticEnum.LOGISTIC_KEY.getValue()).append("\"");
        param.append(",\"parameters\":{");
        param.append("\"callbackurl\":\"").append(callbackurl).append("\"");
        param.append(",\"salt\":\"").append(salt).append("\"");
        if(1 == resultv2) {
            param.append(",\"resultv2\":1");
        } else {
            param.append(",\"resultv2\":0");
        }
        if(1 == autoCom) {
            param.append(",\"autoCom\":1");
        } else {
            param.append(",\"autoCom\":0");
        }
        if(1 == interCom) {
            param.append(",\"interCom\":1");
        } else {
            param.append(",\"interCom\":0");
        }
        param.append(",\"departureCountry\":\"").append(departureCountry).append("\"");
        param.append(",\"departureCom\":\"").append(departureCom).append("\"");
        param.append(",\"destinationCountry\":\"").append(destinationCountry).append("\"");
        param.append(",\"destinationCom\":\"").append(destinationCom).append("\"");
        param.append(",\"phone\":\"").append(phone).append("\"");
        param.append("}");
        param.append("}");

        Map<String, String> params = new HashMap<String, String>();
        params.put("schema", "json");
        params.put("param", param.toString());
        log.info("订阅物流信息开始，物流参数信息为：【{}】", params);
        return HttpRequestUtil.post(LogisticEnum.LOGISTIC_POLL_URL.getValue(),params);
    }

    public static String query(String num,String com){
        String phone = "";				//手机号码后四位
        String from = "";				//出发地
        String to = "";					//目的地
        int resultv2 = 0;
        String result = synQueryData(com, num, phone, from, to, resultv2);
        return result ;
    }

    public static String synQueryData(String com, String num, String phone, String from, String to, int resultv2) {

        StringBuilder param = new StringBuilder("{");
        param.append("\"com\":\"").append(com).append("\"");
        param.append(",\"num\":\"").append(num).append("\"");
        param.append(",\"phone\":\"").append(phone).append("\"");
        param.append(",\"from\":\"").append(from).append("\"");
        param.append(",\"to\":\"").append(to).append("\"");
        if(1 == resultv2) {
            param.append(",\"resultv2\":1");
        } else {
            param.append(",\"resultv2\":0");
        }
        param.append("}");

        Map<String, String> params = new HashMap<String, String>();
        params.put("customer", LogisticEnum.LOGISTIC_CUSTOMER.getValue());
        String sign = MD5Utils.encode(param + LogisticEnum.LOGISTIC_KEY.getValue() + LogisticEnum.LOGISTIC_CUSTOMER.getValue());
        params.put("sign", sign);
        params.put("param", param.toString());

        return HttpRequestUtil.post(LogisticEnum.LOGISTIC_QUERY_URL.getValue(),params);
    }
}
