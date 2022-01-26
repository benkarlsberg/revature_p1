package revature.orm.testing.models;

import revature.orm.annotation.PrimaryKey;
import revature.orm.annotation.Serial;

public class School {
    @Serial
    @PrimaryKey
    private int id;
    private String name;

}
