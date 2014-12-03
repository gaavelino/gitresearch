package dcc.gaa.mes.prototype;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import br.ufmg.aserg.topicviewer.control.distribution.DistributionMapCalculator;
import br.ufmg.aserg.topicviewer.gui.distribution.DistributionMap;
import br.ufmg.aserg.topicviewer.gui.distribution.DistributionMapPanel;
import br.ufmg.aserg.topicviewer.util.UnsufficientNumberOfColorsException;

public class TestGitHubProjects {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
//	static final String DB_URL = "jdbc:mysql://localhost:3306/gitresearch";
	static final String DB_URL = "jdbc:mysql://localhost:3306/";
//	static final String DB_URL = "jdbc:mysql://localhost:3306/gitelasticsearch";

//  Database credentials
	static final String USER = "git";
	static final String PASS = "git";

	static Set<UserInfoData> usersInfo;

	public static void main(String[] args) {
		String projectName = "gitjunit";
//		String projectName = "gitelasticsearch";
		System.out.println(projectName);
		List<CommitFile> cFiles = getCommitFiles(projectName);
		Collections.sort(cFiles);
		CommitFile baseFile =  getBaseFile(cFiles,true);
		CommitFile lastFile = cFiles.get(cFiles.size()-1);
		long diff = lastFile.getDate().getTime() - baseFile.getDate().getTime();
		float per = 0.00f;
			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);
		System.out.println(diffDays + " - " + diffHours + " - " + diffMinutes + " - " + diffSeconds);	
		int ccount=0;
		for (per = 0.0f; per < 1.0f; per+=0.01) {
			int countBefore = 0;
			int countAfter = 0;
			Date cutDate = new Date(baseFile.getDate().getTime()
					+ ((long) (diff * per)));
			long cutDays = ((long) (diff * per)) / (24 * 60 * 60 * 1000);
			ccount=0;
			Status status = Status.MODIFIED; 
			for (CommitFile commitFile : cFiles) {
				if (commitFile.getDate().compareTo(baseFile.getDate())>=0) {
					if (commitFile.getDate().compareTo(cutDate) <= 0
							&& commitFile.getStatus() == status)
						countBefore++;
					if (commitFile.getDate().compareTo(cutDate) > 0
							&& commitFile.getStatus() == status)
						countAfter++;
					if (commitFile.getStatus() == status)
						ccount++;
				}

				//			System.out.println(commitFile);
			}
//			System.out.println("CutDate = " + cutDate + "(" + cutDays
//					+ " dias) " + "\nCommitsBefore = " + countBefore
//					+ "\nCommitsAfter = " + countAfter);
			System.out.println(per +" , " + cutDays + " , " + countBefore + " , " + countAfter);
//			System.out.println(countBefore);
		}
		System.out.println(ccount);
	}
	
	
	
	private static CommitFile getBaseFile(List<CommitFile> cFiles, boolean flag) {
		if (!flag)
			return cFiles.get(0);
		else{
			for (CommitFile commitFile : cFiles) {
				if (commitFile.getLogin()!=null){
					System.out.println("First commit with author in:  " + commitFile.getDate());
					return commitFile;
				}
			}
		}
		return null;
	}



	private static List<CommitFile> getCommitFiles(String database, String filter) {
		List<CommitFile> allCommitFiles = getCommitFiles(database);
		List<CommitFile> filteredCommitFiles = new ArrayList<CommitFile>();
		for (CommitFile commitFile : allCommitFiles) {
			if (commitFile.getFileName().contains(filter))
				filteredCommitFiles.add(commitFile);
		}
		return filteredCommitFiles;
	}



	private static Map<String, Set<String>> getMap(Rank rank){
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		List<UserFileRank> userFiles = rank.getOnlyJavaFilesRank();
		for (UserFileRank userFile : userFiles) {
			if (!map.containsKey(userFile.getUser()))
				map.put(userFile.getUser(), new HashSet<String>());
			map.get(userFile.getUser()).add(userFile.getFilename());
		}
		return map;
	}
	private static Map<String, Set<String>> getAuthorMap(Rank rank){
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		List<UserFileRank> userFiles = rank.getOnlyJavaFilesRankAuthor();
		for (UserFileRank userFile : userFiles) {
			if (!map.containsKey(userFile.getUser()))
				map.put(userFile.getUser(), new HashSet<String>());
			map.get(userFile.getUser()).add(userFile.getFilename());
		}
		return map;
	}
	private static void printUserFiles(Rank rank, String user) {
		for (UserFileRank userFile : rank.getCompleteRank()) {
			String filename = userFile.getFilename();
			
			if (user.equals(userFile.getUser()) && filename.substring(filename.lastIndexOf('.')+1, filename.length()).equals("java")) 
					System.out.print(filename+",");
		}
		System.out.println();
	}
	
	private static List<CommitFile> getCommitFiles(String database) {
		Connection conn = null;
		Statement stmt = null;
		List<CommitFile> cFiles = new ArrayList<CommitFile>();
		
		try{
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			//STEP 3: Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL+database,USER,PASS);

			//STEP 4: Execute a query
			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT gcuser.DATE, gfile.FILENAME, gfile.STATUS, grc.AUTHOR_ID, grc.COMMITTER_ID, gcuser.NAME, gcuser.EMAIL, gfile.ADDITIONS, gfile.DELETIONS, grc.SHA, grc.COMMIT_ID ,gcommit.MESSAGE FROM gitrepositorycommit_gitcommitfile gg "
					+ "JOIN gitrepositorycommit grc ON (grc.SHA = gg.GitRepositoryCommit_SHA) "
					+ "JOIN gitcommitfile gfile ON (gfile.ID = gg.files_ID) "
					+ "JOIN gitcommit gcommit on grc.COMMIT_ID = gcommit.ID "
					+ "JOIN gitcommituser gcuser on gcommit.AUTHOR_ID = gcuser.ID;";
			ResultSet rs = stmt.executeQuery(sql);
			
			//STEP 5: Extract data from result set
			while(rs.next()){
				//Retrieve by column name
				String fileName = rs.getString("filename");
				String status = rs.getString("status");
				String login = rs.getString("author_id");
				String name = rs.getString("name");
				String email = rs.getString("email");
				int additions  = rs.getInt("additions");
				int deletions = rs.getInt("deletions");
				String sha = rs.getString("sha");
				int commitId  = rs.getInt("commit_id");
				String message = rs.getString("message");
//				Date date = rs.getDate("date");
				Timestamp time = rs.getTimestamp("date");
				
//				String simpleFileName = fileName.substring(fileName.lastIndexOf('/')+1,fileName.length());
				cFiles.add(new CommitFile(time, fileName, Status.getStatus(status), 
						login, name, email, additions, deletions, sha, commitId, message));
			}
			//STEP 6: Clean-up environment
			rs.close();
			stmt.close();
			conn.close();
		}catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}finally{
			//finally block used to close resources
			try{
				if(stmt!=null)
					stmt.close();
			}catch(SQLException se2){
			}// nothing we can do
			try{
				if(conn!=null)
					conn.close();
			}catch(SQLException se){
				se.printStackTrace();
			}//end finally try
		}//end try
		return cFiles;
	}
	
	
	
	
	static void distributionMap(Map<String, Set<String>> maps, String mapName){
		usersInfo = new HashSet<UserInfoData>();
		DistributionMap dm = new DistributionMap(mapName);
		String semanticTopics[][] = new String[maps.size()][];
		int index = 0;
		int stCount = 0;
		Queue<Entry<String, Set<String>>> queuedMap = getOrderedMap(maps);
		while (!queuedMap.isEmpty()){
			Entry<String, Set<String>> entry = queuedMap.poll();
//		for (Entry<String, Set<String>> entry : getOrderedMap(maps).entrySet()) {
			String sTopics[] = new String[entry.getValue().size()];
			int i =0;
			for (String name : entry.getValue()) {
				name = name.replace(".java", "");
				name = name.replace("/", ".");
				String className = getClassName(name);
				sTopics[i++] = className;
				dm.put(getPackageName(name), className, index, 0.0);
			}
			index++;
			usersInfo.add(new UserInfoData(entry.getKey(), stCount, entry.getValue()));
			semanticTopics[stCount++] = sTopics;
			
		}
		try {
			dm = DistributionMapCalculator.addSemanticClustersMetrics(dm, maps.size());
			DistributionMapPanel dmPanel = new DistributionMapPanel(dm,semanticTopics);
			calcDMValues(dm);
			JFrame frame = new JFrame("DistributionMap - "+mapName);
			JScrollPane scrollPane = new JScrollPane(dmPanel);  
//			scrollPane.setBorder(BorderFactory.createTitledBorder("DistributionMap"));
			frame.setContentPane(scrollPane);
			frame.setVisible(true);
		} catch (UnsufficientNumberOfColorsException e1) {
			e1.printStackTrace();
		}
	}
	
	private static Queue<Entry<String, Set<String>>> getOrderedMap(
			Map<String, Set<String>> maps) {
		Map<String, Set<String>> clonedMap =  new HashMap<String, Set<String>>();
		Queue<Entry<String, Set<String>>> newMap =  new LinkedList<Entry<String, Set<String>>>();
		for (Entry<String, Set<String>> entry : maps.entrySet()) {
			clonedMap.put(entry.getKey(), entry.getValue());

		}
		
		while (!clonedMap.isEmpty()) {
			int bigSize = 0;
			Entry<String, Set<String>> bigEntry =  null;
			for (Entry<String, Set<String>> entry : clonedMap.entrySet()) {
				if (entry.getValue().size() > bigSize) {
					bigSize = entry.getValue().size();
					bigEntry = entry;
				}

			}
			if (bigEntry!=null){
				newMap.add(bigEntry);
				clonedMap.remove(bigEntry.getKey());
			}
			
		}
		return newMap;
	}



	private static void calcDMValues(DistributionMap dm) {
		for (UserInfoData userInfo : usersInfo) {
			userInfo.setFocus(dm.getFocus(userInfo.getIndex()));
			userInfo.setSpread(dm.getSpread(userInfo.getIndex()));
			System.out.println(userInfo.getUserName()+ ","+userInfo.getnFiles() + ","+userInfo.getSpread() + ","+userInfo.getFocus());
		}
	}



	static String getClassName(String id) {
		// TODO Auto-generated method stub
		if (id.contains("."))
			return id.substring(id.lastIndexOf('.')+1);
		else
			return id;
	}
	static String getPackageName(String id) {
		if (id.contains("."))
			return id.substring(0, id.lastIndexOf('.'));
		else
			return id;
	}
}
