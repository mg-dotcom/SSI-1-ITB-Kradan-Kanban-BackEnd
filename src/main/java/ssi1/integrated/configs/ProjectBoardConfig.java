package ssi1.integrated.configs;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "ssi1.integrated.project_board",
        entityManagerFactoryRef = "projectManagementEntityManager",
        transactionManagerRef = "projectManagementTransactionManager"
)

public class ProjectBoardConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.pm")
    public DataSourceProperties projectManagementDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.pm.configuration")
    public DataSource projectManagementDataSource() {
        return projectManagementDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "projectManagementEntityManager")
    public LocalContainerEntityManagerFactoryBean projectManagementEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(projectManagementDataSource())
                .packages("ssi1.integrated.project_board")
                .build();
    }

    @Primary
    @Bean(name = "projectManagementTransactionManager")
    public PlatformTransactionManager projectManagementTransactionManager(
            final @Qualifier("projectManagementEntityManager") LocalContainerEntityManagerFactoryBean projectManagementEntityManager) {
        return new JpaTransactionManager(
                Objects.requireNonNull(
                        projectManagementEntityManager.getObject()
                )
        );
    }
}
