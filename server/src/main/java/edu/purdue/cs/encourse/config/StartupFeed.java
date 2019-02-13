package edu.purdue.cs.encourse.config;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.domain.relations.CourseStudent;
import edu.purdue.cs.encourse.model.AccountModel;
import edu.purdue.cs.encourse.model.CourseModel;
import edu.purdue.cs.encourse.model.CourseProjectModel;
import edu.purdue.cs.encourse.model.CourseSectionModel;
import edu.purdue.cs.encourse.model.CourseStudentModel;
import edu.purdue.cs.encourse.model.SectionModel;
import edu.purdue.cs.encourse.model.StudentTAModel;
import edu.purdue.cs.encourse.model.UserModel;
import edu.purdue.cs.encourse.service.*;
import edu.purdue.cs.encourse.util.ConfigurationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Conditional(value = {ProdProfileCondition.class})
public class StartupFeed implements ApplicationListener<ApplicationReadyEvent> {
    
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private AdminServiceV2 adminService;
    
    @Autowired
    private CourseServiceV2 courseService;
    
    @Autowired
    private ProjectService projectService;
    
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        feedDatabase();
    }
    
    private void feedDatabase() {
        try {
            Course course = null;
            if (accountRepository.findAll().iterator().hasNext()) {

                Account grr = accountService.addAccount(new AccountModel("grr", "Gustavo", "Rodriguez-Rivera", "grr@purdue.edu", Account.Role.PROFESSOR.ordinal()));
                Account killian = accountService.addAccount(new AccountModel("kleclain-a", "Killian", "LeClainche", "kleclain@purdue.edu", Account.Role.ADMIN.ordinal()));
                Account jordan = accountService.addAccount(new AccountModel("reed226-a", "William", "Reed", "reed226@purdue.edu", Account.Role.ADMIN.ordinal()));

                adminService.addUser(new UserModel(grr, "$2a$04$/zamuN8nrPT0qZ4jbaTTp..kBjKUtMu.Jbj2DAHZ..KLDON4REPJu"));
                adminService.addUser(new UserModel(killian, "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6"));
                adminService.addUser(new UserModel(jordan, "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6"));

                course = courseService.addCourse(new CourseModel(grr.getUserID(), "1001", "Systems Programming", "cs252", "Spring2019", "/homes/cs252/sourcecontrol/work"));

                courseService.addSection(new CourseSectionModel(course.getCourseID(), "LE1/2", "N/A"));

                BufferedReader reader = new BufferedReader(new FileReader(course.getCourseHub() + "/students.txt"));

                String student;
                int count = 1;
                List<CourseStudent> students = new ArrayList<>();

                while ((student = reader.readLine()) != null && count <= ConfigurationManager.getInstance().limit) {
                    Account studentAccount = accountService.addAccount(new AccountModel(student, "Student", student, student + "@purdue.edu", Account.Role.STUDENT.ordinal()));

                    students.add(adminService.addCourseStudent(new CourseStudentModel(course.getCourseID(), studentAccount.getUserID())));

                    count++;
                }

                reader.close();

                reader = new BufferedReader(new FileReader("src/taconfig.txt"));
                BufferedReader studentReader = new BufferedReader(new FileReader("src/stuconfig.txt"));
                String input;
                List<CourseStudent> tas = new ArrayList<>();
                while ((student = reader.readLine()) != null) {
                    String[] lab = student.split(" ");
                    if (!lab[0].equals("Start")) {
                        continue;
                    }
                    tas.clear();
                    String[] info;
                    while (!(info = reader.readLine().split(" "))[0].equals("End")) {
                        Account ta = accountService.addAccount(new AccountModel(info[0] + "-lab" + lab[2], info[1], info[2], info[0] + "@purdue.edu", Account.Role.STUDENT.ordinal()));
                        adminService.addUser(new UserModel(ta, info[4]));
                        tas.add(adminService.addCourseTA(new CourseStudentModel(course.getCourseID(), ta.getUserID())));
                    }

                    while ((input = studentReader.readLine()) != null) {
                        String[] roster = input.split(" ");
                        if (!roster[0].equals("Start")) {
                            continue;
                        }
                        while ((input = studentReader.readLine()) != null) {
                            roster = input.split(" ");
                            if (roster[0].equals("End")) {
                                break;
                            }
                            roster = input.split(",");
                            CourseStudent studentAccount = null;
                            for (CourseStudent studentIterator : students) {
                                if (studentIterator.getStudent().getUsername().equals(roster[0])) {
                                    studentAccount = studentIterator;
                                    break;
                                }
                            }
                            if (studentAccount != null) {
                                for (CourseStudent ta : tas) {
                                    adminService.addCourseStudentToTA(new StudentTAModel(studentAccount.getId(), ta.getId()));
                                }
                            }
                        }
                        break;
                    }
                }
            }
            if (course == null) {
                course = courseRepository.findByNameAndSemester("cs252", "Spring2019");
            }

            // TODO: @KILLIAN Integrate redundancy checks into addProject() method
            if (!projectRepository.existsByName("MyMalloc")) {
                Project mymalloc = projectService.addProject(new CourseProjectModel(course.getCourseID(), "MyMalloc", LocalDate.of(2019, 1, 8), LocalDate.of(2019, 1, 28), "lab1-src", false));
            }
            if (!projectRepository.existsByName("Shell Scripting")) {
                Project bash = projectService.addProject(new CourseProjectModel(course.getCourseID(), "Shell Scripting", LocalDate.of(2019, 1, 29), LocalDate.of(2019, 2, 11), "lab2-src", false));
            }
            if (!projectRepository.existsByName("Implementing a Shell")) {
                Project shell = projectService.addProject(new CourseProjectModel(course.getCourseID(), "Implementing a Shell", LocalDate.of(2019, 2, 11), LocalDate.of(2019, 3, 4), "lab3-src", false));
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void addMallocTestScripts(Project project) {
        /*professorService.addTestScript(project.getProjectID(), "test_simple0", false, 2);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_simple0", "Simple Tests");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_simple0", "Part 1");
        professorService.addTestScript(project.getProjectID(), "test_simple1", false, 3);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_simple1", "Simple Tests");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_simple1", "Part 1");
        professorService.addTestScript(project.getProjectID(), "test_simple2", false, 3);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_simple2", "Simple Tests");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_simple2", "Part 1");
        professorService.addTestScript(project.getProjectID(), "test_simple3", false, 3);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_simple3", "Simple Tests");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_simple3", "Part 1");
        professorService.addTestScript(project.getProjectID(), "test_simple4", false, 3);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_simple4", "Simple Tests");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_simple4", "Part 1");
        professorService.addTestScript(project.getProjectID(), "test_simple5", false, 3);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_simple5", "Simple Tests");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_simple5", "Part 1");
        professorService.addTestScript(project.getProjectID(), "test_simple6", false, 3);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_simple6", "Simple Tests");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_simple6", "Part 1");

        professorService.addTestScript(project.getProjectID(), "test_exact", false, 5);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_exact", "Malloc Tests");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_exact", "Part 2");
        professorService.addTestScript(project.getProjectID(), "test_split", false, 5);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_split", "Malloc Tests");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_split", "Part 2");
        professorService.addTestScript(project.getProjectID(), "test_multi_malloc", false, 5);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_multi_malloc", "Malloc Tests");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_multi_malloc", "Part 2");
        professorService.addTestScript(project.getProjectID(), "test_insert_chunk", false, 3);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_insert_chunk", "Malloc Tests");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_insert_chunk", "Part 2");
        professorService.addTestScript(project.getProjectID(), "test_coalesce_chunk_insert", false, 3);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_coalesce_chunk_insert", "Malloc Tests");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_coalesce_chunk_insert", "Part 2");
        professorService.addTestScript(project.getProjectID(), "test_coalesce_chunk_coalesce", false, 4);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_coalesce_chunk_coalesce", "Malloc Tests");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_coalesce_chunk_coalesce", "Part 2");
        professorService.addTestScript(project.getProjectID(), "test_malloc_large", false, 5);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_malloc_large", "Malloc Tests");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_malloc_large", "Part 2");

        professorService.addTestScript(project.getProjectID(), "test_free_insert", false, 3);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_free_insert", "Free Tests");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_free_insert", "Part 2");
        professorService.addTestScript(project.getProjectID(), "test_free_left", false, 4);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_free_left", "Free Tests");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_free_left", "Part 2");
        professorService.addTestScript(project.getProjectID(), "test_free_right", false, 4);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_free_right", "Free Tests");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_free_right", "Part 2");
        professorService.addTestScript(project.getProjectID(), "test_free_both", false, 3);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_free_both", "Free Tests");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_free_both", "Part 2");
        professorService.addTestScript(project.getProjectID(), "test_free_even", false, 5);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_free_even", "Free Tests");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_free_even", "Part 2");
        professorService.addTestScript(project.getProjectID(), "test_free_odd", false, 5);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_free_odd", "Free Tests");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_free_odd", "Part 2");

        professorService.addTestScript(project.getProjectID(), "test_large", false, 10);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_large", "Robustness Tests");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_large", "Part 2");
        professorService.addTestScript(project.getProjectID(), "test_all_lists", false, 5);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_all_lists", "Robustness Tests");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_all_lists", "Part 2");

        professorService.addTestScript(project.getProjectID(), "test_locks", false, 3);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_locks", "Other Tests");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_locks", "Part 2");
        professorService.addTestScript(project.getProjectID(), "test_malloc_zero", false, 2);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_malloc_zero", "Other Tests");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_malloc_zero", "Part 2");
        professorService.addTestScript(project.getProjectID(), "test_free_null", false, 2);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_free_null", "Other Tests");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_free_null", "Part 2");
        professorService.addTestScript(project.getProjectID(), "test_double_free", false, 2);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_double_free", "Other Tests");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_double_free", "Part 2");
        professorService.addTestScript(project.getProjectID(), "test_out_of_ram", false, 2);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_out_of_ram", "Other Tests");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_out_of_ram", "Part 2");*/
    }

    private void addShellTestScripts(Project project) {
        //professorService.addTestScript(project.getProjectID(), "test_redirect_input", false, 2);
        /*professorService.addTestScript(project.getProjectID(), "test_redirect_output", false, 2);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_redirect_output", "IO Redirection");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_redirect_output", "Part 1");
        professorService.addTestScript(project.getProjectID(), "test_redirect_error", false, 1);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_redirect_error", "IO Redirection");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_redirect_error", "Part 1");
        professorService.addTestScript(project.getProjectID(), "test_redirect_error2", false, 1);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_redirect_error2", "IO Redirection");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_redirect_error2", "Part 1");
        professorService.addTestScript(project.getProjectID(), "test_redirect_out_err", false, 2);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_redirect_out_err", "IO Redirection");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_redirect_out_err", "Part 1");
        professorService.addTestScript(project.getProjectID(), "test_redirect_multiple", false, 1);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_redirect_multiple", "IO Redirection");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_redirect_multiple", "Part 1");
        professorService.addTestScript(project.getProjectID(), "test_append_output", false, 1);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_append_output", "IO Redirection");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_append_output", "Part 1");

        professorService.addTestScript(project.getProjectID(), "test_pipes1", false, 2);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_pipes1", "Pipes");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_pipes1", "Part 1");
        professorService.addTestScript(project.getProjectID(), "test_pipes2", false, 2);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_pipes2", "Pipes");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_pipes2", "Part 1");
        professorService.addTestScript(project.getProjectID(), "test_pipes_redirect_out", false, 2);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_pipes_redirect_out", "Pipes");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_pipes_redirect_out", "Part 1");
        professorService.addTestScript(project.getProjectID(), "test_pipes_redirect_err", false, 2);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_pipes_redirect_err", "Pipes");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_pipes_redirect_err", "Part 1");

        professorService.addTestScript(project.getProjectID(), "test_background", false, 2);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_background", "Background Processes");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_background", "Part 1");
        //professorService.addTestScript(project.getProjectID(), "test_zombie", false, 1);

        professorService.addTestScript(project.getProjectID(), "test_printenv", false, 1);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_printenv", "Builtin Functions");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_printenv", "Part 2");
        //professorService.addTestScript(project.getProjectID(), "test_setenv", false, 0.5);
        professorService.addTestScript(project.getProjectID(), "test_unsetenv", false, 0.5);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_unsetenv", "Builtin Functions");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_unsetenv", "Part 2");
        //professorService.addTestScript(project.getProjectID(), "test_source", false, 2);

        professorService.addTestScript(project.getProjectID(), "test_cd1", false, 0.5);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_cd1", "cd");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_cd1", "Part 2");
        professorService.addTestScript(project.getProjectID(), "test_cd2", false, 0.5);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_cd2", "cd");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_cd2", "Part 2");
        professorService.addTestScript(project.getProjectID(), "test_cd3", false, 0.5);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_cd3", "cd");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_cd3", "Part 2");
        //professorService.addTestScript(project.getProjectID(), "test_cd4", false, 0.5);

        professorService.addTestScript(project.getProjectID(), "test_parsing1", false, 0.5);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_parsing1", "Parsing");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_parsing1", "Part 2");
        professorService.addTestScript(project.getProjectID(), "test_parsing2", false, 0.5);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_parsing2", "Parsing");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_parsing2", "Part 2");
        //professorService.addTestScript(project.getProjectID(), "test_quotes1", false, 1);
        //professorService.addTestScript(project.getProjectID(), "test_quotes2", false, 1);
        professorService.addTestScript(project.getProjectID(), "test_escape", false, 2);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_escape", "Parsing");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_escape", "Part 2");
        //professorService.addTestScript(project.getProjectID(), "test_subshell", false, 10);

        professorService.addTestScript(project.getProjectID(), "test_env_expand1", false, 1);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_env_expand1", "Environment Variable Expansion");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_env_expand1", "Part 3");
        professorService.addTestScript(project.getProjectID(), "test_env_expand2", false, 1);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_env_expand2", "Environment Variable Expansion");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_env_expand2", "Part 3");
        professorService.addTestScript(project.getProjectID(), "test_env_var_shell", false, 1);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_env_var_shell", "Environment Variable Expansion");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_env_var_shell", "Part 3");
        professorService.addTestScript(project.getProjectID(), "test_env_var_dollar", false, 1);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_env_var_dollar", "Environment Variable Expansion");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_env_var_dollar", "Part 3");
        professorService.addTestScript(project.getProjectID(), "test_env_var_question", false, 1);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_env_var_question", "Environment Variable Expansion");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_env_var_question", "Part 3");
        professorService.addTestScript(project.getProjectID(), "test_env_var_bang", false, 1);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_env_var_bang", "Environment Variable Expansion");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_env_var_bang", "Part 3");
        //professorService.addTestScript(project.getProjectID(), "test_env_var_uscore", false, 1);

        professorService.addTestScript(project.getProjectID(), "test_wildcards1", false, 3);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_wildcards1", "Wildcarding");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_wildcards1", "Part 3");
        professorService.addTestScript(project.getProjectID(), "test_wildcards2", false, 1);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_wildcards2", "Wildcarding");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_wildcards2", "Part 3");
        professorService.addTestScript(project.getProjectID(), "test_wildcards3", false, 1);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_wildcards3", "Wildcarding");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_wildcards3", "Part 3");
        professorService.addTestScript(project.getProjectID(), "test_wildcards4", false, 1);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_wildcards4", "Wildcarding");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_wildcards4", "Part 3");
        professorService.addTestScript(project.getProjectID(), "test_wildcards5", false, 1);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_wildcards5", "Wildcarding");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_wildcards5", "Part 3");
        professorService.addTestScript(project.getProjectID(), "test_wildcards6", false, 1);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_wildcards6", "Wildcarding");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_wildcards6", "Part 3");
        professorService.addTestScript(project.getProjectID(), "test_wildcards7", false, 1);
        professorService.addTestScriptToSuite(project.getProjectID(), "test_wildcards7", "Wildcarding");
        professorService.addTestScriptToSuite(project.getProjectID(), "test_wildcards7", "Part 3");
        //professorService.addTestScript(project.getProjectID(), "test_tilde", false, 2);
        //professorService.addTestScript(project.getProjectID(), "test_robustness", false, 10);*/
    }
}

abstract class ProfileCondition extends SpringBootCondition {
    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        if (matchProfiles(conditionContext.getEnvironment())) {
            return ConditionOutcome.match("A local profile has been found.");
        }
        return ConditionOutcome.noMatch("No local profiles found.");
    }

    protected abstract boolean matchProfiles(final Environment environment);
}

class ProdProfileCondition extends ProfileCondition {
    public boolean matchProfiles(final Environment environment) {
        return !Arrays.stream(environment.getActiveProfiles()).anyMatch(prof -> {
            return prof.equals("dev");
        });
    }
}
