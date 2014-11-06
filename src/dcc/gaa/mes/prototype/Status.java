package dcc.gaa.mes.prototype;

public enum Status {
	ADDED, MODIFIED, RENAMED, REMOVED;
	
	public static Status getStatus(String str){
		if (str.equals("added"))
			return ADDED; 
		if (str.equals("modified"))
			return MODIFIED; 
		if (str.equals("renamed"))
			return RENAMED; 
		if (str.equals("removed"))
			return REMOVED;
		
		return null;				
	}
}
