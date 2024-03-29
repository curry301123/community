package com.nowcode.commuity.controller;

import com.google.code.kaptcha.Producer;
import com.nowcode.commuity.domain.User;
import com.nowcode.commuity.service.UserService;
import com.nowcode.commuity.util.CommunityUtil;
import com.nowcode.commuity.util.Constant;
import com.nowcode.commuity.util.RedisLikeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController implements Constant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate template;


    @Value("${server.servlet.context-path}")
    private String path;


    @Autowired
    private Producer kaptchaProducer;

    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String getRegisterPage(){

        return "/site/register";
    }

    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String,Object> map = userService.register(user);
        if(map == null || map.isEmpty()){
            model.addAttribute("msg","注册成功，已发送激活邮件，请尽快激活");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }

    @RequestMapping(path = "/activation/{id}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("id") int id,@PathVariable("code") String code){
        int activation = userService.activation(id, code);
        if(activation == ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功，您账号已经可以正常使用");
            model.addAttribute("target","/login");
        }else if (activation == ACTIVATION_FAILED){
            model.addAttribute("msg","无效操作，该账号已经激活过！");
            model.addAttribute("target","/index");
        }else {
            model.addAttribute("msg","激活失败，激活码错误");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }

    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String getLogin(){
        return "/site/login";
    }


    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response/*, HttpSession session*/){
        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
       /* //将验证码存入session
        session.setAttribute("kaptcha",text);*/

        //生成验证码归属
        String owner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner",owner);
        cookie.setMaxAge(60);
        cookie.setPath(path);
        response.addCookie(cookie);
        //将验证码存入redis
        String kaptchaKey = RedisLikeUtil.getKaptchaKey(owner);
        template.opsForValue().set(kaptchaKey,text,60, TimeUnit.SECONDS);

        //将图片输出给浏览器
        response.setContentType("image/png");

        try {
            ServletOutputStream outputStream = response.getOutputStream();
            ImageIO.write(image,"png",outputStream);
        } catch (IOException e) {
            logger.error("响应验证码失败" + e.getMessage());
        }
    }

    @RequestMapping(path = "/login",method = RequestMethod.POST)
    public String Login(String username, String password, String code,
                      boolean rememberme, Model model,/*HttpSession session,*/HttpServletResponse response,
                        @CookieValue(value = "kaptchaOwner",required = false) String owner){
        //String kaptcha = (String) session.getAttribute("kaptcha");
        String kaptcha = null;
        if(StringUtils.isNotBlank(owner)){
            String redisKey = RedisLikeUtil.getKaptchaKey(owner);
            kaptcha = (String) template.opsForValue().get(redisKey);
        }
        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码错误！");
            return "/site/login";
        }

        long expired = rememberme ? REMEMBER_EXPIRED: DEFAULT_EXPIRED;
        Map<String,Object> map = userService.userLogin(username,password,expired);
        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("code",map.get("ticket").toString());
            cookie.setPath(path);
            cookie.setMaxAge((int) expired);
            response.addCookie(cookie);
            return "redirect:/index";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("activeMsg",map.get("activeMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("code") String ticket){
        userService.logout(ticket);
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }
}
