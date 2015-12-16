package specs.example;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.Before;
import org.junit.runner.RunWith;
import specs.model.Qualification;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import java.util.*;

import static java.util.stream.Collectors.toList;

@RunWith(ConcordionRunner.class)
public class StackUserTest {
    private Client client;
    private String cookie;

    @Before
    public void setUp() throws Exception {
        client = ClientBuilder.newClient();
    }

    public String userLogin(String username) {
        Form form = new Form();
        form.param("user_name", username);
        final Response response = client.target(getUrl("/authentication")).request().post(Entity.form(form));
        cookie = response.getHeaderString("Set-Cookie");
        return response.getStatus() + "";
    }

    public String userLogout() {
        final Response response = client.target(getUrl("/authentication")).request().delete();
        return response.getStatus() + "";
    }

    public void setLoginUser(String username) {
        userLogin(username);
    }

    public String getCurrentUser() {
        final Response response = client.target(getUrl("/users/current"))
                .request()
                .header("Cookie", cookie)
                .get();
        return response.readEntity(Map.class).get("role") + "";
    }

    private String getUrl(String path) {
        return "http://localhost:8088" + path;
    }

    public String createUser(String userId, String username) {
        Form form = new Form();
        form.param("name", username);
        form.param("id", userId);
        final Response response = client.target(getUrl("/users"))
                .request()
                .header("Cookie", cookie)
                .post(Entity.form(form));
        return response.getStatus() + "";
    }

    public String uniqueUserId() {
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        return instance.getTimeInMillis()+"";
    }

    public String getProjects() {
        final Response response = client.target(getUrl("/users/22222/projects"))
                .request()
                .header("Cookie", cookie)
                .get();
        return response.getStatus() + "";
    }
    public String getProjectsSize() {
        final Response response = client.target(getUrl("/users/22222/projects"))
                .request()
                .header("Cookie", cookie)
                .get();
        List map = response.readEntity(ArrayList.class);
        return map.size() + "";
    }
    public Iterable<String> getSearchResultsFor(){
        final Response response = client.target(getUrl("/users/22222/projects"))
                .request()
                .header("Cookie", cookie)
                .get();
        List<Map> projects = response.readEntity(ArrayList.class);
        return projects.stream().map(project ->
                        project.get("name").toString()
        ).collect(toList());
    }
    public String getCapabilityNumber(){
        final Response response = client.target(getUrl("/users/22222/qualifications"))
                .request()
                .header("Cookie", cookie)
                .get();
        List<Map> projects = response.readEntity(ArrayList.class);
        return projects.size()+"";
    }
    public Iterable<Qualification> getQualifications() {
        final Response response = client.target(getUrl("/users/22222/qualifications"))
                .request()
                .header("Cookie", cookie)
                .get();
        List<Map> projects = response.readEntity(ArrayList.class);
        return projects.stream().map(project ->
                        new Qualification(project.get("solution_name") + "", project.get("stack_name")+"")
        ).collect(toList());
    }
}
