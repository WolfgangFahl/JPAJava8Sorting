package com.bitplan.java8sorttest;

import java.util.List;

/**
 * Folder interface
 * @author wf
 *
 */
public interface Folder {
	public List<Document> getDocuments();
	public void setDocuments(List<Document> documents);
	public List<Document> getDocumentsByModificationDate();
}
