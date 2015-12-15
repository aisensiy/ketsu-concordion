package specs.example;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.Before;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/* Run this class as a JUnit test. */

@RunWith(ConcordionRunner.class)
public class FunctionalTest {

    private Client client;
    private String cookie;
    private String solutionId;
    private String stackId;
    private String examProfileId;
    private String examProfileName = "Jersey+Mysql";
    private String examRequestId;
    private String projectId;
    private String projectSolutionId;

    @Before
    public void setUp() throws Exception {
        client = ClientBuilder.newClient();
    }

    public int createSolution() {
        stackOwnerLogin();

        Form form = new Form();
        form.param("solution[name]", "solution");
        form.param("solution[description]", "solution description");
        final Response response = client.target(getUrl("/solutions"))
                .request().header("Cookie", cookie).post(Entity.form(form));
        String[] splits = response.getHeaderString("Location").split("/");
        solutionId = splits[splits.length - 1];
        return response.getStatus();
    }

    public int createStack() {
        stackOwnerLogin();

        Form form = new Form();
        form.param("stack[name]", "stack");
        form.param("stack[backing_services][]", "mysql");
        form.param("stack[backing_services][]", "jersey");
        final Response response = client.target(getUrl("/solutions/" + solutionId + "/stacks"))
                .request().header("Cookie", cookie).post(Entity.form(form));
        String[] splits = response.getHeaderString("Location").split("/");
        stackId = splits[splits.length - 1];
        return response.getStatus();
    }

    public int createExamProfile() {
        stackOwnerLogin();

        Form form = new Form();
        form.param("exam_profile[name]", examProfileName);
        form.param("exam_profile[raml]", "http://xxx/raml");
        form.param("exam_profile[archetype]", "https://github.com/abc/exam");
        final Response response = client.target(getUrl(String.format("/solutions/%s/stacks/%s/exam_profiles", solutionId, stackId)))
                .request().header("Cookie", cookie).post(Entity.form(form));
        String[] splits = response.getHeaderString("Location").split("/");
        examProfileId = splits[splits.length - 1];
        return response.getStatus();
    }

    public int createProject() {
        stackOwnerLogin();

        Form form = new Form();
        form.param("project[name]", "project");
        form.param("project[description]", "project description");
        final Response response = client.target(getUrl("/projects"))
                .request().header("Cookie", cookie).post(Entity.form(form));
        String[] splits = response.getHeaderString("Location").split("/");
        projectId = splits[splits.length - 1];
        return response.getStatus();
    }

    public int addSolutionAndStack() {
        stackOwnerLogin();

        Form form = new Form();
        form.param("solution_id", solutionId);
        form.param("stack_id", stackId);
        final Response response = client.target(getUrl("/projects/" + projectId + "/solutions"))
                .request().header("Cookie", cookie).post(Entity.form(form));
        return response.getStatus();
    }

    public void getProjectSolution() {
        stackOwnerLogin();

        final Response response = client.target(getUrl("/projects/" + projectId)).request().header("Cookie", cookie).get();
        Map data = response.readEntity(Map.class);
        List solutions = (List) data.get("solutions");
        Map solution = (Map) solutions.get(0);
        projectSolutionId = (String) solution.get("id");
    }

    public int removeSolution() {
        stackOwnerLogin();

        final Response response = client.target(getUrl(String.format("/projects/%s/solutions/%s", projectId, projectSolutionId)))
                .request().header("Cookie", this.cookie).delete();
        return response.getStatus();
    }

    public int replaceStack() {
        stackOwnerLogin();
        Form form = new Form();
        form.param("stack_id", stackId);
        final Response response = client.target(getUrl(String.format("/projects/%s/solutions/%s/stacks", projectId, projectSolutionId)))
                .request().header("Cookie", this.cookie).post(Entity.form(form));
        return response.getStatus();
    }

    public int createExamRequest() {
        stackUserLogin();

        Form form = new Form();
        form.param("exam", String.format("/solutions/%s/stacks/%s/exam_profiles/%s", solutionId, stackId, examProfileId));
        final Response response = client.target(getUrl("/exam_requests")).request()
                .header("Cookie", cookie).post(Entity.form(form));
        String[] splits = response.getHeaderString("Location").split("/");
        examRequestId = splits[splits.length - 1];
        return response.getStatus();
    }

    public String getExamRequest() {
        stackUserLogin();

        final Response response = client.target(getUrl("/exam_requests/" + examRequestId)).request()
                .header("Cookie", cookie).get();
        Map map = response.readEntity(Map.class);
        return (String) map.get("name");
    }

    public int createExamSubmission() {
        stackUserLogin();
        final Form form = new Form();
        form.param("exam_submission[repository]", "http://test.com/");
        final Response response = client.target(getUrl("/exam_requests/" + examRequestId + "/submission")).request()
                .header("Cookie", cookie).post(Entity.form(form));
        return response.getStatus();
    }

    public void createResultForSubmission() {
        adminLogin();

        Form form = new Form();
        form.param("exam_result[isSuccess]", "true");
        form.param("exam_result[timeCost]", "123");
        client.target(getUrl("/exam_requests/" + examRequestId + "/submission/exam-result")).request()
                .header("Cookie", cookie).post(Entity.form(form));

    }

    public boolean getResultInExamRequest() {
        stackUserLogin();

        final Response response = client.target(getUrl("/exam_requests/" + examRequestId)).request().header("Cookie", cookie)
                .get();
        Map map = response.readEntity(Map.class);
        Map result = (Map) map.get("result");
        String cost = (String) result.get("time_cost");
        return cost.equals("123");
    }



    private void stackOwnerLogin() {
        Form form = new Form();
        form.param("name", "stack_owner");
        form.param("password", "admin123");
        final Response response = client.target(getUrl("/members/login")).request().post(Entity.form(form));
        cookie = response.getHeaderString("Set-Cookie");
    }

    private void adminLogin() {
        Form form = new Form();
        form.param("name", "admin");
        form.param("password", "admin123");
        final Response response = client.target(getUrl("/members/login")).request().post(Entity.form(form));
        cookie = response.getHeaderString("Set-Cookie");
    }

    private void stackUserLogin() {
        Form form = new Form();
        form.param("name", "stack_user");
        form.param("password", "admin123");
        final Response response = client.target(getUrl("/members/login")).request().post(Entity.form(form));
        cookie = response.getHeaderString("Set-Cookie");
    }

    private String getUrl(String path) {
        return "http://localhost:3000" + path;
    }
}
