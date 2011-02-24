<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:import href="template.xsl"/>
    <xsl:variable name="body" select="/sc-web/body"/>
    <xsl:variable name="head" select="/sc-web/head"/>
    <xsl:variable name="serverParam" select="$head/query/param/@server"/>
    <xsl:variable name="serviceParam" select="$head/query/param/@service"/>
    <xsl:variable name="server" select="$body/server"/>
    <xsl:variable name="service" select="$body/service"/>
    <xsl:template name="sc_script">
      setInterval('infoCall()', 5000);	    
      setInterval("contentCall('<xsl:value-of select="$urlencoded"/>', 'subscriptions', 'server=<xsl:value-of select="$serverParam"/>&amp;service=<xsl:value-of select="$serviceParam"/>')", 10000);            
    </xsl:template>
    <xsl:template name="sc_content">
      <div class="sc_table max_width">
        <div class="sc_table_title">
           List of subscriptions
        </div>             
        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
          <tr class="sc_table_header">
            <th class="sc_table">Service</th>
            <th class="sc_table">Subscription ID</th>
            <th class="sc_table">Subscription Mask</th>
            <th class="sc_table">IP Addresslist</th>
            <th class="sc_table">Subscription Timeout (ms)</th>
            <th class="sc_table">No Data Interval (s)</th>
            <th class="sc_table">Server</th>
          </tr>
          <xsl:if test="not($body/subscriptions/subscription)">
            <tr class="sc_table_even"><td colspan="5" class="sc_table">no subscriptions</td></tr>
          </xsl:if>          
          <xsl:apply-templates select="$body/subscriptions/subscription"/>
          <xsl:if test="string-length($serverParam) &gt; 0 and $server">
            <tr>
               <xsl:call-template name="server_details"/>
            </tr>
           </xsl:if>
          <xsl:if test="string-length($serviceParam) &gt; 0 and $service">
            <tr>
               <xsl:call-template name="service_details"/>
            </tr>
           </xsl:if>
        </table>
      </div>
    </xsl:template>
	<xsl:template name="sc_menu_left"><xsl:call-template name="menu_separator"/><div class="sc_menu_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_menu_item" href="./subscriptions{$urlencoded}">Subscriptions</a></div></xsl:template>
	<xsl:template match="subscription">
	  <xsl:if test="position() mod 2 = 0">
	     <tr class="sc_table_even" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">
	        <xsl:if test="details">
	          <xsl:attribute name="style">height:40px;</xsl:attribute>
	        </xsl:if>
	        <xsl:call-template name="subscription_row">
	          <xsl:with-param name="class">sc_table_even</xsl:with-param>
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
	  <xsl:if test="position() mod 2 != 0">
	     <tr class="sc_table_odd" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">	    
	        <xsl:if test="details">
	          <xsl:attribute name="style">height:40px;</xsl:attribute>
	        </xsl:if>
	        <xsl:call-template name="subscription_row">
	          <xsl:with-param name="class">sc_table_odd</xsl:with-param>
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
      <xsl:if test="details">
        <tr>
          <xsl:call-template name="subscription_details"/>
        </tr>
      </xsl:if>
	</xsl:template>
	<xsl:template name="subscription_row">
	    <xsl:param name="class"/>
        <td class="{$class}"><a class="sc_table" href="subscriptions{$urlencoded}?service={server/serviceName}"><xsl:value-of select="server/serviceName"/></a></td>
	    <td class="{$class}"><xsl:value-of select="id"/></td>
	    <td class="{$class}"><xsl:value-of select="subscriptionMask"/></td>
	    <td class="{$class}"><xsl:value-of select="ipAddressList"/></td>
	    <td class="{$class}"><xsl:value-of select="subscriptionTimeoutMillis"/></td>
	    <td class="{$class}"><xsl:value-of select="noDataInterval"/></td>
        <td class="{$class}"><a class="sc_table" href="subscriptions{$urlencoded}?server={server/serverKey}"><xsl:value-of select="server/host"/>:<xsl:value-of select="server/port"/></a></td>
	</xsl:template>
	<xsl:template name="subscription_details">
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
	<xsl:template name="service_details">
	  <td colspan="7">
	  <div class="sc_table_details">
	    <div class="sc_table_title">
	         Service [<xsl:value-of select="$serviceParam"/>]
        </div>             
        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
          <tr class="sc_table_header">          
            <th class="sc_table">Service State</th>
            <th class="sc_table">Service Name</th>
            <th class="sc_table">Service Type</th>
            <th class="sc_table">Servers</th>
            <th class="sc_table">Message Queue</th>
            <th class="sc_table">Allocated Sessions / Subscriptions</th>
            <th class="sc_table">Available Sessions</th>
          </tr>
          <tr>
	    <td class="sc_table">
	      <xsl:choose>
	        <xsl:when test="$service/enabled = 'true'">Enabled</xsl:when>
	        <xsl:otherwise>Disabled</xsl:otherwise>
	      </xsl:choose>
	    </td>
	    <td class="sc_table"><xsl:value-of select="$service/name"/></td>	    
	    <td class="sc_table"><xsl:value-of select="$service/type"/></td>	    
	    <td class="sc_table"><xsl:value-of select="$service/countServers"/></td>	    
	    <xsl:choose>
	       <xsl:when test="$service/publishMessageQueueSize &gt; 0">
	         <td class="sc_table"><a class="sc_table" href="services{$urlencoded}?service={$serviceParam}&amp;subscription=yes"><xsl:value-of select="$service/publishMessageQueueSize"/></a>&#160;</td>
	      </xsl:when>
	      <xsl:otherwise>
	         <td class="sc_table"><xsl:call-template name="fieldValue"><xsl:with-param name="value" select="$service/publishMessageQueueSize"/></xsl:call-template></td>
	      </xsl:otherwise>
	    </xsl:choose>
	    <xsl:choose>
	       <xsl:when test="$service/countAllocatedSessions &gt; 0 and $service/type = 'PUBLISH_SERVICE'">
	         <td class="sc_table"><a class="sc_table" href="services{$urlencoded}?service={$serviceParam}&amp;showsessions=yes"><xsl:value-of select="$service/countAllocatedSessions"/></a></td>
	      </xsl:when>
	      <xsl:otherwise>
	         <td class="sc_table"><xsl:call-template name="fieldValue"><xsl:with-param name="value" select="$service/countAllocatedSessions"/></xsl:call-template></td>
	      </xsl:otherwise>
	    </xsl:choose>
        <td class="sc_table">
          <xsl:choose>
            <xsl:when test="type = 'PUBLISH_SERVICE'">-</xsl:when>
            <xsl:otherwise><xsl:call-template name="fieldValue"><xsl:with-param name="value" select="$service/countAvailableSessions"/></xsl:call-template></xsl:otherwise>
          </xsl:choose>          
        </td>
          </tr>          
        </table>
       </div>	  
	  </td>
	</xsl:template>		
</xsl:stylesheet>
