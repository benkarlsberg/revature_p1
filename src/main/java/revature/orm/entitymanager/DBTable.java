package revature.orm.entitymanager;

import revature.orm.annotation.ForeignKey;
import revature.orm.annotation.PrimaryKey;
import revature.orm.annotation.Serial;
import revature.orm.connection.JDBCConnection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//responsible for Create and CRUD operation for one table
public class DBTable<E> {
    Class clazz;
    private Connection conn = JDBCConnection.getConnection();
    public DBTable(Class clazz){

        this.clazz = clazz;
        createTable();
        addForeignKey();
        checkFields();
    }

    public boolean createTable() {
        Field[] fields = clazz.getDeclaredFields();
        fields = fieldFilter(fields);

        StringBuilder sqlStatement = new StringBuilder("CREATE TABLE " + clazz.getSimpleName() + "( ");
        for (Field field : fields) {
            String type = null;
            type = " " + sqlTypeConverter(field) + ",";
            sqlStatement.append(field.getName()).append(type);
        }
        sqlStatement.append("PRIMARY KEY (");
        for (Field field : fields) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                sqlStatement.append(field.getName()).append(")");
            }
        }
        sqlStatement.append(")");
        System.out.println(sqlStatement);
        //check if table exists, then no need to create
        if(!tableExists(clazz.getSimpleName())) {
            return executeStatement(sqlStatement.toString());
        }else{
            return true;
        }
    }

    public boolean addForeignKey(){
        for (Field field: clazz.getDeclaredFields()) {
            if(field.isAnnotationPresent(ForeignKey.class)){
                ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
                String entity = field.getType().getSimpleName();
                String f = foreignKey.field();
                String sqlStatement= "ALTER TABLE " +clazz.getSimpleName()+
                        " ADD FOREIGN KEY ("+field.getName()+") REFERENCES "+entity+"("+f+")";
                System.out.println(sqlStatement);
                try {
                    if(tableExists(entity)){
                        executeStatement(sqlStatement);
                        return true;
                    }else{
                        DBTable<Object> objectDBTable= new DBTable<>(Class.forName(clazz.getPackage().getName()+"."+entity));
                        executeStatement(sqlStatement);
                        return true;
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
        return false;
    }

    public void checkFields(){
        Statement stmt = null;
        try {
            stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery("Select * from "+clazz.getSimpleName());
        ResultSetMetaData rsMetaData = rs.getMetaData();
        int count = rsMetaData.getColumnCount();
        List<String> metaData = new ArrayList<>();
        List<String> fieldName = new ArrayList<>();
        for(int i = 1; i<=count; i++) {
            metaData.add(rsMetaData.getColumnName(i).toLowerCase());
        }

        Field[] fields = clazz.getDeclaredFields();
        fields = fieldFilter(fields);
        for (Field field : fields) {
            fieldName.add(field.getName().toLowerCase());
        }

        //if a field in metaData doesn't contain in entity model ->alter drop column
        for (String metaDatum : metaData) {
            if (!fieldName.contains(metaDatum)) {
                String sqlStatement = "ALTER TABLE " + clazz.getSimpleName() + " DROP COLUMN " + metaDatum;
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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean tableExists(String tableName){
        DatabaseMetaData meta = null;
        try {
            meta = conn.getMetaData();

        ResultSet resultSet = meta.getTables(null, null, tableName.toLowerCase(), new String[] {"TABLE"});

        return resultSet.next();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public boolean executeStatement(String sqlStatement){
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

    public String getMethod(String methodName) {

        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (name.equalsIgnoreCase(methodName)) {
                return name;
            }
        }
        return "";

    }

    public E insertInto(E entity){
        Field[] fields = clazz.getDeclaredFields();
        fields = fieldFilter(fields);

        StringBuilder sqlStatement = new StringBuilder("INSERT INTO " + clazz.getSimpleName() + " VALUES (");
        try {
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].isAnnotationPresent(Serial.class)) {
                    sqlStatement.append("default");

                } else if (fields[i].isAnnotationPresent(ForeignKey.class)) {
                    ForeignKey foreignKey = fields[i].getAnnotation(ForeignKey.class);
                    String fieldName = foreignKey.field();
                    String foreignEntity = fields[i].getType().getName();
                    Class foreignClass = Class.forName(foreignEntity);
                    String type = foreignClass.getDeclaredField(fieldName).getType().getName();

                    if (type.equals("class java.lang.String")) {
                        Method foreignMethod = foreignClass.getMethod(getMethod("get" + fieldName));
                        Method primaryMethod = clazz.getMethod(getMethod("get" + fields[i].getName()));

                        Object value = foreignMethod.invoke(primaryMethod.invoke(entity));
                        sqlStatement.append("'").append(value).append("'");
                    } else if (type.equals("int") || fields[i].getType().toString().equals("float") || fields[i].getType().toString().equals("double")) {
                        Method foreignMethod = foreignClass.getMethod(getMethod("get" + fieldName));
                        Method primaryMethod = clazz.getMethod(getMethod("get" + fields[i].getName()));

                        Object value = foreignMethod.invoke(primaryMethod.invoke(entity));
                        System.out.println(value);
                        sqlStatement.append(value);
                    }
                } else if (fields[i].getType().toString().equals("class java.lang.String")) {
                    Method method = entity.getClass().getMethod(getMethod("get" + fields[i].getName()));
                    String s = (String) method.invoke(entity);
                    sqlStatement.append("'").append(s).append("'");
                } else if (fields[i].getType().toString().equals("int") || fields[i].getType().toString().equals("float") || fields[i].getType().toString().equals("double")) {
                    Method method = entity.getClass().getMethod(getMethod("get" + fields[i].getName()));
                    int num = (int) method.invoke(entity);
                    sqlStatement.append(num);
                } else if (fields[i].getType().toString().equals("class java.sql.Date")) {
                    Method method = entity.getClass().getMethod(getMethod("get" + fields[i].getName()));
                    Date date = (Date) method.invoke(entity);
                    sqlStatement.append("'").append(date).append("'");
                }

                if (!((i + 1) == fields.length)) {
                    sqlStatement.append(", ");
                }
            }
            sqlStatement.append(");");

            if (tableExists(clazz.getSimpleName())) {
                System.out.println(sqlStatement);
                executeStatement(sqlStatement.toString());
                return entity;
            } else {
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public E update(int primaryKey, E entity){
        Field[] fields = clazz.getDeclaredFields();
        fields = fieldFilter(fields);
        int idField = 0;

        StringBuilder sqlStatement = new StringBuilder("UPDATE " + clazz.getSimpleName() + " SET ");

        try {
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].isAnnotationPresent(Serial.class)) {
                    idField = i;
                } else if (fields[i].isAnnotationPresent(ForeignKey.class)) {
                    ForeignKey foreignKey = fields[i].getAnnotation(ForeignKey.class);
                    String fieldName = foreignKey.field();
                    String foreignEntity = fields[i].getType().getName();
                    Class foreignClass = Class.forName(foreignEntity);
                    String type = foreignClass.getDeclaredField(fieldName).getType().getName();

                    if (type.equals("class java.lang.String")) {
                        Method foreignMethod = foreignClass.getMethod(getMethod("get" + fieldName));
                        Method primaryMethod = clazz.getMethod(getMethod("get" + fields[i].getName()));

                        Object value = foreignMethod.invoke(primaryMethod.invoke(entity));
                        sqlStatement.append(fields[i].getName()).append("=");
                        sqlStatement.append("'").append(value).append("'");
                    } else if (type.equals("int") || fields[i].getType().toString().equals("float") || fields[i].getType().toString().equals("double")) {
                        Method foreignMethod = foreignClass.getMethod(getMethod("get" + fieldName));
                        Method primaryMethod = clazz.getMethod(getMethod("get" + fields[i].getName()));
//                    System.out.println("===================");
//                    System.out.println(primaryMethod.invoke(entity));
//                    System.out.println(foreignMethod.invoke(primaryMethod.invoke(entity)));
                        Object value = foreignMethod.invoke(primaryMethod.invoke(entity));
                        System.out.println(value);
                        sqlStatement.append(fields[i].getName()).append("=");
                        sqlStatement.append(value);
                    }
                } else if (fields[i].getType().toString().equals("class java.lang.String")) {
                    sqlStatement.append(fields[i].getName()).append("=");
                    Method method = entity.getClass().getMethod(getMethod("get" + fields[i].getName()));
                    String s = (String) method.invoke(entity);
                    sqlStatement.append("'").append(s).append("'");
                } else if (fields[i].getType().toString().equals("int") || fields[i].getType().toString().equals("float") || fields[i].getType().toString().equals("double")) {
                    sqlStatement.append(fields[i].getName()).append("=");
                    Method method = entity.getClass().getMethod(getMethod("get" + fields[i].getName()));
                    int num = (int) method.invoke(entity);
                    sqlStatement.append(num);
                } else if (fields[i].getType().toString().equals("class java.sql.Date")) {
                    sqlStatement.append(fields[i].getName()).append("=");
                    Method method = entity.getClass().getMethod(getMethod("get" + fields[i].getName()));
                    Date date = (Date) method.invoke(entity);
                    sqlStatement.append("'").append(date).append("'");
                }
                if (i == 0) {
                    sqlStatement.append("");
                } else if (!((i + 1) == fields.length)) {
                    sqlStatement.append(", ");
                }
            }

            sqlStatement.append(" WHERE ").append(fields[idField].getName()).append("=").append(primaryKey).append(";");

            if (tableExists(clazz.getSimpleName())) {
                System.out.println(sqlStatement);
                executeStatement(sqlStatement.toString());
                return entity;
            } else {
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public E delete(int primaryKey){
        Field[] fields = clazz.getDeclaredFields();
        fields = fieldFilter(fields);
        int idField = 0;
        try {
            E e = get(primaryKey);
            StringBuilder sqlStatement = new StringBuilder("DELETE FROM " + clazz.getSimpleName());

            for (int i = 0; i < fields.length; i++) {
                // System.out.println(fields[i].isAnnotationPresent(Serial.class));
                if (fields[i].isAnnotationPresent(Serial.class)) {
                    idField = i;
                }
            }
            sqlStatement.append(" WHERE ").append(fields[idField].getName()).append("=").append(primaryKey).append(" RETURNING *;");


            if (tableExists(clazz.getSimpleName())) {
                System.out.println(sqlStatement);
                PreparedStatement ps = conn.prepareStatement(String.valueOf(sqlStatement));
                ResultSet rs = ps.executeQuery();

//            executeStatement(sqlStatement.toString());
//            return entity;
            } else {
                return null;
            }
            return e;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    public E get(int primaryKey){
        Field[] fields = clazz.getDeclaredFields();
        fields = fieldFilter(fields);
        int idField = 0;
        StringBuilder sqlStatement = new StringBuilder("SELECT * FROM " + clazz.getSimpleName() + " WHERE ");

        for (int i = 0; i < fields.length; i++) {
            // System.out.println(fields[i].isAnnotationPresent(Serial.class));
            if (fields[i].isAnnotationPresent(PrimaryKey.class)) {
                idField = i;
            }
        }
        sqlStatement.append(fields[idField].getName()).append("=").append(primaryKey);
        try {
            if (tableExists(clazz.getSimpleName())) {
                PreparedStatement ps = conn.prepareStatement(String.valueOf(sqlStatement));
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {

                    // Object e = new Object();

                    E e = (E) clazz.getConstructor().newInstance();
                    for (int i = 0; i < fields.length; i++) {
                        String methodName = getMethod("set" + fields[i].getName());
                        //System.out.println(methodName);
                        Method method = e.getClass().getMethod(methodName, fields[i].getType());
                        if (fields[i].isAnnotationPresent(ForeignKey.class)) {
                            Object value = rs.getObject(i + 1);

                            Object fkDB = new DBTable<>(Class.forName(fields[i].getType().getName()));
                            Method fkMethod = fkDB.getClass().getDeclaredMethod("get", int.class);
                            Object fbObj = fkMethod.invoke(fkDB, value);

                            method.invoke(e, fbObj);
                        } else if (fields[i].getType().toString().equals("int")) {
                            method.invoke(e, rs.getInt(i + 1));
                        } else if (fields[i].getType().toString().equals("class java.lang.String")) {
                            method.invoke(e, rs.getString(i + 1));
                        } else if (fields[i].getType().toString().equals("class java.sql.Date")) {
                            System.out.println("date=" + rs.getDate(i + 1));
                            method.invoke(e, rs.getDate(i + 1));
                        }

                    }
                    return e;
                }
            } else {
                return null;
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //Select * from student where id=1, age>10
    public List<E> get(String... condition){
        List<String> conditions = new ArrayList<String>(condition.length);
        List<E> list = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        fields = fieldFilter(fields);
        for (String s: condition) conditions.add(s);


        String sqlStatement = "select * from "+ clazz.getSimpleName().toLowerCase()+" where "+conditions.get(0);
        if (conditions.size()>1){
            for (int i = 1; i < conditions.size(); i++) {
                sqlStatement+=" and "+conditions.get(i);
            }
        }
        try {
            System.out.println(sqlStatement);
            PreparedStatement ps = conn.prepareStatement(sqlStatement);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                E e = (E) clazz.getConstructor().newInstance();
                for (int i = 0; i < fields.length; i++) {
                    String methodName = getMethod("set" + fields[i].getName());
                    //System.out.println(methodName);
                    Method method = e.getClass().getMethod(methodName, fields[i].getType());
                    if (fields[i].isAnnotationPresent(ForeignKey.class)) {
                        Object value = rs.getObject(i + 1);

                        Object fkDB = new DBTable<>(Class.forName(fields[i].getType().getName()));
                        Method fkMethod = fkDB.getClass().getDeclaredMethod("get", int.class);
                        Object fbObj = fkMethod.invoke(fkDB, value);

                        method.invoke(e, fbObj);
                    } else if (fields[i].getType().toString().equals("int")) {
                        method.invoke(e, rs.getInt(i + 1));
                    } else if (fields[i].getType().toString().equals("class java.lang.String")) {
                        method.invoke(e, rs.getString(i + 1));
                    } else if (fields[i].getType().toString().equals("class java.sql.Date")) {
                        System.out.println("date=" + rs.getDate(i + 1));
                        method.invoke(e, rs.getDate(i + 1));
                    }

                }
                list.add(e);
            }
            return list;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

//    public List<Field> getPrimaryKeys(Class clazz) {
//        List<Field> primaryFields = new ArrayList<>();
//        Field[] fields = clazz.getDeclaredFields();
//        fields = fieldFilter(fields);
//        for (int i = 0; i < fields.length; i++) {
//            if(fields[i].isAnnotationPresent(PrimaryKey.class)){
//                primaryFields.add(fields[i]);
//            }
//        }
//        return primaryFields;
//    }

    public String sqlTypeConverter(Field field){
        try {
            if (field.isAnnotationPresent(Serial.class)) {
                return "SERIAL";
            } else if (field.isAnnotationPresent(ForeignKey.class)) {
                ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
                String fieldName = foreignKey.field();
                Class foreignKeyClass = Class.forName(field.getType().getName());
                String type = foreignKeyClass.getDeclaredField(fieldName).getType().getSimpleName();
                if (type.equals("int")) {
                    return "int";
                } else if (type.equals("class java.lang.String")) {
                    return "varchar(255)";
                }
            } else if (field.getType().toString().equals("class java.lang.String")) {
                return "varchar(255)";
            } else if (field.getType().toString().equals("int")) {
                return "int";
            } else if (field.getType().toString().equals("float") || field.getType().equals("double")) {
                return "double";
            } else if (field.getType().toString().equals("class java.sql.Date")) {
                return "DATE";
            }
            return "";
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    // removes __$lineHits$__ for code coverage testing
    public Field[] fieldFilter(Field[] fields) {
        if (fields.length == 0) {
            return fields;
        }
        Field[] fieldCopy = new Field[fields.length];
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName().equals("__$lineHits$__")) {
                fieldCopy = new Field[fields.length - 1];
                break;
            }
        }
        for (int i = 0; i < fields.length; i++) {
            if (!fields[i].getName().equals("__$lineHits$__")) {
                fieldCopy[i] = fields[i];
            }
        }
        return fieldCopy;
    }

}
