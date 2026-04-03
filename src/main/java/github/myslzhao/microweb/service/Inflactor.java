package github.myslzhao.microweb.service;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.springframework.stereotype.Service;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * y计算类
 *
 * @author MyslZhao
 */
@Service
public class Inflactor {
	public double mappingY(String expr, double target) {
		ExprEvaluator laplace = new ExprEvaluator();

		String order = "Solve(" + expr + ", y)";

		return 0;
	}

}
