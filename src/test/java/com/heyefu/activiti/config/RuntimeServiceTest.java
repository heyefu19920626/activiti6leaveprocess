package com.heyefu.activiti.config;

import com.google.common.collect.Maps;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceBuilder;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Description:
 * 运行时类的测试.
 *
 * @author heyefu
 * Create in: 2019-06-14
 * Time: 9:44
 **/
public class RuntimeServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeServiceTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    /**
     * 根据流程实例对象来查询
     */
    @Test
    @org.activiti.engine.test.Deployment(resources = {"process/moreSimple.bpmn"})
    public void testExecutionQuery() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        Map<String, Object> variables = Maps.newHashMap();
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("moreSimple", variables);
        LOGGER.info("processInstance = [{}]", processInstance);

/*
        1、流程实例（ProcessInstance）表示一次工作流业务的数据实体；
        2、执行流（Execution）表示流程实例汇总具体的执行路径；
        3、流程实例接口（ProcessInstance）继承了执行流（Execution），流程实例在执行流的基础上扩展了一些其他系列的操作。
*/

        //获取执行流
        List<Execution> executionList = runtimeService.createExecutionQuery()
                .listPage(0, 100);

        for (Execution execution : executionList) {
            LOGGER.info("execution = [{}]", execution);
        }
    }

    /**
     * 根据流程实例来查询
     */
    @Test
    @org.activiti.engine.test.Deployment(resources = {"process/moreSimple.bpmn"})
    public void testProcessInstanceQuery() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        Map<String, Object> variables = Maps.newHashMap();
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("moreSimple", variables);
        LOGGER.info("processInstance = [{}]", processInstance);

        //processInstance1与processInstance 对象相同
        ProcessInstance processInstance1 = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstance.getId()).singleResult();
        LOGGER.info("processInstance1 = [{}]", processInstance1);
    }


    /**
     * 根据变量测试的测试方法
     */
    @Test
    @org.activiti.engine.test.Deployment(resources = {"process/moreSimple.bpmn"})
    public void testVariables() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        //启动流程时传入一些参数
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("key1", "value1");
        variables.put("key2", "value2");
        //我们通过Key来启动流程  前面的参数是 process/moreSimple.bpmn 里的id值
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("moreSimple", variables);
        //输出流程实例对象
        LOGGER.info("processInstance = [{}]", processInstance);

        //对参数进行修改，添加一个key3数据
        runtimeService.setVariable(processInstance.getId(), "key3", "value3");
        //将key2键值修改为value2_2
        runtimeService.setVariable(processInstance.getId(), "key2", "value2_2");

        //通过执行Id来获取参数
        Map<String, Object> variables1 = runtimeService.getVariables(processInstance.getId());
        //可以获取实例对象中的参数
        LOGGER.info("variables1 = [{}]", variables1);
    }

    /**
     * 根据key启动流程
     */
    @Test
    @org.activiti.engine.test.Deployment(resources = {"process/moreSimple.bpmn"})
    public void testStartProcessByKey() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        //启动流程时传入一些参数
        Map<String, Object> variables = Maps.newHashMap();
        //添加一个参数为key1，值为value1
        variables.put("key1", "value1");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("moreSimple", variables);
        //输出流程实例对象
        LOGGER.info("processInstance = [{}]", processInstance);
    }

    /**
     * 根据流程ID启动流程
     */
    @Test
    @org.activiti.engine.test.Deployment(resources = {"process/moreSimple.bpmn"})
    public void testStartProcessById() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        //使用RepositoryService获取流程定义对象
        ProcessDefinition processDefinition = activitiRule
                .getRepositoryService().createProcessDefinitionQuery().singleResult();
        //启动流程时传入一些参数
        Map<String, Object> variables = Maps.newHashMap();
        //添加一个参数为key1，值为value1
        variables.put("key1", "value1");
        //我们通过流程Id来启动流程
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceById(processDefinition.getId(), variables);
        //输出流程实例对象
        LOGGER.info("processInstance = [{}]", processInstance);
    }

    /**
     * 根据builder来部署流程
     */
    @Test
    @org.activiti.engine.test.Deployment(resources = {"process/moreSimple.bpmn"})
    public void testProcessInstanceBuilder() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        //启动流程时传入一些参数
        Map<String, Object> variables = Maps.newHashMap();
        //添加一个参数为key1，值为value1
        variables.put("key1", "value1");
        //获取到流程实例建造者
        ProcessInstanceBuilder processInstanceBuilder = runtimeService.createProcessInstanceBuilder();
        //设置流程定义Key   变量  再将它启动
        ProcessInstance processInstance = processInstanceBuilder.businessKey("businessKey001")
                .processDefinitionKey("moreSimple")
                .variables(variables)
                .start();
        //输出流程实例对象
        LOGGER.info("processInstance = [{}]", processInstance);
    }
}
