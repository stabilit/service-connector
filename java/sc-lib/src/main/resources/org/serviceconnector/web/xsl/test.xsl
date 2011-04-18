<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:import href="template.xsl"/>
    <xsl:variable name="body" select="/sc-web/body"/>
    <xsl:variable name="head" select="/sc-web/head"/>
    <xsl:variable name="pageParam" select="$head/query/param/@page"/>
    <xsl:variable name="testParam" select="$head/query/param/@test"/>
    <xsl:variable name="test" select="$body/test"/>
    <xsl:template name="sc_script">
      setInterval('infoCall()', 5000);	    
      setInterval("contentCall('<xsl:value-of select="$urlencoded"/>', 'test', 'test=<xsl:value-of select="$testParam"/>&amp;page=<xsl:value-of select="$pageParam"/>')", 10000);            
    </xsl:template>
    <xsl:template name="sc_content">
      <div class="sc_table max_width">
        <div class="sc_table_title">           
           <xsl:call-template name="pageArea">
             <xsl:with-param name="title">List of test data</xsl:with-param>
             <xsl:with-param name="size" select="$test/@size"/>
             <xsl:with-param name="currentPage" select="$test/@page"/>
             <xsl:with-param name="lastPage" select="$test/@last"/>
           </xsl:call-template>
        </div>             
        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
          <tr class="sc_table_header">
            <th class="sc_table">Index</th>
            <th class="sc_table">Data</th>
          </tr>
          <xsl:if test="not($body/test/item)">
            <tr class="sc_table_even"><td colspan="4" class="sc_table">no test data</td></tr>
          </xsl:if>          
          <xsl:apply-templates select="$body/test/item"/>
        </table>
      </div>
    </xsl:template>
	<xsl:template name="sc_menu_left"><xsl:call-template name="menu_separator"/><div class="sc_menu_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_menu_item" href="./test{$urlencoded}">Test</a></div></xsl:template>
	<xsl:template match="item">
	  <xsl:if test="position() mod 2 = 0">
	     <tr class="sc_table_even" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">
	        <xsl:call-template name="test_row">
	          <xsl:with-param name="class">sc_table_even</xsl:with-param>
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
	  <xsl:if test="position() mod 2 != 0">
	     <tr class="sc_table_odd" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">	    
	        <xsl:call-template name="test_row">
	          <xsl:with-param name="class">sc_table_odd</xsl:with-param>
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
	</xsl:template>
	<xsl:template name="test_row">
	    <xsl:param name="class"/>
        <td class="{$class}"><a class="sc_table" href="test{$urlencoded}?test={@index}"><xsl:value-of select="@index"/></a></td>
	    <td class="{$class}"><xsl:value-of select="."/></td>
	</xsl:template>
</xsl:stylesheet>