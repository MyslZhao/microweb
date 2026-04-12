package github.myslzhao.microweb.service;

public enum Status {
    NORMAL(0),         //无异常
    UNSUPPORTED(1),    //存在额外的变量/不支持含参函数
    UNSTANDARD(2),     //非标准的函数式
    UNSAFE(3),          //存在恶意代码
    UNKNOWN(4);         //未知的错误/无法计算

    private final int code;

    Status(int code){
        this.code = code;
    }

    public int getCode(){
        return code;
    }
}
