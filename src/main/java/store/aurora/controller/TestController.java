package store.aurora.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    @GetMapping("/api/test")
    @ResponseBody
    public String test() {
        return "ok";
    }
}
