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
 * Description: Extend this customizable Swing wrapper library.
 * Home Page: http://code.google.com/u/berlin.brown/
 * 
 * Contact: Berlin Brown <berlin dot brown at gmail.com>
 */

package org.berlin.swing.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.berlin.swing.ui.Components.IButton;
import org.berlin.swing.ui.Components.ITextArea;

/**
 * 
 * @author berlin.brown
 * 
 */
public class Components {

	/**
	 * Library Interface.
	 */
	public static interface IWidget {

		/**
		 * Sets the text of this <code>TextComponent</code> to the specified text.
		 */
		public void setText(final String text);

		/**
		 * Gets the text of this <code>TextComponent</code> to the specified text.
		 */
		public String getText();

		public JComponent getComponent();

	} // End of the Interface //

	/**
	 * Library Interface.
	 */
	public static interface IActionHandler {

		/**
		 * @return the window
		 */
		public IWindow getWindow();

	}

	/**
	 * Library interface.
	 */
	public interface ITextBox extends IWidget {

	}

	/**
	 * Library Interface.
	 */
	public static interface IButton extends IWidget {

		public void setText(final String text);

		public String getText();

		public void addEventHandler();

		public JComponent getComponent();

		/**
		 * Directly invoke the event handler.
		 */
		public void onEvent();

		/**
		 * @return the window
		 */
		public IWindow getWindow();

	} // End of the Interface //

	/**
	 * Library Interface.
	 */
	public interface IFrame extends IWidget {

		public void addWindowListener(final WindowAdapter winAdapter);

	} // End of the Interface //

	/**
	 * Library Interface.
	 */
	public static interface ILayout {

		/**
		 * Method defaultSettings.
		 */
		public void defaultSettings();

		/**
		 * Move the position right.
		 * 
		 * @return GridBagConstraints
		 */
		public GridBagConstraints shiftRight();

		/**
		 * Method shiftDown.
		 * 
		 * @return GridBagConstraints
		 */
		public GridBagConstraints shiftDown();

		/**
		 * @param layout
		 *            the layout to set
		 */
		public void setLayout(final GridBagLayout layout);

		/**
		 * @return the layout
		 */
		public GridBagLayout getLayout();

		/**
		 * @return the constraints
		 */
		public GridBagConstraints getConstraints();

		/**
		 * @param constraints
		 *            the constraints to set
		 */
		public void setConstraints(final GridBagConstraints constraints);

	} // End of the interface //

	/**
	 * Library Interface.
	 */
	public static interface IPanel extends IWidget {

		/**
		 * Method addButton.
		 * 
		 * @param button
		 *            IButton
		 */
		public void addButton(IButton button);

		/**
		 * Method addTextArea.
		 * 
		 * @param text
		 *            ITextArea
		 */
		public void addTextArea(ITextArea text);

		/**
		 * Method getLayout.
		 * 
		 * @return ILayout
		 */
		public ILayout getLayout();

		/**
		 * Construct the layout with the internal swing components.
		 */
		public void constructView();

	} // End of Interface //

	/**
	 * Library Interface.
	 */
	public static interface ITextArea extends IWidget {

		/**
		 * Method setText.
		 * 
		 * @param text
		 *            String
		 */
		public void setText(final String text);

		/**
		 * Method getText.
		 * 
		 * @return String
		 */
		public String getText();

		/**
		 * Method getComponent.
		 * 
		 * @return JComponent
		 */
		public JComponent getComponent();

		/**
		 * Method setColumnsAndRows.
		 * 
		 * @param cols
		 *            int
		 * @param rows
		 *            int
		 */
		public void setColumnsAndRows(final int cols, final int rows);

		/**
		 * Method setLineWrap.
		 * 
		 * @param lineWrap
		 *            boolean
		 */
		public void setLineWrap(final boolean lineWrap);

		/**
		 * Method setCaretPosition.
		 * 
		 * @param pos
		 *            int
		 */
		public void setCaretPosition(final int pos);

		/**
		 * Method setEditable.
		 * 
		 * @param b
		 *            boolean
		 */
		public void setEditable(final boolean b);

		/**
		 * Method defaultSettings.
		 */
		public void defaultSettings();

	} // End of the Interface //

	/**
	 * Library Interface.
	 */
	public static interface IWindow extends IWidget {

		public IActionHandler getActionHandler();

		/**
		 * @param actionHandler
		 *            the actionHandler to set
		 */
		public void setActionHandler(IActionHandler actionHandler);

	} // End of the Class //

	/**
	 * Library Interface.
	 */
	public static interface IWindowBuilder {

		public IWindow build();

		public IWindow getBasicWindow();

	} // End of the Class //

	/**
	 * Library Interface.
	 */
	public static class Button implements IButton {

		private final JButton button;

		private final IEventWorker eventWorker;

		private final IWindow window;

		public Button(final JButton button, final IEventWorker eventWorker, final IWindow window) {
			this.button = button;
			this.eventWorker = eventWorker;
			this.window = window;
		}

		/**
		 * Sets the object's text.
		 * 
		 * @param text
		 *            the string used to set the text
		 * @see #getText
		 * 
		 */
		public void setText(final String text) {
			this.button.setText(text);
		}

		/**
		 * Returns the objects's text.
		 * 
		 * @return the object text
		 * @see #setText
		 */
		public String getText() {
			return this.button.getText();
		}

		public void addEventHandler() {
			// Build a listener based on the swing component.
			this.button.addActionListener(this.eventWorker.buildListener(this));
		}

		/**
		 * Directly invoke the event handler.
		 */
		public void onEvent() {

		}

		public JComponent getComponent() {
			return this.button;
		}

		/**
		 * @return the window
		 */
		public IWindow getWindow() {
			return window;
		}

	} // End of the Interface //

	/**
	 * Library Interface.
	 */
	public static class Frame implements IFrame {

		/**
		 * Method addWindowListener.
		 * 
		 * @param winAdapter
		 *            WindowAdapter
		 * @see swing.IFrame#addWindowListener(WindowAdapter)
		 */
		public void addWindowListener(final WindowAdapter winAdapter) {

		}

		/**
		 * Returns the objects's text.
		 * 
		 * @return the object text
		 * @see #setText
		 */
		public String getText() {
			return "";
		}

		public void setText(String text) {

		}

		public JComponent getComponent() {
			return null;
		}

	} // End of the Interface //

	/**
	 * Library Interface.
	 */
	public static abstract class Panel implements IPanel {

		private final ILayout layout;

		private final JPanel panel;

		/**
		 * Constructor for Panel.
		 * 
		 * @param panel
		 *            JPanel
		 * @param layout
		 *            ILayout
		 */
		public Panel(final JPanel panel, final ILayout layout) {
			this.panel = panel;
			this.layout = layout;
		}

		/**
		 * Method addButton.
		 * 
		 * @param button
		 *            IButton
		 * @see swing.IPanel#addButton(IButton)
		 */
		public void addButton(IButton button) {

		}

		/**
		 * Method addTextArea.
		 * 
		 * @param text
		 *            ITextArea
		 * @see swing.IPanel#addTextArea(ITextArea)
		 */
		public void addTextArea(ITextArea text) {

		}

		/**
		 * Returns the objects's text.
		 * 
		 * @return the object text
		 * @see #setText
		 */
		public String getText() {
			return "";
		}

		public void setText(String text) {

		}

		/**
		 * Method toString.
		 * 
		 * @return String
		 */
		public String toString() {
			return String.format("#{Panel: %s}", this.layout);
		}

		/**
		 * @return the layout
		 * @see swing.IPanel#getLayout()
		 */
		public ILayout getLayout() {
			return layout;
		}

		/**
		 * @return the panel
		 * @see swing.IPanel#getComponent()
		 */
		public JComponent getComponent() {
			return this.panel;
		}

	} // End of the Class //

	/**
	 * Library interface.
	 */
	public static class TextArea implements ITextArea {

		private final JTextArea textArea;

		/**
		 * Constructor for TextArea.
		 * 
		 * @param textArea
		 *            JTextArea
		 */
		public TextArea(final JTextArea textArea) {
			this.textArea = textArea;
		}

		/**
		 * Method getComponent.
		 * 
		 * @return JComponent
		 * @see swing.ITextArea#getComponent()
		 */
		public JComponent getComponent() {
			return this.textArea;
		}

		/**
		 * Returns the text contained in this <code>TextComponent</code>. If the
		 * underlying document is <code>null</code>, will give a
		 * <code>NullPointerException</code>.
		 * 
		 * @return String
		 * @see swing.ITextArea#getText()
		 */
		public String getText() {
			synchronized (this.textArea) {
				return this.textArea.getText();
			}
		}

		/**
		 * Sets the text of this <code>TextComponent</code> to the specified text.
		 * 
		 * @param text
		 *            String
		 * @see swing.ITextArea#setText(String)
		 */
		public void setText(String text) {
			synchronized (this.textArea) {
				this.textArea.setText(text);
			}
		}

		/**
		 * Method setColumnsAndRows.
		 * 
		 * @param cols
		 *            int
		 * @param rows
		 *            int
		 * @see swing.ITextArea#setColumnsAndRows(int, int)
		 */
		public void setColumnsAndRows(final int cols, final int rows) {
			this.textArea.setColumns(cols);
			this.textArea.setRows(rows);
		}

		/**
		 * Method setLineWrap.
		 * 
		 * @param lineWrap
		 *            boolean
		 * @see swing.ITextArea#setLineWrap(boolean)
		 */
		public void setLineWrap(final boolean lineWrap) {
			this.textArea.setLineWrap(lineWrap);
		}

		/**
		 * Method setCaretPosition.
		 * 
		 * @param pos
		 *            int
		 * @see swing.ITextArea#setCaretPosition(int)
		 */
		public void setCaretPosition(final int pos) {
			this.textArea.setCaretPosition(pos);
		}

		/**
		 * Method setEditable.
		 * 
		 * @param b
		 *            boolean
		 * @see swing.ITextArea#setEditable(boolean)
		 */
		public void setEditable(final boolean b) {
			this.textArea.setEditable(b);
		}

		/**
		 * Method defaultSettings.
		 * 
		 * @see swing.ITextArea#defaultSettings()
		 */
		public void defaultSettings() {

			this.setColumnsAndRows(70, 30);
			this.setLineWrap(false);
			this.setCaretPosition(0);
			this.setEditable(true);

		}

	} // End of the Class //

	/**
	 * Library Interface.
	 */
	public static class TextBox implements ITextBox {

		/**
		 * Returns the objects's text.
		 * 
		 * @return the object text
		 * @see #setText
		 */
		public String getText() {
			return "";
		}

		/**
		 * Sets the text of this <code>TextComponent</code> to the specified text.
		 * 
		 * @param text
		 *            String
		 * @see swing.ITextArea#setText(String)
		 */
		public void setText(String text) {

		}

		public JComponent getComponent() {
			return null;
		}

	} // End of the class

	/**
	 * Default teeter layout.
	 * 
	 * @author berlin.brown
	 *
	 */
	public static class DefaultLayout implements ILayout {

		private GridBagLayout layout = new GridBagLayout();
		private GridBagConstraints constraints = new GridBagConstraints();

		/**
		 * Method defaultSettings.
		 * 
		 * @see org.berlin.seesaw.swing.layout.ITeeterLayout#defaultSettings()
		 */
		public void defaultSettings() {

			final Insets insets = new Insets(2, 2, 2, 2);
			constraints.insets = insets;
			constraints.anchor = GridBagConstraints.NORTHWEST;
			constraints.gridy = 0;
			constraints.gridx = 0;
			// Specifies how to distribute extra horizontal space.
			constraints.weightx = 1;
			constraints.weighty = 1;
			constraints.fill = GridBagConstraints.BOTH;
		}

		/**
		 * Move the position right.
		 * 
		 * @return GridBagConstraints
		 * @see org.berlin.seesaw.swing.layout.ITeeterLayout#shiftRight()
		 */
		public GridBagConstraints shiftRight() {
			constraints.gridx = constraints.gridx + 1;
			return constraints;
		}

		/**
		 * Move the position down.
		 * 
		 * @return GridBagConstraints
		 * @see org.berlin.seesaw.swing.layout.ITeeterLayout#shiftDown()
		 */
		public GridBagConstraints shiftDown() {
			constraints.gridy = constraints.gridy + 1;
			return constraints;
		}

		/**
		 * @param layout
		 *            the layout to set
		 * @see org.berlin.seesaw.swing.layout.ITeeterLayout#setLayout(GridBagLayout)
		 */
		public void setLayout(final GridBagLayout layout) {
			this.layout = layout;
		}

		/**
		 * @return the layout
		 * @see org.berlin.seesaw.swing.layout.ITeeterLayout#getLayout()
		 */
		public GridBagLayout getLayout() {
			return layout;
		}

		/**
		 * @return the constraints
		 * @see org.berlin.seesaw.swing.layout.ITeeterLayout#getConstraints()
		 */
		public GridBagConstraints getConstraints() {
			return constraints;
		}

		/**
		 * @param constraints
		 *            the constraints to set
		 * @see org.berlin.seesaw.swing.layout.ITeeterLayout#setConstraints(GridBagConstraints)
		 */
		public void setConstraints(final GridBagConstraints constraints) {
			this.constraints = constraints;
		}

	} // End of the Class //

	public static final class Mutable<T> {
		private T mutable;
		/**
		 * Constructor.
		 * 
		 * @param m
		 */
		public Mutable(final T m) {
			this.mutable = m;
		}
		/**
		 * Set the mutable.
		 * 
		 * @param val
		 * @return
		 */
		public synchronized T set(final T val) {
			this.mutable = val;
			return mutable;
		}
		public synchronized T get() {
			return mutable;
		}
		@Override
		public String toString() {
			return String.valueOf(mutable);
		}
	}

} // End of the class
