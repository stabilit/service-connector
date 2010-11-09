<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:import href="template.xsl"/>
    <xsl:variable name="body" select="/sc-web/body"/>
    <xsl:variable name="head" select="/sc-web/head"/>
    <xsl:template name="sc_script">
      setInterval('infoCall()', 5000);	    
      setInterval("contentCall('services', 'service=<xsl:value-of select="$head/query/param/@service"/>&amp;subscription=<xsl:value-of select="$head/query/param/@subscription"/>')", 10000);      
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
            <th class="sc_table">Subscriptions</th>
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
	        <xsl:if test="details">
	          <xsl:attribute name="style">height:40px;</xsl:attribute>
	        </xsl:if>
	        <xsl:call-template name="service_row"/>
	     </tr>	    
	  </xsl:if>
	  <xsl:if test="position() mod 2 != 0">
	     <tr class="sc_table_odd" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">	    
	        <xsl:if test="details">
	          <xsl:attribute name="style">height:40px;</xsl:attribute>
	        </xsl:if>
	        <xsl:call-template name="service_row"/>
	     </tr>	    
	  </xsl:if>
      <xsl:if test="details">
        <tr>
          <xsl:call-template name="service_details"/>
        </tr>
      </xsl:if>
	</xsl:template>
	<xsl:template name="service_row">
	    <td class="sc_table"><xsl:value-of select="state"/></td>
	    <td class="sc_table"><xsl:value-of select="type"/></td>
	    <xsl:choose>
	      <xsl:when test="countServers &gt; 0">
	         <td class="sc_table"><a class="sc_table" href="services?service={serviceName}"><xsl:value-of select="serviceName"/></a></td>
          </xsl:when>
          <xsl:otherwise>	       
	         <td class="sc_table"><xsl:value-of select="serviceName"/></td>
	      </xsl:otherwise>
        </xsl:choose>       
	    <td class="sc_table"><xsl:value-of select="countServers"/></td>
	    <xsl:choose>
	       <xsl:when test="subscriptionQueueSize &gt; 0">
	         <td class="sc_table"><a class="sc_table" href="services?service={serviceName}&amp;subscription=yes"><xsl:value-of select="subscriptionQueueSize"/></a></td>
	      </xsl:when>
	      <xsl:otherwise>
	         <td class="sc_table"><xsl:value-of select="subscriptionQueueSize"/></td>
	      </xsl:otherwise>
	    </xsl:choose>
	    <td class="sc_table"><xsl:value-of select="countAllocatedSessions"/></td>
	    <td class="sc_table"><xsl:value-of select="countAvailableSessions"/></td>	
	</xsl:template>
	<xsl:template name="service_details">
	  <td colspan="7">
	    <xsl:choose>
	      <xsl:when test="$head/query/param/@subscription = 'yes'">
	        <xsl:apply-templates select="details/subscriptionQueue"/>	    
	      </xsl:when>
	      <xsl:otherwise>
	        <xsl:apply-templates select="details/servers"/>	    
	      </xsl:otherwise>
	    </xsl:choose>
	  </td>
	</xsl:template>
	<xsl:template match="servers">
	    <div class="sc_table_details">
	        <div class="sc_table_title">
	           List of servers
	        </div>             
	        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
	          <tr class="sc_table_header">
	            <th class="sc_table">Host</th>
	            <th class="sc_table">Port</th>
	            <th class="sc_table">Max Connections</th>            
	            <th class="sc_table">Sessions</th>            
	          </tr>          
	          <xsl:apply-templates select="server"/>
	        </table>
        </div>
	</xsl:template>
	<xsl:template match="server">
	  <xsl:if test="position() mod 2 = 0">
	     <tr class="sc_table_even" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">
	        <xsl:call-template name="server_row"/>
	     </tr>	    
	  </xsl:if>
	  <xsl:if test="position() mod 2 != 0">
	     <tr class="sc_table_odd" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">	    
	        <xsl:call-template name="server_row"/>
	     </tr>	    
	  </xsl:if>
	</xsl:template>
	<xsl:template name="server_row">
	    <td class="sc_table"><xsl:value-of select="host"/></td>
	    <td class="sc_table"><xsl:value-of select="portNr"/></td>
	    <td class="sc_table"><xsl:value-of select="maxConnections"/></td>
	    <xsl:choose>
	       <xsl:when test="sessions/session">
	          <td class="sc_table">
	            <table class="sc_table">
	              <xsl:apply-templates select="sessions/session"/>
	            </table>
	          </td>	         
	       </xsl:when>
	       <xsl:otherwise>
	          <td class="sc_table">-</td>	         
	       </xsl:otherwise>
	    </xsl:choose>
	</xsl:template>
	<xsl:template match="session">
	  <xsl:if test="position() mod 2 = 0">
	    <tr class="sc_sub_table_even" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">
	      <td class="sc_table"><img width="20" height="20" src="rightarrow.png"/></td>
	      <td class="sc_table"><xsl:value-of select="id"/></td>
	    </tr>	    
	  </xsl:if>
	  <xsl:if test="position() mod 2 != 0">
	     <tr class="sc_sub_table_odd" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">	    
	      <td class="sc_table"><img width="20" height="20" src="rightarrow.png"/></td>
	      <td class="sc_table"><xsl:value-of select="id"/></td>
	     </tr>	    
	  </xsl:if>
	</xsl:template>
	<xsl:template match="subscriptionQueue">
	    <div class="sc_table_details">
	        <div class="sc_table_title">
	           Subscription Queue
	        </div>             
	        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
	          <tr class="sc_table_header">
	            <th class="sc_table">oti</th>
	            <th class="sc_table">msk</th>
	            <th class="sc_table">bty</th>
	            <th class="sc_table">mid</th>
	            <th class="sc_table">mty</th>
	          </tr>          
	          <xsl:apply-templates select="scmpMessage"/>
	        </table>
        </div>
	</xsl:template>
	<xsl:template match="scmpMessage">
	  <xsl:if test="position() mod 2 = 0">
	     <tr class="sc_table_even" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">
	        <xsl:call-template name="message_row"/>
	     </tr>	    
	  </xsl:if>
	  <xsl:if test="position() mod 2 != 0">
	     <tr class="sc_table_odd" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">	    
	        <xsl:call-template name="message_row"/>
	     </tr>	    
	  </xsl:if>
	</xsl:template>
	<xsl:template name="message_row">
	    <td class="sc_table"><xsl:value-of select="header/oti"/></td>
	    <td class="sc_table"><xsl:value-of select="header/msk"/></td>
	    <td class="sc_table"><xsl:value-of select="header/bty"/></td>
	    <td class="sc_table"><xsl:value-of select="header/mid"/></td>
	    <td class="sc_table"><xsl:value-of select="header/mty"/></td>
	</xsl:template>
</xsl:stylesheet>
