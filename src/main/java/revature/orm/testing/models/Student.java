package revature.orm.testing.models;

import revature.orm.annotation.Entity;
import revature.orm.annotation.ForeignKey;
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

    @ForeignKey(entity="School",field="id")
    private int schoolId;

    public int getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public Student() {
    }

    public Student(int id, String firstName, String lastName, int age) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", schoolId=" + schoolId +
                '}';
    }
}
