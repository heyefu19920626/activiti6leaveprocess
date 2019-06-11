package com.heyefu.activiti.config;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 * activiti数据库测试.
 *
 * @author heyefu
 * Create in: 2019-06-11
 * Time: 14:40
 **/
public class ConfigDBTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(ConfigDBTest.class);


    @Test
    public void testDefaultConfig() {
        ProcessEngineConfiguration cfg = ProcessEngineConfiguration.createProcessEngineConfigurationFromResourceDefault();
        LOGGER.info("configuration = [{}]", cfg);

        ProcessEngine engine = cfg.buildProcessEngine();
        LOGGER.info("获取流程引擎 = [{}]", engine);
        engine.close();
    }

    @Test
    public void testDruidConfig() {
        ProcessEngineConfiguration cfg = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.druid.xml");
        LOGGER.info("configuration = [{}]", cfg);

        ProcessEngine engine = cfg.buildProcessEngine();
        LOGGER.info("获取流程引擎 = [{}]", engine);
        engine.close();
    }
}
