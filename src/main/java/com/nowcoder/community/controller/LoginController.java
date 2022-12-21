package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.MailClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private UserService userService;

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
}
