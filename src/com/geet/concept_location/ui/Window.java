package com.geet.concept_location.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import com.geet.concept_location.corpus_creation.Document;
import com.geet.concept_location.corpus_creation.DocumentExtractor;
import com.geet.concept_location.corpus_creation.SimpleDocument;
import com.geet.concept_location.indexing_lsi.Lsi;
import com.geet.concept_location.indexing_lsi.LsiQuery;
import com.geet.concept_location.indexing_lsi.Vector;
import com.geet.concept_location.indexing_vsm.VectorSpaceMatrix;
import com.geet.concept_location.indexing_vsm.VectorSpaceModel;
import com.geet.concept_location.io.JavaFileReader;
import com.geet.concept_location.preprocessing.JavaClassPreprocessor;
import com.geet.concept_location.utils.StringUtils;

public class Window {
	public String projectPath="D:\\BSSE0501\\Project-801\\UltimateCalculator-master";
	public List<String> javaFilePaths =new ArrayList<String>();
	ExplorerPage explorerPage;
	SearchPage searchPage;
	private JFrame frame = null,
				   searchFrame = null,
				   aboutFrame = null;
	private int width, height;
	private JPanel panel;
	private JTabbedPane tabs;
	private Menu menu;
	
	List<SimpleDocument> returnDocuments = new ArrayList<SimpleDocument>();
	public JFrame getSearchFrame() {
		return searchFrame;
	}
	public JFrame getAboutFrame() {
		return aboutFrame;
	}
	public JPanel getPanel() {
		return panel;
	}
	public Menu getMenu() {
		return menu;
	}
	public JTabbedPane getTabs() {
		return tabs;
	}
	public JFrame getFrame() {
		return frame;
	}	
	public void setAboutFrame(JFrame f) {
		aboutFrame = f;
	}
	public void setFrame(JFrame f) {
		frame = f;
	}
	public void setSearchFrame(JFrame f) {
		searchFrame = f;
	}
	private void createControls() throws IOException {
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage("src/res/icon.png"));
		panel = new JPanel(new java.awt.BorderLayout()); 
		
		tabs = new JTabbedPane();
		/// Project Explorer
		setProjectExplorerPage();
		/*VectorSpaceMatrix vectorSpaceMatrix = loadVectorSpaceMatrix();
		for (SimpleDocument simpleDocument : vectorSpaceMatrix.simpleDocuments) {
			System.out.println(simpleDocument.docInJavaFile+","+simpleDocument.docName);
		}	
		System.exit(0);
		*/
		// SearchPage added
		if (javaFilePaths.size()>0) {
			JavaFileReader javaFileReader = new JavaFileReader();
			File selectedFile = new File(javaFilePaths.get(0));
			if (!selectedFile.isDirectory()) {
				if (javaFileReader.openFile(selectedFile)) {
					explorerPage.projectExplorerViewPanel.sourceViewPanel.getSourceTextArea().setText(
							javaFileReader.getText());
					explorerPage.projectExplorerViewPanel.sourceViewPanel.getFileName().setText(
							selectedFile.getName());
					
				}
			}
		}
		setSearchPage();
		
		menu = new Menu(new WindowActionHandler(this));
		panel.add(menu.getMenuBar(), BorderLayout.PAGE_START);
		panel.add(tabs, BorderLayout.CENTER);
		frame.add(panel);
		frame.setVisible(true);
	}
	private void setSearchPage() {
		searchPage = new SearchPage("Search Home");
		searchPage.searchUI.doLayout();
		AppManager.addDocument(searchPage);
		tabs.addTab(searchPage.getFilename(), searchPage.searchUI);
		int index = tabs.getTabCount()-1;
		JPanel pnlTab = new JPanel(new GridBagLayout()), iconPanel1 = new JPanel(new GridLayout(1, 1, 4, 4));
		pnlTab.setOpaque(false);
		JLabel lblTitle = new JLabel(searchPage.getFilename());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.ipadx = 5;
		pnlTab.add(lblTitle, gbc);
		gbc.gridx++;
		gbc.weightx = 0;
		gbc.ipadx = 5;
		tabs.setTabComponentAt(index, pnlTab);	
		
		searchPage.searchUI.getSearchButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
						SimpleDocument queryDocument = new SimpleDocument(
								"Query", searchPage.searchUI
										.getSearchTextField().getText());
						Lsi myLsi = new Lsi();
						returnDocuments = myLsi.search(new LsiQuery(
								queryDocument, new Vector(Lsi.NUM_FACTORS)));
						
						searchPage.searchUI.updateList(returnDocuments);

			}
		});
		searchPage.searchUI.openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// java file
				int index = searchPage.searchUI.searchResultList.getSelectedIndex();
				if (index != -1) {
					openJavaFile(returnDocuments.get(index).getDocInJavaFile());
				}
			}
		});
	}
	private void setProjectExplorerPage() throws IOException {
		explorerPage = new ExplorerPage("Project Explorer");
		explorerPage.projectExplorerViewPanel = new ProjectExplorerViewPanel(new Bound(0, 0,1300 - 100, 800 - 50),new File( projectPath));
		AppManager.addDocument(explorerPage);
		tabs.addTab(explorerPage.getFilename(), explorerPage.getProjectExplorerViewPanel());
		int index = tabs.getTabCount()-1;
		JPanel pnlTab = new JPanel(new GridBagLayout()), iconPanel = new JPanel(new GridLayout(1, 1, 4, 4));
		pnlTab.setOpaque(false);
		JLabel lblTitle = new JLabel(explorerPage.getFilename());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.ipadx = 5;
		pnlTab.add(lblTitle, gbc);
		gbc.gridx++;
		gbc.weightx = 0;
		gbc.ipadx = 5;
		tabs.setTabComponentAt(index, pnlTab);
		javaFilePaths = explorerPage.projectExplorerViewPanel.projectTreePanel.javaFilePaths;
		explorerPage.projectExplorerViewPanel.projectTreePanel.tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				JavaFileReader javaFileReader = new JavaFileReader();
				System.out.println(e.getPath().toString());
				String filePath = StringUtils.getFilePathNameNew(e
						.getPath().toString());
				System.out.println("File Path:"+filePath);
				File selectedFile = new File(filePath);
				if (!selectedFile.isDirectory()) {
					if (javaFileReader.openFile(selectedFile)) {
						explorerPage.projectExplorerViewPanel.sourceViewPanel.getSourceTextArea().setText(
								javaFileReader.getText());
						explorerPage.projectExplorerViewPanel.sourceViewPanel.getFileName().setText(
								selectedFile.getName());
						
					}
				}
			}
		});
		createVectorSpaceMatrix();
//		System.exit(0);
	}
	public Window(int w, int h) throws Exception {
		width = w;
		height = h;
		frame = new JFrame("Concept Locator");
		createControls();
	}
	/**
	 * 
	 * @throws IOException
	 */
	// indexing...
	private void createVectorSpaceMatrix() throws IOException{
			FileWriter fileWriter = new FileWriter(new File("D:\\BSSE0501\\Project-801\\Corpus.txt"));
			List<SimpleDocument> allDocuments = new ArrayList<SimpleDocument>();
			int classNo = 0;
			for (String path : javaFilePaths) {
				if (new JavaClassPreprocessor().processJavaFile(new File(path))) {
					fileWriter.write(classNo+"\n");
					fileWriter.write(path+"\n");
					System.out.println(classNo);
					System.out.println(path);
					allDocuments.addAll(new DocumentExtractor(new File(path)).getAllDocuments());
				}
				if (classNo >= 0) {
				//	break;
				}
				classNo++;
			}
			System.out.println("Size "+allDocuments.size());
			for (SimpleDocument simpleDocument : allDocuments) {
				fileWriter.write("------------------------\n");
				System.out.println("------------------------------------------------------------------------");
				Document document = (Document)simpleDocument;
				fileWriter.write(document.docInJavaFile+","+document.docName+"\n");
				System.out.println(document.docInJavaFile+","+document.docName);
				fileWriter.write(document.getStartPosition().line+" , "+document.getEndPosition().line+"\n");
				System.out.println(document.getStartPosition().line+" , "+document.getEndPosition().line);
				System.out.println(new JavaFileReader().getText(document));
				fileWriter.write("------------------------------------------------------------------------\n");
				fileWriter.write(new JavaFileReader().getText(document)+"\n");
				fileWriter.write("------------------------------------------------------------------------\n");
				fileWriter.write(document.getArticle()+"\n");
				fileWriter.write("------------------------------------------------------------------------\n");
				fileWriter.write(document.getTermsInString()+"\n");
				
			}
			fileWriter.close();
//			System.exit(0);
			VectorSpaceModel vectorSpaceModel = new VectorSpaceModel(allDocuments);
			System.out.println("Initializing.............");
			storeVectorSpaceMatrix(vectorSpaceModel.getVectorSpaceMatrix());
			generateLsiSpaceFromVectorSpaceMatrix();
	}

	/**
	 * 
	 * @param vectorSpaceMatrix
	 */
	private void storeVectorSpaceMatrix(VectorSpaceMatrix vectorSpaceMatrix) {
		System.out.println("Vector Space Model is storing...");
		try {
			FileOutputStream file = new FileOutputStream("vectorspace.ser");
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(file);
			objectOutputStream.writeObject(vectorSpaceMatrix);
			objectOutputStream.close();
			System.out.println("Vector Space Model is stored");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public VectorSpaceMatrix loadVectorSpaceMatrix() throws IOException{
		System.out.println("Vector Space Model is loading...");
		VectorSpaceMatrix vectorSpaceMatrix = null;
		FileInputStream file = new FileInputStream("vectorspace.ser");
		ObjectInputStream objectInputStream = new ObjectInputStream(file);
		try {
			vectorSpaceMatrix = (VectorSpaceMatrix) objectInputStream.readObject();
			objectInputStream.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return vectorSpaceMatrix;
	}
	private void openJavaFile(String fileName){
		File file = new File(fileName);
		String val = "";
		try {
			val = AppManager.readFile(file);
		} catch (IOException exc) {
			exc.printStackTrace();
		}
		ClassPage d = new ClassPage(file);
		d.setEditor(new RSyntaxTextArea());
	    d.getEditor().setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
	    d.getEditor().setCodeFoldingEnabled(true);
	    d.getEditor().setAntiAliasingEnabled(true);
		//RTextScrollPane sp = new RTextScrollPane(d.getEditor());
		d.setScroll(new RTextScrollPane(d.getEditor()));
		d.getScroll().setFoldIndicatorEnabled(true);
		JPanel t = d.getTabPanel();
		t.add(d.getScroll(), BorderLayout.CENTER);
		t.doLayout();
		d.setType(SyntaxConstants.SYNTAX_STYLE_JAVA);
		d.setText(val);
		//d.getEditor().setLocation(0, 0);
		
		//d.getEditor().
		//AppManager.addDocument(d);
		getTabs().addTab(d.getFilename(), t);
		int index = getTabs().getTabCount()-1;
		JPanel pnlTab = new JPanel(new GridBagLayout()), iconPanel = new JPanel(new GridLayout(1, 1, 4, 4));
		//iconPanel.setPreferredSize(new Dimension(12, 12));
		pnlTab.setOpaque(false);
		JLabel lblTitle = new JLabel(d.getFilename());
		ImageIcon icon = new ImageIcon("src/res/close.png");
		JButton btnClose = new JButton(new ImageIcon(icon.getImage().getScaledInstance(12, 12, 4)));
		btnClose.setActionCommand("TabClose");
		btnClose.setOpaque(false);
		btnClose.setBorderPainted(false);
		btnClose.setContentAreaFilled(false);
		btnClose.setFocusPainted(false);
		btnClose.setPreferredSize(new Dimension(11, 15));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.ipadx = 5;

		pnlTab.add(lblTitle, gbc);

		gbc.gridx++;
		gbc.weightx = 0;
		gbc.ipadx = 5;
		iconPanel.add(btnClose);
		pnlTab.add(btnClose, gbc);
		
		getTabs().setTabComponentAt(index, pnlTab);

		try {
			AppManager.addDocument(d);
		} catch (Exception exc) {
			System.out.println("Nie spodziewałem się Hiszpańskiej Inkwizycji!");
		}
		getFrame().repaint();

	}
	public void generateLsiSpaceFromVectorSpaceMatrix() throws IOException{
		VectorSpaceModel vectorSpaceModel = new VectorSpaceModel();
		VectorSpaceMatrix vectorSpaceMatrix = loadVectorSpaceMatrix();
		System.out.println(vectorSpaceMatrix.terms.size() +"X"+ vectorSpaceMatrix.simpleDocuments.size());
		vectorSpaceModel.generateLsi(vectorSpaceMatrix);
	}
}
