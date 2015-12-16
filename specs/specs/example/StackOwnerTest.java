package specs.example;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.Before;
import org.junit.runner.RunWith;
import specs.model.Capability;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import java.util.*;

import static java.util.stream.Collectors.toList;

@RunWith(ConcordionRunner.class)
public class StackOwnerTest {
    private Client client;
    private String cookie;

    @Before
    public void setUp() throws Exception {
        client = ClientBuilder.newClient();
    }

    private String getUrl(String path) {
        return "http://localhost:8088" + path;
    }

    public String uniqueProjectId() {
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        return (instance.getTimeInMillis() + "").substring(4, 12);
    }

    public String userLogin(String username) {
        Form form = new Form();
        form.param("user_name", username);
        final Response response = client.target(getUrl("/authentication")).request().post(Entity.form(form));
        cookie = response.getHeaderString("Set-Cookie");
        System.out.println(cookie);
        return response.getStatus() + "";
    }

    public void setLoginUser(String username) {
        userLogin(username);
    }

    public String importNewProject(String id, String name, String account) {
        Form form = new Form();
        form.param("name", name);
        form.param("id", id);
        form.param("account", account);
        final Response response = client
                .target(getUrl("/projects"))
                .request()
                .header("Cookie", cookie)
                .post(Entity.form(form));

        return response.getStatus() + "";
    }

    public String getProjectName(String id) {
        final Response response = client
                .target(getUrl("/projects/" + id))
                .request()
                .header("Cookie", cookie)
                .get();
        Map map = response.readEntity(Map.class);
        System.out.println(response.getHeaderString("Set-Cookie"));

        return map.get("name") + "";
    }

    public String getProjectAccount(String id) {
        final Response response = client
                .target(getUrl("/projects/" + id))
                .request()
                .header("Cookie", cookie)
                .get();
        Map map = response.readEntity(Map.class);
        System.out.println(response.getHeaderString("Set-Cookie"));

        return map.get("account") + "";
    }

    public String defineANewCapability(String projectId, String solutionId, String stackId){
        Form form = new Form();
        form.param("solution_id", solutionId);
        form.param("stack_id", stackId);
        final Response response = client
                .target(getUrl("/projects/"+projectId+"/capabilities"))
                .request()
                .header("Cookie", cookie)
                .post(Entity.form(form));

        return response.getStatus() + "";
    }

    public Iterable<Capability> getFirstCapability(String id){
        final Response response = client
                .target(getUrl("/projects/" + id +"/capabilities"))
                .request()
                .header("Cookie", cookie)
                .get();
        List<Map> capabilities = response.readEntity(ArrayList.class);
        return capabilities.stream().map(capability -> {
            Map stack = (Map) capability.get("stack");
            Map solution = (Map) capability.get("solution");
            return new Capability(
                    stack.get("id")+"",
                    stack.get("name")+"",
                    solution.get("id")+"",
                    solution.get("name")+""
            );
        }).collect(toList());
    }
    public String defineANewSolution(String solutionName){
        Form form = new Form();
        form.param("name", solutionName);
        final Response response = client
                .target(getUrl("/solutions"))
                .request()
                .header("Cookie", cookie)
                .post(Entity.form(form));
        return response.getStatus()+"";
    }
    public String defineANewStack(String stackName){
        Form form = new Form();
        form.param("name", stackName);
        form.param("services", "1");
        form.param("services", "2");
        final Response response = client
                .target(getUrl("/stacks"))
                .request()
                .header("Cookie", cookie)
                .post(Entity.form(form));
        return response.getStatus()+"";
    }
}
