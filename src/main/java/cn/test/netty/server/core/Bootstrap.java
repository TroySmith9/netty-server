package cn.test.netty.server.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by tingsky on 16/5/2.
 */
public class Bootstrap {
    private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);

    public static void main(String[] args) {

        log.info("###################################");
        log.info("start spring container....");

        new ClassPathXmlApplicationContext("spring-context.xml").start();
        log.info("end spring container...");
        log.info("###################################");
    }


}
