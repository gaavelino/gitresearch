package dcc.gaa.mes.prototype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rank {
	private List<CommitFile> commits;
	private Map<String, FileRank> mapFileRank;
	
	public Rank(List<CommitFile> commits) {
		this.commits =  commits;
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
	
	public List<UserFileRank> getCompleteRank(){
		List<UserFileRank> completeRank = new ArrayList<UserFileRank>();
		for (FileRank fileRank : mapFileRank.values()) {
			completeRank.add(fileRank.getOwner());
		}
		return completeRank;
	}
	
}
