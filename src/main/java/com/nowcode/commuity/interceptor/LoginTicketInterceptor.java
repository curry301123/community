package com.nowcode.commuity.interceptor;

import com.nowcode.commuity.domain.LoginTicket;
import com.nowcode.commuity.domain.User;
import com.nowcode.commuity.service.UserService;
import com.nowcode.commuity.util.CookieUtil;
import com.nowcode.commuity.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder holder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String cookie = CookieUtil.getValue(request, "code");
        if(cookie != null){
            LoginTicket loginTicket = userService.findByTicket(cookie);
            if(loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                int userId = loginTicket.getUserId();
                User user = userService.findById(userId);
                holder.setUser(user);
                //构建用户认证的结果，并存入securityContext,以便于security进行授权
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user,user.getPassword(), userService.getAuthorities(user.getId())
                );
                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = holder.getUser();
        if(user != null && modelAndView != null){
            modelAndView.addObject("loginUser",user);
        }
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        holder.clear();
        //SecurityContextHolder.clearContext();
    }
}
