package com.bitplan.java8deleagation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.persistence.indirection.IndirectList;

/**
 * implementation of Folder interface (independent of storage)
 * @author wf
 *
 */
public class FolderImpl implements Folder {

	protected static Logger LOGGER = Logger.getLogger("com.bitplan.storage.sql");
	
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
	
	/**
	 * add the given document to this folder
	 * @param document - the document to add
	 */
	public void addDocument(Document document) { 
		documents.add(document);
		document.setParentFolder(this);
  }

	/**
	 * get the documents of this folder by name
	 * @return a sorted list of documents
	 */
	public List<Document> getDocumentsByName() {
		List<Document> docs = this.getDocuments();
		LOGGER.log(Level.INFO, "sorting " + docs.size() + " documents by name");
		if (docs instanceof IndirectList) {
		  LOGGER.log(Level.INFO,"The document list is an IndirectList");	
	  }
		Comparator<Document> comparator = new ByNameComparator();
		Collections.sort(docs, comparator);
		return docs;
	}

	@Override
	public Folder getImpl() {
		return this;
	}
	

}
