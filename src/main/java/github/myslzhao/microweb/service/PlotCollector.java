package github.myslzhao.microweb.service;

import github.myslzhao.microweb.dto.FromService;
import github.myslzhao.microweb.dto.ToService;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.core.parser.ExprParserFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PlotCollector {

	private final ExprConverter exprConverter;
	private final ExprGuard exprGuard;
	private final Inflactor inflactor;

	public PlotCollector(ExprConverter exprConverter, ExprGuard exprGuard, Inflactor inflactor) {
		this.exprConverter = exprConverter;
		this.exprGuard = exprGuard;
		this.inflactor = inflactor;
	}

	public FromService genePlots(ToService req) {
		FromService response = new FromService();

		// ========== 1. 自然表达式转换 + 基础格式校验 ==========
		StringBuilder converted = new StringBuilder();
		Status convertStatus = exprConverter.convert(req.getExpression(), converted);
		if (convertStatus != Status.NORMAL) {
			response.setstatusCode(convertStatus);
			return response;
		}
		String equationStr = converted.toString();   // 例如 "x*y + Ln(y) == 0"

		// ========== 2. 解析方程部分为 AST ==========
		EvalEngine engine = EvalEngine.get();
		ExprParser parser = new ExprParser(engine, ExprParserFactory.RELAXED_STYLE_FACTORY, true);
		IExpr equationAST;
		try {
			equationAST = parser.parse(equationStr);
		} catch (RuntimeException e) {
			response.setstatusCode(Status.UNSTANDARD);
			return response;
		}

		//DEBUG
		System.out.println("equationStr: " + equationStr);
		System.out.println("equationAST class: " + equationAST.getClass().getName());
		System.out.println("equationAST full: " + equationAST.fullFormString());

		// ========== 3. AST 安全校验（检查方程部分） ==========
		Status securityStatus = exprGuard.isHacking(equationAST);
		if (securityStatus != Status.NORMAL) {
			response.setstatusCode(securityStatus);
			return response;
		}

		// ========== 4. 提取 y 表达式 ==========
		String yExprStr;
		if (equationAST instanceof IAST ast && ast.head().equals(F.Equal) && ast.arg1().toString().equals("y")) {
			yExprStr = ast.arg2().toString();
		} else {
			// ========== 符号求解 y = f(x) ==========
			IExpr solveCommand = F.Solve(equationAST, F.y);

			//DEBUG
			System.out.println("solveCommand: " + solveCommand.fullFormString());

			IExpr solveResult;
			try {
				solveResult = engine.evaluate(solveCommand);
			} catch (RuntimeException e) {
				response.setstatusCode(Status.UNSUPPORTED);
				return response;
			}

			// ========== 从求解结果中提取 y 的表达式字符串 ==========
			yExprStr = extractYExpression(solveResult);
			if (yExprStr == null) {
				response.setstatusCode(Status.UNSUPPORTED);
				return response;
			}
		}

		// ========== 6. 生成点集 ==========
		List<FromService.Point> pointSet = new ArrayList<>();
		double start = req.getxMin();
		double step = req.getStep();
		double end = req.getxMax();

		for (double x = start; x <= end + 1e-12; x += step) {
			double y = inflactor.mappingY(yExprStr, x);
			if (!Double.isNaN(y) && y >= req.getRenderMin() && y <= req.getRenderMax()) {
				pointSet.add(new FromService.Point(x, y));
			}
		}

		response.setPoints(pointSet);
		response.setstatusCode(Status.NORMAL);
		return response;
	}

	/**
	 * 从 Solve 结果中提取 y 的表达式字符串。
	 * 例如输入 "{{y -> 10 - x}}" 返回 "10 - x"。
	 * 若结果为空或格式异常，返回 null。
	 */
	private String extractYExpression(IExpr solveResult) {
		String resultStr = solveResult.toString();
		// 匹配 "y -> ..." 模式，取第一个解
		Pattern pattern = Pattern.compile("y\\s*->\\s*([^,\\}]+)");
		Matcher matcher = pattern.matcher(resultStr);
		if (matcher.find()) {
			return matcher.group(1).trim();
		}
		return null;
	}
}