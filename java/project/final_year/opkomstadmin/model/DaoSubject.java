package project.final_year.opkomstadmin.model;

public class DaoSubject {
    String code, department, departmentsem, faculty, noofassignments, noofclasses, semester, subjectname, year;

    public DaoSubject(){}

    public DaoSubject(String code, String department, String departmentsem, String faculty, String noofassignments, String noofclasses, String semester, String subjectname, String year) {
        this.code = code;
        this.department = department;
        this.departmentsem = departmentsem;
        this.faculty = faculty;
        this.noofassignments = noofassignments;
        this.noofclasses = noofclasses;
        this.semester = semester;
        this.subjectname = subjectname;
        this.year = year;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDepartmentsem() {
        return departmentsem;
    }

    public void setDepartmentsem(String departmentsem) {
        this.departmentsem = departmentsem;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getNoofassignments() {
        return noofassignments;
    }

    public void setNoofassignments(String noofassignments) {
        this.noofassignments = noofassignments;
    }

    public String getNoofclasses() {
        return noofclasses;
    }

    public void setNoofclasses(String noofclasses) {
        this.noofclasses = noofclasses;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getSubjectname() {
        return subjectname;
    }

    public void setSubjectname(String subjectname) {
        this.subjectname = subjectname;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
