package uk.ac.le.brisskit;

import edu.wustl.catissuecore.domain.CollectionProtocol;
import edu.wustl.catissuecore.domain.CollectionProtocolRegistration;
import edu.wustl.catissuecore.domain.Participant;
import gov.nih.nci.common.util.HQLCriteria;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationServiceProvider;
import gov.nih.nci.system.comm.client.ClientSession;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
*
* <brisskit:licence>
* Copyright University of Leicester, 2012
* This software is made available under the terms & conditions of brisskit
* @author Shajid Issa
* Registation of Participant into caTissue 1.2 Plus v20
* </brisskit:licence>
*
*/

public class ParticipantRegistrationPlus {

	
	static ApplicationService appService = null;
	final static Logger logger = LogManager.getLogger(ParticipantRegistrationPlus.class);
	private static String jbossServerUrl = "http://localhost:8080/catissuecore";	
	private static String userName = "";
	private static String password = "";
	
	/**
	 * Initialise caTissue Service
	 */
	public static void initCaTissueService()
	{	
		logger.info("ParticipantRegistrationPlus:initCaTissueService");
		
		Properties prop = new Properties();
		
		try {
			prop.load(new FileInputStream("/var/local/brisskit/catissue/webservice.properties"));
			String u = prop.getProperty("username");
			String p = prop.getProperty("password");
			
			userName = u;
			password = p;
			
		} catch (FileNotFoundException e) {			
			logger.info("FileNotFoundException in ParticipantRegistrationPlus:initCaTissueService");
			e.printStackTrace();
		} catch (IOException e) {
			logger.info("IOException in ParticipantRegistrationPlus:initCaTissueService");
			e.printStackTrace();
		}	
		appService = ApplicationServiceProvider.getRemoteInstance(jbossServerUrl + "/http/remoteService");

	}
	
	/**
	 * Register a participant without collection protocol
	 */
	public static String regParticipants(String fileName) {
		
		logger.info("ParticipantRegistrationPlus:regParticipants " + fileName);	
		ClientSession cs = ClientSession.getInstance();
				
		try {
			cs.startSession(userName, password);
		} catch (ApplicationException e1) {
			logger.info("ApplicationException in ParticipantRegistrationPlus:regParticipants");
			e1.printStackTrace();
		}
		
		boolean isError=false;
		String errorMsg="";
		
		try
		{
			File fXmlFile = new File(fileName);
			SAXBuilder builder = new SAXBuilder();
			Document document = builder.build(new ByteArrayInputStream(getFileAsString(fXmlFile).getBytes()));
			Element root = document.getRootElement();

			List rows = root.getChild("patient_set").getChildren("patient");
			for (int i = 0; i < rows.size(); i++) {
				Element row = (Element) rows.get(i);
				Element patientElement = row.getChild("patient_id");
				Element cpElement = row.getChild("study_name");
				HashMap<String, String> hashMap = new HashMap<String, String>();
				String gender = "Unknown", def_vital_status_cd = "Unknown", sex_cd="";

				List params = row.getChildren("param");
				for (int j = 0; j < params.size(); j++) {
					Element column = (Element) params.get(j);
					String value = column.getText();
					hashMap.put(j+"", value);
				}
				Participant p = new Participant();
				p.setLastName(patientElement.getText());
				p.setFirstName("");
				p.setMiddleName(cpElement.getText());
				p.setActivityStatus("Active");
				p.setBirthDate(dateFormat(hashMap.get("1")));
				sex_cd=hashMap.get("4");

				if(sex_cd.equalsIgnoreCase("MALE"))
					gender="Male Gender";
				else if(sex_cd.equalsIgnoreCase("FEMALE"))
					gender="Female Gender";

				p.setGender(gender);
				p.setVitalStatus(def_vital_status_cd);
				
				try{
					p = (Participant) appService.createObject(p);
				}
				catch(Exception e){
			       	e.printStackTrace();		            
				}				
				logger.info("ParticipantRegistrationPlus:regParticipants - Participant created successfully");					
			}
		} catch (JDOMException e) {
			e.printStackTrace();
			isError = true;
			errorMsg=e.getMessage();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			isError = true;
			errorMsg=e.getMessage();
		} catch (IOException e) {
			e.printStackTrace();
			isError = true;
			errorMsg=e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			isError = true;
			errorMsg=e.getMessage();
		}
		
		if(isError)
			return "Participant registration failed because of an error ["+errorMsg+"]";
		else
			return "success";
	}
	
	
	/**
	 * Register a participant with collection protocol
	 */
	public static String regParticipantsWithCP(String fileName) {
		
		logger.info("ParticipantRegistrationPlus:regParticipantsWithCP " + fileName);	
		ClientSession cs = ClientSession.getInstance();
		
		try {
			cs.startSession(userName, password);
		} catch (ApplicationException e1) {
			logger.info("ApplicationException in ParticipantRegistrationPlus:regParticipantsWithCP");
			e1.printStackTrace();
		}
		
		boolean isError=false;
		String errorMsg="";
		
		try
		{			
			File fXmlFile = new File(fileName);
			SAXBuilder builder = new SAXBuilder();
			Document document = builder.build(new ByteArrayInputStream(getFileAsString(fXmlFile).getBytes()));
			Element root = document.getRootElement();
		
			List rows = root.getChild("patient_set").getChildren("patient");
			for (int i = 0; i < rows.size(); i++) {
				Element row = (Element) rows.get(i);
				Element patientElement = row.getChild("patient_id");
				Element cpElement = row.getChild("study_name");
				HashMap<String, String> hashMap = new HashMap<String, String>();
				String gender = "Unknown", def_vital_status_cd = "Unknown", sex_cd="";

				List params = row.getChildren("param");
				for (int j = 0; j < params.size(); j++) {
					Element column = (Element) params.get(j);
					String value = column.getText();
					hashMap.put(j+"", value);
				}
				Participant p = new Participant();
				p.setLastName(patientElement.getText());
				p.setFirstName("");
				//p.setMiddleName(cpElement.getText());
				p.setActivityStatus("Active");
				p.setBirthDate(dateFormat(hashMap.get("1")));
				sex_cd=hashMap.get("4");

				if(sex_cd.equalsIgnoreCase("MALE"))
					gender="Male Gender";
				else if(sex_cd.equalsIgnoreCase("FEMALE"))
					gender="Female Gender";

				p.setGender(gender);

				p.setVitalStatus(def_vital_status_cd);
			
				try{
					p = (Participant) appService.createObject(p);									
					String getCPQuery = "select cp from edu.wustl.catissuecore.domain.CollectionProtocol cp where cp.shortTitle='" + cpElement.getText() + "'";
					HQLCriteria hql = new HQLCriteria(getCPQuery);					
					List<CollectionProtocol> resultList =appService.query(hql,CollectionProtocol.class.getName());
									
					if (resultList.size() == 1)
					{
						CollectionProtocol cp = resultList.get(0);						
						CollectionProtocolRegistration collectionProtocolRegistration = new CollectionProtocolRegistration();					
						collectionProtocolRegistration.setActivityStatus("Active");
						collectionProtocolRegistration.setCollectionProtocol(cp);
						collectionProtocolRegistration.setParticipant(p);
						collectionProtocolRegistration = (CollectionProtocolRegistration) appService.createObject(collectionProtocolRegistration);
					}									
				}
				catch(Exception e){
			       	e.printStackTrace();
		            
				}
				
				System.out.println("Participant created successfully");
			}
		} catch (JDOMException e) {
			e.printStackTrace();
			isError = true;
			errorMsg=e.getMessage();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			isError = true;
			errorMsg=e.getMessage();
		} catch (IOException e) {
			e.printStackTrace();
			isError = true;
			errorMsg=e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			isError = true;
			errorMsg=e.getMessage();
		}
		if(isError)
			return "Participant registration failed because of an error ["+errorMsg+"]";
		else
			return "success";
	}
	
		/**
		 * Register a participant. Simple call without any input
		 */
		public static String regParticipantsTest() {
			
			logger.info("ParticipantRegistrationPlus:regParticipantsTest");	
			
			ClientSession cs = ClientSession.getInstance();
						
			try {
				cs.startSession(userName, password);
			} catch (ApplicationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
						
			boolean isError=false;
			String errorMsg="";
			Properties prop = new Properties();
			try
			{
					Participant p = new Participant();
					p.setLastName("patientElement");
					p.setFirstName("");
					p.setMiddleName("cpElement");
					p.setActivityStatus("Active");
					p.setBirthDate(dateFormat("0000-00-00"));
					String gender="Male Gender";
					p.setGender(gender);
					p.setVitalStatus("Unknown");
						
					try{
						p = (Participant) appService.createObject(p);
												
					}
					catch(Exception e){
						logger.info("while creating participant");
				       	e.printStackTrace();
			            
					}					
					logger.info("Participant created successfully");
			} catch (Exception e) {
				e.printStackTrace();
				isError = true;
				errorMsg=e.getMessage();
			}
			if(isError)
				return "Participant registration failed because of an error ["+errorMsg+"]";
			else
				return "success";
		}

		/**
		 *
		 * @param dateString
		 * @return
		 * @throws ParseException
		 * to convert into i2b2 acceptable date format
		 */
		private static Date dateFormat(String dateString) throws ParseException
		{
			if(dateString.contains("0000-00-00"))
				dateString="1970-01-01";

			logger.info("first..."+dateString);

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date convertedDate = dateFormat.parse(dateString);

			logger.info("second..."+convertedDate);

			return convertedDate;
		}
		
		/**
		 * getFileAsString
		 */
		private static String getFileAsString(File file) throws FileNotFoundException,IOException {
			FileInputStream fis = null;
			BufferedReader br = null;
			StringBuffer sb = new StringBuffer();
			String line="";

			fis = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(fis));

			while ((line = br.readLine()) != null) {
				sb.append( line.replace("pdo:", "") +"\n");
			}
			fis.close();
			br.close();

			return sb.toString();
		}
}
