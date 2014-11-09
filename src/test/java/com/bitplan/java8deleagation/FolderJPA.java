package com.bitplan.java8deleagation;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * combined JPA, JAXB implementation of Folder -  delegates Function calls to FolderImpl
 * @author wf
 *
 */
@Entity(name = "Folder")
@Table(name = "folder")
@XmlRootElement(name = "folder")
@Access(AccessType.PROPERTY)
public class FolderJPA implements Folder, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -590883790792959372L;

	/**
	 * default constructor for JaxB
	 */
	public FolderJPA() {
	};

	/**
	 * getter for name
	 * 
	 * @return name
	 */
	@Id
	@XmlID
	@Column(name = "name")
	@Override
	public String getName() {
		return getImpl().getName();
	}

	@Override
	public void setName(String name) {
		getImpl().setName(name);
	}

	/**
	 * @return the documents
	 */
	@XmlElementWrapper(name = "documents")
	@XmlElement(type = DocumentJPA.class,name="document")
	@OneToMany(targetEntity = DocumentJPA.class, cascade = CascadeType.ALL, mappedBy = "parentFolder")
	@Override
	public List<Document> getDocuments() {
		return getImpl().getDocuments();
	}
	
	@Override
	public void setDocuments(List<Document> pdocuments) { 
  	getImpl().setDocuments(pdocuments); 
  }

	/**
	 * marshal me to an xml string
	 * 
	 * @return
	 * @throws Exception
	 */
	public String asXML() throws Exception {
		JAXBContext context = JAXBContext.newInstance(FolderJPA.class);
		Marshaller marshaller = context.createMarshaller();
		java.io.StringWriter sw = new StringWriter();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(this, sw);
		String result = sw.toString();
		return result;
	}

	/**
	 * get a Folder from an XML String
	 * 
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public static FolderJPA fromXML(String xml) throws Exception {
		JAXBContext jaxbContext = JAXBContext.newInstance(FolderJPA.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		StringReader reader = new StringReader(xml);
		FolderJPA folder = (FolderJPA) unmarshaller.unmarshal(reader);
		return folder;
	}
	
	public void addDocument(Document document) { 
		this.getDocuments().add(document);
		document.setParentFolder(this);
  }

	/**
	 * get a folder example (for testing)
	 * @return - a test folder with three documents
	 */
	public static FolderJPA getFolderExample() {
		FolderJPA folder = new FolderJPA();
		folder.setName("testFolder");
		folder.addDocument(new DocumentJPA("test3"));
		folder.addDocument(new DocumentJPA("test2"));
		folder.addDocument(new DocumentJPA("test1"));
		return folder;
	}
	
	@Transient
	Folder impl = new FolderImpl();

	@Override
	public Folder getImpl() {
		return impl;
	}

}