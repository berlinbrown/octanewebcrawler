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

import java.awt.Dimension;
import java.util.Date;

import javax.swing.JFrame;

import org.berlin.swing.ui.ICloser;
import org.berlin.swing.ui.app.BasicAppBaseUI.AbstractWindowBuilder;
import org.berlin.swing.ui.app.BasicAppBaseUI.BasicWindow;
import org.berlin.swing.ui.app.BasicAppBaseUI.IBasicWindow;
import org.berlin.swing.ui.app.BasicAppCore.WindowBuilder;

/** 
 * Basic Java swing application, modify the basic app classes
 * for your particular task and application.
 * 
 * @author berlin.brown 
 */
public class BasicApp implements ICloser {

    private JFrame frame;
    
    public static final String TIME = "";
    public static final String FILE = "scan_timestamps_global.txt";
    public static final String DIR  = "C:\\usr\\local\\";
   
    /**
     * Create application.
     * Do not extend the JFrame class.
     */
    public void createApplication() {
        
        this.frame = new JFrame("ScanLogs - " + new Date());                      
        final IBasicWindow window = new BasicWindow(); 
        final AbstractWindowBuilder windowBuilder = new WindowBuilder(window);
        windowBuilder.setCloser(this);
        windowBuilder.build();        
        System.out.println("Window Data : " + window);        
        frame.add(window.getComponent());
        frame.setLocation(200, 200);
        frame.setPreferredSize(new Dimension(900, 760)); 
        frame.setSize(new Dimension(900, 760));
        frame.pack();
        frame.setResizable(true);        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
        frame.setVisible(true);         
        this.startServer();
    }
    
    public void close() {
        if (frame != null) {
            System.out.println("Closer operation invoked, closing");
            frame.setVisible(false);
            frame.dispose();
            System.exit(0);
        }
    }
    
    protected void startServer() {
      try {
        final ThreadEvent resultsReady = new ThreadEvent();
        final Runnable r = new Runnable() {
          @Override
          public void run() {
            System.out.println("Starting server thread");           
            // Normally when we are done with server,
            resultsReady.signal();
          };
        };
        new Thread(r).start();
        resultsReady.await();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    public static class ThreadEvent {

      private final Object lock = new Object();

      public void signal() {
        synchronized (lock) {
          lock.notify();
        }
      }

      public void await() throws InterruptedException {
        synchronized (lock) {
          lock.wait();
        }
      }
    }
    
    /*
     * To Run with maven:
     * mvn exec:java -Dexec.mainClass="org.berlin.swing.ui.app.BasicApp"  
     * mvn exec:java -Dexec.mainClass="com.Main" -Dexec.args="arg0 arg1 arg2"  
     */    
    
    /**     
     * @param args
     */
    private static void main(final String [] args) {        
        final BasicApp app = new BasicApp();        
        app.createApplication();
    }       
    
} // End of the class
