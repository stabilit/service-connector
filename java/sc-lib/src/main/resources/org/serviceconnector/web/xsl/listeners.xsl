<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:import href="template.xsl"/>
    <xsl:variable name="body" select="/sc-web/body"/>
    <xsl:variable name="head" select="/sc-web/head"/>
    <xsl:variable name="remoteNode" select="$head/query/param/@remoteNode"/>
    <xsl:template name="sc_script">
      setInterval('infoCall()', 5000);	    
      setInterval("contentCall('<xsl:value-of select="$urlencoded"/>', 'listeners', 'remoteNode=<xsl:value-of select="$remoteNode"/>')", 10000);      
    </xsl:template>
    <xsl:template name="sc_content">
      <div class="sc_table max_width">
        <div class="sc_table_title">
           List of listeners
        </div>             
        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
          <tr class="sc_table_header">
            <th class="sc_table">Host</th>
            <th class="sc_table">Port</th>
            <th class="sc_table">Name</th>
            <th class="sc_table">Connection Type</th>
            <th class="sc_table">Interfaces</th>
            <th class="sc_table">Remote Node</th>            
          </tr>          
          <xsl:if test="not($body/responders/responder)">
            <tr class="sc_table_even"><td colspan="7" class="sc_table">no listeners</td></tr>
          </xsl:if>          
          <xsl:apply-templates select="$body/responders/responder"/>
          <xsl:if test="string-length($remoteNode) &gt; 0">
            <tr>
               <xsl:call-template name="responder_details"/>
            </tr>
           </xsl:if>
        </table>
      </div>
    </xsl:template>
	<xsl:template name="sc_menu_left"><xsl:call-template name="menu_separator"/><div class="sc_menu_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_menu_item" href="./listeners{$urlencoded}">Listeners</a></div></xsl:template>
	<xsl:template match="responder">
	  <xsl:if test="position() mod 2 = 0">
	     <tr class="sc_table_even" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">
	        <xsl:if test="details">
	          <xsl:attribute name="style">height:40px;</xsl:attribute>
	        </xsl:if>
	        <xsl:call-template name="responder_row">
	          <xsl:with-param name="class">sc_table_even</xsl:with-param>
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
	  <xsl:if test="position() mod 2 != 0">
	     <tr class="sc_table_odd" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">	    
	        <xsl:if test="details">
	          <xsl:attribute name="style">height:40px;</xsl:attribute>
	        </xsl:if>
	        <xsl:call-template name="responder_row">
	          <xsl:with-param name="class">sc_table_odd</xsl:with-param>
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
	</xsl:template>
	<xsl:template name="responder_row">
	    <xsl:param name="class"/>
	    <td class="{$class}">
	      <xsl:for-each select="responderConfig/interfaces">
	         <xsl:value-of select="string"/><br/>
	      </xsl:for-each>	    
	    </td>
	    <td class="{$class}"><xsl:value-of select="responderConfig/port"/></td>
	    <td class="{$class}"><xsl:value-of select="responderConfig/name"/></td>
	    <td class="{$class}"><xsl:value-of select="responderConfig/connectionType"/></td>
	    <td class="{$class}">
	      <xsl:for-each select="responderConfig/interfaces/string">
	        <xsl:value-of select="."/><br/>
	      </xsl:for-each>
	    </td>
	    <td class="{$class}">
	      <xsl:choose>  
	        <xsl:when test="responderConfig/remoteNodeConfiguration/name">
	          <a class="sc_table" href="listeners{$urlencoded}?remoteNode={responderConfig/remoteNodeConfiguration/name}"><xsl:call-template name="fieldValue"><xsl:with-param name="value" select="responderConfig/remoteNodeConfiguration/name"/></xsl:call-template></a>
	        </xsl:when>
	        <xsl:otherwise>
	          <xsl:call-template name="fieldValue"><xsl:with-param name="value" select="responderConfig/remoteNodeConfiguration/name"/></xsl:call-template>
	        </xsl:otherwise>
	      </xsl:choose>
	    </td>
	</xsl:template>
	<xsl:template name="responder_details">
	  <td colspan="7">
	  <div class="sc_table_details">
	    <div class="sc_table_title">
	         Remote Node Configuration [<xsl:value-of select="$remoteNode"/>]
        </div>             
        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
          <tr class="sc_table_header">          
            <th class="sc_table">Name</th>
            <th class="sc_table">Host</th>
            <th class="sc_table">Port</th>            
            <th class="sc_table">serverType</th>            
            <th class="sc_table">connectionType</th>            
            <th class="sc_table">maxPoolSize</th>            
            <th class="sc_table">keepAliveInterval (s)</th>            
          </tr>
          <tr>
            <xsl:variable name="remoteNodeConfiguration" select="$body/responders/responder/responderConfig/remoteNodeConfiguration[name = $remoteNode]"/>
            <td class="sc_table"><xsl:value-of select="$remoteNodeConfiguration/name"/></td>
            <td class="sc_table"><xsl:value-of select="$remoteNodeConfiguration/host"/></td>            
            <td class="sc_table"><xsl:value-of select="$remoteNodeConfiguration/port"/></td>            
            <td class="sc_table"><xsl:value-of select="$remoteNodeConfiguration/serverType"/></td>            
            <td class="sc_table"><xsl:value-of select="$remoteNodeConfiguration/connectionType"/></td>            
            <td class="sc_table"><xsl:value-of select="$remoteNodeConfiguration/maxPoolSize"/></td>            
            <td class="sc_table"><xsl:value-of select="$remoteNodeConfiguration/keepAliveIntervalSeconds"/></td>            
          </tr>          
        </table>
       </div>	  
	  </td>
	</xsl:template>
</xsl:stylesheet>
