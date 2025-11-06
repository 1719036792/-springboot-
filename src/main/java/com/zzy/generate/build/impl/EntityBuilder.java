package com.zzy.generate.build.impl;

import com.zzy.generate.build.TemplateBuilder;
import com.zzy.generate.convert.ObjectToMapConverter;
import com.zzy.generate.enums.TemplateEnum;
import com.zzy.generate.info.TableField;
import com.zzy.generate.info.TableInfo;
import com.zzy.generate.utils.PackageInfoUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 实体类类代码生成器
 */
@Slf4j
public class EntityBuilder extends TemplateBuilder {

    @Override
    public Template getTemplate() {
        Configuration config = super.getConfiguration();
        try {
            return config.getTemplate(TemplateEnum.ENTITY.templateName);
        } catch (Exception e) {
            throw new RuntimeException("Template not found: " + TemplateEnum.ENTITY.templateName, e);
        }
    }

    /**
     * 填充entity模板数据
     *
     * @param tableInfo 表信息
     * @return Map
     */
    @Override
    public Map<String, Object> getDataModel(TableInfo tableInfo) {
        Map<String, Object> dateModel = new HashMap<>();
        dateModel.put("packageName", this.parentPackageName + "." + this.packageName);
        dateModel.put("swagger2", true);
        dateModel.put("entityLombokModel", true);
        dateModel.put("entitySerialVersionUID", true);
        dateModel.put("activeRecord", false);
        dateModel.put("entityColumnConstant", false);
        dateModel.put("entity", tableInfo.getTableName());
        dateModel.put("author", author);

        ObjectToMapConverter<TableField> mapConverter = new ObjectToMapConverter<>();
        dateModel.put("table", new HashMap<String, Object>() {{
            put("comment", tableInfo.getTableComment());
            try {
                put("fields", mapConverter.convertListToMap(tableInfo.getTableFields()));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            put("convert", false);
        }});

        Set<String> packagePathList = new HashSet<>();
        tableInfo.getTableFields().forEach(field -> PackageInfoUtil.setFieldPackagePath(field.getFieldType(), packagePathList));
        dateModel.put("packagePathList", packagePathList);
        log.info("生成dataModel->{}", dateModel);
        return dateModel;
    }
}
