package edu.purdue.cs.encourse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.purdue.cs.encourse.domain.Account;
import edu.purdue.cs.encourse.domain.Section;
import edu.purdue.cs.encourse.domain.Student;
import edu.purdue.cs.encourse.domain.User;
import edu.purdue.cs.encourse.domain.relations.StudentSection;
import edu.purdue.cs.encourse.service.AccountService;
import edu.purdue.cs.encourse.service.AdminService;
import edu.purdue.cs.encourse.service.ProfessorService;
import edu.purdue.cs.encourse.util.JSONReturnable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value="/secured")
public class Controller {

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private AccountService accountService;

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
    public @ResponseBody ResponseEntity<?> getCommitData(@RequestParam(name = "projectID") String projectID) {
        // TODO: RYAN PUT YOUR SHIT HERE
        JSONReturnable returnJson = professorService.countAllCommitsByDay(projectID);
        String json = returnJson.jsonObject.toJSONString();
        User loggedIn = getUserFromAuth();
        // loggedIn.getUsername()
        // professorService.getCommitData(loggedIn.getUsername());
        // json = JSONObject.toString();r
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/progressHistogram", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getProgressHistogram() {
        // TODO: RYAN PUT YOUR SHIT HERE
        User loggedIn = getUserFromAuth();
        Account account = accountService.retrieveAccount(loggedIn.getUsername(), loggedIn.getPassword());
        System.out.println(account);
        JSONReturnable returnJson = profService.getProgressHistogram(account.getUserID());
        String json = returnJson.jsonObject.toJSONString();
        System.out.println(json);
        // loggedIn.getUsername()
        // profService.getCommitData(loggedIn.getUsername());
        // json = JSONObject.toString();
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/section/students", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getSectionStudents(@RequestParam(name = "courseID") String courseID,
                                                              @RequestParam(name = "semester") String semester,
                                                              @RequestParam(name = "type") String type) {
        List<StudentSection> sections = adminService.getAllStudentsInSection(courseID, semester, type);
        List<Student> students = new ArrayList<>();
        for (StudentSection s: sections) {
           // students.add(accountService.retrieveStudent());
        }

        System.out.println(sections);
        // loggedIn.getUsername()
        // profService.getCommitData(loggedIn.getUsername());
        // json = JSONObject.toString();
        return new ResponseEntity<>(sections, HttpStatus.OK);
    }

    private User getUserFromAuth() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return ((User)securityContext.getAuthentication().getPrincipal());
    }

}
