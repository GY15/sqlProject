package task2;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 61990 on 2017/10/31.
 */
public class Record {
    String bikeID;
    String userID;
    String startPoint;
    Date startTime;
    String endPoint;
    Date endTime;

    public Record(String userID,String bikeID, String startPoint, String startTime, String endPoint, String endTime) {
        this.bikeID = bikeID;
        this.userID = userID;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
        try {
            this.startTime =sdf.parse(startTime);
            this.endTime =sdf.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public String getBikeID() {
        return bikeID;
    }

    public String getUserID() {
        return userID;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public Date getStartTime() {
        return startTime;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public Date getEndTime() {
        return endTime;
    }
}
