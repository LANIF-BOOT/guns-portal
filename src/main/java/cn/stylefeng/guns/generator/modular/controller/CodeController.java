/**
 * 
 */
package cn.stylefeng.guns.generator.modular.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.stylefeng.guns.generator.executor.config.WebGeneratorConfig;
import cn.stylefeng.guns.generator.executor.model.GenQo;
import cn.stylefeng.guns.generator.modular.factory.DefaultTemplateFactory;
import cn.stylefeng.guns.generator.modular.service.TableService;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.config.properties.DruidProperties;

/**   
* 代码生成控制器
* @author weiq
* @date 2019年5月30日 上午10:06:04 
* @version V1.0
*/
@Controller
@RequestMapping("/manage/code")
public class CodeController extends BaseController{
	private static String PREFIX = "/modular/code";
	
	@Autowired
    private TableService tableService;

    @Autowired
    private DruidProperties druidProperties;
	
	@RequestMapping("")
	public String index(Model model) {
		model.addAttribute("tables", tableService.getAllTables());
		model.addAttribute("params", DefaultTemplateFactory.getDefaultParams());
		model.addAttribute("templates", DefaultTemplateFactory.getDefaultTemplates());
		return PREFIX + "/code.html";
	}
	
	/**
     * 生成代码
     */
    @RequestMapping(value = "/generate", method = RequestMethod.POST)
    @ResponseBody
    public Object generate(GenQo genQo) {
        genQo.setUrl(druidProperties.getUrl());
        genQo.setUserName(druidProperties.getUsername());
        genQo.setPassword(druidProperties.getPassword());
        WebGeneratorConfig webGeneratorConfig = new WebGeneratorConfig(genQo);
        webGeneratorConfig.doMpGeneration();
        webGeneratorConfig.doGunsGeneration();
        return SUCCESS_TIP;
    }
}
