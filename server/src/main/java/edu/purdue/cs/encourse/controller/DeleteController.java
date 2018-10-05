package edu.purdue.cs.encourse.controller;

import edu.purdue.cs.encourse.domain.User;
import edu.purdue.cs.encourse.service.AccountService;
import edu.purdue.cs.encourse.service.AdminService;
import edu.purdue.cs.encourse.service.CourseService;
import edu.purdue.cs.encourse.service.ProfessorService;
import edu.purdue.cs.encourse.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/api")
public class DeleteController {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CourseService courseService;

    // TODO: make delete work for all types
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    @RequestMapping(value = "/delete/user", method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity<?> deleteUser(@RequestParam(name = "userName") String userName) {
        if (hasPermissionForStudent(userName)) {
            userDetailsService.changeUserEnabled((User)userDetailsService.loadUserByUsername(userName));
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    @RequestMapping(value = "/delete/project", method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity<?> deleteProject(@RequestParam(value = "projectID") String projectID) {

        int result = professorService.deleteProject(projectID);
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

    private boolean hasPermissionForStudent(String userName) {
        return adminService.hasPermissionForStudent(getUserFromAuth(), userName);
    }

}
