package github.myslzhao.microweb.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * dto请求数据包类
 *
 * @author MyslZhao
 */

public class ToService {
    /// 表达式串
    @Schema(description = "x,y表达式", example = "y=2x+1", type = "String")
    private String expression;

    /// x最小值
    @Schema(description = "渲染左端点,默认包括该点", type = "double")
    private double x_min;

    /// x最大值
    @Schema(description = "渲染右端点,默认包括该点", type = "double")
    private double x_max;

    /// 采样步长
    @Schema(description = "渲染采样步长", type = "double", defaultValue = "0.01")
    private double step = 0.01; // DEBUG: 暂时默认取0.01做调试用

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public double getX_min() {
        return x_min;
    }

    public void setX_min(double x_min) {
        this.x_min = x_min;
    }

    public double getX_max() {
        return x_max;
    }

    public void setX_max(double x_max) {
        this.x_max = x_max;
    }

    public double getStep() {
        return step;
    }

    public void setStep(double step) {
        this.step = step;
    }
}
