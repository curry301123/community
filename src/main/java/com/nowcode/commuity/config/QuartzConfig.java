package com.nowcode.commuity.config;

import com.nowcode.commuity.quartz.AlphaJob;
import com.nowcode.commuity.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

// 配置 --> 存在数据库中 -->调用数据库中的文件
@Configuration
public class QuartzConfig {

    //FactoryBean可简化Bean的实例化过程
    //1.通过FactoryBean封装Bean的实例化过程
    //2.将FactoryBean装配到Spring容器中
    //3.将FactoryBean注入给其它Bean
    //4.该Bean得到的是FactoryBean所管理的对象实例

    //配置JobDetail
    @Bean
    public JobDetailFactoryBean postScoreJobDetail(){
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("postScoreRefreshJob");
        factoryBean.setGroup("communityJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }
    //配置Trigger
    @Bean
    public SimpleTriggerFactoryBean postScoreTriggerFactoryBean(JobDetail postScoreJobDetail){
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreJobDetail);
        factoryBean.setName("postScoreRefreshTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        factoryBean.setRepeatInterval(1000*60*5 );
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }

    //@Bean
    public JobDetailFactoryBean alphaJobDetail(){
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(AlphaJob.class);
        factoryBean.setName("alphaJob");
        factoryBean.setGroup("alphaJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }
    //配置Trigger
    //@Bean
    public SimpleTriggerFactoryBean alphaTriggerFactoryBean(JobDetail alphaJobDetail){
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(alphaJobDetail);
        factoryBean.setName("alphaJobTrigger");
        factoryBean.setGroup("alphaJobGroup");
        factoryBean.setRepeatInterval(3000 );
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }
}
