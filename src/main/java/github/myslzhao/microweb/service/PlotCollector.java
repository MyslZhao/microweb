package github.myslzhao.microweb.service;

import github.myslzhao.microweb.dto.FromService;
import github.myslzhao.microweb.dto.ToService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 点集生成类
 *
 * @author MyslZhao
 */
@Service
public class PlotCollector {

	private final Inflactor inflactor;

	private final ASTGuard astGuard;

	public PlotCollector(Inflactor inflactor, ASTGuard astGuard) {
		this.inflactor = inflactor;
		this.astGuard = astGuard;
	}

	/**
	 * 生成点集
	 * @param req 客户端请求dto
	 * @return 服务器响应dto
	 */
	public FromService genePlots(ToService req) {
		FromService a = new FromService();
		List<FromService.Point> m = new ArrayList<>();

		double start = req.getxMin();
		while (start <= req.getxMax()) {
			m.add(new FromService.Point(start, inflactor.mappingY(req.getExpression(), start)));
			start += req.getStep();
		}

		a.setPoints(m);
		return a;
	}

}
