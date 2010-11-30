<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:include href="constants.xsl"/>
	<xsl:variable name="head" select="/sc-web/head"/>
	<xsl:variable name="body" select="/sc-web/body"/>
	<xsl:variable name="userid" select="/sc-web/head/meta/@userid"/>
	<xsl:output
			method="html"
			doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"
			doctype-public="-//W3C//DTD XHTML 1.1//EN"/>				
	<xsl:template match="/">
		<html>
			<head>
				<title><xsl:call-template name="sc_title"/></title>
				<META HTTP-EQUIV="Pragma" CONTENT="no-cache"></META>
                <META HTTP-EQUIV="Cache-Control" CONTENT="no-store, no-cache, must-revalidate"></META>
                <META HTTP-EQUIV="Cache-Control" CONTENT="post-check=1, pre-check=2"></META>
                <META HTTP-EQUIV="Expires" CONTENT="-1"></META>				
				<link rel="stylesheet" href="sc-web.css"></link>
				<script type="text/javascript" src="ajax.js"></script>
				<script type="text/javascript" src="sc_ajax.js"></script>
				<script type="text/javascript" src="sc.js"></script>
				<script type="text/javascript"><xsl:call-template name="sc_script"/></script>
			</head>
			<body>
				<div id="sc_root" >
					<div id="sc_header">
					   <xsl:call-template name="sc_header"/> 
					</div>
					<div id="sc_menu">
				       <xsl:call-template name="sc_menu"/>
					</div>
					<xsl:if test="string-length($userid) &gt; 0">
					   <div id="sc_navigation">
				          <xsl:call-template name="sc_navigation"/> 
					   </div>
					</xsl:if>
					<div id="sc_content">
					   <xsl:if test="string-length($userid) &lt;= 0">
					     <xsl:attribute name="style">left:0px;</xsl:attribute>
					   </xsl:if>					
					   <xsl:call-template name="sc_content"/> 
					</div>
				</div>
			</body>
		</html>
	</xsl:template>
	<xsl:template name="sc_title">
	  Service Connector <xsl:value-of select="$head/meta/@scversion"/> on <xsl:value-of select="$head/meta/@hostname"/> 
	</xsl:template>
	<xsl:template name="sc_header">
	   <div id="sc_logo">
	     <xsl:call-template name="sc_title"/>
	   </div>
	  <div id="sc_meta">
	    <xsl:call-template name="sc_dateTime">
	      <xsl:with-param name="dateTime" select="$head/meta/@creation"/>
	    </xsl:call-template>
	    <br/>
	    <xsl:if test="string-length($userid) &gt; 0">
	       User [<xsl:value-of select="$userid"/>]
	    </xsl:if>
	    <br/>
	    <br/>
	    Service Connector provided by <a href="www.stabilit.ch" class="sc_header" target="stabilit">Stabilit</a>
	  </div>
	</xsl:template>
	<xsl:template name="sc_menu">
	   <div id="sc_menu_left">
	     <div class="sc_menu_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_menu_item" href="./">Home</a></div><xsl:call-template name="sc_menu_left"/>
	   </div>
	   <div id="sc_menu_right">
	     <div class="sc_menu_item" style="float:right" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_menu_item" href="?action=logout">Logout</a></div>
	   </div>
	</xsl:template>
	<xsl:template name="sc_navigation">
	<!-- 
	  <div class="sc_navigation_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_navigation_item" href="status">Status</a></div>
    -->	  
	  <div class="sc_navigation_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_navigation_item"  href="services">Services</a></div>
	  <div class="sc_navigation_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_navigation_item"  href="sessions">Sessions</a></div>
	  <div class="sc_navigation_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_navigation_item"  href="servers">Servers</a></div>
	  <div class="sc_navigation_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_navigation_item"  href="listeners">Listeners</a></div>
	  <!-- 
	  <div class="sc_navigation_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_navigation_item"  href="network">Network</a></div>
	   -->
	  <div class="sc_navigation_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_navigation_item"  href="cache">Cache</a></div>
	  <div class="sc_navigation_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_navigation_item"  href="maintenance">Maintenance</a></div>
	  <div class="sc_navigation_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_navigation_item"  href="logs">Logs</a></div>
	  <div class="sc_navigation_item" style="height:20px;"></div>
	  <div id="sc_info">
		  <xsl:call-template name="sc_info"/> 
	  </div>	 
	</xsl:template>
	<xsl:template name="sc_content">Service Connector</xsl:template>
	<xsl:template name="sc_menu_left"></xsl:template>
	<xsl:template name="sc_dateTime">
	    <xsl:param name="dateTime"/>
	    <xsl:value-of select="substring($dateTime,0,11)"/>&#160;
	    <xsl:value-of select="substring($dateTime,12,8)"/>
	</xsl:template>
	<xsl:template name="sc_info">
	  <xsl:call-template name="sc_status"/>
	  <xsl:call-template name="sc_runtime"/>
	  <xsl:call-template name="sc_statistics"/>
	</xsl:template>
	<xsl:template name="sc_script">
	   setInterval('infoCall()', 5000);	
	</xsl:template>
	<xsl:template name="sc_status">
      <div class="sc_table" style="width:160px;">
        <div class="sc_table_title">
           <div style="float:left;width:130px;">Status</div>
           <xsl:choose>
             <xsl:when test="$head/meta/@scstatus = 'success'">
               <div id="sc_status_area" style="padding-left:2px; float:right">
                 <div id="sc_status_area_success" style="visibility:visible; float:right">
                   <img border="0" width="14" height="14" src="green.png"></img>
                 </div>             
                 <div id="sc_status_area_error" style="visibility:hidden; float:right">
                    <img border="0" width="14" height="14" src="red.png"></img>             
                 </div>
               </div>
             </xsl:when>
             <xsl:otherwise>
               <div id="sc_status_area" style="padding-left:2px; float:right">
                 <div id="sc_status_area_success" style="visibility:hidden; float:right">
                   <img border="0" width="14" height="14" src="green.png"></img>
                 </div>             
                 <div id="sc_status_area_error" style="visibility:visible; float:right">
                    <img border="0" width="14" height="14" src="red.png"></img>       
                 </div>
               </div>
             </xsl:otherwise>
           </xsl:choose>
        </div>             
      </div>
	</xsl:template>
	<xsl:template name="sc_runtime">
      <div class="sc_table" style="width:160px;">
        <div class="sc_table_title">
           Runtime
        </div>             
        <table border="0" class="sc_table border_right" cellspacing="0" cellpadding="0">
          <xsl:apply-templates select="$body/system/runtime"/>        
        </table>
      </div>
	</xsl:template>
	<xsl:template match="runtime">
	  <tr class="sc_table_even">
	    <td class="sc_table_even">Processors</td>	  
	    <td class="sc_table_even"><xsl:value-of select="availableProcessors"/></td>	  
	  </tr>
	  <tr class="sc_table_odd">
	    <td class="sc_table_odd">Free Memory</td>	  
	    <td class="sc_table_odd"><xsl:value-of select="freeMemory"/></td>	  
	  </tr>
	  <tr class="sc_table_even">
	    <td class="sc_table_even">Total Memory</td>	  
	    <td class="sc_table_even"><xsl:value-of select="totalMemory"/></td>	  
	  </tr>
	  <tr class="sc_table_odd">
	    <td class="sc_table_odd">Max Memory</td>	  
	    <td class="sc_table_odd"><xsl:value-of select="maxMemory"/></td>	  
	  </tr>
	  <tr>
	    <td colspan="2" class="sc_table_even"><a class="sc_table" href="javascript:runGC()">Run GC</a></td>
	  </tr>
	</xsl:template> 
	<xsl:template name="sc_statistics">
      <div class="sc_table" style="width:160px;">
        <div class="sc_table_title">
           Statistics
        </div>             
        <table border="0" class="sc_table border_right" cellspacing="0" cellpadding="0">
          <xsl:apply-templates select="$body/system/statistics"/>        
        </table>
      </div>
	</xsl:template>
	<xsl:template match="statistics">
	  <tr class="sc_table_even">
	    <td class="sc_table_even">Startup Time</td>	  
	    <td class="sc_table_even">
	      <xsl:value-of select="substring(startupDateTime,0,11)"/><br/>
	      <xsl:value-of select="substring(startupDateTime,12,8)"/>
	    </td>
	  </tr>
	  <tr class="sc_table_odd">
	    <td class="sc_table_odd">Runtime (s)</td>	  
	    <td class="sc_table_odd"><xsl:value-of select="runtimeSinceStartupSeconds"/></td>	  
	  </tr>
	  <tr class="sc_table_even">
	    <td class="sc_table_even">Total Messages</td>	  
	    <td class="sc_table_even"><xsl:value-of select="totalMessages"/></td>	  
	  </tr>
	  <tr class="sc_table_odd">
	    <td class="sc_table_odd">Total Bytes</td>	  
	    <td class="sc_table_odd"><xsl:value-of select="totalBytes"/></td>	  
	  </tr>
	  <tr class="sc_table_even">
	    <td class="sc_table_even">Cached Messages</td>	  
	    <td class="sc_table_even"><xsl:value-of select="cachedMessages"/></td>	  
	  </tr>
	  <tr class="sc_table_odd">
	    <td class="sc_table_odd">Cached Bytes</td>	  
	    <td class="sc_table_odd"><xsl:value-of select="cachedBytes"/></td>	  
	  </tr>
	  <tr class="sc_table_even">
	    <td class="sc_table_even">Cached Files</td>	  
	    <td class="sc_table_even"><xsl:value-of select="cachedFiles"/></td>	  
	  </tr>
	</xsl:template>
	<xsl:template name="fieldValue">
	  <xsl:param name="value"/>
	  <xsl:choose>
	    <xsl:when test="string-length($value) &gt; 0">
	      <xsl:value-of select="$value"/>
	    </xsl:when>
	    <xsl:otherwise>-</xsl:otherwise>
	  </xsl:choose>	  
	</xsl:template>  
</xsl:stylesheet>
