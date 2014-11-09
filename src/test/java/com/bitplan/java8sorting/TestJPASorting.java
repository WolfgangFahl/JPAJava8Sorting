package com.bitplan.java8sorting;

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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Persistence;
import javax.persistence.Table;

import org.eclipse.persistence.indirection.IndirectList;
import org.junit.Test;

public class TestJPASorting {
	protected static Logger LOGGER = Logger.getLogger("com.bitplan.storage.sql");
	
	public static class ByNameComparator implements Comparator<Document> {

		// @Override
		public int compare(Document d1, Document d2) {
			LOGGER.log(Level.INFO,"comparing " + d1.getName() + "<=>" + d2.getName());
			return d1.getName().compareTo(d2.getName());
		}
	}
	
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
		 * @param name the name to set
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
		 * @param parentFolder the parentFolder to set
		 */
		public void setParentFolder(Folder parentFolder) {
			this.parentFolder = parentFolder;
		}
	}
	
	@Entity(name = "Folder")
	@Table(name = "folder")
	@Access(AccessType.FIELD)
	public static class Folder {
		@Id
		String name;

		@OneToMany(cascade = CascadeType.ALL, mappedBy = "parentFolder")
		List<Document> documents=new ArrayList<Document>();

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the name to set
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
		 * @param documents the documents to set
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
			Collections.sort(docs, comparator);
			return docs;
		}
		
		/**
		 * get a folder example (for testing)
		 * @return - a test folder with three documents
		 */
		public static Folder getFolderExample() {
			Folder folder = new Folder();
			folder.setName("testFolder");
			for (int i=3;i>0;i--) {
				Document document=new Document();
				document.setName("test"+i);
				document.setParentFolder(folder);
				folder.getDocuments().add(document);
			}
			return folder;
		}
	}
	
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
	public void persist(EntityManager em, Folder folder) {
		em.getTransaction().begin();
		em.persist(folder);
		em.getTransaction().commit();		
	}
	

	@Test
	public void testSorting() {
		Folder folder=Folder.getFolderExample();
		EntityManager em=getEntityManager();
		persist(em,folder);
		List<Document> sortedDocuments = folder.getDocumentsByName();
	}
}
