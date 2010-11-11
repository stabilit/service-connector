<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:import href="template.xsl"/>
    <xsl:variable name="body" select="/sc-web/body"/>
    <xsl:variable name="head" select="/sc-web/head"/>
    <xsl:template name="sc_script">
      setInterval('infoCall()', 5000);	    
      setInterval("contentCall('cache', '')", 10000);      
    </xsl:template>
    <xsl:template name="sc_content">
        <div class="sc_table max_width">
          <div class="sc_table_title">
           Cache manager configuration
          </div>
        <xsl:call-template name="cache_config"/>
        <div class="sc_table_title">
           List of caches
        </div>             
        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
          <tr class="sc_table_header">
            <th class="sc_table">Service Name</th>
            <th class="sc_table">Cache Name</th>
            <th class="sc_table">Element Size</th>
            <th class="sc_table">Memory Store Size</th>
            <th class="sc_table">Disk Store Size</th>
          </tr>          
          <xsl:if test="not($body/caches/cache)">
            <tr class="sc_table_even"><td colspan="7" class="sc_table">no caches</td></tr>
          </xsl:if>          
          <xsl:apply-templates select="$body/caches/cache"/>
        </table>
      </div>
    </xsl:template>
	<xsl:template name="sc_menu_left"><xsl:call-template name="menu_separator"/><div class="sc_menu_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_menu_item" href="./cache">Cache</a></div></xsl:template>
	<xsl:template match="cache">
	  <xsl:if test="position() mod 2 = 0">
	     <tr class="sc_table_even" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">
	        <xsl:if test="details">
	          <xsl:attribute name="style">height:40px;</xsl:attribute>
	        </xsl:if>
	        <xsl:call-template name="cache_row">
	          <xsl:with-param name="class">sc_table_even</xsl:with-param>
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
	  <xsl:if test="position() mod 2 != 0">
	     <tr class="sc_table_odd" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">	    
	        <xsl:if test="details">
	          <xsl:attribute name="style">height:40px;</xsl:attribute>
	        </xsl:if>
	        <xsl:call-template name="cache_row">
	          <xsl:with-param name="class">sc_table_odd</xsl:with-param>
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
      <xsl:if test="false">
        <tr>
          <xsl:call-template name="cache_details"/>
        </tr>
      </xsl:if>
	</xsl:template>
	<xsl:template name="cache_row">
	    <xsl:param name="class"/>
	    <td class="{$class}"><xsl:value-of select="serviceName"/></td>
	    <td class="{$class}"><xsl:value-of select="cacheName"/></td>
	    <td class="{$class}"><xsl:value-of select="elementSize"/></td>
	    <td class="{$class}"><xsl:value-of select="memoryStoreSize"/></td>
	    <td class="{$class}"><xsl:value-of select="diskStoreSize"/></td>
	</xsl:template>
	<xsl:template name="cache_details">
	  <td colspan="7">
	  </td>
	</xsl:template>
	<xsl:template name="cache_config">
	  <xsl:variable name="config" select="$body/cache/config"/>
      <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
         <tr class="sc_table_header">
           <th class="sc_table">Status</th>
           <th class="sc_table">diskPersistent</th>
           <th class="sc_table">Name</th>
           <th class="sc_table">Disk Path</th>
           <th class="sc_table">maxElementsInMemory</th>
           <th class="sc_table">maxElementsOnDisk</th>
         </tr>          
         <tr class="sc_table_odd" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">
           <td class="sc_table_odd">
             <xsl:if test="$config/enabled = 'true'">ENABLED</xsl:if>
             <xsl:if test="$config/enabled != 'true'">DISABLED</xsl:if>
           </td>
           <td class="sc_table_odd">
             <xsl:if test="$config/diskPersistent = 'true'">ENABLED</xsl:if>
             <xsl:if test="$config/diskPersistent != 'true'">DISABLED</xsl:if>
           </td>
           <td class="sc_table_odd"><xsl:value-of select="$config/name"/></td>           
           <td class="sc_table_odd"><xsl:value-of select="$config/diskPath"/></td>           
           <td class="sc_table_odd"><xsl:value-of select="$config/maxElementsInMemory"/></td>           
           <td class="sc_table_odd"><xsl:value-of select="$config/maxElementsOnDisk"/></td>           
         </tr>
      </table>	
	  <div class="sc_separator"/>
	  <div class="sc_separator"/>
	</xsl:template>
</xsl:stylesheet>
