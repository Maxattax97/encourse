package edu.purdue.cs.encourse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.domain.relations.StudentSection;
import edu.purdue.cs.encourse.service.*;
import edu.purdue.cs.encourse.util.JSONReturnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    @Autowired
    private TeachingAssistantService taService;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'TA')")
    @RequestMapping(value = "/studentsData", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getStudentData(@RequestParam(name = "courseID") String courseID,
                                                          @RequestParam(name = "semester") String semester,
                                                          @RequestParam(name = "page", defaultValue = "1", required = false) int page,
                                                          @RequestParam(name = "size", defaultValue = "100", required = false) int size,
                                                          @RequestParam(name = "sortBy", defaultValue = "id", required = false) String sortBy,
                                                          @RequestParam(name = "projectID", required = false) String projectID,
                                                          @RequestParam(name = "userName", required = false) List<String> userNames) {
        JSONArray returnJson = null;
        Iterator iter = getUserAuthorities().iterator();
        while (iter.hasNext()) {
            String auth = ((Authority) iter.next()).getAuthority();
            if (auth.contentEquals(Account.Role_Names.PROFESSOR) || auth.contentEquals(Account.Role_Names.ADMIN)) {
                returnJson = professorService.getStudentData(semester, courseID);
                break;
            } else if (auth.contentEquals(Account.Role_Names.TA)) {
                returnJson = taService.getStudentData(semester, courseID, getUserFromAuth().getUsername());
                break;
            }
        }
        if (returnJson == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        List<JSONObject> jsonValues = new ArrayList<>();
        List<JSONObject> specifiedStudents = new ArrayList<>();
        boolean specificStudent = false;
        for (int i = 0; i < returnJson.size(); i++) {
            JSONObject json = (JSONObject)returnJson.get(i);
            if (userNames != null) {
                if (userNames.contains(json.get("id"))) {
                    specifiedStudents.add(json);
                    specificStudent = true;
                    continue;
                }
            }
            jsonValues.add(json);
        }
        if (specificStudent) {
            return new ResponseEntity<>(specifiedStudents, HttpStatus.OK);
        }

        Comparator<JSONObject> compare;
        switch(sortBy) {
            case "timeSpent":
            case "grades":
                if (projectID == null) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                compare = (JSONObject a, JSONObject b) -> {
                    JSONObject jsonA = (JSONObject) a.get(sortBy);
                    JSONObject jsonB = (JSONObject) b.get(sortBy);

                    double valA = ((Number)jsonA.get(projectID)).doubleValue();
                    double valB = ((Number)jsonB.get(projectID)).doubleValue();
                    return Double.compare(valA, valB);
                };
                break;
            case "id":
            default:
                compare = (JSONObject a, JSONObject b) -> {
                    String valA = (String) a.get(sortBy);
                    String valB = (String) b.get(sortBy);
                    return valA.compareTo(valB);
                };
                break;
        }
        jsonValues.sort(compare);

        JSONArray sortedAndPagedJsonArray = new JSONArray();
        page = (page > jsonValues.size() / size + 1) ? jsonValues.size() / size + 1 : page;
        for (int i = (page - 1) * size; i < jsonValues.size(); i++) {
            if (i >= page * size) {
                break;
            }
            sortedAndPagedJsonArray.add(jsonValues.get(i));
        }

        JSONObject response = new JSONObject();
        response.put("content", sortedAndPagedJsonArray);
        response.put("totalPages", jsonValues.size() / size + 1);
        response.put("page", page);
        response.put("totalSize", jsonValues.size());
        response.put("size", size);
        response.put("elements", sortedAndPagedJsonArray.size());
        response.put("sortedBy", sortBy);
        response.put("last", (page >= jsonValues.size() / size));
        response.put("first", (page == 1));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    @RequestMapping(value = "/teachingAssistantsData", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getTeachingAssistantData(@RequestParam(name = "courseID") String courseID,
                                                                    @RequestParam(name = "semester") String semester) {
        JSONArray json = professorService.getTeachingAssistantData(semester, courseID);
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
    public @ResponseBody ResponseEntity<?> getAllSections(@RequestParam(name = "page", defaultValue = "1", required = false) int page,
                                                          @RequestParam(name = "size", defaultValue = "10", required = false) int size,
                                                          @RequestParam(name = "sortBy", defaultValue = "courseID", required = false) String sortBy) {
        List<Section> sections = adminService.findAllSections();
            switch (sortBy) {
                case "courseID":
                default:
                    sections.sort(Comparator.comparing(Section::getCourseID));
                    break;
            }

            List<Section> sortedAndPagedJsonArray = new ArrayList<>();
            for (int i = (page - 1) * size; i < sections.size(); i++) {
                if (i >= page * size) {
                    break;
                }
                sortedAndPagedJsonArray.add(sections.get(i));
            }

            JSONObject response = new JSONObject();
            response.put("content", sortedAndPagedJsonArray);
            response.put("totalPages", sections.size() / size + 1);
            response.put("page", page);
            response.put("totalSize", sections.size());
            response.put("size", size);
            response.put("elements", sortedAndPagedJsonArray.size());
            response.put("sortedBy", sortBy);
            response.put("last", (page >= sections.size() / size));
            response.put("first", (page == 1));

            return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'TA')")
    @RequestMapping(value = "/coursesData", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getCourseData() {
        JSONArray returnJson = null;
        Iterator<Authority> iter = getUserAuthorities().iterator();
        while (iter.hasNext()) {
            String auth = iter.next().getAuthority();
            if (auth.contentEquals(Account.Role_Names.PROFESSOR) || auth.contentEquals(Account.Role_Names.ADMIN)) {
                returnJson = professorService.getCourseData(getUserFromAuth().getUsername());
                break;
            } else if (auth.contentEquals(Account.Role_Names.TA)) {
                returnJson = taService.getCourseData(getUserFromAuth().getUsername());
                break;
            }
        }
        if (returnJson == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(returnJson, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    @RequestMapping(value = "/sectionsData", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getSectionData(@RequestParam(name = "courseID") String courseID,
                                                          @RequestParam(name = "semester") String semester) {
        JSONArray json = courseService.getSectionData(semester, courseID);

        if (json == null) {
            return new ResponseEntity<>(json, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/testSummary", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getTestSummary(@RequestParam(name = "projectID") String projectID,
                                                          @RequestParam(name = "anonymous", defaultValue = "false", required = false) boolean anon) {
        JSONReturnable returnJson = null;
        Iterator iter = getUserAuthorities().iterator();
        while (iter.hasNext()) {
            String auth = ((Authority) iter.next()).getAuthority();
            if (auth.contentEquals(Account.Role_Names.PROFESSOR) || auth.contentEquals(Account.Role_Names.ADMIN)) {
                returnJson = professorService.getTestSummary(projectID);
                break;
            } else if (auth.contentEquals(Account.Role_Names.TA)) {
                if (anon) {
                    returnJson = taService.getAnonymousTestSummary(projectID);
                } else {
                    returnJson = taService.getAssignmentsTestSummary(projectID, getUserFromAuth().getUsername());
                }
                break;
            }
        }

        if (returnJson == null) {
            return new ResponseEntity<>(returnJson, HttpStatus.NO_CONTENT);
        }
        if (returnJson.jsonObject == null) {
            return new ResponseEntity<>(returnJson, HttpStatus.NO_CONTENT);
        }
        String json = returnJson.jsonObject.toJSONString();
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    //TODO: Update to consume list of students
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/commitList", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getStudentCommitByTime(@RequestParam(name = "projectID") String projectID,
                                                                  @RequestParam(name = "userName") String userName,
                                                                  @RequestParam(name = "page", defaultValue = "1", required = false) int page,
                                                                  @RequestParam(name = "size", defaultValue = "10", required = false) int size,
                                                                  @RequestParam(name = "sortBy", defaultValue = "date", required = false) String sortBy) {
        List<String> errors = new ArrayList<>();
        List<String> correct = new ArrayList<>();
        JSONReturnable returnJson = null;

        if (hasPermissionOverAccount(userName)) {
            Iterator iter = getUserAuthorities().iterator();
            while (iter.hasNext()) {
                String auth = ((Authority) iter.next()).getAuthority();
                if (auth.contentEquals(Account.Role_Names.PROFESSOR) || auth.contentEquals(Account.Role_Names.ADMIN)) {
                    returnJson = professorService.getCommitList(projectID, userName);
                    break;
                } else if (auth.contentEquals(Account.Role_Names.TA)) {
                    returnJson = taService.getCommitList(projectID, userName, getUserFromAuth().getUsername());
                    break;
                }
            }

            if (returnJson == null || returnJson.jsonObject == null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            String json = returnJson.jsonObject.toJSONString();
            correct.add(json);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        JSONArray json = (JSONArray) returnJson.getJsonObject().get("data");
        if (json == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<JSONObject> jsonValues = new ArrayList<>();
        for (int i = 0; i < json.size(); i++) {
            JSONObject obj = (JSONObject) json.get(i);
            jsonValues.add(obj);
        }

        Comparator<JSONObject> compare;
        switch (sortBy) {
            case "date":
                compare = (JSONObject a, JSONObject b) -> {
                    String valA = (String) a.get(sortBy);
                    String valB = (String) b.get(sortBy);
                    Date parsedA;
                    Date parsedB;
                    try {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
                        parsedA = format.parse(valA);
                        parsedB = format.parse(valB);
                    } catch (ParseException pe) {
                        throw new IllegalArgumentException(pe);
                    }
                    return parsedA.compareTo(parsedB);
                };
                break;
            case "commit_count":
            case "additions":
            case "deletions":
                compare = (JSONObject a, JSONObject b) -> {
                    long valA = (long) a.get(sortBy);
                    long valB = (long) b.get(sortBy);
                    return Long.compare(valA, valB);
                };
                break;
            case "time_spent":
            default:
                compare = (JSONObject a, JSONObject b) -> {
                    String valA = (String) a.get(sortBy);
                    String valB = (String) b.get(sortBy);
                    return valA.compareTo(valB);
                };
                break;
        }
        jsonValues.sort(compare);

        JSONArray sortedAndPagedJsonArray = new JSONArray();
        page = (page > jsonValues.size() / size + 1) ? jsonValues.size() / size + 1 : page;
        for (int i = (page - 1) * size; i < jsonValues.size(); i++) {
            if (i >= page * size) {
                break;
            }
            sortedAndPagedJsonArray.add(jsonValues.get(i));
        }

        JSONObject response = new JSONObject();
        response.put("content", sortedAndPagedJsonArray);
        response.put("totalPages", jsonValues.size() / size + 1);
        response.put("page", page);
        response.put("totalSize", jsonValues.size());
        response.put("size", size);
        response.put("elements", sortedAndPagedJsonArray.size());
        response.put("sortedBy", sortBy);
        response.put("last", (page >= jsonValues.size() / size));
        response.put("first", (page == 1));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/commitCount", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getCommitCount(@RequestParam(name = "projectID") String projectID,
                                                          @RequestParam(name = "userName") List<String> userNames) {
        List<String> errors = new ArrayList<>();
        List<String> correct = new ArrayList<>();
        for (String userName: userNames) {
            if (hasPermissionOverAccount(userName)) {
                JSONReturnable returnJson = null;
                Iterator iter = getUserAuthorities().iterator();
                while (iter.hasNext()) {
                    String auth = ((Authority) iter.next()).getAuthority();
                    if (auth.contentEquals(Account.Role_Names.PROFESSOR) || auth.contentEquals(Account.Role_Names.ADMIN)) {
                        returnJson = professorService.getCommitCounts(projectID, userName);
                        break;
                    } else if (auth.contentEquals(Account.Role_Names.TA)) {
                        returnJson = taService.getCommitCounts(projectID, userName, getUserFromAuth().getUsername());
                        break;
                    }
                }

                if (returnJson == null || returnJson.jsonObject == null) {
                    errors.add("\"" + userName + " does not have content" + "\"");
                    continue;
                }
                String json = returnJson.jsonObject.toJSONString();
                correct.add(json);
            } else {
                errors.add("\"" + getUserFromAuth().getUsername() + " does not have access over " + userName + "\"");
            }
        }
        if (errors.isEmpty()) {
            return new ResponseEntity<>(correct.get(0), HttpStatus.OK);
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
                JSONReturnable returnJson = null;
                Iterator iter = getUserAuthorities().iterator();
                while (iter.hasNext()) {
                    String auth = ((Authority) iter.next()).getAuthority();
                    if (auth.contentEquals(Account.Role_Names.PROFESSOR) || auth.contentEquals(Account.Role_Names.ADMIN)) {
                        returnJson = professorService.getStatistics(projectID, userName);
                        break;
                    } else if (auth.contentEquals(Account.Role_Names.TA)) {
                        returnJson = taService.getStatistics(projectID, userName, getUserFromAuth().getUsername());
                        break;
                    }
                }

                if (returnJson == null || returnJson.jsonObject == null) {
                    errors.add("\"" + userName + " does not have content" + "\"");
                    continue;
                }
                String json = returnJson.jsonObject.toJSONString();
                correct.add(json);
            } else {
                errors.add("\"" + getUserFromAuth().getUsername() + " does not have access over " + userName + "\"");
            }
        }
        if (errors.isEmpty()) {
            return new ResponseEntity<>(correct.get(0), HttpStatus.OK);
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
                JSONReturnable returnJson = null;
                Iterator iter = getUserAuthorities().iterator();
                while (iter.hasNext()) {
                    String auth = ((Authority) iter.next()).getAuthority();
                    if (auth.contentEquals(Account.Role_Names.PROFESSOR) || auth.contentEquals(Account.Role_Names.ADMIN)) {
                        returnJson = professorService.getAdditionsAndDeletions(projectID, userName);
                        break;
                    } else if (auth.contentEquals(Account.Role_Names.TA)) {
                        returnJson = taService.getAdditionsAndDeletions(projectID, userName, getUserFromAuth().getUsername());
                        break;
                    }
                }

                if (returnJson == null || returnJson.jsonObject == null) {
                    errors.add("\"" + userName + " does not have content" + "\"");
                    continue;
                }
                String json = returnJson.jsonObject.toJSONString();
                correct.add(json);
            } else {
                errors.add("\"" + getUserFromAuth().getUsername() + " does not have access over " + userName + "\"");
            }
        }
        if (errors.isEmpty()) {
            return new ResponseEntity<>(correct.get(0), HttpStatus.OK);
        }
        return new ResponseEntity<>("{\"errors\": " + errors + ", \"correct\": " + correct + "}", HttpStatus.BAD_REQUEST);
    }


    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/velocity", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getVelocity(@RequestParam(name = "projectID") String projectID,
                                                       @RequestParam(name = "userName") String userName) {
        JSONReturnable returnJson = null;
        if (hasPermissionOverAccount(userName)) {
            Iterator iter = getUserAuthorities().iterator();
            while (iter.hasNext()) {
                String auth = ((Authority) iter.next()).getAuthority();
                if (auth.contentEquals(Account.Role_Names.PROFESSOR) || auth.contentEquals(Account.Role_Names.ADMIN)) {
                    returnJson = professorService.getCommitVelocity(projectID, userName);
                    break;
                } else if (auth.contentEquals(Account.Role_Names.TA)) {
                    returnJson = null;
                    break;
                }
            }

            if (returnJson == null || returnJson.jsonObject == null) {
                return new ResponseEntity<>("{\"errors\": \"" + userName + " does not have content\"}", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("{\"errors\": \"" + getUserFromAuth().getUsername() + " does not have access over " + userName + "\"}", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(returnJson.jsonObject.toJSONString(), HttpStatus.OK);
    }


    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/progress", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getProgress(@RequestParam(name = "projectID") String projectID,
                                                       @RequestParam(name = "userName", required = false) List<String> userNames) {
        List<String> errors = new ArrayList<>();
        List<String> correct = new ArrayList<>();

        JSONReturnable returnJson = null;
        Iterator iter = getUserAuthorities().iterator();
        while (iter.hasNext()) {
            String auth = ((Authority) iter.next()).getAuthority();
            if (auth.contentEquals(Account.Role_Names.PROFESSOR) || auth.contentEquals(Account.Role_Names.ADMIN)) {
                break;
            } else if (auth.contentEquals(Account.Role_Names.TA) && userNames == null) {
                returnJson = taService.getAssignmentsProgress(projectID, getUserFromAuth().getUsername());
                if (returnJson == null || returnJson.jsonObject == null) {
                    errors.add("\"" + getUserFromAuth().getUsername() + "'s students do not have content" + "\"");
                    break;
                }
                String json = returnJson.jsonObject.toJSONString();
                correct.add(json);
                break;
            }
        }
        if (userNames == null) {
            userNames = new ArrayList<>();
        }

        for (String userName: userNames) {
            if (hasPermissionOverAccount(userName)) {
                iter = getUserAuthorities().iterator();
                while (iter.hasNext()) {
                    String auth = ((Authority) iter.next()).getAuthority();
                    if (auth.contentEquals(Account.Role_Names.PROFESSOR) || auth.contentEquals(Account.Role_Names.ADMIN)) {
                        returnJson = professorService.getStudentProgress(projectID, userName);
                        break;
                    } else if (auth.contentEquals(Account.Role_Names.TA)) {
                        returnJson = taService.getStudentProgress(projectID, userName, getUserFromAuth().getUsername());
                        break;
                    }
                }

                if (returnJson == null || returnJson.jsonObject == null) {
                    errors.add("\"" + userName + " does not have content" + "\"");
                    continue;
                }
                String json = returnJson.jsonObject.toJSONString();
                correct.add(json);
            } else {
                errors.add("\"" + getUserFromAuth().getUsername() + " does not have access over " + userName + "\"");
            }
        }
        if (errors.isEmpty()) {
            return new ResponseEntity<>(correct.get(0), HttpStatus.OK);
        }
        return new ResponseEntity<>("{\"errors\": " + errors + ", \"correct\": " + correct + "}", HttpStatus.BAD_REQUEST);
    }


    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    @RequestMapping(value = "/classProgress", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getProgress(@RequestParam(name = "projectID") String projectID,
                                                       @RequestParam(name = "anonymous", required = false, defaultValue = "false") boolean anon) {
        JSONReturnable returnJson;
        if (anon) {
            returnJson = taService.getAnonymousClassProgress(projectID);
        } else {
            returnJson = professorService.getClassProgress(projectID);
        }
        if (returnJson == null) {
            return new ResponseEntity<>(returnJson, HttpStatus.NO_CONTENT);
        }
        if (returnJson.jsonObject == null) {
            return new ResponseEntity<>(returnJson, HttpStatus.NO_CONTENT);
        }
        String json = returnJson.jsonObject.toJSONString();
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'TA')")
    @RequestMapping(value = "/classCheating", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getClassCheating(@RequestParam(name = "projectID") String projectID) {
        JSONReturnable returnJson = null;
        Iterator<Authority> iter = getUserAuthorities().iterator();
        while (iter.hasNext()) {
            String auth = iter.next().getAuthority();
            if (auth.contentEquals(Account.Role_Names.PROFESSOR) || auth.contentEquals(Account.Role_Names.ADMIN)) {
                returnJson = professorService.getClassCheating(projectID);
                break;
            } else if (auth.contentEquals(Account.Role_Names.TA)) {
                returnJson = taService.getAssignmentsCheating(projectID, getUserFromAuth().getUsername());
                break;
            }
        }

        if (returnJson == null || returnJson.jsonObject == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(returnJson.jsonObject.toJSONString(), HttpStatus.OK);

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

    private Collection<Authority> getUserAuthorities() {
        return getUserFromAuth().getAuthorities();
    }

}
