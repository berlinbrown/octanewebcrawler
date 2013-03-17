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
package org.berlin.swing.ui.app;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.berlin.logs.scan.ui.script.ScriptActionHandler;
import org.berlin.swing.ui.Components.IActionHandler;
import org.berlin.swing.ui.Components.IButton;
import org.berlin.swing.ui.Components.ILayout;
import org.berlin.swing.ui.Components.IPanel;
import org.berlin.swing.ui.Components.ITextArea;
import org.berlin.swing.ui.Components.IWindow;
import org.berlin.swing.ui.ICloser;

/**
 * Library interface.
 */
public class BasicAppBaseUI {
    
    /**
     * Library interface.
     */
    public static class BasicFrame {
        public BasicFrame(final IBasicWindow window) {

        }
    } // End of the Class //

    /**
     * Library interface.     
     */
    public static abstract class AbstractWindowBuilder implements IWindowBuilder {
        private final IBasicWindow basicWindow;

        public AbstractWindowBuilder(final IBasicWindow basicWindow) {
            this.basicWindow = basicWindow;
        }
        public abstract IBasicWindow build();

        /**
         * @return the basicWindow
         */
        public IWindow getBasicWindow() {
            return this.basicWindow;
        }
    } // End of the Class //

    /**
     * Library interface.
     */
    public static interface IBasicWindow extends IWindow {

        public void setWindowPanel(final IPanel panel);
        public void setOutputTextArea(final ITextArea textArea);
        public void setInputTextArea(final ITextArea inputTextArea);
        public void setButtonPanel(final IPanel buttonPanel);
        public void setButtonEnter(final IButton buttonEnter);
        public void setButtonClear(final IButton buttonClear);
        public void setButtonExit(final IButton buttonExit);
        public void setLayout(ILayout layout);
        public void setTestSelectPanel(final IPanel testSelectPanel);

        public ITextArea getOutputTextArea();

        /**
         * @return the inputTextArea
         */
        public ITextArea getInputTextArea();

        /**
         * @return the buttonPanel
         */
        public IPanel getButtonPanel();

        /**
         * @return the buttonPanel
         */
        public IPanel getTestSelectPanel();

    } // End of the Interface //

    /**    
     * @author NNN    
     */
    public static class BasicWindow implements IBasicWindow {

        private ILayout layout;
        private IPanel windowPanel;
        private ITextArea outputTextArea;
        private ITextArea inputTextArea;
        private IPanel buttonPanel;
        private IButton buttonEnter;
        private IButton buttonClear;
        private IButton buttonExit;
        private IPanel testSelectPanel;

        private IActionHandler actionHandler = new ScriptActionHandler(this);
                        
        /**
         * Method toString.
         * 
         * @return String
         */
        public String toString() {
            return String.format("#{Basic Window: %s - %s}", super.toString(), this.windowPanel);
        }

        public void setWindowPanel(final IPanel windowPanel) {
            this.windowPanel = windowPanel;
        }

        public void setOutputTextArea(final ITextArea textArea) {
            this.outputTextArea = textArea;
        }

        public void setInputTextArea(final ITextArea inputTextArea) {
            this.inputTextArea = inputTextArea;
        }

        public void setButtonPanel(final IPanel buttonPanel) {
            this.buttonPanel = buttonPanel;
        }

        public void setButtonEnter(final IButton buttonEnter) {
            this.buttonEnter = buttonEnter;
        }

        public void setButtonClear(final IButton buttonClear) {
            this.buttonClear = buttonClear;
        }

        public void setButtonExit(final IButton buttonExit) {
            this.buttonExit = buttonExit;
        }

        /**
         * Method getComponent.
         * 
         * @return JComponent
         * @see org.berlin.seesaw.swing.IWidget#getComponent()
         */
        public JComponent getComponent() {
            return this.windowPanel.getComponent();
        }

        /**
         * @param layout
         *            the layout to set
         * @see win.base.IBasicWindow#setLayout(ILayout)
         */
        public void setLayout(ILayout layout) {
            this.layout = layout;
        }

        /**
         * @return the chatTextArea
         * @see win.base.IBasicWindow#getChatTextArea()
         */
        public ITextArea getOutputTextArea() {
            return outputTextArea;
        }

        /**
         * @return the inputTextArea
         * @see win.base.IBasicWindow#getInputTextArea()
         */
        public ITextArea getInputTextArea() {
            return inputTextArea;
        }

        /**
         * @return the buttonPanel
         * @see win.base.IBasicWindow#getButtonPanel()
         */
        public IPanel getButtonPanel() {
            return buttonPanel;
        }

        /**
         * @return the layout
         */
        public ILayout getLayout() {
            return layout;
        }

        /**
         * Method getText.
         * 
         * @return String
         * @see org.berlin.seesaw.swing.IWidget#getText()
         */
        public String getText() {
            return "";
        }

        /**
         * Method setText.
         * 
         * @param text
         *            String
         * @see org.berlin.seesaw.swing.IWidget#setText(String)
         */
        public void setText(String text) {

        }

        /**
         * @return the actionHandler
         * @see org.berlin.seesaw.app.IWindow#getActionHandler()
         */
        public IActionHandler getActionHandler() {
            return actionHandler;
        }

        /**
         * @param actionHandler
         *            the actionHandler to set
         * @see org.berlin.seesaw.app.IWindow#setActionHandler(IActionHandler)
         */
        public void setActionHandler(IActionHandler actionHandler) {
            this.actionHandler = actionHandler;
        }

        /**
         * @return the testSelectPanel
         */
        public IPanel getTestSelectPanel() {
            return testSelectPanel;
        }

        /**
         * @param testSelectPanel
         *            the testSelectPanel to set
         */
        public void setTestSelectPanel(IPanel testSelectPanel) {
            this.testSelectPanel = testSelectPanel;
        }

    } // End of the Class //
   
    /**
     * 
     * @author berlin.brown
     *
     */
    public static interface IWindowBuilder {
        public IWindow build();        
        public IWindow getBasicWindow();        
        public void setCloser(final ICloser closer);        
        public ICloser getCloser();
        
    } // End of the Class //
        
    /**
     * Library interface.     
     */
    public static class MainFrame extends JFrame {
        private static final long serialVersionUID = 8886912074222991557L;
    }

} // End of the class
