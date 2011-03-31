/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 *-----------------------------------------------------------------------------*/

package org.serviceconnector.web.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.serviceconnector.web.IWebRequest;
import org.serviceconnector.web.WebUtil;

/**
 * The Class LogXMLLoader.
 */
public class LogsXMLLoader extends AbstractXMLLoader {

	/** The distinct logger set, prevent multiple entries of log files. */
	private Set<String> distinctLoggerSet;

	/**
	 * Instantiates a new default xml loader.
	 */
	public LogsXMLLoader() {
		distinctLoggerSet = new HashSet<String>();
	}

	/**
	 * Load body.
	 * 
	 * @param writer
	 *            the writer
	 * @param request
	 *            the request
	 * @throws Exception
	 *             the exception {@inheritDoc}
	 */
	@Override
	public final void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
		writer.writeStartElement("logs");
		String dateParameter = request.getParameter("date");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date today = cal.getTime();
		Date current = today;
		if (dateParameter != null) {
			current = WebUtil.getXMLDateFromString(dateParameter);
		}
		if (today.before(current)) {
			current = today;
		}
		// get previous and next date
		String next = WebUtil.getXMLNextDateAsString(current);
		String previous = WebUtil.getXMLPreviousDateAsString(current);
		// set selected date
		writer.writeAttribute("previous", previous);
		writer.writeAttribute("current", WebUtil.getXMLDateAsString(current));
		if (current.before(today)) {
			writer.writeAttribute("next", next);
		}
		// write available log levels
		writeLogLevels(writer);
		Logger rootLogger = LogManager.getRootLogger();
		writeLogger(writer, rootLogger, today, current);
		Enumeration<?> currentLoggers = LogManager.getCurrentLoggers();
		while (currentLoggers.hasMoreElements()) {
			Logger currentLogger = (Logger) currentLoggers.nextElement();
			Enumeration<?> appenders = currentLogger.getAllAppenders();
			if (appenders.hasMoreElements()) {
				writeLogger(writer, currentLogger, today, current);
			}
		}
		writer.writeEndElement(); // close logs tag
	}

	private void writeLogLevels(XMLStreamWriter writer) throws Exception {
		writer.writeStartElement("log-levels");
		writer.writeStartElement("level");
		writer.writeCharacters(Level.ALL.toString());
		writer.writeEndElement();
		writer.writeStartElement("level");
		writer.writeCharacters(Level.DEBUG.toString());
		writer.writeEndElement();
		writer.writeStartElement("level");
		writer.writeCharacters(Level.ERROR.toString());
		writer.writeEndElement();
		writer.writeStartElement("level");
		writer.writeCharacters(Level.FATAL.toString());
		writer.writeEndElement();
		writer.writeStartElement("level");
		writer.writeCharacters(Level.INFO.toString());
		writer.writeEndElement();
		writer.writeStartElement("level");
		writer.writeCharacters(Level.OFF.toString());
		writer.writeEndElement();
		writer.writeStartElement("level");
		writer.writeCharacters(Level.TRACE.toString());
		writer.writeEndElement();
		writer.writeStartElement("level");
		writer.writeCharacters(Level.WARN.toString());
		writer.writeEndElement();
		writer.writeEndElement();  // end of log-levels		
	}

	/**
	 * Write logger.
	 * 
	 * @param writer
	 *            the writer
	 * @param logger
	 *            the logger
	 * @param today
	 *            the today
	 * @param current
	 *            the current
	 * @throws XMLStreamException
	 *             the xML stream exception
	 */
	public void writeLogger(XMLStreamWriter writer, Logger logger, Date today, Date current) throws XMLStreamException {
		writer.writeStartElement("logger");
		writer.writeAttribute("name", logger.getName());
		Enumeration<?> appenders = logger.getAllAppenders();
		Level level = logger.getLevel();
		writer.writeStartElement("level");
		writer.writeCharacters(level.toString());
		writer.writeEndElement();
		while (appenders.hasMoreElements()) {
			Appender appender = (Appender) appenders.nextElement();
			String appenderName = appender.getName();
			if (distinctLoggerSet.contains(appenderName)) {
				continue;
			}
			distinctLoggerSet.add(appenderName);
			writer.writeStartElement("appender");
			writer.writeAttribute("name", appender.getName());
			if (appender instanceof FileAppender) {
				writer.writeAttribute("type", "file");
				FileAppender fileAppender = (FileAppender) appender;
				String sFile = fileAppender.getFile();
				if (current.before(today)) {
					sFile += "." + WebUtil.getXMLDateAsString(current);
				}
				writer.writeStartElement("file");
				File file = new File(sFile);
				if (file.exists() && file.isFile()) {
					long length = file.length();
					writer.writeAttribute("size", String.valueOf(length));
				}
				if (sFile != null) {
				   writer.writeCData(sFile);
				}
				writer.writeEndElement();
			}
			writer.writeEndElement(); // close appender tag
		}
		writer.writeEndElement(); // close logger tag
	}
	
	/**
	 * Gets the current log files.
	 * 
	 * @return the current log files
	 */
	protected List<String> getCurrentLogFiles() {
		distinctLoggerSet.clear();
		List<String> logFileList = new ArrayList<String>();
		Logger rootLogger = LogManager.getRootLogger();
		addLogFiles(rootLogger, logFileList);
		Enumeration<?> currentLoggers = LogManager.getCurrentLoggers();
		while (currentLoggers.hasMoreElements()) {
			Logger currentLogger = (Logger) currentLoggers.nextElement();
			Enumeration<?> appenders = currentLogger.getAllAppenders();
			if (appenders.hasMoreElements()) {
				addLogFiles(currentLogger, logFileList);
			}
		}
		return logFileList;
	}

	/**
	 * Adds the log files.
	 * 
	 * @param logger
	 *            the logger
	 * @param logFileList
	 *            the log file list
	 */
	protected void addLogFiles(Logger logger, List<String> logFileList) {
		Enumeration<?> appenders = logger.getAllAppenders();
		while (appenders.hasMoreElements()) {
			Appender appender = (Appender) appenders.nextElement();
			String appenderName = appender.getName();
			if (distinctLoggerSet.contains(appenderName)) {
				continue;
			}
			distinctLoggerSet.add(appenderName);
			if (appender instanceof FileAppender) {
				FileAppender fileAppender = (FileAppender) appender;
				String sFile = fileAppender.getFile();
				File file = new File(sFile);
				if (file.exists() && file.isFile()) {
					logFileList.add(sFile);
				}
			}
		}
	}

}