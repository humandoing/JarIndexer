package net.humandoing.jarindexer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;
import java.util.ArrayList;

/**
 * User: danwin
 * Date: Jun 22, 2005
 * Time: 11:10:30 AM
 */
public class View extends JFrame {

  Container contentPane;

  private JTextField filenameTextfield;
  private JTextField searchTextField;
  private JList resultsList;
  private JButton deleteIndexButton;
  private JButton createIndexButton;
  private JButton fileBrowseButton;

  private JLabel jarFiles;
  private JLabel jarFileEntries;

  private JLabel absolutePathLabel;
  private JLabel jarFileLabel;
  private JLabel classNameLabel;

  private DefaultListModel searchResultsModel;

  /**
   * Constructor
   */
  public View() {
    buildGUI();
  }

  /**
   * Build and show the user interface.
   */
  private void buildGUI() {
    contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());
    JPanel panel = new JPanel();
    GridBagLayout gridBag = new GridBagLayout();
    GridBagConstraints c = null;

    panel.setBorder(BorderFactory.createTitledBorder("Manage Jar Indexes"));
    panel.setLayout(gridBag);

    contentPane.add(panel, BorderLayout.NORTH);

    JLabel dirLabel = new JLabel("Directory to Index");
    c = getConstraints(0, 0, 1, 3, 0.0, 0.0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE);
    gridBag.setConstraints(dirLabel, c);
    panel.add(dirLabel);

    filenameTextfield = new JTextField(30);
    c = getConstraints(3, 0, 1, 5, 1.0, 0.0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL);
    gridBag.setConstraints(filenameTextfield, c);
    panel.add(filenameTextfield);

    fileBrowseButton = new JButton("Browse...");
    fileBrowseButton.setToolTipText("Click here to choose a directory that you would like to recursively index");
    fileBrowseButton.addActionListener(new ChooseFileAction());
    c = getConstraints(7, 1, 1, 1, 1.0, 0.0, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE);
    gridBag.setConstraints(fileBrowseButton, c);
    panel.add(fileBrowseButton);

    createIndexButton = new JButton("Create Indexes");
    createIndexButton.addActionListener(new CreateIndexAction());
    c = getConstraints(0, 2, 1, 2, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE);
    gridBag.setConstraints(createIndexButton, c);
    panel.add(createIndexButton);

    deleteIndexButton = new JButton("Delete Indexes");
    deleteIndexButton.addActionListener(new DeleteIndexAction());
    c = getConstraints(2, 2, 1, 2, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE);
    gridBag.setConstraints(deleteIndexButton, c);
    panel.add(deleteIndexButton);

    JLabel cuurentStats = new JLabel("Current Statistics:");
    c = getConstraints(0, 4, 1, 2, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE);
    gridBag.setConstraints(cuurentStats, c);
    panel.add(cuurentStats);

    jarFiles = new JLabel("Jar Files Indexed: " + Database.getInstance().getJarsIndexedCount());
    c = getConstraints(0, 5, 1, 2, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE);
    gridBag.setConstraints(jarFiles, c);
    panel.add(jarFiles);

    jarFileEntries = new JLabel("Classes Indexed: " + Database.getInstance().getClassesIndexedCount());
    c = getConstraints(0, 6, 1, 2, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE);
    gridBag.setConstraints(jarFileEntries, c);
    panel.add(jarFileEntries);


    gridBag = new GridBagLayout();
    JPanel searchPanel = new JPanel();
    searchPanel.setBorder(BorderFactory.createTitledBorder("Search Indexed Jar/Class files"));
    searchPanel.setLayout(gridBag);
    contentPane.add(searchPanel, BorderLayout.CENTER);


    searchTextField = new JTextField(30);
    searchTextField.setToolTipText("Press Enter to search (enter some criteria first)");
    searchTextField.addActionListener(new SearchAction());
    c = getConstraints(0, 0, 1, 8, 1.0, 0.0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL);
    gridBag.setConstraints(searchTextField, c);
    searchPanel.add(searchTextField);


    searchResultsModel = new DefaultListModel();
    resultsList = new JList(searchResultsModel);
    resultsList.setVisibleRowCount(5);
    resultsList.addListSelectionListener(new SelectedClassListener());

    JScrollPane scrollPane = new JScrollPane(resultsList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    c = getConstraints(0, 1, 5, 8, 0.0, 1.0, GridBagConstraints.LINE_START, GridBagConstraints.BOTH);
    gridBag.setConstraints(scrollPane, c);
    searchPanel.add(scrollPane);


    gridBag = new GridBagLayout();
    JPanel classDetailPanel = new JPanel();
    classDetailPanel.setBorder(BorderFactory.createTitledBorder("Selected Class Details"));
    classDetailPanel.setLayout(gridBag);
    contentPane.add(classDetailPanel, BorderLayout.SOUTH);

    classNameLabel = new JLabel("Class Name: ");
    c = getConstraints(0, 0, 1, 8, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE);
    gridBag.setConstraints(classNameLabel, c);
    classDetailPanel.add(classNameLabel);

    jarFileLabel = new JLabel("Jar File: ");
    c = getConstraints(0, 1, 1, 8, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE);
    gridBag.setConstraints(jarFileLabel, c);
    classDetailPanel.add(jarFileLabel);

    absolutePathLabel = new JLabel("Path to File: ");
    c = getConstraints(0, 2, 1, 8, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE);
    gridBag.setConstraints(absolutePathLabel, c);
    classDetailPanel.add(absolutePathLabel);

    addWindowListener(new CloseWindowAdapter(this));
    setTitle("Jar Indexer");
    setSize(600, 500);
    setLocation(300, 200);
    setVisible(true);
  }

  /**
   * Utility method to get a GridBagConstraints object.
   *
   * @param gridX
   * @param gridY
   * @param gridHeight
   * @param gridWidth
   * @param weightX
   * @param weightY
   * @param anchor
   * @param fill
   * @return
   */
  private GridBagConstraints getConstraints(int gridX, int gridY, int gridHeight, int gridWidth, double weightX, double weightY, int anchor, int fill) {
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = gridX;
    c.gridy = gridY;
    c.gridwidth = gridWidth;
    c.gridheight = gridHeight;
    c.weightx = weightX;
    c.weighty = weightY;
    c.anchor = anchor;
    c.fill = fill;
    c.insets = new Insets(2, 2, 2, 2);
    return c;
  }

  /**
   * Reset the statistics label when a user deletes indexes or
   * adds new indexes.
   */
  private void resetStatisticsLabels() {
    jarFiles.setText("Jar Files Indexed: " + Database.getInstance().getJarsIndexedCount());
    jarFileEntries.setText("Classes Indexed: " + Database.getInstance().getClassesIndexedCount());
  }

  /**
   * Enable / disable components as needed based on when a user is
   * performing indexing.
   *
   * @param enable
   */
  private void enableComponents(boolean enable) {
    deleteIndexButton.setEnabled(enable);
    createIndexButton.setEnabled(enable);
    searchTextField.setEnabled(enable);
    filenameTextfield.setEnabled(enable);
    fileBrowseButton.setEnabled(enable);
  }


  /**
   * Action class for the Delete Index button
   */
  private class DeleteIndexAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      try {
        Database.getInstance().deleteIndexes();
        resetStatisticsLabels();
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
    }
  }


  /**
   * Action class for Browse File button
   */
  private class ChooseFileAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      JFileChooser chooser = new JFileChooser();
      chooser.setDialogTitle("Choose a directory to index");
      chooser.setMultiSelectionEnabled(false);
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      int returnVal = chooser.showOpenDialog(contentPane);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        filenameTextfield.setText(chooser.getSelectedFile().getAbsolutePath());
      }
    }
  }


  /**
   * Action class for the Search text field
   */
  private class SearchAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      String searchText = searchTextField.getText();
      if (searchText != null && searchText.length() > 0) {
        try {
          searchResultsModel.removeAllElements();
          ArrayList searchResults = Database.getInstance().search(searchText);
          for (Iterator i = searchResults.iterator(); i.hasNext();) {
            searchResultsModel.addElement(i.next());
          }
        } catch (SQLException sqle) {
          sqle.printStackTrace();
        }
      }
    }
  }


  /**
   * Action class for the Create Index button.
   */
  private class CreateIndexAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      final File file = new File(filenameTextfield.getText());
      if (!file.exists()) {
        JOptionPane.showMessageDialog(contentPane, "You must choose a directory that exists.");
      } else if (!file.isDirectory()) {
        JOptionPane.showMessageDialog(contentPane, "You must choose a directory.");
      } else {
        new Thread() {
          public void run() {
            try {
              if (Database.getInstance().isAlreadyIndexed(file.getAbsolutePath())) {
                int result = JOptionPane.showConfirmDialog(contentPane, "I'm pretty sure we have already indexed this directory, would you like to continue anyways?", "Already Indexed", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                  doIndex();
                }
              } else {
                doIndex();
              }
            } catch (Exception e1) {
              e1.printStackTrace();
            } finally {
              enableComponents(true);
            }
          }

          private void doIndex() throws Exception {
            enableComponents(false);
            JarIndexer indexer = new JarIndexer(file);
            indexer.index();
            resetStatisticsLabels();
            JOptionPane.showMessageDialog(contentPane, "Indexes have been created.");
          }

        }.start();
      }
    }
  }

  /**
   * Class to handle changes to the selected item in the search results list.
   */
  private class SelectedClassListener implements ListSelectionListener {
    public void valueChanged(ListSelectionEvent e) {
      if (!e.getValueIsAdjusting()) {
        JarFileEntry entry = (JarFileEntry) resultsList.getSelectedValue();
        if (entry != null) {
          try {
            JarFile file = Database.getInstance().getJarFile(entry.getJarFileId());
            classNameLabel.setText("Class Name: " + entry.getClassName());
            jarFileLabel.setText("Jar File: " + file.getFilename());
            absolutePathLabel.setText("Path to File: " + file.getAbsolutePath());
          } catch (SQLException e1) {
            e1.printStackTrace();
          }
        }
      }
    }
  }
}
