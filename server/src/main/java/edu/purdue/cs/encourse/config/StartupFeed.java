package edu.purdue.cs.encourse.config;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.domain.relations.CourseStudent;
import edu.purdue.cs.encourse.model.AccountModel;
import edu.purdue.cs.encourse.model.CourseModel;
import edu.purdue.cs.encourse.model.CourseProjectModel;
import edu.purdue.cs.encourse.model.CourseSectionModel;
import edu.purdue.cs.encourse.model.CourseStudentModel;
import edu.purdue.cs.encourse.model.ProjectIgnoreModel;
import edu.purdue.cs.encourse.model.ProjectTestScriptModel;
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
import java.io.FileNotFoundException;
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
    
    @Autowired
    private ProjectAnalysisService projectAnalysisService;
    
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        feedDatabase();
    }
    
    private void feedDatabase() {
        try {
            Course course = null;
            if (!accountRepository.findAll().iterator().hasNext()) {

                Account grr = accountService.addAccount(new AccountModel("grr", "Gustavo", "Rodriguez-Rivera", "grr@purdue.edu", Account.Role.PROFESSOR.ordinal()));
                Account jordan = accountService.addAccount(new AccountModel("reed226-a", "William", "Reed", "reed226@purdue.edu", Account.Role.ADMIN.ordinal()));
                Account jordanOG = accountService.addAccount(new AccountModel("jordan", "Jordan", "Buckmaster", "buckmast@purdue.edu", Account.Role.ADMIN.ordinal()));

                adminService.addUser(new UserModel(grr, "$2a$04$/zamuN8nrPT0qZ4jbaTTp..kBjKUtMu.Jbj2DAHZ..KLDON4REPJu"));
                adminService.addUser(new UserModel(jordan, "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6"));
                adminService.addUser(new UserModel(jordanOG, "$2a$04$VN1/mgDvNA.xqw7xf9eAcOZsB2PYVQiVBsVf/MlJgYjzEkZ7sRAcS"));

                course = courseService.addCourse(new CourseModel(grr.getUserID(), "1001", "Systems Programming", "cs252", "Summer2019", "/homes/cs252/sourcecontrol/work"));

                courseService.addSection(new CourseSectionModel(course.getCourseID(), "LE1", "N/A"));

                try {
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
                } catch (FileNotFoundException f) {
                    f.printStackTrace();
                }

                /*

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

                */
            }
            if (course == null) {
                course = courseRepository.findByNameAndSemester("cs252", "Summer2019");
            }
            if (!projectRepository.existsByName("MyMalloc")) {
                Project mymalloc = projectService.addProject(new CourseProjectModel(course.getCourseID(), "MyMalloc", LocalDate.of(2019, 1, 8), LocalDate.of(2019, 2, 2), "lab1-src", false));
                projectService.addProjectIgnoreUser(new ProjectIgnoreModel(mymalloc.getProjectID(), "cs252@cs.purdue.edu"));
            }

            /*

            if (!projectRepository.existsByName("Shell Scripting")) {
                Project bash = projectService.addProject(new CourseProjectModel(course.getCourseID(), "Shell Scripting", LocalDate.of(2019, 1, 29), LocalDate.of(2019, 2, 16), "lab2-src", false));
                projectService.addProjectIgnoreUser(new ProjectIgnoreModel(bash.getProjectID(), "cs252@cs.purdue.edu"));
            }

            if (!projectRepository.existsByName("Implementing a Shell")) {
                Project shell = projectService.addProject(new CourseProjectModel(course.getCourseID(), "Implementing a Shell", LocalDate.of(2019, 2, 11), LocalDate.of(2019, 3, 9), "lab3-src", true));

                projectService.addProjectIgnoreUser(new ProjectIgnoreModel(shell.getProjectID(), "cs252@cs.purdue.edu"));
                projectService.addProjectIgnoreUser(new ProjectIgnoreModel(shell.getProjectID(), "ps@parthshel.com"));

                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_redirect_input", false, 2.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_redirect_output", false, 2.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_redirect_error", false, 1.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_redirect_error2", false, 1.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_redirect_out_err", false, 2.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_redirect_multiple", false, 1.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_append_output", false, 1.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_pipes1", false, 2.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_pipes2", false, 2.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_pipes_redirect_out", false, 2.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_pipes_redirect_err", false, 2.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_background", false, 2.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_zombie", false, 1.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_printenv", false, 1.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_setenv", false, .5));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_unsetenv", false, .5));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_source", false, 2.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_cd1", false, .5));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_cd2", false, .5));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_cd3", false, .5));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_cd4", false, .5));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_parsing1", false, .5));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_parsing2", false, .5));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_quotes1", false, 1.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_quotes2", false, 1.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_escape", false, 2.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_subshell", false, 10.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_env_expand1", false, 1.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_env_expand2", false, 1.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_env_var_shell", false, 1.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_env_var_dollar", false, 1.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_env_var_question", false, 1.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_env_var_bang", false, 1.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_env_var_uscore", false, 1.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_wildcards1", false, 3.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_wildcards2", false, 1.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_wildcards3", false, 1.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_wildcards4", false, 1.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_wildcards5", false, 1.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_wildcards6", false, 1.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_wildcards7", false, 1.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_tilde", false, 2.0));
                projectService.addTestScript(new ProjectTestScriptModel(shell.getProjectID(), "test_robustness", false, 10.0));
            }

            if (!projectRepository.existsByName("Building a HTTP Server")) {
                Project server = projectService.addProject(new CourseProjectModel(course.getCourseID(), "Building a HTTP Server", LocalDate.of(2019, 3, 25), LocalDate.of(2019, 4, 8), "lab5-src", false));
                projectService.addProjectIgnoreUser(new ProjectIgnoreModel(server.getProjectID(), "cs252@cs.purdue.edu"));
            }

            */
        }
        catch(Exception e) {
            e.printStackTrace();
        }
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
