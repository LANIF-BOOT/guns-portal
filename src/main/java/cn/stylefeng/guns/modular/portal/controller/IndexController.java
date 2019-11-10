package cn.stylefeng.guns.modular.portal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.stylefeng.roses.core.base.controller.BaseController;

@Controller
@RequestMapping(value = "/")
public class IndexController extends BaseController {
	private String PREFIX = "/modular/portal/";

	@RequestMapping(value = "")
	public String index() {
		return PREFIX + "index.html";
	}
}
