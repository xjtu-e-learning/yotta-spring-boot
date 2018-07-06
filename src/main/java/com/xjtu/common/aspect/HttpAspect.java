package com.xjtu.common.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
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

    @Pointcut(value = "execution(* com.xjtu.*.controller.*.*(..))")
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

        //remote address
        String remoteAddress = request.getRemoteAddr();
        logger.info("remote address:{}", remoteAddress);

        //class method
        String classMethod = joinPoint.getSignature().getDeclaringTypeName()
                + "." + joinPoint.getSignature().getName();
        logger.info("class method:{}", classMethod);
        //parameters
        String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        String args = "";
        Object[] objects = joinPoint.getArgs();
        for (int i = 0; i < objects.length; i++) {
            args += paramNames[i] + ":" + objects[i].toString() + " ";
        }
        logger.info("parameters:{}", args);
    }

    @AfterReturning(returning = "object", value = "log()")
    private void logAfterReturn(Object object) {
        logger.info("response:{}", object.toString());
    }
}
