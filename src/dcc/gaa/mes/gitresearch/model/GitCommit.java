package dcc.gaa.mes.gitresearch.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.eclipse.egit.github.core.Commit;

@SuppressWarnings("serial")
@Entity
public class GitCommit implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(cascade = { CascadeType.ALL })
	private GitCommitUser author;

	@ManyToOne(cascade = { CascadeType.ALL })
	private GitCommitUser committer;

	private int commentCount;

	@OneToMany(cascade = { CascadeType.REFRESH })
	private List<GitCommit> parents;

	@Lob
	private String message;

	private String sha;

	private String url;
	
	@OneToMany(cascade = { CascadeType.ALL })
	private List<GitCommitComment> gitComments;

	// TODO Avaliar necessidade desse atributo
	// private Tree tree;

	public GitCommit() {
		super();
	}

	public GitCommit(Commit commit) {
		if (commit != null) {
			if (commit.getAuthor() != null) {
				this.author = new GitCommitUser(commit.getAuthor());
			}
			
			if (commit.getCommitter() != null) {
				this.committer = new GitCommitUser(commit.getCommitter());
			}
			
			this.commentCount = commit.getCommentCount();
			
			this.parents = new ArrayList<GitCommit>();
			if (commit.getParents() != null) {
				for (Commit parentCommit : commit.getParents()) {
					if (parentCommit != null) {
						parents.add(new GitCommit(parentCommit));
					}
				}
			}
			
			this.message = commit.getMessage();
			this.sha = commit.getSha();
			this.url = commit.getUrl();
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public GitCommitUser getAuthor() {
		return author;
	}

	public void setAuthor(GitCommitUser author) {
		this.author = author;
	}

	public GitCommitUser getCommitter() {
		return committer;
	}

	public void setCommitter(GitCommitUser committer) {
		this.committer = committer;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public List<GitCommit> getParents() {
		return parents;
	}

	public void setParents(List<GitCommit> parents) {
		this.parents = parents;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSha() {
		return sha;
	}

	public void setSha(String sha) {
		this.sha = sha;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "GitCommit [id=" + id + ", sha=" + sha + "]";
	}

	public List<GitCommitComment> getGitComments() {
		return gitComments;
	}

	public void setGitComments(List<GitCommitComment> gitComments) {
		this.gitComments = gitComments;
	}
	
}