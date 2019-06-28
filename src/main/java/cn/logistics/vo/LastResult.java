package cn.logistics.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class LastResult {
    private String message ;
    private Integer state ;
    private Integer status ;
    private String condition ;
    private Integer ischeck ;
    private String com ;
    private String nu ;
    private List<Data> data ;

}
