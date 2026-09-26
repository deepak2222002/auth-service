package web.minda.project.entity;

public class JwtRequest {

	private String employeeId;
	
	private String email;

	private String password;

	private String role;

	private String department;

	private String name;

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public JwtRequest(String employeeId, String email, String password, String role, String department, String name) {
		super();
		this.employeeId = employeeId;
		this.email = email;
		this.password = password;
		this.role = role;
		this.department = department;
		this.name = name;
	}

	public JwtRequest() {
		super();
		// TODO Auto-generated constructor stub
	}


}
