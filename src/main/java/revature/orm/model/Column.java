package revature.orm.model;

public class Column {
    private String column;
    private String datatype;

    public Column(String column, String datatype) {
        this.column = column;
        this.datatype = datatype;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }
}
