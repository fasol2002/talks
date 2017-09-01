package mgm;

public class Person {

	private String sex;
	private String firstname;
	private String count;
	
	public Person(String line) {
		String[] args = line.split("\\s+");
		setSex(args[0]);
		setFirstname(args[1]);
		setCount(args[2]);
	}
	
	@Override
	public String toString() {
		return sex + " - " + firstname + " - "  + count;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

}
