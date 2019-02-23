package com.espalier.flowable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.variable.api.history.HistoricVariableInstance;

public class DemoMain {
    public static void main(String[] args) {
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("flowable.cfg.xml").buildProcessEngine();

        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        TaskService taskService = processEngine.getTaskService();
        HistoryService historyService = processEngine.getHistoryService();

        repositoryService.createDeployment().addClasspathResource("demo-process.bpmn20.xml").deploy();
        System.out.println("Process definitions deployed = " + repositoryService.createProcessDefinitionQuery().count());

        Random random = new Random();
        for (int i=0; i<100; i++) {
            Map<String, Object> vars = new HashMap<>();
            vars.put("var", random.nextInt(100));
            runtimeService.startProcessInstanceByKey("myProcess", vars);
        }

        System.out.println("Process instances running = " + runtimeService.createProcessInstanceQuery().count());
        LinkedList<Task> tasks = new LinkedList<>(taskService.createTaskQuery().list());
        int taskCounter = 0;
        while (!tasks.isEmpty()) {
            Task task = taskService.createTaskQuery().taskId(tasks.pop().getId()).singleResult();
            if (task != null) {
                taskService.complete(task.getId());

                taskCounter++;
                if (taskCounter % 10 == 0) {
                    System.out.println("Completed " + taskCounter + " tasks");
                }
            }

            if (tasks.isEmpty()) {
                tasks.addAll(taskService.createTaskQuery().list());
            }
        }

        System.out.println("Finished all tasks. Finished process instances = "
                + historyService.createHistoricProcessInstanceQuery().finished().count());

        List<HistoricProcessInstance> instances =
                historyService.createHistoricProcessInstanceQuery().orderByProcessInstanceId().asc().list();

        for (HistoricProcessInstance instance : instances) {
            System.out.println(instance.getId() + " " + instance.getProcessDefinitionKey() +  " took " + instance.getDurationInMillis()
                    + " milliseconds");
            List<HistoricVariableInstance> variables = historyService.createHistoricVariableInstanceQuery().processInstanceId(instance.getId()).list();
            variables.forEach(v -> System.out.println(v));
        }

        processEngine.close();
    }
}
