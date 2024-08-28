import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.*;

import java.net.URI;
import java.util.List;

public class JenkinsApiService {
    private JenkinsServer jenkinsServer;
    private int passFailRate;
    private int numRunsInPeriod;
    private long executeTime;
    private long failToSuccessTime;

    public JenkinsApiService(String jobName){
        try{
            jenkinsServer = new JenkinsServer(new URI("https://fem1s11-eiffel216.eiffel.gic.ericsson.se:8443/jenkins/"),
                    "efolhar", "Sp5t0303!!");
            JobWithDetails jbd =  jenkinsServer.getJob(jobName);
            List<Build> buildList = jbd.getAllBuilds();
            for(Build b: buildList){
                BuildWithDetails details = b.details();
                executeTime = details.getDuration();
                break;
            }

        }
        catch(Exception e){System.out.println(e);}
    }

    public int getNumRunsInPeriod() {
        return numRunsInPeriod;
    }

    public int getPassFailRate() {
        return passFailRate;
    }

    public long getExecuteTime() {
        return executeTime;
    }

    public long getFailToSuccessTime() {
        return failToSuccessTime;
    }
}
