package com.bitplan.java8deleagation;

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
public class TestEclipseLinkSortingWithDelegation {
	protected static Logger LOGGER = Logger.getLogger("com.bitplan.storage.sql");
	String folderXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
			"<folder>\n" + 
			"    <documents>\n" + 
			"        <document>\n" + 
			"            <name>test3</name>\n" + 
			"            <Folder>testFolder</Folder>\n" + 
			"        </document>\n" + 
			"        <document>\n" + 
			"            <name>test2</name>\n" + 
			"            <Folder>testFolder</Folder>\n" + 
			"        </document>\n" + 
			"        <document>\n" + 
			"            <name>test1</name>\n" + 
			"            <Folder>testFolder</Folder>\n" + 
			"        </document>\n" + 
			"    </documents>\n" + 
			"    <name>testFolder</name>\n" + 
			"</folder>\n";

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
					"com.bitplan.java8sorting", jpaProperties);
			entityManager = emf.createEntityManager();
		}
		return entityManager;
	}

	/**
	 * persist the given Folder with the given entityManager
	 * @param em
	 * @param folderJpa
	 */
	public void persist(EntityManager em, FolderJPA folderJpa) {
		em.getTransaction().begin();
		em.persist(folderJpa);
		em.getTransaction().commit();		
	}
	
	@Test
	public void testJPA1Write() throws Exception {
		EntityManager em = getEntityManager();
		persist(em,FolderJPA.fromXML(folderXml));
		Query query = em.createQuery("select d from Document1 d");
		@SuppressWarnings("unchecked")
		List<Document> documents = query.getResultList();
		assertEquals(3,documents.size());
	}

	/**
	 * get the folders for the given query
	 * @param em
	 * @param sql
	 * @return
	 */
	public List<Folder> getFoldersForQuery(EntityManager em, String sql) {
		Query query = em.createQuery(sql);
    @SuppressWarnings("unchecked")
		List<Folder> folders = query.getResultList();
    return folders;
	}
	
	@Test
	public void testJPA2ReadWithSorting() throws Exception {
		EntityManager em = getEntityManager();
		String sql="select f from Folder1 f";
		List<Folder> folders = getFoldersForQuery(em,sql);
    // folders size is zero at this point this test might have been run standalone
    // so we do the same as testJPA1Write to populate the database
    if (folders.size()==0) {
    	persist(em,FolderJPA.fromXML(folderXml));
    	folders = getFoldersForQuery(em,sql);
    }
    assertEquals(1,folders.size());
    Folder folder=folders.get(0);
    List<Document> sortedDocuments = folder.getDocumentsByName();
	}

	@Test
	public void testAsXml() throws Exception {
		FolderJPA folderJPA = FolderJPA.getFolderExample();
		String xml = folderJPA.asXML();
		System.out.println(xml);
		assertEquals(folderXml, xml);
	}

	@Test
	public void testXMLSorting() throws Exception {
		FolderJPA folder = FolderJPA.fromXML(folderXml);
		assertNotNull(folder);
		List<Document> sortedDocuments = folder.getDocumentsByName();
		int index=0;
		for (Document document : sortedDocuments) {
			System.out.println(document.getName());
			index++;
			assertEquals("test"+index,document.getName());
		}

	}
}
