package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.ProjectRepository;
import edu.purdue.cs.encourse.database.SectionRepository;
import edu.purdue.cs.encourse.database.StudentProjectRepository;
import edu.purdue.cs.encourse.database.StudentRepository;
import edu.purdue.cs.encourse.database.StudentSectionRepository;
import edu.purdue.cs.encourse.database.TeachingAssistantRepository;
import edu.purdue.cs.encourse.database.TeachingAssistantStudentRepository;
import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.domain.Section;
import edu.purdue.cs.encourse.domain.Student;
import edu.purdue.cs.encourse.domain.relations.StudentSection;
import edu.purdue.cs.encourse.exceptions.BadRequestException;
import edu.purdue.cs.encourse.exceptions.ParameterEmptyException;
import edu.purdue.cs.encourse.exceptions.BadQueryException;
import edu.purdue.cs.encourse.exceptions.EncourseException;
import edu.purdue.cs.encourse.service.CourseService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by Killian Le Clainche on 9/26/2018.
 */
@Service(value = CourseServiceImpl.NAME)
public class CourseServiceImpl implements CourseService {
	
	final static String NAME = "CourseService";
	
	private final StudentRepository studentRepository;
	
	private final TeachingAssistantRepository teachingAssistantRepository;
	
	private final ProjectRepository projectRepository;
	
	private final SectionRepository sectionRepository;
	
	private final StudentSectionRepository studentSectionRepository;
	
	private final StudentProjectRepository studentProjectRepository;
	
	private final TeachingAssistantStudentRepository teachingAssistantStudentRepository;
	
	@Autowired
	public CourseServiceImpl(StudentRepository studentRepository, TeachingAssistantRepository teachingAssistantRepository,
	                         ProjectRepository projectRepository, SectionRepository sectionRepository, StudentSectionRepository studentSectionRepository,
	                         StudentProjectRepository studentProjectRepository, TeachingAssistantStudentRepository teachingAssistantStudentRepository) {
		
		this.studentRepository = studentRepository;
		this.teachingAssistantRepository = teachingAssistantRepository;
		this.projectRepository = projectRepository;
		this.sectionRepository = sectionRepository;
		this.studentSectionRepository = studentSectionRepository;
		this.studentProjectRepository = studentProjectRepository;
		this.teachingAssistantStudentRepository = teachingAssistantStudentRepository;
	}
	
	private void validateParameter(@NotNull final String parameterName, @NotNull final String parameter) throws ParameterEmptyException {
		if(parameter == null || parameter.length() == 0) throw new ParameterEmptyException(parameterName);
	}
	
	private List<Section> getSectionsByCourseID(@NotNull final String courseID) throws EncourseException {
		validateParameter("courseID", courseID);
		
		List<Section> sections = sectionRepository.findByCourseID(courseID);
		
		if(sections == null || sections.isEmpty()) throw new BadQueryException.NoCourseWithIDException(courseID);
		
		return sections;
	}
	
	private Project getProjectByProjectID(@NotNull final String projectID) throws EncourseException {
		validateParameter("projectID", projectID);
		
		Project project = projectRepository.findByProjectIdentifier(projectID);
		
		if(project == null) throw new BadQueryException.NoProjectWithIDException(projectID);
		
		return project;
	}
	
	private void executeBashScript(@NotNull final String cmd) throws EncourseException {
		try {
			Process process = Runtime.getRuntime().exec("./src/main/bash/" + cmd);
			
			StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
			
			Executors.newSingleThreadExecutor().submit(streamGobbler);
			
			process.waitFor();
			
		}
		catch(Exception e) {
			throw new EncourseException(e.getMessage());
		}
	}
	
	/** Mainly needed to populate the database when course hub already exists **/
	public void setDirectory(@NotNull final String courseID) throws EncourseException {
		
		List<Section> sections = getSectionsByCourseID(courseID);
		
		String courseDirectory = "/sourcecontrol/" + courseID + "/" + sections.get(0).getSemester();
		
		for(Section s : sections) {
			if(s == null) continue;
			
			s.setCourseHub(courseDirectory);
			sectionRepository.save(s);
		}
	}
	
	/** Creates a directory containing a directory for every student in the course. Each of those student directories
	 will contain all of the cloned repositories that is being used to track git information **/
	// TODO: Add Semester as a parameter
	public void createDirectory(@NotNull final String courseID) throws EncourseException {
		
		List<Section> sections = getSectionsByCourseID(courseID);
		
		String courseDirectory = "/sourcecontrol/" + courseID + "/" + sections.get(0).getSemester();
		
		if(!(new File(courseDirectory)).mkdirs()) throw new EncourseException("Could not make directories for: " + courseDirectory);
		
		StringBuilder listOfBadFolders = new StringBuilder();
		
		for(Section s : sections) {
			if(s == null) continue;
			
			s.setCourseHub(courseDirectory);
			sectionRepository.save(s);
			
			List<StudentSection> assignments = studentSectionRepository.findByIdSectionIdentifier(s.getSectionIdentifier());
			
			if(assignments == null) continue;
			
			for(StudentSection a : assignments){
				Student student = studentRepository.findByUserID(a.getStudentID());
				
				if(!(new File(courseDirectory + "/" + student.getUserName())).mkdir()) listOfBadFolders.append(student.getUserID()).append(" ");
			}
		}
		
		if(listOfBadFolders.length() > 0) throw new EncourseException("Could not make directories for students: " + listOfBadFolders);
	}
	
	/** Adds a new project to the database, which needs to be done before cloning the project in the course hub **/
	public void createProject(@NotNull final String courseID, @NotNull final String semester,
	                          @NotNull final String projectName, @NotNull final String repositoryName,
	                          @NotNull final String startDate, @NotNull final String dueDate) throws EncourseException {
		
		validateParameter("courseID", courseID);
		validateParameter("semester", semester);
		validateParameter("projectName", projectName);
		validateParameter("repositoryName", repositoryName);
		validateParameter("startDate", startDate);
		validateParameter("dueDate", dueDate);
		
		Project project = new Project(courseID, semester, projectName, repositoryName, startDate, dueDate);
		
		if(projectRepository.existsByProjectIdentifier(project.getProjectIdentifier())) throw new BadQueryException.ProjectExistsException(project.getProjectIdentifier());
		
		projectRepository.save(project);
	}
	
	/** Modifies project information like start and end dates **/
	public void updateProject(@NotNull final String projectID, @NotNull final String field, @NotNull final String value) throws EncourseException {
		validateParameter("field", field);
		validateParameter("value", value);
		
		Project project = getProjectByProjectID(projectID);
		
		switch(field.toLowerCase()) {
			case "startdate": project.setStartDate(value); break;
			case "duedate": project.setDueDate(value); break;
			case "reponame": project.setRepoName(value); break;
			default: throw new BadRequestException("No editable field with key: (" + field + ") exists in project.");
		}
		
		projectRepository.save(project);
	}
	
	/** Sets the location of the git repositories for every section under a particular courseID **/
	public void setRemotePath(@NotNull final String courseID, @NotNull final String remotePath) throws EncourseException {
		validateParameter("remotePath", remotePath);
		
		List<Section> sections = getSectionsByCourseID(courseID);

		for(Section s : sections) {
			if(s == null) continue;
			
			s.setRemotePath(remotePath);
			sectionRepository.save(s);
		}
	}
	
	/** Runs a bash script to initially clone every student's git repository. Each university should supply its own
	 bash script, since repo locations will vary **/
	// TODO: Add Semester as a parameter
	public void cloneProjectsFromRemote(@NotNull final String courseID, @NotNull final String projectID) throws EncourseException {
		List<Section> sections = getSectionsByCourseID(courseID);
		Project project = getProjectByProjectID(projectID);
		
		//better approach here
		Section section = sections.get(0);
		
		if(section.getCourseHub() == null) throw new BadRequestException("Section directory must be specified.");
		if(section.getRemotePath() == null) throw new BadRequestException("Section remote path must be specified.");
		if(project.getRepoName() == null) throw new BadRequestException("Project repository name must be specified.");
		
		for(Section s : sections) {
			if(s == null) continue;
			
			List<StudentSection> assignments = studentSectionRepository.findByIdSectionIdentifier(s.getSectionIdentifier());
			
			if(assignments == null) continue;
			
			for(StudentSection a : assignments){
				Student student = studentRepository.findByUserID(a.getStudentID());
				
				if(student == null) continue;
				
				if(!(new File(s.getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName()).exists())) {
					String destPath = (s.getCourseHub() + "/" + student.getUserName());
					String repoPath = (s.getRemotePath() + "/" + student.getUserName() + "/" + project.getRepoName() + ".git");
					
					executeBashScript("cloneRepositories.sh " + destPath + " " + repoPath);
				}
			}
		}
		
		executeBashScript("setPermissions.sh " + section.getCourseID());
	}
	
	/** Pulls the designated project within every students directory under the course hub **/
	// TODO: Add Semester as a parameter
	public void pullProjectsFromRemote(@NotNull final String courseID, @NotNull final String projectID) throws Exception {
		List<Section> sections = getSectionsByCourseID(courseID);
		Project project = getProjectByProjectID(projectID);
		
		Section section = sections.get(0);
		
		if(section.getCourseHub() == null) throw new BadRequestException("Section directory must be specified.");
		if(section.getRemotePath() == null) throw new BadRequestException("Section remote path must be specified.");
		if(project.getRepoName() == null) throw new BadRequestException("Project repository name must be specified.");
		
		List<String> completedStudents = new ArrayList<>();
		
		for(Section s : sections) {
			List<StudentSection> assignments = studentSectionRepository.findByIdSectionIdentifier(s.getSectionIdentifier());
			
			if(assignments == null) continue;
			
			for(StudentSection a : assignments) {
				Student student = studentRepository.findByUserID(a.getStudentID());
				
				if(student == null) continue;
				
				if(!(completedStudents.contains(student.getUserName()))) {
					String destPath = (section.getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
					
					executeBashScript("pullRepositories.sh " + destPath);
					
					completedStudents.add(student.getUserName());
				}
			}
		}
		
		executeBashScript("setPermissions.sh " + section.getCourseID());
	}
	
	@Override
	public @NotNull JSONObject getStudentsProjectInfoByCourse(@NotNull final String courseID, @NotNull final String projectID) {
		return null;
	}
	
}
