<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:include href="constants.xsl"/>
	<xsl:variable name="head" select="/sc-web/head"/>
	<xsl:variable name="body" select="/sc-web/body"/>
	<xsl:variable name="userid" select="/sc-web/head/meta/@userid"/>
	<xsl:variable name="urlencoded" select="/sc-web/head/meta/@urlencoded"/>
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
				<link rel="stylesheet" href="sc-web-{$head/meta/@colorscheme}.css"></link>
				<script type="text/javascript" src="ajax.js"></script>
				<script type="text/javascript" src="sc_ajax.js"></script>
				<script type="text/javascript" src="sc.js"></script>
				<script type="text/javascript"><xsl:call-template name="sc_script"/></script>
				<script type="text/javascript">
				  var callPending = false;
				  window.document.onclick=function() {
				     if (callPending == false) {
				        hideLayer('DialogBox');
				     }
				  }
				</script>
			</head>
			<body>
                <div id="overlay">overlay</div>
			    <div id="DialogBox"></div>
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
	  <xsl:value-of select="$head/meta/@headerprefix"/> SC <xsl:value-of select="$head/meta/@scversion"/> on <xsl:value-of select="$head/meta/@hostname"/> 
	</xsl:template>
	<xsl:template name="sc_header">
	   <div id="sc_logo">
	     <xsl:call-template name="sc_title"/>
	   </div>
	  <div id="sc_meta">
	    <xsl:call-template name="sc_dateTime">
	      <xsl:with-param name="dateTime" select="$head/meta/@creation"/>
	    </xsl:call-template>
	    <!-- 
	    <br/>
	    <xsl:if test="string-length($userid) &gt; 0">
	       User [<xsl:value-of select="$userid"/>]
	    </xsl:if>
	     -->
	    <br/>
	    <br/>
	    Service Connector provided by <a href="http://www.stabilit.ch/" class="sc_header" target="stabilit">Stabilit</a>
	  </div>
	</xsl:template>
	<xsl:template name="sc_menu">
	   <div id="sc_menu_left">
	     <div class="sc_menu_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_menu_item" href="./{$urlencoded}">Home</a></div><xsl:call-template name="sc_menu_left"/>
	   </div>
	   <div id="sc_menu_right">
	     <div class="sc_menu_item" style="float:right" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_menu_item" href="{$urlencoded}?action=logout">Logout</a></div>
	   </div>
	</xsl:template>
	<xsl:template name="sc_navigation">
	<!-- 
	  <div class="sc_navigation_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_navigation_item" href="status{$urlencoded}">Status</a></div>
    -->	  
	  <div class="sc_navigation_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_navigation_item"  href="services{$urlencoded}">Services</a></div>
	  <div class="sc_navigation_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_navigation_item"  href="sessions{$urlencoded}">Sessions</a></div>
	  <div class="sc_navigation_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_navigation_item"  href="subscriptions{$urlencoded}">Subscriptions</a></div>
	  <div class="sc_navigation_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_navigation_item"  href="servers{$urlencoded}">Servers</a></div>
	  <div class="sc_navigation_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_navigation_item"  href="listeners{$urlencoded}">Listeners</a></div>
	  <!-- 
	  <div class="sc_navigation_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_navigation_item"  href="network{$urlencoded}">Network</a></div>
	   -->
	  <div class="sc_navigation_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_navigation_item"  href="cache{$urlencoded}">Cache</a></div>
	  <div class="sc_navigation_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_navigation_item"  href="maintenance{$urlencoded}">Maintenance</a></div>
	  <div class="sc_navigation_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_navigation_item"  href="logs{$urlencoded}">Logs</a></div>
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
                 <div id="sc_status_area_success" style="visibility:visible; float:right"><img border="0" width="14" height="14" src="green.png"></img></div>             
                 <div id="sc_status_area_error" style="visibility:hidden; float:right"><img border="0" width="14" height="14" src="red.png"/></div>
               </div>
             </xsl:when>
             <xsl:otherwise>
               <div id="sc_status_area" style="padding-left:2px; float:right">
                 <div id="sc_status_area_success" style="visibility:hidden; float:right"><img border="0" width="14" height="14" src="green.png"/></div>             
                 <div id="sc_status_area_error" style="visibility:visible; float:right"><img border="0" width="14" height="14" src="red.png"/></div>
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
          <xsl:apply-templates select="$body/system/webinfo"/>        
          <xsl:apply-templates select="$body/system/runtime"/>        
        </table>
      </div>
	</xsl:template>
	<xsl:template match="webinfo">
	  <tr class="sc_table_even">
	    <td class="sc_table_even">Web Sessions</td>	  
	    <td class="sc_table_even"><xsl:value-of select="sessions"/></td>	  
	  </tr>	  
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
	  <tr class="sc_table_odd">
	    <td class="sc_table_even">Thread Count</td>	  
	    <td class="sc_table_even"><xsl:value-of select="threadCount"/></td>	  
	  </tr>
	  <tr class="sc_table_odd">
	    <td class="sc_table_odd">Daemon Thrds</td>	  
	    <td class="sc_table_odd"><xsl:value-of select="daemonThreadCount"/></td>	  
	  </tr>
	  <tr class="sc_table_odd">
	    <td class="sc_table_even">Peak Threads</td>	  
	    <td class="sc_table_even"><xsl:value-of select="peakThreadCount"/></td>	  
	  </tr>
	  <tr>
	    <td colspan="2" class="sc_table_even"><a class="sc_table" href="javascript:runGC('{$urlencoded}')">Run GC</a></td>
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
	    <xsl:call-template name="runtimeSinceStartup"/>
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
	  <!-- 
	  <tr class="sc_table_odd">
	    <td class="sc_table_odd">Cached Bytes</td>	  
	    <td class="sc_table_odd"><xsl:value-of select="cachedBytes"/></td>	  
	  </tr>
	  <tr class="sc_table_even">
	    <td class="sc_table_even">Cached Files</td>	  
	    <td class="sc_table_even"><xsl:value-of select="cachedFiles"/></td>	  
	  </tr>
	   -->
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
	<xsl:template name="runtimeSinceStartup">
	    <xsl:choose>
	      <xsl:when test="runtimeSinceStartupSeconds &gt;= (3600 * 24)">
	        <xsl:variable name="days" select="floor(runtimeSinceStartupSeconds div (3600 * 24))"/>
	        <xsl:variable name="hours" select="floor((runtimeSinceStartupSeconds mod (3600 * 24)) div 3660)"/>
	        <xsl:variable name="minutes" select="floor((runtimeSinceStartupSeconds mod 3600) div 60)"/>
	       	<td class="sc_table_odd">Runtime (d:h:m)</td>	  
	        <td class="sc_table_odd"><xsl:value-of select="$days"/>d&#160;<xsl:value-of select="$hours"/>h&#160;<xsl:value-of select="$minutes"/>&apos;</td>	  	       	     
	      </xsl:when>
	      <xsl:when test="runtimeSinceStartupSeconds &gt;= (60 * 60)">
	        <xsl:variable name="hours" select="floor(runtimeSinceStartupSeconds div 3600)"/>
	        <xsl:variable name="minutes" select="floor((runtimeSinceStartupSeconds mod 3600) div 60)"/>
	        <xsl:variable name="seconds" select="floor((runtimeSinceStartupSeconds mod 3600) mod 60)"/>
	       	<td class="sc_table_odd">Runtime (h:m:s)</td>	  
	        <td class="sc_table_odd"><xsl:value-of select="$hours"/>:<xsl:value-of select="$minutes"/>:<xsl:value-of select="$seconds"/></td>	  	       
	      </xsl:when>	      
	      <xsl:when test="runtimeSinceStartupSeconds &gt;= 60">
	      	<td class="sc_table_odd">Runtime (min)</td>	  
	        <td class="sc_table_odd"><xsl:value-of select="floor(runtimeSinceStartupSeconds div 60)"/>&apos;&#160;<xsl:value-of select="runtimeSinceStartupSeconds mod 60"/></td>	  	       
	      </xsl:when>
	      <xsl:otherwise>
	      	<td class="sc_table_odd">Runtime (s)</td>	  
	        <td class="sc_table_odd"><xsl:value-of select="runtimeSinceStartupSeconds"/></td>	  
	      </xsl:otherwise>
	    </xsl:choose>	    
	</xsl:template>
	<xsl:template name="pageArea">
	  <xsl:param name="title"/>
      <xsl:param name="size"/>
      <xsl:param name="currentPage"/>
      <xsl:param name="lastPage"/>
      <center>
      <table border="0" cellspacing="0" cellpadding="2" style="position:relative; height:20px; top:-3px;">
        <tr>
          <td style="padding-right:10px;"><xsl:value-of select="$title"/></td>
          <xsl:call-template name="pageAreaDetails">
            <xsl:with-param name="size" select="$size"/>
            <xsl:with-param name="currentPage" select="$currentPage"/>
            <xsl:with-param name="page">1</xsl:with-param>
            <xsl:with-param name="lastPage" select="$lastPage"/>
          </xsl:call-template>    
          <td style="border-right:1px solid white;"></td>
        </tr>
      </table>	
      </center>
	</xsl:template>
	<xsl:template name="pageAreaDetails">
      <xsl:param name="size"/>
      <xsl:param name="page"/>
      <xsl:param name="currentPage"/>
      <xsl:param name="lastPage"/>
      <xsl:choose>
        <xsl:when test="$currentPage = $page">
          <td style="background:orange; border-left:1px solid white; padding:2px; width:12px;text-align:center;">        
            <a href="{$head/meta/@path}{$urlencoded}?page={$page}" style="color:white;"><xsl:value-of select="$page"/></a>
          </td>
        </xsl:when>
        <xsl:otherwise>
          <td style="border-left:1px solid white; padding:2px; width:12px;text-align:center;">        
            <a href="{$head/meta/@path}{$urlencoded}?page={$page}" style="color:white;"><xsl:value-of select="$page"/></a>
          </td>
        </xsl:otherwise>
      </xsl:choose>      
      <xsl:if test="$page &lt; $lastPage">
	       <xsl:call-template name="pageAreaDetails">
	         <xsl:with-param name="size" select="$size"/>
	         <xsl:with-param name="currentPage" select="$currentPage"/>
	         <xsl:with-param name="page" select="$page + 1"/>
	         <xsl:with-param name="lastPage" select="$lastPage"/>
	       </xsl:call-template>
      </xsl:if>           
    </xsl:template>
</xsl:stylesheet>
