<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:include href="maintenance.xsl"/>
    <xsl:variable name="action" select="$head/query/param/@action"/>
    <xsl:variable name="serviceParam" select="$head/query/param/@service"/>
    <xsl:template match="/">      
      <xsl:choose>
        <xsl:when test="$action = 'sc_dump_list'">
          <xsl:call-template name="scDumpList">
            <xsl:with-param name="title">SC Dump List</xsl:with-param>
          </xsl:call-template>
        </xsl:when>
        <xsl:when test="$action = 'sc_property_download'">
          <xsl:call-template name="downloadPropertyFile"/>
        </xsl:when>
        <xsl:when test="$action = 'sc_logs_upload'">
          <xsl:call-template name="uploadLogFiles"/>
        </xsl:when>
        <xsl:otherwise>
          Invalid action [<xsl:value-of select="$action"/>]
        </xsl:otherwise>
      </xsl:choose>
	</xsl:template>
	<xsl:template name="downloadPropertyFile">
	    <div class="sc_table">
	        <div class="sc_table_title">
	           Property File Download, File Service [<xsl:value-of select="$serviceParam"/>]
	        </div>             
	        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
	          <tr class="sc_table_header">
	            <th class="sc_table">SC Configuration Listing (Local)</th>
	            <th class="sc_table">&#160;</th>
	            <th class="sc_table">File Service Listing (Remote)</th>
	          </tr>
	          <tr>
	            <td valign="top">
	              <table border="0" cellspacing="0" cellpadding="0" style="background:white; width:100%;border-left:1px solid #666666">
	                <xsl:for-each select="$body/resource/files/file">
	                  <tr>
	                    <td style="border-bottom:1px solid #666666;width:20px;padding:4px;text-align:left;">
	                      &#160;-&#160;
	                    </td>
	                    <td style="border-bottom:1px solid #666666;padding:4px;text-align:left;">
	                      <xsl:value-of select="."/>
	                    </td>
	                  </tr>
	                </xsl:for-each>
	              </table>
	            </td>
	            <td valign="top" style="text-align:center;width:100px;">
	              <input class="sc_form_button_download" name="DownloadAndReplace" type="button" value="&lt;&lt;" onclick="javascript:downloadAndReplaceSelected('{$serviceParam}')"></input>
	            </td>
	            <td valign="top">
	              <table border="0" cellspacing="0" cellpadding="0" style="background:white; width:100%;border-right:1px solid #666666">
	                <xsl:if test="count($body/service/files/file) = 0">
	                  <tr>
	                    <td colspan="0" style="border-bottom:1px solid #666666;width:20px;padding:4px;text-align:left;">
	                      <xsl:choose>
	                        <xsl:when test="$body/service/exception">
	                          <xsl:value-of select="$body/service/exception"/>
	                        </xsl:when>
	                        <xsl:otherwise>
	                          no files found
	                        </xsl:otherwise>
	                      </xsl:choose>
	                    </td>
	                  </tr>
	                </xsl:if>
	                <xsl:for-each select="$body/service/files/file">
	                  <xsl:sort data-type="text" order="descending" select="."/>
	                  <tr>
	                    <td style="border-bottom:1px solid #666666;width:20px;padding:4px;text-align:left;">
	                      <input id="fs:{.}:fs" type="checkbox"></input>
	                    </td>
	                    <td style="border-bottom:1px solid #666666;padding:4px;text-align:left;"><xsl:value-of select="."/></td>
	                  </tr>
	                </xsl:for-each>
	              </table>
	            </td>
	          </tr>          
	        </table>
        </div>	  
	</xsl:template>
	<xsl:template name="uploadLogFiles">
	    <div class="sc_table">
	        <div class="sc_table_title">
	           Upload Current Logfiles to File Service [<xsl:value-of select="$serviceParam"/>]
	        </div>             
	        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
	          <tr class="sc_table_header">
	            <th class="sc_table">Current Log Files</th>
	            <th class="sc_table">&#160;</th>
	            <th class="sc_table">File Service Logfiles (Remote)</th>
	          </tr>
	          <tr>
	            <td valign="top">
	              <table border="0" cellspacing="0" cellpadding="0" style="background:white; width:100%;border-left:1px solid #666666">
	                <xsl:for-each select="$body/logs/logger/appender[@type = 'file']">
	                  <tr>
	                    <td style="border-bottom:1px solid #666666;width:20px;padding:4px;text-align:left;">
	                    -
	                    </td>
	                    <td style="border-bottom:1px solid #666666;padding:4px;text-align:left;"><xsl:value-of select="."/>&#160;(<xsl:value-of select="file/@size"/>)</td>
	                  </tr>
	                </xsl:for-each>
	              </table>
	            </td>
	            <td valign="top" style="text-align:center;width:100px;">
	              <input class="sc_form_button_download" name="Upload" type="button" value="&gt;&gt;" onclick="javascript:uploadLogFiles('{$serviceParam}')"></input>
	            </td>
	            <td valign="top">
	              <table border="0" cellspacing="0" cellpadding="0" style="background:white; width:100%;border-right:1px solid #666666">
	                <xsl:if test="count($body/service/files/file) = 0">
	                  <tr>
	                    <td colspan="0" style="border-bottom:1px solid #666666;width:20px;padding:4px;text-align:left;">
	                      <xsl:choose>
	                        <xsl:when test="$body/service/exception">
	                          <xsl:value-of select="$body/service/exception"/>
	                        </xsl:when>
	                        <xsl:otherwise>
	                          no files found
	                        </xsl:otherwise>
	                      </xsl:choose>
	                    </td>
	                  </tr>
	                </xsl:if>
	                <xsl:for-each select="$body/service/files/file">
	                  <xsl:sort data-type="text" order="descending" select="."/>
	                  <tr>
	                    <td style="border-bottom:1px solid #666666;width:20px;padding:4px;text-align:left;">
	                      -
	                    </td>
	                    <td style="border-bottom:1px solid #666666;padding:4px;text-align:left;"><xsl:value-of select="."/></td>
	                  </tr>
	                </xsl:for-each>
	              </table>
	            </td>
	          </tr>          
	        </table>
        </div>	  
	</xsl:template>
	<xsl:template name="scDumpList">
	    <xsl:param name="title"/>
	    <div class="sc_table">
	       <div class="sc_table_title">
	         <xsl:value-of select="$title"/> [<xsl:value-of select="$body/dumplist/path"/>]
	       </div>
           <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
             <tr class="sc_table_header">
               <th class="sc_table" style="width:20px;">Nr</th>
               <th class="sc_table">Name</th>
               <th class="sc_table">Last Modified</th>
               <th class="sc_table">Size</th>
             </tr>
             <xsl:if test="count($body/dumplist/files/file) &lt;= 0">
	           <tr class="sc_table_even"><td class="sc_table_even" colspan="3">no dump files</td></tr>               
             </xsl:if>
             <xsl:apply-templates select="$body/dumplist/files/file">
             </xsl:apply-templates>
           </table>
         </div>
	</xsl:template>
	<xsl:template match="dumplist/files/file">
	  <xsl:if test="position() mod 2 = 0">
	     <tr class="sc_table_even" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">
	        <xsl:call-template name="dump_file_row">
	          <xsl:with-param name="class">sc_table_even</xsl:with-param>
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
	  <xsl:if test="position() mod 2 != 0">
	     <tr class="sc_table_odd" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">	    
	        <xsl:call-template name="dump_file_row">
	          <xsl:with-param name="class">sc_table_odd</xsl:with-param>
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
	</xsl:template>	
	<xsl:template name="dump_file_row">
	    <xsl:param name="class"/>
	    <td class="{$class}"><xsl:value-of select="position()"/></td>	    
	    <td class="{$class}"><a class="sc_table" href="./dump{$urlencoded}?name={name}" target="{name}"><xsl:value-of select="name"/></a></td>	    
	    <td class="{$class}"><xsl:value-of select="lastModified"/></td>	    
	    <td class="{$class}"><xsl:value-of select="length"/></td>	    
	</xsl:template>	
</xsl:stylesheet>
