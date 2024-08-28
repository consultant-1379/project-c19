import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildResult;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.JobWithDetails;

import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeBetweenBuilds {


    public static void main(String [] args) {

        System.out.println(timeBetweenFailedBuildandNextSuccess());

//44 fail 45 suc
//26 fail 32 suc

    }

    public static String timeBetweenFailedBuildandNextSuccess(){
        long elapsedTimeSeconds = -1;
        int nextSuccessfulBuildNum = -1;
        int failBuildNum = -1;
        List<Build> buildList = null;
        try{
            JenkinsServer jenkins = new JenkinsServer(new URI("https://fem1s11-eiffel216.eiffel.gic.ericsson.se:8443/jenkins/"), //jenkins/job/eric-csm-p_PreCodeReview/
                    "efolhar", "Sp5t0303!!");
            JobWithDetails jbd = jenkins.getJob("eric-oss-testrepo1_Publish");//"idun-sdk_PCR");//"Admin_ms-ci-visualization");
            failBuildNum = jbd.getLastFailedBuild().getNumber();
            buildList = jbd.getAllBuilds();
            int failIndex = getIndex(failBuildNum, buildList);
            if(failIndex > -1){System.out.println("Index of fail is " + failIndex);}
            else{System.out.println("not a fail");
                return "build number " + failBuildNum + "was not a failed build";
            }
            long failBuildTimeStamp = buildList.get(failIndex).details().getTimestamp();
            boolean notFound = true;
            while(notFound){
                failIndex = failIndex - 1;
                if(buildList.get(failIndex).details().getResult().toString() == "SUCCESS"){
                    nextSuccessfulBuildNum = failIndex;
                    notFound = false;
                }
            }
            long successBuildTimeStamp = buildList.get(failIndex).details().getTimestamp();
            long elapsedTime = successBuildTimeStamp-failBuildTimeStamp;
            elapsedTimeSeconds =  TimeUnit.NANOSECONDS.toMillis(elapsedTime);//; //TimeUnit.MILLISECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);


        } catch(Exception e) {

        }
        return "Time between failed build " + failBuildNum + " and next successful build " + buildList.get(nextSuccessfulBuildNum).getNumber()
                + " was " + elapsedTimeSeconds + " nanoseconds";

    }

    public static int getIndex(int buildNum, List<Build> buildList){
        for(Build b: buildList) {
            if (b.getNumber() == buildNum){
                return buildList.indexOf(b);
                    }
                }
            return -1;
            }


}
