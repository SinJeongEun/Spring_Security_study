package com.sp.fc.web.controller;

import com.sp.fc.web.annotation.CustomSecurityAnnotation;
import com.sp.fc.web.service.Paper;
import com.sp.fc.web.service.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/paper")
public class PaperController {
    @Autowired
    private PaperService paperService;

    @PreAuthorize("isStudent()")
    @GetMapping("/mypapers")
    public List<Paper> myPapers(@AuthenticationPrincipal User user){
        return paperService.getMyPapers(user.getUsername());
    }

    @PostFilter("notPrepareState(filterObject) && filterObject.studentIds.contains(#user.username)")
    @GetMapping("/mypapers2")
    public List<Paper> myPapers2(@AuthenticationPrincipal User user){
        return paperService.getMyPapers(user.getUsername());
    }

    @PreAuthorize("hasPermission(#paperId, 'paper', 'read')")
    @GetMapping("/get/{paperId}")
    public Paper getPaper(@AuthenticationPrincipal User user, @PathVariable Long paperId){
        return paperService.getPaper(paperId);
    }

    @PostAuthorize("returnObject.studentIds.contains(#user.username)")
    @GetMapping("/get2/{paperId}")
    public Paper getPaper2(@AuthenticationPrincipal User user, @PathVariable Long paperId){
        return paperService.getPaper(paperId);
    }

//    @Secured({"SCHOOL_PRIMARY"})
    @GetMapping("/papersByPrimary")
    public List<Paper> getPaperByPrimary (@AuthenticationPrincipal User user) {
        return paperService.getAllPapers();
    }

    @CustomSecurityAnnotation("SCHOOL_PRIMARY") //커스텀 어노테이션 사용하기
    @GetMapping("/papersByPrimary2")
    public List<Paper> getPaperByPrimary2 (@AuthenticationPrincipal User user) {
        return paperService.getAllPapers();
    }
}
