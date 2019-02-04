package edu.purdue.cs.encourse.config;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.domain.relations.StudentProject;
import edu.purdue.cs.encourse.service.*;
import edu.purdue.cs.encourse.util.ConfigurationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Conditional(value = {ProdProfileCondition.class})
public class StartupFeed implements ApplicationListener<ApplicationReadyEvent> {

    /*@Autowired
    private AdminService adminService;

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentProjectRepository studentProjectRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeachingAssistantRepository teachingAssistantRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private HelperService helperService;*/
    
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        feedDatabase();
    }
    
    private void feedDatabase() {
        /*System.out.println("CONDITIONAL RAN");
        if (adminService.findAllUsers().isEmpty()) {
            adminService.addAccount("0", "grr", "Gustavo", "Rodriguez-Rivera", Account.Role_Names.PROFESSOR, "A", "grr@purdue.edu");
            adminService.addAccount("1", "kleclain-a", "Killian", "LeClainche", Account.Role_Names.ADMIN, "A", "kleclain@purdue.edu");
            adminService.addAccount("2", "reed226-a", "William", "Reed", Account.Role_Names.ADMIN, "J", "reed226@purdue.edu");

            adminService.addUser("grr", "$2a$04$/zamuN8nrPT0qZ4jbaTTp..kBjKUtMu.Jbj2DAHZ..KLDON4REPJu", "PROFESSOR", false, false, false, true);
            adminService.addUser("kleclain-admin", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", "ADMIN", false, false, false, true);
            adminService.addUser("reed226-admin", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", "ADMIN", false, false, false, true);

            Section section = adminService.addSection("1001", "Spring2019", "cs252", "Systems Programming", "All", "N/A");
            adminService.assignProfessorToCourse("grr", "cs252", "Spring2019");

            try {
                BufferedReader fileReader = new BufferedReader(new FileReader("/sourcecontrol/cs252/Spring2019/students.txt"));
                String student = null;
                int count = 1;
                while ((student = fileReader.readLine()) != null && count <= ConfigurationManager.getInstance().limit) {
                    //adminService.addAccount(Integer.toString(100 + count), student, "Student", Integer.toString(count),
                    adminService.addAccount(Integer.toString(100 + count), student, "Student", student,
                            Account.Role_Names.STUDENT, null, student + "@purdue.edu");
                    adminService.registerStudentToSection(student, section.getSectionID());
                    count++;
                }
                fileReader.close();
                BufferedReader taReader = new BufferedReader(new FileReader("src/taconfig.txt"));
                BufferedReader studentReader = new BufferedReader(new FileReader("src/stuconfig.txt"));
                ArrayList<Account> teachingAssistants = new ArrayList<>();
                String input;
                count = 0;
                while ((input = taReader.readLine()) != null) {
                    String[] lab = input.split(" ");
                    if(!lab[0].equals("Start")) {
                        continue;
                    }
                    teachingAssistants.clear();
                    String[] info;
                    while (!(info = taReader.readLine().split(" "))[0].equals("End")) {
                        adminService.addAccount(Integer.toString(count + 1000), info[0] + "-lab" + lab[2], info[1], info[2], Account.Role_Names.TA, null, info[0] + "@purdue.edu");
                        adminService.addUser(info[0] + "-lab" + lab[2], lab[3], "TA", false, false, false, true);
                        adminService.assignTeachingAssistantToCourse(info[0] + "-lab" + lab[2], "cs252", "Spring2019");
                        professorService.assignTeachingAssistantToSection(info[0] + "-lab" + lab[2], section.getSectionID());
                        teachingAssistants.add(teachingAssistantRepository.findByUserName(info[0] + "-lab" + lab[2]));
                        count++;
                    }
                    while ((input = studentReader.readLine()) != null) {
                        String[] roster = input.split(" ");
                        if(!roster[0].equals("Start")) {
                            continue;
                        }
                        while ((input = studentReader.readLine()) != null) {
                            roster = input.split(" ");
                            if(roster[0].equals("End")) {
                                break;
                            }
                            roster = input.split(",");
                            for(Account ta : teachingAssistants) {
                                professorService.assignTeachingAssistantToStudentInSection(ta.getUserName(), roster[0], section.getSectionID());
                            }
                        }
                        break;
                    }
                }
                taReader.close();
                studentReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            courseService.setSectionRemotePaths("Spring2019", "cs252", "/homes/cs252/sourcecontrol/work");
            courseService.setDirectory("Spring2019", "cs252");
            Project malloc = professorService.addProject("cs252", "Spring2019", "MyMalloc", "lab1-src", "1/8/2019", "1/28/2019", 0);
            Project bash = professorService.addProject("cs252", "Spring2019", "Shell Scripting", "lab2-src", "1/29/2019", "2/11/2019", 0);
            //Project shell = professorService.addProject("cs252", "Spring2019", "Shell", "lab3-src", "9/24/2018", "10/8/2018", 0);

            //malloc.setMaximumRuntime(35000);
            //malloc = projectRepository.save(malloc);
            //addMallocTestScripts(malloc);
            //professorService.runHistoricTestall(malloc.getProjectID());

            professorService.assignProject(malloc.getProjectID());
            professorService.assignProject(bash.getProjectID());
            List<StudentProject> projects = studentProjectRepository.findByIdProjectID(malloc.getProjectID());
            for (StudentProject p : projects) {
                Student student = studentRepository.findByUserID(p.getStudentID());
                helperService.updateStudentInformation(p.getProjectID(), student.getUserName());
            }
        }
        else {
            for(Project p : projectRepository.findAll()) {
                p.setTestRate(ConfigurationManager.getInstance().rate);
                p.setTestCount(0);
                projectRepository.save(p);
                List<StudentProject> studentProjects = studentProjectRepository.findByIdProjectID(p.getProjectID());
                for(StudentProject s : studentProjects) {
                    Student student = studentRepository.findByUserID(s.getStudentID());
                    helperService.updateStudentInformation(s.getProjectID(), student.getUserName());
                }
            }
        }*/
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
