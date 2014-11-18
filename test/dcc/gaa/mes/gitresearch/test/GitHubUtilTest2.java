package dcc.gaa.mes.gitresearch.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import dcc.gaa.mes.gitresearch.GitHubService.RepoInfo;
import dcc.gaa.mes.gitresearch.util.GitHubUtil;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GitHubUtilTest2 extends AbstractTest {

    static final Logger logger = LogManager.getLogger(GitHubUtilTest2.class);

    private String[] tokens = new String[] { "acebecaff6fbdc6213be4d478be01fc604066757", 
    										 "4999affe50d647fb6127bba6fa5dd7a654da00ed",
    										 "fea785517975ea8eefd192926a03c16ffb489748"};

    @Test
    public void test1_getResetTime() {
    	Logger.shutdown();
        logger.info("Testing GitHubUtil.getResetTime()");

        try {
            for (String token : tokens) {
                assertNotNull(GitHubUtil.getResetTime(token));
            }
            logger.info("The process was finished without problems");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    @Test
    public void test2_printRepoInfo() {
        logger.info("Testing GitHubUtil.printRepoInfo() using: ");

        HashSet<String> tokens = new LinkedHashSet<String>();
        Collections.addAll(tokens, this.tokens);

        logger.info("tokens = " + tokens);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("language", "java");
        params.put("stars", ">=1000");
//        params.put("user", "gavelino");

//        params.put("repo", "elasticsearch/elasticsearch");

        logger.info("params = " + params);

        try {
        	GitHubUtil.printRepositoryInfo(tokens, params);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
            
            System.out.println(e.getMessage());
        }
    }
//
//    @Test
//    public void test3_updateRepository() throws IOException {
//        logger.info("Testing GitHubUtil.updateRepository(Set<String>, GitRepository)");
//
//        HashSet<String> tokens = new HashSet<String>();
//        Collections.addAll(tokens, this.tokens);
//
//        for (GitResearch research : new ResearchDAO().getAll()) {
//            for (GitRepository rep : research.getRepositories()) {
//                GitHubUtil.updateRepository(tokens, rep);
//            }
//        }
//
//        logger.info("The process was finished without problems");
//    }
//    
//    @Test
//    public void test4_updateUser() throws IOException {
//        logger.info("Testing GitHubUtil.updateUser(Set<String>, GitUser)");
//        
//        HashSet<String> tokens = new HashSet<String>();
//        Collections.addAll(tokens, this.tokens);
//        
//        logger.debug("Updating all users");
//        for (GitUser gu : new UserDAO().getAll()) {
//            GitHubUtil.updateUser(tokens, gu);
//        }
//        
//        logger.info("The process was finished without problems");
//    }

}
