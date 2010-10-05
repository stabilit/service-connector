<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:import href="template.xsl"/>
    <xsl:variable name="body" select="/sc-web/body"/>
    <xsl:template name="sc_script">
      setInterval('infoCall()', 5000);	    
      setInterval("contentCall('services')", 10000);      
    </xsl:template>
    <xsl:template name="sc_content">
      <div class="sc_table" style="width:800px;">
        <div class="sc_table_title">
           List of services
        </div>             
        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
          <tr class="sc_table_header">
            <th class="sc_table">Service State</th>
            <th class="sc_table">Service Type</th>
            <th class="sc_table">Service Name</th>
            <th class="sc_table">Servers</th>
            <th class="sc_table">Allocated Sessions</th>
            <th class="sc_table">Available Sessions</th>
          </tr>          
          <xsl:apply-templates select="$body/services/service"/>
        </table>
      </div>
    </xsl:template>
	<xsl:template name="sc_menu_left"><xsl:call-template name="menu_separator"/><div class="sc_menu_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_menu_item" href="./services">Services</a></div></xsl:template>
	<xsl:template match="service">
	  <xsl:if test="position() mod 2 = 0">
	     <tr class="sc_table_even" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">
	        <xsl:call-template name="service_row"/>
	     </tr>	    
	  </xsl:if>
	  <xsl:if test="position() mod 2 != 0">
	     <tr class="sc_table_odd" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">	    
	        <xsl:call-template name="service_row"/>
	     </tr>	    
	  </xsl:if>
	</xsl:template>
	<xsl:template name="service_row">
	    <td class="sc_table"><xsl:value-of select="state"/></td>
	    <td class="sc_table"><xsl:value-of select="type"/></td>
	    <td class="sc_table"><xsl:value-of select="serviceName"/></td>
	    <td class="sc_table"><xsl:value-of select="countServers"/></td>
	    <td class="sc_table"><xsl:value-of select="countAllocatedSessions"/></td>
	    <td class="sc_table"><xsl:value-of select="countAvailableSessions"/></td>	
	</xsl:template>
</xsl:stylesheet>
