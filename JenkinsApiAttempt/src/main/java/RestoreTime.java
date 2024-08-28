import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.JobWithDetails;

import java.io.IOException;
import java.net.URI;
import java.util.List;

public class RestoreTime {
    public static void main(String[] args){
        System.out.println(getDeliveryTime("eric-oss-testrepo1_Publish"));

    }

    public static String getRestoreTime(final String jobName){
        long restoreTimeNanoSeconds = -1;
        int nextSuccessfulBuildNum = -1;
        int failBuildNum = -1;
        List<Build> buildList = null;

        try{
            JenkinsServer jenkins = new JenkinsServer(new URI("https://fem1s11-eiffel216.eiffel.gic.ericsson.se:8443/jenkins/"), //jenkins/job/eric-csm-p_PreCodeReview/
                    "efolhar", "Sp5t0303!!");


            JobWithDetails jobWithDetails = jenkins.getJob(jobName);
            failBuildNum = jobWithDetails.getLastFailedBuild().getNumber();

            if(failBuildNum < 0){
                return  "there have been no failed builds for this job";
            }

            buildList = jobWithDetails.getAllBuilds();
            int failIndex = getIndex(failBuildNum, buildList);
            long failBuildTimeStamp = buildList.get(failIndex).details().getTimestamp();
            boolean notFound = true;
            while(notFound){
                failIndex = failIndex - 1;
                if(buildList.get(failIndex).details().getResult().toString() == "SUCCESS"){
                    nextSuccessfulBuildNum = failIndex;
                    notFound = false;
                }
            }

            if(notFound){
                return "There has been no successful build since build number " + buildList.get(failBuildNum).getNumber();
            }

            long successBuildTimeStamp = buildList.get(failIndex).details().getTimestamp();
            restoreTimeNanoSeconds = successBuildTimeStamp-failBuildTimeStamp;
        }
        catch(Exception e) {

        }
        return "Time between failed build " + buildList.get(failBuildNum).getNumber() + " and next successful build " + buildList.get(nextSuccessfulBuildNum).getNumber()
                + " was " + restoreTimeNanoSeconds + " NanoSeconds";
    }



    public static int getIndex(int buildNum, List<Build> buildList){
        for(Build b: buildList) {
            if (b.getNumber() == buildNum){
                return buildList.indexOf(b);
            }
        }
        return -1;
    }

    public static long getDeliveryTime(final String jobName) {
        long deliveryTime = -1;
        try {
            JenkinsServer jenkins = new JenkinsServer(new URI("https://fem1s11-eiffel216.eiffel.gic.ericsson.se:8443/jenkins/"), //jenkins/job/eric-csm-p_PreCodeReview/
                    "efolhar", "Sp5t0303!!");

            deliveryTime = jenkins.getJob(jobName).getLastSuccessfulBuild().details()
                    .getDuration();
        } catch (Exception e) {

        }


        return deliveryTime;
    }
}
