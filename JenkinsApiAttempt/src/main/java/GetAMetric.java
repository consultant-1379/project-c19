import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.model.*;
import org.xml.sax.InputSource;

import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class GetAMetric {
    public static void main(String[] args){
        System.out.println("Trying to get a Metric using Jenkins API");
        try {

            JenkinsServer jenkins = new JenkinsServer(new URI("https://fem1s11-eiffel216.eiffel.gic.ericsson.se:8443/jenkins/"), //jenkins/job/eric-csm-p_PreCodeReview/
                    "efolhar", "Sp5t0303!!");
            System.out.println("#### Start ####");


           /* System.out.println(jenkins.isRunning());
            Map<String, Job> jobs = jenkins.getJobs();
            JobWithDetails job =  (jobs.get("Admin_ms-ci-visualization").details());
            System.out.println(job);
            System.out.println("Name: " + job.getName());
            System.out.println("Has Description: "+job.hasLastBuildRun());
            */

            JobWithDetails jbd =  jenkins.getJob("Admin_ms-ci-visualization");

            System.out.println("Job Name: " + jbd.getDisplayName());
            System.out.println("Job Details: " + jbd.details());
            List<Build> buildList = jbd.getAllBuilds();
            System.out.println("Number of builds for job: " + buildList.size());
            for(Build b: buildList){
                System.out.println(b.getUrl());
                BuildWithDetails details = b.details();
                System.out.println(details.getDisplayName());
                //TestResult build_result = details.getTestResult();
                System.out.println(details.getDuration());
                break;
            }
            //Build lastBuild =  (job.getLastBuild());
            //System.out.println(lastBuild.details());
            //lastBuild.
            //System.out.println(lastBuild.details());
            System.out.println("#### End ####");
            }
        catch(Exception e){
            System.out.println("really screaming");
            System.out.println(e);
        }





    }
}
