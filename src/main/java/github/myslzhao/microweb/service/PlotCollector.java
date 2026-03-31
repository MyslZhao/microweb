package github.myslzhao.microweb.service;

import github.myslzhao.microweb.controller.PlotsController;
import github.myslzhao.microweb.dto.FromService;
import github.myslzhao.microweb.dto.ToService;
import org.springframework.stereotype.Service;

/**
 * TODO:点集生成类,
 * 按x周方向等距选取
 */
@Service
public class PlotCollector {
    private final Inflactor inflactor;

    public PlotCollector(Inflactor inflactor){
        this.inflactor = inflactor;
    }

    /**
     * TODO:点集生成方法入口
     * @return 服务器响应
     */
    public FromService genePlots(ToService req){
        return new FromService();
    }
}
