package github.myslzhao.microweb.service;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
public class ExprGuard {

	// 允许的数学函数（包括运算符和方程中的 Equal）
	private static final Set<String> KEYWORDS = Set.of(
			"Plus", "Times", "Power", "Equal",  // 运算符和方程
			"Sin", "Cos", "Tan", "Cot", "Sec", "Csc",
			"ArcSin", "ArcCos", "ArcTan",
			"Sinh", "Cosh", "Tanh",
			"Log", "Log10", "Exp", "Sqrt", "Abs",
			"Floor", "Ceil", "Round"
	);

	// 允许的变量和常量
	private static final Set<String> SYMBOLS = Set.of("x", "y", "E", "Pi", "Degree");

	/**
	 * 对表达式进行 AST 安全校验。
	 * @param expr 待校验的表达式（应该是方程部分，不含 Solve）
	 * @return NORMAL: 安全；UNSUPPORTED: 包含不允许的变量/常量；UNSAFE: 包含危险函数或结构
	 */
	public Status isHacking(IExpr expr) {
		if (expr.isAtom()) {
			if (expr instanceof ISymbol) {
				String name = expr.toString();
				return SYMBOLS.contains(name) ? Status.NORMAL : Status.UNSUPPORTED;
			}
			// 数字是安全的
			if (expr.isNumber()) {
				return Status.NORMAL;
			}
			// 其他原子类型（字符串、模式等）不安全
			return Status.UNSUPPORTED;
		}

		if (expr instanceof IAST ast) {
			IExpr head = ast.head();
			if (!(head instanceof ISymbol)) {
				return Status.UNSAFE; // 非常规结构，如 (x+y)[z]
			}
			String funcName = head.toString();
			if (!KEYWORDS.contains(funcName)) {
				return Status.UNSAFE; // 未知函数（可能是 Import 等）
			}

			// 递归检查所有参数
			for (int i = 1; i < ast.size(); i++) {
				Status subStatus = isHacking(ast.get(i));
				if (subStatus != Status.NORMAL) {
					return subStatus;
				}
			}
			return Status.NORMAL;
		}

		return Status.UNSUPPORTED;
	}
}