/*******************************************************************************
 * Mission Control Technologies, Copyright (c) 2009-2012, United States Government
 * as represented by the Administrator of the National Aeronautics and Space 
 * Administration. All rights reserved.
 *
 * The MCT platform is licensed under the Apache License, Version 2.0 (the 
 * "License"); you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations under 
 * the License.
 *
 * MCT includes source code licensed under additional open source licenses. See 
 * the MCT Open Source Licenses file included with this distribution or the About 
 * MCT Licenses dialog available at runtime from the MCT Help menu for additional 
 * information. 
 *******************************************************************************/
package gov.nasa.arc.mct.util.ext.commands;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * External command process builder stream helper implementation.
 *
 */
public class CmdProcessBuilderStreamHelper extends Thread {
	private static Logger logger = LoggerFactory.getLogger(CmdProcessBuilderStreamHelper.class);

	private InputStream is;
	// Routes to either I/O stream types stdout or stderr
	private String type = "";
	private InputStreamReader isr;
	private BufferedReader br;
	private StringBuffer buffer;

	/**
	 * Constructor to initialize the input stream and the type.
	 * @param is - input stream.
	 * @param type - I/O stream type.
	 */
	public CmdProcessBuilderStreamHelper(InputStream is, String type) {
		this.is = is;
		this.type = type;
	}

	/**
	 * Run it as thread safe.
	 */
	public void run() {

		try {
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			String line = null;
			buffer = new StringBuffer();
			
			while ((line = br.readLine()) != null) {
				if (line != null) {
					logger.debug("I/O stream type: " + type + " line: " + line);
					
					if (!line.isEmpty()) {
						buffer.append("[" + type + "]: ");
						buffer.append(line); 
						buffer.append("\n");
					}
						
				}
			}

		} catch (IOException ioe) {
			logger.error("IOException: ", ioe);
		} finally {
			closeAllResources();
		}
	}
	
	/**
	 * Gets the I/O stream message if any; else returns null.
	 * @return the I/O stream messages.
	 */
	public String getIOStreamMessages() {
		return buffer == null ? "" : buffer.toString();
	}
	
	/**
	 * Closes & flushes all I/O streams.
	 */
    private void closeAllResources() { 	
        close(br, "BufferedReader");
        close(isr, "InputStreamReader");
        close(is, "InputStream");
        
    }
    
    private void close(Closeable c, String exceptionType) {
        if (c != null) {
            try {
                 c.close();
            } catch (IOException ioe) {
               logger.warn("[" + exceptionType + "] IOException: ", ioe);
            }
        }
    }
}
	
