package com.bitplan.java8deleagation;

import java.util.List;

/**
 * Folder interface
 * 
 * @author wf
 *
 */
public interface Folder {
	public Folder getImpl();

	public default String getName() {
		return getImpl().getName();
	}

	public default void setName(String name) {
		getImpl().setName(name);
	}

	public default List<Document> getDocuments() {
		return getImpl().getDocuments();
	}

	public default void setDocuments(List<Document> documents) {
		getImpl().setDocuments(documents);
	}

	public default List<Document> getDocumentsByName() {
		return getImpl().getDocumentsByName();
	}
	
	public default void addDocument(Document document) { getImpl().addDocument(document);}

}
