package com.espalier.flowable;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class ExternalJavaDelegate implements JavaDelegate {
    public void execute(DelegateExecution execution) {
        Integer var = (Integer) execution.getVariable("var");
        System.out.println(Thread.currentThread().getName() + " var=" + var);
        execution.setVariable("newVar", var * 10);
    }
}
