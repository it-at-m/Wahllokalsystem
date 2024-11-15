package de.muenchen.oss.wahllokalsystem.authservice.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SessionController {

    @GetMapping("/loginwls")
    public String index(Model model, HttpSession session) {
        session.setAttribute("TestAttr-Spring_Session-Table", "remove_me");
        return "loginwls";
    }

    @PostMapping("/savecolor")
    public String saveMessage(@RequestParam("color") String color, HttpServletRequest request){
        List<String> favoriteColors
                = getFavColors(request.getSession());
        if (!StringUtils.isEmpty(color)) {
            favoriteColors.add(color);
            request.getSession().
                    setAttribute("TeatAttr-Spring_Session_Attributes-Table", favoriteColors);
        }
        return "redirect:/loginwls";
    }

    private List<String> getFavColors(HttpSession session) {
        List<String> favoriteColors = (List<String>) session.getAttribute("TeatAttr-Spring_Session_Attributes-Table");
        if (favoriteColors == null) {
            favoriteColors = new ArrayList<>();
        }
        return favoriteColors;
    }
}
