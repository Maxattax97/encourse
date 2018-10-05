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
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value="/secured")
public class ReadController {

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CourseService courseService;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    @RequestMapping(value = "/studentsData", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getStudentData(@RequestParam(name = "courseID") String courseID,
                                                          @RequestParam(name = "semester") String semester) {
        JSONArray json = courseService.getStudentData(semester, courseID);
        System.out.println("STUDENT DATA RETURN: " + json);
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
//        Map<String, JSONObject> map = new HashMap<>();
//        for (Object j: json) {
//            JSONObject jsonObject = (JSONObject)j;
//            map.put(jsonObject.get("id").toString(), jsonObject);
//        }
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/commitList", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getStudentCommitByTime(@RequestParam(name = "projectID") String projectID,
                                                                  @RequestParam(name = "userName") String userName) {
        if (hasPermissionForStudent(userName)) {
            JSONReturnable returnJson = professorService.getCommitList(projectID, userName);
            if (returnJson == null) {
                return new ResponseEntity<>(returnJson, HttpStatus.NO_CONTENT);
            }
            if (returnJson.jsonObject == null) {
                return new ResponseEntity<>(returnJson, HttpStatus.NO_CONTENT);
            }
            String json = returnJson.jsonObject.toJSONString();
            return new ResponseEntity<>(json, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/commitCount", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getCommitCount(@RequestParam(name = "projectID") String projectID,
                                                          @RequestParam(name = "userName") String userName) {
        if (hasPermissionForStudent(userName)) {
            JSONReturnable returnJson = professorService.getCommitCounts(projectID, userName);
            if (returnJson == null) {
                return new ResponseEntity<>(returnJson, HttpStatus.NO_CONTENT);
            }
            if (returnJson.jsonObject == null) {
                return new ResponseEntity<>(returnJson, HttpStatus.NO_CONTENT);
            }
            String json = returnJson.jsonObject.toJSONString();
            return new ResponseEntity<>(json, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getStatistics(@RequestParam(name = "projectID") String projectID,
                                                         @RequestParam(name = "userName") String userName) {
        if (hasPermissionForStudent(userName)) {
            JSONReturnable returnJson = professorService.getStatistics(projectID, userName);
            if (returnJson == null) {
                return new ResponseEntity<>(returnJson, HttpStatus.NO_CONTENT);
            }
            if (returnJson.jsonObject == null) {
                return new ResponseEntity<>(returnJson, HttpStatus.NO_CONTENT);
            }
            String json = returnJson.jsonObject.toJSONString();
            return new ResponseEntity<>(json, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/diffs", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getDiffs(@RequestParam(name = "projectID") String projectID,
                                                    @RequestParam(name = "userName") String userName) {
        if (hasPermissionForStudent(userName)) {
            JSONReturnable returnJson = professorService.getAdditionsAndDeletions(projectID, userName);
            if (returnJson == null) {
                return new ResponseEntity<>(returnJson, HttpStatus.NO_CONTENT);
            }
            if (returnJson.jsonObject == null) {
                return new ResponseEntity<>(returnJson, HttpStatus.NO_CONTENT);
            }
            String json = returnJson.jsonObject.toJSONString();
            return new ResponseEntity<>(json, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/progress", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getProgress(@RequestParam(name = "projectID") String projectID,
                                                       @RequestParam(name = "userName") String userName) {
        if (hasPermissionForStudent(userName)) {
            JSONReturnable returnJson = professorService.getStudentProgress(projectID, userName);
            if (returnJson == null) {
                return new ResponseEntity<>(returnJson, HttpStatus.NO_CONTENT);
            }
            if (returnJson.jsonObject == null) {
                return new ResponseEntity<>(returnJson, HttpStatus.NO_CONTENT);
            }
            String json = returnJson.jsonObject.toJSONString();
            return new ResponseEntity<>(json, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }


    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    @RequestMapping(value = "/classProgress", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getProgress(@RequestParam(name = "projectID") String projectID) {
        JSONReturnable returnJson = professorService.getClassProgress(projectID);
        if (returnJson == null) {
            return new ResponseEntity<>(returnJson, HttpStatus.NO_CONTENT);
        }
        if (returnJson.jsonObject == null) {
            return new ResponseEntity<>(returnJson, HttpStatus.NO_CONTENT);
        }
        String json = returnJson.jsonObject.toJSONString();
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    private Account getAccountFromAuth() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        User user = ((User)securityContext.getAuthentication().getPrincipal());
        return accountService.retrieveAccount(user.getUsername());
    }

    private User getUserFromAuth() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return ((User)securityContext.getAuthentication().getPrincipal());
    }

    private boolean hasPermissionForStudent(String userName) {
        return adminService.hasPermissionForStudent(getUserFromAuth(), userName);
    }

}
