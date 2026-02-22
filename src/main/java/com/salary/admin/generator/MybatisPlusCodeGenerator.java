package com.salary.admin.generator;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class MybatisPlusCodeGenerator {

    public static void main(String[] args) {
        List<String> tableList = List.of(
                "sys_user"
        );

        FastAutoGenerator.create(
                        "jdbc:mysql://localhost:33061/salary_admin?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&serverTimezone=Asia/Shanghai",
                        "root",
                        "123456"
                )
                // 全局配置
                .globalConfig(builder -> builder
                        .author("system")
                        .outputDir(Paths.get(System.getProperty("user.dir"), "src/main/java").toString())
                        .commentDate("yyyy-MM-dd")
                        .enableSwagger() // 启用Swagger支持
                )
                // 包配置
                .packageConfig(builder -> builder
                        .parent("com.salary.admin")
                        .entity("model.entity.sys")
                        .mapper("mapper.auto")
                        .service("service")
                        .serviceImpl("service.impl")
                        .controller("controller")
                        .xml("mapper.xml")
                        .pathInfo(Collections.singletonMap(OutputFile.xml,
                                Paths.get(System.getProperty("user.dir"), "src/main/resources/mapper/auto").toString()))
                )
                // 策略配置
                .strategyConfig(builder -> builder
                        .addInclude(".*")
                        // 这里用 * 表示所有表
                        .entityBuilder()
                        .enableLombok()
                        .enableChainModel()
                        .enableActiveRecord()
                        .enableFileOverride()
                        .superClass(Model.class)
                        .enableTableFieldAnnotation()
                        .logicDeleteColumnName("delete_flag")
                        .addTableFills(
                                new com.baomidou.mybatisplus.generator.fill.Column("create_time", FieldFill.INSERT),
                                new com.baomidou.mybatisplus.generator.fill.Column("update_time", FieldFill.INSERT_UPDATE)
                        )
                        .javaTemplate("templates/entity_with_schema.java") // 只写路径，不写 .ftl
                        .mapperBuilder()
                        .enableBaseResultMap()
                        .enableBaseColumnList()
                        .controllerBuilder()
                        .enableRestStyle()
                        .enableHyphenStyle()
                )
                // 模板引擎
                .templateEngine(new FreemarkerTemplateEngine())
                // 执行生成
                .execute();
    }
}
