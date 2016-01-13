<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:import href="template.xsl"/>
    <xsl:template name="sc_content">
      <div class="sc_table max_width">
        <div class="sc_table_title">
           System Info
        </div>             
        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
          <xsl:apply-templates select="$body/system/info/*"/>
        </table>
      </div>
    </xsl:template>
    <xsl:template match="system/info/*">
      <xsl:if test="position() mod 2 = 0">
	     <tr class="sc_table_even" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">
	        <xsl:call-template name="service_row">
	          <xsl:with-param name="class">sc_table_even</xsl:with-param>
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
	  <xsl:if test="position() mod 2 != 0">
	     <tr class="sc_table_odd" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">	    
	        <xsl:call-template name="service_row">
	          <xsl:with-param name="class">sc_table_odd</xsl:with-param>
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
	</xsl:template>
	<xsl:template name="service_row">
	    <xsl:param name="class"/>
	    <td class="{$class}"><xsl:value-of select="local-name()"/></td>
	    <td class="{$class}">
	      <xsl:if test="local-name() = 'configFileName'">
	         <a class="sc_table" href="./resource{$urlencoded}?name={.}"><xsl:value-of select="."/></a>
	      </xsl:if>
	      <xsl:if test="local-name() != 'configFileName'">
	         <xsl:value-of select="."/>
	      </xsl:if>
	    </td>	
	</xsl:template>
</xsl:stylesheet>
