package com.bitplan.java8sorttest;

import org.w3c.dom.stylesheets.DocumentStyle;

public class DocumentImpl implements Document {

	String name;
	Folder parentFolder;

	public DocumentImpl() {
	}

	public DocumentImpl(String name) {
		this.setName(name);
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

	public Folder getParentFolder() {
		return parentFolder;
	}
	
	/**
	 * @param parentFolder the parentFolder to set
	 */
	public void setParentFolder(Folder parentFolder) {
		this.parentFolder = parentFolder;
	}

	
	public Document getImpl() {
		return this;
	}

}
