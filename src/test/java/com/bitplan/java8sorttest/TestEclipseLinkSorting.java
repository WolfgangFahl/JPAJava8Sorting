package com.bitplan.java8sorttest;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Persistence;
import javax.persistence.Table;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

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

	public static interface Document {
		public String getName();
		public Folder getParentFolder();
	}

	@Entity(name="Document")
	@Table(name = "document")
	public static class DocumentImpl implements Document, Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1206897141744490247L;
	  
		@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
	  
		String name;
		Folder parentFolder;

		/**
		 * @return the parentFolder
		 */
		@ManyToOne(targetEntity=Folder.class)
		public Folder getParentFolder() {
			return parentFolder;
		}

		/**
		 * @param parentFolder the parentFolder to set
		 */
		public void setParentFolder(Folder parentFolder) {
			this.parentFolder = parentFolder;
		}

		/**
		 * default constructor to make jaxb happy
		 */
		public DocumentImpl(){};
		
		public DocumentImpl(String pName) {
			name = pName;
		}

		public String getName() {
			return name;
		}
		
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
	}

	@XmlRootElement
	@Entity(name="Folder")
	@Table(name = "folder")
	public static class Folder implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -590883790792959372L;
		
		List<Document> documents;

		/**
		 * default constructor for JaxB
		 */
		public Folder() {
		};
		
	  @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

		/**
		 * @return the documents
		 */
		@XmlElementWrapper(name = "document")
		@XmlElement(type=DocumentImpl.class)
	  @OneToMany(targetEntity=DocumentImpl.class, cascade=CascadeType.ALL, mappedBy="parentFolder")
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

		public List<Document> getDocumentsByModificationDate() {
			List<Document> docs = this.getDocuments();
			LOGGER.log(Level.INFO, "sorting " + docs.size() + "documents by name");
			Comparator<Document> comparator = new ByNameComparator();
			Collections.sort(docs, comparator);
			return docs;
		}
		
		/**
		 * marshal me to an xml string
		 * @return
		 * @throws Exception
		 */
		public String asXML() throws Exception {
			JAXBContext context = JAXBContext.newInstance(Folder.class);
			Marshaller marshaller = context.createMarshaller();
			java.io.StringWriter sw = new StringWriter();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(this, sw);
			String result=sw.toString();
			return result;
		}
		
		/**
		 * get a Folder from an XML String
		 * @param xml
		 * @return
		 * @throws Exception
		 */
		public static Folder fromXML(String xml) throws Exception {
			JAXBContext jaxbContext = JAXBContext.newInstance(Folder.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			StringReader reader = new StringReader(xml);
			Folder folder = (Folder) unmarshaller.unmarshal(reader);
			return folder;
		}
		
		/**
		 * get a folder example
		 * @return
		 */
		public static Folder getFolderExample() {
			List<Document> documents = new ArrayList<Document>();
			documents.add(new DocumentImpl("test2"));
			documents.add(new DocumentImpl("test1"));
			Folder folder = new Folder();
			folder.setDocuments(documents);
			return folder;
		}
	}

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
