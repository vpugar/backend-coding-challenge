package com.engagetech.expenses;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.MySQLContainer;

import java.time.Duration;

public class ExpensesApplicationIntegrationTestHelper {

    private static MySQLContainer mysqlSQLContainer =
            (MySQLContainer) new MySQLContainer("mysql:5.7.22")
                    .withDatabaseName("test")
                    .withUsername("test")
                    .withPassword("test")
                    .withStartupTimeout(Duration.ofSeconds(600L));

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + mysqlSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + mysqlSQLContainer.getUsername(),
                    "spring.datasource.password=" + mysqlSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    public static MySQLContainer getMysqlSQLContainer() {
        return mysqlSQLContainer;
    }

    private ExpensesApplicationIntegrationTestHelper() {
        throw new UnsupportedOperationException("Cannot use constructor");
    }
}
