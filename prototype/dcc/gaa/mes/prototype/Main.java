package dcc.gaa.mes.prototype;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import br.ufmg.aserg.topicviewer.control.distribution.DistributionMapCalculator;
import br.ufmg.aserg.topicviewer.gui.distribution.DistributionMap;
import br.ufmg.aserg.topicviewer.gui.distribution.DistributionMapPanel;
import br.ufmg.aserg.topicviewer.util.UnsufficientNumberOfColorsException;

public class Main {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
//	static final String DB_URL = "jdbc:mysql://localhost:3306/gitresearch";
		   static final String DB_URL = "jdbc:mysql://localhost:3306/";
	//	   static final String DB_URL = "jdbc:mysql://localhost:3306/gitelasticsearch";

	//  Database credentials
	static final String USER = "git";
	static final String PASS = "git";

	static Set<UserInfoData> usersInfo;

	public static void main(String[] args) {
		Rank rank;
		
//		System.out.println("GitResearch");
//		rank = new Rank(getCommitFiles("gitresearch"), "gitresearch");		
//		distributionMap(getMap(rank), "Rank GitResearch");
//		
//		System.out.println("\n\nJUnit");
//		rank = new Rank(getCommitFiles("gitjunit"), "gitjunit");		
//		distributionMap(getMap(rank), "Rank JUnit");
		
		System.out.println("\n\nElasticSearch");
		rank = new Rank(getCommitFiles("gitelasticsearch"), "gitelasticsearch");		
		distributionMap(getMap(rank), "Rank ElasticSearch");
		
		
		System.out.println();
		distributionMap(getAuthorMap(rank), "Author");
		

//		for (UserFileRank userFile : rank.getCompleteRank()) {
//			System.out.println(userFile);
//		}
//		System.out.println("\n------------------------------\n");
//		for (String projectName : Util.getProjectNames()) {
//			System.out.println("Projeto:  "+projectName);
//			for (String user : Util.getAllUsers(projectName)) {
////				System.out.println("user = " + user);
//				printUserFiles(rank, user);
//			}
//		}
	}
	
	
	
	private static Map<String, Set<String>> getMap(Rank rank){
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		for (UserFileRank userFile : rank.getOnlyJavaFilesRank()) {
			if (!map.containsKey(userFile.getUser()))
				map.put(userFile.getUser(), new HashSet<String>());
			map.get(userFile.getUser()).add(userFile.getFilename());
		}
		return map;
	}
	private static Map<String, Set<String>> getAuthorMap(Rank rank){
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		for (UserFileRank userFile : rank.getOnlyJavaFilesRankAuthor()) {
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
			sql = "SELECT gcuser.DATE, gfile.FILENAME, gfile.STATUS, gCuser.NAME, gfile.ADDITIONS, gfile.DELETIONS, grc.SHA, grc.COMMIT_ID ,gcommit.MESSAGE FROM gitrepositorycommit_gitcommitfile gg "
					+ "JOIN gitrepositorycommit grc ON (grc.SHA = gg.GitRepositoryCommit_SHA) "
					+ "JOIN gitcommitfile gfile ON (gfile.ID = gg.files_ID) "
					+ "JOIN gitcommit gcommit on grc.COMMIT_ID = gcommit.ID "
					+ "JOIN gitcommituser gcuser on gcommit.AUTHOR_ID = gcuser.ID;";
//			sql = "SELECT gcuser.DATE, gfile.FILENAME, gfile.STATUS, gcuser.NAME, gfile.ADDITIONS, gfile.DELETIONS, grc.SHA, grc.COMMIT_ID ,gcommit.MESSAGE FROM gitrepositorycommit_gitcommitfile gg"
//					+ "JOIN gitcommitfile gfile ON (gfile.ID = gg.files_ID) "
//					+ "JOIN gitrepositorycommit grc ON (grc.SHA = gg.GitRepositoryCommit_SHA) "
//					+ "JOIN gitcommit gcommit on grc.COMMIT_ID = gcommit.ID "
//					+ "JOIN gitcommituser gcuser on gcommit.AUTHOR_ID = gcuser.ID;";
			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			while(rs.next()){
				//Retrieve by column name
				String fileName = rs.getString("filename");
				String status = rs.getString("status");
//				String login = rs.getString("login");
				String login = rs.getString("name");
				int additions  = rs.getInt("additions");
				int deletions = rs.getInt("deletions");
				String sha = rs.getString("sha");
				int commitId  = rs.getInt("commit_id");
				String message = rs.getString("message");
//				Date date = rs.getDate("date");
				Timestamp time = rs.getTimestamp("date");
				
//				String simpleFileName = fileName.substring(fileName.lastIndexOf('/')+1,fileName.length());
				
				cFiles.add(new CommitFile(time, fileName, Status.getStatus(status), 
						login, additions, deletions, sha, commitId, message));
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
		for (Entry<String, Set<String>> entry : maps.entrySet()) {
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
