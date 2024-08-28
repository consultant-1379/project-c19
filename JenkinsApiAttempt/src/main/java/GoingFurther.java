import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.model.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class GoingFurther {

    static String uri = "https://fem1s11-eiffel216.eiffel.gic.ericsson.se:8443/jenkins/";
    public static void main(String[] args){
        try{
            JenkinsServer jenkinsServer = new JenkinsServer(new URI(uri), //jenkins/job/eric-csm-p_PreCodeReview/
                    "efolhar", "Sp5t0303!!");
            JobWithDetails job = jenkinsServer.getJob("eric-csm-p_PreCodeReview");
            System.out.println("Time: " + job.getLastBuild().details().getTimestamp());
            List<Build> buildList = job.getAllBuilds();
            List<BuildWithDetails> buildWithDetailsList = new ArrayList<>();
            for(Build b : buildList ){
                buildWithDetailsList.add(b.details());
            }

            float numFail = 0;
            float numSuccess= 0;

            for(BuildWithDetails buildWithDetails : buildWithDetailsList){
                if(buildWithDetails.getResult() == BuildResult.FAILURE) numFail++;
                if(buildWithDetails.getResult() == BuildResult.SUCCESS) numSuccess++;
            }
            System.out.println(numFail);

            System.out.println("The failure rate is: " + (numFail/buildList.size())*100);
            System.out.println("The success rate is: " + (numSuccess/buildList.size())*100);


        }
        catch(Exception e){
            System.out.println(e);
            System.out.println(e.getCause());
            System.out.println("End of exception");
        }
    }
}
