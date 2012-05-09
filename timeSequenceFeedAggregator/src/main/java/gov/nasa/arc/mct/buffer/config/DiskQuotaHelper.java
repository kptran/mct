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
package gov.nasa.arc.mct.buffer.config;

import java.io.File;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DiskQuotaHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiskQuotaHelper.class);

    private double usableSpaceAvailableInPercentage = 0;
    private double freeSpaceAvailableInPercentage = 0;
    private double totalSpaceInMB = 0;
    private double freeSpaceInMB = 0;
    private double usableSpaceInMB = 0;
    private int bufferMinDiskSpaceAvailableInMB = 10;
    private int bufferMinDiskSpaceAvailableInPercentage = 1;
    
    public String DISK_SPACE_PERCENTAGE_ERROR_MSG = "bufferMinDiskSpaceAvailableInMB = " 
        + bufferMinDiskSpaceAvailableInMB + " bufferMinDiskSpaceAvailableInPercentage= " + bufferMinDiskSpaceAvailableInPercentage + "%";
    
    public DiskQuotaHelper(Properties prop, File bufferHome) {
        bufferMinDiskSpaceAvailableInMB = Integer.parseInt(prop.getProperty("buffer.min.disk.space.megabytes"));
        bufferMinDiskSpaceAvailableInPercentage = Integer.parseInt(prop.getProperty("buffer.min.percentage.disk.space"));
        
        DISK_SPACE_PERCENTAGE_ERROR_MSG = "Disk space for MCT Buffer is &lt;= " 
            + bufferMinDiskSpaceAvailableInMB + " MB or Total free disk space available is &lt;= " + bufferMinDiskSpaceAvailableInPercentage + "%";
        printAvailableDiskSpace("bufferHome from properties", bufferHome);
    }
    
    private void printAvailableDiskSpace(String fileNameDesignation, File filePartition) { 

        // NOTE: Usable Disk Space available in JVM is always less than Free Disk Space
       LOGGER.info("*** Disk Partition '" + fileNameDesignation + "' at path:"+ filePartition.getAbsolutePath()+" ***");
       
       // Prints total disk space in bytes for volume partition specified by file abstract pathname.
       long totalSpace = filePartition.getTotalSpace(); 
       totalSpaceInMB = totalSpace /1024 /1024;
       
       // Prints an accurate estimate of the total free (and available to this JVM) bytes 
       // on the volume. This method may return the same result as 'getFreeSpace()' on some platforms.
       long usableSpace = filePartition.getUsableSpace(); 
       usableSpaceInMB = usableSpace /1024 /1024;
       
       // Prints the total free unallocated bytes for the volume in bytes.
       long freeSpace = filePartition.getFreeSpace(); 
       freeSpaceInMB = freeSpace /1024 /1024;
       
       LOGGER.info("MCT property specifying Min Disk Space Available (in MB): " + bufferMinDiskSpaceAvailableInMB );
       LOGGER.info("MCT property specifying Min Disk Space Available (in Percentage): " + bufferMinDiskSpaceAvailableInPercentage );
              
       LOGGER.info("total Space In MB: " + totalSpaceInMB + " MB");
       LOGGER.info("usable Space In MB: " + usableSpaceInMB + " MB");
       LOGGER.info("free Space In MB: " + freeSpaceInMB + " MB");
       
       if (totalSpaceInMB > 0) {
           usableSpaceAvailableInPercentage = (usableSpaceInMB / totalSpaceInMB) * 100; 
           freeSpaceAvailableInPercentage = (freeSpaceInMB / totalSpaceInMB) * 100;
           LOGGER.info("Calculated Usable Space Available (in Percentage): " + usableSpaceAvailableInPercentage + " %");
           LOGGER.info("Calculated Free Space Available (in Percentage): " + freeSpaceAvailableInPercentage + " %");  
       } else {
           LOGGER.info("filePartition.getTotalSpace() reported: " + totalSpace);
       }
     
     
       String m = String.format("The disc is full when: " +
       		"\n usableSpaceAvailableInPercentage (%.1f) <= bufferMinDiskSpaceAvailableInPercentage (%d), or \n " +
       		"usableSpaceInMB  (%.1f) <= bufferMinDiskSpaceAvailableInMB  (%d),  or \n " +
       		"freeSpaceInMB (%.1f) <= bufferMinDiskSpaceAvailableInMB  (%d) \n" +
       		"***",
       		usableSpaceAvailableInPercentage, bufferMinDiskSpaceAvailableInPercentage,
       		usableSpaceInMB, bufferMinDiskSpaceAvailableInMB,
       		freeSpaceInMB, bufferMinDiskSpaceAvailableInMB);
       
       LOGGER.info(m);
    }
    
    public String getErrorMsg() {
        return ("<HTML>" + DISK_SPACE_PERCENTAGE_ERROR_MSG 
                + "<BR>Total Disk Space (in MB): " + totalSpaceInMB
                + "<BR>JVM Usable Disk Space Available (in MB): " + usableSpaceInMB 
                + "<BR>System Free Disk Space Availble (in MB): " + freeSpaceInMB
                + "<BR>Percentage JVM Usable Disk Space Available: " + usableSpaceAvailableInPercentage 
                + "%<BR>Percentage System Free Disk Space Available: " + freeSpaceAvailableInPercentage + "%</HTML>");
    }
   
    public boolean isDiskBufferFull() {
        return (usableSpaceAvailableInPercentage <= bufferMinDiskSpaceAvailableInPercentage) ||
            (usableSpaceInMB <= bufferMinDiskSpaceAvailableInMB) || 
            (freeSpaceInMB <= bufferMinDiskSpaceAvailableInMB);
    }
}
