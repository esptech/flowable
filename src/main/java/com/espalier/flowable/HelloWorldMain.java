package com.espalier.flowable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;

public class HelloWorldMain {

    public static void main(String[] args) {
        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
                .setJdbcUrl("jdbc:h2:mem:flowable;DB_CLOSE_DELAY=-1")
                .setJdbcUsername("sa")
                .setJdbcPassword("")
                .setJdbcDriver("org.h2.Driver")
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

        ProcessEngine processEngine = cfg.buildProcessEngine();

        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("hello.bpmn20.xml")
                .deploy();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();
        System.out.println("Found process definition : " + processDefinition.getName());

        for (int cl=0; cl < 1000; cl++){
            RuntimeService runtimeService = processEngine.getRuntimeService();
            Map<String, Object> variables = new HashMap<String, Object>();
            variables.put("name", "Joe");
            variables.put("test", new Test(cl,"Tom"));
            ProcessInstance processInstance =
                    runtimeService.startProcessInstanceByKey("helloWorld", variables);

            HistoryService historyService = processEngine.getHistoryService();
            List<HistoricActivityInstance> activities =
                    historyService.createHistoricActivityInstanceQuery()
                            .processInstanceId(processInstance.getId())
                            .finished()
                            .orderByHistoricActivityInstanceEndTime().asc()
                            .list();

            for (HistoricActivityInstance activity : activities) {
                System.out.println(activity.getActivityId() + " took "
                        + activity.getDurationInMillis() + " milliseconds");
            }
        }
    }

    public static class Test implements Serializable {
        int id;
        String name;

        public Test(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
