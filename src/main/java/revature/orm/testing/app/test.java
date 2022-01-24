package revature.orm.testing.app;

import revature.orm.entitymanager.DBTable;
import revature.orm.testing.models.Student;

import java.sql.ResultSet;
import java.sql.SQLException;

public class test {
    public static void main(String[] args) throws SQLException {
        DBTable<Student> studentDB = new DBTable<>(Student.class);
        //studentDB.createTable();
        ResultSet rs = studentDB.get("age>11","lastname like '%a%'");
        while(rs.next()){
            System.out.println(rs.getString("lastname"));
        }

    }
}
