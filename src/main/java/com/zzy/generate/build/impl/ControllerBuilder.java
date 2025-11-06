package com.zzy.generate.build.impl;

import com.zzy.generate.build.TemplateBuilder;
import com.zzy.generate.enums.TemplateEnum;
import com.zzy.generate.info.TableInfo;
import com.zzy.generate.utils.TableInfoUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ControllerBuilder extends TemplateBuilder {

    @Override
    protected Template getTemplate() {
        Configuration config = super.getConfiguration();
        try {
            return config.getTemplate(TemplateEnum.CONTROLLER.templateName);
        } catch (Exception e) {
            throw new RuntimeException("Template not found: " + TemplateEnum.CONTROLLER.templateName, e);
        }
    }

    @Override
    protected Map<String, Object> getDataModel(TableInfo tableInfo) {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("packageName", this.parentPackageName + "." + this.packageName);
        dataModel.put("restControllerStyle", true);
        dataModel.put("superControllerClass", "BaseController");
        dataModel.put("superControllerClassPackage", "com.sys." + author + ".controller.common.BaseController");
        dataModel.put("author", author);
        dataModel.put("entityPath", TableInfoUtil.camelCaseToUnderscore(tableInfo.getTableName()));
        dataModel.put("className", tableInfo.getTableName() + "Controller");

        dataModel.put("table", new HashMap<String, Object>() {{
            put("tableName", TableInfoUtil.firstCharLowerCase(tableInfo.getTableName()));
            put("tableNameUP", TableInfoUtil.firstCharUpperCase(tableInfo.getTableName()));
        }});
        log.info("生成dataModel->{}", dataModel);
        return dataModel;
    }
}
