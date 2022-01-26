package revature.orm.entitymanager;

import revature.orm.annotation.ForeignKey;
import revature.orm.annotation.PrimaryKey;
import revature.orm.annotation.Serial;
import revature.orm.connection.JDBCConnection;
import revature.orm.testing.models.Student;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

//responsible for Create and CRUD operation for one table
public class DBTable<E> {
    Class clazz;
    private Connection conn = JDBCConnection.getConnection();
    public DBTable(Class clazz) throws SQLException, ClassNotFoundException, NoSuchFieldException {

        this.clazz = clazz;
        createTable();
        addForeignKey();
    }

    public boolean createTable() throws SQLException, ClassNotFoundException, NoSuchFieldException {
        Field[] fields = clazz.getDeclaredFields();

        String sqlStatement = "CREATE TABLE "+ clazz.getSimpleName()+"(";
        for (int i = 0; i < fields.length; i++) {
           // System.out.println(fields[i].isAnnotationPresent(Serial.class));
            if(fields[i].isAnnotationPresent(Serial.class))
            {
                sqlStatement += fields[i].getName()+" SERIAL,";
            }else if(fields[i].isAnnotationPresent(ForeignKey.class))
            {
                ForeignKey foreignKey = fields[i].getAnnotation(ForeignKey.class);
                String fieldName = foreignKey.field();
                Class foreignKeyClass = Class.forName(fields[i].getType().getName());
                String type = foreignKeyClass.getDeclaredField(fieldName).getType().getSimpleName();
                //System.out.println(type);
                if(type.equals("int"))
                {
                    sqlStatement += fields[i].getName()+" int,";
                }else if(type.equals("class java.lang.String"))
                {
                    sqlStatement += fields[i].getName()+" varchar(255),";
                }
            }else if(fields[i].getType().toString().equals("class java.lang.String"))
            {
                sqlStatement += fields[i].getName()+" varchar(255),";
            }else if(fields[i].getType().toString().equals("int"))
            {
                sqlStatement += fields[i].getName()+" int,";
            }else if(fields[i].getType().toString().equals("float") || fields[i].getType().equals("double")){
                sqlStatement += fields[i].getName()+" float,";
            }
        }
        sqlStatement+= "PRIMARY KEY (";
        for (int i = 0; i < fields.length; i++) {
            if(fields[i].isAnnotationPresent(PrimaryKey.class)){
                sqlStatement+=fields[i].getName()+")";
            }
        }
        sqlStatement+=")";
        System.out.println(sqlStatement);
        //check if table exists, then no need to create
        if(!tableExists(clazz.getSimpleName())) {
            return executeStatement(sqlStatement);
        }else{
            return true;
        }
    }

    public boolean addForeignKey() throws SQLException, ClassNotFoundException, NoSuchFieldException {
        for (Field field: clazz.getDeclaredFields()) {
            if(field.isAnnotationPresent(ForeignKey.class)){
                ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
                String entity = field.getType().getSimpleName();
                String f = foreignKey.field();
                String sqlStatement= "ALTER TABLE " +clazz.getSimpleName()+
                        " ADD FOREIGN KEY ("+field.getName()+") REFERENCES "+entity+"("+f+")";
                System.out.println(sqlStatement);
                if(tableExists(entity)){
                    executeStatement(sqlStatement);
                    return true;
                }else{

                    DBTable<Object> objectDBTable= new DBTable<>(Class.forName(clazz.getPackage().getName().toString()+"."+entity));
                    executeStatement(sqlStatement);
                    return true;
                }

            }
        }
        return false;
    }

    public void checkFields() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("Select * from "+clazz.getSimpleName());
        ResultSetMetaData rsMetaData = rs.getMetaData();
        int count = rsMetaData.getColumnCount();
        List<String> metaData = new ArrayList<>();
        List<String> fieldName = new ArrayList<>();
        for(int i = 1; i<=count; i++) {
            metaData.add(rsMetaData.getColumnName(i).toLowerCase());
        }

        Field[] fields = clazz.getDeclaredFields();


        for (int i = 0; i < fields.length; i++) {
            fieldName.add(fields[i].getName().toLowerCase());
        }


//        System.out.println("metadata");
//        System.out.println(metaData);
//        System.out.println("fields");
//        System.out.println(fieldName);
        //if a field in metaData doesn't contain in entity model ->alter drop column
        for (int i = 0; i < metaData.size(); i++) {
            if(!fieldName.contains(metaData.get(i))){
                String sqlStatement = "ALTER TABLE "+clazz.getSimpleName()+" DROP COLUMN "+metaData.get(i);
                System.out.println(sqlStatement);
                executeStatement(sqlStatement);
            }
        }
        //if a field in entity model is not in table ->alter add column
        for (int i = 0; i < fieldName.size(); i++) {
            if(!metaData.contains(fieldName.get(i))){
                String type =fields[i].getType().getSimpleName();
                if(type.equals("int"))
                {
                    type= "int";
                }else if(type.equals("String"))
                {
                    type="varchar(255)";
                }
                String sqlStatement = "ALTER TABLE "+clazz.getSimpleName()+" ADD COLUMN "+fieldName.get(i)+" "+type;
                System.out.println(sqlStatement);
                executeStatement(sqlStatement);
            }
        }
    }

    boolean tableExists(String tableName) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        ResultSet resultSet = meta.getTables(null, null, tableName.toLowerCase(), new String[] {"TABLE"});

        return resultSet.next();
    }

    private boolean executeStatement(String sqlStatement){
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sqlStatement);
            //System.out.println(clazz.getSimpleName()+" table created");
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }
    // print out sql statement
    public E insertInto(E entity) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Field[]  fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            System.out.println(fields[i].getName());
        }
        Method method = entity.getClass().getMethod("getAge");
        int age = (int) method.invoke(entity);
        System.out.println(age);
        return entity;
    }

    public E update(int primaryKey,E entity){
        return entity;
    }

    public E delete(int primaryKey){
        return null;
    }

    public E get(int primaryKey) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        Field[] fields = clazz.getDeclaredFields();
        int idField = 0;
        StringBuilder sqlStatement = new StringBuilder("SELECT * FROM " + clazz.getSimpleName() + " WHERE ");

        for (int i = 0; i < fields.length; i++) {
            // System.out.println(fields[i].isAnnotationPresent(Serial.class));
            if (fields[i].isAnnotationPresent(PrimaryKey.class)) {
                idField = i;
            }
        }
        sqlStatement.append(fields[idField].getName()).append("=").append(primaryKey);

//        Method method = entity.getClass().getMethod(getMethod("get" + fields[i].getName()));
//        int id = (int)method.invoke(entity);
        if(tableExists(clazz.getSimpleName())) {
//            System.out.println(sqlStatement);
            PreparedStatement ps = conn.prepareStatement(String.valueOf(sqlStatement));
            ResultSet rs = ps.executeQuery();

            while (rs.next()){

                // Object e = new Object();
                E e = (E) clazz.getConstructor().newInstance();
                for (int i = 0; i < fields.length; i++) {
                    String methodName = getMethod("set" + fields[i].getName());
                    //System.out.println(methodName);
                    Method method = e.getClass().getMethod(methodName,fields[i].getType());
                    if(fields[i].getType().toString().equals("int")){
                        method.invoke(e, rs.getInt(i+1));
                    }else if(fields[i].getType().toString().equals("class java.lang.String")){
                        method.invoke(e, rs.getString(i+1));
                    }

                }
                return e;
            }
        }else {
            return null;
        }
        return null;
    }

    //args paramter
    private E objectBuilder(ResultSet rs) throws NoSuchFieldException, SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Field[] fields = clazz.getDeclaredFields();
       // Object e = new Object();
        E e = (E) clazz.getConstructor().newInstance();
        for (int i = 0; i < fields.length; i++) {
            String methodName = getMethod("set" + fields[i].getName());

            Method method = e.getClass().getMethod(methodName,fields[i].getType());
            if(fields[i].getType().toString().equals("int")){
                method.invoke(e, rs.getInt(i+1));
            }else if(fields[i].getType().toString().equals("class java.lang.String")){
                method.invoke(e, rs.getString(i+1));
            }

        }
        return e;
    }
    public String getMethod(String methodName) {

        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (name.equalsIgnoreCase(methodName)) {
                return name;
            } else if (name.equalsIgnoreCase(methodName)) {
                return name;
            }
        }
        return "";

    }
    //Select * from student where id=1, age>10
    public List<E> get(String... condition) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        List<String> conditions = new ArrayList<String>(condition.length);
        List<E> list = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (String s: condition) conditions.add(s);


        String sqlStatement = "select * from "+ clazz.getSimpleName().toLowerCase()+" where "+conditions.get(0);
        if (conditions.size()>1){
            for (int i = 1; i < conditions.size(); i++) {
                sqlStatement+=" and "+conditions.get(i);
            }
        }
        //sqlStatement="SELECT * FROM student";
        System.out.println(sqlStatement);
        PreparedStatement ps = conn.prepareStatement(sqlStatement);
        ResultSet rs =ps.executeQuery();

        while (rs.next()){

            E e = (E) clazz.getConstructor().newInstance();
            for (int i = 0; i < fields.length; i++) {
                String methodName = getMethod("set" + fields[i].getName());

                Method method = e.getClass().getMethod(methodName,fields[i].getType());
                if(fields[i].getType().toString().equals("int")){
                    method.invoke(e, rs.getInt(i+1));
                }else if(fields[i].getType().toString().equals("class java.lang.String")){
                    method.invoke(e, rs.getString(i+1));
                }

            }
            list.add(e);
        }
        return list;
    }


    public List<Field> getPrimaryKeys(Class clazz) {
        List<Field> primaryFields = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if(fields[i].isAnnotationPresent(PrimaryKey.class)){
                primaryFields.add(fields[i]);


            }
        }
        return primaryFields;
    }
}
