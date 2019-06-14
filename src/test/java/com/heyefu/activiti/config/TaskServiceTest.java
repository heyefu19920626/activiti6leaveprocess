package com.heyefu.activiti.config;

import com.google.common.collect.Maps;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * Description:
 * 任务管理服务测试.
 * <p>
 * TaskService功能：
 * 1、对用户任务（UserTask）管理和流程的控制；
 * 2、设置用户任务（UserTask）的权限信息（拥有者，候选人，办理人）；
 * 3、针对用户任务添加任务附件、任务评论和事件记录。
 * TaskService对Task管理与流程控制：
 * 1、Task对象的创建，删除。但是很少使用TaskService手动创建Task，因为一般Task创建通过流程定义。
 * 2、查询Task，并驱动Task节点完成执行。
 * 3、Task相关参数变量（variable）设置。其中本地变量，只能基于TaskId获取，在整个流程过程中使不可变的。
 *
 * @author heyefu
 * Create in: 2019-06-14
 * Time: 13:43
 **/
public class TaskServiceTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    /**
     * Task事件权限的配置
     * <p>
     * TaskService设置Task权限信息：
     * 1、候选用户（candidateUser）和候选组（candidateGroup）；
     * 2、指定拥有人（Owner）和办理人（Assignee）；
     * 3、通过claim设置办理人。
     */
    @Test
    @Deployment(resources = {"process/task.bpmn"})
    public void testTaskServiceUser() {
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("message", "my test message . . .");
        //启动流程
        activitiRule.getRuntimeService().startProcessInstanceByKey("task", variables);
        TaskService taskService = activitiRule.getTaskService();
        //获取唯一的当前流程暂停的结点
        Task task = taskService.createTaskQuery().singleResult();
        //使用json格式输出
        LOGGER.info("task = [{}]", ToStringBuilder.reflectionToString(task, ToStringStyle.JSON_STYLE));
        //输出配置描述信息
        LOGGER.info("task.description = [{}]", task.getDescription());

        //设置task的发起人owner，这里是user1用户
        taskService.setOwner(task.getId(), "user1");
        // 设置task事件的代办人,但是没有对属性进行校验，会发生权限冲突,不推荐
        //taskService.setAssignee(task.getId(),"jjf");
        //指定了 heyefu 候选人，但是taskUnassigned没有被指定某人个
        List<Task> taskList = taskService.createTaskQuery()
                .taskCandidateUser("heyefu").taskUnassigned().listPage(0, 100);

        for (Task task1 : taskList) {
            try {
                //claim时发现指定了代办人则会报错，所以要捕获异常
                taskService.claim(task1.getId(), "heyefu");
            } catch (Exception e) {
                //打印异常日志
                LOGGER.error(e.getMessage(), e);
            }
        }
        //查询指定的task与多少用户相关
        List<IdentityLink> identityLinksForTask = taskService.getIdentityLinksForTask(task.getId());
        for (IdentityLink identityLink : identityLinksForTask) {
            LOGGER.info("identityLink = [{}]", identityLink);
        }

        //将用户任务向下继续执行
        List<Task> tasks = taskService.createTaskQuery().taskAssignee("heyefu").listPage(0, 100);

        for (Task task1 : tasks) {
            Map<String, Object> vars = Maps.newHashMap();
            vars.put("skey1", "cvalue1");
            taskService.complete(task1.getId(), vars);
        }

        LOGGER.info("task是否为空 [{}]", CollectionUtils.isEmpty(tasks));
        tasks = taskService.createTaskQuery().taskAssignee("heyefu").listPage(0, 100);
        LOGGER.info("task是否为空 [{}]", CollectionUtils.isEmpty(tasks));

    }

    /**
     * TaskService对Task的查询与设置变量
     */
    @Test
    @Deployment(resources = {"process/task.bpmn"})
    public void testTaskService() {
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("message", "my test message . . .");
        //启动流程
        activitiRule.getRuntimeService().startProcessInstanceByKey("task", variables);
        TaskService taskService = activitiRule.getTaskService();
        //获取唯一的当前流程暂停的结点
        Task task = taskService.createTaskQuery().singleResult();
        //使用json格式输出
        LOGGER.info("task = [{}]", ToStringBuilder.reflectionToString(task, ToStringStyle.JSON_STYLE));
        //输出配置描述信息
        LOGGER.info("task.description = [{}]", task.getDescription());

        //给task设置普通变量
        taskService.setVariable(task.getId(), "key1", "value1");
        //给task设置本地变量
        taskService.setVariableLocal(task.getId(), "localKey1", "localValue1");

        //获取变量
        Map<String, Object> taskServiceVariables = taskService.getVariables(task.getId());
        //获取本地变量
        Map<String, Object> taskServiceVariablesLocal = taskService.getVariablesLocal(task.getId());

        //根据执行流获取变量
        Map<String, Object> processVariables = activitiRule.getRuntimeService().getVariables(task.getExecutionId());

        //{key1=value1, localKey1=localValue1, message=my test message . . .}
        LOGGER.info("taskServiceVariables = [{}]", taskServiceVariables);
        //{localKey1=localValue1}
        LOGGER.info("taskServiceVariablesLocal = [{}]", taskServiceVariablesLocal);
        //{key1=value1, message=my test message . . .}
        LOGGER.info("processVariables = [{}]", processVariables);

        Map<String, Object> completeVar = Maps.newConcurrentMap();
        completeVar.put("cKey1", "cValue1");
        //将流程驱动向下结点执行
        taskService.complete(task.getId());

        //测试看看流程有没有向下执行，若向下执行则为空
        Task task1 = taskService.createTaskQuery().taskId(task.getId()).singleResult();
        //null
        LOGGER.info("Task = [{}]", task1);
    }
}
