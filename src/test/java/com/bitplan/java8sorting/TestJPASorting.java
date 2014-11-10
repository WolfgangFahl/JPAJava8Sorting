package com.bitplan.java8sorting;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.Table;

import org.eclipse.persistence.indirection.IndirectList;
import org.junit.Test;

/**
 * Testcase for
 * http://stackoverflow.com/questions/26816650/java8-collections-sort
 * -sometimes-does-not-sort-jpa-returned-lists
 * 
 * @author wf
 *
 */
public class TestJPASorting {

	// the number of documents we want to sort
	public static final int NUM_DOCUMENTS = 3;

	// Logger for debug outputs
	protected static Logger LOGGER = Logger.getLogger("com.bitplan.java8sorting");

	/**
	 * a classic comparator
	 * 
	 * @author wf
	 *
	 */
	public static class ByNameComparator implements Comparator<Document> {

		// @Override
		public int compare(Document d1, Document d2) {
			LOGGER
					.log(Level.INFO, "comparing " + d1.getName() + "<=>" + d2.getName());
			return d1.getName().compareTo(d2.getName());
		}
	}

	// Document Entity - the sort target
	@Entity(name = "Document")
	@Table(name = "document")
	@Access(AccessType.FIELD)
	public static class Document {
		@Id
		String name;

		@ManyToOne
		Folder parentFolder;

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name
		 *          the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the parentFolder
		 */
		public Folder getParentFolder() {
			return parentFolder;
		}

		/**
		 * @param parentFolder
		 *          the parentFolder to set
		 */
		public void setParentFolder(Folder parentFolder) {
			this.parentFolder = parentFolder;
		}
	}

	// Folder entity - owning entity for documents to be sorted
	@Entity(name = "Folder")
	@Table(name = "folder")
	@Access(AccessType.FIELD)
	public static class Folder {
		@Id
		String name;

		// see
		// http://stackoverflow.com/questions/8301820/onetomany-relationship-is-not-working
		@OneToMany(cascade = CascadeType.ALL, mappedBy = "parentFolder"
  	// comment out to FetchType you'd like to see - default is LAZY
		// fetch=FetchType.LAZY)
		// fetch=FetchType.EAGER)
		)
		List<Document> documents;

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name
		 *          the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the documents
		 */
		public List<Document> getDocuments() {
			return documents;
		}

		/**
		 * @param documents
		 *          the documents to set
		 */
		public void setDocuments(List<Document> documents) {
			this.documents = documents;
		}

		/**
		 * get the documents of this folder by name
		 * 
		 * @return a sorted list of documents
		 */
		public List<Document> getDocumentsByName() {
			List<Document> docs = this.getDocuments();
			LOGGER.log(Level.INFO, "sorting " + docs.size() + " documents by name");
			if (docs instanceof IndirectList) {
				LOGGER.log(Level.INFO, "The document list is an IndirectList");
			}
			Comparator<Document> comparator = new ByNameComparator();
			// here is the culprit - do or don't we sort correctly here?
			Collections.sort(docs, comparator);
			return docs;
		}

		/**
		 * get a folder example (for testing)
		 * 
		 * @return - a test folder with NUM_DOCUMENTS documents
		 */
		public static Folder getFolderExample() {
			Folder folder = new Folder();
			folder.setName("testFolder");
			folder.setDocuments(new ArrayList<Document>());
			for (int i = NUM_DOCUMENTS; i > 0; i--) {
				Document document = new Document();
				document.setName("test" + i);
				document.setParentFolder(folder);
				folder.getDocuments().add(document);
			}
			return folder;
		}
	}

	/**
	 * possible Database configurations using generic persistence.xml: <?xml
	 * version="1.0" encoding="UTF-8"?> <!-- generic persistence.xml which only
	 * specifies a persistence unit name --> <persistence
	 * xmlns="http://java.sun.com/xml/ns/persistence" version="2.0">
	 * <persistence-unit name="com.bitplan.java8sorting"
	 * transaction-type="RESOURCE_LOCAL"> <description>sorting test</description>
	 * <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
	 * <exclude-unlisted-classes>false</exclude-unlisted-classes> <properties>
	 * <!-- set programmatically --> </properties> </persistence-unit>
	 * </persistence>
	 */
	// in MEMORY database
	public static final JPASettings JPA_DERBY = new JPASettings("Derby",
			"org.apache.derby.jdbc.EmbeddedDriver",
			"jdbc:derby:memory:test-jpa;create=true", "APP", "APP");
	// MYSQL Database
	// needs preparation:
	// create database testsqlstorage;
	// grant all privileges on testsqlstorage to cm@localhost identified by
	// 'secret';
	public static final JPASettings JPA_MYSQL = new JPASettings("MYSQL",
			"com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/testsqlstorage",
			"cm", "secret");

	/**
	 * Wrapper class for JPASettings
	 * 
	 * @author wf
	 *
	 */
	public static class JPASettings {
		String driver;
		String url;
		String user;
		String password;
		String targetDatabase;

		EntityManager entityManager;

		/**
		 * @param driver
		 * @param url
		 * @param user
		 * @param password
		 * @param targetDatabase
		 */
		public JPASettings(String targetDatabase, String driver, String url,
				String user, String password) {
			this.driver = driver;
			this.url = url;
			this.user = user;
			this.password = password;
			this.targetDatabase = targetDatabase;
		}

		/**
		 * get an entitymanager based on my settings
		 * 
		 * @return the EntityManager
		 */
		public EntityManager getEntityManager() {
			if (entityManager == null) {
				Map<String, String> jpaProperties = new HashMap<String, String>();
				jpaProperties.put("eclipselink.ddl-generation.output-mode", "both");
				jpaProperties.put("eclipselink.ddl-generation",
						"drop-and-create-tables");
				jpaProperties.put("eclipselink.target-database", targetDatabase);
				jpaProperties.put("eclipselink.logging.level", "FINE");

				jpaProperties.put("javax.persistence.jdbc.user", user);
				jpaProperties.put("javax.persistence.jdbc.password", password);
				jpaProperties.put("javax.persistence.jdbc.url", url);
				jpaProperties.put("javax.persistence.jdbc.driver", driver);

				EntityManagerFactory emf = Persistence.createEntityManagerFactory(
						"com.bitplan.java8sorting", jpaProperties);
				entityManager = emf.createEntityManager();
			}
			return entityManager;
		}
	}

	/**
	 * persist the given Folder with the given entityManager
	 * 
	 * @param em
	 *          - the entityManager
	 * @param folderJpa
	 *          - the folder to persist
	 */
	public void persist(EntityManager em, Folder folder) {
		em.getTransaction().begin();
		em.persist(folder);
		em.getTransaction().commit();
	}

	/**
	 * check the sorting - assert that the list has the correct size NUM_DOCUMENTS
	 * and that documents are sorted by name assuming test# to be the name of the
	 * documents
	 * 
	 * @param sortedDocuments
	 *          - the documents which should be sorted by name
	 */
	public void checkSorting(List<Document> sortedDocuments) {
		assertEquals(NUM_DOCUMENTS, sortedDocuments.size());
		for (int i = 1; i <= NUM_DOCUMENTS; i++) {
			Document document = sortedDocuments.get(i - 1);
			assertEquals("docs are not sorted","test" + i, document.getName());
		}
	}

	/**
	 * this test case shows that the list of documents retrieved will not be
	 * sorted if JDK8 and lazy fetching is used
	 */
	@Test
	public void testSorting() {
		// get a folder with a few documents
		Folder folder = Folder.getFolderExample();
		// get an entitymanager JPA_DERBY=inMemory JPA_MYSQL=Mysql disk database
		EntityManager em = JPA_DERBY.getEntityManager();
		// persist the folder
		persist(em, folder);
		// sort list directly created from memory
		checkSorting(folder.getDocumentsByName());

		// detach entities;
		em.clear();
		// get all folders from database
		String sql = "select f from Folder f";
		Query query = em.createQuery(sql);
		@SuppressWarnings("unchecked")
		List<Folder> folders = query.getResultList();
		// there should be exactly one
		assertEquals(1, folders.size());
		// get the first folder
		Folder folderJPA = folders.get(0);
		// sort the documents retrieved
		checkSorting(folderJPA.getDocumentsByName());
	}
}
