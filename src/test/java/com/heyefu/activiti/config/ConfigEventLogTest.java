package com.heyefu.activiti.config;

import org.activiti.engine.event.EventLogEntry;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Description:
 * 事件日志测试.
 *
 * @author heyefu
 * Create in: 2019-06-11
 * Time: 16:50
 **/
public class ConfigEventLogTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigEventLogTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti.cfg.xml");    //我们配置使用activiti_eventlog.cfg.xml文件

    //单元测试启动之前将这个定义文件部署到流程引擎中
    @Test
    @Deployment(resources = {"process/moreSimple.bpmn"})
    public void test() {
        ProcessInstance processInstance = activitiRule.getRuntimeService().
                startProcessInstanceByKey("moreSimple");
        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();
        //执行Task
        activitiRule.getTaskService().complete(task.getId());

        //基于流程实例id去取
        List<EventLogEntry> eventLogEntries = activitiRule.getManagementService()
                .getEventLogEntriesByProcessInstanceId(processInstance.getProcessInstanceId());

        for (EventLogEntry eventLogEntry : eventLogEntries) {
            LOGGER.info("eventlog的类型 = [{}],eventLog的内容 = [{}]", eventLogEntry.getType(), new String(eventLogEntry.getData()));    //将事件日志全部输出看看
        }
        LOGGER.info("eventLog的计数条数: [{}]", eventLogEntries.size());
    }
}

