<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:import href="template.xsl"/>
    <xsl:variable name="body" select="/sc-web/body"/>
    <xsl:variable name="head" select="/sc-web/head"/>
    <xsl:variable name="serviceParam" select="$head/query/param/@service"/>
    <xsl:variable name="subscriptionParam" select="$head/query/param/@subscription"/>
    <xsl:variable name="showSessionsParam" select="$head/query/param/@showsessions"/>
    <xsl:variable name="services" select="$body/services"/>    
    <xsl:template name="sc_script">
      setInterval('infoCall()', 5000);	    
      setInterval("contentCall('<xsl:value-of select="$urlencoded"/>', 'services', 'service=<xsl:value-of select="$head/query/param/@service"/>&amp;subscription=<xsl:value-of select="$head/query/param/@subscription"/>&amp;showsessions=<xsl:value-of select="$head/query/param/@showsessions"/>&amp;page=<xsl:value-of select="$page"/>&amp;site=<xsl:value-of select="$site"/>&amp;sim=<xsl:value-of select="$sim"/>')", 10000);      
    </xsl:template>
    <xsl:template name="sc_content">
      <div class="sc_table max_width">
        <div class="sc_table_title">
           <xsl:call-template name="pageArea">
             <xsl:with-param name="title">List of Services</xsl:with-param>
             <xsl:with-param name="size" select="$services/@size"/>
             <xsl:with-param name="currentSite" select="$services/@site"/>
             <xsl:with-param name="currentPage" select="$services/@page"/>
             <xsl:with-param name="lastPage" select="$services/@lastPage"/>
             <xsl:with-param name="lastSite" select="$services/@lastSite"/>
             <xsl:with-param name="siteSize" select="$services/@siteSize"/>
             <xsl:with-param name="pageSize" select="$services/@pageSize"/>
           </xsl:call-template>
        </div>             
        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
          <tr class="sc_table_header">
            <th class="sc_table">Service State</th>
            <th class="sc_table">Service Name</th>
            <th class="sc_table">Service Type</th>
            <th class="sc_table">Servers</th>
            <th class="sc_table">Message Queue (Total Size / To Deliver)</th>
            <th class="sc_table">Allocated Sessions / Subscriptions</th>
            <th class="sc_table">Available Sessions</th>
          </tr>          
          <xsl:apply-templates select="$body/services/service">
            <xsl:sort data-type="text" order="ascending" select="name"/>
          </xsl:apply-templates>
        </table>
      </div>
    </xsl:template>
	<xsl:template name="sc_menu_left"><xsl:call-template name="menu_separator"/><div class="sc_menu_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_menu_item" href="./services{$urlencoded}">Services</a></div></xsl:template>
	<xsl:template match="service">
	  <xsl:if test="position() mod 2 = 0">
	     <tr class="sc_table_even" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">
	        <xsl:if test="details">
	          <xsl:attribute name="style">height:40px;</xsl:attribute>
	        </xsl:if>
	        <xsl:call-template name="service_row">
	          <xsl:with-param name="class">sc_table_even</xsl:with-param>
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
	  <xsl:if test="position() mod 2 != 0">
	     <tr class="sc_table_odd" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">	    
	        <xsl:if test="details">
	          <xsl:attribute name="style">height:40px;</xsl:attribute>
	        </xsl:if>
	        <xsl:call-template name="service_row">
	          <xsl:with-param name="class">sc_table_odd</xsl:with-param>
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
	  <xsl:choose>
	    <xsl:when test="$showSessionsParam = 'yes' and details">
          <tr>
            <xsl:call-template name="subscription_details"/>
          </tr>	    
	    </xsl:when>
	    <xsl:when test="details">
          <tr>
            <xsl:call-template name="service_details"/>
          </tr>
	    </xsl:when>
	  </xsl:choose>
	</xsl:template>
	<xsl:template name="service_row">
	    <xsl:param name="class"/>
	    <td class="{$class}" id="service_state">
	      <xsl:choose>
	        <xsl:when test="enabled = 'true'"><a class="sc_table"  href="javascript:disableService('{$urlencoded}', '{name}');">Enabled</a></xsl:when>
	        <xsl:otherwise><a class="sc_table" href="javascript:enableService('{$urlencoded}', '{name}')">Disabled</a></xsl:otherwise>
	      </xsl:choose>
	    </td>
	    <xsl:choose>
	      <xsl:when test="countServers &gt; 0">
	         <td class="{$class}"><a class="sc_table" href="services{$urlencoded}?service={name}"><xsl:value-of select="name"/></a>&#160;</td>
          </xsl:when>
          <xsl:otherwise>	       
	         <td class="{$class}"><xsl:value-of select="name"/>&#160;</td>
	      </xsl:otherwise>
        </xsl:choose>       
	    <td class="{$class}"><xsl:value-of select="type"/></td>	    
	    <td class="{$class}"><xsl:call-template name="fieldValue"><xsl:with-param name="value" select="countServers"/></xsl:call-template></td>
	    <xsl:choose>
	       <xsl:when test="publishMessageQueueSize &gt; 0">
	         <td class="{$class}"><a class="sc_table" href="services{$urlencoded}?service={name}&amp;subscription=yes"><xsl:value-of select="publishMessageQueueSize"/></a>/<xsl:value-of select="publishMessageQueueReferencedNodeCount"/></td>
	      </xsl:when>
	      <xsl:otherwise>
	         <td class="{$class}"><xsl:call-template name="fieldValue"><xsl:with-param name="value" select="publishMessageQueueSize"/></xsl:call-template>/<xsl:call-template name="fieldValue"><xsl:with-param name="value" select="publishMessageQueueReferencedNodeCount"/></xsl:call-template></td>
	      </xsl:otherwise>
	    </xsl:choose>
	    <xsl:choose>
	       <xsl:when test="countAllocatedSessions &gt; 0 and type = 'PUBLISH_SERVICE'">
	         <td class="{$class}"><a class="sc_table" href="services{$urlencoded}?service={name}&amp;showsessions=yes"><xsl:value-of select="countAllocatedSessions"/></a></td>
	      </xsl:when>
	      <xsl:otherwise>
	         <td class="{$class}"><xsl:call-template name="fieldValue"><xsl:with-param name="value" select="countAllocatedSessions"/></xsl:call-template></td>
	      </xsl:otherwise>
	    </xsl:choose>
        <td class="{$class}">
          <xsl:call-template name="fieldValue"><xsl:with-param name="value" select="countAvailableSessions"/></xsl:call-template>
        </td>
	</xsl:template>
	<xsl:template name="service_details">
	  <td colspan="7">
	    <xsl:choose>
	      <xsl:when test="$head/query/param/@subscription = 'yes'">
	        <xsl:apply-templates select="details/publishMessageQueue"/>	    
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
	           List of Servers [<xsl:value-of select="$serviceParam"/>]
	        </div>             
	        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
	          <tr class="sc_table_header">
	            <th class="sc_table">Host:Port</th>
	            <th class="sc_table">
	            	<xsl:if test="server/sessions/session">Session Count</xsl:if>
	            	<xsl:if test="server/sessions/subscription">Subscription Count</xsl:if>
	            </th>
	            <th class="sc_table">
	            	<xsl:if test="server/sessions/session">Max Sessions</xsl:if>
	            	<xsl:if test="server/sessions/subscription">Max Subscriptions</xsl:if>
	           </th>         
	            <th class="sc_table">Max Connections</th>            
	            <th class="sc_table">
	               <xsl:if test="server/sessions/session">Sessions</xsl:if>
	               <xsl:if test="server/sessions/subscription">Subscriptions</xsl:if>
	            </th>            
	          </tr>          
	          <xsl:apply-templates select="server"/>
	        </table>
        </div>
	</xsl:template>
	<xsl:template match="server">
	  <xsl:if test="position() mod 2 = 0">
	     <tr class="sc_table_even" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">
	        <xsl:call-template name="server_row">
	          <xsl:with-param name="class">sc_table_even</xsl:with-param>
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
	  <xsl:if test="position() mod 2 != 0">
	     <tr class="sc_table_odd" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">	    
	        <xsl:call-template name="server_row">
	          <xsl:with-param name="class">sc_table_odd</xsl:with-param>
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
	</xsl:template>
	<xsl:template name="server_row">
	    <xsl:param name="class"/>
	    <td class="{$class}"><xsl:value-of select="host"/>:<xsl:value-of select="portNr"/>&#160;</td>
	    <td class="{$class}"><xsl:value-of select="sessionCount"/>&#160;</td>
	    <td class="{$class}"><xsl:value-of select="maxSessions"/>&#160;</td>
	    <td class="{$class}"><xsl:value-of select="maxConnections"/>&#160;</td>
	    <xsl:choose>
	       <xsl:when test="sessions/session">
	          <td class="{$class}">
	            <table class="sc_table">
	              <xsl:apply-templates select="sessions/session"/>
	            </table>
	          </td>	         
	       </xsl:when>
	       <xsl:when test="sessions/subscription">
	          <td class="{$class}">
	            <table class="sc_table">
	              <xsl:apply-templates select="sessions/subscription" mode="server"/>
	            </table>
	          </td>
	       </xsl:when>	         
	       <xsl:otherwise>
	          <td class="{$class}">-</td>	         
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
	<xsl:template match="subscription" mode="server">
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
	<xsl:template match="publishMessageQueue">
	    <div class="sc_table_details">
	        <div class="sc_table_title">
	           Message Queue [<xsl:value-of select="$serviceParam"/>]
	        </div>             
	        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
	          <tr class="sc_table_header">
	            <th class="sc_table">msk</th>
	            <th class="sc_table">msn</th>
	            <th class="sc_table">references</th>
	          </tr>          
	          <xsl:apply-templates select="scmpMessage"/>
	        </table>
        </div>
	</xsl:template>
	<xsl:template match="scmpMessage">
	  <xsl:if test="position() mod 2 = 0">
	     <tr class="sc_table_even" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">
	        <xsl:call-template name="message_row">
	          <xsl:with-param name="class">sc_table_even</xsl:with-param>
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
	  <xsl:if test="position() mod 2 != 0">
	     <tr class="sc_table_odd" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">	    
	        <xsl:call-template name="message_row">
	          <xsl:with-param name="class">sc_table_odd</xsl:with-param>	        
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
	</xsl:template>
	<xsl:template name="message_row">
	    <xsl:param name="class"/>
	    <td class="{$class}"><xsl:value-of select="header/msk"/></td>
	    <td class="{$class}"><xsl:value-of select="header/msn"/></td>
	    <td class="{$class}"><xsl:value-of select="references"/></td>	    
	</xsl:template>
	<xsl:template name="subscription_details">
	  <td colspan="7">
	    <div class="sc_table_details">
	        <div class="sc_table_title">
	           Subscribed Clients  [<xsl:value-of select="$serviceParam"/>]
	        </div>             
	        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
	          <tr class="sc_table_header">
	            <th class="sc_table">Subscription ID</th>
	            <th class="sc_table">Subscription Mask</th>
	            <th class="sc_table">IP Addresslist</th>
	            <th class="sc_table">Subscription Timeout (ms)</th>
	            <th class="sc_table">No Data Interval (s)</th>
	            <th class="sc_table">Server</th>
	          </tr> 
	          <xsl:if test="not(details/servers/server/sessions/subscription)">
                <tr class="sc_table_even"><td colspan="5" class="sc_table">no subscriptions</td></tr>
              </xsl:if>          
              <xsl:apply-templates select="details/servers/server/sessions/subscription"/>	                   
	        </table>
        </div>
       </td>
	</xsl:template>
	<xsl:template match="subscription">
      <xsl:if test="position() mod 2 = 0">
	     <tr class="sc_table_even" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">
	        <xsl:call-template name="subscription_row"/>
	     </tr>	    
	  </xsl:if>
	  <xsl:if test="position() mod 2 != 0">
	     <tr class="sc_table_odd" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">	    
	        <xsl:call-template name="subscription_row"/>
	     </tr>	    
	  </xsl:if>
	</xsl:template>
	<xsl:template name="subscription_row">
	    <td class="sc_table"><xsl:value-of select="id"/></td>
	    <td class="sc_table"><xsl:value-of select="subscriptionMask"/></td>
	    <td class="sc_table"><xsl:value-of select="ipAddressList"/></td>
	    <td class="sc_table"><xsl:value-of select="subscriptionTimeoutMillis"/></td>
	    <td class="sc_table"><xsl:value-of select="noDataIntervalMillis"/></td>
        <td class="sc_table"><xsl:value-of select="server/host"/>:<xsl:value-of select="server/port"/></td>
	</xsl:template>
</xsl:stylesheet>
