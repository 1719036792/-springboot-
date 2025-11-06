package com.zzy.generate.build.impl;

import com.zzy.generate.build.TemplateBuilder;
import com.zzy.generate.enums.TemplateEnum;
import com.zzy.generate.info.TableInfo;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ServiceImplBuilder extends TemplateBuilder {

    @Override
    protected Template getTemplate() {
        Configuration config = super.getConfiguration();
        try {
            return config.getTemplate(TemplateEnum.SERVICE_IMPL.templateName);
        } catch (Exception e) {
            throw new RuntimeException("Template not found: " + TemplateEnum.SERVICE_IMPL.templateName, e);
        }
    }

    @Override
    protected Map<String, Object> getDataModel(TableInfo tableInfo) {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("packageName", this.parentPackageName + "." + this.packageName);
        dataModel.put("author", author);
        dataModel.put("tableName", tableInfo.getTableName());
        dataModel.put("className", tableInfo.getTableName() + "ServiceImpl");
        dataModel.put("comment", tableInfo.getTableComment());

        log.info("生成dataModel->{}", dataModel);
        return dataModel;
    }
}
