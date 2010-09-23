<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:variable name="head" select="/sc-web/head"/>
	<xsl:variable name="body" select="/sc-web/body"/>
	<xsl:template match="/">
		<html>
			<head>
				<title><xsl:call-template name="sc_title"/></title>
				<link rel="stylesheet" href="sc-web.css"></link>
				<script type="text/javascript" src="ajax.js"></script>
				<script type="text/javascript" src="sc_ajax.js"></script>
			</head>
			<body>
				<div id="sc_root" >
					<div id="sc_header">
					   <xsl:call-template name="sc_header"/> 
					</div>
					<div id="sc_menu">
					   <xsl:call-template name="sc_menu"/> 
					</div>
					<div id="sc_navigation">
					   <xsl:call-template name="sc_navigation"/> 
					</div>
					<div id="sc_content">
					   <xsl:call-template name="sc_content"/> 
					</div>
				</div>
			</body>
		</html>
	</xsl:template>
	<xsl:template name="sc_title">Service Connector</xsl:template>
	<xsl:template name="sc_header">
	   <div id="sc_logo">
	     <a href="." class="sc_reload">Service Connector</a>
	   </div>
	   <div id="sc_logo_img">
         <a href="." class="sc_reload"><img border="0" src="stabilit.png"/></a>
	   </div>
	  <div id="sc_meta">
	    <xsl:call-template name="sc_dateTime">
	      <xsl:with-param name="dateTime" select="$head/meta/@creation"/>
	    </xsl:call-template>
	    <br/>
	    V <xsl:value-of select="$head/meta/@scversion"/>
	  </div>
	</xsl:template>
	<xsl:template name="sc_menu">
	   <div id="sc_menu_left"><xsl:call-template name="sc_menu_left"/></div>
	   <div id="sc_menu_right"><a href="logout">Logout</a></div>
	</xsl:template>
	<xsl:template name="sc_navigation">
	  <div class="sc_navigation_item"><a href="status">Status</a></div>
	  <div class="sc_navigation_item"><a href="services">Services</a></div>
	  <div class="sc_navigation_item"><a href="network">Network</a></div>
	  <div class="sc_navigation_item"><a href="cache">Cache</a></div>
	  <div class="sc_navigation_item"><a href="maintenance">Maintenance</a></div>
	  <div class="sc_navigation_item"><a href="logs">Logs</a></div>
	  <div class="sc_navigation_item" style="height:400px;"></div>
	</xsl:template>
	<xsl:template name="sc_content">Service Connector</xsl:template>
	<xsl:template name="sc_menu_left"></xsl:template>
	<xsl:template name="sc_dateTime">
	    <xsl:param name="dateTime"/>
	    <xsl:value-of select="substring($dateTime,0,11)"/>
	    <br/>
	    <xsl:value-of select="substring($dateTime,12,8)"/>
	</xsl:template>
</xsl:stylesheet>
