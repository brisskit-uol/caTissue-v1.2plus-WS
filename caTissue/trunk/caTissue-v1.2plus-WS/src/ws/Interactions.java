package ws;

import edu.wustl.catissuecore.api.test.TestCaseUtility;
import edu.wustl.catissuecore.api.test.UniqueKeyGeneratorUtil;
import edu.wustl.catissuecore.domain.CollectionProtocol;
import edu.wustl.catissuecore.domain.CollectionProtocolRegistration;
import edu.wustl.catissuecore.domain.ConsentTier;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.domain.User;
import edu.wustl.common.util.Utility;
import gov.nih.nci.common.util.HQLCriteria;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationServiceProvider;
import gov.nih.nci.system.comm.client.ClientSession;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
*
* <brisskit:licence>
* Copyright University of Leicester, 2012
* Interactions with caTissue
* @author Shajid Issa
* Registation of Participant into caTissue 1.2 Plus v20
* </brisskit:licence>
*
*/

public class Interactions {

	final static Logger logger = LogManager.getLogger(Interactions.class);	
	static ApplicationService appService = null;
	private static String jbossServerUrl = "http://localhost:8080/catissuecore";
	private static String userName = "";
	private static String password = "";
	
	Interactions() {	
	}
	
	/**
	 * Initialise caTissue Service
	 */
	public static void initCaTissueService()
	{		
		logger.info("Interactions:initCaTissueService");
		
		Properties prop = new Properties();
		
		try {
			prop.load(new FileInputStream("/var/local/brisskit/catissue/webservice.properties"));
			String u = prop.getProperty("username");
			String p = prop.getProperty("password");
			
			userName = u;
			password = p;
			
		} catch (FileNotFoundException e) {			
			logger.info("FileNotFoundException in Interactions:initCaTissueService");
			e.printStackTrace();
		} catch (IOException e) {
			logger.info("IOException in Interactions:initCaTissueService");
			e.printStackTrace();
		}	
		appService = ApplicationServiceProvider.getRemoteInstance(jbossServerUrl + "/http/remoteService");
	}
	
	/**
	 * get Collection Protocol as json
	 */
	public String getCP()
	{
		logger.info("Interactions:getCP");
		
		ClientSession cs = ClientSession.getInstance();
		
		try {
			cs.startSession(userName, password);
		} catch (ApplicationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String ret="";
		String getCPQuery = "select cp  from edu.wustl.catissuecore.domain.CollectionProtocol cp";
		HQLCriteria hql = new HQLCriteria(getCPQuery);
		
		List <String>protocols = new ArrayList<String>();
		
		try {
			List<CollectionProtocol> resultList =appService.query(hql,CollectionProtocol.class.getName());
				
			for (CollectionProtocol temp : resultList) {
													
				protocols.add("{\"title\":\""+ temp.getTitle() + "\", \"shortTitle\":\""+ temp.getShortTitle() + "\", \"objectId\":\""+ temp.getObjectId() + "\", " +
				  "\"activityStatus\":\""+ temp.getActivityStatus() + "\", \"aliquotLabelFormat\":\""+ temp.getAliquotLabelFormat() + "\", \"derivativeLabelFormat\":\""+ temp.getDerivativeLabelFormat() + "\", " +	
				  "\"descriptionURL\":\""+ temp.getDescriptionURL() + "\", \"irbIdentifier\":\""+ temp.getIrbIdentifier() + "\", \"messageLabel\":\""+ temp.getMessageLabel() + "\", " +	
				  "\"specimenLabelFormat\":\""+ temp.getSpecimenLabelFormat() + "\", \"type\":\""+ temp.getType() + "\", \"unsignedConsentDocumentURL\":\""+ temp.getUnsignedConsentDocumentURL() + "\", " +	
				  "\"enrollment\":\""+ temp.getEnrollment() + "\", \"Id\":\""+ temp.getId() + "\", \"sequenceNumber\":\""+ temp.getSequenceNumber() + "\", " +	
				  "\"studyCalendarEventPoint\":\""+ temp.getStudyCalendarEventPoint() + "\", \"consentsWaived\":\""+ temp.getConsentsWaived() + "\", \"endDate\":\""+ temp.getEndDate() + "\", " +
				  "\"isEMPIEnabled\":\""+ temp.getIsEMPIEnabled() + "\", \"principalInvestigator\":\""+ temp.getPrincipalInvestigator() + "\", \"startDate\":\""+ temp.getStartDate() + "\" }");
												
			}
			
			ret =  "{ \"protocols\": [ ";
			int count = 0;
			for (String temp : protocols) {
				count++;
				ret += temp;
				
				if (protocols.size() != 1 && count != protocols.size())
				{
					ret += ", ";
				}
			}
			
			ret += "] }";
			
			
		} catch (ApplicationException e) {
			logger.info("FileNotFoundException in Interactions:getCP");
			e.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 * Everything below is a test that creates a participant, creates a new collection protocol
	 * Maybe required for further integrations, if civi ever want to create collection protocols
	 */
	public void doIt() {
			ClientSession cs = ClientSession.getInstance();
			logger.info("doIt x1");
			
			try {
				cs.startSession(userName, password);
			} catch (ApplicationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			System.out.println("1 *****************");
			
			Participant p = new Participant();
			p.setLastName("Saj");
			p.setFirstName("");
			p.setMiddleName("Issa");
			p.setActivityStatus("Active");
			p.setVitalStatus("Alive");
			try {
				p.setBirthDate(dateFormat("1912-10-15T00:00:00.000 01:00"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			p.setGender("Male Gender");

			//p.setVitalStatus("N");
			
			System.out.println("2 *****************");
			
			CollectionProtocol collectionProtocol = initCollectionProtocol();
			
			System.out.println("3 *****************");
			
			try {
				System.out.println("4 *****************");
				p = (Participant) appService.createObject(p);
				System.out.println("5 *****************");
				collectionProtocol = (CollectionProtocol) appService.createObject(collectionProtocol);
				System.out.println("6 *****************");
			} catch (ApplicationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			/*CollectionProtocol collectionProtocol=new CollectionProtocol();

			collectionProtocol.setTitle("qwewqer");
			collectionProtocol.setShortTitle("sesdrwe");*/

			/*User user=new User();
			user.setId(1L);
			List<?> resultList1 = null;
			try {
				resultList1 = appService.search(User.class, user);
				user=(User)resultList1.get(0);
				user.setRoleId("1");
				user.setPageOf(Constants.PAGE_OF_USER_ADMIN);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			*/
			
			System.out.println("7 *****************");
			
			CollectionProtocolRegistration collectionProtocolRegistration = new CollectionProtocolRegistration();		
			
			collectionProtocolRegistration.setActivityStatus("Active");
			collectionProtocolRegistration.setCollectionProtocol(collectionProtocol);
			collectionProtocolRegistration.setParticipant(p);
			
			System.out.println("8 *****************");
			
			try{
				System.out.println("9 *****************");
				collectionProtocolRegistration = (CollectionProtocolRegistration) appService.createObject(collectionProtocolRegistration);
				System.out.println("10 *****************");
			}
			catch(Exception e){
				System.out.println("11 *****************");
				System.out.println(e.getMessage() + e);
	           	//e.printStackTrace();
	           	//assertFalse("Failed to register participant", true);
			}	
						
	}
	
	public static CollectionProtocol initCollectionProtocol()
	{
	    CollectionProtocol collectionProtocol = new CollectionProtocol();

	    Collection consentTierColl = new LinkedHashSet();
		ConsentTier c1 = new ConsentTier();
		c1.setStatement("Consent for aids research");
		consentTierColl.add(c1);
		ConsentTier c2 = new ConsentTier();
		c2.setStatement("Consent for cancer research");
		consentTierColl.add(c2);
		ConsentTier c3 = new ConsentTier();
		c3.setStatement("Consent for Tb research");
		consentTierColl.add(c3);

		collectionProtocol.setConsentTierCollection(consentTierColl);
		collectionProtocol.setAliquotInSameContainer(new Boolean(true));
		collectionProtocol.setDescriptionURL("");
		collectionProtocol.setActivityStatus("Active");

		collectionProtocol.setEndDate(null);
		collectionProtocol.setEnrollment(null);
		collectionProtocol.setIrbIdentifier("77777");
		collectionProtocol.setTitle("coll proto"+UniqueKeyGeneratorUtil.getUniqueKey());
		collectionProtocol.setShortTitle("pc"+UniqueKeyGeneratorUtil.getUniqueKey());
		collectionProtocol.setTitle("ColProt"+UniqueKeyGeneratorUtil.getUniqueKey());
		collectionProtocol.setShortTitle("cp"+UniqueKeyGeneratorUtil.getUniqueKey());
		collectionProtocol.setEnrollment(2);

		collectionProtocol.setSpecimenLabelFormat("");
		collectionProtocol.setDerivativeLabelFormat("");
		collectionProtocol.setAliquotLabelFormat("");
		System.out.println("reached setUp");

		try
		{
			collectionProtocol.setStartDate(Utility.parseDate("08/15/2003", Utility.datePattern("08/15/1975")));

		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

        /*
		Collection collectionProtocolEventList = new LinkedHashSet();
		CollectionProtocolEvent collectionProtocolEvent = null;
		for(int specimenEventCount = 0 ;specimenEventCount<2 ;specimenEventCount++)
		{
			collectionProtocolEvent= new CollectionProtocolEvent();
			setCollectionProtocolEvent(collectionProtocolEvent);
			collectionProtocolEvent.setCollectionProtocol(collectionProtocol);
			collectionProtocolEventList.add(collectionProtocolEvent);
		}
		collectionProtocol.setCollectionProtocolEventCollection(collectionProtocolEventList);
        */
				
        User principalInvestigator = new User();
        principalInvestigator.setId(new Long("1"));
        collectionProtocol.setPrincipalInvestigator(principalInvestigator);

		//User protocolCordinator = new User();
		//protocolCordinator.setId(new Long("2"));

        Collection protocolCordinatorCollection = new HashSet();
		//protocolCordinatorCollection.add(protocolCordinator);
        collectionProtocol.setCoordinatorCollection(protocolCordinatorCollection);

		return collectionProtocol;
	}
	

	public void testSearchCollectionProtocol()
	{
    	CollectionProtocol collectionProtocol = new CollectionProtocol();
    	CollectionProtocol cachedCollectionProtocol = (CollectionProtocol) TestCaseUtility.getObjectMap(CollectionProtocol.class);
    	collectionProtocol.setId((Long) cachedCollectionProtocol.getId());
       	//Logger.out.info(" searching domain object");
    	try {
        	// collectionProtocol = (CollectionProtocol) appService.createObject(collectionProtocol);
        	 List resultList = appService.search(CollectionProtocol.class,collectionProtocol);
        	 for (Iterator resultsIterator = resultList.iterator(); resultsIterator.hasNext();)
        	 {
        		 CollectionProtocol returnedcollectionprotocol = (CollectionProtocol) resultsIterator.next();
        		 //Logger.out.info(" Domain Object is successfully Found ---->  :: " + returnedcollectionprotocol.getTitle());
             }
          }
          catch (Exception e) {
        	//Logger.out.error(e.getMessage(),e);
	 		e.printStackTrace();
	 		//fail("Doesnot found collection protocol");
          }
	}
	
	
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
}
