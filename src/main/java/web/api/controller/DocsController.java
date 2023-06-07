package web.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/docs")
public class DocsController {

    @GetMapping("/member")
    public String memberDocs() {
        return "/docs/index.html";
    }


    @GetMapping("/test")
    public String memberTest() {
        System.out.println("test");
        System.out.println("test22");
        return "st";
    }

}
