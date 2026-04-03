package github.myslzhao.microweb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * dto请求数据包类
 *
 * @author MyslZhao
 */
@SuppressWarnings("unused")
public class ToService {

	/// 表达式串
	@Schema(description = "x,y表达式", example = "y=2x+1", type = "String")
	private String expression;

	/// x最小值
	@Schema(description = "渲染左端点,默认包括该点", example = "0.1", type = "double")
	@JsonProperty("xMin")
	private double xMin;

	/// x最大值
	@Schema(description = "渲染右端点,默认包括该点", example = "0.1", type = "double")
	@JsonProperty("xMax")
	private double xMax;

	/// 求解y最大值
	@Schema(description = "求解范围最大值", example = "500", type = "double")
	@JsonProperty("renderMax")
	private double renderMax;

	/// 求解y最小值
	@Schema(description = "求解范围最小值", example = "-500", type = "double")
	@JsonProperty("renderMin")
	private double renderMin;

	/// 采样步长
	@Schema(description = "渲染采样步长", type = "double", defaultValue = "0.01", minimum = "0.001")
	private double step = 0.01; // DEBUG: 暂时默认取0.01做调试用

	public String getExpression() {
		return expression;
	}

	public double getxMin() {
		return xMin;
	}

	public double getxMax() {
		return xMax;
	}

	public double getStep() {
		return step;
	}

	public double getRenderMax() {
		return renderMax;
	}

	public double getRenderMin() {
		return renderMin;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public void setxMin(double xmin) {
		this.xMin = xmin;
	}

	public void setxMax(double xmax) {
		this.xMax = xmax;
	}

	public void setStep(double step) {
		this.step = step;
	}

	public void setRenderMax(double renderMax) {
		this.renderMax = renderMax;
	}

	public void setRenderMin(double renderMin) {
		this.renderMin = renderMin;
	}

}
