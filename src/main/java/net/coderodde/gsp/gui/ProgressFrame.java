package net.coderodde.gsp.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import net.coderodde.gsp.gui.data.ProgressListener;

/**
 * This class implements a small window showing the progress of an operation.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 20, 2015)
 */
public class ProgressFrame extends JFrame implements ProgressListener {
    
    private final Dimension DIMENSION = new Dimension(300, 150);
    
    private final JLabel descriptionLabel;
    private final JProgressBar progressBar;
    private final JButton cancelButton;
    
    public ProgressFrame() {
        this.descriptionLabel = new JLabel();
        this.progressBar = new JProgressBar();
        this.cancelButton = new JButton("Cancel");
        
        this.getContentPane().setLayout(new GridLayout(3, 1));
        this.getContentPane().add(descriptionLabel);
        this.getContentPane().add(progressBar);
        this.getContentPane().add(cancelButton);
        
        this.pack();
    }
    
    public void addCancelButtonListener(ActionListener listener) {
        cancelButton.addActionListener(listener);
    }
    
    public void removeCancelButtonListener(ActionListener listener) {
        cancelButton.removeActionListener(listener);
    }
    
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(DIMENSION);
    }
    
    @Override
    public Dimension getMaximumSize() {
        return getMinimumSize();
    }
    
    @Override
    public Dimension getPreferredSize() {
        return getMinimumSize();
    }
    
    @Override
    public void init(int tokens, String description) {
        descriptionLabel.setText(description);
        progressBar.setMaximum(tokens);
        progressBar.setValue(0);
        repaint();
    }

    @Override
    public void add(int tokens) {
        progressBar.setValue(progressBar.getValue() + tokens);
        repaint();
    }
}
