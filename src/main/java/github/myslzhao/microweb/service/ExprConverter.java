package github.myslzhao.microweb.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

@Service
public class ExprConverter {

	// 允许的字符集（不包括 e/E，因为不考虑科学计数法）
	private static final Pattern ALLOWED_CHARS = Pattern.compile("^[a-zA-Z0-9+\\-*/^=()\\s.,]+$");

	// 函数名映射（小写 -> Symja 标准大写）
	private static final Map<String, String> FUNCTION_MAP = new HashMap<>();
	static {
		FUNCTION_MAP.put("ln", "Ln");
		FUNCTION_MAP.put("log", "Log");
		FUNCTION_MAP.put("sin", "Sin");
		FUNCTION_MAP.put("cos", "Cos");
		FUNCTION_MAP.put("tan", "Tan");
		FUNCTION_MAP.put("cot", "Cot");
		FUNCTION_MAP.put("sec", "Sec");
		FUNCTION_MAP.put("csc", "Csc");
		FUNCTION_MAP.put("arcsin", "ArcSin");
		FUNCTION_MAP.put("arccos", "ArcCos");
		FUNCTION_MAP.put("arctan", "ArcTan");
		FUNCTION_MAP.put("sinh", "Sinh");
		FUNCTION_MAP.put("cosh", "Cosh");
		FUNCTION_MAP.put("tanh", "Tanh");
		FUNCTION_MAP.put("sqrt", "Sqrt");
		FUNCTION_MAP.put("abs", "Abs");
	}

	/**
	 * 转换自然表达式为 Symja 规范形式，并进行基础格式校验。
	 *
	 * @param input 用户输入的原始表达式
	 * @param out   用于存放转换后表达式的 StringBuilder（成功时会被追加内容）
	 * @return 状态码：NORMAL（成功）, UNSTANDARD（格式错误）, UNSUPPORTED（不支持的写法）
	 */
	public Status convert(String input, StringBuilder out) {
		// 0. 去除空格
		input = input.replaceAll(" ", "");

		// 1. 空值检查
		if (input == null || (input = input.trim()).isEmpty()) {
			return Status.UNSTANDARD;
		}
		if (input.length() > 2000) {
			return Status.UNSTANDARD;
		}

		// 2. 字符集检查
		if (!ALLOWED_CHARS.matcher(input).matches()) {
			return Status.UNSTANDARD;
		}

		// 3. 等号数量检查（必须恰好一个 '='）
		int equalCount = input.replaceAll("[^=]", "").length();
		if (equalCount == 0) {
			return Status.UNSTANDARD; // 缺少等号
		}
		if (equalCount > 1) {
			return Status.UNSTANDARD; // 多个等号
		}

		// 4. 括号匹配检查
		if (!isBalanced(input, '(', ')')) {
			return Status.UNSTANDARD;
		}

		// 5. 连续运算符检查（禁止 "++", "--", "**", "//", "+-" 等）
		if (hasConsecutiveOperators(input)) {
			return Status.UNSTANDARD;
		}

		// 6. 开始转换
		String expr = input;

		// 6.1 自然常量替换（注意边界，避免替换变量中的字母）
		expr = expr.replaceAll("\\be\\b", "E");
		expr = expr.replaceAll("\\bpi\\b", "Pi");

		// 6.2 等号替换：= -> ==
		expr = expr.replaceAll("(?<![!<>])=(?![=>])", "==");
		// 6.3 函数名规范化（只替换名称，不自动加括号）
		expr = normalizeFunctionNames(expr);

		// 6.4 插入隐式乘号
		// HACK: 使用简单的regEx识别隐式乘号，无法覆盖全部情况，暂时选择对用户输入限制
		expr = insertImplicitMultiplication(expr);

		// 7. 检查是否包含不支持的函数调用（函数后没有括号的情况）
		//    例如 "Ln y" 是不允许的，必须写成 "Ln(y)"
		if (hasFunctionWithoutParentheses(expr)) {
			return Status.UNSTANDARD;
		}

		// 8. 所有检查通过
		out.append(expr);
		return Status.NORMAL;
	}

	// 括号匹配
	private boolean isBalanced(String s, char open, char close) {
		Stack<Character> stack = new Stack<>();
		for (char c : s.toCharArray()) {
			if (c == open) stack.push(c);
			else if (c == close) {
				if (stack.isEmpty() || stack.pop() != open) return false;
			}
		}
		return stack.isEmpty();
	}

	// 连续运算符检测
	private boolean hasConsecutiveOperators(String s) {
		String noSpace = s.replaceAll("\\s+", "");
		// 不允许连续两个运算符（但允许 "=="，因为等号尚未替换？这里检查原始输入，所以 "==" 本身不合法）
		return noSpace.matches(".*[+\\-*/^]{2,}.*") || noSpace.contains("==");
	}

	// 仅替换函数名（不处理括号）
	private String normalizeFunctionNames(String expr) {
		for (Map.Entry<String, String> entry : FUNCTION_MAP.entrySet()) {
			String lower = entry.getKey();
			String proper = entry.getValue();
			expr = expr.replaceAll("\\b" + lower + "\\b", proper);
		}
		return expr;
	}

	// 插入隐式乘号（规则不变）
	private String insertImplicitMultiplication(String expr) {
		expr = expr.replaceAll("(\\d)([a-zA-Z])", "$1*$2");
		System.out.println("DEBUG: " + expr);
		expr = expr.replaceAll("(x)([a-zA-Z])", "$1*$2");
		System.out.println("DEBUG: " + expr);
		expr = expr.replaceAll("(\\d)(\\()", "$1*$2");
		System.out.println("DEBUG: " + expr);
		expr = expr.replaceAll("(x|Pi|E)(\\()", "$1*$2");
		System.out.println("DEBUG: " + expr);
		expr = expr.replaceAll("(\\))(\\()", "$1*$2");
		System.out.println("DEBUG: " + expr);
		expr = expr.replaceAll("(\\))([a-zA-Z])", "$1*$2");
		System.out.println("DEBUG: " + expr);
		return expr;
	}

	// 检查是否存在函数名后没有括号的情况（例如 "Ln y" 或 "Ln   y"）
	private boolean hasFunctionWithoutParentheses(String expr) {
		// 匹配任何白名单中的函数名，后跟空白字符且不紧跟 '('
		for (String proper : FUNCTION_MAP.values()) {
			Pattern p = Pattern.compile("\\b" + proper + "\\s+(?!\\()");
			if (p.matcher(expr).find()) {
				return true;
			}
		}
		return false;
	}
}