<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:import href="template.xsl"/>
    <xsl:template name="sc_script">
    function loadResource() {
       resourceCall('<xsl:value-of select="$head/query/param/@name"/>');
    }
    </xsl:template>
    <xsl:template name="sc_content">
      <div class="sc_table max_width">
        <div class="sc_table_title">
           Resource <xsl:value-of select="$head/query/param/@name"/>
        </div>             
        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
          <xsl:apply-templates select="$body/resource"/>
        </table>
      </div>
    </xsl:template>
    <xsl:template match="resource">
      <div id="sc_resource">
        ... loading <xsl:value-of select="$head/query/param/@name"/> ...
      </div>
      <script type="text/javascript">loadResource();</script>
	</xsl:template>
	<xsl:template name="sc_menu_left">
	  <xsl:if test="$head/query/param/@id = 'logs'">	    
        <xsl:call-template name="menu_separator"/><div class="sc_menu_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_menu_item" href="./logs?date={substring-after($head/query/param/@name,'.log.')}">Logs</a></div>
      </xsl:if> 
	  <xsl:call-template name="menu_separator"/><div class="sc_menu_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_menu_item" href="./resource?name={$head/query/param/@name}"><xsl:value-of select="$head/query/param/@name"/></a></div>
	</xsl:template>
</xsl:stylesheet>
