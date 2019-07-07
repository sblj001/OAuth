package com.yootk.oauth.config;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@PropertySource("classpath:config/redis.properties")
public class ShiroTokenRedisConfig {
    @Bean("shiroTokenRedisConfiguration")
    public RedisStandaloneConfiguration getRedisConfiguration(
            @Value("${shiro.token.redis.host}") String hostName ,
            @Value("${shiro.token.redis.port}") int port,
            @Value("${shiro.token.redis.auth}") String password,
            @Value("${shiro.token.redis.database}") int database
    ) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration() ;
        configuration.setHostName(hostName); // 设置Redis主机名称
        configuration.setPort(port); // 设置Redis的访问端口
        configuration.setPassword(RedisPassword.of(password)); // 设置密码
        configuration.setDatabase(database); // 设置数据库索引
        return configuration ;
    }
    @Bean("shiroTokenObjectPoolConfig")
    public GenericObjectPoolConfig getObjectPoolConfig(
            @Value("${shiro.token.redis.pool.maxTotal}") int maxTotal ,
            @Value("${shiro.token.redis.pool.maxIdle}") int maxIdle ,
            @Value("${shiro.token.redis.pool.minIdle}") int minIdle ,
            @Value("${shiro.token.redis.pool.testOnBorrow}") boolean testOnBorrow
    ) {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig() ;
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setTestOnBorrow(testOnBorrow);
        return poolConfig ;
    }
    @Bean("shiroTokenClientConfiguration")
    public LettuceClientConfiguration getLettuceClientConfiguration(
            @Autowired GenericObjectPoolConfig shiroTokenObjectPoolConfig
    ) { // 创建Lettuce组件的连接池客户端配置对象
        return LettucePoolingClientConfiguration.builder().poolConfig(shiroTokenObjectPoolConfig).build() ;
    }
    @Bean("shiroTokenRedisConnectionFactory")
    public RedisConnectionFactory getConnectionFactory(
            @Autowired RedisStandaloneConfiguration shiroTokenRedisConfiguration ,
            @Autowired LettuceClientConfiguration shiroTokenClientConfiguration
    ) {
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(shiroTokenRedisConfiguration,
                shiroTokenClientConfiguration) ;
        return connectionFactory ;
    }
    @Bean("shiroTokenRedisTemplate")
    public RedisTemplate getRedisTempalate(
            @Autowired RedisConnectionFactory shiroTokenRedisConnectionFactory
    ) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>() ;
        redisTemplate.setConnectionFactory(shiroTokenRedisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer()); // 数据的key通过字符串存储
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer()); // 保存的value为对象
        redisTemplate.setHashKeySerializer(new StringRedisSerializer()); // 数据的key通过字符串存储
        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer()); // 保存的value为对象
        return redisTemplate ;
    }
}
