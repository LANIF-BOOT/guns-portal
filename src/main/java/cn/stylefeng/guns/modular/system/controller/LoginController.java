/**
 * Copyright 2018-2020 stylefeng & fengshuonan (https://gitee.com/stylefeng)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.stylefeng.guns.modular.system.controller;

import static cn.stylefeng.roses.core.util.HttpContext.getIp;

import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.code.kaptcha.Constants;

import cn.hutool.core.util.URLUtil;
import cn.stylefeng.guns.core.common.exception.InvalidKaptchaException;
import cn.stylefeng.guns.core.common.node.MenuNode;
import cn.stylefeng.guns.core.log.LogManager;
import cn.stylefeng.guns.core.log.factory.LogTaskFactory;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.core.shiro.ShiroUser;
import cn.stylefeng.guns.core.util.KaptchaUtil;
import cn.stylefeng.guns.modular.system.service.UserService;
import cn.stylefeng.roses.core.base.controller.BaseController;

/**
 * 登录控制器
 *
 * @author fengshuonan
 * @Date 2017年1月10日 下午8:25:24
 */
@Controller
@RequestMapping("/manage")
public class LoginController extends BaseController {
	private static String PREFIX = "/modular/system/";

    @Autowired
    private UserService userService;

    /**
     * 跳转到主页
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:41 PM
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Model model) {

        //获取当前用户角色列表
        ShiroUser user = ShiroKit.getUserNotNull();
        List<Long> roleList = user.getRoleList();

        if (roleList == null || roleList.size() == 0) {
            ShiroKit.getSubject().logout();
            model.addAttribute("tips", "该用户没有角色，无法登陆");
            return PREFIX + "login.html";
        }

        List<MenuNode> menus = userService.getUserMenuNodes(roleList);
        model.addAttribute("menus", menus);

        return PREFIX + "index.html";
    }

    /**
     * 跳转到登录页面
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:41 PM
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Model model, HttpServletRequest request) {
		if (ShiroKit.isAuthenticated() || ShiroKit.getUser() != null) {
			return REDIRECT + "/manage";
		} else {
			// 获取跳转前url
			String referer = request.getHeader("Referer");
			if (referer != null) {
				String refererPath = URLUtil.getPath(referer);
				String uri = request.getRequestURI();
				model.addAttribute("backURL", refererPath);
				// 如果当前是后台界面
				if (uri.indexOf("manage") > -1 && refererPath.indexOf("manage") > -1) {
					return PREFIX + "login.html";
				} else { // 如果是从前台过来的
					model.addAttribute("backURL", refererPath);
				}
			}
			return PREFIX + "login.html";
		}
	}

    /**
     * 点击登录执行的动作
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:42 PM
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginVali(HttpServletRequest request) {

        String username = super.getPara("username").trim();
        String password = super.getPara("password").trim();
        String remember = super.getPara("remember");
        String backURL = super.getPara("backURL");
        
        // 判断验证码
        if (KaptchaUtil.getKaptchaOnOff()) {
        	String kaptcha = super.getPara("code");
        	Object kaptchaObj = super.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
        	if (kaptchaObj == null || StringUtils.isBlank(kaptchaObj.toString()) || !kaptchaObj.equals(kaptcha)) {
        		throw new InvalidKaptchaException();
        	}
        }

        Subject currentUser = ShiroKit.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password.toCharArray());

        //如果开启了记住我功能
        if ("on".equals(remember)) {
            token.setRememberMe(true);
        } else {
            token.setRememberMe(false);
        }

        //执行shiro登录操作
        currentUser.login(token);

        //登录成功，记录登录日志
        ShiroUser shiroUser = ShiroKit.getUserNotNull();
        LogManager.me().executeLog(LogTaskFactory.loginLog(shiroUser.getId(), getIp()));

        ShiroKit.getSession().setAttribute("sessionFlag", true);
        // 获取跳转前url
        String referer = request.getHeader("Referer");
        String refererPath = URLUtil.getPath(referer);
        String uri = request.getRequestURI();
        
        if (StringUtils.isNotBlank(backURL) && !"".equals(request.getContextPath())) {
        	return REDIRECT + backURL.split(request.getContextPath())[1];
        } else if (uri.indexOf("manage")>-1 && refererPath.indexOf("manage")>-1){
    		return REDIRECT + "/manage";
    	}else{
    		return REDIRECT + refererPath;
    	}
//        SavedRequest savedRequest = WebUtils.getSavedRequest(request);
//        if (savedRequest != null) {
//        	return REDIRECT + savedRequest.getRequestUrl();
//        } else {
//        	return REDIRECT + "/manage";
//        }
    }

    /**
     * 退出登录
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:42 PM
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logOut(HttpServletRequest request) {
        LogManager.me().executeLog(LogTaskFactory.exitLog(ShiroKit.getUserNotNull().getId(), getIp()));
        ShiroKit.getSubject().logout();
        deleteAllCookie();
        
        // 获取跳转前url
        String referer = request.getHeader("Referer");
        String refererPath = URLUtil.getPath(referer);
        String uri = request.getRequestURI();
        // 从后台退出
    	if (uri.indexOf("manage")>-1 && refererPath.indexOf("manage")>-1){
    		return REDIRECT + "/manage/login";
    	}else{ // 从前台退出
    		return REDIRECT + "/";
    	}
    }
}
