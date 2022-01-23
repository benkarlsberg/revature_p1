package revature.orm.testing.models;

import revature.orm.annotation.Entity;
import revature.orm.annotation.PrimaryKey;
import revature.orm.annotation.Serial;

@Entity
public class Student {
    @PrimaryKey
    @Serial
    private int id;

    private String firstName;
    private String lastName;
    private int age;

    public Student(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
