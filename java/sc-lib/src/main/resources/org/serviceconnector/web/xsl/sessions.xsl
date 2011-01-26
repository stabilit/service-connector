<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:import href="template.xsl"/>
    <xsl:variable name="body" select="/sc-web/body"/>
    <xsl:variable name="head" select="/sc-web/head"/>
    <xsl:variable name="serverParam" select="$head/query/param/@server"/>
    <xsl:variable name="server" select="$body/server"/>
    <xsl:template name="sc_script">
      setInterval('infoCall()', 5000);	    
      setInterval("contentCall('sessions', '')", 10000);      
      setInterval("contentCall('sessions', 'server=<xsl:value-of select="$serverParam"/>)", 10000);            
    </xsl:template>
    <xsl:template name="sc_content">
      <div class="sc_table max_width">
        <div class="sc_table_title">
           List of sessions
        </div>             
        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
          <tr class="sc_table_header">
            <th class="sc_table">Session ID</th>
            <th class="sc_table">Session Timeout (s)</th>
            <th class="sc_table">Server</th>
          </tr>
          <xsl:if test="not($body/sessions/session)">
            <tr class="sc_table_even"><td colspan="3" class="sc_table">no sessions</td></tr>
          </xsl:if>          
          <xsl:apply-templates select="$body/sessions/session"/>
          <xsl:if test="string-length($serverParam) &gt; 0 and $server">
            <tr>
               <xsl:call-template name="server_details"/>
            </tr>
           </xsl:if>
        </table>
      </div>
    </xsl:template>
	<xsl:template name="sc_menu_left"><xsl:call-template name="menu_separator"/><div class="sc_menu_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_menu_item" href="./sessions">Sessions</a></div></xsl:template>
	<xsl:template match="session">
	  <xsl:if test="position() mod 2 = 0">
	     <tr class="sc_table_even" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">
	        <xsl:if test="details">
	          <xsl:attribute name="style">height:40px;</xsl:attribute>
	        </xsl:if>
	        <xsl:call-template name="session_row">
	          <xsl:with-param name="class">sc_table_even</xsl:with-param>
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
	  <xsl:if test="position() mod 2 != 0">
	     <tr class="sc_table_odd" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">	    
	        <xsl:if test="details">
	          <xsl:attribute name="style">height:40px;</xsl:attribute>
	        </xsl:if>
	        <xsl:call-template name="session_row">
	          <xsl:with-param name="class">sc_table_odd</xsl:with-param>
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
      <xsl:if test="details">
        <tr>
          <xsl:call-template name="session_details"/>
        </tr>
      </xsl:if>
	</xsl:template>
	<xsl:template name="session_row">
	    <xsl:param name="class"/>
	    <td class="{$class}"><xsl:value-of select="id"/></td>
	    <td class="{$class}"><xsl:value-of select="sessionTimeoutSeconds"/></td>
        <td class="{$class}"><a class="sc_table" href="sessions?server={server/serverKey}"><xsl:value-of select="server/host"/>:<xsl:value-of select="server/port"/></a></td>
	</xsl:template>
	<xsl:template name="session_details">
	  <td colspan="7">
	  </td>
	</xsl:template>
	<xsl:template name="server_details">
	  <td colspan="7">
	  <div class="sc_table_details">
	    <div class="sc_table_title">
	         Server [<xsl:value-of select="$serverParam"/>]
        </div>             
        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
          <tr class="sc_table_header">          
            <th class="sc_table">Host</th>
            <th class="sc_table">Port</th>
            <th class="sc_table">Server Key</th>
            <th class="sc_table">Service Name</th>
            <th class="sc_table">Max Sessions</th>
            <th class="sc_table">Max Connections</th>
          </tr>
          <tr>
            <td class="sc_table"><xsl:value-of select="$server/host"/></td>
            <td class="sc_table"><xsl:value-of select="$server/portNr"/></td>
            <td class="sc_table"><xsl:value-of select="$server/serverKey"/></td>
            <td class="sc_table"><xsl:call-template name="fieldValue"><xsl:with-param name="value" select="$server/serviceName"/></xsl:call-template></td>
            <td class="sc_table"><xsl:call-template name="fieldValue"><xsl:with-param name="value" select="$server/maxSessions"/></xsl:call-template></td>
            <td class="sc_table"><xsl:value-of select="$server/maxConnections"/></td>                        
          </tr>          
        </table>
       </div>	  
	  </td>
	</xsl:template>	
</xsl:stylesheet>
