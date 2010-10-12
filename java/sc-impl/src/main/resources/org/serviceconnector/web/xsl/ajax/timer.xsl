<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:variable name="userid" select="/sc-web/head/meta/@userid"/>
    <xsl:template match="/">
        <xsl:variable name="dateTime" select="/sc-web/head/meta/@creation"/>
	    <xsl:value-of select="substring($dateTime,0,11)"/>&#160;
	    <xsl:value-of select="substring($dateTime,12,8)"/>
	    <br/>
	    <xsl:if test="string-length($userid) &gt; 0">
	       User [<xsl:value-of select="$userid"/>]
	    </xsl:if>
	    <br/>
	    <br/>
	    Service Connector provided by <a href="www.stabilit.ch" class="sc_header" target="stabilit">Stabilit</a>
	</xsl:template>
</xsl:stylesheet>
