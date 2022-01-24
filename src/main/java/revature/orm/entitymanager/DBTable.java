package revature.orm.entitymanager;

import revature.orm.annotation.PrimaryKey;
import revature.orm.annotation.Serial;
import revature.orm.connection.JDBCConnection;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

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
    // print out sql statement
    public E insertInto(E entity){
        return entity;
    }

    public E update(int primaryKey,E entity){
        return entity;
    }

    public E delete(int primaryKey){
        return null;
    }
    public E get(int primaryKey){
        return null;
    }
    //aaaaaa

    //args paramter

    //Select * from student where id=1, age>10
    public List<E> get(String... condition){
       // List<String> conditions = new ArrayList<>(condition.)
        return null;
    }
}
