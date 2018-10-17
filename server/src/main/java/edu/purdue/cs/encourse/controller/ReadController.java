package edu.purdue.cs.encourse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.domain.relations.StudentSection;
import edu.purdue.cs.encourse.service.*;
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
@RequestMapping(value="/api")
public class ReadController {

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ReportService reportService;

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

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    @RequestMapping(value = "/sections", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getAllSections() {
        List<Section> sections = adminService.findAllSections();
        return new ResponseEntity<>(sections, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    @RequestMapping(value = "/coursesData", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getProjectData(@RequestParam(name = "userName") String userName) {
        if (hasPermissionOverAccount(userName)) {
            JSONArray json = courseService.getCourseData(userName);

            if (json == null) {
                return new ResponseEntity<>(json, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(json, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/testSummary", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getTestSummary(@RequestParam(name = "projectID") String projectID) {
        JSONReturnable returnJson = professorService.getTestSummary(projectID);
        if (returnJson == null) {
            return new ResponseEntity<>(returnJson, HttpStatus.NO_CONTENT);
        }
        if (returnJson.jsonObject == null) {
            return new ResponseEntity<>(returnJson, HttpStatus.NO_CONTENT);
        }
        String json = returnJson.jsonObject.toJSONString();
        return new ResponseEntity<>(json, HttpStatus.OK);
    }



    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/commitList", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getStudentCommitByTime(@RequestParam(name = "projectID") String projectID,
                                                                  @RequestParam(name = "userName") List<String> userNames) {
        List<String> errors = new ArrayList<>();
        List<String> correct = new ArrayList<>();
        for (String userName: userNames) {
            if (hasPermissionOverAccount(userName)) {
                JSONReturnable returnJson = professorService.getCommitList(projectID, userName);
                if (returnJson == null || returnJson.jsonObject == null) {
                    errors.add(userName + " does not have content");
                }
                String json = returnJson.jsonObject.toJSONString();
                correct.add(json);
            } else {
                errors.add(getUserFromAuth().getUsername() + " does not have access over " + userName);
            }
        }
        if (errors.isEmpty()) {
            return new ResponseEntity<>(correct, HttpStatus.OK);
        }
        return new ResponseEntity<>("{\"errors\": " + errors + ", \"correct\": " + correct + "}", HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/commitCount", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getCommitCount(@RequestParam(name = "projectID") String projectID,
                                                          @RequestParam(name = "userName") List<String> userNames) {
        List<String> errors = new ArrayList<>();
        List<String> correct = new ArrayList<>();
        for (String userName: userNames) {
            if (hasPermissionOverAccount(userName)) {
                JSONReturnable returnJson = professorService.getCommitCounts(projectID, userName);
                if (returnJson == null || returnJson.jsonObject == null) {
                    errors.add(userName + " does not have content");
                }
                String json = returnJson.jsonObject.toJSONString();
                correct.add(json);
            } else {
                errors.add(getUserFromAuth().getUsername() + " does not have access over " + userName);
            }
        }
        if (errors.isEmpty()) {
            return new ResponseEntity<>(correct, HttpStatus.OK);
        }
        return new ResponseEntity<>("{\"errors\": " + errors + ", \"correct\": " + correct + "}", HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getStatistics(@RequestParam(name = "projectID") String projectID,
                                                         @RequestParam(name = "userName") List<String> userNames) {
        List<String> errors = new ArrayList<>();
        List<String> correct = new ArrayList<>();
        for (String userName: userNames) {
            if (hasPermissionOverAccount(userName)) {
                JSONReturnable returnJson = professorService.getStatistics(projectID, userName);
                if (returnJson == null || returnJson.jsonObject == null) {
                    errors.add(userName + " does not have content");
                }
                String json = returnJson.jsonObject.toJSONString();
                correct.add(json);
            } else {
                errors.add(getUserFromAuth().getUsername() + " does not have access over " + userName);
            }
        }
        if (errors.isEmpty()) {
            return new ResponseEntity<>(correct, HttpStatus.OK);
        }
        return new ResponseEntity<>("{\"errors\": " + errors + ", \"correct\": " + correct + "}", HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/diffs", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getDiffs(@RequestParam(name = "projectID") String projectID,
                                                    @RequestParam(name = "userName") List<String> userNames) {
        List<String> errors = new ArrayList<>();
        List<String> correct = new ArrayList<>();
        for (String userName: userNames) {
            if (hasPermissionOverAccount(userName)) {
                JSONReturnable returnJson = professorService.getAdditionsAndDeletions(projectID, userName);
                if (returnJson == null || returnJson.jsonObject == null) {
                    errors.add(userName + " does not have content");
                }
                String json = returnJson.jsonObject.toJSONString();
                correct.add(json);
            } else {
                errors.add(getUserFromAuth().getUsername() + " does not have access over " + userName);
            }
        }
        if (errors.isEmpty()) {
            return new ResponseEntity<>(correct, HttpStatus.OK);
        }
        return new ResponseEntity<>("{\"errors\": " + errors + ", \"correct\": " + correct + "}", HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/progress", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getProgress(@RequestParam(name = "projectID") String projectID,
                                                       @RequestParam(name = "userName") List<String> userNames) {
        List<String> errors = new ArrayList<>();
        List<String> correct = new ArrayList<>();
        for (String userName: userNames) {
            if (hasPermissionOverAccount(userName)) {
                JSONReturnable returnJson = professorService.getStudentProgress(projectID, userName);
                if (returnJson == null || returnJson.jsonObject == null) {
                    errors.add(userName + " does not have content");
                }
                String json = returnJson.jsonObject.toJSONString();
                correct.add(json);
            } else {
                errors.add(getUserFromAuth().getUsername() + " does not have access over " + userName);
            }
        }
        if (errors.isEmpty()) {
            return new ResponseEntity<>(correct, HttpStatus.OK);
        }
        return new ResponseEntity<>("{\"errors\": " + errors + ", \"correct\": " + correct + "}", HttpStatus.BAD_REQUEST);
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

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    @RequestMapping(value = "/report", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getReport(@RequestParam(name = "reportID") String reportID,
                                                     @RequestParam(name = "lock") String lock) {

        Report report = reportService.getReport(reportID, lock);
        if (report != null) {
            return new ResponseEntity<>(report, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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

    private boolean hasPermissionOverAccount(String userName) {
        return adminService.hasPermissionOverAccount(getUserFromAuth(), userName);
    }

}
