package de.muenchen.oss.wahllokalsystem.authservice.rest;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import de.muenchen.oss.wahllokalsystem.authservice.domain.OAuthServerSession;
import de.muenchen.oss.wahllokalsystem.authservice.domain.OAuthServerSessions;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SessionController {

    //SessionRegistry sessionRegistry;

    //@GetMapping("/loginwls")
    @RequestMapping(value = "/loginwls", method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        session.setAttribute("TestAttr-Spring_Session-Table", "remove_me");
        return "loginwls";
    }

    @PostMapping("/savecolor")
    public String saveMessage(@RequestParam("color") String color, HttpServletRequest request, Principal principal){
        List<String> favoriteColors
                = getFavColors(request.getSession());
        if (!StringUtils.isEmpty(color)) {
            favoriteColors.add(color);
            request.getSession().
                    setAttribute("TeatAttr-Spring_Session_Attributes-Table", favoriteColors);
        }
//        sessionRegistry.getAllPrincipals().forEach(pr ->{
//            log.info("one found {}", pr.toString());
//        });
        return "redirect:/loginwls";
    }

    //@PreAuthorize("hasAuthority(" + ROLE_SESSION_MANAGEMENT + ")")
//    @RequestMapping(value = "/oauthsessions/", method = GET)
//    public ResponseEntity<OAuthServerSessions> listActiveSessions() {
//        log.info("listActiveSessions");
//
//        OAuthServerSessions allSessionInformation = new OAuthServerSessions();
//        List<OAuthServerSession> sessions = new ArrayList<>();
//
//        sessionRegistry.getAllPrincipals().stream().forEach(
//                principal -> sessionRegistry.getAllSessions(principal, false).stream().forEach(currSessionInfo -> {
//                    OAuthServerSession currSession = new OAuthServerSession();
////                    if (principal instanceof UserInfo) {
////                        currSession.setUsername(((UserInfo) principal).getUsername());
////                    } else
//                        if (principal instanceof Principal) {
//                        currSession.setUsername(((Principal) principal).getName());
////                    } else if (principal instanceof LdapUserDetailsImpl) {
////                        currSession.setUsername(((LdapUserDetailsImpl) principal).getUsername());
//                    } else if (principal instanceof String) {
//                        currSession.setUsername((String) principal);
//                    } else {
//                        currSession.setUsername("<unknown>");
//                    }
//                    currSession.setSessionId(currSessionInfo.getSessionId());
//                    sessions.add(currSession);
//                }));
//
//        allSessionInformation.setSessions(sessions);
//        return new ResponseEntity<>(allSessionInformation, OK);
//    }

    private List<String> getFavColors(HttpSession session) {
        List<String> favoriteColors = (List<String>) session.getAttribute("TeatAttr-Spring_Session_Attributes-Table");
        if (favoriteColors == null) {
            favoriteColors = new ArrayList<>();
        }
        return favoriteColors;
    }
}
