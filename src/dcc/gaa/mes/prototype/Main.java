package dcc.gaa.mes.prototype;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main {
	 // JDBC driver name and database URL
	   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	   static final String DB_URL = "jdbc:mysql://localhost:3306/gitresearch";

	   //  Database credentials
	   static final String USER = "git";
	   static final String PASS = "git";
	   
	public static void main(String[] args) {
		List<CommitFile> cFiles = getCommitFiles();

		Rank rank = new Rank(cFiles);
		
		for (UserFileRank userFile : rank.getCompleteRank()) {
			System.out.println(userFile);
		}
		System.out.println("\n------------------------------\n");
		printUserFiles(rank, "gavelino");
		printUserFiles(rank, "hsborges");
		printUserFiles(rank, "gaavelino");


	}
	private static void printUserFiles(Rank rank, String user) {
		for (UserFileRank userFile : rank.getCompleteRank()) {
			String filename = userFile.getFilename();
			
			if (user.equals(userFile.getUser()) && filename.substring(filename.lastIndexOf('.')+1, filename.length()).equals("java")) 
					System.out.print(filename+",");
		}
		System.out.println();
	}
	private static List<CommitFile> getCommitFiles() {
		Connection conn = null;
		Statement stmt = null;
		List<CommitFile> cFiles = new ArrayList<CommitFile>();
		
		try{
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			//STEP 3: Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);

			//STEP 4: Execute a query
			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT gcuser.DATE, gfile.FILENAME, gfile.STATUS, guser.LOGIN, gfile.ADDITIONS, gfile.DELETIONS, grc.SHA, grc.COMMIT_ID ,gcommit.MESSAGE FROM gitrepositorycommit_gitcommitfile gg "
					+ "JOIN gitrepositorycommit grc ON (grc.SHA = gg.GitRepositoryCommit_SHA) "
					+ "JOIN gitcommitfile gfile ON (gfile.ID = gg.files_ID) "
					+ "JOIN gituser guser ON grc.AUTHOR_ID = guser.ID "
					+ "JOIN gitcommit gcommit on grc.COMMIT_ID = gcommit.ID "
					+ "JOIN gitcommituser gcuser on gcommit.AUTHOR_ID = gcuser.ID;";
//			sql = "SELECT  FILENAME, STATUS FROM gitcommitfile";
			ResultSet rs = stmt.executeQuery(sql);
			
			//STEP 5: Extract data from result set
			while(rs.next()){
				//Retrieve by column name
				String fileName = rs.getString("filename");
				String status = rs.getString("status");
				String login = rs.getString("login");
				int additions  = rs.getInt("additions");
				int deletions = rs.getInt("deletions");
				String sha = rs.getString("sha");
				int commitId  = rs.getInt("commit_id");
				String message = rs.getString("message");
				Date date = rs.getDate("date");
				
				String simpleFileName = fileName.substring(fileName.lastIndexOf('/')+1,fileName.length());
				
				cFiles.add(new CommitFile(date, fileName, Status.getStatus(status), 
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
}
