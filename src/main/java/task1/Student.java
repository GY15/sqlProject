package task1;

/**
 * Created by 61990 on 2017/10/30.
 */
public class Student {
    private String Id;

    public String getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public String getSex() {
        return Sex;
    }

    public String getDepartment() {
        return department;
    }

    public Student(String id, String name, String sex, String department) {

        Id = id;
        Name = name;
        Sex = sex;
        this.department = department;
    }

    private String Name;
    private String Sex;
    private String department;
}
