package github.myslzhao.microweb.controller;

import github.myslzhao.microweb.dto.FromService;
import github.myslzhao.microweb.dto.ToService;
import github.myslzhao.microweb.service.PlotCollector;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * 前端JSON数据处理类
 *
 * @author MyslZhao
 */
@RestController
@RequestMapping("/api")
@Tag(name = "图形计算器接口", description = "生成函数图像的点集JSON数据")
public class PlotsController {

	private final PlotCollector plotCollector;

	public PlotsController(PlotCollector plotcollector) {
		this.plotCollector = plotcollector;
	}

	@Tag(name = "点集源")
	@PostMapping("/render")
	public FromService render(@RequestBody ToService req) {
		return plotCollector.genePlots(req);
	}

}
