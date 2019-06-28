package cn.logistics.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LogisticsEntity {
    private Long id;

    private String logisticsNo;

    private String expressCompanCode;

    private Integer status;

    private Integer isCheck;

    private Long orderId;

    private String expressCompan;

    private String data;
}