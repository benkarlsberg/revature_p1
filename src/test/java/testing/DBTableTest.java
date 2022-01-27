package testing;

import org.junit.jupiter.api.*;
import revature.orm.entitymanager.DBTable;
import revature.orm.testing.models.School;
import revature.orm.testing.models.Student;

import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DBTableTest {

    static DBTable<Student> studentDB;
    static DBTable<School> schoolDB;
    static School school;
    static Student student;

    @BeforeAll
    public static void before() throws SQLException, NoSuchFieldException, ClassNotFoundException {

        studentDB = new DBTable<>(Student.class);
        schoolDB = new DBTable<>(School.class);
        school = new School("BERKELEY");
        student = new Student("First","Last",20, Date.valueOf("2000-11-20"),"Male",10,school);

    }

    @Test
    @Order(1)
    public void createTable() throws SQLException, NoSuchFieldException, ClassNotFoundException {

        boolean createdStudentDB = studentDB.createTable();
        boolean createdSchoolDB = schoolDB.createTable();
        assertTrue(createdStudentDB);
        assertTrue(createdSchoolDB);
        assertTrue(studentDB.tableExists("student"));
        assertTrue(schoolDB.tableExists("school"));
    }

    @Test
    @Order(2)
    public void insertInto() throws SQLException, NoSuchFieldException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {

        schoolDB.insertInto(school);
        studentDB.insertInto(student);

    }

    @Test
    @Order(3)
    public void get() throws SQLException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        schoolDB.get(1);
        studentDB.get(1);
        schoolDB.get("true");
    }

    @Test
    @Order(4)
    public void update() throws SQLException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        student = studentDB.get(1);
        student.setAge(100);
        studentDB.update(1,student);
    }

    @Test
    @Order(5)
    public void delete() throws SQLException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        studentDB.delete(1);
        schoolDB.delete(1);
    }
}
