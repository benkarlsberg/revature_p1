# Reference to Web Appication Github: https://github.com/ntluandt95/project1WebApp
	
### Annotation
* @ForeignKey(field=id): mark foreignKey field and specify to reference field.
* @PrimaryKey: mark primaryKey field
* @Serial: mark serial type, must be int

### Database connection configuration
* Using property file (Place database connection information into /src/main/resources/application.properties <br>
###### Example:
	endpoint=http://localhost:8080
	username=username
	password=password

### Creating an instance - Java Syntax
	DBTable<Student> studentDB = new DBTable<>(Student.class);

### Automatically create table
* After the instantiation of DBTable. The table and its related table will be created automatically if it doesn’t exist.

### Link table - Annotation syntax (Foreign Keys)
	@ForeignKey(field="id")
	private School school;

### Check fields -> Alter table
* If the fields of table is not equivalent with the models. The table will be altered base on its corresponding model.

### CRUD operation
* Support all CRUD operation: Create, read, update and delete 

### Complex query
* select data from a table based on multiple conditions.
    	studentDB.get("name='First_Name_Here'")
		
### Junit Tests/Code Coverage
* 80% Code Coverage. Reports located in src/main/resources
