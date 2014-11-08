package com.bitplan.java8sorttest;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name="Document")
@Table(name = "document")
public class DocumentImpl implements Document, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1206897141744490247L;
  
	@Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  
	String name;
	FolderImpl parentFolder;

	/**
	 * @return the parentFolder
	 */
	@ManyToOne(targetEntity=FolderImpl.class)
	public FolderImpl getParentFolder() {
		return parentFolder;
	}

	/**
	 * @param parentFolder the parentFolder to set
	 */
	public void setParentFolder(FolderImpl parentFolder) {
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