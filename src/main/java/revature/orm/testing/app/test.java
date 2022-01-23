package revature.orm.testing.app;

import revature.orm.entitymanager.DBTable;
import revature.orm.testing.models.Student;

public class test {
    public static void main(String[] args) {
        DBTable<Student> studentDB = new DBTable<>(Student.class);
        studentDB.createTable();

    }
}
