package com.feidee.money.server.handler;

import com.feidee.money.server.core.RequestMapping;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ObjectUtils;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;

/**
 * Created by tingsky on 16/5/16.
 */
@ChannelHandler.Sharable
public class GlobleHandler extends SimpleChannelInboundHandler<FullHttpRequest> implements BeanFactoryPostProcessor, ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(GlobleHandler.class);

    @Value("#{cfg['uri.prefix']}")
    private String uriPrefix;

    private ApplicationContext context;

    private ConcurrentMap<String, String> urlHandlerMap = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (!request.getDecoderResult().isSuccess()) {
            BaseHandler.renderError(ctx, BAD_REQUEST);
            return;
        }
        final String uri = request.getUri();
        log.debug("uri is:" + uri);

        String key = uri;
        if (uri.indexOf("?") != -1) {
            key = StringUtils.substring(uri, 0, uri.indexOf("?"));
        }
        log.debug("uri key is:" + key);

        String beanName = urlHandlerMap.get(key);
        if(beanName != null){
            Handler handler = (Handler) context.getBean(beanName);
            handler.handleRequest(ctx, request);
        }else {
            BaseHandler.renderError(ctx, FORBIDDEN);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("服务器抛出未被捕获的异常", cause);
        if (ctx.channel().isActive()) {
            BaseHandler.renderError(ctx, INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    /**
     * 扫描所有的注册bean,并注册映射关系
     * @param beanFactory
     * @throws BeansException
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] beanNames = context.getBeanNamesForType(Object.class);

        for (String beanName : beanNames) {
            registerHandler(beanName);
        }
    }

    /**
     * 通过RequestMapping注解的值,在url处理映射表中注册映射关系
     * @param beanName
     */
    private void registerHandler (String beanName){
        RequestMapping mapping = context.findAnnotationOnBean(beanName, RequestMapping.class);
        if(mapping != null){
            String[] urls = mapping.value();
            if(!ObjectUtils.isEmpty(urls)){
                String uriPrefix = getUriPrefix();
                for(String url : urls){
                    String key = uriPrefix + url;
                    if(urlHandlerMap.containsKey(url)){
                        throw new RuntimeException("该url已被注册:" + key);
                    }
                    log.info("Mapping url [{}] to bean : {}", key , beanName);
                    urlHandlerMap.put(key, beanName);
                }
            }
        }
    }

    private String getUriPrefix() {
        String prefix = ((Properties) context.getBean("cfg")).getProperty("uri.prefix");
        return prefix == null ? "" : prefix;
    }


}
