package com.bitplan.java8sorttest;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Test;

/**
 * http://stackoverflow.com/questions/26816650/java8-collections-sort-sometimes-
 * does-not-sort-jpa-returned-lists
 * 
 * <dependency> <groupId>org.eclipse.persistence</groupId>
 * <artifactId>eclipselink</artifactId> <!-- <version>2.6.0-M3</version> -->
 * <version>2.5.2</version> --> </dependency>
 * 
 * @author wf
 *
 */
public class TestEclipseLinkSorting {
	protected static Logger LOGGER = Logger.getLogger("com.bitplan.storage.sql");

	public static class ByNameComparator implements Comparator<Document> {

		// @Override
		public int compare(Document d1, Document d2) {
			LOGGER.log(Level.INFO,"comparing " + d1.getName() + "<=>" + d2.getName());
			return d1.getName().compareTo(d2.getName());
		}
	}
	
	@Test 
	public void testJPA() throws Exception {
		Map<String,String> jpaProperties = new HashMap<String,String>();
		jpaProperties.put("eclipselink.ddl-generation.output-mode","both");
		jpaProperties.put("eclipselink.ddl-generation","drop-and-create-tables");
		jpaProperties.put("eclipselink.target-database", "MYSQL");
		jpaProperties.put("eclipselink.logging.level","FINE");
		
		jpaProperties.put("javax.persistence.jdbc.user", "cm");
		jpaProperties.put("javax.persistence.jdbc.password","secret");
		jpaProperties.put("javax.persistence.jdbc.url","jdbc:mysql://localhost:3306/testsqlstorage");
		jpaProperties.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.bitplan.testentity", jpaProperties);
		EntityManager em = emf.createEntityManager();
		Folder folder=Folder.getFolderExample();
		em.getTransaction().begin();
		em.persist(folder);
		em.getTransaction().commit();
		
		// Query query = em.createQuery("select f from Folder f");
		
	}

	@Test
	public void testSorting() throws Exception {
		String folderXml="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
				"<folder>\n" + 
				"    <document>\n" + 
				"        <documents>\n" + 
				"            <name>test2</name>\n" + 
				"        </documents>\n" + 
				"        <documents>\n" + 
				"            <name>test1</name>\n" + 
				"        </documents>\n" + 
				"    </document>\n" + 
				"</folder>";
		Folder folder=Folder.fromXML(folderXml);	
		List<Document> sortedDocuments = folder.getDocumentsByModificationDate();
		for (Document document : sortedDocuments) {
			System.out.println(document.getName());
		}

	}
}
