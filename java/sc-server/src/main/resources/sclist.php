<?php
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
/*
#   instructions for use  								
#	- Call the script http://host:port/path/sclist.php
# ------------------------------------------------------------------------------
*/
if ($handle = opendir('.')) {
	$i = 0;
    while (false !== ($file = readdir($handle))) {
        if ($file != "." && $file != "..") {
        	// ignore php exentsions
        	$pos = strrpos($file,".php");
        	$len = strlen($file);
        	if ($pos == $len - 4) {
        		continue;
        	}
        	if ( $i++ > 0) {
        		echo "|";
        	}
            echo "$file";
        }
    }
    closedir($handle);
}
?>
