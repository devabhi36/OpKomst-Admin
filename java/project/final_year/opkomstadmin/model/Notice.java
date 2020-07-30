package project.final_year.opkomstadmin.model;

public class Notice {
    String name, date, time, url;

    public Notice() {
    }

    public Notice(String name, String date, String time, String url) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
