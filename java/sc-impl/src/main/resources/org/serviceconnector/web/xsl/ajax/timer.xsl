<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:variable name="userid" select="/sc-web/head/meta/@userid"/>
    <xsl:template match="/">
        <xsl:variable name="dateTime" select="/sc-web/head/meta/@creation"/>
	    <xsl:value-of select="substring($dateTime,0,11)"/>
	    <br/>
	    <xsl:value-of select="substring($dateTime,12,8)"/>
	    <br/>
	    <xsl:if test="string-length($userid) &gt; 0">
	       User [<xsl:value-of select="$userid"/>]
	       <br/>
	    </xsl:if>
	    V <xsl:value-of select="/sc-web/head/meta/@scversion"/>	   
	</xsl:template>
</xsl:stylesheet>
