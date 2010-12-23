<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:template match="/">
      <xsl:text disable-output-escaping="yes">&lt;!--action:</xsl:text><xsl:value-of select="/system/action"/><xsl:text disable-output-escaping="yes">:action--&gt;</xsl:text>
      <xsl:text disable-output-escaping="yes">&lt;!--service:</xsl:text><xsl:value-of select="/system/service"/><xsl:text disable-output-escaping="yes">:service--&gt;</xsl:text>
      <table border="0" cellspacing="0" cellpadding="0" width="100%" class="sc_dialog_table">
        <tr>
          <th class="sc_dialog_table_header" style="width:20px;">&#160;</th>
          <th class="sc_dialog_table_header">Information [<xsl:value-of select="/system/status"/>]</th>
          <th class="sc_dialog_table_header" style="width:20px;"><img src="close.png" onclick="javascript:hideLayer('DialogBox')"></img></th>
        </tr>
        <xsl:for-each select="/system/messages/message">          
          <tr>
            <td class="sc_dialog_table_content" colspan="3">
              <xsl:value-of select="."/>   
            </td>
          </tr>
        </xsl:for-each>
      </table>
	</xsl:template>
</xsl:stylesheet>
