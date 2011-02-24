<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:import href="template.xsl"/>
    <xsl:variable name="body" select="/sc-web/body"/>
    <xsl:variable name="head" select="/sc-web/head"/>
    <xsl:template name="sc_content">
      <div class="sc_table max_width">
        <div class="sc_table_title">
          SC Maintenance
        </div>
        <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
          <tr class="sc_table_even">
            <td id="sc_dump" style="float:left;width:200px;"><input class="sc_form_button" style="margin:10px;" name="SC Dump" type="button" value="SC Dump" onclick="javascript:scDump('{$urlencoded}')"></input></td> 
            <td id="sc_dump_list" style="float:left;width:200px;"><input class="sc_form_button" style="margin:10px;" name="SC Dump List" type="button" value="Show SC Dump List" onclick="javascript:maintenanceCall('{$urlencoded}', 'sc_dump_list')"></input></td> 
            <td id="sc_dump_delete" style="float:left;width:200px;"><input class="sc_form_button" style="margin:10px;" name="SC Dump" type="button" value="Delete All SC Dumps" onclick="javascript:scDumpDelete('{$urlencoded}', 'sc_dump_delete')"></input></td> 
            <td id="sc_terminate" style="float:left;width:200px;"><input class="sc_form_button" style="margin:10px;" name="Terminate SC" type="button" value="Terminate SC" onclick="javascript:terminateSC('{$urlencoded}')"></input></td> 
            <td id="sc_cache_clear" style="float:left;width:200px;"><input class="sc_form_button" style="margin:10px;" name="Terminate SC" type="button" value="Clear Cache" onclick="javascript:clearCache('{$urlencoded}')"></input></td> 
            <td id="sc_translet_reset" style="float:left;width:200px;"><input class="sc_form_button" style="margin:10px;" name="Terminate SC" type="button" value="Reset Translet" onclick="javascript:resetTranslet('{$urlencoded}')"></input></td>
          </tr>
        </table>
        <xsl:if test="$body/maintenance/services/service">          
          <table border="0" class="sc_table" cellspacing="0" cellpadding="0">
            <tr class="sc_table_even">
              <xsl:for-each select="$body/maintenance/services/service">
                <xsl:sort data-type="text" select="name"/>
                <xsl:if test="scDownloadService = 'true'">
                   <td id="sc_file_service_download_{name}" style="float:left"><input class="sc_form_button" style="margin:10px;" name="File Service" type="button" value="Property File Download - {name}" onclick="javascript:maintenanceCall('{$urlencoded}', 'sc_property_download', '{name}')"></input></td>
                </xsl:if>                
                <xsl:if test="scUploadService = 'true'">
                   <td id="sc_file_service_upload_{name}" style="float:left"><input class="sc_form_button" style="margin:10px;" name="File Service" type="button" value="Logile Upload - {name}" onclick="javascript:maintenanceCall('{$urlencoded}', 'sc_logs_upload', '{name}')"></input></td>
                </xsl:if> 
              </xsl:for-each>              
            </tr>
          </table>
        </xsl:if>
        <div id="sc_maintenance"></div>
      </div>
    </xsl:template>
	<xsl:template name="sc_menu_left"><xsl:call-template name="menu_separator"/><div class="sc_menu_item" onmouseover="javascript:setStyleOver(this)" onmouseout="javascript:setStyleOut(this)"><a class="sc_menu_item" href="./maintenance{$urlencoded}">Maintenance</a></div></xsl:template>
</xsl:stylesheet>
