<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:include href="maintenance.xsl"/>
    <xsl:variable name="serviceParam" select="$head/query/param/@service"/>
    <xsl:template match="/">      
      <xsl:choose>
        <xsl:when test="$serviceParam">
          <xsl:call-template name="downloadPropertyFile"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="sc_content"/>
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
	            <th class="sc_table">File Service Listing</th>
	            <th class="sc_table">&#160;</th>
	            <th class="sc_table">SC Configuration Listing</th>
	          </tr>
	          <tr>
	            <td valign="top">
	              <table border="0" cellspacing="0" cellpadding="0" style="background:white; width:100%;border-right:1px solid #666666">
	                <xsl:for-each select="$body/service/files/file">
	                  <tr>
	                    <td style="border-bottom:1px solid #666666;width:20px;padding:4px;text-align:left;">
	                      <input id="fs:{.}:fs" type="checkbox"></input>
	                    </td>
	                    <td style="border-bottom:1px solid #666666;padding:4px;text-align:left;"><xsl:value-of select="."/></td>
	                  </tr>
	                </xsl:for-each>
	              </table>
	            </td>
	            <td valign="top" style="text-align:center;width:100px;">
	              <input class="sc_form_button" style="height:80px; margin:10px;" name="DownloadAndReplace" type="button" value="Download and Replace Selected &gt;&gt;" onclick="javascript:downloadAndReplaceSelected('{$serviceParam}')"></input>
	            </td>
	            <td valign="top">
	              <table border="0" cellspacing="0" cellpadding="0" style="background:white; width:100%;border-left:1px solid #666666">
	                <xsl:for-each select="$body/resource/files/file">
	                  <tr>
	                    <td style="border-bottom:1px solid #666666;width:20px;padding:4px;text-align:left;">
	                      <input type="checkbox"></input>
	                    </td>
	                    <td style="border-bottom:1px solid #666666;padding:4px;text-align:left;">
	                      <xsl:value-of select="."/>
	                    </td>
	                  </tr>
	                </xsl:for-each>
	              </table>
	            </td>
	          </tr>          
	        </table>
        </div>
	  
	</xsl:template>
</xsl:stylesheet>
