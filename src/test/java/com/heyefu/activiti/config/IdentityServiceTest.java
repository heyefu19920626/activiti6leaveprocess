package com.heyefu.activiti.config;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.test.ActivitiRule;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Description:
 * activiti的身份管理服务.
 * <p>
 * IdentityService：
 * <p>
 * 1、管理用户（User），创建、查询与删除用户；
 * <p>
 * 2、管理用户组（Group）；
 * <p>
 * 3、用户与用户组的关系（Membership），使用Membership构建关系。
 * <p>
 * eg:identityService.createMembership("user1","group1");。
 *
 * @author heyefu
 * Create in: 2019-06-15
 * Time: 14:27
 **/
public class IdentityServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdentityServiceTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    /**
     * 测试函数中首先创建两个user1与user2用户，
     * 然后创建group1与group2用户组，再将user1、user2与group1建立关系，user1与group2建立关系，
     * 将user1的LastName设为user1_2，最后输出group1用户组中的所有用户与user1加入的所有用户组
     */
    @Test
    public void testIdentity() {
        //这里不依赖流程部署文件，所以不用配置
        IdentityService identityService = activitiRule.getIdentityService();
        //初始化用户user1
        User user1 = identityService.newUser("user1");
        user1.setEmail("user1@111.com");
        User user2 = identityService.newUser("user2");
        user2.setEmail("user2@111.com");
        //将用户保存
        identityService.saveUser(user1);
        identityService.saveUser(user2);

        //初始化用户组group1
        Group group1 = identityService.newGroup("group1");
        identityService.saveGroup(group1);
        Group group2 = identityService.newGroup("group2");
        identityService.saveGroup(group2);

        //构建用户与用户组的关系
        identityService.createMembership("user1", "group1");
        identityService.createMembership("user2", "group1");
        identityService.createMembership("user1", "group2");

        //将用户名修改,版本号改为2
        User user1_2 = identityService.createUserQuery().userId("user1").singleResult();
        user1_2.setLastName("user1_2");
        identityService.saveUser(user1_2);

        //查询用户列表
        List<User> userList = identityService.createUserQuery().memberOfGroup("group1").listPage(0, 100);
        //查询group1组中的所有用户
        for (User user : userList) {
            LOGGER.info("group1的user : [{}]", ToStringBuilder.reflectionToString(user, ToStringStyle.JSON_STYLE));
        }

        List<Group> groupList = identityService.createGroupQuery().groupMember("user1").listPage(0, 100);
        //查询user1用户所属的组
        for (Group group : groupList) {
            LOGGER.info("user1的group : [{}]", ToStringBuilder.reflectionToString(group, ToStringStyle.JSON_STYLE));
        }
    }
}
