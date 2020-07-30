package project.final_year.opkomstadmin.model;

public class DaoStudent {
    String name, roll, department, email, contact, semester, year, lateral, enrollmentNo, dpurl;

   public DaoStudent() {
    }

    public DaoStudent(String name, String roll, String department, String email, String contact, String semester, String year, String lateral, String enrollmentNo, String dpurl) {
        this.name = name;
        this.roll = roll;
        this.department = department;
        this.email = email;
        this.contact = contact;
        this.semester = semester;
        this.year = year;
        this.lateral = lateral;
        this.enrollmentNo = enrollmentNo;
        this.dpurl = dpurl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getLateral() {
        return lateral;
    }

    public void setLateral(String lateral) {
        this.lateral = lateral;
    }

    public String getEnrollmentNo() {
        return enrollmentNo;
    }

    public void setEnrollmentNo(String enrollmentNo) {
        this.enrollmentNo = enrollmentNo;
    }

    public String getDpurl() {
        return dpurl;
    }

    public void setDpurl(String dpurl) {
        this.dpurl = dpurl;
    }
}
