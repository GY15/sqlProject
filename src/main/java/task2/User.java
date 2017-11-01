package task2;

/**
 * Created by 61990 on 2017/10/31.
 */
public class User {
    private String ID;
    private String userName;
    private String phone;
    private Double credit;

    public User(String ID, String userName, String phone, Double credit) {
        this.ID=ID;
        this.userName = userName;
        this.phone = phone;
        this.credit = credit;
    }

    public String getID() {
        return ID;
    }

    public String getUserName() {
        return userName;
    }

    public String getPhone() {
        return phone;
    }

    public Double getCredit() {
        return credit;
    }
}
