package com.zq.config;

import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

@Configuration
public class ElasticJobRegistryCenterConfig {

    //zookeeper端口
    private static final int ZOOKEEPER_PORT=2181;

    //zookeeper连接字符串192.168.29.100
    private static final String ZOOKEEPER_CONNECTION_STRING="192.168.29.100:2181";

    //定时任务命名空间
    private static final String JOB_NAMESPACE = "elastic-job-spring-boot";


    @Bean(initMethod="init")
    private static CoordinatorRegistryCenter setUpRegisryCenter(){
        //zk的配置
        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(ZOOKEEPER_CONNECTION_STRING, JOB_NAMESPACE);

        //减少zk超时时间
        zookeeperConfiguration.setSessionTimeoutMilliseconds(100);

        //创建注册中心
        CoordinatorRegistryCenter zookeeperRegistryCenter = new ZookeeperRegistryCenter(zookeeperConfiguration);
        return zookeeperRegistryCenter;
    }

}
