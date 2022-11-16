package com.shiro.vuln.SpringMemShell;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.http.Parameters;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

@Controller
public class UserController {


    @PostMapping("/doLogin")
    public String doLoginPage(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam(name="rememberme", defaultValue="") String rememberMe){
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login((AuthenticationToken)new UsernamePasswordToken(username, password, rememberMe.equals("remember-me")));
        // 如果认证失败
        }catch (AuthenticationException e) {
            return "forward:/koishiHaHaHa";
        }
        return "forward:/koishiLveLveLve";
    }
    @ResponseBody
    @RequestMapping(value={"/koishiHaHaHa"})
    public String koishiTry() throws Exception {

        //这里的输出格式居然是 html 格式的，我之前尝试加 \n 和多输入几个空格，页面都没变化，突然反应过来挺像html的，加个</br>，果然能换行了
        return "你说说看有没有一种可能，咱账号密码妹整对呀~ </br> poc页面貌似可以传入poc参数进行反序列化捏";
    }
    @ResponseBody
    @RequestMapping(value={"/koishiLveLveLve"})
    public String koishiTryTry() throws Exception {

        return "你把账号密码蒙出来了，那是真滴牛逼";
    }

    @ResponseBody
    @RequestMapping(value={"/"})
    public String helloPage() throws Exception {

        return "hello";
    }

    @ResponseBody
    @RequestMapping(value={"/unauth"})
    public String errorPage() {
        return "error";
    }

    @ResponseBody
    @RequestMapping(value={"/login"})
    public String loginPage() {
        return "please login pattern /doLogin";
    }

    @RequestMapping(value={"/poc"})
    public void poc(@RequestParam("poc") String poc) {
        byte[] bpoc = Base64.decodeBase64(poc);
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bpoc));
            ois.readObject();
            ois.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
