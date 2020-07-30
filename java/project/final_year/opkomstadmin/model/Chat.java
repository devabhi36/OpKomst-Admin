package project.final_year.opkomstadmin.model;

public class Chat {
    private String message, user, userId, date, time, type, key;

    public Chat() {
    }

    public Chat(String message, String user, String userId, String date, String time, String type, String key) {
        this.message = message;
        this.user = user;
        this.userId = userId;
        this.date = date;
        this.time = time;
        this.type = type;
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
