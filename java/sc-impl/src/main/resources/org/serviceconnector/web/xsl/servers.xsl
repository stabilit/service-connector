<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:import href="template.xsl"/>
    <xsl:variable name="body" select="/sc-web/body"/>
    <xsl:variable name="head" select="/sc-web/head"/>
    <xsl:variable name="paramHost" select="/sc-web/head/query/param/@host"/>
    <xsl:variable name="paramPort" select="/sc-web/head/query/param/@port"/>
    <xsl:template name="sc_script">
      setInterval('infoCall()', 5000);	    
      setInterval("contentCall('servers', 'host=<xsl:value-of select="$paramHost"/>&amp;port=<xsl:value-of select="$paramPort"/>')", 10000);      
    </xsl:template>
    <xsl:template name="sc_content">
      <div class="sc_table" style="width:800px;">
        <div class="sc_table_title">
           List of servers
        </div>             
        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
          <tr class="sc_table_header">
            <th class="sc_table">Host</th>
            <th class="sc_table">Port</th>
            <th class="sc_table">Service Name</th>
            <th class="sc_table">Max Sessions</th>
            <th class="sc_table">Max Connections</th>
            <th class="sc_table">Busy Connections(Requester)</th>
          </tr>          
          <xsl:if test="not($body/servers/server)">
            <tr class="sc_table_even"><td colspan="6" class="sc_table">no servers</td></tr>
          </xsl:if>          
          <xsl:apply-templates select="$body/servers/server"/>
        </table>
      </div>
    </xsl:template>
	<xsl:template name="sc_menu_left"><xsl:call-template name="menu_separator"/><div class="sc_menu_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_menu_item" href="./servers">Servers</a></div></xsl:template>
	<xsl:template match="server">
	  <xsl:if test="position() mod 2 = 0">
	     <tr class="sc_table_even" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">
	        <xsl:if test="details">
	          <xsl:attribute name="style">height:40px;</xsl:attribute>
	        </xsl:if>
	        <xsl:call-template name="server_row"/>
	     </tr>	    
	  </xsl:if>
	  <xsl:if test="position() mod 2 != 0">
	     <tr class="sc_table_odd" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">	    
	        <xsl:if test="details">
	          <xsl:attribute name="style">height:40px;</xsl:attribute>
	        </xsl:if>
	        <xsl:call-template name="server_row"/>
	     </tr>	    
	  </xsl:if>
      <xsl:if test="$paramHost and $paramPort">
        <tr>
          <xsl:call-template name="server_details"/>
        </tr>
      </xsl:if>
	</xsl:template>
	<xsl:template name="server_row">
	    <td class="sc_table"><xsl:value-of select="host"/></td>
	    <td class="sc_table"><xsl:value-of select="portNr"/></td>
	    <td class="sc_table"><xsl:value-of select="serviceName"/></td>
	    <td class="sc_table"><xsl:value-of select="maxSessions"/></td>
	    <td class="sc_table"><xsl:value-of select="maxConnections"/></td>
	    <td class="sc_table"><a class="sc_table" href="servers?host={host}&amp;port={portNr}"><xsl:value-of select="requester/context/connectionPool/busyConnections"/></a></td>	    
	</xsl:template>
	<xsl:template name="server_details">
	  <td colspan="7">
        <xsl:apply-templates select="requester/context/connectionPool"/>	    
	  </td>
	</xsl:template>
	<xsl:template match="connectionPool">
	  <div class="sc_table_details">
	    <div class="sc_table_title">
	         Connection Pool (Requester)
        </div>             
        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
          <tr class="sc_table_header">
            <th class="sc_table">keepAliveInterval</th>
            <th class="sc_table">maxConnections</th>
            <th class="sc_table">busyConnections</th>            
          </tr>
          <tr>
            <xsl:call-template name="connectionPool_row"/>
          </tr>          
        </table>
       </div>	  
	</xsl:template>
	<xsl:template name="connectionPool_row">
	    <td class="sc_table"><xsl:value-of select="keepAliveInterval"/></td>
	    <td class="sc_table"><xsl:value-of select="maxConnections"/></td>
	    <td class="sc_table"><xsl:value-of select="busyConnections"/></td>
	</xsl:template>	
</xsl:stylesheet>
