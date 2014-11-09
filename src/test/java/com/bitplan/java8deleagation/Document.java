package com.bitplan.java8deleagation;

/**
 * Document interface
 * 
 * @author wf
 *
 */
public interface Document {

	public Document getImpl();

	public default String getName() {
		return getImpl().getName();
	}

	public default void setName(String name) {
		getImpl().setName(name);
	};

	public default Folder getParentFolder() {
		return getImpl().getParentFolder();
	}

	public default void setParentFolder(Folder parentFolder) {
		getImpl().setParentFolder(parentFolder);
	}
}