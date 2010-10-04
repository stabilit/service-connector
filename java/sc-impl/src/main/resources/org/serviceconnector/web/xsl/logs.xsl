<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:import href="template.xsl"/>
    <xsl:template name="sc_content">
      <div class="sc_table" style="width:800px;">
        <div class="sc_table_title">
           Log files
        </div>             
        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
          <tr class="sc_table_header">
            <th class="sc_table">Logger Name</th>
            <th class="sc_table">Appender Name</th>
            <th class="sc_table">File</th>
          </tr>          
          <xsl:apply-templates select="$body/logs/logger/appender[@type = 'file']"/>
        </table>
      </div>
    </xsl:template>
	<xsl:template name="sc_menu_left"><xsl:call-template name="menu_separator"/><div class="sc_menu_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_menu_item" href="./logs">Logs</a></div></xsl:template>
	<xsl:template match="appender">
	  <xsl:if test="position() mod 2 = 0">
	     <tr class="sc_table_even" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">
	        <xsl:call-template name="appender_row"/>
	     </tr>	    
	  </xsl:if>
	  <xsl:if test="position() mod 2 != 0">
	     <tr class="sc_table_odd" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">	    
	        <xsl:call-template name="appender_row"/>
	     </tr>	    
	  </xsl:if>
	</xsl:template>
	<xsl:template name="appender_row">
	    <td class="sc_table"><xsl:value-of select="../@name"/></td>
	    <td class="sc_table"><xsl:value-of select="@name"/></td>
	    <td class="sc_table"><a class="sc_table" href="./resource?name={file}"><xsl:value-of select="file"/></a></td>
	</xsl:template>
</xsl:stylesheet>
