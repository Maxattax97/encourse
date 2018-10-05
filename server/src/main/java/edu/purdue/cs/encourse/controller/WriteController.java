package edu.purdue.cs.encourse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.service.AccountService;
import edu.purdue.cs.encourse.service.AdminService;
import edu.purdue.cs.encourse.service.CourseService;
import edu.purdue.cs.encourse.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping(value="/secured")
public class WriteController {

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CourseService courseService;


    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/add/section", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody
    ResponseEntity<?> addSection(@RequestParam(name = "userName", required = false) String userName,
                                 @RequestBody String json) {
        int result;
        Section s;
        try {
            s = new ObjectMapper().readValue(json, Section.class);
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
    @RequestMapping(value = "/add/professorToCourse", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> addProfessorToCourse(@RequestParam(name = "userName") String userName,
                                                              @RequestParam(name = "courseID") String courseID,
                                                              @RequestParam(name = "semester") String semester) {
        int result = adminService.assignProfessorToCourse(userName, courseID, semester);
        HttpStatus status = (result == 0)? HttpStatus.OK : HttpStatus.NOT_MODIFIED;
        return new ResponseEntity<>(result, status);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/add/studentToSection", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> addStudentToSection(@RequestParam(name = "userName") String userName,
                                                               @RequestParam(name = "courseID") String courseID,
                                                               @RequestParam(name = "semester") String semester,
                                                               @RequestParam(name = "sectionType") String sectionType) {
        int result = adminService.registerStudentToSection(userName, courseID, semester, sectionType);
        HttpStatus status = (result == 0)? HttpStatus.OK : HttpStatus.NOT_MODIFIED;
        return new ResponseEntity<>(result, status);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/add/studentToProject", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> addStudentToProject(@RequestParam(name = "projectID") String projectID,
                                                               @RequestParam(name = "userName") String userName) {
        int result = professorService.assignProjectToStudent(projectID, userName);
        if (result == 0) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else if (result == -1) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/add/studentToTA", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> addTA(@RequestParam(name = "userName") String userName) {

        Student student = accountService.retrieveStudent(userName);
        if (student != null) {
            adminService.hireStudentAsTeachingAssistant(userName);
            TeachingAssistant ta = accountService.retrieveTA(userName);
            return new ResponseEntity<>(ta, HttpStatus.OK);
        }
        return new ResponseEntity<>(student, HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    @RequestMapping(value = "/add/test", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> addTestScript(@RequestParam(name = "projectID") String projectID,
                                                         @RequestParam(name = "testName") String testName,
                                                         @RequestParam(name = "isHidden") boolean isHidden,
                                                         @RequestParam(name = "points") int points,
                                                         @RequestBody String body) {
        String contents = null;
        try {
            Base64.Decoder dec= Base64.getDecoder();
            byte[] strdec=dec.decode(body);
            contents = new String(strdec, "UTF-8");
        } catch (Exception e) {
            System.out.println("Could not parse body: \n" + e);
        }

        if (contents == null) {
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        int result = professorService.uploadTestScript(projectID, testName, contents, isHidden, points);
        if (result == 0) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else if (result == -1) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    @RequestMapping(value = "/add/project", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody
    ResponseEntity<?> addProject(@RequestBody String json) {
        int result;
        Project p;
        try {
            p = new ObjectMapper().readValue(json, Project.class);
            result = professorService.addProject(p.getCourseID(), p.getSemester(), p.getProjectName(), p.getRepoName(), p.getStartDate(), p.getDueDate(), p.getTestRate());
            if (result == 0) {
                professorService.assignProject(p.getProjectIdentifier());
            }
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>((p != null)? p: result, (p != null)? HttpStatus.CREATED: HttpStatus.NOT_MODIFIED);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    @RequestMapping(value = "/modify/project", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> modifyProject(@RequestParam(name = "projectID") String projectID,
                                                         @RequestParam(name = "field") String field,
                                                         @RequestParam(name = "value") String value) {

        int result = professorService.modifyProject(projectID, field, value);
        if (result == 0) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else if (result == -1) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    @RequestMapping(value = "/clone/project", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> cloneProject(@RequestParam(name = "projectID") String projectID) {

        int result = professorService.cloneProjects(projectID);
        if (result == 0) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else if (result == -1) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    @RequestMapping(value = "/pull/project", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> pullProject(@RequestParam(name = "projectID") String projectID) {

        int result = professorService.pullProjects(projectID);
        if (result == 0) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else if (result == -1) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
}
