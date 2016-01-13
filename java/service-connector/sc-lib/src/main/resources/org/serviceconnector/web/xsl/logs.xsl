<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:import href="template.xsl"/>
    <xsl:template name="sc_script">
      setInterval('infoCall()', 5000);	    
      setInterval("contentCall('<xsl:value-of select="$urlencoded"/>', 'logs', 'date=<xsl:value-of select="$head/query/param/@date"/>')", 10000);      
    </xsl:template>
    <xsl:template name="sc_content">
      <div class="sc_table max_width">
        <div class="sc_table_title">
           Log files 
           <xsl:if test="$body/logs/@previous">
             <a class="sc_table_title"  alt="{$body/logs/@previous}" title="{$body/logs/@previous}" href="./logs{$urlencoded}?date={$body/logs/@previous}">&lt;&lt;</a>
           </xsl:if>
           &#160;<xsl:value-of select="$body/logs/@current"/>&#160;
           <xsl:if test="$body/logs/@next">             
             <a class="sc_table_title" alt="{$body/logs/@next}" title="{$body/logs/@next}" href="./logs{$urlencoded}?date={$body/logs/@next}">&gt;&gt;</a>
           </xsl:if>
        </div>             
        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
          <tr class="sc_table_header">
            <th class="sc_table">Logger Name</th>
            <th class="sc_table">Appender Name</th>
            <th class="sc_table">File</th>
            <xsl:if test="not($body/logs/@next)">
               <th class="sc_table">Log Level</th>
            </xsl:if>
          </tr>          
          <xsl:apply-templates select="$body/logs/logger/appender[@type = 'file']">
            <xsl:sort data-type="text" select="@name"/>
          </xsl:apply-templates>
        </table>
      </div>
    </xsl:template>
	<xsl:template name="sc_menu_left"><xsl:call-template name="menu_separator"/><div class="sc_menu_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_menu_item" href="./logs{$urlencoded}">Logs</a></div></xsl:template>
	<xsl:template match="appender">
	  <xsl:if test="position() mod 2 = 0">
	     <tr class="sc_table_even" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">
	        <xsl:call-template name="appender_row">
	          <xsl:with-param name="class">sc_table_even</xsl:with-param>
	          <xsl:with-param name="evenodd">even</xsl:with-param>
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
	  <xsl:if test="position() mod 2 != 0">
	     <tr class="sc_table_odd" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">	    
	        <xsl:call-template name="appender_row">
	          <xsl:with-param name="class">sc_table_odd</xsl:with-param>
	          <xsl:with-param name="evenodd">odd</xsl:with-param>
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
	</xsl:template>
	<xsl:template name="appender_row">
	    <xsl:param name="class"/>
	    <xsl:param name="evenodd"/>
	    <td class="{$class}"><xsl:value-of select="../@name"/></td>
	    <td class="{$class}"><xsl:value-of select="@name"/></td>
	    <td class="{$class}">
	      <xsl:choose>
	        <xsl:when test="file/@size">
  	          <a class="sc_table" href="./resource{$urlencoded}?id=logs&amp;name={file}"><xsl:value-of select="file"/></a>
	          &#160;
	          (<xsl:value-of select="file/@size"/>)
	        </xsl:when>
	        <xsl:otherwise>
	          not found
	        </xsl:otherwise>
	      </xsl:choose>
	    </td>
        <xsl:if test="not($body/logs/@next)">
	      <td class="{$class}">
	        <xsl:call-template name="logLevel">
	          <xsl:with-param name="evenodd" select="$evenodd"/>
	        </xsl:call-template>
          </td>
       </xsl:if>
	</xsl:template>
	<xsl:template name="logLevel">
	  <xsl:param name="evenodd"/>
	  <xsl:variable name="level" select="../level"/>
	  <select id="{../@name}" name="{../@name}" class="sc_select_{$evenodd}" style="width:80px;" onchange="javascript:changeLogLevel('{$urlencoded}', '{../@name}', this)">
	    <xsl:for-each select="$body/logs/log-levels/level">
	      <option>
            <xsl:if test="$level = .">
              <xsl:attribute name="selected">yes</xsl:attribute>
            </xsl:if>	        
	        <xsl:value-of select="."/>
	      </option>
	    </xsl:for-each>
	  </select>
	</xsl:template>
</xsl:stylesheet>
