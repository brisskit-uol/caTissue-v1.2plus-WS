
package uk.ac.le.brisskit;

import java.util.Iterator;
import java.util.List;

import clinical_annotation.AlcoholHealthAnnotation;
import clinical_annotation.TreatmentAnnotation;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.domain.StudyFormContext;
import edu.wustl.catissuecore.domain.deintegration.ParticipantRecordEntry;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationServiceProvider;
import gov.nih.nci.system.comm.client.ClientSession;

/*
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.Variables;
import org.apache.log4j.PropertyConfigurator;
 */
/**
 * <!-- LICENSE_TEXT_START -->
 * Copyright 2001-2004 SAIC. Copyrigh
 * t 2001-2003 SAIC. This software was developed in conjunction with the National Cancer Institute,
 * and so to the extent government employees are co-authors, any rights in such works shall be subject to Title 17 of the United States Code, section 105.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the disclaimer of Article 3, below. Redistributions
 * in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself,
 * wherever such third-party acknowledgments normally appear.
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software.
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize
 * the recipient to use any trademarks owned by either NCI or SAIC-Frederick.
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE,
 * SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * @author caBIO Team
 * @version 1.0
 */

/**
 * ClientDemo_CA.java demonstartes various ways to execute searches with and without
 * using Application Service Layer (convenience layer that abstracts building criteria
 * Uncomment different scenarios below to demonstrate the various types of searches
 */

public class ClientDemo_CA
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}
	private static Logger logger = Logger.getCommonLogger(ClientDemo_CA.class);
	//TODO 2
	private final static String STATIC_ENTITY_CLASS_NAME = "edu.wustl.catissuecore.domain.deintegration.ParticipantRecordEntry";
	//Make sure that there is only one class in the db by this name.
	private final static String DE_CLASS_NAME = "AlcoholHealthAnnotation";
	private static String jbossServerUrl = null;
	private static String userName = null;
	private static String password = null;
	static ApplicationService appServiceDEEntity = null;
	static ApplicationService appServiceCatissue = null;

	private static EntityManagerInterface entityManager = EntityManager.getInstance();

	public static void main(String[] args)
	{
		try
		{
			System.out.println("*** ClientDemo_CA...");
			initialiseInstanceVariables(args);
			HttpsConnectionUtil.trustAllHttpsCertificates();
			initCaTissueService();
			ClientSession cs = ClientSession.getInstance();

			//caTissue Service
			initCaTissueService();
			//TODO 3
			cs.startSession(userName, password);
			edu.wustl.catissuecore.domain.Participant participant = searchParticipant();
			if (participant != null)
			{
				addAnnotationToStaticEntity(participant);
				System.out.println("Added annotation");
				//Query
				queryDEClass(participant.getId());

				queryTreatmentAnnotation();
			}
			cs.terminateSession();
		}
		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
			ex.printStackTrace();
			return;
		}

	}

	private static void initialiseInstanceVariables(String[] args) throws ApplicationException
	{
		if (args.length < 1)
		{
			throw new ApplicationException("Please provide proper arguments");
		}
		jbossServerUrl = args[0];
		userName = args[1];
		password = args[2];
	}

	/**
		 * @return
		 */
	//TODO 4
	private static void initCaTissueService()
	{
		appServiceCatissue = ApplicationServiceProvider.getRemoteInstance(jbossServerUrl
				+ "/catissuecore/http/remoteService");
		System.out.println("appServiceCatissue = " + appServiceCatissue);
	}

	private static void initDEService()
	{
		appServiceDEEntity = ApplicationServiceProvider.getRemoteInstance(jbossServerUrl
				+ "/CA/http/remoteService");
	}

	/**
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	private static void addAnnotationToStaticEntity(Participant participant)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		try
		{
			initCaTissueService();
			System.out.println("Creating the new recordEntry.");
			ParticipantRecordEntry recEntry = getRecordEntry(participant);

			//DE Entity Service
			initDEService();
			//TODO 5
			Object deObjectToBeCreated = getDEToBeCreated(recEntry.getId());
			//			Object deObjectToBeCreated = getHealthExaminationAnnotation();

			//TODO 6
			AlcoholHealthAnnotation createdDE = (AlcoholHealthAnnotation) appServiceDEEntity
					.createObject(deObjectToBeCreated);
			System.out.println("Newly created Annotation = " + createdDE);

			System.out.println("Updating Integration Tables");

		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * @param createdDE
		 * @throws DynamicExtensionsApplicationException
		 * @throws DynamicExtensionsSystemException
		 * @throws ApplicationException
	 */
	private static ParticipantRecordEntry getRecordEntry(Participant participant)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException,
			ApplicationException
	{
		//initDEIntegrationService();
		Long particpantClassId = getEntityId(STATIC_ENTITY_CLASS_NAME);
		System.out.println("Entity Id for scgRecEntry " + particpantClassId);

		Long deContainerId = getContainerId(DE_CLASS_NAME);
		System.out.println("Container Id for Person " + deContainerId);

		ParticipantRecordEntry recEntry = initRecordEntry(participant, getFormContext(
				particpantClassId, deContainerId));
		return insertRecordEntry(recEntry);
	}

	/**
	 * @param recEntry
	 * @param createdDE
	 * @throws ApplicationException
	 */
	private static ParticipantRecordEntry insertRecordEntry(ParticipantRecordEntry recEntry)
			throws ApplicationException
	{
		System.out.println("Creating Specimen Record Entry");
		Object createdEntityMapRecord = appServiceCatissue.createObject(recEntry);
		System.out.println("Inserted Specimen Record Entry");
		return (ParticipantRecordEntry) createdEntityMapRecord;

	}

	/**
	 * @param participant
	 * @param dynamicRecordId
	 * @return
	 * @throws ApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	private static ParticipantRecordEntry initRecordEntry(Participant participant,
			StudyFormContext formContext) throws DynamicExtensionsSystemException,
			ApplicationException
	{
		System.out.println("Initializing record entry");
		ParticipantRecordEntry recEntry = new ParticipantRecordEntry();
		getNextIdentifier(STATIC_ENTITY_CLASS_NAME);
		if (formContext != null)
		{
			recEntry.setActivityStatus("Active");
			//recEntry.setId(identifier);
			recEntry.setFormContext(formContext);
			recEntry.setParticipant(participant);
		}
		return recEntry;
	}

	/**
	 * @param participantId
	 */
	private static void queryTreatmentAnnotation()
	{

		TreatmentAnnotation treatmentAnnotation = new TreatmentAnnotation();
		treatmentAnnotation.setAgent("ACACIA");
		treatmentAnnotation.setOtherAgent("Other");

		try
		{
			System.out.println("Searching treatmentAnnotation " + treatmentAnnotation);
			initDEService();

			List resultList = appServiceCatissue.search(TreatmentAnnotation.class,
					treatmentAnnotation);

			System.out.println("Returned treatmentAnnotation " + resultList);
			if (resultList != null)
			{
				Iterator resultsIterator = resultList.iterator();
				if (resultsIterator.hasNext())
				{
					TreatmentAnnotation returnedtreatmentAnnotation = (TreatmentAnnotation) resultsIterator
							.next();
					System.out.println("Searched returnedtreatmentAnnotation "
							+ returnedtreatmentAnnotation);
					System.out.println("returnedtreatmentAnnotation Id: "
							+ returnedtreatmentAnnotation.getId());
					System.out
							.println("----------------------------------------------------------------------");
					System.out.println("/n returnedtreatmentAnnotation retrieved Agent : "
							+ returnedtreatmentAnnotation.getAgent());

				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @param participantRecEntryId
	 * @return
	 * @throws ApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	private static Object getDEToBeCreated(Long participantRecEntryId) throws ApplicationException,
			DynamicExtensionsSystemException
	{
		clinical_annotation.ParticipantRecordEntry deRecordEntry = new clinical_annotation.ParticipantRecordEntry();
		deRecordEntry.setId(participantRecEntryId);
		initDEService();
		List resultsList = appServiceDEEntity.search(
				clinical_annotation.ParticipantRecordEntry.class, deRecordEntry);
		if (resultsList.isEmpty())
		{
			throw new ApplicationException("Record entry not found.");
		}
		clinical_annotation.ParticipantRecordEntry pathRecEntry = (clinical_annotation.ParticipantRecordEntry) resultsList
				.iterator().next();

		AlcoholHealthAnnotation alcoholHealthAnnotation = new AlcoholHealthAnnotation();
		System.out.println("Getting next id");
		Long smokingHistoryId = getNextIdentifier(DE_CLASS_NAME);
		alcoholHealthAnnotation.setId(smokingHistoryId);

		alcoholHealthAnnotation.setAgent("Agent 1");
		alcoholHealthAnnotation.setDrinksPerWeek(new Long(7));
		alcoholHealthAnnotation.setOtherAgent("Other Agent 1");
		alcoholHealthAnnotation.setYearsAgentFree(new Double(1.2));
		alcoholHealthAnnotation.setParticipantRecordEntry_AlcoholHealthAnnotation(pathRecEntry);

		return alcoholHealthAnnotation;
	}

	/**
	 * @param participantId
	 */
	private static void queryDEClass(Long participantId)
	{
		try
		{
			System.out.println("Querying the DE class on the specimenId.");
			Long containerId = getContainerId(DE_CLASS_NAME);
			ParticipantRecordEntry recEntry = new ParticipantRecordEntry();
			Participant participant = new Participant();
			participant.setId(participantId);
			recEntry.setParticipant(participant);
			StudyFormContext formContext = new StudyFormContext();
			formContext.setContainerId(containerId);
			recEntry.setFormContext(formContext);
			initCaTissueService();

			List scgRecEntryResultList = appServiceCatissue.search(ParticipantRecordEntry.class,
					recEntry);
			for (Object result : scgRecEntryResultList)
			{
				ParticipantRecordEntry returnedRecEntry = (ParticipantRecordEntry) result;
				clinical_annotation.ParticipantRecordEntry deRecEntry = new clinical_annotation.ParticipantRecordEntry();
				deRecEntry.setId(returnedRecEntry.getId());

				AlcoholHealthAnnotation alcoholAnnotaion = new AlcoholHealthAnnotation();
				alcoholAnnotaion.setAgent("Agent 1");
				alcoholAnnotaion.setParticipantRecordEntry_AlcoholHealthAnnotation(deRecEntry);

				System.out.println("Searching treatmentRegimen " + alcoholAnnotaion);
				initDEService();

				List resultList = appServiceCatissue.search(AlcoholHealthAnnotation.class,
						alcoholAnnotaion);

				System.out.println("Returned treatmentRegimen " + resultList);
				if (resultList != null)
				{
					Iterator resultsIterator = resultList.iterator();
					if (resultsIterator.hasNext())
					{
						AlcoholHealthAnnotation returnedAlcoholAnnnoation = (AlcoholHealthAnnotation) resultsIterator
								.next();
						System.out.println("Searched AlcoholHealthAnnotation "
								+ returnedAlcoholAnnnoation);
						System.out.println("AlcoholHealthAnnotation participant record entry Id: "
								+ returnedAlcoholAnnnoation
										.getParticipantRecordEntry_AlcoholHealthAnnotation()
										.getId());
						System.out
								.println("----------------------------------------------------------------------");
						System.out.println("/n AlcoholHealthAnnotation retrieved Agent : "
								+ returnedAlcoholAnnnoation.getAgent());

					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @throws ApplicationException
	 * @throws DynamicExtensionsSystemException
	 *
	 */
	private static Long getNextIdentifier(String deEntity) throws ApplicationException,
			DynamicExtensionsSystemException
	{
		/*DetachedCriteria maxDEIdentifierCriteria = DetachedCriteria.forClass(deClass).setProjection( Property.forName("id").count());
		List identifierList = appServiceDEEntity.query(maxDEIdentifierCriteria, deClass.getName());

		if(identifierList!=null)
		{
			Iterator identifierIterator = identifierList.iterator();
			if(identifierIterator.hasNext())
			{
				Integer maxIdentifier = (Integer)identifierIterator.next();
				System.out.println("I = " + maxIdentifier);
				Long l =new Long(maxIdentifier.intValue() + 1);
				return (l);

			}
		}
		return null;*/
		return entityManager.getNextIdentifierForEntity(deEntity);
	}

	/**
	 *
	 */
	private static StudyFormContext getFormContext(Long staticEntityClassId, Long DEContainerId)
	{
		System.out.println("Searching Form Context : staticEntityClassId= " + staticEntityClassId
				+ " DEContainerId=" + DEContainerId);

		StudyFormContext formContext = new StudyFormContext();
		formContext.setContainerId(DEContainerId);
		try
		{

			List resultList = appServiceCatissue.search(StudyFormContext.class, formContext);

			Iterator resultsIterator = resultList.iterator();
			if (resultsIterator.hasNext())
			{
				StudyFormContext returnedFormContext = (StudyFormContext) resultsIterator.next();
				System.out.println(returnedFormContext);
				if (returnedFormContext != null)
				{
					return returnedFormContext;

				}
			}
		}
		catch (Exception e)
		{
			//Logger.out.error(e.getMessage(),e);
			e.printStackTrace();
		}

		return null;

	}

	/**
	 * @param string
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private static Long getContainerId(String string) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		Long entityId = getEntityId(string);
		return entityManager.getContainerIdForEntity(entityId);

	}

	/**
	 * @param string
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	private static Long getEntityId(String entityName) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		return entityManager.getEntityId(entityName);
		/*System.out.println("Here " + entityManager);

		if(entityManager!=null)
		{
			System.out.println("entity  name "  +entityName);
			EntityInterface entity = entityManager.getEntityByName(entityName);
			if(entity!=null)
			{
				System.out.println("entity "  + entity.getId());
				return entity.getId();
			}
		}
		return null;*/
	}

	/**
	 * @return
	 */
	private static edu.wustl.catissuecore.domain.Participant searchParticipant()
	{
		edu.wustl.catissuecore.domain.Participant participant = getParticipantToSearch();
		try
		{
			System.out.println("Searching participant " + participant.getId());
			List resultList = appServiceCatissue.search(
					edu.wustl.catissuecore.domain.Participant.class, participant);
			System.out.println("Returned participants " + resultList);
			if (resultList != null)
			{
				Iterator resultsIterator = resultList.iterator();
				if (resultsIterator.hasNext())
				{
					edu.wustl.catissuecore.domain.Participant returnedparticipant = (edu.wustl.catissuecore.domain.Participant) resultsIterator
							.next();
					System.out.println("Searched particiant " + participant);
					return returnedparticipant;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * @return
	 */
	private static edu.wustl.catissuecore.domain.Participant getParticipantToSearch()
	{
		edu.wustl.catissuecore.domain.Participant participant = new edu.wustl.catissuecore.domain.Participant();;
		//Set parameters for participant to be searched
		participant.setId(1L); //Set ID.

		return participant;
	}

}
