package com.heyefu.activiti.intercetor;

import org.activiti.engine.impl.agenda.AbstractOperation;
import org.activiti.engine.impl.interceptor.DebugCommandInvoker;
import org.activiti.engine.logging.LogMDC;

/**
 * Description:
 * MDC拦截器.
 *
 * @author heyefu
 * Create in: 2019-06-11
 * Time: 15:50
 **/
public class MDCCommandInvoker extends DebugCommandInvoker {

    @Override
    public void executeOperation(Runnable runnable) {
        //判断MDC是否生效
        boolean mdcEnable = LogMDC.isMDCEnabled();
        //不管MDC是否生效，都将MDC设置为true
        LogMDC.setMDCEnabled(true);
        //判断可运行对象（runnable）是不是流程引擎支持的Operation
        if (runnable instanceof AbstractOperation) {
            //将runnable强制转换为AbstractOperation
            AbstractOperation operation = (AbstractOperation) runnable;
            //如果operation出现异常则执行
            if (operation.getExecution() != null) {
                LogMDC.putMDCExecution(operation.getExecution());
            }
        }

        super.executeOperation(runnable);
        //最后将它清理
        LogMDC.clear();
        //mdcEnable不生效
        if (!mdcEnable) {
            //把状态再设置为不生效
            LogMDC.setMDCEnabled(false);
        }

    }
}
