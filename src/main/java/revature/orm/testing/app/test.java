package revature.orm.testing.app;

import revature.orm.annotation.ForeignKey;
import revature.orm.entitymanager.DBTable;
import revature.orm.testing.models.Student;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class test {
    public static void main(String[] args) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchFieldException, InstantiationException {
        DBTable<Student> studentDB = new DBTable<>(Student.class);
        //studentDB.checkFields();
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


    }
}
