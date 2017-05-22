package de.spiritaner.maz.util.document;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class which handles reading the merge-file-data from
 * either a CSV or XLS/XLSX file.
 */
public class MailMergeData {
	final static Logger logger = Logger.getLogger(MailMergeData.class);

	private List<String> headers = new ArrayList<>();
	private List<List<String>> values = new ArrayList<>();

	/**
	 * Return a list of rows containing the data-values.
	 *
	 * @return a list of rows, each containing a list of data-values as strings.
	 */
	public List<List<String>> getData() {
		return values;
	}

	/**
	 * A list of header-names that are used to replace the templates.
	 *
	 * @return The header-names as found in the .csv/.xls/.xlsx file.
	 */
	public List<String> getHeaders() {
		return headers;
	}
}