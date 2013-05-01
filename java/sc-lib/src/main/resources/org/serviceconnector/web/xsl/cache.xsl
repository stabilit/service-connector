<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:import href="template.xsl"/>
    <xsl:variable name="head" select="/sc-web/head"/>
    <xsl:variable name="body" select="/sc-web/body"/>
    <xsl:variable name="type" select="$head/query/param/@type"/>
	<xsl:variable name="comp_page" select="/sc-web/head/query/param/@comp_page"/>
	<xsl:variable name="comp_site" select="/sc-web/head/query/param/@comp_site"/>
	<xsl:variable name="msg_page" select="/sc-web/head/query/param/@msg_page"/>
	<xsl:variable name="msg_site" select="/sc-web/head/query/param/@msg_site"/>
    <xsl:variable name="cacheModules" select="$body/cache/cacheModules"/>        
    <xsl:variable name="query">page=<xsl:value-of select="$page"/>&amp;site=<xsl:value-of select="$site"/>&amp;</xsl:variable>
    <xsl:template name="sc_script">
      setInterval('infoCall()', 5000);	    
      setInterval("contentCall('<xsl:value-of select="$urlencoded"/>', 'cache', 'cacheModule=<xsl:value-of select="$head/query/param/@cacheModule"/>&amp;cacheMessageKey=<xsl:value-of select="$head/query/param/@cacheMessageKey"/>&amp;type=<xsl:value-of select="$type"/>&amp;page=<xsl:value-of select="$page"/>&amp;comp_page=<xsl:value-of select="$comp_page"/>&amp;msg_page=<xsl:value-of select="$msg_page"/>&amp;site=<xsl:value-of select="$site"/>&amp;comp_site=<xsl:value-of select="$comp_site"/>&amp;msg_site=<xsl:value-of select="$msg_site"/>&amp;sim=<xsl:value-of select="$sim"/>')", 10000);      
    </xsl:template>
    <xsl:template name="sc_content">
        <div class="sc_table max_width">
          <div class="sc_table_title">
           Cache Configuration
          </div>
        <xsl:call-template name="cache_config"/>
        <div class="sc_table_title">
           <xsl:call-template name="pageArea">
             <xsl:with-param name="title">List of Cache Modules</xsl:with-param>
             <xsl:with-param name="prefix"></xsl:with-param>
             <xsl:with-param name="query"></xsl:with-param>
             <xsl:with-param name="size" select="$cacheModules/@size"/>
             <xsl:with-param name="currentSite" select="$cacheModules/@site"/>
             <xsl:with-param name="currentPage" select="$cacheModules/@page"/>
             <xsl:with-param name="lastPage" select="$cacheModules/@lastPage"/>
             <xsl:with-param name="lastSite" select="$cacheModules/@lastSite"/>
             <xsl:with-param name="siteSize" select="$cacheModules/@siteSize"/>
             <xsl:with-param name="pageSize" select="$cacheModules/@pageSize"/>
           </xsl:call-template>
        </div>             
        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
          <tr class="sc_table_header">
            <th class="sc_table">Cache Module Name</th>
            <th class="sc_table">Message Count</th>
            <th class="sc_table">Messages in Memory</th>
            <th class="sc_table">Messages on Disk</th>
          </tr>          
          <xsl:if test="not($body/cache/cacheModules/cacheModule)">
            <tr class="sc_table_even"><td colspan="7" class="sc_table">no cache modules</td></tr>
          </xsl:if>          
          <xsl:apply-templates select="$body/cache/cacheModules/cacheModule"/>
        </table>
      </div>
    </xsl:template>
	<xsl:template name="sc_menu_left"><xsl:call-template name="menu_separator"/><div class="sc_menu_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_menu_item" href="./cache{$urlencoded}">Cache</a></div></xsl:template>
	<xsl:template match="cacheModule">
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
	    <td class="{$class}"><xsl:value-of select="cacheModuleName"/></td>
	    <td class="{$class}">
	      <xsl:choose>
	        <xsl:when test="cachedMessageCount &gt; 0">
	         <a class="sc_table" href="cache{$urlencoded}?{$query}cacheModule={cacheModuleName}"><xsl:value-of select="cachedMessageCount"/></a>
            </xsl:when>
            <xsl:otherwise>	       
	         <xsl:value-of select="cachedMessageCount"/>
	        </xsl:otherwise>
          </xsl:choose>       	    
	    </td>
	    <td class="{$class}"><xsl:value-of select="numberOfMessagesInMemoryStore"/></td>
	    <td class="{$class}"><xsl:value-of select="numberOfMessagesInDiskStore"/></td>
	</xsl:template>
	<xsl:template name="cache_details">
	  <td colspan="7">
	    <div class="sc_table_details">
	        <div class="sc_table_title">
	           <xsl:choose>
	             <xsl:when test="details/@size &gt; 0">
	               <xsl:call-template name="pageArea">
	                 <xsl:with-param name="title">List of Cached Messages</xsl:with-param>
	                 <xsl:with-param name="prefix">comp_</xsl:with-param>
	                 <xsl:with-param name="query"><xsl:value-of select="$query"/>cacheModule=<xsl:value-of select="$head/query/param/@cacheModule"/>&amp;</xsl:with-param>
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
	            <th class="sc_table">Key</th><!-- ID -->
	            <th class="sc_table">Status</th>
	            <xsl:if test="details/cacheMessage/expiration">
	            	<th class="sc_table">Expiration</th>
	            </xsl:if>
	             <xsl:if test="details/cacheMessage/expirationTimeout">
	            	 <th class="sc_table">Expiration Timeout (ms)</th>  
	            </xsl:if>          
	            <th class="sc_table">Creation</th>            
	            <th class="sc_table">Last Access</th>
	            <xsl:if test="details/cacheMessage/nrOfAppendix">
	            	<th class="sc_table">Number of Appendix</th>
	            </xsl:if>
	            <xsl:if test="details/cacheMessage/cacheGuardianName">
	            	<th class="sc_table">Cache Guardian</th>
	            </xsl:if>
	            <th class="sc_table">Header Field Count</th><!-- Headers -->
	          </tr>          
	          <xsl:apply-templates select="details/cacheMessage">
	            <xsl:with-param name="query"><xsl:value-of select="$query"/>cacheModule=<xsl:value-of select="$head/query/param/@cacheModule"/>&amp;</xsl:with-param>
	            <xsl:with-param name="subPagingQuery">comp_page=<xsl:value-of select="details/@page"/>&amp;comp_site=<xsl:value-of select="details/@site"/>&amp;</xsl:with-param>       
	          	<xsl:with-param name="cacheMessageKey"><xsl:value-of select="$head/query/param/@cacheMessageKey"/></xsl:with-param>       
	          </xsl:apply-templates>
	        </table>
        </div>
	  </td>
	</xsl:template>	
	<xsl:template match="cacheMessage">
	  <xsl:param name="query"/>
	  <xsl:param name="subPagingQuery"/>
	  <xsl:param name="cacheMessageKey"/>
	  <xsl:if test="position() mod 2 = 0">
	     <tr class="sc_table_even" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">
	        <xsl:call-template name="cacheMessage_row">
	          <xsl:with-param name="class">sc_table_even</xsl:with-param>
	          <xsl:with-param name="query" select="$query"/>
	          <xsl:with-param name="subPagingQuery" select="$subPagingQuery"/>
	        </xsl:call-template>
	     </tr>	    
	  </xsl:if>
	  <xsl:if test="position() mod 2 != 0">
	     <tr class="sc_table_odd" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)">	    
	        <xsl:call-template name="cacheMessage_row">
	          <xsl:with-param name="class">sc_table_odd</xsl:with-param>
              <xsl:with-param name="query" select="$query"/>               
	          <xsl:with-param name="subPagingQuery" select="$subPagingQuery"/>
	        </xsl:call-template>
	     </tr>   
	  </xsl:if>	  
      <xsl:if test="$type='header'">
	   	 <xsl:if test="key=$cacheMessageKey">
       	 <tr>
           	 <xsl:call-template name="cache_header_details">
           		<xsl:with-param name="cacheKey" select="key"/>
          	</xsl:call-template>
       	</tr>
       	</xsl:if>
     </xsl:if>
	</xsl:template>	
    <xsl:template name="cacheMessage_row">
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
	    <xsl:if test="expirationTimeout">
		    <td class="{$class}"><xsl:value-of select="expirationTimeout"/></td>
		</xsl:if>
	    <xsl:if test="expiration">
		    <td class="{$class}"><xsl:value-of select="expiration"/></td>
		</xsl:if>
	    <td class="{$class}"><xsl:value-of select="creation"/></td>
	    <td class="{$class}"><xsl:value-of select="lastAccess"/></td>
    	<xsl:if test="nrOfAppendix">
		    <td class="{$class}"><xsl:value-of select="nrOfAppendix"/></td>
		</xsl:if>
		<xsl:if test="cacheGuardianName">
		    <td class="{$class}"><xsl:value-of select="cacheGuardianName"/></td>
		</xsl:if>    	    
	    <td class="{$class}">
	      <xsl:choose>
	        <xsl:when test="count(header/item) &gt; 0">
	         <a class="sc_table" href="cache{$urlencoded}?{$query}{$subPagingQuery}cacheMessageKey={key}&amp;type=header"><xsl:value-of select="count(header/item)"/></a>
            </xsl:when>
            <xsl:otherwise>	       
	         <xsl:value-of select="count(header/item)"/>
	        </xsl:otherwise>
          </xsl:choose>       	    
	    </td>
	</xsl:template>
	<xsl:template name="cache_config">
	  <xsl:variable name="config" select="$body/cache/config"/>
      <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
         <tr class="sc_table_header">
           <th class="sc_table">Status</th>
           <th class="sc_table">Disk Path</th>
           <th class="sc_table">Max Messages in Memory</th><!-- maxElementsInMemory -->
           <th class="sc_table">Max Messages on Disk</th><!-- maxElementsOnDisk -->
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
	<xsl:template name="cache_header_details">
	  <xsl:param name="cacheKey" />
	  <td colspan="8">
	    <div class="sc_table_details">
	        <div class="sc_table_title">
	           Message header [<xsl:value-of select="$cacheKey" />]
	        </div>             
	        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
	          <tr class="sc_table_header">
	            <xsl:for-each select="header/item">
	               <th class="sc_table"><xsl:value-of select="@name" /></th>
	            </xsl:for-each>
	          </tr>          
		      <tr class="sc_table_header">
		         <xsl:for-each select="header/item">
		           <td class="sc_table_odd"><xsl:value-of select="." /></td>
		         </xsl:for-each>
		      </tr>          
	        </table>
        </div>
	  </td>
	</xsl:template>
</xsl:stylesheet>
