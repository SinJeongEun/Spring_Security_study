package com.sp.fc.web.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaperService implements InitializingBean {

    private HashMap<Long, Paper> paperDB = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    public void setPaper(Paper paper){
        paperDB.put(paper.getPaperId(), paper);
    }

    public List<Paper> getMyPapers(String username) {
//        return paperDB.values().stream().collect(Collectors.toList());
        return paperDB.values().stream().filter(
                paper -> paper.getStudentIds().contains(username)
        ).collect(Collectors.toList());
    }

    public Paper getPaper(Long paperId) {
        return paperDB.get(paperId);
    }

    public List<Paper> getAllPapers() {
        return paperDB.values().stream().collect(Collectors.toList());
    }

    @Secured({"ROLE_PRIMARY", "ROLE_RUN_AS_PRIMARY"})
    public List<Paper> getAllPapers2() {
        return paperDB.values().stream().collect(Collectors.toList());
    }
}
