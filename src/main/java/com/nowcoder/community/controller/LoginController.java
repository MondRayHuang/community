package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.MailClient;
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

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private UserService userService;

    @Value("${Server.servlet.context.path}")
    private String contextPath;

    @RequestMapping(path = "/register",method = RequestMethod.GET )
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(path = "/login",method = RequestMethod.GET )
    public String getLoginPage(){
        return "/site/login";
    }

    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String, Object> register = userService.register(user);
        if(register == null || register.isEmpty()){
            System.out.println("空的");
            model.addAttribute("msg","注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }
        model.addAttribute("usernameMsg",register.get("usernameMsg"));
        model.addAttribute("passwordMsg",register.get("passwordMsg"));
        model.addAttribute("emailMsg",register.get("emailMsg"));
        return "/site/register";
    }
    //domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode()
    @RequestMapping(path = "/activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId,@PathVariable("code") String code) {
        int activation = userService.activation(userId, code);
        if (activation == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功，您的帐号已经可以正常使用了");
            model.addAttribute("target", "/login");
        } else if (activation == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "该用户已经激活过了");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败，您提供的激活码不正确");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    public void getKatcha(HttpServletResponse response, HttpSession session){
        //利用工具生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        //将验证码存入到session中
        session.setAttribute("kaptcha",text);

        //将图片输出到浏览器
        response.setContentType("image/png");
        try{
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @RequestMapping(path = "/login",method = RequestMethod.POST)
    public String login(String username,String password,String code,boolean rememberme,
                        HttpServletResponse response,HttpSession httpSession,Model model){
        //检查验证码
        String kaptcha = (String)(httpSession.getAttribute("kaptcha"));
        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equals(code)){
            model.addAttribute("codeMsg","验证码不正确");
            return "/site/login";
        }
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECOND : DEFAULE_EXPIRED_SECOND;
        Map<String, Object> login = userService.login(username, password, expiredSeconds);
        if(login.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",login.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }else{
            model.addAttribute("usernameMsg",login.get("usernameMsg"));
            model.addAttribute("passwordMsg",login.get("passwordMsg"));
            return "/site/login";
        }
    }



}
