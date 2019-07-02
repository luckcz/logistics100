package cn.logistics.entity;

import cn.logistics.vo.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

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

    private List<Data> dataObject ;

    private Date pushTime ;
}