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
    private final PlotCollector plot_collector;

    public PlotsController(PlotCollector plot_collector){
        this.plot_collector = plot_collector;
    }

    @Tag(name = "点集源")
    @PostMapping("/render")
    public FromService render(@RequestBody ToService req){
        return plot_collector.genePlots(req);
    }
}
