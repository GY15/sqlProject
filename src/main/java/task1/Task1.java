package task1;

import jxl.Sheet;
import jxl.Workbook;

import java.io.*;
import java.sql.*;
import java.util.HashMap;

public class Task1 {

    public static void main(String[] args) {
//        //2
        createTable();

        //3
        System.out.println("插入数据");
        long startTime = System.currentTimeMillis(); //获取结束时间
        insertTable();
        long endTime = System.currentTimeMillis(); //获取结束时间
        System.out.println("插入完成");
        System.out.println("程序运行时间： " + (endTime - startTime) * 1.0 / 1000 + "s");

        //4
        String sql ="SELECT a2.department \n" +
                "FROM allocate as a2\n" +
                "where a2.name in\n" +
                "(SELECT allocate.name\n" +
                "from allocate,(SELECT student.sex,student.department\n" +
                "FROM student\n" +
                "where student.`name`= \"王小星\")as a\n" +
                "WHERE a.department = allocate.department and a.sex = allocate.sex\n" +
                ")";
        ResultSet resultSet = exeSql(sql);

        //5
        System.out.println("开始更新");
        long startTime1 = System.currentTimeMillis(); //获取结束时间
        String sql1 ="UPDATE dormitory SET money = 1200 WHERE `name` = \"陶园1舍\" ";
        Connection conn = getConn();
        updateSql(sql1,conn);
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        long endTime1 = System.currentTimeMillis(); //获取结束时间
        System.out.println("更新完成");
        System.out.println("程序运行时间： " + (endTime1 - startTime1) * 1.0 / 1000 + "s");


        //6
        System.out.println("开始更新");
        long startTime2 = System.currentTimeMillis(); //获取结束时间
        changeDormitory();
        long endTime2 = System.currentTimeMillis(); //获取结束时间
        System.out.println("更新完成");
        System.out.println("程序运行时间： " + (endTime2 - startTime2) * 1.0 / 1000 + "s");
    }

    /**
     * test1
     *
     */

    /**
     * 2. 新建表
    * */
    private static void createTable() {
        System.out.println("连接到数据库");
        long startTime = System.currentTimeMillis();   //获取开始时间
        Connection conn =getConn();
        try {

            if (!validateTableExist("Student",conn)) {
                String sql = "create table Student (" +
                        "  ID varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL," +
                        "  name varchar(20)," +
                        "  sex varchar(2)," +
                        "  department varchar(30)," +
                        " PRIMARY KEY(ID)" +
                        ");";
                updateSql(sql,conn);
            }
            if (!validateTableExist("Dormitory",conn)) {
                String sql = "create table Dormitory (" +
                        "  name varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL," +
                        "  local varchar(20)," +
                        "  money int(6)," +
                        "  phone varchar(20)," +
                        " PRIMARY KEY(name)" +
                        ");";
                updateSql(sql,conn);
            }
            if (!validateTableExist("Allocate",conn)) {
                String sql = "create table Allocate (" +
                        "  sex varchar(2) NOT NULL," +
                        "  department varchar(20)," +
                        "  name varchar(20)," +
                        " PRIMARY KEY(sex,department)" +
                        ");";
                updateSql(sql,conn);
            }

            long endTime = System.currentTimeMillis(); //获取结束时间
            System.out.println("数据库创建成功");
            System.out.println("程序运行时间： " + (endTime - startTime) * 1.0 / 1000 + "s");
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
    * 查看表是否已经存在
    * */
    public static boolean validateTableExist(String tableName,Connection conn) {
        boolean flag = false;
        try {
            DatabaseMetaData meta = conn.getMetaData();
            String type[] = {"TABLE"};
            ResultSet rs = meta.getTables(null, null, tableName, type);
            flag = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }
    public static void updateSql(String sql,Connection ss) {

        try {
            Statement stmt = ss.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (Exception e) {
            System.out.println("创建失败或已经存在该表格");
            e.printStackTrace();
        }
    }
    public static void  insertTable() {
        HashMap<String, String> map = null;
        map = getAllPhone("src/main/resources/电话.txt");

        jxl.Workbook readwb = null;
        try {
            Connection conn = getConn();
            //构建Workbook对象, 只读Workbook对象
            //直接从本地文件创建Workbook
            InputStream instream = new FileInputStream("src/main/resources/分配方案.xls");
            readwb = Workbook.getWorkbook(instream);
            //获取Sheet表
            Sheet readsheet = readwb.getSheet(0);
            //获取Sheet表中所包含的总行数
            int rsRows = readsheet.getRows();
            //获取指定单元格的对象引用
            try {

                PreparedStatement pstmStudent = conn.prepareStatement("insert into Student (ID,name,Sex,department) values(?,?,?,?)");
                conn.setAutoCommit(false);

                for (int i = 1; i < rsRows; i++) {
                    String department = readsheet.getCell(0, i).getContents();
                    String ID = readsheet.getCell(1, i).getContents();
                    String sname = readsheet.getCell(2, i).getContents();
                    String sex = readsheet.getCell(3, i).getContents();
                    String local = readsheet.getCell(4, i).getContents();
                    String name = readsheet.getCell(5, i).getContents();
                    String money = readsheet.getCell(6, i).getContents();


                    pstmStudent.setString(1, ID);
                    pstmStudent.setString(2, sname);
                    pstmStudent.setString(3, sex);
                    pstmStudent.setString(4, department);
                    pstmStudent.addBatch();

                    insertDormitory(new Dormitory(name, local, money, map.get(name)), conn);
                    insertAllocate(new Allocate(sex, department, name), conn);
                }
                pstmStudent.executeBatch();
                conn.commit();
                pstmStudent.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println("数据已经存在,或者格式不正确");
                e.printStackTrace();
            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            readwb.close();

        }
    }






    private static HashMap<String,String> getAllPhone(String path) {
        HashMap<String,String> map = new HashMap<String, String>();
        File inputFile = new File(path);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(inputFile));
            String temp = null;
            while ((temp = br.readLine()) != null) {
                if (temp.split(";").length==2){
                    map.put(temp.split(";")[0],temp.split(";")[1]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 插入所有的数据
     * */
    private static int insertDormitory( Dormitory dormitory,Connection conn) {
        int i = 0;
        String sql = "insert into Dormitory (name,LOCAL ,money,phone) values(?,?,?,?)";
        Statement stmt =null;
        try {
            stmt= conn.createStatement();
            //插入记录到数据库中
            String name = dormitory.getName();
            name = "\""+name+"\"";
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM dormitory WHERE dormitory.name ="+ name);
            if (resultSet.next()){
                stmt.close();
                return 0;
            }
            stmt.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            PreparedStatement pstmt=conn.prepareStatement("insert into Dormitory (name,LOCAL ,money,phone) values(?,?,?,?)");
            pstmt.setString(1, dormitory.getName());
            pstmt.setString(2, dormitory.getLocal());
            pstmt.setString(3, dormitory.getMoney());
            pstmt.setString(4, dormitory.getPhone());
            i = pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

    private static int insertAllocate(Allocate allocate,Connection conn) {
        String sex =allocate.sex;
        String department = allocate.department;
        String name = allocate.name;
        int j = 0;
        String sql = "insert into allocate (sex,department,name) values(?,?,?)";
        Statement stmt=null;
        try {
            stmt = conn.createStatement();
            //插入记录到数据库中
            String s = "SELECT * FROM allocate WHERE sex ="+"\""+sex+"\""+" and department =" +  "\""+department+"\"";
            ResultSet resultSet = stmt.executeQuery(s);
            if (resultSet.next()){
                stmt.close();
                return 0;
            }
            stmt.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            PreparedStatement pstmt=conn.prepareStatement(sql);
            pstmt.setString(1, sex);
            pstmt.setString(2, department);
            pstmt.setString(3, name);
            j = pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return j;
    }


    private static void changeDormitory() {
        ResultSet resultSet = null;
        String male="",female="";
        Connection conn =getConn();
        try {
            //获取对象
            Statement stmt = conn.createStatement();
            //插入记录到数据库中
            long startTime = System.currentTimeMillis();   //获取开始时间
            resultSet = stmt.executeQuery("SELECT *\n" +
                    "from allocate \n" +
                    "WHERE department = \"软件学院\" ");
            while (resultSet.next()){
                if(resultSet.getString("sex").equals("男")){
                    male =resultSet.getString("name");
//                    System.out.println(male);
                }else{
                    female =resultSet.getString("name");
//                    System.out.println(female);
                }
            }
            String sql1 = "UPDATE allocate SET `name` = " +"\""+female+"\""+
                    "where allocate.department = \"软件学院\" and allocate.sex =\"男\"";
            updateSql(sql1,conn);
            String sql2 = "UPDATE allocate SET `name` = "  +"\""+male+"\""+
                    "where allocate.department = \"软件学院\" and allocate.sex =\"女\"";
            updateSql(sql2,conn);
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }







    public static ResultSet exeSql(String sql) {
        System.out.println("连接到数据库");
        Connection ss = getConn();
        ResultSet resultSet = null;
        try {
            //获取对象
            Statement stmt = ss.createStatement();
            //插入记录到数据库中
            long startTime = System.currentTimeMillis();   //获取开始时间
            resultSet = stmt.executeQuery(sql);
            long endTime = System.currentTimeMillis(); //获取结束时间
            System.out.println("查询成功");
            System.out.println("程序运行时间： " + (endTime - startTime) * 1.0 / 1000 + "s");
            while (resultSet.next()){
                System.out.println(resultSet.getString("department"));
            }
            stmt.close();
            ss.close();
        } catch (Exception e) {
            System.out.println("查询失败");
            e.printStackTrace();
        }
        return resultSet;
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
