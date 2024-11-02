package io.github.vimisky.luta.mysql.binlog.helper.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class LutaIndexController {

    @RequestMapping("/")
    public String index(){
        return "redirect:/console/task/list";
    }
}
