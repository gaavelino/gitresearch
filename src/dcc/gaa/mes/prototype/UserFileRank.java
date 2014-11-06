package dcc.gaa.mes.prototype;

public class UserFileRank {
	String filename;
	String user;
	float value;

	public UserFileRank(String filename, String user) {
		super();
		this.filename = filename;
		this.user = user;
		this.value = 0;
	}

	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public Float getValue() {
		return value;
	}
	public void setValue(Float value) {
		this.value = value;
	}

	public void incValue(float incValue){
		this.value += incValue;
	}
	
	public void decValue(float decValue){
		this.value -= decValue;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return filename + " = " + user + " (" + value + ")";
	}
}
