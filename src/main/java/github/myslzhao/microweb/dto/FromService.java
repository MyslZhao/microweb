package github.myslzhao.microweb.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * dto发送数据包
 *
 * @author MyslZhao
 */
@SuppressWarnings("unused")
public class FromService {
	/**
	 * 样本点类
	 *
	 * @author MyslZhao
	 */
    @Schema(name = "Point", description = "样本点")
	public static class Point {

		@Schema(description = "x坐标", type = "double")
		private double x;

		@Schema(description = "y坐标", type = "double")
		private double y;

		public Point() {
		}

		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public void setX(double x) {
			this.x = x;
		}

		public void setY(double y) {
			this.y = y;
		}

	}

	@Schema(description = "点集序列", minContains = 1, type = "array")
	private List<Point> points;

	public FromService() {
	}

	public FromService(List<Point> points) {
		this.points = points;
	}

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}

}
