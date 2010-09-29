<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
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
				<link rel="stylesheet" href="sc-web.css"></link>
				<script type="text/javascript" src="ajax.js"></script>
				<script type="text/javascript" src="sc_ajax.js"></script>
				<script type="text/javascript" src="sc.js"></script>
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
	  <div class="sc_navigation_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_navigation_item" href="status">Status</a></div>
	  <div class="sc_navigation_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_navigation_item"  href="services">Services</a></div>
	  <div class="sc_navigation_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_navigation_item"  href="network">Network</a></div>
	  <div class="sc_navigation_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_navigation_item"  href="cache">Cache</a></div>
	  <div class="sc_navigation_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_navigation_item"  href="maintenance">Maintenance</a></div>
	  <div class="sc_navigation_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_navigation_item"  href="logs">Logs</a></div>
	  <div class="sc_navigation_item" style="height:400px;"></div>
	</xsl:template>
	<xsl:template name="sc_content">Service Connector</xsl:template>
	<xsl:template name="sc_menu_left"></xsl:template>
	<xsl:template name="sc_dateTime">
	    <xsl:param name="dateTime"/>
	    <xsl:value-of select="substring($dateTime,0,11)"/>&#160;
	    <xsl:value-of select="substring($dateTime,12,8)"/>
	</xsl:template>
</xsl:stylesheet>
