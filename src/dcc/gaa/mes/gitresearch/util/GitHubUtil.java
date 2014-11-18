package dcc.gaa.mes.gitresearch.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.egit.github.core.CommitFile;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.UserService;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dcc.gaa.mes.gitresearch.GitHubService;
import dcc.gaa.mes.gitresearch.MyGitHubClient;
import dcc.gaa.mes.gitresearch.GitHubService.RepoInfo;
import dcc.gaa.mes.gitresearch.dao.RepositoryDAO;
import dcc.gaa.mes.gitresearch.dao.ResearchDAO;
import dcc.gaa.mes.gitresearch.dao.UserDAO;
import dcc.gaa.mes.gitresearch.model.GitComment;
import dcc.gaa.mes.gitresearch.model.GitCommitComment;
import dcc.gaa.mes.gitresearch.model.GitCommitFile;
import dcc.gaa.mes.gitresearch.model.GitCommitStats;
import dcc.gaa.mes.gitresearch.model.GitIssue;
import dcc.gaa.mes.gitresearch.model.GitPullRequest;
import dcc.gaa.mes.gitresearch.model.GitRepository;
import dcc.gaa.mes.gitresearch.model.GitRepositoryCommit;
import dcc.gaa.mes.gitresearch.model.GitResearch;
import dcc.gaa.mes.gitresearch.model.GitUser;

public class GitHubUtil {
	
	private static final Logger logger = Logger.getLogger(GitHubUtil.class);
	
	public static SearchRepository createFakeSearchRepository(GitRepository gitRepository){
		SearchRepository searchRepository =  new SearchRepository(gitRepository.getOwner(), gitRepository.getName());
		return searchRepository;
	}
	
	public static void searchAndInsert(Set<String> tokens, HashMap<String, String> keywords) throws IOException {
		logger.trace("GitHubUtil.searchAndInsert(Set, HashMap)");
		
		GitHubService gitHubservice = new GitHubService(new MyGitHubClient(tokens));
		
		int page = 1;
		List<GitRepository> repositories;
		do {
			repositories = gitHubservice.searchRepositories(keywords, page, page++);
			for (int i = 0; i < repositories.size(); i++) {
//				GitRepository repo = repositories.get(i);
//				List<GitIssue> issues = gitHubservice.getAllIssues(repo);
//				List<GitPullRequest> pullRequests =  gitHubservice.getPullRequests(repo);
//				for (GitPullRequest gitPullRequest : pullRequests) {
//					for (GitIssue gitIssue : issues) {
//						if (gitIssue.getNumber() == gitPullRequest.getNumber()){
//							gitIssue.setPullRequest(gitPullRequest);
////							for (GitComment comment : gitIssue.getGitComments()) {
////								GitCommitComment commitComment = new GitCommitComment();
////								gitPullRequest.setGitComments(commitComment);
////							}
//						}
//						
//					}
//				}
//				repo.setRepositoryIssues(issues);
			}
		} while (repositories.size() == 100);
		
		GitResearch research = new GitResearch(keywords, repositories);
		
		logger.debug("Number of repositories found: " + repositories.size());
		logger.debug("Persisting the repositories ...");
		new ResearchDAO().persist(research);
		logger.debug("Repositories persisted");
	}
	
	public static void printRepositoryInfo(Set<String> tokens, HashMap<String, String> keywords) throws IOException {
		logger.trace("GitHubUtil.searchRepositories(Set, HashMap)");
		
		GitHubService gitHubservice = new GitHubService(new MyGitHubClient(tokens));
		
		
		
		gitHubservice.printRepositoryInfo(tokens, keywords);
	}
	
	public static final Date getResetTime(String token) throws IOException {
		logger.trace("GitHubUtil.getResetTime(String)");
		
		String url = "https://api.github.com/rate_limit?access_token=" + token;
		URL obj = new URL(url);
		
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		if (con.getResponseCode() == 200) {
			StringWriter writer = new StringWriter();
			InputStream inputStream = con.getInputStream();
			IOUtils.copy(inputStream, writer);
			String theString = writer.toString();
			inputStream.close();
			
			JsonElement json = new JsonParser().parse(theString);
			JsonObject resources = json.getAsJsonObject().getAsJsonObject("resources");
			JsonObject core = resources.getAsJsonObject("core");
			return new Date(core.getAsJsonPrimitive("reset").getAsLong() * 1000);
		}
		
		return null;
	}
	
	public static final void updateRepository(Set<String> tokens, GitRepository gitRepository) throws IOException {
		logger.trace("GitHubUtil.updateRepository(Set<String>, GitRepository)");
		
		logger.debug("Updating the repository " + gitRepository.getName());
		RepositoryId repositoryId = new RepositoryId(gitRepository.getOwner(), gitRepository.getName());
		CommitService commitService = new CommitService(new MyGitHubClient(tokens));
		
		for (GitRepositoryCommit rc : gitRepository.getRepositoryCommits()) {
			RepositoryCommit repositoryCommit = commitService.getCommit(repositoryId, rc.getSha());
			
			rc.setStats(new GitCommitStats(repositoryCommit.getStats()));
			rc.setFiles(new LinkedList<GitCommitFile>());
			for (CommitFile cf : repositoryCommit.getFiles()) {
				rc.getFiles().add(new GitCommitFile(cf));
			}
			
		}
		
		logger.debug("Updating database ... ");
		new RepositoryDAO().merge(gitRepository);
		
		logger.debug("Repository " + gitRepository.getName() +  " updated");
	}
	
	
	public static final void updateUser(Set<String> tokens, GitUser gitUser) throws IOException {
	    logger.trace("GitHubUtil.updateUser(Set<String>, GitUser)");
	    
	    UserService userService = new UserService(new MyGitHubClient(tokens));
	    
	    logger.debug("Updating " + gitUser.getLogin() + " user");
	    User user = userService.getUser(gitUser.getLogin());
	    gitUser = new GitUser(user);
	    
	    logger.debug("Updating database ... ");
	    new UserDAO().merge(gitUser);

	    logger.debug("User " + gitUser.getLogin() + " updated");
	}
	
}
