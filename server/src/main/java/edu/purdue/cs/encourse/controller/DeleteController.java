package edu.purdue.cs.encourse.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/api")
public class DeleteController {

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR')")
    @RequestMapping(value = "/delmove/project", method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity<?> modifyProject(@RequestParam(name = "projectID") String projectID) {

        //int result = professorService.modifyProject(projectID, field, value);
//        if (result == 0) {
//            return new ResponseEntity<>(result, HttpStatus.OK);
//        } else if (result == -1) {
//            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
//        } else {
//            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
//        }
        return null;
    }

}
