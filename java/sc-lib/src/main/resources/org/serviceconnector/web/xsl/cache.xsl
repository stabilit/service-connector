<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:import href="template.xsl"/>
    <xsl:variable name="body" select="/sc-web/body"/>
    <xsl:variable name="head" select="/sc-web/head"/>
    <xsl:variable name="type" select="$head/query/param/@type"/>
	<xsl:variable name="comp_page" select="/sc-web/head/query/param/@comp_page"/>
	<xsl:variable name="comp_site" select="/sc-web/head/query/param/@comp_site"/>
	<xsl:variable name="msg_page" select="/sc-web/head/query/param/@msg_page"/>
	<xsl:variable name="msg_site" select="/sc-web/head/query/param/@msg_site"/>
    <xsl:variable name="caches" select="$body/caches"/>        
    <xsl:variable name="query">page=<xsl:value-of select="$page"/>&amp;site=<xsl:value-of select="$site"/>&amp;</xsl:variable>
    <xsl:template name="sc_script">
      setInterval('infoCall()', 5000);	    
      setInterval("contentCall('<xsl:value-of select="$urlencoded"/>', 'cache', 'cache=<xsl:value-of select="$head/query/param/@cache"/>&amp;composite=<xsl:value-of select="$head/query/param/@composite"/>&amp;type=<xsl:value-of select="$type"/>&amp;page=<xsl:value-of select="$page"/>&amp;comp_page=<xsl:value-of select="$comp_page"/>&amp;msg_page=<xsl:value-of select="$msg_page"/>&amp;site=<xsl:value-of select="$site"/>&amp;comp_site=<xsl:value-of select="$comp_site"/>&amp;msg_site=<xsl:value-of select="$msg_site"/>&amp;sim=<xsl:value-of select="$sim"/>')", 10000);      
    </xsl:template>
    <xsl:template name="sc_content">
        <div class="sc_table max_width">
          <div class="sc_table_title">
           Cache Manager Configuration
          </div>
        <xsl:call-template name="cache_config"/>
        <div class="sc_table_title">
           <xsl:call-template name="pageArea">
             <xsl:with-param name="title">List of Caches</xsl:with-param>
             <xsl:with-param name="prefix"></xsl:with-param>
             <xsl:with-param name="query"></xsl:with-param>
             <xsl:with-param name="size" select="$caches/@size"/>
             <xsl:with-param name="currentSite" select="$caches/@site"/>
             <xsl:with-param name="currentPage" select="$caches/@page"/>
             <xsl:with-param name="lastPage" select="$caches/@lastPage"/>
             <xsl:with-param name="lastSite" select="$caches/@lastSite"/>
             <xsl:with-param name="siteSize" select="$caches/@siteSize"/>
             <xsl:with-param name="pageSize" select="$caches/@pageSize"/>
           </xsl:call-template>
        </div>             
        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
          <tr class="sc_table_header">
            <th class="sc_table">Service Name</th>
            <th class="sc_table">Message Count</th> <!-- Composite Size -->
            <th class="sc_table">Message Part (MP) Count</th>  <!-- Element Size -->
            <th class="sc_table">MP in Memory</th> <!-- Memory Store Size -->
            <th class="sc_table">MP on Disk</th> <!-- Disk Store Size -->
          </tr>          
          <xsl:if test="not($body/caches/cache)">
            <tr class="sc_table_even"><td colspan="7" class="sc_table">no caches</td></tr>
          </xsl:if>          
          <xsl:apply-templates select="$body/caches/cache"/>
        </table>
      </div>
    </xsl:template>
	<xsl:template name="sc_menu_left"><xsl:call-template name="menu_separator"/><div class="sc_menu_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_menu_item" href="./cache{$urlencoded}">Cache</a></div></xsl:template>
	<xsl:template match="cache">
	  <xsl:if test="position() mod 2 = 0">
	     <tr class="sc_table_even" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">
	        <xsl:if test="details">
	          <xsl:attribute name="style">height:40px;</xsl:attribute>
	        </xsl:if>
	        <xsl:call-template name="cache_row">
	          <xsl:with-param name="class">sc_table_even</xsl:with-param>
	          <xsl:with-param name="query" select="$query"/>
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
	          <xsl:with-param name="query" select="$query"/>
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
      <xsl:if test="details">
        <tr>
          <xsl:call-template name="cache_details"/>
        </tr>
      </xsl:if>
	</xsl:template>
	<xsl:template name="cache_row">
	    <xsl:param name="query"/>
	    <xsl:param name="class"/>
	    <td class="{$class}"><xsl:value-of select="serviceName"/></td>
	    <td class="{$class}">
	      <xsl:choose>
	        <xsl:when test="compositeSize &gt; 0">
	         <a class="sc_table" href="cache{$urlencoded}?{$query}cache={serviceName}"><xsl:value-of select="compositeSize"/></a>
            </xsl:when>
            <xsl:otherwise>	       
	         <xsl:value-of select="compositeSize"/>
	        </xsl:otherwise>
          </xsl:choose>       	    
	    </td>
	    <td class="{$class}"><xsl:value-of select="elementSize"/></td>
	    <td class="{$class}"><xsl:value-of select="memoryStoreSize"/></td>
	    <td class="{$class}"><xsl:value-of select="diskStoreSize"/></td>
	</xsl:template>
	<xsl:template name="cache_details">
	  <td colspan="7">
	    <div class="sc_table_details">
	        <div class="sc_table_title">
	           <xsl:choose>
	             <xsl:when test="details/@size &gt; 0">
	               <xsl:call-template name="pageArea">
	                 <xsl:with-param name="title">List of Cached Messages</xsl:with-param> <!-- List of cache composites -->
	                 <xsl:with-param name="prefix">comp_</xsl:with-param>
	                 <xsl:with-param name="query"><xsl:value-of select="$query"/>cache=<xsl:value-of select="$head/query/param/@cache"/>&amp;</xsl:with-param>
	                 <xsl:with-param name="size" select="details/@size"/>
	                 <xsl:with-param name="currentSite" select="details/@site"/>
	                 <xsl:with-param name="currentPage" select="details/@page"/>
	                 <xsl:with-param name="lastPage" select="details/@lastPage"/>
	                 <xsl:with-param name="lastSite" select="details/@lastSite"/>
	                 <xsl:with-param name="siteSize" select="details/@siteSize"/>
	                 <xsl:with-param name="pageSize" select="details/@pageSize"/>
	               </xsl:call-template>	               
	             </xsl:when>
	             <xsl:otherwise>No Cached Messages</xsl:otherwise>	
	           </xsl:choose>           
	        </div>             
	        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
	          <tr class="sc_table_header">
	            <th class="sc_table">cid</th><!-- ID -->
	            <th class="sc_table">Status</th>
	            <th class="sc_table">Header Field Count</th><!-- Headers -->
	            <th class="sc_table">Message Parts</th><!-- Messages -->
	            <th class="sc_table">Expiration</th>
	            <th class="sc_table">Loading Timeout (ms)</th>            
	            <th class="sc_table">Creation</th>            
	            <th class="sc_table">Last Modified</th>            
	          </tr>          
	          <xsl:apply-templates select="details/composite">
	            <xsl:with-param name="query"><xsl:value-of select="$query"/>cache=<xsl:value-of select="$head/query/param/@cache"/>&amp;</xsl:with-param>
	            <xsl:with-param name="subPagingQuery">comp_page=<xsl:value-of select="details/@page"/>&amp;comp_site=<xsl:value-of select="details/@site"/>&amp;</xsl:with-param>	          
	          </xsl:apply-templates>
	        </table>
        </div>
	  </td>
	</xsl:template>
	<xsl:template match="composite">
	  <xsl:param name="query"/>
	  <xsl:param name="subPagingQuery"/>
	  <xsl:if test="position() mod 2 = 0">
	     <tr class="sc_table_even" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">
	        <xsl:call-template name="composite_row">
	          <xsl:with-param name="class">sc_table_even</xsl:with-param>
	          <xsl:with-param name="query" select="$query"/>
	          <xsl:with-param name="subPagingQuery" select="$subPagingQuery"/>
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
	  <xsl:if test="position() mod 2 != 0">
	     <tr class="sc_table_odd" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">	    
	        <xsl:call-template name="composite_row">
	          <xsl:with-param name="class">sc_table_odd</xsl:with-param>
              <xsl:with-param name="query" select="$query"/>               
	          <xsl:with-param name="subPagingQuery" select="$subPagingQuery"/>
	        </xsl:call-template>
	     </tr>	    
         <xsl:if test="messages/message and $type='message'">
           <tr>
             <xsl:call-template name="cache_message_details">
               <xsl:with-param name="serviceName" select="key"/>
             </xsl:call-template>
           </tr>
        </xsl:if>
         <xsl:if test="messages/message and $type='header'">
           <tr>
             <xsl:call-template name="cache_header_details">
               <xsl:with-param name="serviceName" select="key"/>
             </xsl:call-template>
           </tr>
        </xsl:if>
	  </xsl:if>	  
	</xsl:template>	
    <xsl:template name="composite_row">
	    <xsl:param name="class"/>
	    <xsl:param name="query"/>
	    <xsl:param name="subPagingQuery"/>	    
	    <td class="{$class}"><xsl:value-of select="key"/></td>
	    <td class="{$class}">
	      <xsl:value-of select="state"/>
	      <xsl:if test="state = 'LOADING'">
	        &#160;(<xsl:value-of select="loadingSessionId"/>)
	      </xsl:if>
	    </td>
	    <td class="{$class}">
	      <xsl:choose>
	        <xsl:when test="count(header/item) &gt; 0">
	         <a class="sc_table" href="cache{$urlencoded}?{$query}{$subPagingQuery}composite={key}&amp;type=header"><xsl:value-of select="count(header/item)"/></a>
            </xsl:when>
            <xsl:otherwise>	       
	         <xsl:value-of select="count(header/item)"/>
	        </xsl:otherwise>
          </xsl:choose>       	    
	    </td>
	    <td class="{$class}">
	      <xsl:choose>
	        <xsl:when test="size &gt; 0">
	         <a class="sc_table" href="cache{$urlencoded}?{$query}{$subPagingQuery}composite={key}&amp;type=message"><xsl:value-of select="size"/></a>
            </xsl:when>
            <xsl:otherwise>	       
	         <xsl:value-of select="size"/>
	        </xsl:otherwise>
          </xsl:choose>       	    
	    </td>
	    <td class="{$class}"><xsl:call-template name="fieldValue"><xsl:with-param name="value" select="expiration"/></xsl:call-template></td>
	    <td class="{$class}"><xsl:value-of select="loadingTimeout"/></td>
	    <td class="{$class}"><xsl:value-of select="creation"/></td>
	    <td class="{$class}"><xsl:value-of select="lastModified"/></td>
	</xsl:template>
	<xsl:template name="cache_config">
	  <xsl:variable name="config" select="$body/cache/config"/>
      <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
         <tr class="sc_table_header">
           <th class="sc_table">Status</th>
           <th class="sc_table">Disk Path</th>
           <th class="sc_table">Max Message Parts in Memory</th><!-- maxElementsInMemory -->
           <th class="sc_table">Max Message Parts on Disk</th><!-- maxElementsOnDisk -->
           <th class="sc_table">Loading (Sessions/MP)</th>            
         </tr>          
         <tr class="sc_table_odd" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">
           <td class="sc_table_odd">
             <xsl:if test="$config/enabled = 'true'">ENABLED</xsl:if>
             <xsl:if test="$config/enabled != 'true'">DISABLED</xsl:if>
           </td>
           <td class="sc_table_odd"><xsl:value-of select="$config/diskPath"/></td>           
           <td class="sc_table_odd"><xsl:value-of select="$config/maxElementsInMemory"/></td>           
           <td class="sc_table_odd"><xsl:value-of select="$config/maxElementsOnDisk"/></td>           
           <td class="sc_table_odd"><xsl:value-of select="count($body/cacheLoading/session)"/>/<xsl:value-of select="count($body/cacheLoading/session/cacheId)"/></td>           
         </tr>
      </table>	
	  <div class="sc_separator"/>
	  <div class="sc_separator"/>
	</xsl:template>
	<xsl:template name="cache_message_details">
	  <xsl:param name="serviceName"/>
	  <td colspan="8">
	    <div class="sc_table_details">
	        <div class="sc_table_title">
	           <xsl:call-template name="pageArea">
                 <xsl:with-param name="title">List of Cached Message Parts [<xsl:value-of select="$serviceName"/>]</xsl:with-param> <!-- List of cache messages (parts) -->
                 <xsl:with-param name="prefix">msg_</xsl:with-param>
                 <xsl:with-param name="query"><xsl:value-of select="$query"/>cache=<xsl:value-of select="$head/query/param/@cache"/>&amp;composite=<xsl:value-of select="key"/>&amp;comp_page=<xsl:value-of select="$comp_page"/>&amp;comp_site=<xsl:value-of select="$comp_site"/>&amp;type=message&amp;</xsl:with-param>
                 <xsl:with-param name="size" select="messages/@size"/>
                 <xsl:with-param name="currentSite" select="messages/@site"/>
                 <xsl:with-param name="currentPage" select="messages/@page"/>
                 <xsl:with-param name="lastPage" select="messages/@lastPage"/>
                 <xsl:with-param name="lastSite" select="messages/@lastSite"/>
                 <xsl:with-param name="siteSize" select="messages/@siteSize"/>
                 <xsl:with-param name="pageSize" select="messages/@pageSize"/>
	           </xsl:call-template>	               	           
	        </div>             
	        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
	          <tr class="sc_table_header">
	            <th class="sc_table">cid/cpn</th><!-- ID -->
	            <th class="sc_table">Sequence Nr</th>
	            <th class="sc_table">Message Type</th>
	            <th class="sc_table">Compression</th>
	            <th class="sc_table">Length (Bytes)</th>            
	          </tr>          
	          <xsl:apply-templates select="messages/message"/>
	        </table>
        </div>
	  </td>
	</xsl:template>
	<xsl:template match="message">
	  <xsl:if test="position() mod 2 = 0">
	     <tr class="sc_table_even" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">
	        <xsl:call-template name="message_row">
	          <xsl:with-param name="class">sc_table_even</xsl:with-param>
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
	  <xsl:if test="position() mod 2 != 0">
	     <tr class="sc_table_odd" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">	    
	        <xsl:call-template name="message_row">
	          <xsl:with-param name="class">sc_table_odd</xsl:with-param>
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
	</xsl:template>
	<xsl:template name="message_row">
	  <xsl:param name="class"/>
      <td class="{$class}"><xsl:value-of select="id"/></td>
      <td class="{$class}"><xsl:value-of select="sequenceNr"/></td>
      <td class="{$class}"><xsl:call-template name="fieldValue"><xsl:with-param name="value" select="messageType"/></xsl:call-template></td>
      <td class="{$class}"><xsl:value-of select="compressed"/></td>
      <td class="{$class}"><xsl:call-template name="fieldValue"><xsl:with-param name="value" select="bodyLength"/></xsl:call-template></td>
	</xsl:template>
	<xsl:template name="cache_header_details">
	  <xsl:param name="serviceName"/>
	  <td colspan="8">
	    <div class="sc_table_details">
	        <div class="sc_table_title">
	           Message headers [<xsl:value-of select="$serviceName"/>]<!-- composite headers -->
	        </div>             
	        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
	          <tr class="sc_table_header">
	            <xsl:for-each select="header/item">
	               <th class="sc_table"><xsl:value-of select="@name"/></th>
	            </xsl:for-each>
	          </tr>          
		      <tr class="sc_table_header">
		         <xsl:for-each select="header/item">
		           <td class="sc_table_odd"><xsl:value-of select="."/></td>
		         </xsl:for-each>
		      </tr>          
	        </table>
        </div>
	  </td>
	</xsl:template>
</xsl:stylesheet>
