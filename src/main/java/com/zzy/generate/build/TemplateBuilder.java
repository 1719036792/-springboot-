package com.zzy.generate.build;

import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.zzy.generate.build.impl.*;
import com.zzy.generate.info.TableInfo;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.Map;

@Setter
@Getter
@Slf4j
public abstract class TemplateBuilder {

    /**
     * 模板配置文件
     */
    private static volatile Configuration configuration;

    /**
     * 模板文件路径
     */
    private final String templatePath = System.getProperty("user.dir") +  "/base-gene/src/main/resources/templates";

    /**
     * 作者
     */
    protected final String author = "pro";

    /**
     * 文件后缀
     */
    protected String fileSuffix = ".java";

    /**
     * 完整包父路径
     */
    protected final String projectPath = System.getProperty("user.dir") + "/base-server/src/main/java/com/sys/" + author;

    /**
     * xml父路径
     */
    protected final String xmlPath = System.getProperty("user.dir") + "/base-server/src/main/resources/mapper/";

    /**
     * 父类名称
     */
    protected String parentClassName;

    /**
     * 所在包父路径
     */
    protected String parentPackageName = "com.sys." + author;

    /**
     * 所在包名
     */
    protected String packageName;

    /**
     * 模板类
     */
    protected Template template;

    protected TemplateBuilder nextBuilder;

    protected TemplateBuilder() {
        initializeConfiguration();
    }

    /**
     * 生成对应的文件
     */
    public void buildFile(TableInfo tableInfo) {
        try {
            this.template = this.getTemplate();
            Map<String, Object> dataModel = this.getDataModel(tableInfo);
            String filePath = projectPath + "/" + packageName;
            if (dataModel.get("isXml") != null && (boolean)dataModel.get("isXml")) {
                filePath = xmlPath;
            }
            if (filePath.contains(".")) {
                filePath = filePath.replace(".", File.separator);
            }
            Object className = dataModel.get("className") == null ? tableInfo.getTableName() : dataModel.get("className");
            String fileName = filePath + File.separator + className + fileSuffix;
            File file = new File(fileName);
            if (!file.getParentFile().exists()) {
                boolean result = file.getParentFile().mkdirs();
                log.info("创建文件目录{}，结果：{}", file.getParentFile().getPath(), result);
            }
//            FileWriter out = new FileWriter(fileName);

            OutputStream out = Files.newOutputStream(file.toPath());
            template.process(dataModel, new OutputStreamWriter(out, ConstVal.UTF8));
            out.close();

            this.buildNext(tableInfo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    };


    /**
     * 初始化 Configuration 对象。
     */
    protected void initializeConfiguration() {
        if (configuration == null) {
            synchronized (TemplateBuilder.class) {
                if (configuration == null) {
                    try {
                        configuration = new Configuration(Configuration.VERSION_2_3_32);
                        configuration.setDirectoryForTemplateLoading(new File(templatePath));
                        configuration.setDefaultEncoding("UTF-8");
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to initialize configuration", e);
                    }
                }
            }
        }
    }

    protected Configuration getConfiguration() {
        if (configuration == null) {
            initializeConfiguration();
        }
        return configuration;
    }

    protected void buildNext(TableInfo tableInfo) {
        if (nextBuilder != null) {
            nextBuilder.buildFile(tableInfo);
        }
    }

    public static TemplateBuilder getTemplateBuilder() {
        TemplateBuilder entityBuilder = new EntityBuilder();
        entityBuilder.setPackageName("pojo");

        TemplateBuilder controllerBuilder = new ControllerBuilder();
        controllerBuilder.setPackageName("controller");

        TemplateBuilder entityVoBuilder = new EntityVoBuilder();
        entityVoBuilder.setPackageName("vo");

        TemplateBuilder serviceBuilder = new ServiceBuilder();
        serviceBuilder.setPackageName("service");

        TemplateBuilder serviceImplBuilder = new ServiceImplBuilder();
        serviceImplBuilder.setPackageName("service.impl");

        TemplateBuilder mapperBuilder = new MapperBuilder();
        mapperBuilder.setPackageName("mapper");

        TemplateBuilder mapperXmlBuilder = new MapperXMLBuilder();
        mapperXmlBuilder.setFileSuffix(".xml");

        entityBuilder.setNextBuilder(controllerBuilder);
        controllerBuilder.setNextBuilder(entityVoBuilder);
        entityVoBuilder.setNextBuilder(serviceBuilder);
        serviceBuilder.setNextBuilder(serviceImplBuilder);
        serviceImplBuilder.setNextBuilder(mapperBuilder);
        mapperBuilder.setNextBuilder(mapperXmlBuilder);
        return entityBuilder;
    }

    /**
     * 生成对应的文件
     */
    protected abstract Template getTemplate();

    /**
     * 获取需要的数据模型
     */
    protected abstract Map<String, Object> getDataModel(TableInfo tableInfo);
}
