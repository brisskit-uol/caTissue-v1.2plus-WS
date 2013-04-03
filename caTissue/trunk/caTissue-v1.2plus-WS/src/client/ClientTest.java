package client;

import java.net.URI;
import java.util.Locale;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class ClientTest {

	public String performCall()
	{
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource service = client.resource(getBaseURI());

        String xml = "<pdo:patient_set>" +
        		"<patient download_date=\"2012-05-03T17:03:06.300+01:00\" import_date=\"2012-05-03T17:03:06.300+01:00\" sourcesystem_cd=\"BRICCS\" update_date=\"2012-05-03T17:03:06.300+01:00\" upload_id=\"1\">" +
        		"<patient_id source=\"BRICCS\">BPt00000040</patient_id>" +
        		"<vital_status_cd column=\"vital_status_cd\" name=\"date interpretation code\">N</vital_status_cd>" +
        		"<birth_date column=\"birth_date\" name=\"birthdate\">1972-04-25T00:00:00.000+01:00</birth_date>" +
        		"<age_in_years_num column=\"age_in_years_num\" name=\"age\">40</age_in_years_num>" +
        		"<race_cd column=\"race_cd\" name=\"ethnicity\">Unknown</race_cd>" +
        		"<sex_cd column=\"sex_cd\" name=\"sex\">MALE</sex_cd>" +
        		"<study_name source=\"BRICCS\">CP_Prostate_Cancer</study_name>" +
        		"</patient> " +
        		"<patient download_date=\"2012-05-04T17:03:06.300+01:00\" import_date=\"2012-05-04T17:03:06.300+01:00\" sourcesystem_cd=\"BRICCS\" update_date=\"2012-05-04T17:03:06.300+01:00\" upload_id=\"1\">" +
        		"<patient_id source=\"BRICCS\">BPt00000041</patient_id>" +
        		"<vital_status_cd column=\"vital_status_cd\" name=\"date interpretation code\">N</vital_status_cd>" +
        		"<birth_date column=\"birth_date\" name=\"birthdate\">1973-04-25T00:00:00.000+01:00</birth_date>" +
        		"<age_in_years_num column=\"age_in_years_num\" name=\"age\">41</age_in_years_num>" +
        		"<race_cd column=\"race_cd\" name=\"ethnicity\">Unknown</race_cd>" +
        		"<sex_cd column=\"sex_cd\" name=\"sex\">MALE</sex_cd>" +
        		"<study_name source=\"BRICCS\">CellSpecimen</study_name>" +
        		"</patient>" +
        		" </pdo:patient_set>";

        ClientResponse response = service.path("service").path("pdo").type(MediaType.APPLICATION_XML).post(ClientResponse.class, xml);

 		System.out.println("performCall Output from Server .... \n");
 		String output = response.getEntity(String.class);
 		System.out.println(output);

 		return output;
	}


    public static void main(String[] args) {
    	ClientTest c = new ClientTest();
    	/*String s = */c.performCall();
    }

    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost:8080/civi.catissue.ws/rest").build();
    }

}