package task1;

/**
 * Created by 61990 on 2017/10/30.
 */
public class Dormitory {
    String name;
    String local;
    String money;
    String phone;

    public String getName() {
        return name;
    }

    public String getLocal() {
        return local;
    }

    public String getMoney() {
        return money;
    }

    public String getPhone() {
        return phone;
    }

    public Dormitory(String name, String local, String money, String phone) {

        this.name = name;
        this.local = local;
        this.money = money;
        this.phone = phone;
    }
}
