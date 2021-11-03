package com.nowcode.commuity.service;

import com.nowcode.commuity.domain.User;
import com.nowcode.commuity.mapper.UserMapper;
import com.nowcode.commuity.util.CommunityUtil;
import com.nowcode.commuity.util.Constant;
import com.nowcode.commuity.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements Constant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine engine;

    @Value("${commuity.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;


    public Map<String,Object> register(User user){
        Map<String,Object> map = new HashMap<>();
        //空置处理
        if(user == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }
        //账号验证
        if(userMapper.findByName(user.getUsername()) !=null){
            map.put("usernameMsg","该用户名已注册");
            return map;
        }
        //邮箱验证
        if(userMapper.findByEmail(user.getEmail()) !=null){
            map.put("emailMsg","该邮箱已注册");
            return map;
        }

        //注册用户

        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //发送邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        String url = domain + contextPath +"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);
        String content = engine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);

        return map;
    }

        /*
        * 激活处理
        *
        * */
    public int activation(int id,String code){
         User user = userMapper.findById(id);
         if(user.getStatus() == 1){
             return ACTIVATION_REPEAT;
         }else if(user.getActivationCode().equals(code)){
             userMapper.updateStatus(id,1);
             return ACTIVATION_SUCCESS;
         }else {
             return ACTIVATION_FAILED;
         }
    }




    public User findById(int userId){
        return userMapper.findById(userId);
    }


}
