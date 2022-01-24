package revature.orm.testing.app;

import revature.orm.entitymanager.DBTable;
import revature.orm.testing.models.Student;

import java.sql.SQLException;

public class test {
    public static void main(String[] args) throws SQLException {
        DBTable<Student> studentDB = new DBTable<>(Student.class);
        studentDB.createTable();

    }
}
