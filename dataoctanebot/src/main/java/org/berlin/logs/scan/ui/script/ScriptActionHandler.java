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
 * Date: 12/15/2009 
 *   
 * Home Page: http://code.google.com/u/berlin.brown/
 * 
 * Contact: Berlin Brown <berlin dot brown at gmail.com>
 */
package org.berlin.logs.scan.ui.script;

import org.berlin.logs.scan.ui.script.ScriptInterpreter.Mutable;
import org.berlin.swing.ui.AbstractAction;
import org.berlin.swing.ui.Components.IWindow;
import org.berlin.swing.ui.app.BasicAppBaseUI.BasicWindow;

/** 
 * Basic Java swing application, modify the basic app classes
 * for your particular task and application.
 * 
 * @author berlin.brown 
 */
public class ScriptActionHandler extends AbstractAction {
    
    private final BasicWindow window;
    
    public static final char NL = '\n';
    
    private Mutable<Boolean> inProcessRunningCommand = new Mutable<Boolean>(false);
    
    /**
     * Basic application action.
     * 
     * @param window
     */
    public ScriptActionHandler(final IWindow window) { 
        super(window);
        this.window = (BasicWindow) window;
    }
    
    /**    
     * Basic application.
     */
    public synchronized void handleOnButtonEnter() {        
        
        System.out.println("InputText at Command : " + this.window.getInputTextArea().getText());
        final StringBuffer bufOut = new StringBuffer();        
        bufOut.append(this.window.getOutputTextArea().getText());
        bufOut.append(NL);
        bufOut.append("Last Doman Language Input Script : ").append(NL + this.window.getInputTextArea().getText());
        bufOut.append(NL);        
        try {            
            new ScriptInterpreter(this.window, bufOut, inProcessRunningCommand).interpret();           
        } catch(final Exception e) {
            e.printStackTrace();            
        }        
        // Set text //
        this.window.getOutputTextArea().setText(bufOut.toString());       
    }
    
    public synchronized void handleOnButtonClear() {                      
        this.window.getOutputTextArea().setText("");               
    }        
} // End of the class
