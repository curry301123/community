package com.nowcode.commuity.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommunityUtil {

    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
    //MD5加密
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }else {
            return DigestUtils.md5DigestAsHex(key.getBytes());
        }
    }
    public static String getJSONSting(int code, String msg, Map<String,Object> map){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if(map != null){
            for (String key: map.keySet()){
                json.put(key,map.get(key));
            }
        }
        return json.toJSONString();
    }
    public static String getJSONSting(int code,String msg){
        return getJSONSting(code,msg,null);
    }

    public static String getJSONSting(int code ){
        return getJSONSting(code,null, null);
    }

    public static void main(String[] args) {
        Map<String,Object> map = new HashMap<>();
        map.put("name","zhangsan");
        map.put("age",25);
        System.out.println(getJSONSting(0,"ok",map));
    }
}
