package dcc.gaa.mes.prototype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Util {
	static Map<String,Set<String>> mapUsers =  new HashMap<String, Set<String>>();
	
	public static void addUser(String user, String projectname){
		if (!mapUsers.containsKey(projectname))
			mapUsers.put(projectname, new HashSet<String>());
		mapUsers.get(projectname).add(user);
	}
	
	public static Set<String> getAllUsers(String projectname){
		return mapUsers.get(projectname);
	}
	
	public static Set<String> getProjectNames(){
		return mapUsers.keySet();
	}
}
