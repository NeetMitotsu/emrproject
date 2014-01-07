package wi.annotator;

import java.awt.*;

import javax.swing.*; 
import java.io.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;


import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.Vector;

public class NewEmrAnnotatorViewer
{
	public static int wordSize = 16;
	public static int chooseSize = 18;
	
	
	private static void addOpenFileButtonListener(JButton buttonOpen,final JTextPane textPane,final JTextField inputFile,final JTable entityTable,final JTable relationTable){
	    buttonOpen.addActionListener(new ActionListener()
	    {
	    	public void actionPerformed(ActionEvent e)
	    	{
				
	    		JFileChooser j=new JFileChooser(GlobalCache.currentPath);//�ļ�ѡ����
	    		j.setFileFilter(new EmrFileFiller(".xml"));
	    	    if(j.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
	    		{	    	    	
	    	    	try{
	    	    		File f=j.getSelectedFile();
	    	    		GlobalCache.currentPath = f.getAbsolutePath();
	    	    		FileInputStream in2=new FileInputStream(f);
	    	    		BufferedReader br = new BufferedReader(new InputStreamReader(in2,"UTF-8"));
	    	    		StringBuffer sb = new StringBuffer();
	    	    		String line = null;
	    	    		while((line = br.readLine())!= null){
	    	    			sb.append(line+"\n");
	    	    		}
	    	    		textPane.setText(sb.toString());
	    	    		br.close();
	    	    		
	    	    		SimpleAttributeSet attr=new SimpleAttributeSet();
	    	    		StyleConstants.setForeground(attr,Color.black);
	    	    		StyleConstants.setFontSize(attr,wordSize);
	    	    		((DefaultStyledDocument) textPane.getDocument()).setCharacterAttributes(0,textPane.getText().length(),attr,false);
	    	    		
	    	    		inputFile.setText(f.getAbsolutePath());
	    	    		if(entityTable != null){
	    	    			clearTable(entityTable);
	    	    		}
	    	    		if(relationTable != null){
	    	    			clearTable(relationTable);
	    	    		}
	    	    	}catch(Exception ee){}
	    		}
	    	       
	    	}
	    });
	    
	}
	
	private static void clearTable(JTable table){
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		
		for(int row = model.getRowCount() - 1;row >=0; row --){
			model.removeRow(row);
		}
	}
	
	private static void addImportNEButtonListener(JButton buttonInNE,final JTextPane textPane,final JTextField inputFile,final JTable table){
	    buttonInNE.addActionListener(new ActionListener()
	    {
	    	public void actionPerformed(ActionEvent e)
	    	{
	    		String filename = inputFile.getText()+".ent";
	    		//	    		JFileChooser j=new JFileChooser(GlobalCache.currentPath);//�ļ�ѡ����
	    		//	    	    if(j.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
	    		try{
	    			clearTable(table);
	    			File f=new File(filename);
	    			FileInputStream in=new FileInputStream(f);
	    			GlobalCache.currentPath = f.getAbsolutePath();
	    			BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
	    			StringBuffer sb = new StringBuffer();
	    			String line = null;
	    			Entity sudo = new Entity();
	    			sudo.setStartPos(0);
	    			sudo.setEndPos(textPane.getText().length());
	    			clearEntityColor(textPane,sudo);
	    			DefaultTableModel model = (DefaultTableModel)table.getModel();
	    			while((line = br.readLine())!= null){
	    				if(line.length() > 0){
	    					Entity ent = Entity.createBySaveStr(line);
	    					TypeColor assertType = null;
	    					if(ent.getAssertType() != null){
	    						assertType = TypeColorMap.getType(ent.getAssertType());
	    					}
	    					Object[] rowData = new Object[]{ent.toAnnotation(),TypeColorMap.getType(ent.getEntityType()),assertType};
	    					model.addRow(rowData);

	    					setEntityForeground(textPane,ent,TypeColorMap.getType(ent.getEntityType()));
	    				}
	    			}
	    			br.close();


	    		}catch(Exception ex){

	    		}
	    		//	    	    }

	    	}
	    });
	}
	
	private static void addAddNEButtonListener(JButton buttonNE,final JTextPane textPane,final JTable table){
//	    JButton buttonNE;//���ʵ�尴����ͨ���������ѡ������һ���֣�֮�������ʵ�弴��
//	    buttonNE = new JButton("���ʵ�� A");
	    buttonNE.setMnemonic(KeyEvent.VK_A);
	    buttonNE.addActionListener(new ActionListener()
	    {
	    	public void actionPerformed(ActionEvent e)
	    	{
	    		int p0 = textPane.getSelectionStart();//ѡ�����ݵ���ʼλ��
	    		int p1 = textPane.getSelectionEnd();//ѡ�����ݵ���ֹλ��

	    	
	    		if (p0 < p1)
	    		{
			    	
			    	DefaultTableModel model = (DefaultTableModel)table.getModel();			    	
			    	try {
			    		Entity entity = new Entity();
			    		entity.setEntity(textPane.getText(p0, p1-p0));
			    		entity.setStartPos(p0);
			    		entity.setEndPos(p1);
			    		String annotationStr = entity.toAnnotation();
			    		model.addRow(new Object[]{annotationStr,null,null});
			    		int row = model.getRowCount() - 1;
			    		table.setRowSelectionInterval(row, row);
			    		
			    		setEntityBackground(textPane,entity);
			    		
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
			    	table.repaint();
	    		}
	    		else
	    		{
	    			JOptionPane.showMessageDialog(null, "����ѡ��һ���Ϸ���ʵ��", "��ʾ",
	    		            JOptionPane.INFORMATION_MESSAGE);
	    		}
	    	}
	    });
	}
	
	
	private static void addDeleteNEButtonListener(JButton buttonNO,final JTextPane textPane,final JTable table){
//	    JButton buttonNO;//ɾ��һ��ʵ��
//	    buttonNO = new JButton("ɾ��ʵ�� D");
	    buttonNO.setMnemonic(KeyEvent.VK_D);
	    buttonNO.addActionListener(new ActionListener()
	    {
	    	public void actionPerformed(ActionEvent e)
	    	{
	    		int response = JOptionPane.showConfirmDialog(null, "���Ƿ�ϣ�������ǰѡ��ʵ�壿", "��ʾ", JOptionPane.YES_NO_OPTION);
	    		
	    		if (response == 0)
	    		{
			    	int row = table.getSelectedRow();
			    	if (row >= 0)
			    	{
			    		String annoationStr = (String)table.getValueAt(row, table.getColumnModel().getColumnIndex("ʵ��"));
			    		Entity ent = Entity.createByAnnotationStr(annoationStr);
			    		clearEntityColor(textPane, ent);
			    		((DefaultTableModel)table.getModel()).removeRow(row);
			    	}
			    	else
			    	{
			    		JOptionPane.showMessageDialog(null, "����ѡ��һ���Ѿ���ע��ʵ��", "��ʾ",
			    		           JOptionPane.INFORMATION_MESSAGE);
			    	}
	    		}
	    	}
	    });
	}
	
	//E=�Թ��� P=p0:p1 T=���� A=��ǰ��
	private static void addExportNEButtonToTab1(JButton buttonSave,final JTable table,final JTextField inputFile){
	    buttonSave.addActionListener(new ActionListener()
	    {
	    	public void actionPerformed(ActionEvent e)
	    	{
	    		
	    		String path;
	    		JFileChooser file = new JFileChooser (GlobalCache.currentPath);
//	    		file.setAcceptAllFileFilterUsed(false);
	    		file.setSelectedFile(new File(inputFile.getText()+".ent"));
	    		
	    		if(table.getRowCount() == 0){
	    			return;
	    		}
	    		if(file.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
	    		{
	    			path = file.getSelectedFile().getAbsolutePath();
		    		try {
		    			GlobalCache.currentPath = path;
		    			PrintWriter out = new PrintWriter(path,"UTF-8");		
		    			DefaultTableModel model = (DefaultTableModel)table.getModel();
		    			Vector rowdatas = model.getDataVector();
		    			ArrayList<Entity> entities = new ArrayList<Entity>();
		    			for(Object obj : rowdatas){
		    				String outStr = "";
		    				Vector rowdata = (Vector)obj;
		    				String annotation = (String)rowdata.get(0);
		    				Entity ent = Entity.createByAnnotationStr(annotation);
		    				TypeColor entitytype = (TypeColor)rowdata.get(1);
		    				if(entitytype != null){
		    					ent.setEntityType(entitytype.getTypeId());
		    				}
		    				TypeColor asserttype = (TypeColor)rowdata.get(2);
		    				if(asserttype != null){
		    					ent.setAssertType(asserttype.getTypeId());
		    				}
		    				if(ent.getEntityType() != null){
		    					entities.add(ent);
		    				}
		    				
		    			}
		    			Collections.sort(entities);
		    			for(Entity ent : entities){
		    				out.println(ent.toSave());
		    			}
		    			
		    			out.flush();
		    			out.close();
		    			
		    			
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
		    		
		    		JOptionPane.showMessageDialog(null, "����ɹ�  ·����"+path, "��ʾ",
	    		            JOptionPane.INFORMATION_MESSAGE);
	    		}
	    		else
	    		{
	    			JOptionPane.showMessageDialog(null, "��ȡ��  δ����", "��ʾ",
	    		            JOptionPane.INFORMATION_MESSAGE);
	    		}
	    	}
	    });
		
	}
	
	
	private static void addTableMouseListener(final JTextPane textPane,final JTable table){
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				int row = table.getSelectedRow();
				String entityvalue = (String)table.getValueAt(row, table.getColumnModel().getColumnIndex("����"));
				Entity ent = Entity.createByAnnotationStr(entityvalue);
				setEntityBackground(textPane,ent);
				GlobalCache.pastSelectedEntity = ent;
			}
		});
	}
	
	private static void clearEntityColor(JTextPane textPane,Entity ent){
		int p0 = ent.getStartPos();
		int p1 = ent.getEndPos();
	    SimpleAttributeSet attr=new SimpleAttributeSet();
    	StyleConstants.setBackground(attr,Color.WHITE);
    	StyleConstants.setForeground(attr,Color.BLACK);
    	StyleConstants.setFontSize(attr,chooseSize);
    	((DefaultStyledDocument) textPane.getDocument()).setCharacterAttributes(p0,p1-p0,attr,false);
	}
	
	private static void setEntityForeground(JTextPane textPane,Entity ent,TypeColor tc){
		if(tc != null){
			int p0 = ent.getStartPos();
			int p1 = ent.getEndPos();
			SimpleAttributeSet attr=new SimpleAttributeSet();
			StyleConstants.setForeground(attr,tc.getColor());
			StyleConstants.setFontSize(attr,chooseSize);
			((DefaultStyledDocument) textPane.getDocument()).setCharacterAttributes(p0,p1-p0,attr,false);
		}
	}
	
	private static void setEntityBackground(JTextPane textPane,Entity ent){
		if(GlobalCache.pastSelectedEntity != null){
			setEntityBackgroundColor(textPane,GlobalCache.pastSelectedEntity,Color.WHITE);
		}
		setEntityBackgroundColor(textPane,ent,TypeColor.SelectedColor);
		GlobalCache.pastSelectedEntity = ent;
	}
	
	
	private static void setEntityBackgroundColor(JTextPane textPane,Entity ent,Color color){
		int p0 = ent.getStartPos();
		int p1 = ent.getEndPos();
	    SimpleAttributeSet attr=new SimpleAttributeSet();
    	StyleConstants.setBackground(attr,color);
    	StyleConstants.setFontSize(attr,chooseSize);
    	((DefaultStyledDocument) textPane.getDocument()).setCharacterAttributes(p0,p1-p0,attr,false);
	}
	
	
	
	private static JTable createEntityTable(final JTextPane textPane,final boolean isForEntity){
		Object columnNames[] = {"����", "����","����"};//����4������
//		final Object rowData[][] = new Object[maxNum][2];//��������е�Ԫ������
		final JTable table = new JTable(null, columnNames);//�������
		DefaultTableModel model = new DefaultTableModel(){
			public boolean isCellEditable(int row, int column)
            {
						if(!isForEntity){
							return false;
						}
						if(getColumnName(column).equals("����") &&
								(getValueAt(row, column - 1) != null && getValueAt(row, column - 1).toString().length() > 0)){
							
							return true;
						}
						if(getColumnName(column).equals("����")){
							TypeColor tc = ((TypeColor)getValueAt(row, column - 1));
							if(tc != null && (tc.getFlag() == 1)){
								return true;
							}
						}
                       return false;//��������༭
            }
			public void setValueAt(Object aValue, int row, int column) {
				super.setValueAt(aValue, row, column);
				if(column == table.getColumnModel().getColumnIndex("����")){
					TypeColor tc = (TypeColor)aValue;
					String entityvalue = (String)table.getValueAt(row, table.getColumnModel().getColumnIndex("ʵ��"));
					Entity ent = Entity.createByAnnotationStr(entityvalue);				
			    	setEntityForeground(textPane,ent,tc);
			    	
			    	if(tc.getFlag() == 0){
			    		table.setValueAt(null, row, table.getColumnModel().getColumnIndex("����"));
			    	}

				}
			}
		};
		
		table.setSurrendersFocusOnKeystroke(true);
		model.setColumnIdentifiers(columnNames);
		table.setModel(model);
		table.getColumn("����").setCellRenderer(new TypeCellRender(true));
		
		table.setRowHeight(25);
		
		final JTextField inputFile = new JTextField(40);
		inputFile.setEditable(false);
		
		final JComboBox combo = new JComboBox();//����ʵ����������˵�
		for(TypeColor tc : TypeColorMap.getEntityTypeArray()){
			combo.addItem(tc);
		}
		combo.setRenderer(new ComboxRender(true));
	    combo.setEditable(false);
	    
	    
	    DefaultCellEditor typeeditor = new DefaultCellEditor(combo);
	    table.getColumn("����").setCellEditor(typeeditor);//����3����Ϊ��������ѡ��
	    
	    JComboBox combo2 = new JComboBox();//����ʵ����������˵�
		for(TypeColor tc : TypeColorMap.getAssertTypeArray()){
			combo2.addItem(tc);
		}
	    combo2.setEditable(false);
	    DefaultCellEditor asserteditor = new DefaultCellEditor(combo2);
	    table.getColumn("����").setCellEditor(asserteditor);//����3����Ϊ��������ѡ��
	    
	    
	    addTableMouseListener(textPane,table);
	    
	    return  table;
	}
	
	private static JPanel createEntityButtonPanel(JTextPane textPane,JTable table){
		JPanel btnpanel = new JPanel();
	    
	    JTextField inputFile = new JTextField(40);
		inputFile.setEditable(false);

	    JButton buttonOpen = new JButton("���ļ�");
	    JButton buttonInNE = new JButton("�����ȡ���");
//	    JButton buttonNE = new JButton("���ʵ�� A");
//	    JButton buttonNO = new JButton("ɾ��ʵ�� D");
//	    JButton buttonSave = new JButton("�������");
	    btnpanel.add(buttonOpen);
	    btnpanel.add(buttonInNE);
//	    btnpanel.add(buttonNE);
//	    btnpanel.add(buttonNO);
	    btnpanel.add(inputFile);
//	    btnpanel.add(buttonSave);
	    
	    addOpenFileButtonListener(buttonOpen,textPane,inputFile,table,null);
//	    addAddNEButtonListener(buttonNE,textPane,table);
//	    addExportNEButtonToTab1(buttonSave,table,inputFile);
	    addImportNEButtonListener(buttonInNE,textPane,inputFile,table);
//	    addDeleteNEButtonListener(buttonNO,textPane,table);
	    
	    return btnpanel;
	}
	
	private static void addEntityAnnotationTab(JTabbedPane tabbedPane, String text)//ҳ��һ��ʵ���ע
	{
		JPanel entityPanel = new JPanel();//�½�һ������
		entityPanel.setLayout(new BorderLayout());//��BorderLayout�԰�����в���
		
		final JTextPane entityTextPane = new JTextPane();//�½�һ���ı��༭��������ʾ�ı������в���
		entityTextPane.setEditable(false);//���ı��ǲ������û��ڿ��ڱ༭��
	    
		final JTable table = createEntityTable(entityTextPane,true);	  
		
	    JPanel btnpanel = createEntityButtonPanel(entityTextPane,table);
	    entityPanel.add(btnpanel,BorderLayout.NORTH);
	   
	    
	    JSplitPane jSplitPane0 = new JSplitPane();
	    jSplitPane0.setSize(1024, 768);
	    jSplitPane0.setOrientation(JSplitPane.VERTICAL_SPLIT);
	    jSplitPane0.setDividerLocation(0.5);
	    entityPanel.add(jSplitPane0,BorderLayout.CENTER);
	    jSplitPane0.setTopComponent(new JScrollPane(entityTextPane));
	    
	    
	    jSplitPane0.setBottomComponent(new JScrollPane(table));
	    
		tabbedPane.addTab(text, entityPanel);
	}
	
	
	
	
	private static JTable createRelationTable(final JTextPane textPane ){
		
		Object columnNames[] = {"ʵ��1", "ʵ��2","��ϵ����"};//����4������
//		final Object rowData[][] = new Object[maxNum][2];//��������е�Ԫ������
		final JTable table = new JTable(null, columnNames);//�������
		DefaultTableModel model = new DefaultTableModel(){
			public boolean isCellEditable(int row, int column)
			{
				if(getColumnName(column).equals("��ϵ����") ){
					return true;
				}

				return false;//��������༭
			}
			public void setValueAt(Object aValue, int row, int column) {
				super.setValueAt(aValue, row, column);
				if(column == table.getColumnModel().getColumnIndex("��ϵ����")){
					String entity1value = (String)table.getValueAt(row, table.getColumnModel().getColumnIndex("ʵ��1"));
					Entity ent1 = Entity.createByAnnotationStr(entity1value);				
					String entity2value = (String)table.getValueAt(row, table.getColumnModel().getColumnIndex("ʵ��2"));
					Entity ent2 = Entity.createByAnnotationStr(entity2value);		
					TypeColor tc = (TypeColor)aValue;
					if(tc == null){
						setEntityPairBackGround(textPane,ent1,ent2,Color.WHITE);
						return;
					}
					
					if(tc.getFlag() == 0){
						JOptionPane.showMessageDialog(null, "��ѡ�����Ĺ�ϵ����", "��ʾ",
		    		            JOptionPane.INFORMATION_MESSAGE);
						setValueAt(null,row,column);
						setEntityPairBackGround(textPane,ent1,ent2,Color.WHITE);
					}else{
						setEntityPairBackGround(textPane,ent1,ent2,tc.getColor());
					}
					
			    	
				}
			}
		};
		
		table.setSurrendersFocusOnKeystroke(true);
		model.setColumnIdentifiers(columnNames);
		table.setModel(model);
		table.getColumn("��ϵ����").setCellRenderer(new TypeCellRender(false));
		
		table.setRowHeight(25);
		
		
		final JComboBox combo = new JComboBox();//����ʵ����������˵�
		TypeColor[] tcs = TypeColorMap.getRelationTypeArray();
		combo.setMaximumRowCount(tcs.length);
		combo.setRenderer(new ComboxRender(false));
		for(TypeColor tc : tcs){
			combo.addItem(tc);
		}
	    combo.setEditable(false);
	    
	    
	    DefaultCellEditor typeeditor = new DefaultCellEditor(combo);
	    table.getColumn("��ϵ����").setCellEditor(typeeditor);//����3����Ϊ��������ѡ��
	    
	    
	    addRelationTableMouseListener(textPane,table);
		
		
		return table;
	}
	
	
	private static void setEntityPairBackGround(JTextPane textPane,Entity ent1,Entity ent2,Color color){
		if(GlobalCache.pastSelectedEntityPair!= null){
			setEntityBackgroundColor(textPane,GlobalCache.pastSelectedEntityPair[0],Color.WHITE);
			setEntityBackgroundColor(textPane,GlobalCache.pastSelectedEntityPair[1],Color.WHITE);
		}
		setEntityBackgroundColor(textPane,ent1,color);
		setEntityBackgroundColor(textPane,ent2,color);
		
		GlobalCache.pastSelectedEntityPair = new Entity[]{ent1,ent2};
	}
	
	
	private static void addRelationTableMouseListener(final JTextPane textPane,final JTable table){
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				int row = table.getSelectedRow();
				String entity1value = (String)table.getValueAt(row, table.getColumnModel().getColumnIndex("ʵ��1"));
				Entity ent1 = Entity.createByAnnotationStr(entity1value);				
				String entity2value = (String)table.getValueAt(row, table.getColumnModel().getColumnIndex("ʵ��2"));
				Entity ent2 = Entity.createByAnnotationStr(entity2value);		
				//�ñ���ɫ
				TypeColor tc = (TypeColor)table.getValueAt(row, table.getColumnModel().getColumnIndex("��ϵ����"));
				if(tc != null){
					setEntityPairBackGround(textPane,ent1,ent2,tc.getColor());
				}else{
					setEntityPairBackGround(textPane,ent1,ent2,Color.WHITE);
				}

			}
		});
	}
	
	
	private static void addEntityBtn1Listener(JButton entityBtn1,final JTextField entityTxt1,final JTable entityTable){
		entityBtn1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = entityTable.getSelectedRow();
				if(row >= 0){
					entityTxt1.setText((String)entityTable.getValueAt(row, entityTable.getColumnModel().getColumnIndex("ʵ��")));
				}else{
					JOptionPane.showMessageDialog(null, "����ѡ��ʵ����е�һ��", "��ʾ",
	    		            JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
	}
	
	private static void addAddrealtionBtnListener(JButton addRelationBtn,final JTextField entityTxt1,final JTextField entityTxt2,final JTable relationTable){
		addRelationBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(entityTxt1.getText().length() == 0 || entityTxt2.getText().length() == 0 
						|| entityTxt1.getText().equals(entityTxt2.getText())){
					JOptionPane.showMessageDialog(null, "ʵ��1��ʵ��2������Ϊ�ղ��Ҳ�����ͬ", "��ʾ",
							JOptionPane.INFORMATION_MESSAGE);
				}else{
					Object[] rowData = new Object[]{entityTxt1.getText(),entityTxt2.getText(),null};
					((DefaultTableModel)relationTable.getModel()).addRow(rowData);
					entityTxt1.setText("");
					entityTxt2.setText("");
				}
			}
		});
	}
	
	
	 private static void addDeleteRelBtnListener(JButton buttonNORel,final JTextPane entityTextPane,final JTable relationTable){
		 buttonNORel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int row = relationTable.getSelectedRow();
					String entity1value = (String)relationTable.getValueAt(row, relationTable.getColumnModel().getColumnIndex("ʵ��1"));
					Entity ent1 = Entity.createByAnnotationStr(entity1value);				
					String entity2value = (String)relationTable.getValueAt(row, relationTable.getColumnModel().getColumnIndex("ʵ��2"));
					Entity ent2 = Entity.createByAnnotationStr(entity2value);		
					setEntityPairBackGround(entityTextPane,ent1,ent2,Color.WHITE);
					((DefaultTableModel)relationTable.getModel()).removeRow(row);
				}
			});
	 }
	 
	private static void  addImportRelBtnListener(JButton buttonInRel,final JTable relationTable){
		 buttonInRel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
		    		JFileChooser j=new JFileChooser(GlobalCache.currentPath);//�ļ�ѡ����
		    		j.setFileFilter(new RelationFileFilter());
		    	    if(j.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
		    	    	 try{
		 	    	    	File f=j.getSelectedFile();
		 	    	    	FileInputStream in=new FileInputStream(f);
		 	    	    	GlobalCache.currentPath = f.getAbsolutePath();
		    	    		BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		    	    		String line = null;
		    	    		DefaultTableModel model = (DefaultTableModel)relationTable.getModel();
		    	    		
		    	    		clearTable(relationTable);
		    	    		
		    	    		
		    	    		while((line = br.readLine())!= null){
		    	    			if(line.length() > 0){
		    	    				Relation rel = Relation.createBySaveStr(line);
		    	    				Object[] rowData = new Object[]{rel.getEnt1().toAnnotation(),rel.getEnt2().toAnnotation(),TypeColorMap.getType(rel.getRelationType())};
		    	    				model.addRow(rowData);
		    	    			}
		    	    		}
		    	    		br.close();
		    	    		
		 	    	    	
		    	    	 }catch(Exception ex){
		    	    		 
		    	    	 }
		    	    }
				}
			});
	 }
	 
	 private static void addSaveRelBtnLinstener(JButton buttonSave,final JTable relationTable,final JTextField inputFile){
		 buttonSave.addActionListener(new ActionListener()
		    {
		    	public void actionPerformed(ActionEvent e)
		    	{
		    		
		    		String path;
		    		JFileChooser file = new JFileChooser (GlobalCache.currentPath);
		    		file.setAcceptAllFileFilterUsed(false);
		    		file.setSelectedFile(new File(inputFile.getText()+".rel"));
		    		
		    		if(relationTable.getRowCount() == 0){
		    			return;
		    		}
		    		if(file.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
		    		{
		    			path = file.getSelectedFile().getAbsolutePath();
			    		try {
			    			GlobalCache.currentPath = path;
			    			PrintWriter out = new PrintWriter(path,"UTF-8");		
			    			DefaultTableModel model = (DefaultTableModel)relationTable.getModel();
			    			Vector rowdatas = model.getDataVector();
			    			for(Object obj : rowdatas){
			    				Vector rowdata = (Vector)obj;
			    				Entity ent1 = Entity.createByAnnotationStr((String)rowdata.get(0));
			    				Entity ent2 = Entity.createByAnnotationStr((String)rowdata.get(1));
			    				TypeColor relationtype = (TypeColor)rowdata.get(2);
			    				if(relationtype != null){
			    					Relation rel = new Relation();
			    					rel.setEnt1(ent1);
			    					rel.setEnt2(ent2);
			    					rel.setRelationType(relationtype.getTypeId());
			    					out.println(rel.toSave());
			    				}
			    			}
			    			
			    			out.flush();
			    			out.close();
			    			
			    			
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
			    		
			    		JOptionPane.showMessageDialog(null, "����ɹ�  ·����"+path, "��ʾ",
		    		            JOptionPane.INFORMATION_MESSAGE);
		    		}
		    		else
		    		{
		    			JOptionPane.showMessageDialog(null, "��ȡ��  δ����", "��ʾ",
		    		            JOptionPane.INFORMATION_MESSAGE);
		    		}
		    	}
		    });
	 }
	
	private static JPanel createRelationButtonPanel(final JTextPane entityTextPane,final JTable entityTable,final JTable relationTable){
		JPanel btnpanel = new JPanel(new GridLayout(0,1));
		
		JPanel btnpanel1 = new JPanel();
	    
	    JTextField inputFile = new JTextField(40);
		inputFile.setEditable(false);

	    JButton buttonOpen = new JButton("���ļ�"); 
	    JButton buttonInNE = new JButton("����NE");
	    JButton buttonInRel = new JButton("�����ϵ");
	    btnpanel1.add(buttonOpen);
	    btnpanel1.add(inputFile);
	    btnpanel1.add(buttonInNE);
	    btnpanel1.add(buttonInRel);
	    
	    addOpenFileButtonListener(buttonOpen,entityTextPane,inputFile,entityTable,relationTable);
	    addImportNEButtonListener(buttonInNE,entityTextPane,inputFile,entityTable);
	    addImportRelBtnListener(buttonInRel,relationTable);
	    
	    
		JPanel entityBtnPanel = new JPanel();
		entityBtnPanel.setLayout(new GridLayout(1,0));
		
		JPanel panel1 = new JPanel();
		panel1.setLayout(new BorderLayout());
		JButton entityBtn1 = new JButton("ʵ��1");
		JTextField entityTxt1 = new JTextField();
		panel1.add(entityBtn1,BorderLayout.WEST);
		panel1.add(entityTxt1,BorderLayout.CENTER);
		addEntityBtn1Listener(entityBtn1,entityTxt1,entityTable);
		
		JPanel panel2 = new JPanel();
		panel2.setLayout(new BorderLayout());
		JButton entityBtn2 = new JButton("ʵ��2");
		JTextField entityTxt2 = new JTextField();
		panel2.add(entityBtn2,BorderLayout.WEST);
		panel2.add(entityTxt2,BorderLayout.CENTER);
		addEntityBtn1Listener(entityBtn2,entityTxt2,entityTable);
		
		JButton addRelationBtn = new JButton("���ʵ���ϵ");
	    JButton buttonNORel = new JButton("ɾ��ʵ���ϵ");
	    JButton buttonSave = new JButton("�������");
	    
	    addAddrealtionBtnListener(addRelationBtn,entityTxt1,entityTxt2,relationTable);
	    addDeleteRelBtnListener(buttonNORel,entityTextPane,relationTable);
	    addSaveRelBtnLinstener(buttonSave,relationTable,inputFile);

		entityBtnPanel.add(panel1);
		entityBtnPanel.add(panel2);
		entityBtnPanel.add(addRelationBtn);
		entityBtnPanel.add(buttonNORel);
		entityBtnPanel.add(buttonSave);
	    
	    
		btnpanel.add(btnpanel1);
		btnpanel.add(entityBtnPanel);
		
	    return btnpanel;
		
		
		
	}
	
	static void addRelationAnnotaionTab(JTabbedPane tabbedPane, String text)//��ϵ��ע
	{
		JPanel relationPanel = new JPanel();
		relationPanel.setLayout(new BorderLayout());//��BorderLayout�԰�����в���
		
		final JTextPane entityTextPane = new JTextPane();
		entityTextPane.setEditable(false);

		
		JSplitPane entitySplitPane = new JSplitPane();
		entitySplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		entitySplitPane.setSize(1024, 384);
		entitySplitPane.setDividerLocation(0.3);
		final JTable entityTable = createEntityTable(entityTextPane,false);
		
		entitySplitPane.setLeftComponent(new JScrollPane(entityTable));
		entitySplitPane.setRightComponent(new JScrollPane(entityTextPane));
		
		
	    JSplitPane mainSplitPane = new JSplitPane();
	    mainSplitPane.setSize(1024, 768);
	    mainSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
	    mainSplitPane.setDividerLocation(0.6);   
	    
	    mainSplitPane.setTopComponent(entitySplitPane);
	    
	    final JTable relationTable = createRelationTable(entityTextPane);	

	    JPanel btnpanel = createRelationButtonPanel(entityTextPane,entityTable,relationTable);
	    relationPanel.add(btnpanel,BorderLayout.NORTH);

	    mainSplitPane.setBottomComponent(new JScrollPane(relationTable));
	    relationPanel.add(mainSplitPane,BorderLayout.CENTER);
		
	    tabbedPane.addTab(text, relationPanel);
	    
	}
	
	

	public static void main(String args[])                                            //������
	{
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame f = new JFrame("WIʵ���ҵ��Ӳ��������ȡ");
//		f.setResizable(false);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container content = f.getContentPane();
		JTabbedPane tabbedPane = new JTabbedPane();
		addEntityAnnotationTab(tabbedPane, "�����ȡ");
//		addRelationAnnotaionTab(tabbedPane, "ʵ���ϵ��ע");
		content.add(tabbedPane, BorderLayout.CENTER);
		f.setSize(1024, 768);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}
}

