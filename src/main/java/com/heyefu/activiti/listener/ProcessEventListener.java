package com.heyefu.activiti.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 * 流程事件监听器.
 *
 * @author heyefu
 * Create in: 2019-06-13
 * Time: 15:30
 **/
public class ProcessEventListener implements ActivitiEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessEventListener.class);

    @Override
    public void onEvent(ActivitiEvent event){
        ActivitiEventType eventType = event.getType();

        //如果这个事件类型是  流程启动
        if (ActivitiEventType.PROCESS_STARTED.equals(eventType)){
            //输出事件的类型与ID
            LOGGER.info("流程启动: [{}] \t [{}]" , eventType,event.getProcessInstanceId());
            //如果流程结束
        }else if (ActivitiEventType.PROCESS_COMPLETED.equals(eventType)){
            LOGGER.info("流程结束: [{}] \t [{}]" , eventType,event.getProcessInstanceId());
        }
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}
