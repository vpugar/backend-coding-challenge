package com.engagetech.expenses;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.MySQLContainer;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = ExpensesApplicationIntegrationTestHelper.Initializer.class)
public class ExpensesApplicationTest {

    private static final Logger logger = LoggerFactory.getLogger(ExpensesApplicationTest.class);

    @ClassRule
    public static MySQLContainer mysqlSQLContainer = ExpensesApplicationIntegrationTestHelper.getMysqlSQLContainer();

    @Test
    public void contextLoads() {
        logger.info("Context loaded successfully");
    }
}
