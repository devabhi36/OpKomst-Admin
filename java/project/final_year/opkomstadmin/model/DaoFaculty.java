package project.final_year.opkomstadmin.model;

public class DaoFaculty {
    String contact = "", department = "", email = "", hod = "", name = "", password = "", type = "", uniqueId = "", dpurl="";

    public DaoFaculty() {
    }

    public DaoFaculty(String contact, String department, String dpurl, String email, String hod, String name, String password, String type, String uniqueId) {
        this.contact = contact;
        this.department = department;
        this.dpurl = dpurl;
        this.email = email;
        this.hod = hod;
        this.name = name;
        this.password = password;
        this.type = type;
        this.uniqueId = uniqueId;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDpurl() {
        return dpurl;
    }

    public void setDpurl(String dpurl) {
        this.dpurl = dpurl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHod() {
        return hod;
    }

    public void setHod(String hod) {
        this.hod = hod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
