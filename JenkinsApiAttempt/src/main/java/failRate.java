import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildResult;
import com.offbytwo.jenkins.model.JobWithDetails;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class failRate {
    public  static void main(String[] args){
        String jobName = "Admin_ms-ci-visualization";
        System.out.println(getFailureSuccessRate(jobName));
    }

    public static List<Float> getFailureSuccessRate(String jobName){
        List<Float> failureSuccessRate = new ArrayList<>();
        try{
            JenkinsServer jenkins = new JenkinsServer(new URI("https://fem1s11-eiffel216.eiffel.gic.ericsson.se:8443/jenkins/"), //jenkins/job/eric-csm-p_PreCodeReview/
                    "efolhar", "Sp5t0303!!");
            JobWithDetails job = jenkins.getJob(jobName);
            List<Build> buildList = job.getAllBuilds();
            float failureNum = 0;
            float successNum = 0;
            for(Build b : buildList){
                if(b.details().getResult() == BuildResult.FAILURE)  failureNum++;
                if(b.details().getResult() == BuildResult.SUCCESS)  successNum++;
            }
            failureSuccessRate.add((failureNum/buildList.size())*100);
            failureSuccessRate.add((successNum/buildList.size())*100);

        } catch(Exception e){}
        return failureSuccessRate;
    }
}
