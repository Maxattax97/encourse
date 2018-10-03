package edu.purdue.cs.encourse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.purdue.cs.encourse.domain.Account;
import edu.purdue.cs.encourse.domain.Section;
import edu.purdue.cs.encourse.domain.Student;
import edu.purdue.cs.encourse.domain.User;
import edu.purdue.cs.encourse.domain.relations.StudentSection;
import edu.purdue.cs.encourse.service.AccountService;
import edu.purdue.cs.encourse.service.AdminService;
import edu.purdue.cs.encourse.service.CourseService;
import edu.purdue.cs.encourse.service.ProfessorService;
import edu.purdue.cs.encourse.util.JSONReturnable;
import org.json.simple.JSONArray;
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

    @Autowired
    private CourseService courseService;

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

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    @RequestMapping(value = "/studentsData", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getStudentData(@RequestParam(name = "courseID") String courseID,
                                                          @RequestParam(name = "semester") String semester) {
        JSONArray json = courseService.getStudentData(semester, courseID);
        if (json == null) {
            return new ResponseEntity<>(json, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    @RequestMapping(value = "/projectsData", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getProjectData(@RequestParam(name = "courseID") String courseID,
                                                          @RequestParam(name = "semester") String semester) {
        JSONArray json = courseService.getProjectData(semester, courseID);
        if (json == null) {
            return new ResponseEntity<>(json, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    @RequestMapping(value = "/studentCommitTime", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getStudentCommitByTime(@RequestParam(name = "projectID") String projectID,
                                                                  @RequestParam(name = "userName") String userName) {
        JSONReturnable returnJson = professorService.getCommitList(projectID, userName);
        if (returnJson == null) {
            return new ResponseEntity<>(returnJson, HttpStatus.NOT_FOUND);
        }
        if (returnJson.jsonObject == null) {
            return new ResponseEntity<>(returnJson, HttpStatus.NOT_FOUND);
        }
        String json = returnJson.jsonObject.toJSONString();
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    @RequestMapping(value = "/commitCount", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getStudentCommitCountByDay(@RequestParam(name = "projectID") String projectID,
                                                                      @RequestParam(name = "userName", required = false) String userName,
                                                                      @RequestParam(name = "all", required = false, defaultValue = "false") boolean all) {
        JSONReturnable returnJson = professorService.getCommitList(projectID, userName);
        if (returnJson == null) {
            return new ResponseEntity<>(returnJson, HttpStatus.NOT_FOUND);
        }
        if (returnJson.jsonObject == null) {
            return new ResponseEntity<>(returnJson, HttpStatus.NOT_FOUND);
        }
        String json = returnJson.jsonObject.toJSONString();
        return new ResponseEntity<>(json, HttpStatus.OK);
    }



    private User getUserFromAuth() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return ((User)securityContext.getAuthentication().getPrincipal());
    }

}
