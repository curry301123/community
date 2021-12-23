package com.nowcode.commuity.controller;

import com.nowcode.commuity.annoation.LoginRequired;
import com.nowcode.commuity.domain.User;
import com.nowcode.commuity.service.FolloweService;
import com.nowcode.commuity.service.LikeService;
import com.nowcode.commuity.service.UserService;
import com.nowcode.commuity.util.CommunityUtil;
import com.nowcode.commuity.util.Constant;
import com.nowcode.commuity.util.CookieUtil;
import com.nowcode.commuity.util.HostHolder;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
@RequestMapping(path = "/user")
public class UserController implements Constant {
    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder holder;

    @Autowired
    private FolloweService followeService;

    @Value("${commuity.path.domain}")
    private String domain;

    @Value("${commuity.upload}")
    private String upload;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.header.name}")
    private String headerBucketName;

    @Value("${qiniu.bucket.header.url}")
    private String headerBucketUrl;

    @Autowired
    private LikeService likeService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPath(Model model){
        //生成上传文件名称
        String fileName = CommunityUtil.generateUUID();
        //设置响应信息
        StringMap policy = new StringMap();
        policy.put("returnBody",CommunityUtil.getJSONSting(0));
        //生成上传凭证
        Auth auth = Auth.create(accessKey,secretKey);
        String uploadToken = auth.uploadToken(headerBucketName,fileName,3600,policy);
        model.addAttribute("uploadToken",uploadToken);
        model.addAttribute("fileName",fileName);
        return "/site/setting";
    }

    //更新头像路径
    @RequestMapping(path = "/header/url",method = RequestMethod.POST)
    @ResponseBody
    public String updateHeaderUrl(String fileName){
        if(StringUtils.isBlank(fileName)){
            return CommunityUtil.getJSONSting(1,"文件名不能为空");
        }
        String url = headerBucketUrl+"/"+fileName;
        userService.updateHeadUrl(holder.getUser().getId(),url);
        return CommunityUtil.getJSONSting(0);
    }

    //废弃
    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImg, Model model){
        if(headerImg == null){
            model.addAttribute("error","图片上传为空！");
            return "/site/setting";
        }
        String filename = headerImg.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));

        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式不正确！");
            return "/site/setting";
        }
        filename = CommunityUtil.generateUUID()+suffix;

        //确定文件存放路径
        File dest = new File(upload+"/"+filename);
        try {
            //存储文件
            headerImg.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件错误："+e.getMessage());
            throw new RuntimeException("上传文件失败",e);
        }

        //更新用户头像(web访问路径)
        //http://localhost:8080/community/user/header/xxx.png
        User user = holder.getUser();
        String headerUrl = domain+contextPath+"/user/header/"+filename;
        userService.updateHeadUrl(user.getId(),headerUrl);

        return "redirect:/index";
    }
    //废弃
    @RequestMapping(path ="/header/{filename}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response){
        filename = upload+"/"+filename;
        String suffix = filename.substring(filename.lastIndexOf(".")+1);
        //响应图片
        response.setContentType("image/"+suffix);

        try (
                OutputStream outputStream = response.getOutputStream();
                FileInputStream fis = new FileInputStream(filename);
                ){
             byte[] buffer = new byte[1024];
             int b =0;
             while ((b = fis.read(buffer))!=-1){
                 outputStream.write(buffer,0,b);
             }
        } catch (IOException e) {
            logger.error("读取头像失败"+e.getMessage());
        }
    }

    @RequestMapping(path = "/resetPass",method = RequestMethod.POST)
    public String resetPass(String password, String newPass, String confirmPass, Model model, HttpServletRequest request){
        if(StringUtils.isBlank(password)){
            model.addAttribute("passError","密码不能为空");
            return "/site/setting";
        }
        if(StringUtils.isBlank(newPass)){
            model.addAttribute("passErrorTwo","密码不能为空");
            return "/site/setting";
        }
        if (StringUtils.isBlank(confirmPass)){
            model.addAttribute("passErrorTwo","密码不能为空");
            return "/site/setting";
        }
        if(!confirmPass.equals(newPass)){
            model.addAttribute("passErrorTwo","密码不一致！");
            return "/site/setting";
        }
        String cookie = CookieUtil.getValue(request,"code");
        User user = holder.getUser();
        Map<String, Object> map = userService.updatePassword(user, password, newPass,cookie);
        if(map.containsKey("fixMsg")){
            model.addAttribute("passError",map.get("fixMsg"));
            return "/site/setting";
        }
            return "redirect:/login";
    }

    @RequestMapping(value = "/profile/{userId}",method = RequestMethod.GET)
    public String profile(@PathVariable("userId") int userId,Model model){
        User user = userService.findById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user",user);
        int likeCount = likeService.userLikeNums(userId);
        model.addAttribute("likeCount",likeCount);

        long followeeNum = followeService.followeeNum(userId,ENTITY_TYPE_USER);
        long followerNum = followeService.followerNum(ENTITY_TYPE_USER,userId);
        boolean followStatus = false;
        if(holder.getUser() !=null){
            followStatus = followeService.followStatus(holder.getUser().getId(),ENTITY_TYPE_USER,userId);
        }
        model.addAttribute("followeeNum",followeeNum);
        model.addAttribute("followerNum",followerNum);
        model.addAttribute("followStatus",followStatus);


        return "/site/profile";
    }
}
