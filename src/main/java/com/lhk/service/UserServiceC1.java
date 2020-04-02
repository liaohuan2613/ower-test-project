package com.lhk.service;

import com.ly.train.flower.common.core.service.Service;
import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.core.akka.router.FlowRouter;
import com.ly.train.flower.core.service.container.FlowerFactory;
import com.ly.train.flower.core.service.container.ServiceFactory;
import com.ly.train.flower.core.service.container.ServiceFlow;
import com.ly.train.flower.core.service.container.simple.SimpleFlowerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeoutException;

public class UserServiceC1 implements Service<User, User> {
  static final Logger logger = LoggerFactory.getLogger(UserServiceC1.class);
  @Override
  public User process(User message, ServiceContext context) throws Throwable {
    message.setDesc(message.getDesc() + " --> " + getClass().getSimpleName());
    message.setAge(message.getAge() + 1);
    logger.info("结束处理消息, message : {}", message);
    return message;
  }


    public static void main(String[] args) throws TimeoutException {
        FlowerFactory flowerFactory = new SimpleFlowerFactory();
        ServiceFactory serviceFactory = flowerFactory.getServiceFactory();
        serviceFactory.registerService(UserServiceA.class.getSimpleName(), UserServiceA.class);
        serviceFactory.registerService(UserServiceB.class.getSimpleName(), UserServiceB.class);
        serviceFactory.registerService(UserServiceC1.class.getSimpleName(), UserServiceC1.class);

        final String flowName = "flower_test";
        ServiceFlow serviceFlow = serviceFactory.getOrCreateServiceFlow(flowName);
        serviceFlow.buildFlow(UserServiceA.class, UserServiceB.class);
        serviceFlow.buildFlow(UserServiceB.class, UserServiceC1.class);
        serviceFlow.build();

        final FlowRouter flowRouter = flowerFactory.buildFlowRouter(flowName, 16);

        User user = new User();
        user.setName("响应式编程 ");
        user.setAge(2);

        Object o = flowRouter.syncCallService(user);
        System.out.println("响应结果： " + o);

        flowRouter.asyncCallService(user);
    }
}
