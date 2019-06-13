package com.heyefu.activiti.config;

import org.activiti.engine.task.IdentityLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

/**
 * Description:
 * 流程存储测试.
 *
 * @author heyefu
 * Create in: 2019-06-13
 * Time: 16:34
 **/
public class RepositoryServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryServiceTest.class);

    //默认流程配置文件
    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    public void testRepository() {
        //获取RepositoryService对象
        RepositoryService repositoryService = activitiRule.getRepositoryService();

        //流程部署对象
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        //部署两个流程定义配置文件
        deploymentBuilder.name("测试部署资源")
                .addClasspathResource("process/moreSimple.bpmn")
                .addClasspathResource("process/leave.bpmn");

        //将部署对象与两个定义文件部署到数据库中
        Deployment deploy = deploymentBuilder.deploy();
        //输出这个部署对象
        LOGGER.info("deploy = [{}]", deploy);

        //流程部署对象
        DeploymentBuilder deploymentBuilder2 = repositoryService.createDeployment();
        //部署两个流程定义配置文件
        deploymentBuilder2.name("测试部署资源2")
                .addClasspathResource("process/moreSimple.bpmn")
                .addClasspathResource("process/leave.bpmn");
        //第二次部署
        deploymentBuilder2.deploy();

       /* //通过这个类查询
        DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery();
        //根据deploy的ID查询出一个独立的对象
        Deployment deployment = deploymentQuery.deploymentId(deploy.getId()).singleResult();
        //上下 deploy与deployment 输出的部署对象相同
        LOGGER.info("deployment = [{}]", deployment);
        //使用流程定义的查询对象,使用listPage因为有两个资源文件
        List<ProcessDefinition> definitions = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId()).listPage(0, 100);
*/


//        一次文件部署生成了7个文件：1个部署文件，2个流程定义文件，2个流程定义文件对应的数据流数据，2个请假流程定义文件中对应的图片
        DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery();
        //根据deploy的部署时间进行排序查出所有的部署对象
        List<Deployment> deploymentlist = deploymentQuery
                .orderByDeploymenTime().asc().listPage(0, 100);
        //当通过ID查找时， deploy与deployment 输出的部署对象相同
        LOGGER.info("deployment = [{}]", deploymentlist);
        for (Deployment deployment : deploymentlist) {
            LOGGER.info("deployment = [{}]", deployment);
        }
        LOGGER.info("deploymentlist.size = [{}]", deploymentlist.size());
        List<ProcessDefinition> definitions = repositoryService.createProcessDefinitionQuery()
                .orderByProcessDefinitionKey().asc().listPage(0, 100);


        for (ProcessDefinition processDefinition : definitions) {
            LOGGER.info("processDefinition = [{}] , version = [{}] , key = [{}] , id = [{}]",
                    processDefinition,
                    processDefinition.getVersion(),
                    processDefinition.getKey(),
                    processDefinition.getId());
        }
    }

    /**
     * 测试用户与用户组与流程定义文件建立关系
     */
    @Test
    @org.activiti.engine.test.Deployment(resources = {"process/moreSimple.bpmn"})
    public void testCandidateStarter() {
        RepositoryService repositoryService = activitiRule.getRepositoryService();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().singleResult();

        LOGGER.info("processDefinition的ID: [{}]", processDefinition.getId());

        //按照用户的ID（user）来添加用户
        repositoryService.addCandidateStarterUser(processDefinition.getId(), "user");
        //添加用户组
        repositoryService.addCandidateStarterGroup(processDefinition.getId(), "groupM");

        //通过流程定义获取我们设置的关系
        List<IdentityLink> identityLinkList = repositoryService
                .getIdentityLinksForProcessDefinition(processDefinition.getId());

        for (IdentityLink identityLink : identityLinkList) {
            //日志输出设置的关系
            LOGGER.info("identityLink = [{}]", identityLink);
        }

        //删除用户组
        repositoryService.deleteCandidateStarterGroup(processDefinition.getId(), "groupM");
        //删除user用户
        repositoryService.deleteCandidateStarterUser(processDefinition.getId(), "user");

    }
}

