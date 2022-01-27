package revature.orm.testing.models;

import revature.orm.annotation.Entity;
import revature.orm.annotation.ForeignKey;
import revature.orm.annotation.PrimaryKey;
import revature.orm.annotation.Serial;

import java.sql.Date;

@Entity
public class Student {
    @PrimaryKey
    @Serial
    private int id;
    private String firstName;
    private String lastName;
    private int age;
    private Date enrollDate;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    private String gender;
    private int grade;


    @ForeignKey(field="id")
    private School school;

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public Student() {
    }

    public Student(int id, String firstName, String lastName, int age, Date enrollDate, String gender, int grade, School school) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.enrollDate = enrollDate;
        this.gender = gender;
        this.grade = grade;
        this.school = school;
    }

    public Student(String firstName, String lastName, int age, Date enrollDate, String gender, int grade, School school) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.enrollDate = enrollDate;
        this.gender = gender;
        this.grade = grade;
        this.school = school;
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
                ", enrollDate=" + enrollDate +
                ", gender='" + gender + '\'' +
                ", grade=" + grade +
                ", school=" + school +
                '}';
    }

    public Date getEnrollDate() {
        return enrollDate;
    }

    public void setEnrollDate(Date enrollDate) {
        this.enrollDate = enrollDate;
    }
}
