package ws;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import static uk.ac.le.brisskit.ParticipantRegistrationPlus.*;


/**
 *
 * <brisskit:licence>
 * Copyright University of Leicester, 2012
 * This software is made available under the terms & conditions of brisskit
 * @author Somaraja Surampudi
 * This resource represents the WebServices to send pdo files to i2b2 and send an acknowledgement to civi on receipt of patient
 * data from civi
 * </brisskit:licence>
 *
 */

@Path("/service")

public class WebService {
	final static Logger logger = LogManager.getLogger(WebService.class);

	@GET 
	@Path("getCPs")
	@Produces("text/plain")  
	public String getCPs() {
		
		Interactions interact = new Interactions();
		Interactions.initCaTissueService();
		String rt = interact.getCP();
		
		return rt;
	}
	
	@POST
	@Path("pdo")
	@Consumes({MediaType.APPLICATION_XML})
	@Produces({MediaType.TEXT_HTML, MediaType.TEXT_PLAIN})
	public String postOnlyXML2(@FormParam("incomingXML") String incomingXML,@FormParam("activity_id") String activity_id) {
		String str = incomingXML, details="", errMsg="";
		boolean isError = false;
		try
		{
			Properties props = new Properties();

			 //load from classpath
			 InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("log4j.properties");
			 props.load(inputStream);

		PropertyConfigurator.configure(props);
		logger.info("incomingXML :" + incomingXML);
		logger.info("activity_id :" + activity_id);

		logger.info("************* A PDO HAS ARRIVED *******************");
        logger.info(" ");
        logger.info("CONTENT OF XML " + incomingXML);
        logger.info(" ");
        logger.info("activity_id : " + activity_id);

		//Current Date time in Local time zone
		SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String date = localDateFormat.format( new Date());

		if (str.contains("xml="))
		{
			str = str.substring(str.indexOf("xml=")+4);
		}


			String param2AfterDecoding = URLDecoder.decode(str, "UTF-8");
			logger.info("param2 after decoding:" + param2AfterDecoding);
			String fileName="/var/local/brisskit/catissue/civi.catissue.ws/files/civi_catissue_"+date+".xml";
			//String fileName="c:\\var\\local\\brisskit/catissue\\civi.catissue.ws\\files\\civi_catissue_"+date+".xml";
			//String fileName="/home/localadmin1/workspace/civi.catissue.ws/files/civi_catissue_"+date+".xml";

			logger.info(" ");
	        logger.info("FILE NAME : " + "/var/local/brisskit/catissue/civi.catissue.ws/files/civi_catissue_"+date+".xml");
	        //logger.info("FILE NAME : " + "c:\\var\\local\\brisskit/catissue\\civi.catissue.ws\\files\\civi_catissue_"+date+".xml");

			FileWriter fstream = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(param2AfterDecoding);
			//Close the output stream
			out.close();

			logger.info(" ");
	        logger.info("caTissue IMPORT COMPLETE");

	        initCaTissueService();
			
	        //regParticipants is to register patient in caTissue database without CP
	        //String status = regParticipants(fileName);
	        
	        String status = regParticipantsWithCP(fileName);
	        //regParticipants is to register patient in caTissue database and this class is

			logger.info(status);
			details=status;
			String status_var="Failed",option_group_id="",activity_status_id="";

			ClientConfig config = new DefaultClientConfig();
			Client client = Client.create(config);
			
			/*client.addFilter(new HTTPBasicAuthFilter(
	                "soma",
	                "Leicester2"));*/

			if(status.equalsIgnoreCase("success"))
			{
				status_var = "Completed";
				details = "Participant Registration Completed Successfully in caTissue";
			}

			logger.info(" ");
			logger.info(details);

			WebResource optionGroupService = client.resource(getOptionGroupBaseURI());
			option_group_id = jsonGetColumnValue(optionGroupService.get(String.class), "option_group");

			if(status.equalsIgnoreCase("success"))
				status_var = "Completed";

			WebResource optionValueService = client.resource(getOptionValueBaseURI(option_group_id,status_var));
			activity_status_id = jsonGetColumnValue(optionValueService.get(String.class), "option_value");

			logger.info("activity_status_id is............"+activity_status_id);

			WebResource detailsService = client.resource(getDetailsURI(activity_id));
			details = (((jsonGetColumnValue(detailsService.get(String.class), "details")+" - "+new Date()+" - "+details).replace(" ", "%20")).replace("\n", "%5Cn")).replace("\\n", "%5Cn");

			WebResource service = client.resource(getBaseURI(activity_id, activity_status_id, details));
			String response = service.get(String.class);

			logger.info("response............"+response);

			logger.info(" ");
	        logger.info("response............"+response);

            logger.info(" ");
	        logger.info("CIVI CALLBACK COMPLETE");

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isError = true;
			errMsg = e.getMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isError = true;
			errMsg = e.getMessage();
		} catch (UniformInterfaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isError = true;
			errMsg = e.getMessage();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isError = true;
			errMsg = e.getMessage();
		}

		if(isError) {
			logger.info(errMsg);
			logger.info(" ");
			logger.info("Civi Callback Failed");
		}

		return "Uploaded Successfuly";
	}

	private static URI getBaseURI(String activity_id, String status_id, String details) {
		logger.info("http://civicrm/civicrm/civicrm/ajax/rest?json=1&debug=1&version=3&entity=Activity&action=update&id="+activity_id+"&details="+details+"&status_id="+status_id);
		//return UriBuilder.fromUri("http://bru2.brisskit.le.ac.uk/civicrm/sites/all/modules/brisskit/lib/update_activity.php?activity_id="+activity_id+"&status="+status_id+"&log=Single").build();
		return UriBuilder.fromUri("http://civicrm/civicrm/civicrm/ajax/rest?json=1&debug=1&version=3&entity=Activity&action=update&id="+activity_id+"&details="+details+"&status_id="+status_id).build();
	}

	private static URI getDetailsURI(String activity_id) {
		logger.info("http://civicrm/civicrm/civicrm/ajax/rest?json=1&debug=1&version=3&entity=Activity&action=get&id="+activity_id);
		//return UriBuilder.fromUri("http://bru2.brisskit.le.ac.uk/civicrm/sites/all/modules/brisskit/lib/update_activity.php?activity_id="+activity_id+"&status="+status_id+"&log=Single").build();
		return UriBuilder.fromUri("http://civicrm/civicrm/civicrm/ajax/rest?json=1&debug=1&version=3&entity=Activity&action=get&id="+activity_id).build();
	}

	private static URI getOptionGroupBaseURI() {
		//return UriBuilder.fromUri("http://bru2.brisskit.le.ac.uk/civicrm/civicrm/ajax/rest?json=1&debug=1&version=3&entity=OptionGroup&action=get&name=activity_status").build();
		return UriBuilder.fromUri("http://civicrm/civicrm/civicrm/ajax/rest?json=1&debug=1&version=3&entity=OptionGroup&action=get&name=activity_status").build();
	}

	private static URI getOptionValueBaseURI(String option_group_id, String status) {
		//return UriBuilder.fromUri("http://bru2.brisskit.le.ac.uk/civicrm/civicrm/ajax/rest?json=1&debug=1&version=3&entity=OptionValue&action=get&option_group_id="+option_group_id+"&name="+status).build();
		return UriBuilder.fromUri("http://civicrm/civicrm/civicrm/ajax/rest?json=1&debug=1&version=3&entity=OptionValue&action=get&option_group_id="+option_group_id+"&name="+status).build();
	}

	/**
	 *
	 * @param service
	 * @param option
	 * @return
	 * @throws JSONException
	 *
	 * jsonGetColumnValue is to get a specific column value from civi Activity
	 */
	private static String jsonGetColumnValue(String service, String option) throws JSONException
	{
		String columnValue="";

		JSONObject obj1 = new JSONObject(service);
		JSONObject obj2 = new JSONObject(obj1.get("values").toString());
		Iterator itr = obj2.keys();

		if(itr.hasNext())
		{
			JSONObject obj3 = new JSONObject(obj2.get(itr.next().toString()).toString());

			if(option.equalsIgnoreCase("option_group"))
				columnValue = obj3.get("id").toString();
			else if(option.equalsIgnoreCase("option_value"))
				columnValue = obj3.get("value").toString();
			else
				columnValue = obj3.get("details").toString();
		}

		return columnValue;
	}


}