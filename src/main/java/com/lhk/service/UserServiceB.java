package com.lhk.service;

import com.ly.train.flower.common.core.service.Service;
import com.ly.train.flower.common.core.service.ServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserServiceB implements Service<User, User> {
  static final Logger logger = LoggerFactory.getLogger(UserServiceB.class);
  @Override
  public User process(User message, ServiceContext context) throws Throwable {
    message.setDesc(message.getDesc() + " --> " + getClass().getSimpleName());
    message.setAge(message.getAge() + 1);
    logger.info("结束处理消息, message : {}", message);
    return message;
  }

}
