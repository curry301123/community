package com.nowcode.commuity.interceptor;

import com.nowcode.commuity.domain.User;
import com.nowcode.commuity.service.MessageService;
import com.nowcode.commuity.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MessageInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder holder;

    @Autowired
    private MessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = holder.getUser();
        if(user != null && modelAndView != null){
            int letterUnread = messageService.selectUnreadLetters(user.getId(), null);
            int noticeUnread = messageService.findUnread(user.getId(),null);
            modelAndView.addObject("allUnreadCount",letterUnread+noticeUnread);
        }
    }
}
