/**
 * Copyright (c) 2006-2011 Berlin Brown.  All Rights Reserved
 *
 * http://www.opensource.org/licenses/bsd-license.php
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * * Neither the name of the Botnode.com (Berlin Brown) nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Date: 8/15/2011
 *  
 * Description: LogFile Searcher.  Search log file and build statistics from the scan.
 * 
 * Contact: Berlin Brown <berlin dot brown at gmail.com>
 */
package org.berlin.logs.scan.filter.custom;

public class HTMLOutputLineFormatter {

  final String inputLine;
  final FilterLineInfo info;
  final HTML h = new HTML();
  
  public HTMLOutputLineFormatter(final String inputLine, final FilterLineInfo info) {
    this.inputLine = inputLine;
    this.info = info;
  }
  
  public String format() {        
    String newline = inputLine;
    final StringBuilder buf = new StringBuilder(300);    
            
    if ((info != null) && (info.javaDate != null) && (info.endSizePrefixForStrip > 0)) {                                     
      final String sid = String.valueOf((info.sessionIndex % (h.sessionBgColor.size()-1)) + 1);
      final String aid = String.valueOf((info.agentIndex % (h.agentBgColor.size()-1)) + 1);
      final String sidx = h.sessionBgColor.get(sid);
      final String aidx = h.agentBgColor.get(aid);
      
      buf.append("\n<tr>\n");
      buf.append("\n<td style='border-right: 1px solid #222; background-color: " + sidx + "'><div style='width:330px;'>");
      buf.append(info.javaDateFormatted);
      buf.append("</div></td>\n");
      
      buf.append("\n<td style='border-right: 1px solid #222; background-color: " + sidx + "'><div style='width:120px;'>");
      buf.append(info.timeOfSession);
      buf.append("</div></td>\n");
      
      buf.append("\n<td style='border-right: 1px solid #222; background-color: " + sidx + "'>");
      buf.append(info.lineIndex);
      buf.append("</td>\n");
      
      buf.append("\n<td style='border-right: 1px solid #222; background-color: " + sidx + "'>");
      buf.append("tId=").append(info.threadId);
      buf.append("</td>\n");
      
      buf.append("\n<td style='border-right: 1px solid #222; background-color: " + sidx + "'>");
      buf.append("session_"+info.sessionIndex+"=").append(info.sessionId);
      buf.append("</td>\n");
      
      buf.append("\n<td style='border-right: 1px solid #222; background-color: " + aidx + "'>");
      buf.append("agent_"+info.agentIndex+"=").append("");
      buf.append("</td>\n");
      
      final String data = newline.substring(info.endSizePrefixForStrip).trim();
      if (data.indexOf("ERROR") > 0) { 
        buf.append("\n<td style='border: 3px solid #f00; background-color: #ffdddd'>");       
      } else {
        if (data.endsWith("launch/")) {
          buf.append("\n<td style='border: 3px solid #f00; background-color: #ccccff'>");
        } else {
          buf.append("\n<td style='border-right: 1px solid #222; background-color: " + sidx + "'>");
        }
      }
      buf.append(newline.substring(info.endSizePrefixForStrip).trim());
      buf.append("</td>\n");           
      buf.append("\n</tr>\n");
           
    } else {
      buf.append("\n<tr>\n");
      buf.append("\n<td colspan='7' style='border: 3px solid #ff3355; background-color: #fdfdfd'><div style='padding: 6px'>");      
      buf.append(newline.trim());
      buf.append("</div></td>\n");  
      buf.append("\n</tr>\n");
    }      
       
    return buf.toString();
  }
  
} // End of the Class //
