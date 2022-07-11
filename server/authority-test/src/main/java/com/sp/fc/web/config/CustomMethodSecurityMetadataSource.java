package com.sp.fc.web.config;

import com.sp.fc.web.controller.PaperController;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.method.MethodSecurityMetadataSource;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

public class CustomMethodSecurityMetadataSource implements MethodSecurityMetadataSource {
    @Override
    public Collection<ConfigAttribute> getAttributes(Method method, Class<?> targetClass) {
        //타켓으로 할 클래스와 그 함수를 지정한다.
        if(method.getName().equals("getPaperByPrimary") && targetClass == PaperController.class){
            return List.of(new SecurityConfig("SCHOOL_PRIMARY"));
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
}
