package cn.logistics.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ParamBody {
    private String status ;
    private String message ;
    private String autoCheck ;
    private String comOld ;
    private String comNew ;
    private LastResult lastResult ;
    private LastResult destResult ;
}
