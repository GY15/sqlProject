package task1;

/**
 * Created by 61990 on 2017/10/30.
 */
public class Allocate {
    String sex;
    String department;
    String name;

    public Allocate(String sex, String department, String name) {
        this.sex = sex;
        this.department = department;
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public String getDepartment() {
        return department;
    }

    public String getName() {
        return name;
    }
}
