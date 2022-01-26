package revature.orm.testing.models;

import revature.orm.annotation.PrimaryKey;
import revature.orm.annotation.Serial;

public class School {
    @Serial
    @PrimaryKey
    private int id;
    private String name;

    public School(){}
    public School(int id, String name) {
        this.id = id;
        this.name = name;
    }
    public School(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "School{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
