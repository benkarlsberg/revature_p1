package revature.orm.testing.app;

import revature.orm.entitymanager.DBTable;
import revature.orm.testing.models.Student;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class test {
    public static void main(String[] args) throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        DBTable<Student> studentDB = new DBTable<>(Student.class);
        studentDB.createTable();

        Student student1 = new Student("first", "last", 10);
        Student student2 = new Student("first", "last", 10);
        Student student3 = new Student("first", "last", 10);

        //testing insertInto
        studentDB.insertInto(student1);
        studentDB.insertInto(student2);
        studentDB.insertInto(student3);

        // testing update
        student1.setAge(100);
        studentDB.update(3, student1);

        // testing get
        System.out.println(studentDB.get(3));

        // testing delete
        System.out.println(studentDB.delete(3));
    }
}
