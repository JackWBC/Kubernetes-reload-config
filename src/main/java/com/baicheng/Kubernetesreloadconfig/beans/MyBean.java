package com.baicheng.Kubernetesreloadconfig.beans;

import com.baicheng.Kubernetesreloadconfig.config.DummyConfig;
import com.baicheng.Kubernetesreloadconfig.config.MyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author baicheng
 * @description
 * @create 2019-08-28 17:11
 */
@Component
public class MyBean {

    @Autowired
    private MyConfig myConfig;

    @Autowired
    private DummyConfig dummyConfig;

    @Scheduled(fixedDelay = 5000)
    public void hello(){
        System.out.println("the first message is: " + this.myConfig.getMessage());
        System.out.println("the other message is: " + this.dummyConfig.getMessage());
    }
}
