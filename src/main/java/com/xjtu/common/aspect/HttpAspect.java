package com.xjtu.common.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 访问日志记录
 *
 * @author yangkuan
 * @date 2018/07/05 13:33
 */
@Aspect
@Component
public class HttpAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Pointcut(value = "execution(* com.xjtu.facet.controller.FacetController.*(..))")
    private void log() {

    }

    @Before(value = "log()")
    private void logBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //url
        String url = request.getRequestURL().toString();
        logger.info("url:{}", url);

        //method(get/post/head)
        String method = request.getMethod();
        logger.info("method:{}", method);

        //ip
        String ip = request.getRemoteAddr();
        logger.info("ip:{}", ip);

        //class method
        String classMethod = joinPoint.getSignature().getDeclaringTypeName()
                + "." + joinPoint.getSignature().getName();
        logger.info("class method:{}", classMethod);
        //arguments
        logger.info("arguments:{}", joinPoint.getArgs());
    }


}
