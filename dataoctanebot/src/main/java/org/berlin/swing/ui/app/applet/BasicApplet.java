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
package org.berlin.swing.ui.app.applet;

import java.awt.Dimension;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

import org.berlin.swing.ui.ICloser;
import org.berlin.swing.ui.app.BasicAppBaseUI.AbstractWindowBuilder;
import org.berlin.swing.ui.app.BasicAppBaseUI.BasicWindow;
import org.berlin.swing.ui.app.BasicAppBaseUI.IBasicWindow;
import org.berlin.swing.ui.app.BasicAppCore.WindowBuilder;

/**
 * Basic swing applet.
 * 
 * @author berlin.brown
 *
 */
public class BasicApplet extends JApplet implements ICloser {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 1L;
	public void init() {
		// Execute a job on the event-dispatching thread; creating this applet's GUI.
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					final IBasicWindow window = new BasicWindow();
					final AbstractWindowBuilder windowBuilder = new WindowBuilder(window);
					windowBuilder.setCloser(BasicApplet.this);
					windowBuilder.build();
					BasicApplet.this.setLocation(0, 0);
					BasicApplet.this.setPreferredSize(new Dimension(500, 460));
					BasicApplet.this.setVisible(true);
					BasicApplet.this.add(window.getComponent());
				}
			});
		} catch (Exception e) {
			System.err.println("createGUI didn't complete successfully");
		}
	}

	public void close() {
	}

} // End of the class
