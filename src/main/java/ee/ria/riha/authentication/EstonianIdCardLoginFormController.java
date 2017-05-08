package ee.ria.riha.authentication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EstonianIdCardLoginFormController {

    @Value("${idCardAuthUrl}")
    private String idCardAuthUrl;

    @GetMapping("/login")
    public String index(Model model) {
        model.addAttribute("idCardAuthUrl", idCardAuthUrl);
        return "login";
    }


}
