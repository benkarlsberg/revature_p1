package testing;

import org.junit.jupiter.api.*;
import revature.orm.entitymanager.DBTable;
import revature.orm.testing.logging.MyLogger;
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

        MyLogger.logger.info("Testing Started");
        studentDB = new DBTable<>(Student.class);
        schoolDB = new DBTable<>(School.class);
        school = new School(1,"BERKELEY");
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
        schoolDB.insertInto(school);
        studentDB.insertInto(student);
        schoolDB.insertInto(school);
        studentDB.insertInto(student);
        schoolDB.insertInto(school);
        studentDB.insertInto(student);

    }

    @Test
    @Order(3)
    public void get() throws SQLException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException {

        schoolDB.get(1);
        studentDB.get(1);
        schoolDB.get("true");

        //student = new Student("First","Last",20, Date.valueOf("2000-11-20"),"Male",10,school);
        assertEquals(studentDB.get(1).toString(), "Student{id=1, firstName='First', lastName='Last', age=20, enrollDate=2000-11-20" +
                ", gender='Male', grade=10, school=School{id=1, name='BERKELEY'}}");
    }

    @Test
    @Order(4)
    public void testException() {
        SQLException thrown = Assertions.assertThrows(SQLException.class, () -> {
            studentDB.get("Hello");
        }, "SQLException expected");
    }

    @Test
    @Order(5)
    public void update() throws SQLException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        student = studentDB.get(1);
        student.setFirstName("Emma");
        student.setLastName("Watson");
        student.setEnrollDate(Date.valueOf("2020-11-20"));
        student.setGender("Female");
        student.setAge(29);
        studentDB.update(1,student);
    }

    @Test
    @Order(6)
    public void delete() throws SQLException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        studentDB.delete(3);
        schoolDB.delete(3);
        studentDB.executeStatement("drop table if exists student;");
        studentDB.executeStatement("drop table if exists school;");
    }
}
