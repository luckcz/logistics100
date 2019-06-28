package cn.logistics.vo;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class PollResult {
    private String result ;
    /**
     * 200: 提交成功
     * 701: 拒绝订阅的快递公司
     * 700: 订阅方的订阅数据存在错误（如不支持的快递公司、单号为空、单号超长等）或错误的回调地址
     * 702: POLL:识别不到该单号对应的快递公司
     * 600: 您不是合法的订阅者（即授权Key出错）
     * 601: POLL:KEY已过期
     * 500: 服务器错误（即快递100的服务器出理间隙或临时性异常，有时如果因为不按规范提交请求，比如快递公司参数写错等，也会报此错误）
     * 501:重复订阅（请格外注意，501表示这张单已经订阅成功且目前还在跟踪过程中（即单号的status=polling），快递100的服务器会因此忽略您最新的此次订阅请求，从而返回501。一个运单号只要提交一次订阅即可，若要提交多次订阅，请在收到单号的status=abort或shutdown后隔半小时再提交订阅
     */
    private String returnCode ;
    private String message ;
}
