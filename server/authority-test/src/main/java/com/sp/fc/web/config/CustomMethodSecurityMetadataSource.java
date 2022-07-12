package com.sp.fc.web.config;

import com.sp.fc.web.annotation.CustomSecurityAnnotation;
import com.sp.fc.web.controller.PaperController;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.log.LogMessage;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

public class CustomMethodSecurityMetadataSource implements MethodSecurityMetadataSource {
    @Override
    public Collection<ConfigAttribute> getAttributes(Method method, Class<?> targetClass) {
        //커스텀 어노테이션 찾기
        CustomSecurityAnnotation annotation = findAnnotation(method, targetClass, CustomSecurityAnnotation.class);
        //타켓으로 할 클래스와 그 함수를 지정한다.
        if(annotation != null){
            return List.of(new SecurityConfig(annotation.value()));
        }
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        ///MethodInvocation 에 대해서 동작한다.
        return MethodInvocation.class.isAssignableFrom(clazz);
    }

    //PrePostAnnotationSecurityMetadataSource 함수를 복사해 옴
    private <A extends Annotation> A findAnnotation(Method method, Class<?> targetClass, Class<A> annotationClass) {
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        A annotation = AnnotationUtils.findAnnotation(specificMethod, annotationClass);
        if (annotation != null) {
            return annotation;
        }
        return annotation;
    }
}
