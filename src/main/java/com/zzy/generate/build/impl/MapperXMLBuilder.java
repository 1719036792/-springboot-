package com.zzy.generate.build.impl;

import com.zzy.generate.build.TemplateBuilder;
import com.zzy.generate.enums.TemplateEnum;
import com.zzy.generate.info.TableInfo;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.util.HashMap;
import java.util.Map;

public class MapperXMLBuilder extends TemplateBuilder {

    @Override
    protected Template getTemplate() {
        Configuration config = super.getConfiguration();
        try {
            return config.getTemplate(TemplateEnum.MAPPER_XML.templateName);
        } catch (Exception e) {
            throw new RuntimeException("Template not found: " + TemplateEnum.MAPPER_XML.templateName, e);
        }
    }

    @Override
    protected Map<String, Object> getDataModel(TableInfo tableInfo) {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("isXml", true);
        dataModel.put("namespace", "com.sys." + author + ".mapper." + tableInfo.getTableName() + "Mapper");
        dataModel.put("className", tableInfo.getTableName() + "Mapper");
        return dataModel;
    }
}
