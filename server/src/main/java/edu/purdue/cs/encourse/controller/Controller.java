package edu.purdue.cs.encourse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.purdue.cs.encourse.domain.Section;
import edu.purdue.cs.encourse.domain.User;
import edu.purdue.cs.encourse.service.AdminService;
import edu.purdue.cs.encourse.service.ProfService;
import edu.purdue.cs.encourse.util.JSONReturnable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(value="/secured")
public class Controller {

    @Autowired
    private ProfService profService;

    @Autowired
    private AdminService adminService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/create/section", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody ResponseEntity<?> createSection(@RequestParam(name = "userName", required = false) String userName,
                                                         @RequestBody String json) {
        int result;
        Section s;
        try {
            s = new ObjectMapper().readValue(json, Section.class);
            System.out.println(s);
            result = adminService.addSection(s.getCRN(), s.getSemester(), s.getCourseID(), s.getCourseTitle(), s.getSectionType());
            if (userName != null && !userName.isEmpty()) {
                adminService.assignProfessorToCourse(userName, s.getCourseID(), s.getSemester());
            }
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>((s != null)? s: result, (s != null)? HttpStatus.CREATED: HttpStatus.NOT_MODIFIED);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/modify/section", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> modifySection(@RequestParam(name = "userName") String userName,
                                                         @RequestParam(name = "courseID") String courseID,
                                                         @RequestParam(name = "semester") String semester) {
        int result = adminService.assignProfessorToCourse(userName, courseID, semester);
        HttpStatus status = (result == 0)? HttpStatus.OK : HttpStatus.NOT_MODIFIED;
        return new ResponseEntity<>(result, status);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/commitData", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getCommitData() {
        // TODO: RYAN PUT YOUR SHIT HERE
        JSONReturnable returnJson = profService.getCommitData();
        String json = returnJson.jsonObject.toJSONString();
        User loggedIn = getUserFromAuth();
        // loggedIn.getUsername()
        // profService.getCommitData(loggedIn.getUsername());
        // json = JSONObject.toString();r
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    private User getUserFromAuth() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return ((User)securityContext.getAuthentication().getPrincipal());
    }

}
