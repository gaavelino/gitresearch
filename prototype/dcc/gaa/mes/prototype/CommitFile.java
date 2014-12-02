package dcc.gaa.mes.prototype;

import java.sql.Timestamp;
import java.util.Date;

public class CommitFile implements Comparable<CommitFile> {
	
	private String fileName;
	private Status status;
	private String login;
	private String name;
	private String email;
	private int additions;
	private int deletions;
	private String sha;
	private int commitId;
	private String message;
	private Timestamp date;
	
	public CommitFile(Timestamp date, String fileName, Status status, String login,
			int additions, int deletions, String sha, int commitId,
			String message) {
		super();
		this.date = date;
		this.fileName = fileName;
		this.status = status;
		this.login = login;
		this.additions = additions;
		this.deletions = deletions;
		this.sha = sha;
		this.commitId = commitId;
		this.message = message;
	}
	
	
	public CommitFile(Timestamp date, String fileName, Status status, String login,
			String name, String email, int additions, int deletions,
			String sha, int commitId, String message) {
		super();
		this.fileName = fileName;
		this.status = status;
		this.login = login;
		this.name = name;
		this.email = email;
		this.additions = additions;
		this.deletions = deletions;
		this.sha = sha;
		this.commitId = commitId;
		this.message = message;
		this.date = date;
	}


	@Override
	public String toString() {
		return date + ", " + fileName + ", " + status + ", " + login + ", " + name + ", " + email; //+ ", " + additions  + ", " + deletions + ", " + sha + ", " + commitId + ", " + message; 
	}
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public int getAdditions() {
		return additions;
	}

	public void setAdditions(int additions) {
		this.additions = additions;
	}

	public int getDeletions() {
		return deletions;
	}

	public void setDeletions(int deletions) {
		this.deletions = deletions;
	}

	public String getSha() {
		return sha;
	}

	public void setSha(String sha) {
		this.sha = sha;
	}

	public int getCommitId() {
		return commitId;
	}

	public void setCommitId(int commitId) {
		this.commitId = commitId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public int compareTo(CommitFile o) {
		// TODO Auto-generated method stub
		return this.date.compareTo(o.date);
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Timestamp date) {
		this.date = date;
	}
	
	
	
}
