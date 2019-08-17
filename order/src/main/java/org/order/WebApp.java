package org.order;

import ch.qos.logback.ext.spring.web.LogbackConfigListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

/**
 * @description: WebApp 启动配置
 * @create: 2019-08-12 12:20
 * @author: zxw
 **/

public class WebApp implements WebApplicationInitializer {

    final static Logger logger = LoggerFactory.getLogger(WebApp.class);

    @Override
    public void onStartup(ServletContext container) {
        container.setInitParameter("debug","true");

        logger.info("invoke by SpringServletContainerInitializer...");


        // 创建RootApplicationContext
        AnnotationConfigWebApplicationContext rootContext =
                new AnnotationConfigWebApplicationContext();
        // 配置
        rootContext.register(AppConfig.class);


        container.addListener(new LogbackConfigListener());
        // 注册ServletContext监听器
        container.addListener(new ContextLoaderListener(rootContext));


        // 创建WebApplicationContext
        AnnotationConfigWebApplicationContext dispatcherContext =
                new AnnotationConfigWebApplicationContext();

        // 配置
        dispatcherContext.register(MvcConfig.class);

        // DispatcherServlet添加上下文并映射
        ServletRegistration.Dynamic dispatcher =
                container.addServlet("dispatcher", new DispatcherServlet(dispatcherContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.setAsyncSupported(true); //异步Controller支持
        dispatcher.addMapping("/");
    }

}