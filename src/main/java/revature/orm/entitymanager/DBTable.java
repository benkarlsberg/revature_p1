package revature.orm.entitymanager;

import revature.orm.annotation.PrimaryKey;
import revature.orm.annotation.Serial;
import revature.orm.connection.JDBCConnection;

import java.lang.reflect.*;
import java.sql.*;
import java.util.List;

//responsible for Create and CRUD operation for one table
public class DBTable<E> {
    Class clazz;
    private Connection conn = JDBCConnection.getConnection();
    public DBTable(Class clazz) {
        this.clazz = clazz;
    }

    public boolean createTable() throws SQLException {
        Field[] fields = clazz.getDeclaredFields();

        String sqlStatement = "CREATE TABLE "+ clazz.getSimpleName()+"(";
        for (int i = 0; i < fields.length; i++) {
           // System.out.println(fields[i].isAnnotationPresent(Serial.class));
            if(fields[i].isAnnotationPresent(Serial.class))
            {
                sqlStatement += fields[i].getName()+" SERIAL,";
            }else if(fields[i].getType().toString().equals("class java.lang.String"))
            {
                sqlStatement += fields[i].getName()+" varchar(255),";
            }else if(fields[i].getType().toString().equals("int"))
            {
                sqlStatement += fields[i].getName()+" int,";
            }else if(fields[i].getType().toString().equals("float") || fields[i].getType().toString().equals("double"))
            {
                sqlStatement += fields[i].getName()+" float,";
            }else if(fields[i].getType().toString().equals("class java.sql.Date"))
            {
                sqlStatement += fields[i].getName() + " DATE,";
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
        if(!tableExists(conn,clazz.getSimpleName())) {
            return executeStatement(sqlStatement);
        }else{
            return true;
        }
    }


    boolean tableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
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

    // print out sql statement
    public E insertInto(E entity) throws SQLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Field[] fields = clazz.getDeclaredFields();

        StringBuilder sqlStatement = new StringBuilder("INSERT INTO " + clazz.getSimpleName() + " VALUES (");


        for (int i = 0; i < fields.length; i++) {
            // System.out.println(fields[i].isAnnotationPresent(Serial.class));
            if(fields[i].isAnnotationPresent(Serial.class))
            {
                sqlStatement.append("default");

            }else if(fields[i].getType().toString().equals("class java.lang.String"))
            {
                Method method = entity.getClass().getMethod(getMethod("get" + fields[i].getName()));
                String s = (String) method.invoke(entity);
                sqlStatement.append("'").append(s).append("'");
            }else if(fields[i].getType().toString().equals("int") || fields[i].getType().toString().equals("float") || fields[i].getType().toString().equals("double"))
            {
                Method method = entity.getClass().getMethod(getMethod("get" + fields[i].getName()));
                int num = (int) method.invoke(entity);
                sqlStatement.append(num);
            }else if(fields[i].getType().toString().equals("class java.sql.Date")) {
                Method method = entity.getClass().getMethod(getMethod("get" + fields[i].getName()));
                Date date = (Date)method.invoke(entity);
                sqlStatement.append("'").append(date).append("'");
            }
//            }else if(fields[i].getType().toString().equals("enum")) {
//                Method method = entity.getClass().getDeclaredMethod(getMethod("values"));
//                enum lst = (enum)method.invoke(entity);
//                sqlStatement.append(enum);
//            }




            if (!((i+1) == fields.length)){
                sqlStatement.append(", ");
            }
        }
        sqlStatement.append(");");

        if(tableExists(conn,clazz.getSimpleName())) {
            System.out.println(sqlStatement);
            executeStatement(sqlStatement.toString());
            return entity;
        }else{
            return null;
        }
    }

    public E update(int primaryKey, E entity) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, SQLException {
        Field[] fields = clazz.getDeclaredFields();
        int idField = 0;

        StringBuilder sqlStatement = new StringBuilder("UPDATE " + clazz.getSimpleName() + " SET ");

        for (int i = 0; i < fields.length; i++) {
            // System.out.println(fields[i].isAnnotationPresent(Serial.class));
            if(fields[i].isAnnotationPresent(Serial.class))
            {
                idField = i;
            } else if(fields[i].getType().toString().equals("class java.lang.String"))
            {
                sqlStatement.append(fields[i].getName()).append("=");
                Method method = entity.getClass().getMethod(getMethod("get" + fields[i].getName()));
                String s = (String) method.invoke(entity);
                sqlStatement.append("'").append(s).append("'");
            }else if(fields[i].getType().toString().equals("int") || fields[i].getType().toString().equals("float") || fields[i].getType().toString().equals("double"))
            {
                sqlStatement.append(fields[i].getName()).append("=");
                Method method = entity.getClass().getMethod(getMethod("get" + fields[i].getName()));
                int num = (int) method.invoke(entity);
                sqlStatement.append(num);
            }else if(fields[i].getType().toString().equals("class java.sql.Date"))
            {
                sqlStatement.append(fields[i].getName()).append("=");
                Method method = entity.getClass().getMethod(getMethod("get" + fields[i].getName()));
                Date date = (Date)method.invoke(entity);
                sqlStatement.append("'").append(date).append("'");
            }
            if (i == 0) {
                sqlStatement.append("");
            }
            else if (!((i+1) == fields.length)){
                sqlStatement.append(", ");
            }
        }

        sqlStatement.append(" WHERE ").append(fields[idField].getName()).append("=").append(primaryKey).append(";");

        if(tableExists(conn,clazz.getSimpleName())) {
            System.out.println(sqlStatement);
            executeStatement(sqlStatement.toString());
            return entity;
        }else {
            return null;
        }
    }

    public ResultSet delete(int primaryKey) throws SQLException {
        Field[] fields = clazz.getDeclaredFields();
        int idField = 0;

        StringBuilder sqlStatement = new StringBuilder("DELETE FROM " + clazz.getSimpleName());

        for (int i = 0; i < fields.length; i++) {
            // System.out.println(fields[i].isAnnotationPresent(Serial.class));
            if(fields[i].isAnnotationPresent(Serial.class))
            {
                idField = i;
            }
        }
        sqlStatement.append(" WHERE ").append(fields[idField].getName()).append("=").append(primaryKey).append(" RETURNING *;");

        if(tableExists(conn,clazz.getSimpleName())) {
            System.out.println(sqlStatement);
            PreparedStatement ps = conn.prepareStatement(String.valueOf(sqlStatement));
            ResultSet rs = ps.executeQuery();
            return rs;
//            executeStatement(sqlStatement.toString());
//            return entity;
        }else {
            return null;
        }
    }
    public ResultSet get(int primaryKey) throws SQLException {
        Field[] fields = clazz.getDeclaredFields();
        int idField = 0;
        StringBuilder sqlStatement = new StringBuilder("SELECT * FROM " + clazz.getSimpleName() + " WHERE ");

        for (int i = 0; i < fields.length; i++) {
            // System.out.println(fields[i].isAnnotationPresent(Serial.class));
            if (fields[i].isAnnotationPresent(Serial.class)) {
                idField = i;
            }
        }
        sqlStatement.append(fields[idField].getName()).append("=").append(primaryKey);

//        Method method = entity.getClass().getMethod(getMethod("get" + fields[i].getName()));
//        int id = (int)method.invoke(entity);
        if(tableExists(conn,clazz.getSimpleName())) {
            System.out.println(sqlStatement);
            PreparedStatement ps = conn.prepareStatement(String.valueOf(sqlStatement));
            ResultSet rs = ps.executeQuery();
            return rs;
        }else {
            return null;
        }
    }

    private E buildObject(ResultSet rs) throws SQLException {
        // ObjectType instance = Activator.CreateInstance<ObjectType>();
        return null;
    }

    //args paramter

    //Select * from student where id=1, age>10
    public List<E> get(String... condition){
       // List<String> conditions = new ArrayList<>(condition.)
        return null;
    }
}
