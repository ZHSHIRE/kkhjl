//import com.oscar.Driver;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//
//public class OSTest {
//    public static void main(String[] args)  throws Exception{
//        Class.forName(Driver.class.getName());
//        String url = "jdbc:oscar://192.168.10.115:2003/SEEYON";
//        String user = "V80SP2";
//        String password = "Seeyon123456";
//        Connection connection = DriverManager.getConnection(url,user,password);
//
//        String sql = " SELECT * FROM V80SP2.DEDUCT_DATA_1CD53EA959441D9  WHERE  field0009=? ";
//        PreparedStatement statement =  connection.prepareStatement (sql);
//        statement.setObject(1,"1814259818030320862");
//        ResultSet set =  statement.executeQuery();
//        while (set.next()){
//            System.out.println(set.getBigDecimal(1));
//        }
//    }
//
//}
