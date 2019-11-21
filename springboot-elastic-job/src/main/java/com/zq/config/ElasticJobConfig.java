package com.zq.config;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class ElasticJobConfig {
    @Resource
    private SimpleJob fileBackupJob;

    @Resource
    private CoordinatorRegistryCenter registryCenter;

    //创建LiteJobConfiguration，因为下面方法需要这个参数
    private LiteJobConfiguration createJobConfiguration(final Class<? extends SimpleJob> jobClass,
                                                        final String cron,
                                                        final int shardingTotalCount,
                                                        final String shardingItemParameters){
        JobCoreConfiguration.Builder builder = JobCoreConfiguration.newBuilder(jobClass.getName(), cron, shardingTotalCount);
        //shardingTotalCount作业分片数量
        //作业分片参数shardingItemParameters不为空,就将这个分片参数设置进去
        if(!StringUtils.isBlank(shardingItemParameters)){
            builder.shardingItemParameters(shardingItemParameters);
        }
        JobCoreConfiguration jobCoreConfiguration = builder.build();
        //创建SimpleJobCoreConfiguration
        SimpleJobConfiguration simpleJobConfiguration = new SimpleJobConfiguration(jobCoreConfiguration, jobClass.getCanonicalName());

        //创建LiteJobConfiguration
        LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration.newBuilder(simpleJobConfiguration).overwrite(true).build();

        return liteJobConfiguration;
    }


    @Bean(initMethod = "init")
    public SpringJobScheduler initSimpleElasticJob(){
        //shardingTotalCount作业分片数量
        //作业分片参数shardingItemParameters不为空,就将这个分片参数设置进去(主要是为了防止分片之后数据重复)
      //例如:  createJobConfiguration(fileBackupJob.getClass(), "0/3 * * * * ?", 4, "0=text,1=radio,2=image,3=vedio");
        //这个分页参数在job的excute方法中可以获取,通过这个参数去数据库查询数据,就能拿到不同的数据了(怎么拿的细节去FileBackupJob的excute方法中查看)

        LiteJobConfiguration liteJobConfiguration = createJobConfiguration(fileBackupJob.getClass(), "0/3 * * * * ?", 3, null);
        //public SpringJobScheduler(ElasticJob elasticJob, CoordinatorRegistryCenter regCenter, LiteJobConfiguration jobConfig, ElasticJobListener... elasticJobListeners)
      return   new SpringJobScheduler(fileBackupJob, registryCenter, liteJobConfiguration);
    }
}
