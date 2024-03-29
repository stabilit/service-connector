<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:import href="template.xsl"/>
    <xsl:variable name="body" select="/sc-web/body"/>
    <xsl:variable name="head" select="/sc-web/head"/>
    <xsl:variable name="paramHost" select="/sc-web/head/query/param/@host"/>
    <xsl:variable name="paramPort" select="/sc-web/head/query/param/@port"/>
    <xsl:variable name="servers" select="$body/servers"/>    
    <xsl:template name="sc_script">
      setInterval('infoCall()', 5000);	    
      setInterval("contentCall('<xsl:value-of select="$urlencoded"/>', 'servers', 'host=<xsl:value-of select="$paramHost"/>&amp;port=<xsl:value-of select="$paramPort"/>&amp;page=<xsl:value-of select="$page"/>&amp;site=<xsl:value-of select="$site"/>&amp;sim=<xsl:value-of select="$sim"/>')", 10000);      
    </xsl:template>
    <xsl:template name="sc_content">
      <div class="sc_table max_width">
        <div class="sc_table_title">
           <xsl:call-template name="pageArea">
             <xsl:with-param name="title">List of Servers</xsl:with-param>
             <xsl:with-param name="size" select="$servers/@size"/>
             <xsl:with-param name="currentSite" select="$servers/@site"/>
             <xsl:with-param name="currentPage" select="$servers/@page"/>
             <xsl:with-param name="lastPage" select="$servers/@lastPage"/>
             <xsl:with-param name="lastSite" select="$servers/@lastSite"/>
             <xsl:with-param name="siteSize" select="$servers/@siteSize"/>
             <xsl:with-param name="pageSize" select="$servers/@pageSize"/>
           </xsl:call-template>        
        </div>             
        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
          <tr class="sc_table_header">
            <th class="sc_table">Host:Port</th>
            <th class="sc_table">Server Key</th>
            <th class="sc_table">Server Type</th>
            <th class="sc_table">Connection Type</th>
            <th class="sc_table">Session Count</th>
            <th class="sc_table">Max Sessions</th>
            <th class="sc_table">Max Connections</th>
          </tr>          
          <xsl:if test="not($body/servers/server)">
            <tr class="sc_table_even"><td colspan="6" class="sc_table">no servers</td></tr>
          </xsl:if>          
          <xsl:apply-templates select="$body/servers/server"/>
        </table>
      </div>
    </xsl:template>
	<xsl:template name="sc_menu_left"><xsl:call-template name="menu_separator"/><div class="sc_menu_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_menu_item" href="./servers{$urlencoded}">Servers</a></div></xsl:template>
	<xsl:template match="server">
	  <xsl:if test="position() mod 2 = 0">
	     <tr class="sc_table_even" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">
	        <xsl:if test="details">
	          <xsl:attribute name="style">height:40px;</xsl:attribute>
	        </xsl:if>
	        <xsl:call-template name="server_row">
	          <xsl:with-param name="class">sc_table_even</xsl:with-param>
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
	  <xsl:if test="position() mod 2 != 0">
	     <tr class="sc_table_odd" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">	    
	        <xsl:if test="details">
	          <xsl:attribute name="style">height:40px;</xsl:attribute>
	        </xsl:if>
	        <xsl:call-template name="server_row">
	          <xsl:with-param name="class">sc_table_odd</xsl:with-param>
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
      <xsl:if test="$paramHost = host and $paramPort = portNr">
        <tr>
          <xsl:call-template name="server_details"/>
        </tr>
      </xsl:if>
	</xsl:template>
	<xsl:template name="server_row">
	    <xsl:param name="class"/> 
	    <td class="{$class}"><xsl:value-of select="host"/>:<xsl:value-of select="portNr"/></td>
	    <td class="{$class}"><xsl:value-of select="serverKey"/></td>
	    <td class="{$class}"><xsl:value-of select="type"/></td>
	    <td class="{$class}"><xsl:value-of select="connectionType"/></td>
	    <td class="{$class}">
	      <xsl:choose>
	        <xsl:when test="contains(type,'STATEFUL_SERVER')">
	        	<xsl:call-template name="fieldValue"><xsl:with-param name="value" select="sessionCount"/></xsl:call-template>
	        </xsl:when>
	        <xsl:otherwise>-</xsl:otherwise>
	      </xsl:choose>
	    </td>
	    <td class="{$class}">
	      <xsl:choose>
	        <xsl:when test="contains(type,'CASCADED')">-</xsl:when>
	        <xsl:otherwise>
	          <xsl:call-template name="fieldValue"><xsl:with-param name="value" select="maxSessions"/></xsl:call-template>
	        </xsl:otherwise>
	      </xsl:choose>
	    </td>
	    <td class="{$class}"><xsl:value-of select="maxConnections"/></td>
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
            <th class="sc_table">minConnections</th>
            <th class="sc_table">maxConnections</th>
            <th class="sc_table">busyConnections</th>            
            <th class="sc_table">usedConnections</th>            
            <th class="sc_table">freeConnections</th>            
          </tr>
          <tr>
            <xsl:call-template name="connectionPool_row"/>
          </tr>          
        </table>
       </div>	  
	</xsl:template>
	<xsl:template name="connectionPool_row">
	    <td class="sc_table"><xsl:value-of select="keepAliveInterval"/></td>
	    <td class="sc_table"><xsl:value-of select="minConnections"/></td>
	    <td class="sc_table"><xsl:value-of select="maxConnections"/></td>
	    <td class="sc_table"><xsl:value-of select="busyConnections"/></td>
	    <td class="sc_table"><xsl:call-template name="usedConnections"/></td>
	    <td class="sc_table"><xsl:call-template name="freeConnections"/></td>
	</xsl:template>
	<xsl:template name="usedConnections">	
	  <xsl:choose>
	  <xsl:when test="count(usedConnections/*) = 0">-</xsl:when>
	  <xsl:otherwise>
	  <table class="sc_table">
	  <xsl:for-each select="usedConnections/*">
	    <xsl:if test="position() mod 2 = 0">
	      <tr class="sc_sub_table_even" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">
	        <td class="sc_sub_table_even"><img width="20" height="20" src="rightarrow.png"/></td>
	        <td class="sc_sub_table_even"><xsl:value-of select="local-name()"/></td>
	      </tr>	    
	    </xsl:if>
	    <xsl:if test="position() mod 2 != 0">
	       <tr class="sc_sub_table_odd" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">	    
	        <td class="sc_sub_table_odd"><img width="20" height="20" src="rightarrow.png"/></td>
	        <td class="sc_sub_table_odd"><xsl:value-of select="local-name()"/></td>
	       </tr>	    
	    </xsl:if>
	  </xsl:for-each>
	  </table>
	  </xsl:otherwise>
	  </xsl:choose>
	</xsl:template>
	<xsl:template name="freeConnections">
	  <xsl:if test="count(freeConnections/*) = 0">-</xsl:if>
	  <table class="sc_table">
	  <xsl:for-each select="freeConnections/*">
	    <xsl:if test="position() mod 2 = 0">
	      <tr class="sc_sub_table_even" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">
	        <td class="sc_sub_table_even"><img width="20" height="20" src="rightarrow.png"/></td>
	        <td class="sc_sub_table_even"><xsl:value-of select="local-name()"/></td>
	      </tr>	    
	    </xsl:if>
	    <xsl:if test="position() mod 2 != 0">
	       <tr class="sc_sub_table_odd" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">	    
	        <td class="sc_sub_table_odd"><img width="20" height="20" src="rightarrow.png"/></td>
	        <td class="sc_sub_table_odd"><xsl:value-of select="local-name()"/></td>
	       </tr>	    
	    </xsl:if>
	  </xsl:for-each>
	  </table>
	</xsl:template>
		
</xsl:stylesheet>
