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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.LayoutManager;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.berlin.logs.scan.ui.script.ScriptActionHandler;
import org.berlin.swing.ui.BaseWorker;
import org.berlin.swing.ui.Components.Button;
import org.berlin.swing.ui.Components.DefaultLayout;
import org.berlin.swing.ui.Components.IButton;
import org.berlin.swing.ui.Components.ILayout;
import org.berlin.swing.ui.Components.IPanel;
import org.berlin.swing.ui.Components.ITextArea;
import org.berlin.swing.ui.Components.Panel;
import org.berlin.swing.ui.Components.TextArea;
import org.berlin.swing.ui.ICloser;
import org.berlin.swing.ui.IEventWorker;
import org.berlin.swing.ui.app.BasicAppBaseUI.AbstractWindowBuilder;
import org.berlin.swing.ui.app.BasicAppBaseUI.BasicWindow;
import org.berlin.swing.ui.app.BasicAppBaseUI.IBasicWindow;

/**
 * Basic Swing UI Application.
 */
public class BasicAppCore {

    /**
     * Log Analyzer Panel.
     * 
     * @author berlin.brown     
     */
    public static class LogAnalyzerPanel extends Panel {        
        private final IBasicWindow window;

        public LogAnalyzerPanel(final IBasicWindow window, final JPanel panel, final ILayout layout) {
            super(panel, layout);
            this.window = window;
        }

        public void constructView() {            
            final GridBagConstraints constraints = this.getLayout().getConstraints();                    
            // Add a scroll pane for the chat area //                
            final JScrollPane outputPane = new JScrollPane(this.window.getOutputTextArea().getComponent());        
            final JScrollPane inputPane = new JScrollPane(this.window.getInputTextArea().getComponent());            
            outputPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            outputPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);            
            inputPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            inputPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);                                
            this.getComponent().add(outputPane, constraints);
            this.getLayout().shiftDown();            
            this.getComponent().add(inputPane, constraints);
            this.getLayout().shiftDown();            
            // Add the button panel  
            constraints.weighty = 0;
            this.getComponent().add(this.window.getButtonPanel().getComponent(), constraints);                    
        }

    } // End of the Class //
       
    /**    
     * @author berlin.brown     
     */
    public static class WindowBuilder extends AbstractWindowBuilder {        
        private ICloser closer = null;        
        /**
         * Constructor for WindowBuilder.
         * @param basicWindow IBasicWindow
         */
        public WindowBuilder(final IBasicWindow basicWindow) {
            super(basicWindow);
        }
        
        /**
         * Method createEnterButton.
         * @return IButton
         */
        public IButton createEnterButton() {           
            final IEventWorker eventWorker = new BaseWorker() {
                public void execute() {                    
                    final IButton button = (IButton) this.getMasterParent();                
                    final ScriptActionHandler action = (ScriptActionHandler) button.getWindow().getActionHandler();
                    action.handleOnButtonEnter();               
                }
            };        
            final IButton button = new Button(new JButton("Execute"), eventWorker, this.getBasicWindow());
            button.addEventHandler();
            return button;
        }
        
        /**
         * Method createClearButton.
         * @return IButton
         */
        public IButton createClearButton() {            
            final IEventWorker eventWorker = new BaseWorker() {
                public void execute() {                                        
                    System.out.println("Action - Clear - Output Window! [2]");
                    System.out.println(this.getLastEvent());                              
                    ((BasicWindow)getBasicWindow()).getOutputTextArea().setText("");                    
                }                
            };        
            final IButton button = new Button(new JButton("Clear"), eventWorker, this.getBasicWindow());
            button.addEventHandler();
            return button;
        }
        
        /**
         * Method createExitButton.
         * @return IButton
         */
        public IButton createExitButton() {
            
            final IEventWorker eventWorker = new BaseWorker() {
                public void execute() {                    
                    System.out.println("Shutting Down Application");                    
                    if (closer != null) {
                        closer.close();
                    } else {
                        System.out.println("WARN: Invalid closer, could not close application");
                    }                                                                   
                }
            };        
            final IButton button = new Button(new JButton("Exit"), eventWorker, this.getBasicWindow());
            button.addEventHandler();
            return button;
        }
        
        /**
         * Method withOutputTextArea.
         * @return WindowBuilder
         */
        public WindowBuilder withOutputTextArea() {           
            final JTextArea ta = new JTextArea("");           
            
            final Font font = new Font("Courier New", Font.BOLD, 14);
            ta.setFont(font);            
            ta.setForeground(Color.green);
            ta.setBackground(Color.black);
            
            final ITextArea textArea = new OutputTextArea(ta);
            textArea.defaultSettings();
            ta.setEditable(false);
            ((BasicWindow) this.getBasicWindow()).setOutputTextArea(textArea); 
            textArea.setText("System Loaded - " + (new Date()) + "\nWith action.command set to help, hit the execute button for help.\nAlso visit browser at http://localhost:7181");      
            return this;
        }
        
        /**
         * Method withInputCommandArea.
         * @return WindowBuilder
         */
        public WindowBuilder withInputCommandArea() {
            
            final JTextArea ta = new JTextArea("");
            final ITextArea textArea = new CommandInputArea(ta);                                   
            textArea.defaultSettings();
            
            final Font font = new Font("Courier New", Font.BOLD, 14);
            ta.setFont(font);                                   
            
            ((BasicWindow) this.getBasicWindow()).setInputTextArea(textArea);
            
            // Add initial input //
            final StringBuffer buf = new StringBuffer();
            buf.append("###############\n");
            buf.append("# Log Script DSL/Doman Language Input (property conf format)\n");
            buf.append("# Modify action.comand=XXXXX and then click execute to invoke a command\n");
            buf.append("#        action.comand=help  for standard commands\n");
            buf.append("###############\n\n");        
            buf.append("#unzipArchiveFiles=false\n\n");
            
            buf.append("user.searchTerm=Exception\n");
            buf.append("action.command=help\n");
            textArea.setText(buf.toString());            
            return this;
        }           
        
        /**
         * Method withButtonPanel.
         * @return WindowBuilder
         */
        public WindowBuilder withButtonPanel() {            
            final IButton enterButton = this.createEnterButton();
            final IButton clearButton = this.createClearButton();
            final IButton exitButton = this.createExitButton();           
            final ILayout layout = new DefaultLayout();
            layout.defaultSettings();
            layout.getConstraints().weighty = 0;            
            final JPanel swingButtonPanel = new JPanel(layout.getLayout());                        
            final IPanel panel = new CommandButtonPanel(swingButtonPanel, layout, enterButton, clearButton, exitButton);            
            panel.constructView();            
            // Set the components on the window //
            ((BasicWindow) this.getBasicWindow()).setButtonEnter(enterButton);
            ((BasicWindow) this.getBasicWindow()).setButtonClear(clearButton);
            ((BasicWindow) this.getBasicWindow()).setButtonExit(exitButton);
            ((BasicWindow) this.getBasicWindow()).setButtonPanel(panel);            
            return this;
        }
                
        /**
         * Method withMainPanel.
         * @return WindowBuilder
         */
        public WindowBuilder withMainPanel() {
                    
            this.withOutputTextArea();
            this.withInputCommandArea();
            this.withButtonPanel();
            
            /////////////////////////////////////////
            // Create a new panel 
            // with the default layout
            /////////////////////////////////////////
            final ILayout layout = new DefaultLayout();
            layout.defaultSettings();
           
            final JPanel swingPanel = new JPanelResize(layout.getLayout());
            final IPanel panel = new LogAnalyzerPanel((IBasicWindow) this.getBasicWindow(), swingPanel, layout);
            panel.constructView();            
            ((BasicWindow) this.getBasicWindow()).setWindowPanel(panel);
            return this;
        }
        
        /**
         * Method build.
         * @return IBasicWindow
         * @see app.IWindowBuilder#build()
         */
        @Override
        public IBasicWindow build() {            
            this.withMainPanel();
            return (IBasicWindow) this.getBasicWindow();            
        }

        public ICloser getCloser() {
            return closer;
        }

        public void setCloser(final ICloser closer) {
            this.closer = closer;
        }        
        
        public Map<Integer, List<Double>> createBuckets() {            
            final Map<Integer, List<Double>> bucket = new HashMap<Integer, List<Double>>();
            for (int i = 0; i < (24 * 60); i++) {
                bucket.put(i, new ArrayList<Double>());
            }
            return Collections.unmodifiableMap(bucket);
        }
        
        
                
    } // End of the Class //
    
    /**
     * Main Render window.
     * 
     * @author berlin.brown
     *
     */
    public static class RenderChartPanel extends Panel {
   
        public RenderChartPanel(final JPanel panel, final ILayout layout) {            
            super(panel, layout);   
        }
        
        /**
         * Construct the layout with the internal swing components.     
         */
        public void constructView() {                   
            System.out.println("Constructing initial components for render chart panel");
            final JPanel panel = (JPanel) super.getComponent();                                   
            final GridBagConstraints constraints = this.getLayout().getConstraints();
            panel.setLayout(this.getLayout().getLayout());            
            panel.setPreferredSize(new Dimension(900, 300)); 
            panel.setSize(new Dimension(900, 300));
            constraints.fill    = GridBagConstraints.BOTH;
            constraints.anchor  = GridBagConstraints.NORTHWEST;
            constraints.weightx = 1;
            constraints.weighty = 1;
            panel.setVisible(true);
        }        
        
        /**
         * @return the panel
         * @see swing.IPanel#getComponent()
         */
        public JComponent getComponent() {            
            return super.getComponent();
        }    
                
    } // End of the Class //

    public static class OutputTextArea extends TextArea {

        /**
         * Constructor for OutputTextArea.
         * @param textArea JTextArea
         */
        public OutputTextArea(JTextArea textArea) {
            super(textArea);
        }

        /**
         * Method defaultSettings.
         * @see swing.ITextArea#defaultSettings()
         */
        @Override
        public void defaultSettings() {
            this.setColumnsAndRows(80, 20);
            this.setLineWrap(false);
            this.setCaretPosition(0);
            this.setEditable(true);            
        }
                
    } // End of the Class //

    public static class JPanelResize extends JPanel implements ComponentListener {
        
        private Dimension size;

        public JPanelResize(final LayoutManager layout) {
            super(layout);
            this.addComponentListener(this);        
        }
        
        public void paintComponent(Graphics g) {
            System.out.println("paintComponent");
        }

        public void componentResized(ComponentEvent e) {
            // Perform calculation here
            System.out.println("componentResized");
        }
        public void componentHidden(ComponentEvent e) { }
        public void componentMoved(ComponentEvent e) { }
        public void componentShown(ComponentEvent e) { }
        
    } // End of the Class //

    /**
     * Command Input Area.    
     * @author berlin.brown     
     */
    public static class CommandInputArea extends TextArea {

        /**
         * Constructor for CommandInputArea.
         * @param textArea JTextArea
         */
        public CommandInputArea(JTextArea textArea) {
            super(textArea);
        }

        /**
         * Method defaultSettings.
         * @see swing.ITextArea#defaultSettings()
         */
        @Override
        public void defaultSettings() {                 
            this.setColumnsAndRows(80, 3);
            this.setLineWrap(false);
            this.setCaretPosition(0);
            this.setEditable(true);           
        }        
        
    } // End of the Class //

    /**
     * Command Button Panel.
     */
    public static class CommandButtonPanel extends Panel {
        
        private final IButton buttonEnter;    
        private final IButton buttonClear;    
        private final IButton buttonExit;
        
        public CommandButtonPanel(final JPanel panel, final ILayout layout,  final IButton enterButton, final IButton clearButton,  final IButton exitButton) {            
            super(panel, layout);
            this.buttonEnter = enterButton;   
            this.buttonClear = clearButton;
            this.buttonExit  = exitButton;        
        }
        
        /**
         * Construct the layout with the internal swing components.     
         */
        public void constructView() {
            
            final GridBagConstraints constraints = this.getLayout().getConstraints();
            
            // Add the enter button        
            this.getComponent().add(this.buttonEnter.getComponent(), constraints);
            this.getLayout().shiftRight();
            
            // Add the clear button        
            this.getComponent().add(this.buttonClear.getComponent(), constraints);
            this.getLayout().shiftRight();
            
            // Add exit
            this.getComponent().add(this.buttonExit.getComponent(), constraints);
            this.getLayout().shiftRight();            
        }               
    } // End of the Class //
    
} // End of the class
