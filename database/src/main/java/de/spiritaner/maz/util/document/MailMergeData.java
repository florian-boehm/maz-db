package de.spiritaner.maz.util.document;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/*
	Copyright (c) 2015, Dominik Stadler
	All rights reserved.
	https://github.com/centic9/poi-mail-merge

	Redistribution and use in source and binary forms, with or without
	modification, are permitted provided that the following conditions are met:

	* Redistributions of source code must retain the above copyright notice, this
	  list of conditions and the following disclaimer.

	* Redistributions in binary form must reproduce the above copyright notice,
	  this list of conditions and the following disclaimer in the documentation
	  and/or other materials provided with the distribution.

	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
	AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
	IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
	DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
	FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
	DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
	SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
	CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
	OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
	OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

	! ----------------------------------------------------------- !
	! The source code below was modified to fit the current needs !
	! ----------------------------------------------------------- !
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