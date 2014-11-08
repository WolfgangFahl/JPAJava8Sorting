package com.bitplan.java8sorttest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

public class FolderImpl implements Folder {

	String name;
	
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
	
	List<Document> documents=new ArrayList<Document>();
	
	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> pdocuments) {
		documents=pdocuments;
	}
	
	public void addDocument(Document document) { 
		documents.add(document);
		document.setParentFolder(this);
  }

	public List<Document> getDocumentsByModificationDate() {
		List<Document> docs = this.getDocuments();
		TestEclipseLinkSorting.LOGGER.log(Level.INFO, "sorting " + docs.size() + "documents by name");
		Comparator<Document> comparator = new ByNameComparator();
		Collections.sort(docs, comparator);
		return docs;
	}

	@Override
	public Folder getImpl() {
		return this;
	}
	

}
