package task1;

/**
 * Created by 61990 on 2017/10/30.
 */
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
public class Mysqlread {
    public static final String [] message=readurl();
    private static String[] readurl() {
        Properties prop=new Properties();
        String [] message=new String[4];
        try {
            InputStream in=new BufferedInputStream(new FileInputStream("src/main/resources/jdbc.properties"));
            prop.load(in);
            message[0]=prop.getProperty("driver");
            message[1]=prop.getProperty("url");
            message[2]=prop.getProperty("username");
            message[3]=prop.getProperty("password");
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return message;
    }
}
