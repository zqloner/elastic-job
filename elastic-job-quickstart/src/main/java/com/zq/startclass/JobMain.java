package com.zq.startclass;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.zq.job.FileBackupJob;
import com.zq.pojo.FileCustom;

//启动类
public class JobMain {
    //zookeeper端口
    private static final int ZOOKEEPER_PORT = 2181;
    //zookeeper连接字符串  192.168.29.100:2181
    private static final String ZOOKEEPER_CONNECTION_STRING = "192.168.29.100:2181";
    //定时任务命名空间
    private static final String JOB_NAMESPACE = "elastic-job-example-java";

    //启动任务
    public static void main(String[] args){
        //生成测试文件
        generateTestFiles();
        //配置zookeeper
        CoordinatorRegistryCenter registryCenter =setUpRegistryCenter();
        //启动任务
        startJob(registryCenter);
    }

    //生成测试文件
    private static void generateTestFiles(){
        for(int i = 1;i<11;i++){
//            text,image丶radio、vedio
            FileBackupJob.files.add(new FileCustom(String.valueOf(i+10),"文件"+(i+10),"text","content"+(i+10)));
            FileBackupJob.files.add(new FileCustom(String.valueOf(i+20),"文件"+(i+20),"image","content"+(i+20)));
            FileBackupJob.files.add(new FileCustom(String.valueOf(i+30),"文件"+(i+30),"radio","content"+(i+30)));
            FileBackupJob.files.add(new FileCustom(String.valueOf(i+40),"文件"+(i+40),"vedio","content"+(i+40)));
        }
    }

    //配置注册中心
    private static CoordinatorRegistryCenter setUpRegistryCenter(){
        //注册中心配置
        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(ZOOKEEPER_CONNECTION_STRING, JOB_NAMESPACE);
        //减小zk的超时时间
        zookeeperConfiguration.setSessionTimeoutMilliseconds(100);//毫秒
        //创建注册中心
        CoordinatorRegistryCenter registryCenter = new ZookeeperRegistryCenter(zookeeperConfiguration);
        registryCenter.init();
        return registryCenter;
    }

    //配置并启动任务
    private static void startJob(CoordinatorRegistryCenter registryCenter){
        //创建JobCoreConfiguration
        JobCoreConfiguration jobCoreConfiguration = JobCoreConfiguration.newBuilder("file-job",
                "0/3 * * * * ?",1).build();
        //创建SimpleJobConfiguration
        SimpleJobConfiguration simpleJobConfiguration = new SimpleJobConfiguration(jobCoreConfiguration, FileBackupJob.class.getCanonicalName());
        //启动任务
        new JobScheduler(registryCenter, LiteJobConfiguration.newBuilder(simpleJobConfiguration).overwrite(true).build()).init();
    }
}
