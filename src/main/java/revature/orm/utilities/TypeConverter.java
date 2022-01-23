package revature.orm.utilities;

//Convert java variable types into sql data type
// class java.lang.String -> VARCHAR(255)
// int -> int
// double, float -> decimal
public class TypeConverter {
    //return string presentation of sql data type
    static String convert(String javaType){
        String sqlType="";
        if(javaType.equals("class java.lang.String")){
            sqlType="VARCHAR(255)";
        }else if(javaType.equals("int")){
            sqlType="int";
        }else if(javaType.equals("float") || javaType.equals("double")){
            sqlType="decimal";
        }
        return sqlType;
    }
}
