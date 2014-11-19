package dcc.gaa.mes.prototype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rank {
	private List<CommitFile> commits;
	private Map<String, FileRank> mapFileRank;
	private String projectName;
	
	public Rank(List<CommitFile> commits, String projectName) {
		this.commits =  commits;
		this.projectName = projectName;
		this.mapFileRank = new HashMap<String, FileRank>();		
		fillMap();		
	}

	private void fillMap() { 
		for (CommitFile commitFile : commits) {
			String fileName = commitFile.getFileName();
			if (!mapFileRank.containsKey(fileName)){
				mapFileRank.put(fileName, new FileRank(fileName, new ArrayList<CommitFile>()));
			}
			mapFileRank.get(fileName).addCommit(commitFile);
		}
	}
	
	public List<UserFileRank> getOnlyJavaFilesRank(){
		List<UserFileRank> allRank = getCompleteRank();
		List<UserFileRank> filteredRank =  new ArrayList<UserFileRank>();
		for (UserFileRank userFileRank : allRank) {
			String filename = userFileRank.getFilename();
			if (filename.substring(filename.lastIndexOf('.')+1, filename.length()).equals("java"))
				filteredRank.add(userFileRank);
		}
		return filteredRank;
	}
	public List<UserFileRank> getOnlyJavaFilesRankAuthor(){
		List<UserFileRank> allRank = getCompleteRankAuthor();
		List<UserFileRank> filteredRank =  new ArrayList<UserFileRank>();
		for (UserFileRank userFileRank : allRank) {
			String filename = userFileRank.getFilename();
			if (filename.substring(filename.lastIndexOf('.')+1, filename.length()).equals("java"))
				filteredRank.add(userFileRank);
		}
		return filteredRank;
	}
	public List<UserFileRank> getCompleteRankAuthor(){
		List<UserFileRank> completeRank = new ArrayList<UserFileRank>();
		for (FileRank fileRank : mapFileRank.values()) {
			UserFileRank userFileRank = fileRank.getFileAuthor();
			if (userFileRank != null){
				completeRank.add(userFileRank);
				Util.addUser(userFileRank.getUser(), projectName);
			}
		}
		return completeRank;
	}
	
	public List<UserFileRank> getCompleteRank(){
		List<UserFileRank> completeRank = new ArrayList<UserFileRank>();
		for (FileRank fileRank : mapFileRank.values()) {
			UserFileRank userFileRank = fileRank.getOwner();
			if (userFileRank != null){
				completeRank.add(userFileRank);
				Util.addUser(userFileRank.getUser(), projectName);
			}
		}
		return completeRank;
	}
	
}
