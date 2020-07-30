package project.final_year.opkomstadmin.model;

public class Show_teacher_subjects {
    String subject_name, year_name, semester, class_held, assignments_given, dept_name;

    public Show_teacher_subjects() {
    }

    public Show_teacher_subjects(String subject_name, String year_name, String semester, String class_held, String assignments_given, String dept_name) {
        this.subject_name = subject_name;
        this.year_name = year_name;
        this.semester = semester;
        this.class_held = class_held;
        this.assignments_given = assignments_given;
        this.dept_name = dept_name;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public String getYear_name() {
        return year_name;
    }

    public void setYear_name(String year_name) {
        this.year_name = year_name;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getClass_held() {
        return class_held;
    }

    public void setClass_held(String class_held) {
        this.class_held = class_held;
    }

    public String getAssignments_given() {
        return assignments_given;
    }

    public void setAssignments_given(String assignments_given) {
        this.assignments_given = assignments_given;
    }

    public String getDept_name() {
        return dept_name;
    }

    public void setDept_name(String dept_name) {
        this.dept_name = dept_name;
    }
}
