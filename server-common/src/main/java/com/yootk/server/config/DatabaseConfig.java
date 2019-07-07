package com.yootk.server.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.repository.NoRepositoryBean;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@PropertySource("classpath:database.properties")
public class DatabaseConfig {
    @Value("${db.druid.driverClassName}")
    private String driverClassName ;
    @Value("${db.druid.url}")
    private String url ;
    @Value("${db.druid.username}")
    private String username ;
    @Value("${db.druid.password}")
    private String password ;
    @Value("${db.druid.maxActive}")
    private int maxActive ;
    @Value("${db.druid.minIdle}")
    private int minIdle ;
    @Value("${db.druid.initialSize}")
    private int initialSize ;
    @Value("${db.druid.maxWait}")
    private long maxWait ;
    @Value("${db.druid.timeBetweenEvictionRunsMillis}")
    private long timeBetweenEvictionRunsMillis ;
    @Value("${db.druid.minEvictableIdleTimeMillis}")
    private long minEvictableIdleTimeMillis ;
    @Value("${db.druid.validationQuery}")
    private String validationQuery ;
    @Value("${db.druid.testWhileIdle}")
    private boolean testWhileIdle ;
    @Value("${db.druid.testOnBorrow}")
    private boolean testOnBorrow ;
    @Value("${db.druid.testOnReturn}")
    private boolean testOnReturn ;

    @Value("${db.druid.poolPreparedStatements}")
    private boolean poolPreparedStatements ;
    @Value("${db.druid.maxPoolPreparedStatementPerConnectionSize}")
    private int maxPoolPreparedStatementPerConnectionSize ;
    @Value("${db.druid.filters}")
    private String filters ;
    @Bean("dataSource")
    public DataSource getDruidDataSource() {
        DruidDataSource dataSource = new DruidDataSource() ;
        dataSource.setDriverClassName(this.driverClassName);
        dataSource.setUrl(this.url);
        dataSource.setUsername(this.username);
        dataSource.setPassword(this.password);
        dataSource.setMaxActive(this.maxActive);
        dataSource.setMinIdle(this.minIdle);
        dataSource.setInitialSize(this.initialSize);
        dataSource.setMaxWait(this.maxWait);
        dataSource.setTimeBetweenEvictionRunsMillis(this.timeBetweenEvictionRunsMillis);
        dataSource.setMinEvictableIdleTimeMillis(this.minEvictableIdleTimeMillis);
        dataSource.setValidationQuery(this.validationQuery);
        dataSource.setTestWhileIdle(this.testWhileIdle);
        dataSource.setTestOnBorrow(this.testOnBorrow);
        dataSource.setTestOnReturn(this.testOnReturn);
        dataSource.setPoolPreparedStatements(this.poolPreparedStatements);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(this.maxPoolPreparedStatementPerConnectionSize);
        try {
            dataSource.setFilters(this.filters);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataSource ;
    }

}
