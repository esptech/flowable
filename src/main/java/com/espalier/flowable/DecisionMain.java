package com.espalier.flowable;

import java.util.List;
import java.util.Map;

import org.flowable.dmn.api.DmnDeployment;
import org.flowable.dmn.api.DmnManagementService;
import org.flowable.dmn.api.DmnRepositoryService;
import org.flowable.dmn.api.DmnRuleService;
import org.flowable.dmn.engine.DmnEngine;
import org.flowable.dmn.engine.DmnEngineConfiguration;

public class DecisionMain {

    public static void main(String[] args) {
        // DmnEngine dmnEngine = DmnEngineConfiguration.createStandaloneInMemDmnEngineConfiguration()
        //         .setDatabaseSchemaUpdate(DmnEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
        //         .setJdbcUrl("jdbc:h2:mem:flowable;DB_CLOSE_DELAY=-1")
        //         .setJdbcUsername("sa")
        //         .setJdbcPassword("")
        //         .setJdbcDriver("org.h2.Driver")
        //         .buildDmnEngine();

        DmnEngine dmnEngine = DmnEngineConfiguration.createStandaloneInMemDmnEngineConfiguration()
                .setDatabaseSchemaUpdate(DmnEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
                .buildDmnEngine();

        DmnRuleService dmnRuleService = dmnEngine.getDmnRuleService();
        DmnRepositoryService dmnRepositoryService = dmnEngine.getDmnRepositoryService();
        DmnManagementService dmnManagementService = dmnEngine.getDmnManagementService();

        DmnDeployment deployment = dmnRepositoryService.createDeployment()
                .addClasspathResource("intro.dmn.xml")
                .deploy();

        List<DmnDeployment> deployments = dmnRepositoryService.createDeploymentQuery().decisionTableKey("intro").list();
        System.out.println(deployments);

        List<Map<String, Object>> results = dmnRuleService.createExecuteDecisionBuilder().decisionKey("intro").variable("name", "Flowable").execute();
        System.out.println(results);
    }
}
