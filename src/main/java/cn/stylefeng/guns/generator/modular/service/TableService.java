package cn.stylefeng.guns.generator.modular.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;

/**
 * 获取数据库所有的表
 *
 * @author fengshuonan
 * @date 2017-12-04-下午1:37
 */
@Service
public class TableService {

    @Value("${spring.datasource.data-source-name:guns}")
    private String dbName;

    /**
     * 获取当前数据库所有的表信息
     */
    public List<Map<String, Object>> getAllTables() {
        String sql = "select TABLE_NAME as tableName,TABLE_COMMENT as tableComment from information_schema.`TABLES` where TABLE_SCHEMA = '" + dbName + "'";
        return SqlRunner.db().selectList(sql);
    }
}
