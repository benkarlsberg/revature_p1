package revature.orm.testing.app;

import revature.orm.annotation.ForeignKey;
import revature.orm.entitymanager.DBTable;
import revature.orm.testing.models.Student;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class test {
    public static void main(String[] args) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchFieldException, InstantiationException {
        DBTable<Student> studentDB = new DBTable<>(Student.class);
        studentDB.checkFields();
        studentDB.checkFields();
        //studentDB.createTable();
        //
        //studentDB.addForeignKey();
//        List<Student> list = studentDB.get("true");
//        System.out.println(list);
//        while(rs.next()){
//            System.out.println(rs.getString("lastname"));
//        }
       // studentDB.insertInto(new Student(1,"first","last",10));
//        for (Field field: Student.class.getDeclaredFields()) {
//            if(field.isAnnotationPresent(ForeignKey.class)){
//                ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
//                System.out.println(foreignKey.entity());
//            }
//        }
        //studentDB.addForeignKey();
//        List<Field> primaryKeys= studentDB.getPrimaryKeys(Student.class);
//        System.out.println(primaryKeys.get(0).getName());
//        System.out.println(primaryKeys.get(0).getType());

        Student student1 = new Student("first", "last", 10, Date.valueOf("2021-01-25"));
        Student student2 = new Student("first", "last", 10, Date.valueOf("2021-01-25"));
        Student student3 = new Student("first", "last", 10, Date.valueOf("2021-01-25"));

        // retrieves array of enum
        System.out.println(Arrays.toString(Student.grade.values()));

        //testing insertInto
        studentDB.insertInto(student1);
        studentDB.insertInto(student2);
        studentDB.insertInto(student3);

        // testing update
        student1.setAge(100);
        studentDB.update(1, student1);

        // testing get
        System.out.println(studentDB.get(1));

        // testing delete
        System.out.println(studentDB.delete(1));
    }
}
