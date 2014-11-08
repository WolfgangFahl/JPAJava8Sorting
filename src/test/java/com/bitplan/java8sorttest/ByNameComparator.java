package com.bitplan.java8sorttest;

import java.util.Comparator;
import java.util.logging.Level;

public class ByNameComparator implements Comparator<Document> {

	// @Override
	public int compare(Document d1, Document d2) {
		TestEclipseLinkSorting.LOGGER.log(Level.INFO,"comparing " + d1.getName() + "<=>" + d2.getName());
		return d1.getName().compareTo(d2.getName());
	}
}