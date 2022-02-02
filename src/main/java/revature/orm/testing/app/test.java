package revature.orm.testing.app;

import revature.orm.annotation.ForeignKey;
import revature.orm.entitymanager.DBTable;
import revature.orm.testing.models.School;
import revature.orm.testing.models.Student;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class test {
    public static void main(String[] args){
        //Testing Create, addForeignKey, checkField()
        DBTable<Student> studentDB = new DBTable<>(Student.class);
        //Testing insertInto()
        DBTable<School> schoolDB = new DBTable<>(School.class);
        schoolDB.insertInto(new School("NDSU"));
        School school =schoolDB.get(2);
        //System.out.println(school);
//        Student student = new Student("First","Last",20,Date.valueOf("2000-11-20"),"Male",10,school);
//        //System.out.println(student.getSchool());
//        studentDB.insertInto(student);

        Student student1 = new Student("First","Last",20, Date.valueOf("2000-11-20"),"Male",10,school);
        studentDB.insertInto(student1);
        //Testing get(int id);
//        System.out.println(schoolDB.get(2));
//        System.out.println(studentDB.get(3));
////
////        //Testing get(String.. condition);
////        System.out.println(schoolDB.get("true"));
////
////        //Testing delete(int id);
//        //System.out.println(studentDB.delete(2));
//
//        //Testing update
//        student = studentDB.get(4);
//        student.setAge(100);
//        System.out.println(studentDB.update(4,student));
//        System.out.println(studentDB.get("true"));
        //Testing

    }
}
