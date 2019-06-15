package com.heyefu.activiti.config;

import com.google.common.collect.Maps;
import org.activiti.engine.FormService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * 表单管理服务.
 * <p>
 * FormService：
 * <p>
 * 1、解析流程定义中表单项的配置；
 * <p>
 * 2、提交表单的方式驱动用户节点流转，FormService可以通过submit表单方式将流程驱动；
 * <p>
 * 3、获取自定义外部表单Key。
 *
 * @author heyefu
 * Create in: 2019-06-15
 * Time: 14:56
 **/
public class FormServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(FormServiceTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    @Deployment(resources = {"process/form.bpmn"})
    public void testFormService(){
        //获取 FormService
        FormService formService = activitiRule.getFormService();
        //流程定义对象
        ProcessDefinition processDefinition = activitiRule.getRepositoryService()
                .createProcessDefinitionQuery().singleResult();

        String startFormKey = formService.getStartFormKey(processDefinition.getId());
        //[/rest/process/form/start]  配制文件中 formKey 参数值
        LOGGER.info("startFormKey = [{}]", startFormKey);

        StartFormData startFormData = formService.getStartFormData(processDefinition.getId());
        //获得表单列表
        List<FormProperty> formProperties = startFormData.getFormProperties();
        for (FormProperty formProperty:formProperties){
            LOGGER.info("formProperty = [{}]" , ToStringBuilder.reflectionToString(formProperty,ToStringStyle.JSON_STYLE));
        }

        Map<String,String> properties = Maps.newHashMap();
        properties.put("message","test message");
        properties.put("date", new SimpleDateFormat("yyyy-MM-dd hh:mm").format(new Date()));
        //根据formService启动流程
        ProcessInstance processInstance = formService.submitStartFormData(processDefinition.getId(), properties);

        //获取唯一的Task
        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();

        //获取task表单
        TaskFormData taskFormData = formService.getTaskFormData(task.getId());
        //获取属性列表
        List<FormProperty> taskFormDataFormProperties = taskFormData.getFormProperties();
        for (FormProperty property:taskFormDataFormProperties){
            LOGGER.info("property = [{}]" , ToStringBuilder.reflectionToString(property,ToStringStyle.JSON_STYLE));
        }

        HashMap<String,String> yesORno = Maps.newHashMap();
        yesORno.put("yesORno","yes");
        formService.submitTaskFormData(task.getId(),yesORno);
        Task task1 = activitiRule.getTaskService().createTaskQuery().taskId(task.getId()).singleResult();
        LOGGER.info("task1 = [{}]" , task1);
    }
}
