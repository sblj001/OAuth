package com.yootk.oauth.action;

import com.yootk.oauth.service.IDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CommonAction {
    @RequestMapping("/logoutInfo")
    public String logout() {
        return "logout_info" ;
    }
    @RequestMapping("/login")
    public String login() {
        return "login" ;
    }
//    @RequestMapping("/noauthz")
//    public String noauthz() {
//        return "plugins/noauthz" ;
//    }
    @Autowired
    private IDeptService deptService ;
    @RequestMapping("/pages/welcome")
    public String welcome() {
        this.deptService.list();
        return "welcome" ;
    }
}
