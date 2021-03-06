package cn.logistics.enums;

import lombok.Getter;
import lombok.Setter;

public enum LogisticEnum {
    LOGISTIC_KEY("key","sOlXYjFm9071"),
    LOGISTIC_POLL_URL("poll_url","http://poll.kuaidi100.com/poll"),
    LOGISTIC_QUERY_URL("query_url","http://poll.kuaidi100.com/poll/query.do"),
    LOGISTIC_CUSTOMER("customer","455DE9CF8418F0DFB6FBE3E2C83AFBF6"),
    LOGISTIC_CALLBACK_URL("url","http://2552025dm3.wicp.vip/rollbackKuaiDi100");
    private String key ;
    private String value ;

    LogisticEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
