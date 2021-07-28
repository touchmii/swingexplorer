package org.swingexplorer.internal;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;

import org.swingexplorer.edt_monitor.EDTDebugQueue;
import org.swingexplorer.graphics.Player;
import org.swingexplorer.idesupport.IDESupport;
import org.swingexplorer.personal.*;
import org.swingexplorer.plaf.PlafUtils;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Actual implementation of org.swingexplorer.Launcher's behavior. This is a separate
 * class to prevent internal implementation details from leaking into the public API
 * of Launcher.
 */
public class Application implements Runnable {


    public IDESupport ideSupport;

    FrmSwingExplorer frmMain;
    public PnlPlayerControls pnlPlayerControls;
    JDialog dlgPlayerControls;
    public MdlSwingExplorer model = new MdlSwingExplorer();
    public Player player = new Player();

    private PersonalizerRegistry personalizerRegistry;

    static final String PREFS_ROOT_PATH = "/flatlaf-demo";
	static final String KEY_TAB = "tab";

	private JTabbedPane jTabbedPane = new JTabbedPane();

    @Override
    public void run() {
        // register JMX bean for IDE support
        try {
//      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.setLookAndFeel( new FlatDarkLaf());
        }
//    catch (ClassNotFoundException | InstantiationException | IllegalAccessException
//               | UnsupportedLookAndFeelException ex) {
        catch( Exception ex ) {
            System.out.println("Could not set look-and-feel");
//            LOG.warn("Could not set look-and-feel", ex);
        }
//        DemoPrefs.init( PREFS_ROOT_PATH );
//
//        // application specific UI defaults
//        FlatLaf.registerCustomDefaultsSource( "com.formdev.flatlaf.demo" );
//
//        // set look and feel
//        String[] arg = new String[1];
//        DemoPrefs.setupLaf(arg);

        // install inspectors
//        FlatInspector.install( "ctrl shift alt X" );
//        FlatUIDefaultsInspector.install( "ctrl shift alt Y" );

        ideSupport = IDESupport.registerMBean();
        EDTDebugQueue.initMonitoring();

        // create frame
        frmMain = new FrmSwingExplorer();
        frmMain.setName("frmMain");
        frmMain.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent evt) {
                exitApplication();
            }
        });

        // we use own L&F for swing explorer to avoid conflict with application's L&F
//        PlafUtils.applyCustomLookAndFeel(frmMain.getContentPane());

        // load options and set to interested parties
        Options options =  new Options();
        options.load();
        model.setOptions(options);
        player.setOptions(options);
        frmMain.setApplication(this);
        personalizerRegistry = new PersonalizerRegistry(frmMain, options);

        // add component personalizers
        personalizerRegistry.addPersonalizer("frmMain", new FramePersonalizer());
        personalizerRegistry.addPersonalizer("sppMain", new SplitPanePersonalizer("verticalDividerLocation"));
        personalizerRegistry.addPersonalizer("sppRight", new SplitPanePersonalizer("horizontalDividerLocation"));
        personalizerRegistry.addPersonalizer("sppMasterDetail", new SplitPanePersonalizer("eventTabDividerLocation"));
        personalizerRegistry.addPersonalizer("tbpTrees", new TabbedPanePersonalizer("selectedTreeTabIndex"));
        personalizerRegistry.addPersonalizer("tbpBottom", new TabbedPanePersonalizer("selectedToolTabIndex"));
        personalizerRegistry.addPersonalizer("tblEvents", new TablePersonalizer("eventTableColumnSizes"));
        personalizerRegistry.addPersonalizer("pnlEventProperties.tblProperties", new TablePersonalizer("eventDetailTableColumnSizes"));
        personalizerRegistry.addPersonalizer("pnlPropertySheet.tblProperties", new TablePersonalizer("propertyTableColumnSizes"));

        // open frame
        frmMain.setVisible(true);

        //ControlBar.initialize(frmMain, jTabbedPane);
    }

    public void showMessageDialog(String message) {
        JOptionPane.showMessageDialog(frmMain, message);
    }

    private void exitApplication() {
        int res = JOptionPane.showOptionDialog(frmMain, "Do you want to finish application or close Swing Explorer window?", "Exit", JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,null, new Object[]{"Exit Application", "Close Window", "Cancel"}, "Exit");

        // save personalization state onto options
        personalizerRegistry.saveState();

        // save options to file
        Options opts = model.getOptions();
        opts.save();

        if(res == 0) {
            System.exit(0);
        } else if(res == 1){
            frmMain.dispose();
        }
    }

}
