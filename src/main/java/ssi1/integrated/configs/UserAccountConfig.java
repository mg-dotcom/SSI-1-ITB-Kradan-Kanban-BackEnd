package ssi1.integrated.configs;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "ssi1.integrated.user_account",
        entityManagerFactoryRef = "userAccountEntityManager",
        transactionManagerRef = "userAccountTransactionManager"
)
public class UserAccountConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.ua")
    public DataSourceProperties userAccountDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.ua.configuration")
    public DataSource userAccountDataSource() {
        return userAccountDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(false);
        return new EntityManagerFactoryBuilder(vendorAdapter, new HashMap<>(), null);
    }

    @Bean(name = "userAccountEntityManager")
    public LocalContainerEntityManagerFactoryBean userAccountEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(userAccountDataSource())
                .packages("ssi1.integrated.user_account")
                .build();
    }

    @Bean(name = "userAccountTransactionManager")
    public PlatformTransactionManager userAccountTransactionManager(
            final @Qualifier("userAccountEntityManager")
            LocalContainerEntityManagerFactoryBean userAccountEntityManager) {
        return new JpaTransactionManager(
                Objects.requireNonNull(
                        userAccountEntityManager.getObject()
                )
        );
    }


}
