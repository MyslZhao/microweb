package github.myslzhao.microweb.service;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.springframework.stereotype.Service;

@Service
public class Inflactor {

	/**
	 * 根据给定的 y 表达式和 x 值，计算对应的 y 数值。
	 *
	 * @param yExpr 关于 x 的表达式，例如 "10 - x" 或 "Sin(x)"
	 * @param x     自变量的值
	 * @return 计算出的 y 值；如果表达式无效或计算失败，返回 Double.NaN
	 */
	public double mappingY(String yExpr, double x) {
		if (yExpr == null || yExpr.trim().isEmpty()) {
			return Double.NaN;
		}
		// 将表达式中的 x 替换为具体数值（注意边界，避免替换函数名中的 x）
		String exprWithValue = yExpr.replaceAll("\\bx\\b", Double.toString(x));
		try {
			EvalEngine engine = EvalEngine.get();
			IExpr result = engine.evaluate(exprWithValue);
			return result.evalDouble();
		} catch (Exception e) {
			return Double.NaN;
		}
	}
}