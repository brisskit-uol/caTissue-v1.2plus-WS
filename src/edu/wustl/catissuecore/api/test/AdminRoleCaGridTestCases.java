package edu.wustl.catissuecore.api.test;

import java.util.List;

import edu.wustl.catissuecore.domain.MolecularSpecimen;
import edu.wustl.catissuecore.domain.TissueSpecimen;
import gov.nih.nci.common.util.HQLCriteria;
import gov.nih.nci.system.applicationservice.ApplicationService;

public class AdminRoleCaGridTestCases extends CaTissueBaseTestCase 
{
  
		/**
		 * Test search for count queries with scientist user
		 */
		public void testCaGridCountQueryWithAdmin()
		{
			try
			{
				StringBuffer hql = new StringBuffer();
			  	String targetClassName = TissueSpecimen.class.getName(); 
				hql.append("GridQuery:select count(*)");
				hql.append(" from "+targetClassName+" xxTargetAliasxx");
				List result = appService.query(new HQLCriteria(hql.toString()), targetClassName);
				System.out.println("Result count"+result.get(0));
				
			 }
			 catch(Exception e)
			 {
				System.out
						.println("AdminRoleCaGridTestCases.testCaGridCountQueryWithScientist()"+e.getMessage());
				 e.printStackTrace();
				fail("Super Admin cannot do count query ");
			 }
		}
		
		public void testAPICountQueryWithAdmin()
		{
			try
			{
			  	String targetClassName = TissueSpecimen.class.getName(); 
				appService.getQueryRowCount(new TissueSpecimen(), targetClassName);
			 }
			 catch(Exception e)
			 {
				System.out
						.println("AdminRoleCaGridTestCases.testAPICountQueryWithScientist()"+e.getMessage());
				 e.printStackTrace();
				 assertEquals("caTissue doesnot support queries which returns result list containing instances of classes other than caTissue domain model",
						 e.getMessage());			
			 }
		}
		
		public void testAPISelectQueryWithAdmin()
		{
			try
			{
				StringBuffer hql = new StringBuffer();
			  	String targetClassName = MolecularSpecimen.class.getName(); 
				hql.append("select id,label");
				hql.append(" from "+targetClassName+" xxTargetAliasxx");
				appService.query(new HQLCriteria(hql.toString()), targetClassName);
			 }
			 catch(Exception e)
			 {
				System.out
						.println("AdminRoleCaGridTestCases.testCaGridCountQueryWithScientist()"+e.getMessage());
				 e.printStackTrace();
				 fail("Super Admin cannot do select attribute query ");			
			 }
		}
	}