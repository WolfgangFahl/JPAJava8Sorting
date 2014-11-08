package com.bitplan.java8sorttest;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("restriction")
@XmlRootElement(name="folder")
@Entity(name="Folder")
@Table(name = "folder")
public class FolderImpl implements Folder,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -590883790792959372L;
	
	List<Document> documents;

	/**
	 * default constructor for JaxB
	 */
	public FolderImpl() {
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
		TestEclipseLinkSorting.LOGGER.log(Level.INFO, "sorting " + docs.size() + "documents by name");
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
		JAXBContext context = JAXBContext.newInstance(FolderImpl.class);
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
	public static FolderImpl fromXML(String xml) throws Exception {
		JAXBContext jaxbContext = JAXBContext.newInstance(FolderImpl.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		StringReader reader = new StringReader(xml);
		FolderImpl folder = (FolderImpl) unmarshaller.unmarshal(reader);
		return folder;
	}
	
	/**
	 * get a folder example
	 * @return
	 */
	public static FolderImpl getFolderExample() {
		List<Document> documents = new ArrayList<Document>();
		documents.add(new DocumentImpl("test2"));
		documents.add(new DocumentImpl("test1"));
		FolderImpl folder = new FolderImpl();
		folder.setDocuments(documents);
		return folder;
	}
}