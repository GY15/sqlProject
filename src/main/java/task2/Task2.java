package task2;

import jxl.Sheet;
import jxl.Workbook;
import task1.Dormitory;
import task1.Mysqlread;
import task1.Student;
import task1.Task1;

import java.io.*;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

public class Task2 {

    public static void main(String[] args) {
        //1
        long startTime = System.currentTimeMillis(); //获取结束时间
        createTable();
        System.out.println("创建完成");

        System.out.println("插入数据");
        insertTable();
        long endTime = System.currentTimeMillis(); //获取结束时间
        System.out.println("插入完成");
        System.out.println("程序运行时间： " + (endTime - startTime) * 1.0 / 1000 + "s");
//
//        //2 添加HOME
        startTime = System.currentTimeMillis(); //获取结束时间
        try {
            System.out.println("创建home");
            Connection conn = getConn();
            Task1.updateSql("alter table usermessage add column home varchar(30);",conn);
            addHome(conn);
            System.out.println("插入home");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        endTime = System.currentTimeMillis(); //获取结束时间
        System.out.println("插入完成");
        System.out.println("程序运行时间： " + (endTime - startTime) * 1.0 / 1000 + "s");

        startTime = System.currentTimeMillis();
        try {
            System.out.println("创建是否欠款");
            Connection conn = getConn();
            Task1.updateSql("alter table usermessage add column arrearage bit(1) DEFAULT b'0';",conn);
            System.out.println("插入是否欠款");
            updateMoney(conn);
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        endTime = System.currentTimeMillis(); //获取结束时间
        System.out.println("插入完成");
        System.out.println("程序运行时间： " + (endTime - startTime) * 1.0 / 1000 + "s");



//
        startTime = System.currentTimeMillis(); //获取结束时间
        try {
            System.out.println("创建维修表");
            Connection conn = getConn();

            updateBike(conn);
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        endTime = System.currentTimeMillis(); //获取结束时间
        System.out.println("完成");
        System.out.println("程序运行时间： " + (endTime - startTime) * 1.0 / 1000 + "s");


        //创建任务计划

        //
        startTime = System.currentTimeMillis(); //获取结束时间
        try {
            System.out.println("创建event");
            Connection conn = getConn();
//            Task1.updateSql("DROP EVENT cleanBike",conn);
            System.out.println("开始创建任务计划");
            String sql ="CREATE EVENT cleanBike\n" +
                    "ON SCHEDULE EVERY 1 month\n" +
                    "STARTS TIMESTAMP '2017-11-1 00:00:00'\n" +
                    "DO truncate table maintain;";
            Task1.updateSql(sql,conn);

//            Task1.updateSql("DROP EVENT cleanBan",conn);
             sql ="CREATE EVENT cleanBan\n" +
                    "ON SCHEDULE EVERY 1 month\n" +
                    "STARTS TIMESTAMP '2017-11-1 00:00:07'\n" +
                    "DO Update bike set forbidden = b'0';\n";
            Task1.updateSql(sql,conn);

//            Task1.updateSql("DROP EVENT updateBike;",conn);
            sql ="CREATE EVENT updateBike\n" +
                    "ON SCHEDULE EVERY 1 month\n" +
                    "STARTS TIMESTAMP '2017-11-1 12:00:15'\n" +
                    "DO INSERT INTO maintain(bikeID,endPoint)\n" +
                    "SELECT record.bikeID,endPoint\n" +
                    "FROM record,\n" +
                    "(SELECT bikeID,MAX(endTime)as endTime\n" +
                    "FROM record\n" +
                    "WHERE DATE_FORMAT(record.endTime,\"%Y %m\") = DATE_FORMAT(DATE_SUB(NOW(),interval 1 month),\"%Y %m\")\n" +
                    "GROUP BY bikeID) as t1,\n" +
                    "(SELECT bikeID,sum(endTime-startTime)as timeSum\n" +
                    "FROM record\n" +
                    "WHERE DATE_FORMAT(record.endTime,\"%Y %m\") = DATE_FORMAT(DATE_SUB(NOW(),interval 1 month),\"%Y %m\")\n" +
                    "GROUP BY bikeID) as t2\n" +
                    "WHERE t1.endTime = record.endTime and record.bikeID = t1.bikeID and t2.bikeID = record.bikeID\n" +
                    "and t2.timeSum>8080000;;\n";
            Task1.updateSql(sql,conn);

//            Task1.updateSql("DROP EVENT banBike",conn);
            sql ="CREATE EVENT banBike\n" +
                    "ON SCHEDULE EVERY 1 month\n" +
                    "STARTS TIMESTAMP '2017-11-1 00:01:00'\n" +
                    "DO truncate table maintain;";
            Task1.updateSql(sql,conn);
            System.out.println("结束创建任务计划");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        endTime = System.currentTimeMillis(); //获取结束时间
        System.out.println("完成");
        System.out.println("程序运行时间： " + (endTime - startTime) * 1.0 / 1000 + "s");


    }



    /**
     * test1
     *
     */

    /**
     * 新建表
    * */
    private static void createTable() {
        System.out.println("连接到数据库");
        Connection conn  = getConn();
        try {
            if (!Task1.validateTableExist("userMessage", conn)) {
                String sql = "create table userMessage (" +
                        "  userID varchar(20)  NOT NULL," +
                        "  userName varchar(20)," +
                        "  phone varchar(12)," +
                        "  credit float(10,2) ," +
                        "  PRIMARY KEY(userID)" +
                        ")";
                Task1.updateSql(sql, conn);
            }
            if (!Task1.validateTableExist("record", conn)) {
                String sql = "create table record (" +
                        "userID varchar(20) NOT NULL," +
                        "  bikeID varchar(20) NOT NULL," +
                        "  startPoint varchar(20)," +
                        "  startTime datetime(0) NOT NULL," +
                        " endPoint varchar(20)," +
                        " endTime datetime(0) NOT NULL," +
                        " primary KEY(bikeID,userID)" +
                        ")";
                Task1.updateSql(sql, conn);
            }
            if (!Task1.validateTableExist("bike", conn)) {
                String sql = "create table bike (" +
                        "bikeID varchar(20) NOT NULL," +
                        "forbidden bit(1)  DEFAULT b'0'," +
                        "primary KEY(bikeID)"+
                        ")";
                Task1.updateSql(sql, conn);
            }

            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    /**
     * 插入所有的数据
     * */
    public static void  insertTable(){


        try {
            Connection conn = getConn();
            insertUser(conn);
            insertBike(conn);
            insertRecord(conn);

            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }


    }
    private static int insertUser(Connection conn) {
        String sql = "insert into usermessage (userID,userName,phone,credit) values(?,?,?,?)";
        try {
            BufferedReader br1 = null;
            File inputFile = new File("src/main/resources/user.txt");
            br1 = new BufferedReader(new FileReader(inputFile));
            PreparedStatement pstmt = conn.prepareStatement(sql);
            conn.setAutoCommit(false);
            String temp = null;
            while ((temp = br1.readLine()) != null) {
                String [] message = temp.split(";");
                User user = new User(message[0],message[1],message[2],Double.parseDouble(message[3]));
                pstmt.setString(1, user.getID());
                pstmt.setString(2, user.getUserName());
                pstmt.setString(3, user.getPhone());
                pstmt.setDouble(4, user.getCredit());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            pstmt.close();
            conn.commit();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int insertBike(Connection conn) {

        int i = 0;
        String sql = "insert into bike (bikeID) values(?)";

        try {
            BufferedReader br1 = null;
            File inputFile = new File("src/main/resources/bike.txt");
            br1 = new BufferedReader(new FileReader(inputFile));
            PreparedStatement pstmt = conn.prepareStatement(sql);
            conn.setAutoCommit(false);
            String temp= null;
            while ((temp = br1.readLine()) != null) {
                pstmt.setString(1, temp);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            pstmt.close();
            conn.commit();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int insertRecord(Connection conn) {
        String sql = "insert into record (userID,bikeID,startPoint,startTime,endPoint,endTime) values(?,?,?,?,?,?)";
        Statement stmt=null;
        try {
            BufferedReader br2 = null;
            File inputFile = new File("src/main/resources/record.txt");
            br2 = new BufferedReader(new FileReader(inputFile));
            String temp =null;
            PreparedStatement pstmt = conn.prepareStatement(sql);
            conn.setAutoCommit(false);
            while ((temp = br2.readLine()) != null) {
                String[] message = temp.split(";");
                Record record = new Record(message[0], message[1], message[2], message[3], message[4], message[5]);
                pstmt.setString(1, record.getUserID());
                pstmt.setString(2, record.getBikeID());
                pstmt.setString(3, record.getStartPoint());
                pstmt.setTimestamp(4, new java.sql.Timestamp(record.getStartTime().getTime()));
                pstmt.setString(5, record.getEndPoint());
                pstmt.setTimestamp(6, new java.sql.Timestamp(record.getEndTime().getTime()));
                pstmt.addBatch();
            }
             pstmt.executeBatch();
            pstmt.close();
            conn.commit();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static void addHome(Connection conn){

        try {
            boolean autoCommit=conn.getAutoCommit();
            //关闭自动提交
            conn.setAutoCommit(false);
            //获取对象
            Statement stmt = conn.createStatement();

            String sql ="SELECT  t.userID,t.endPoint,max(t.num)" +
                    "from(" +
                    "SELECT userID, endPoint ,COUNT(*)as num " +
                    "FROM record " +
                    "WHERE DATE_FORMAT(endTime,\"%H:%i:%s\") BETWEEN \"18:00:00\" and \"23:59:59\" " +
                    "GROUP BY userID, endPoint) as t " +
                    "GROUP BY t.userID,t.endPoint;";
            ResultSet resultSet = stmt.executeQuery(sql);
            String update = "UPDATE usermessage SET home = (?) WHERE userID = (?)";
            PreparedStatement pstm = conn.prepareStatement(update);

            while (resultSet.next()){
                pstm.setString(2, resultSet.getString("userID"));
                pstm.setString(1,  resultSet.getString("endPoint"));
                pstm.addBatch();
            }
            //同时提交所有的sql语句
            pstm.executeBatch();
            //提交修改
            conn.commit();
            conn.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateMoney(Connection conn){
        try {
            boolean autoCommit=conn.getAutoCommit();
            //关闭自动提交
            conn.setAutoCommit(false);
            //获取对象
            Statement stmt = conn.createStatement();

            String sql ="SELECT m.userID,SUM(m.money) as money\n" +
                    "FROM\n" +
                    "(SELECT t.userID,IF(t.time<3000,1,IF(t.time<10000,2,IF(t.time<13000,3,4)))as money\n" +
                    "FROM(\n" +
                    "SELECT userID,(endTime-startTime) as time\n" +
                    "FROM record ) as t\n" +
                    ")as m\n" +
                    "GROUP BY m.userID";
            ResultSet resultSet = stmt.executeQuery(sql);
            String update = "UPDATE usermessage SET credit = credit-(?) WHERE userID = (?)";
            PreparedStatement pstm = conn.prepareStatement(update);
            while (resultSet.next()){
                pstm.setString(2, resultSet.getString("userID"));
                pstm.setDouble(1,  resultSet.getDouble("money"));
                pstm.addBatch();
            }
            //同时提交所有的sql语句
            pstm.executeBatch();
            //提交修改

            //判断是否已经欠费
            sql = "UPDATE usermessage SET arrearage = IF(credit>0, 0, 1)";
            Task1.updateSql(sql,conn);
            conn.commit();
            conn.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateBike(Connection conn) {
        try {
            boolean autoCommit=conn.getAutoCommit();
            //关闭自动提交
            conn.setAutoCommit(false);
            //获取对象
            Statement stmt = conn.createStatement();

            String sql ="SELECT record.bikeID,t2.timeSum,endPoint\n" +
                    "FROM record,\n" +
                    "(SELECT bikeID,MAX(endTime)as endTime\n" +
                    "FROM record\n" +
                    "WHERE DATE_FORMAT(record.endTime,\"%Y %m\") = DATE_FORMAT(DATE_SUB(NOW(),interval 1 month),\"%Y %m\")\n" +
                    "GROUP BY bikeID) as t1,\n" +
                    "(SELECT bikeID,sum(endTime-startTime)as timeSum\n" +
                    "FROM record\n" +
                    "WHERE DATE_FORMAT(record.endTime,\"%Y %m\") = DATE_FORMAT(DATE_SUB(NOW(),interval 1 month),\"%Y %m\")\n" +
                    "GROUP BY bikeID) as t2\n" +
                    "WHERE t1.endTime = record.endTime and record.bikeID = t1.bikeID and t2.bikeID = record.bikeID\n" +
                    "and t2.timeSum>8080000\n";

            if (!Task1.validateTableExist("maintain", conn)) {
                String ss1 = "create table maintain (" +
                        "bikeID varchar(20) NOT NULL," +
                        "endPoint varchar(20) DEFAULT NULL," +
                        "primary KEY(bikeID)"+
                        ")";
                Task1.updateSql(ss1, conn);
            }else{
                Task1.updateSql("truncate table maintain;",conn);
            }
            Task1.updateSql("Update bike set forbidden = b'0'", conn);
            ResultSet resultSet = stmt.executeQuery(sql);
            String update = "insert into maintain (bikeID,endPoint) values(?,?)";
            PreparedStatement pstm = conn.prepareStatement(update);
            while (resultSet.next()){

                pstm.setString(1, resultSet.getString("bikeID"));
                pstm.setString(2, resultSet.getString("endPoint"));
                pstm.addBatch();
            }
            //同时提交所有的sql语句
            pstm.executeBatch();
            //提交修改

            conn.commit();
            conn.setAutoCommit(autoCommit);
            String ss2 = "UPDATE bike set bike.forbidden = b'1' WHERE bike.bikeID in (SELECT maintain.bikeID from maintain);";
            Task1.updateSql(ss2,conn);


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static Connection getConn() {
        Connection ss = null;
        try {
            Class.forName(Mysqlread.message[0]);
            //链接到数据库
            ss = DriverManager.getConnection(Mysqlread.message[1], Mysqlread.message[2], Mysqlread.message[3]);
            //获取对象

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ss;
    }

}
