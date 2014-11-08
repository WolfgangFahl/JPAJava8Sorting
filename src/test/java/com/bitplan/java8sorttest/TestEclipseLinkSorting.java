package com.bitplan.java8sorttest;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.junit.Test;
import org.junit.runners.MethodSorters;

import org.junit.FixMethodOrder;
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
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestEclipseLinkSorting {
	protected static Logger LOGGER = Logger.getLogger("com.bitplan.storage.sql");
	String folderXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
			+ "<folder>\n"
			+ "    <document>\n"
			+ "        <documents>\n"
			+ "            <name>test2</name>\n"
			+ "        </documents>\n"
			+ "        <documents>\n"
			+ "            <name>test1</name>\n"
			+ "        </documents>\n"
			+ "    </document>\n"
			+ "    <name>testFolder</name>\n" + "</folder>\n";

	static EntityManager entityManager;

	public static EntityManager getEntityManager() {
		if (entityManager == null) {
			Map<String, String> jpaProperties = new HashMap<String, String>();
			jpaProperties.put("eclipselink.ddl-generation.output-mode", "both");
			jpaProperties.put("eclipselink.ddl-generation", "drop-and-create-tables");
			jpaProperties.put("eclipselink.target-database", "MYSQL");
			jpaProperties.put("eclipselink.logging.level", "FINE");

			jpaProperties.put("javax.persistence.jdbc.user", "cm");
			jpaProperties.put("javax.persistence.jdbc.password", "secret");
			jpaProperties.put("javax.persistence.jdbc.url",
					"jdbc:mysql://localhost:3306/testsqlstorage");
			jpaProperties.put("javax.persistence.jdbc.driver",
					"com.mysql.jdbc.Driver");

			EntityManagerFactory emf = Persistence.createEntityManagerFactory(
					"com.bitplan.testentity", jpaProperties);
			entityManager = emf.createEntityManager();
		}
		return entityManager;
	}

	@Test
	public void testJPA1Write() throws Exception {
		EntityManager em = getEntityManager();
		FolderJPA folderJpa = FolderJPA.fromXML(folderXml);
		em.getTransaction().begin();
		em.persist(folderJpa);
		em.getTransaction().commit();
	}

	@Test
	public void testJPA2Read() throws Exception {
		EntityManager em = getEntityManager();
		Query query = em.createQuery("select f from Folder f");
    @SuppressWarnings("unchecked")
		List<Folder> folders = query.getResultList();
    assertEquals(1,folders.size());
	}

	@Test
	public void testAsXml() throws Exception {
		FolderJPA folderJPA = FolderJPA.getFolderExample();
		String xml = folderJPA.asXML();
		System.out.println(xml);
		assertEquals(folderXml, xml);
	}

	@Test
	public void testSorting() throws Exception {
		FolderJPA folder = FolderJPA.fromXML(folderXml);
		assertNotNull(folder);
		List<Document> sortedDocuments = folder.getDocumentsByModificationDate();
		for (Document document : sortedDocuments) {
			System.out.println(document.getName());
		}

	}
}
