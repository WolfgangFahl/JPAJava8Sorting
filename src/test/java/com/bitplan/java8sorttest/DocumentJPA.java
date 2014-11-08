package com.bitplan.java8sorttest;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@Entity(name="Document")
@Table(name = "document")
@XmlRootElement(name = "document")
public class DocumentJPA implements Document,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1206897141744490247L;
  
	@Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

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
	@Override
	public Folder getParentFolder() {
		return getImpl().getParentFolder();
	}
	
	@Override
	public void setParentFolder(Folder parentFolder) {
		getImpl().setParentFolder(parentFolder);
	}
}