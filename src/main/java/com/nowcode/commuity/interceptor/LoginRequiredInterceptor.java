package com.nowcode.commuity.interceptor;

import com.nowcode.commuity.annoation.LoginRequired;
import com.nowcode.commuity.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder holder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){ //要拦截的对象是方法对象
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod(); //通过反射获取该方法对象
            LoginRequired annotation = method.getAnnotation(LoginRequired.class);//获取方法上的自定义注解
            if(annotation != null && holder.getUser() ==null){//逻辑判断

                response.sendRedirect(request.getContextPath()+"/login");
                return false;
            }

        }

        return true;

    }
}
