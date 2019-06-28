package cn.logistics.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class TaskRequest {
    /**
     *
     * schema= json
     *     param={
     *         "company":"ems",
     *         "number":"em263999513jp",
     *         "from":"广东省深圳市南山区",
     *         "to":"北京市朝阳区",
     *         "key":"XXX ",
     *         "parameters":{
     *             "callbackurl":"您的回调接口的地址，如http://www.您的域名.com/kuaidi?callbackid=...",
     *             "salt":"XXXXXXXXXX",
     *             "resultv2":"1",
     *             "autoCom":"1",
     *             "interCom"："1",
     *             "departureCountry":"CN",
     *             "departureCom":"ems",
     *             "destinationCountry":"JP",
     *             "destinationCom":"japanposten"
     *         }
     *     }
     */
    private Map<String,Object> parameters = new HashMap<>();
    private String key ;
    private String company ;
    private String number ;
}
