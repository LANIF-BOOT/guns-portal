package cn.stylefeng.guns.generator.modular.factory;


import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.guns.generator.executor.model.GenQo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模板种类构建器
 *
 * @author fengshuonan
 * @date 2017-12-04-下午2:59
 */
public class DefaultTemplateFactory {

    /**
     * 获取所有的模板种类
     */
    public static List<Map<String, Object>> getDefaultTemplates() {
        ArrayList<Map<String, Object>> templates = new ArrayList<>();
        templates.add(create("controllerSwitch", "Controller模板"));
        templates.add(create("entitySwitch", "Entity模板"));
        templates.add(create("serviceSwitch", "Service模板"));
        templates.add(create("daoSwitch", "Dao模板"));
        templates.add(create("indexPageSwitch", "首页模板"));
        templates.add(create("addPageSwitch", "添加页模板"));
        templates.add(create("editPageSwitch", "编辑页模板"));
        templates.add(create("jsSwitch", "主页JS模板"));
        templates.add(create("infoJsSwitch", "详情页JS模板"));
        templates.add(create("sqlSwitch", "SQL语句模板"));
        return templates;
    }

    /**
     * 获取默认的参数
     */
    public static GenQo getDefaultParams() {
        GenQo genQo = new GenQo();
        genQo.setProjectPath(ToolUtil.getWebRootPath(null));
        genQo.setProjectPackage("cn.stylefeng.guns");
        genQo.setCorePackage("cn.stylefeng.guns.core");
        genQo.setIgnoreTabelPrefix("sys_");
        genQo.setModuleName("system");
        genQo.setParentMenuName("系统管理");
        return genQo;
    }

    private static Map<String, Object> create(String key, String desc) {
        HashMap<String, Object> template = new HashMap<>();
        template.put("key", key);
        template.put("desc", desc);
        return template;
    }
}
