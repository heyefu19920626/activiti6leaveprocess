package com.heyefu.activiti.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 * 自定义事件监听器.
 *
 * @author heyefu
 * Create in: 2019-06-13
 * Time: 15:45
 **/
public class CustomEventListener  implements ActivitiEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomEventListener.class);

    @Override
    public void onEvent(ActivitiEvent event){
        ActivitiEventType eventType = event.getType();

        //我们定义的自定义事件
        if (ActivitiEventType.CUSTOM.equals(eventType)){
            //输出事件的类型与ID
            LOGGER.info("监听到自定义事件: [{}] \t [{}]" , eventType,event.getProcessInstanceId());
        }
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}
