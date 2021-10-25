package com.nowcode.commuity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.websocket.server.PathParam;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AlphaController {

    //处理get请求
    @RequestMapping(path = "/student",method = RequestMethod.GET)
    @ResponseBody
    public String getStu(@RequestParam(name = "current") int current, @RequestParam(name = "limit") int limit){
        System.out.println(current);
        System.out.println(limit);

        return "some student";
    }

    @RequestMapping(path = "/students/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String getStu(@PathVariable("id")int id){
        System.out.println(id);
        return "student";
    }
    //处理post请求
    @RequestMapping(path = "/student",method = RequestMethod.POST)
    @ResponseBody
    public String gettt(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return "stutut";
    }

    //响应HTML数据
    @RequestMapping(path = "/teacher",method = RequestMethod.GET)
    public ModelAndView getTeacher(ModelAndView mvc){
        mvc.addObject("name","zhangsan");
        mvc.addObject("age",30);
        mvc.setViewName("/demo/view");
        return mvc;
    }

    @RequestMapping(path = "/emp",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getEmp(){
        Map<String,Object> emp = new HashMap<>();
        emp.put("name","zhangsan");
        emp.put("age",23);
        emp.put("salary",8000);
        return  emp;
    }

}
