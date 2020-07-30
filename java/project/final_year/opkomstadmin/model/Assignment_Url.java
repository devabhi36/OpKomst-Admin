package project.final_year.opkomstadmin.model;

public class Assignment_Url {
    String rollNo, assignmentUrl;

    public Assignment_Url() {
    }

    public Assignment_Url(String rollNo, String assignmentUrl) {
        this.rollNo = rollNo;
        this.assignmentUrl = assignmentUrl;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getAssignmentUrl() {
        return assignmentUrl;
    }

    public void setAssignmentUrl(String assignmentUrl) {
        this.assignmentUrl = assignmentUrl;
    }
}
