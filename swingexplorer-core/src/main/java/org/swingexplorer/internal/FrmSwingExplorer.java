/*
 *   Swing Explorer. Tool for developers exploring Java/Swing-based application internals. 
 * 	 Copyright (C) 2012, Maxim Zakharenkov
 *
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *   
 */
package org.swingexplorer.internal;

import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;

import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.*;

/**
 *
 * @author  Maxim Zakharenkov
 */
public class FrmSwingExplorer extends JFrame {

	private PnlSwingExplorer pnlSwingExplorer;
	private JMenuBar menuBar;
	private JMenu mnuHelp;
	private JMenu mnuTheme;

    private ActHelpAbout actHelpAbout;
    private ActHelp actHelp;

    private JMenuItem showFlatUIInspector;
    private JMenuItem showFlatInspector;

    private JMenu flatThemes;

    private JMenuItem flatLight;
    private JMenuItem flatDark;
    private JMenuItem flatIntelliJ;
    private JMenuItem flatDarcula;
    private JMenuItem flatMetal;
    private JMenuItem flatNimbus;
    private JMenuItem flatWindows;

    private ControlBar controlBar;

	Application application;
	
	FrmSwingExplorer() {
		initComponents();
		initActions();
	}
	
	private void initComponents() {
		// initialize frame
        setTitle("Swing Explorer");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        // initialize main panel
        pnlSwingExplorer = new PnlSwingExplorer();
		this.add(pnlSwingExplorer);
		
        Image appImage = Icons.appSmallImage();
        this.setIconImage(appImage);

        // initialize menu
        menuBar = new JMenuBar(); 
        mnuHelp = new JMenu("Help");        
        mnuTheme = new JMenu("Theme");
        menuBar.add(mnuHelp);
        menuBar.add(mnuTheme);
        this.setJMenuBar(menuBar);

        controlBar = new ControlBar();
        JTabbedPane jTabbedPane = new JTabbedPane();
        controlBar.initialize(this, jTabbedPane);
        
        // doing the following operation through reflection
        // this.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        // this is done to support JRE 1.5 as well
        try {
			Class<?> exclusionType = Class.forName("java.awt.Dialog$ModalExclusionType");
			Field field = exclusionType.getField("APPLICATION_EXCLUDE");
			Object value = field.get(exclusionType);
			
			Method meth = JFrame.class.getMethod("setModalExclusionType", exclusionType);
			meth.invoke(this, value);
		} catch (Exception e) {
			Log.general.warn("Application exclusive modality is not available on this Java platform. It is recommended to use java 1.6 or later.");
		}
	}
	
	private void initActions() {
		actHelpAbout = new ActHelpAbout(this);
        actHelp = new ActHelp();
               
        mnuHelp.add(actHelp);
        mnuHelp.add(actHelpAbout);

        flatLight =    new JMenuItem("Flat Light");
        flatDark =     new JMenuItem("Flat Dark");
        flatIntelliJ = new JMenuItem("Flat IntelliJ");
        flatDarcula =  new JMenuItem("Flat Darcula");
        flatMetal =    new JMenuItem("Flat Metal");
        flatNimbus =   new JMenuItem("Flat Nimbus");
        flatWindows =  new JMenuItem("Flat Windows");

        flatThemes = new JMenu("Flat Dark");

        flatLight.addActionListener(e -> {
            if (!flatThemes.getText().equals("Flat Light")) {
                ControlBar.selectLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
                flatThemes.setText("Flat Light");
            }
        });
        flatDark.addActionListener(e -> {
            if (!flatThemes.getText().equals("Flat Dark")) {
                ControlBar.selectLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
                flatThemes.setText("Flat Dark");
            }
        });
        flatIntelliJ.addActionListener(e -> {
            if(!flatThemes.getText().equals("Flat IntelliJ")) {
                ControlBar.selectLookAndFeel("com.formdev.flatlaf.FlatIntelliJLaf");
                flatThemes.setText("Flat IntelliJ");
            }
        });
        flatDarcula.addActionListener(e -> {
            if(!flatThemes.getText().equals("Flat Darcula")) {
                ControlBar.selectLookAndFeel("com.formdev.flatlaf.FlatDarculaLaf");
                flatThemes.setText("Flat Darcula");
            }
        });

        flatMetal.addActionListener(e -> {
            if(!flatThemes.getText().equals("Flat MetaL")) {
                ControlBar.selectLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                flatThemes.setText("Flat mETAL");
            }
        });
        flatNimbus.addActionListener(e -> {
            if(!flatThemes.getText().equals("Flat Nimbus")) {
                ControlBar.selectLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                flatThemes.setText("Flat Nimbus");
            }
        });
        flatWindows.addActionListener(e -> {
            if(!flatThemes.getText().equals("Flat Windows")) {
                ControlBar.selectLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                flatThemes.setText("Flat Windows");
            }
        });


        flatThemes.add(flatLight);
        flatThemes.add(flatDark);
        flatThemes.add(flatIntelliJ);
        flatThemes.add(flatDarcula);
        flatThemes.add(flatMetal);
        flatThemes.add(flatNimbus);
        flatThemes.add(flatWindows);

        mnuTheme.add(flatThemes);

        showFlatInspector = new JMenuItem();
        showFlatInspector.setText("Show Inspector");
        showFlatInspector.addActionListener(e -> showInspector(this));

        showFlatUIInspector = new JMenuItem();
        showFlatUIInspector.setText("Show UI Default Inspector");
        showFlatUIInspector.addActionListener(e -> showUIDefaultsInspector());

        mnuTheme.add(showFlatInspector);
        mnuTheme.add(showFlatUIInspector);
        
        addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
 			    // invoke refresh
		        SwingUtilities.invokeLater(new Runnable() {
		        	@Override public void run() {
		        		pnlSwingExplorer.actRefresh.actionPerformed(null);
		        	}
		        });
			}
		});
	}

    private void showUIDefaultsInspector() {
        FlatUIDefaultsInspector.show();
    }

    private void showInspector(JFrame own) {
        JDialog inspectorKey = new JDialog(own);
        JLabel l = new JLabel("Press Shit Ctrl Alt X to enable inspector.");
        inspectorKey.setTitle("Help.");
        inspectorKey.add(l);
        inspectorKey.setSize(300, 100);
        inspectorKey.setLocationByPlatform(true);
        inspectorKey.setVisible(true);
    }

    public Application getApplication() {
		return application;
	}

	public void setApplication(Application _application) {
		this.application = _application;
		pnlSwingExplorer.setApplication(_application);
	}
}

