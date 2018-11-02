package edu.purdue.cs.encourse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.purdue.cs.encourse.database.TeachingAssistantStudentRepository;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.domain.relations.ProjectTestScript;
import edu.purdue.cs.encourse.domain.relations.TeachingAssistantStudent;
import edu.purdue.cs.encourse.service.*;
import edu.purdue.cs.encourse.service.impl.UserDetailsServiceImpl;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(value="/api")
public class WriteController {

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TeachingAssistantService taService;

    /**
     * Create a new Section
     *
     * @param  userName (not required : defaults to the logged in user) userName of Professor assigned to section
     * @param  body     http request body with Section
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/add/section", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody ResponseEntity<?> addSection(@RequestParam(name = "userName", required = false) String userName,
                                                      @RequestBody String body) {
        Section result;
        Section s;
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(body);
            String CRN = (String) json.get("CRN");
            String semester = (String) json.get("semester");
            String courseID = (String) json.get("courseID");
            String courseTitle = (String) json.get("courseTitle");
            String sectionType = (String) json.get("sectionType");
            String timeSlot = (String) json.get("timeSlot");
            result = adminService.addSection(CRN, semester, courseID, courseTitle, sectionType, timeSlot);
            if (userName != null && !userName.isEmpty()) {
                adminService.assignProfessorToCourse(userName, courseID, semester);
            }
            s = adminService.retrieveSection(courseID + " "  + semester + ": " + sectionType);
        } catch (ParseException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>((s != null)? s: result, (s != null)? HttpStatus.CREATED: HttpStatus.NOT_MODIFIED);
    }

    /**
     * Assign Professor to Section
     *
     * @param  userName userName of Professor
     * @param  courseID identifier of course such as 'cs252'
     * @param  semester identifier of semester such as 'Fall2018'
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/add/professorToCourse", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> addProfessorToCourse(@RequestParam(name = "userName") String userName,
                                                                @RequestParam(name = "courseID") String courseID,
                                                                @RequestParam(name = "semester") String semester) {
        int result = adminService.assignProfessorToCourse(userName, courseID, semester);
        HttpStatus status = (result == 0)? HttpStatus.OK : HttpStatus.NOT_MODIFIED;
        return new ResponseEntity<>(result, status);
    }

    /**
     * Assign TeachingAssistant to Section
     *
     * @param  userName userName of TeachingAssistant
     * @param  courseID identifier of course such as 'cs252'
     * @param  semester identifier of semester such as 'Fall2018'
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/add/TAToCourse", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> addTAToCourse(@RequestParam(name = "userName") String userName,
                                                         @RequestParam(name = "courseID") String courseID,
                                                         @RequestParam(name = "semester") String semester) {
        int result = adminService.assignTeachingAssistantToCourse(userName, courseID, semester);
        HttpStatus status = (result == 0)? HttpStatus.OK : HttpStatus.NOT_MODIFIED;
        return new ResponseEntity<>(result, status);
    }

    /**
     * Assign Student to Section
     *
     * @param  userName userName of Student
     * @param  sectionID identifier of Section
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/add/studentToSection", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> addStudentToSection(@RequestParam(name = "userName") String userName,
                                                               @RequestParam(name = "sectionID") String sectionID) {
        int result = adminService.registerStudentToSection(userName, sectionID);
        HttpStatus status = (result == 0)? HttpStatus.OK : HttpStatus.NOT_MODIFIED;
        return new ResponseEntity<>(result, status);
    }

    /**
     * Assign a Project to a Student
     *
     * @param  userName userName of Student
     * @param  projectID identifier of Project
     */
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

    /**
     * Upgrade a Student Account to a TeachingAssistant Account
     *
     * @param  userName userName of Student
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/modify/studentToTA", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> modifyStudentToTA(@RequestParam(name = "userName") String userName) {

        Student student = accountService.retrieveStudent(userName);
        if (student != null) {
            adminService.hireStudentAsTeachingAssistant(userName);
            TeachingAssistant ta = accountService.retrieveTA(userName);
            return new ResponseEntity<>(ta, HttpStatus.OK);
        }
        return new ResponseEntity<>(student, HttpStatus.NOT_FOUND);
    }

    /**
     * Assign multiple Students to multiple TeachingAssistants
     *
     * @param  sectionID identifier of Section
     * @param  body http request body map of Student userNames assigned to a TeachingAssistant
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    @RequestMapping(value = "/add/studentsToTA", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> addStudentsToTA(@RequestParam(name = "sectionID") String sectionID,
                                                           @RequestBody String body) {
        List<String> errors = new ArrayList<>();
        List<String> correct = new ArrayList<>();
        Map<String, List<String>> map = new HashMap<>();
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(body);
            for (Object k: json.keySet()) {
                String key = (String) k;
                List<String> names = (List<String>) json.get(key);
                map.put(key, names);
            }
        } catch (ParseException e) {
            return new ResponseEntity<>("{\"errors\": \"Could not parse body\"}", HttpStatus.BAD_REQUEST);
        }

        for (String key: map.keySet()) {
            User user = (User) userDetailsService.loadUserByUsername(key);
            for (String userName: map.get(key)) {
                Student student = accountService.retrieveStudent(userName);
                if (student == null) {
                    errors.add("\"Student " + userName + " could not be found\"");
                    continue;
                }

                int result = professorService.assignTeachingAssistantToStudentInSection(key, userName, sectionID);
                if (result != 0) {
                    if (result == -5) {
                        correct.add("\"Student " + userName + " already assigned to TA\"");
                    } else {
                        errors.add("\"Result: " + result + " from student " + userName + "\"");
                    }
                } else {
                    correct.add("\"Student " + userName + " assigned to TA\"");
                }
            }
        }
        if (errors.isEmpty()) {
            return new ResponseEntity<>("{\"correct\": " + correct + "}", HttpStatus.OK);
        }
        return new ResponseEntity<>("{\"errors\": " + errors + "}", HttpStatus.NOT_MODIFIED);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    @RequestMapping(value = "/share/report", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> addReport(@RequestParam(name = "userName") String userName,
                                                     @RequestParam(name = "reportID") String reportID) {
        Account account = accountService.retrieveAccount(userName);
        if (account != null) {
            emailService.sendGeneratedReportMessage(account.getUserID(), reportID);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
            System.out.println("BEFORE: " + body);
            Base64.Decoder dec = Base64.getDecoder();
            byte[] strdec = dec.decode(body);
            contents = new String(strdec, "UTF-8");
            System.out.println("AFTER: " + contents);
        } catch (Exception e) {
            System.out.println("Could not parse body: \n" + e);
        }

        if (contents == null) {
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        ProjectTestScript result = professorService.uploadTestScript(projectID, testName, contents, isHidden, points);
        System.out.println("RESULT: " + result);
        if (result == null) {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    @RequestMapping(value = "/modify/test", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> modifyProject(@RequestParam(name = "projectID") String projectID,
                                                         @RequestParam(name = "testName") String testName,
                                                         @RequestParam(name = "field") String field,
                                                         @RequestParam(name = "value") String value) {

        int result = professorService.modifyTestScript(projectID, testName, field, value);
        if (result == 0) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else if (result == -1) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    @RequestMapping(value = "/add/directory", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> modifyProject(@RequestParam(name = "courseID") String courseID,
                                                         @RequestParam(name = "semester") String semester) {

        int result = courseService.createDirectory(semester, courseID);
        if (result == 0) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else if (result == -1) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Create a new Project
     *
     * @param  json  http request body containing Project
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    @RequestMapping(value = "/add/project", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody ResponseEntity<?> addProject(@RequestBody String json) {
        Project result;
        Project p;
        try {
            p = new ObjectMapper().readValue(json, Project.class);
            result = professorService.addProject(p.getCourseID(), p.getSemester(), p.getProjectName(), p.getRepoName(), p.getStartDate(), p.getDueDate(), p.getTestRate());
            if (result != null) {
                professorService.assignProject(p.getProjectIdentifier());
            }
        } catch (IOException e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>((p != null)? p: result, (p != null)? HttpStatus.CREATED: HttpStatus.NOT_MODIFIED);
    }

    /**
     * Edit a Project
     *
     * @param  projectID identifier of project
     * @param  body  http request body map of fields to values to update in Project
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    @RequestMapping(value = "/modify/project", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> modifyBulkProject(@RequestParam(name = "projectID") String projectID,
                                                             @RequestBody String body) {
        int result = -1;
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(body);
            for (Object k: json.keySet()) {
                String key = (String)k;
                Object v = json.get(key);
                String val = (String)v;
                result = professorService.modifyProject(projectID, key, val);
                if (result != 0) {
                    throw new ParseException(0);
                }
            }
        } catch (ParseException e) {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        Project p = courseService.getProject(projectID);
        return new ResponseEntity<>(p, HttpStatus.OK);
    }

    /**
     * Clone a Project
     *
     * @param  projectID  identifier of Project
     */
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

    /**
     * Pull a Project
     *
     * @param  projectID identifier of Project
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    @RequestMapping(value = "/pull/project", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> pullProject(@RequestParam(name = "projectID") String projectID,
                                                       @RequestParam(name = "userName", required = false) List<String> userNames) {

        int result = -100;
        if (userNames == null) {
            result = professorService.pullProjects(projectID);
        } else {
            for (String userName: userNames) {
                result = professorService.updateStudentInformation(projectID, userName);
            }
        }
        if (result == 0) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else if (result == -1) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Runs the testall
     *
     * @param  projectID identifier of Project
     * @param  userName (not required) userName of specific student to run testall for
     * @param  historic  (not required) boolean requesting historic or not
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'TA')")
    @RequestMapping(value = "/run/testall", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> testProject(@RequestParam(name = "projectID", required = false) String projectID,
                                                       @RequestParam(name = "userName", required = false) String userName,
                                                       @RequestParam(name = "historic", required = false, defaultValue = "false") boolean historic) {

        int result = -1;
        if (historic) {
            if (projectID != null) {
                result = professorService.runHistoricTestall(projectID);
            }
        } else {
            if (projectID != null && userName == null) {
                result = professorService.runTestall(projectID);
            } else if (projectID != null) {
                Iterator<Authority> iter = getUserAuthorities().iterator();
                while (iter.hasNext()) {
                    String auth = iter.next().getAuthority();
                    if (auth.contentEquals(Account.Role_Names.PROFESSOR) || auth.contentEquals(Account.Role_Names.ADMIN)) {
                        result = professorService.runTestallForStudent(projectID, userName);
                        break;
                    } else if (auth.contentEquals(Account.Role_Names.TA)) {
                        result = taService.runTestallForStudent(projectID, userName, getUserFromAuth().getUsername());
                        break;
                    }
                }
            }
        }
        if (result == 0) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else if (result == -1) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }

    }

    private User getUserFromAuth() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return ((User)securityContext.getAuthentication().getPrincipal());
    }

    private Collection<Authority> getUserAuthorities() {
        return getUserFromAuth().getAuthorities();
    }

    private boolean hasPermissionOverAccount(String userName) {
        return adminService.hasPermissionOverAccount(getUserFromAuth(), userName);
    }

    private boolean hasPermissionOverAccount(User user, String userName) {
        return adminService.hasPermissionOverAccount(user, userName);
    }
}
