package wi.annotator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class EmrRelationAnnotator
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
	    	    		GlobalComponent.relationList.clear();
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
	    	    			File entfile = new File(inputFile.getText()+".ent");
	    	    			if(entfile.exists()){	    	    				
	    	    				importNEForRel(entfile, textPane, entityTable);
	    	    			}
	    	    			File relfile = new File(inputFile.getText()+".rel");
	    	    			File relqstfile = new File(inputFile.getText()+".rel.qst");
	    	    			if(relfile.exists()){
	    	    				importRelation(relfile, relationTable);
	    	    			}else if(relqstfile.exists()){
	    	    				importRelation(relqstfile, relationTable);
	    	    			}
	    	    		}else{
	    	    			File entfile = new File(inputFile.getText()+".ent");
	    	    			File entqstfile = new File(inputFile.getText()+".ent.qst");
	    	    			if(entfile.exists()){
	    	    				importNE(entfile,textPane,entityTable);
	    	    			}else if(entqstfile.exists()){
	    	    				importNE(entqstfile,textPane,entityTable);
	    	    			}
	    	    		}
	    	    	}catch(Exception ee){}
	    		}
	    	    textPane.setCaretPosition(0);
	    	       
	    	}
	    });
	    
	}
	
	private static void clearTable(JTable table){
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		
		for(int row = model.getRowCount() - 1;row >=0; row --){
			model.removeRow(row);
		}
	}
	
	private static void importNE(File f, JTextPane textPane, JTable table)throws Exception{
			clearTable(table);
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
    		ArrayList<Entity> ents = new ArrayList<Entity>(); 
    		while((line = br.readLine())!= null){
    			if(line.length() > 0){
    				Entity ent = Entity.createBySaveStr(line);	    	    				
    				ents.add(ent);	    	    				
    			}
    		}
    		Collections.sort(ents);
    		int rowno = 0;
    		for(Entity ent : ents){
    			rowno ++;
    			TypeColor assertType = null;
    			if(ent.getAssertType() != null){
    				assertType = TypeColorMap.getType(ent.getAssertType());
    			}
    			
    			Object[] rowData = new Object[]{rowno,ent.toAnnotation(),TypeColorMap.getType(ent.getEntityType()),assertType,ent.isQst()};
    			model.addRow(rowData);
    			setEntityForeground(textPane,ent,TypeColorMap.getType(ent.getEntityType()));
    			
    		}
    		
    		
    		br.close();
	}
	
	private static void addImportNEButtonListener(JButton buttonInNE,final JTextPane textPane,final JTable table){
	    buttonInNE.addActionListener(new ActionListener()
	    {
	    	public void actionPerformed(ActionEvent e)
	    	{
	    		
	    		JFileChooser j=new JFileChooser(GlobalCache.currentPath);//�ļ�ѡ����
	    		j.setFileFilter(new EmrFileFiller(".ent,.qst"));
	    	    if(j.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
	    	    	 try{
	    	    		 
	 	    	    	File f=j.getSelectedFile();
	 	    	    	
	 	    	    	importNE(f,textPane,table);
	 	    	    	
	    	    	 }catch(Exception ex){
	    	    		 
	    	    	 }
	    	    }
	    	       
	    	}
	    });
	}
	private static void addImportNEForRelButtonListener(JButton buttonInNE,final JTextPane textPane,final JTable table){
		buttonInNE.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				
				JFileChooser j=new JFileChooser(GlobalCache.currentPath);//�ļ�ѡ����
				j.setFileFilter(new EmrFileFiller(".ent"));
				if(j.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					try{
						File f=j.getSelectedFile();
						importNEForRel(f,textPane,table);
						
					}catch(Exception ex){
						
					}
				}
				
			}
		});
	}
	
	private static void importNEForRel(File f, JTextPane textPane, JTable table)throws Exception{
		clearTable(table);
		FileInputStream in=new FileInputStream(f);
		GlobalCache.currentPath = f.getAbsolutePath();
		BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		String line = null;
		Entity sudo = new Entity();
		sudo.setStartPos(0);
		sudo.setEndPos(textPane.getText().length());
		clearEntityColor(textPane,sudo);
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		ArrayList<Entity> ents = new ArrayList<Entity>(); 
		while((line = br.readLine())!= null){
			if(line.length() > 0){
				Entity ent = Entity.createBySaveStr(line);	    	    				
				ents.add(ent);	    	    				
			}
		}
		Collections.sort(ents);
		for(Entity ent : ents){
			TypeColor assertType = null;
			if(ent.getAssertType() != null){
				assertType = TypeColorMap.getType(ent.getAssertType());
			}
			
			Object[] rowData = new Object[]{ent.toAnnotation(),TypeColorMap.getType(ent.getEntityType()),assertType};
			model.addRow(rowData);
			setEntityForeground(textPane,ent,TypeColorMap.getType(ent.getEntityType()));
			
		}
		
		
		br.close();
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

	    		hidePopupMenu();
	    	
	    		if (p0 < p1)
	    		{
			    	
			    	DefaultTableModel model = (DefaultTableModel)table.getModel();			    	
			    	try {
			    		Entity entity = new Entity();
			    		entity.setEntity(textPane.getText(p0, p1-p0));
			    		entity.setStartPos(p0);
			    		entity.setEndPos(p1);
			    		entity = EntityCleaner.cleanEntity(entity);
			    		String annotationStr = entity.toAnnotation();
			    		model.addRow(new Object[]{table.getRowCount()+1,annotationStr,null,null});
			    		int row = model.getRowCount() - 1;
			    		table.setRowSelectionInterval(row, row);
			    		
			    		setEntityBackground(textPane,entity);
			    		
			    		Rectangle rect = table.getCellRect(table.getRowCount()-1, 0, true);  
			    		table.scrollRectToVisible(rect);

			    		
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
	    		hidePopupMenu();
	    		
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
	    		if(table.getRowCount() == 0){
	    			return;
	    		}
	    		DefaultTableModel model = (DefaultTableModel)table.getModel();
    			Vector rowdatas = model.getDataVector();
    			ArrayList<Entity> entities = new ArrayList<Entity>();
    			StringBuffer sb = new StringBuffer();
    			StringBuffer warning = new StringBuffer();
    			boolean existQst = false;
    			for(Object obj : rowdatas){
    				String outStr = "";
    				Vector rowdata = (Vector)obj;
    				String annotation = (String)rowdata.get(1);
    				Entity ent = Entity.createByAnnotationStr(annotation);
    				TypeColor entitytype = (TypeColor)rowdata.get(2);
    				boolean needAssert = false;
    				if(entitytype != null){
    					ent.setEntityType(entitytype.getTypeId());
    					if(entitytype.getTypeId().equals("disease") ||
    							entitytype.getTypeId().equals("complaintsymptom") ||
    							entitytype.getTypeId().equals("testresult") ||
    							entitytype.getTypeId().equals("treatment")){
    						needAssert = true;
    					}
    				}else{
    					int rowno = (Integer)rowdata.get(0);
    					sb.append("��"+rowno+"��ʵ��Ӧѡ��ʵ������\n");
    				}
    				
    				if(ent.getEntity().matches(".*\\d+.*")){
    					int rowno = (Integer)rowdata.get(0);
    					warning.append("��"+rowno+"��ʵ���а�������\n");
    				}
    				TypeColor asserttype = (TypeColor)rowdata.get(3);
    				if(asserttype != null){
    					ent.setAssertType(asserttype.getTypeId());
    				}else{
    					if(needAssert){
    						int rowno = (Integer)rowdata.get(0);
    						sb.append("��"+rowno+"����Ҫѡ����������\n");
    					}
    				}
    				
    				Boolean qst = (Boolean)rowdata.get(4);
    				if(qst != null && qst){
    					ent.setQst(qst);
    					existQst = true;
    				}
    				
    				if(ent.getEntityType() != null){
    					entities.add(ent);
    				}
    				
    			}
	    		
	    		String path;
	    		JFileChooser file = new JFileChooser (GlobalCache.currentPath);
//	    		file.setAcceptAllFileFilterUsed(false);
	    		File tobedeletedFile = null;
	    		if(existQst){
	    			file.setSelectedFile(new File(inputFile.getText()+".ent.qst"));
	    		}else{
	    			file.setSelectedFile(new File(inputFile.getText()+".ent"));
	    			tobedeletedFile = new File(inputFile.getText()+".ent.qst");
	    		}
	    		
	    		
	    		if(file.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
	    		{
	    			path = file.getSelectedFile().getAbsolutePath();
		    		try {
		    			GlobalCache.currentPath = path;	    			
		    			
		    			String errMsg = sb.toString();
		    			if(errMsg.length() == 0){
		    				String warningMsg = warning.toString();
		    				boolean resume = true;
		    				if(warningMsg.length() > 0){
		    					warningMsg = warningMsg +"\n �Ƿ������";
		    					int state = JOptionPane.showConfirmDialog(null, warningMsg, "����", JOptionPane.YES_NO_OPTION);
		    					if(state != JOptionPane.YES_OPTION){
		    						resume = false;
		    					}
		    				}
		    				if(resume){
		    					PrintWriter out = new PrintWriter(path,"UTF-8");
		    					Collections.sort(entities);
		    					for(Entity ent : entities){
		    						out.println(ent.toSave());
		    					}		    				
		    					out.flush();
		    					out.close();
		    					
		    					if(tobedeletedFile !=null && tobedeletedFile.exists()){
		    						tobedeletedFile.delete();
		    					}
		    					
		    					JOptionPane.showMessageDialog(null, "����ɹ�  ·����"+path, "��ʾ",JOptionPane.INFORMATION_MESSAGE);
		    				}else{
		    					JOptionPane.showMessageDialog(null, "��ע���δ����", "��ʾ",JOptionPane.INFORMATION_MESSAGE);
		    				}
		    				
		    			}else{
		    				JOptionPane.showMessageDialog(null,errMsg, "����",
		    						JOptionPane.INFORMATION_MESSAGE);
		    			}
		    			
		    			
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
		    		
		    		
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
				setEntitySelected(textPane,table);
			}
		});
	}
	
	private static void setEntitySelected(JTextPane textPane,JTable table){
		int row = table.getSelectedRow();
		String entityvalue = (String)table.getValueAt(row, table.getColumnModel().getColumnIndex("ʵ��"));
		Entity ent = Entity.createByAnnotationStr(entityvalue);
		setEntityBackground(textPane,ent);
		textPane.setCaretPosition(ent.getStartPos());
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
		
		setEntityPairBackGround(textPane,new Entity[]{ent},TypeColor.SelectedColor);
		
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
		Object columnNames[] = {"�к�","ʵ��", "����","����","��ȷ��"};//����4������
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
						if(getColumnName(column).equals("��ȷ��")){
							return true;
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
			    	if(tc.getFlag() == 1){
			    		DefaultCellEditor editor = (DefaultCellEditor)table.getCellEditor(row, table.getColumnModel().getColumnIndex("����"));
			    		AssertTypeComboxModel model = (AssertTypeComboxModel)((JComboBox)editor.getComponent()).getModel();
			    		if(tc.getTypeId().equals("treatment")){
			    			model.setCondition("treatment");
			    		}else{
			    			model.setCondition("problem");
			    		}
			    	}

				}
			}
		};
		
		table.setSurrendersFocusOnKeystroke(true);
		model.setColumnIdentifiers(columnNames);
		table.setModel(model);
		
		
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
	    table.getColumn("����").setCellRenderer(new TypeCellRender(true));
	    
	    AssertTypeComboxModel atcm = new AssertTypeComboxModel();
	    JComboBox combo2 = new JComboBox(atcm);//�������η��������˵�
	    AssertTypeMouseListener atml = new AssertTypeMouseListener(table,combo2);
	    combo2.getComponent(0).addMouseListener(atml);
	    combo2.setEditable(false);
	    DefaultCellEditor asserteditor = new DefaultCellEditor(combo2);
	    table.getColumn("����").setCellEditor(asserteditor);//����3����Ϊ��������ѡ��
	    table.getColumn("����").setCellRenderer(new AsserttypeRender());
	    
	    table.getColumn("�к�").setPreferredWidth(1);
	    
	    table.getColumn("��ȷ��").setCellEditor(new DefaultCellEditor(new JCheckBox()));
	    addTableMouseListener(textPane,table);
	    table.getColumn("��ȷ��").setCellRenderer(new QuestionalRenderer());
	    
	    return  table;
	}
//	11
	private static JTable createEntityTableForRelation(final JTextPane textPane){
		Object columnNames[] = {"ʵ��", "����","����"};//����4������
		final JTable table = new JTable(null, columnNames);//�������
		DefaultTableModel model = new DefaultTableModel(){
			public boolean isCellEditable(int row, int column)
			{
				return false;
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
					if(tc.getFlag() == 1){
						DefaultCellEditor editor = (DefaultCellEditor)table.getCellEditor(row, table.getColumnModel().getColumnIndex("����"));
						AssertTypeComboxModel model = (AssertTypeComboxModel)((JComboBox)editor.getComponent()).getModel();
						if(tc.getTypeId().equals("treatment")){
							model.setCondition("treatment");
						}else{
							model.setCondition("problem");
						}
					}
					
				}
			}
		};
		
		table.setSurrendersFocusOnKeystroke(true);
		model.setColumnIdentifiers(columnNames);
		table.setModel(model);
		
		
		table.setRowHeight(25);
		
		table.getColumn("����").setCellRenderer(new TypeCellRender(true));
		
		table.getColumn("����").setCellRenderer(new AsserttypeRender());
		
		addTableMouseListener(textPane,table);
		
		return  table;
	}
	
	
	private static class QuestionalRenderer  extends DefaultTableCellRenderer{

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			// TODO Auto-generated method stub
			Boolean bjvalue = (Boolean)table.getValueAt(row, table.getColumnModel().getColumnIndex("��ȷ��"));
			JCheckBox jcb = new JCheckBox();
			if(bjvalue != null && bjvalue){
				jcb.setSelected(true);
				jcb.setBackground(Color.YELLOW);
			}else{
				jcb.setSelected(false);
			}
			return  jcb;				
		}
	}
	
	private static JPanel createEntityButtonPanel(JTextPane textPane,JTable table){
		JPanel btnpanel = new JPanel();
	    
	    JTextField inputFile = new JTextField(40);
		inputFile.setEditable(false);

	    JButton buttonOpen = new JButton("���ļ�");
	    JButton buttonInNE = new JButton("����ʵ��");
	    JButton buttonNE = new JButton("���ʵ�� A");
	    JButton buttonNO = new JButton("ɾ��ʵ�� D");
	    JButton buttonSave = new JButton("�������");
	    JPopupMenu popmenu = new JPopupMenu();
	    
	    btnpanel.add(buttonOpen);
	    btnpanel.add(buttonInNE);
	    btnpanel.add(buttonNE);
	    btnpanel.add(buttonNO);
	    btnpanel.add(inputFile);
	    btnpanel.add(buttonSave);
	    
	    GlobalComponent.addNEButton = new JButton("���ʵ�� A");
	    GlobalComponent.delNEButton = new JButton("ɾ��ʵ�� D");
	    
	    
	    addOpenFileButtonListener(buttonOpen,textPane,inputFile,table,null);
	    addAddNEButtonListener(buttonNE,textPane,table);
	    addExportNEButtonToTab1(buttonSave,table,inputFile);
	    addImportNEButtonListener(buttonInNE,textPane,table);
	    addDeleteNEButtonListener(buttonNO,textPane,table);
//	    addPopupMenuListener();
	    
	    copyListener(buttonNE,GlobalComponent.addNEButton);
	    copyListener(buttonNO,GlobalComponent.delNEButton);
	    
	    GlobalComponent.entiyPopupmenu = popmenu;

	    
	    return btnpanel;
	}
	
	
	private static void copyListener(JButton from, JButton to){
		ActionListener[] als = from.getActionListeners();
		for(ActionListener al : als){
			to.addActionListener(al);
		}
	}
	
	
	private static void addEntityAnnotationTab(JTabbedPane tabbedPane, String text)//ҳ��һ��ʵ���ע
	{
		JPanel entityPanel = new JPanel();//�½�һ������
		entityPanel.setLayout(new BorderLayout());//��BorderLayout�԰�����в���
		
		final JTextPane entityTextPane = new JTextPane();//�½�һ���ı��༭��������ʾ�ı������в���
		entityTextPane.setEditable(false);//���ı��ǲ������û��ڿ��ڱ༭��
	    
		final JTable table = createEntityTable(entityTextPane,true);	  
		addTextPaneListener(entityTextPane,table);
		
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
	
	
	private static void addTextPaneListener(final JTextPane entityTextPane,final JTable table){
		MouseListener listenser = new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON3){
					boolean existed = false;
					int pos = entityTextPane.viewToModel(e.getPoint());
					int rows = table.getRowCount();
					for(int i = 0;i < rows;i ++){
						String entityvalue = (String)table.getValueAt(i, table.getColumnModel().getColumnIndex("ʵ��"));
						Entity ent = Entity.createByAnnotationStr(entityvalue);
						if(ent.getStartPos() <= pos && ent.getEndPos() >= pos){
							existed = true;
							table.setRowSelectionInterval(i, i);
							setEntitySelected(entityTextPane,table);
							Rectangle rect = table.getCellRect(i, 0, true);  
				    		table.scrollRectToVisible(rect);
							break;
						}
					}
					String selectedText = entityTextPane.getSelectedText();
					if(existed || (!existed && selectedText != null && selectedText.length() > 0)){
						showMenu(existed).show(entityTextPane, (int)e.getPoint().getX(), (int)e.getPoint().getY());
					}
				}
			}
			
		};
		
		entityTextPane.addMouseListener(listenser);
	}
	
	static void createRelPopMenu(final JTable table){
		JPopupMenu menu = new JPopupMenu();
		final JButton btn1 = new JButton("����ʵ�壨�飩1");
		final JButton btn2 = new JButton("����ʵ�壨�飩2");
		menu.add(btn1);
		menu.add(btn2);
		
		ActionListener al = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int i = table.getSelectedRow();
				String entityvalue = (String)table.getValueAt(i, table.getColumnModel().getColumnIndex("ʵ��"));
				String entityType = ((TypeColor)table.getValueAt(i, table.getColumnModel().getColumnIndex("����"))).getTypeId();
				Entity ent = Entity.createByAnnotationStr(entityvalue);
				ent.setEntityType(entityType);
				int addResult = 2;
				if(e.getSource() == btn1){
					addResult = addEntToList(GlobalComponent.entList1,GlobalComponent.entList2,ent,GlobalComponent.entityTxt1);					
				}else if(e.getSource() == btn2){
					addResult = addEntToList(GlobalComponent.entList2,GlobalComponent.entList1,ent,GlobalComponent.entityTxt2);
				}
				
				if(GlobalComponent.relationPopupmenu.isVisible()){
					GlobalComponent.relationPopupmenu.setVisible(false);
				}
				
				if(addResult == 2){
					JOptionPane.showMessageDialog(null,"ʵ�����Ͳ�һ��", "����",JOptionPane.INFORMATION_MESSAGE);
				}else if(addResult == 1){
					JOptionPane.showMessageDialog(null,"��ʵ���Ѽ���", "����",JOptionPane.INFORMATION_MESSAGE);
				}
			}
		};
		btn1.addActionListener(al);
		btn2.addActionListener(al);
		
		GlobalComponent.relationPopupmenu = menu;
		
	}
	
	private static int addEntToList( ArrayList<Entity> entList, ArrayList<Entity> cannotExist,Entity ent,JTextField tf){
		int canAdd = 2;
		if(cannotExist.contains(ent)){//�ж��Ƿ�����ͬ�ļ�����
			canAdd = 1;
			return canAdd;
		}
		if(entList.contains(ent)){//�ж��Ƿ�����ͬ�ļ�����
			canAdd = 1;
			return canAdd;
		}
		if(entList.size() > 0){
			if(entList.get(0).getEntityType().equals(ent.getEntityType())){
				canAdd = 0;
			}else{
				if((entList.get(0).getEntityType().equals("complaintsymptom") && ent.getEntityType().equals("testresult"))||
						(entList.get(0).getEntityType().equals("testresult") && ent.getEntityType().equals("complaintsymptom"))){
					canAdd = 0;
				}else{
					canAdd = 2;
				}
			}
		}else{
			canAdd = 0;
		}
		if(canAdd == 0){
			entList.add(ent);
			tf.setText(tf.getText()+ent.getEntity() + "�� ");
		}
		
		return canAdd;
	}
	
	private static void addRelTextPaneListener(final JTextPane entityTextPane,final JTable table){
		MouseListener listenser = new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON3){
					boolean existed = false;
					int pos = entityTextPane.viewToModel(e.getPoint());
					int rows = table.getRowCount();
					for(int i = 0;i < rows;i ++){
						String entityvalue = (String)table.getValueAt(i, table.getColumnModel().getColumnIndex("ʵ��"));
						Entity ent = Entity.createByAnnotationStr(entityvalue);
						if(ent.getStartPos() <= pos && ent.getEndPos() >= pos){
							existed = true;
							table.setRowSelectionInterval(i, i);
							setEntitySelected(entityTextPane,table);
							Rectangle rect = table.getCellRect(i, 0, true);  
							table.scrollRectToVisible(rect);
							break;
						}
					}
					String selectedText = entityTextPane.getSelectedText();
					if(existed || (!existed && selectedText != null && selectedText.length() > 0)){
						GlobalComponent.relationPopupmenu.show(entityTextPane, (int)e.getPoint().getX(), (int)e.getPoint().getY());
					}
				}
			}
			
		};
		
		entityTextPane.addMouseListener(listenser);
	}
	
	
	
	
	private static void hidePopupMenu(){
		if(GlobalComponent.entiyPopupmenu.isVisible()){
			GlobalComponent.entiyPopupmenu.setVisible(false);
		}
	}
	
	private static JPopupMenu showMenu(boolean existed){
		GlobalComponent.entiyPopupmenu.removeAll();
		if(existed){
			GlobalComponent.entiyPopupmenu.add(GlobalComponent.delNEButton);
		}else{
			GlobalComponent.entiyPopupmenu.add(GlobalComponent.addNEButton);
		}
		return GlobalComponent.entiyPopupmenu;
	}
	
	
	private static JTable createRelationTable(final JTextPane textPane ){
		
		Object columnNames[] = {"ʵ��(��)1", "ʵ��(��)2","��ϵ����","��ȷ��"};//����4������
		final JTable table = new JTable(null, columnNames);//�������
		DefaultTableModel model = new DefaultTableModel(){
			public boolean isCellEditable(int row, int column)
			{
				if(getColumnName(column).equals("��ϵ����") ){
					if(table.getValueAt(row, table.getColumnModel().getColumnIndex("ʵ��(��)2")).equals("")){
						return false;
					}
					return true;
				}
				if(getColumnName(column).equals("��ȷ��")){
					return true;
				}

				return false;//��������༭
			}
			public void setValueAt(Object aValue, int row, int column) {
				super.setValueAt(aValue, row, column);
				if(column == table.getColumnModel().getColumnIndex("��ϵ����")){
					TypeColor tc = (TypeColor)aValue;
					if(tc == null){
						return;
					}
					if(tc.getFlag() == 0){
						setValueAt(null,row,column);
					}else{
						GlobalComponent.relationList.get(row).setRelationType(tc.getTypeId());
					}
				}else{
					NewRelation nr = GlobalComponent.relationList.get(row);
					String condition = nr.getEnts1Type() + nr.getEnts2Type();
					DefaultCellEditor editor = (DefaultCellEditor)table.getCellEditor(row, table.getColumnModel().getColumnIndex("��ϵ����"));
					RelationTypeComboxModel model = (RelationTypeComboxModel)((JComboBox)editor.getComponent()).getModel();
					model.setCondition(condition);
				}
				
			}
		};
		
		table.setSurrendersFocusOnKeystroke(true);
		model.setColumnIdentifiers(columnNames);
		table.setModel(model);
		table.getColumn("��ϵ����").setCellRenderer(new TypeCellRender(false));
		
		table.setRowHeight(25);
		
		RelationTypeComboxModel rtcm = new RelationTypeComboxModel();
	    JComboBox combo2 = new JComboBox(rtcm);//������ϵ���������˵�
	    combo2.setRenderer(new ComboxRender(false));
	    combo2.setEditable(false);
	    RelationTypeListener atml = new RelationTypeListener(table,combo2);
	    combo2.getComponent(0).addMouseListener(atml);
	    combo2.addFocusListener(new RelationTypeItemListener(table,combo2));
	    RelationTypeCellEditor reltypeeditor = new RelationTypeCellEditor(combo2);
	    table.getColumn("��ϵ����").setCellEditor(reltypeeditor);//����3����Ϊ��������ѡ��
	    
	    addRelationTableMouseListener(textPane,table);
		
	    table.getColumn("ʵ��(��)1").setCellRenderer(new RelationEntityRenderer());
	    table.getColumn("ʵ��(��)2").setCellRenderer(new RelationEntityRenderer());
	    
	    table.getColumn("��ȷ��").setCellEditor(new DefaultCellEditor(new JCheckBox()));
	    table.getColumn("��ȷ��").setCellRenderer(new QuestionalRenderer());

		
		return table;
	}
	
	
	private static void setEntityPairBackGround(JTextPane textPane,Entity[] ents,Color color){
		if(GlobalCache.pastSelectedEntityPair!= null){
			for(Entity ent : GlobalCache.pastSelectedEntityPair){
				setEntityBackgroundColor(textPane,ent,Color.WHITE);
			}
		}
		for(Entity ent : ents){
			
			setEntityBackgroundColor(textPane,ent,color);
		}
		
		GlobalCache.pastSelectedEntityPair = ents;
	}
	
	
	private static void addRelationTableMouseListener(final JTextPane textPane,final JTable table){
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				int row = table.getSelectedRow();
				//�ñ���ɫ
				TypeColor tc = (TypeColor)table.getValueAt(row, table.getColumnModel().getColumnIndex("��ϵ����"));
				Entity[] allents = GlobalComponent.relationList.get(row).toAllEntity();
				
				if(tc != null){
					setEntityPairBackGround(textPane,allents,tc.getColor());
				}else{
					setEntityPairBackGround(textPane,allents,TypeColor.SelectedColor);
				}
				textPane.setCaretPosition(allents[0].getStartPos());
			}
		});
	}
	
	
	private static void addAddrealtionBtnListener(JButton addRelationBtn,final JTable relationTable){
		addRelationBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(GlobalComponent.onlyentgroup.isSelected()){
					if(GlobalComponent.entList1.size() == 0){
						JOptionPane.showMessageDialog(null, "ʵ��(��)1����Ϊ��", "��ʾ",JOptionPane.INFORMATION_MESSAGE);
					}else{
						NewRelation nr = new NewRelation();
						nr.addEnts1(GlobalComponent.entList1);
						GlobalComponent.relationList.add(nr);
						Object[] rowData = new Object[]{nr.ents1ToAnnotation(),"",null};
						((DefaultTableModel)relationTable.getModel()).addRow(rowData);
						ressetCurrentSelecttion();
					}
				}else{
					if(GlobalComponent.entList1.size() == 0 || GlobalComponent.entList2.size() == 0){
						JOptionPane.showMessageDialog(null, "ʵ��(��)1��ʵ��(��)2������Ϊ��", "��ʾ",JOptionPane.INFORMATION_MESSAGE);
					}else{
						NewRelation nr = new NewRelation();
						nr.addEnts1(GlobalComponent.entList1);
						nr.addEnts2(GlobalComponent.entList2);						
						if(nr.existRelation()){
							GlobalComponent.relationList.add(nr);
							Object[] rowData = new Object[]{nr.ents1ToAnnotation(),nr.ents2ToAnnotation(),null};
							((DefaultTableModel)relationTable.getModel()).addRow(rowData);
							ressetCurrentSelecttion();
							
							//�޸�comboxģ��
							String condition = nr.getEnts1Type() + nr.getEnts2Type();
							DefaultCellEditor editor = (DefaultCellEditor)relationTable.getCellEditor(relationTable.getSelectedRow(), relationTable.getColumnModel().getColumnIndex("��ϵ����"));
							JComboBox combox = (JComboBox)editor.getComponent();
							RelationTypeComboxModel model = (RelationTypeComboxModel)combox.getModel();
							model.setCondition(condition);
							combox.setModel(model);
							
							
						}else{
							JOptionPane.showMessageDialog(null, "ʵ��(��)1��ʵ��(��)2�����ڹ�ϵ", "��ʾ",JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
				
			}
		});
	}
	
	
	 private static void addDeleteRelBtnListener(JButton buttonNORel,final JTextPane entityTextPane,final JTable relationTable){
		 buttonNORel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int row = relationTable.getSelectedRow();
					Entity[] allents = GlobalComponent.relationList.get(row).toAllEntity();
					setEntityPairBackGround(entityTextPane,allents,Color.WHITE);
					((DefaultTableModel)relationTable.getModel()).removeRow(row);
					GlobalComponent.relationList.remove(row);
				}
			});
	 }
	 
	private static void  addImportRelBtnListener(JButton buttonInRel,final JTable relationTable){
		 buttonInRel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GlobalComponent.relationList.clear();
		    		JFileChooser j=new JFileChooser(GlobalCache.currentPath);//�ļ�ѡ����
		    		j.setFileFilter(new EmrFileFiller(".rel,.qst"));
		    	    if(j.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
		    	    	 try{
		 	    	    	File f=j.getSelectedFile();
		 	    	    	importRelation(f,relationTable);
		    	    		
		 	    	    	
		    	    	 }catch(Exception ex){
		    	    		 
		    	    	 }
		    	    }
				}
			});
	 }
	
	private static void importRelation(File f,JTable relationTable)throws Exception{
		clearTable(relationTable);
		GlobalComponent.relationList.clear();
		FileInputStream in=new FileInputStream(f);
		GlobalCache.currentPath = f.getAbsolutePath();
		BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		String line = null;
		while((line = br.readLine())!= null){
			if(line.length() > 0){
				NewRelation rel = NewRelation.createBySaveStr(line);
				GlobalComponent.relationList.add(rel);
			}
		}
		
		DefaultTableModel model = (DefaultTableModel)relationTable.getModel();
		Collections.sort(GlobalComponent.relationList);
		for(NewRelation rel : GlobalComponent.relationList){
			Object[] rowData = new Object[]{rel.ents1ToAnnotation(),rel.ents2ToAnnotation(),TypeColorMap2.getType(rel.getRelationType()),rel.isQst()};
			rel.setQst(false);
			model.addRow(rowData);
		}
		br.close();
	}
	 
	 private static void addSaveRelBtnLinstener(JButton buttonSave,final JTable relationTable,final JTextField inputFile){
		 buttonSave.addActionListener(new ActionListener()
		    {
		    	public void actionPerformed(ActionEvent e)
		    	{
		    		
		    		String path;
		    		
		    		if(relationTable.getRowCount() == 0){
		    			return;
		    		}
		    		boolean existQst = false;
		    		DefaultTableModel model = (DefaultTableModel)relationTable.getModel();
		    		Vector rowdatas = model.getDataVector();
		    		for(int i = 0;i < rowdatas.size(); i ++){
		    			Vector rowdata = (Vector)rowdatas.get(i);
		    			Boolean qst = (Boolean)rowdata.get(3);
		    			if(qst != null && qst){
		    				GlobalComponent.relationList.get(i).setQst(qst);
		    				existQst = true;
		    			}
		    		}
		    		
		    		JFileChooser file = new JFileChooser (GlobalCache.currentPath);
		    		file.setAcceptAllFileFilterUsed(false);
		    		File tobedeletedFile = null;
		    		File saveFile = null;
		    		
		    		if(existQst){
		    			saveFile = new File(inputFile.getText()+".rel.qst");
		    			tobedeletedFile = new File(inputFile.getText()+".rel");
		    		}else{
		    			saveFile = new File(inputFile.getText()+".rel");
		    			tobedeletedFile = new File(inputFile.getText()+".rel.qst");
		    		}
		    		file.setSelectedFile(saveFile);
		    		
		    		if(file.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
		    		{
		    			path = file.getSelectedFile().getAbsolutePath();
			    		try {
			    			GlobalCache.currentPath = path;
			    			PrintWriter out = new PrintWriter(path,"UTF-8");		    			
			    			
			    			Collections.sort(GlobalComponent.relationList);
			    			for(NewRelation nr : GlobalComponent.relationList){
			    				out.println(nr.toSave());
			    			}
			    			out.flush();
			    			out.close();
			    			
			    			if(tobedeletedFile !=null && tobedeletedFile.exists()){
	    						tobedeletedFile.delete();
	    					}
			    			importRelation(saveFile, relationTable);
			    			
						} catch (Exception e1) {
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
//		JPanel btnpanel = new JPanel(new GridLayout(0,1));
		
		JPanel btnpanel1 = new JPanel();
	    
	    JTextField inputFile = new JTextField(40);
		inputFile.setEditable(false);

	    JButton buttonOpen = new JButton("���ļ�"); 
	    JButton buttonInNE = new JButton("����ʵ��");
	    JButton buttonInRel = new JButton("�����ϵ");
	    JButton buttonSave = new JButton("�������");
	    btnpanel1.add(buttonOpen);
	    btnpanel1.add(inputFile);
	    btnpanel1.add(buttonInNE);
	    btnpanel1.add(buttonInRel);
	    btnpanel1.add(buttonSave);
	    
	    addOpenFileButtonListener(buttonOpen,entityTextPane,inputFile,entityTable,relationTable);
	    addImportNEForRelButtonListener(buttonInNE,entityTextPane,entityTable);
	    addImportRelBtnListener(buttonInRel,relationTable);
	    addSaveRelBtnLinstener(buttonSave,relationTable,inputFile);
	    
	    return btnpanel1;
	}
	
	private static void addButtonClearListener(JButton btn){
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ressetCurrentSelecttion();
			}
		});
		
	}
	
	private static void ressetCurrentSelecttion(){
		GlobalComponent.entityTxt1.setText("");
		GlobalComponent.entityTxt2.setText("");
		GlobalComponent.entList1.clear();
		GlobalComponent.entList2.clear();
		GlobalComponent.onlyentgroup.setSelected(false);
	}
	
	static  JPanel createEntRelBtnPanel(final JTextPane entityTextPane,final JTable entityTable,final JTable relationTable){
		JPanel panel1 = new JPanel();
		panel1.setLayout(new BorderLayout());
		JLabel entityBtn1 = new JLabel("  ʵ��(��)1  ");
		JTextField entityTxt1 = new JTextField();
		entityTxt1.setEditable(false);
		panel1.add(entityBtn1,BorderLayout.WEST);
		panel1.add(entityTxt1,BorderLayout.CENTER);		
		JPanel panel2 = new JPanel();
		panel2.setLayout(new BorderLayout());
		JLabel entityBtn2 = new JLabel("  ʵ��(��)2  ");
		JTextField entityTxt2 = new JTextField();
		entityTxt2.setEditable(false);
		panel2.add(entityBtn2,BorderLayout.WEST);
		panel2.add(entityTxt2,BorderLayout.CENTER);
		
		GlobalComponent.entityTxt1 = entityTxt1;
		GlobalComponent.entityTxt2 = entityTxt2;
		
		
		
		JButton addRelationBtn = new JButton(" ���ʵ���ϵ");
	    JButton buttonNORel = new JButton(" ɾ��ʵ���ϵ");
	    JPanel paneladddel = new JPanel();
	    paneladddel.setLayout(new BorderLayout());
	    paneladddel.add(addRelationBtn,BorderLayout.WEST);
	    paneladddel.add(buttonNORel,BorderLayout.EAST);
	    JButton buttonClear = new JButton("�����ѡʵ��(��)");
	    JCheckBox onlyentgroup = new JCheckBox("ֻ��עʵ����");
	    JPanel p = new JPanel();
	    p.setLayout(new BorderLayout());
	    p.add(onlyentgroup,BorderLayout.WEST);
	    p.add(buttonClear,BorderLayout.EAST);
	    addButtonClearListener(buttonClear);
	    GlobalComponent.onlyentgroup = onlyentgroup;
	    
	    JPanel panel00 = new JPanel();
	    panel00.setLayout(new BorderLayout());
	    panel00.add(panel1,BorderLayout.CENTER);
	    panel00.add(p,BorderLayout.EAST);
	    
	    JPanel panel01 = new JPanel();
	    panel01.setLayout(new BorderLayout());
	    panel01.add(panel2,BorderLayout.CENTER);
	    panel01.add(paneladddel,BorderLayout.EAST);
	    
	    JPanel entityBtnPanel = new JPanel();
		entityBtnPanel.setLayout(new BorderLayout());
	    entityBtnPanel.add(panel00,BorderLayout.NORTH);
	    entityBtnPanel.add(panel01,BorderLayout.SOUTH);
	    
	    addAddrealtionBtnListener(addRelationBtn,relationTable);
	    addDeleteRelBtnListener(buttonNORel,entityTextPane,relationTable);
	    return entityBtnPanel;
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
		final JTable entityTable = createEntityTableForRelation(entityTextPane);
		
		entitySplitPane.setLeftComponent(new JScrollPane(entityTable));
		entitySplitPane.setRightComponent(new JScrollPane(entityTextPane));
		
		createRelPopMenu(entityTable);
		addRelTextPaneListener(entityTextPane,entityTable);
		
	    JSplitPane mainSplitPane = new JSplitPane();
	    mainSplitPane.setSize(1024, 768);
	    mainSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
	    mainSplitPane.setDividerLocation(0.5);   
	    
	    mainSplitPane.setTopComponent(entitySplitPane);
	    
	    final JTable relationTable = createRelationTable(entityTextPane);	

	    JPanel btnpanel = createRelationButtonPanel(entityTextPane,entityTable,relationTable);
	    JPanel btnpanel2 = createEntRelBtnPanel(entityTextPane,entityTable,relationTable);
	    relationPanel.add(btnpanel,BorderLayout.NORTH);
	    
	    JPanel bottomPanel = new JPanel();
	    bottomPanel.setLayout(new BorderLayout());
	    bottomPanel.add(btnpanel2,BorderLayout.NORTH);
	    bottomPanel.add(new JScrollPane(relationTable),BorderLayout.CENTER);

	    mainSplitPane.setBottomComponent(bottomPanel);
	    relationPanel.add(mainSplitPane,BorderLayout.CENTER);
		
	    tabbedPane.addTab(text, relationPanel);
	    
	}
	
	

	public static void main(String args[]) //������
	{
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame f = new JFrame("WIʵ���ҵ��Ӳ���ʵ���ʵ���ϵ��ע����");
//		f.setResizable(false);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container content = f.getContentPane();
		JTabbedPane tabbedPane = new JTabbedPane();
		addEntityAnnotationTab(tabbedPane, "ʵ���ע");
		addRelationAnnotaionTab(tabbedPane, "ʵ���ϵ��ע");
		tabbedPane.setSelectedIndex(1);
		content.add(tabbedPane, BorderLayout.CENTER);
		f.setSize(1024, 768);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}
}

