package com.sp.fc.web.controller;


import com.sp.fc.user.domain.School;
import com.sp.fc.user.domain.User;
import com.sp.fc.user.service.SchoolService;
import com.sp.fc.user.service.UserService;
import com.sp.fc.web.controller.vo.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final SchoolService schoolService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    private RequestCache requestCache = new HttpSessionRequestCache();

    @GetMapping("/")
    public String home(Model model){
        return "index";
    }

    @ResponseBody
    @GetMapping(value="/schools")
    public List<School> getSchoolList(@RequestParam(value="city", required = true) String city){
        return schoolService.getSchoolList(city);
    }

    @ResponseBody
    @GetMapping(value="/teachers")
    public List<UserData> getTeacherList(@RequestParam(value="schoolId", required = true) Long schoolId){
        return userService.findBySchoolTeacherList(schoolId).stream()
                .map(user->new UserData(user.getUserId(), user.getName()))
                .collect(Collectors.toList());
    }

    @GetMapping("/login")
    public String login(
            @AuthenticationPrincipal User user,
            @RequestParam(value="site", required = false) String site, // manager, student, teacher 인지 가져온다
            @RequestParam(value="error", defaultValue = "false") Boolean error,
            HttpServletRequest request,
            Model model
    ){
        if(site == null) {
            SavedRequest savedRequest = requestCache.getRequest(request, null); //로그아웃이 강제로 됐을 때, 로그인 시 기존에 어느 링크를 방문하려 했는지 정보를 파악한다.
            if(savedRequest != null) {
                site = estimateSite(savedRequest.getRedirectUrl());
            }
        }
        model.addAttribute("error", error);
        model.addAttribute("site", site);

        return "loginForm";
    }

    private String estimateSite(String referer) {
        if(referer == null)
            return "study.html";
        try {
            URL url = new URL(referer);
            String path = url.getPath();
            if(path != null){
                if(path.startsWith("/teacher")) return "teacher";
                if(path.startsWith("/study")) return "study";
                if(path.startsWith("/manager")) return "manager";
            }
            String query = url.getQuery();
            if(query != null){
                if(path.startsWith("/site=teacher")) return "teacher";
                if(path.startsWith("/site=study")) return "study";
                if(path.startsWith("/site=manager")) return "manager";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "study.html";
    }

//    @PostMapping("/login")
//    public String loginPost(@RequestParam String site, Model model){
//        model.addAttribute("site", site);
//
//        return "redirect:/"+site;
//    }

    @GetMapping("/signup")
    public String signUp(
            @RequestParam String site,
            HttpServletRequest request
    ){
        if(site == null) {
            site = estimateSite(request.getParameter("referer"));
        }
        return "redirect:/signup/" + site; // manager, student, teacher 에 따른 회원가입 페이지 redirect
//        return "redirect:/"+site+"/signup"; // manager, student, teacher 에 따른 회원가입 페이지 redirect
    }

    @GetMapping("/access-denied")
    public String accessDenied(){
        return "AccessDenied";
    }

}
