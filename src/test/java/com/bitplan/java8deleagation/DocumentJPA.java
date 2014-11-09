package com.bitplan.java8deleagation;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Combined JPA/JAXB implementation of Document - delegates function calls to DocumentImpl
 * @author wf
 *
 */
@Entity(name="Document1")
@Table(name = "document1")
@XmlRootElement(name = "document")
@Access(AccessType.PROPERTY)
public class DocumentJPA implements Document,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1206897141744490247L;

	/**
	 * default constructor to make jaxb happy
	 */
	public DocumentJPA(){};
	
	public DocumentJPA(String name) {
		this.setName(name);
	}
	
	@Transient
	Document impl=new DocumentImpl();
	@Override
	public Document getImpl() {
		return impl;
	}

	@Override
	@Id
	public String getName() {
		return getImpl().getName();
	}

	@Override
	public void setName(String name) {
		getImpl().setName(name);
	};
	
	/**
	 * @return the parentFolder
	 */
	@ManyToOne(targetEntity=FolderJPA.class)
	@XmlElements({
    @XmlElement(name="Folder", type=FolderJPA.class)
  })
	
	@XmlIDREF
	@Override
	public Folder getParentFolder() {
		return getImpl().getParentFolder();
	}
	
	@Override
	public void setParentFolder(Folder parentFolder) {
		getImpl().setParentFolder(parentFolder);
	}
}