<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:import href="template.xsl"/>
    <xsl:variable name="error" select="/sc-web/head/meta[@type = 'error']"/>
    <xsl:variable name="query" select="/sc-web/head/query"/>
	<xsl:template name="sc_script">
	</xsl:template>
    <xsl:template name="sc_content">
      <div id="sc_login">
        <div id="sc_login_title">
           Service Connector Login
        </div>     
        <xsl:for-each select="$error">
          <div class="sc_error"><xsl:value-of select="@message"/></div>
        </xsl:for-each>
        <div id="sc_login_form" style="margin-top:20px;margin-left:30px;">
          <form name="loginForm" method="post" action="?action=login">
            <div style="float:left; padding:10px;">
	            <div class="sc_form" style="float:left; width:100px; text-align:right;">
	              <span class="sc_form_text">Userid :</span>
	            </div>
	            <div class="sc_form" style="float:left; margin-left:20px; text-align:left;">
	              <input class="sc_form_field" name="userid" type="text" value="{$query/param/@userid}" size="20"></input>
	            </div>
            </div>
            <div style="float:left; padding:10px;">
	            <div class="sc_form" style="float:left; width:100px; text-align:right;">
	              <span class="sc_form_text">Password :</span>
	            </div>
	            <div class="sc_form" style="float:left; margin-left:20px; text-align:left;">
	              <input class="sc_form_field" name="password" type="password" value="" size="20"></input>
	            </div>
            </div>
            <div style="float:left; padding:10px;">
	            <div class="sc_form" style="float:left; width:160px; text-align:right;">
	              <input class="sc_form_button" name="submit" type="submit" value="Login"></input>
	            </div>
	            <div class="sc_form" style="float:left; margin-left:20px; text-align:left;">
	              <input class="sc_form_button" name="reset" type="reset" value="Reset"></input>
	            </div>
            </div>
          </form>
        </div>   
      </div>
    </xsl:template>
    <xsl:template name="sc_menu">
    </xsl:template>
    <xsl:template name="sc_navigation">
	</xsl:template>
    <xsl:template name="sc_info">
	</xsl:template>
</xsl:stylesheet>
