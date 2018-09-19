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

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;


import java.awt.event.KeyEvent;
import java.util.*;

public class NewEmrAnnotator {
    public static int wordSize = 16;
    public static int chooseSize = 18;


    private static void addOpenFileButtonListener(JButton buttonOpen, final JTextPane textPane, final JTextField inputFile, final JTable entityTable, final JTable relationTable) {
        buttonOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JFileChooser j = new JFileChooser(GlobalCache.currentPath);//文件选择器
                j.setFileFilter(new EmrFileFiller(".txt"));
                if (j.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    try {
                        File f = j.getSelectedFile();
                        GlobalCache.currentPath = f.getAbsolutePath();
                        FileInputStream in2 = new FileInputStream(f);
                        BufferedReader br = new BufferedReader(new InputStreamReader(in2, "UTF-8"));
                        StringBuffer sb = new StringBuffer();
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        textPane.setText(sb.toString());
                        br.close();

                        SimpleAttributeSet attr = new SimpleAttributeSet();
                        StyleConstants.setForeground(attr, Color.black);
                        StyleConstants.setFontSize(attr, wordSize);
                        ((DefaultStyledDocument) textPane.getDocument()).setCharacterAttributes(0, textPane.getText().length(), attr, false);

                        inputFile.setText(f.getAbsolutePath());
                        if (entityTable != null) {
                            clearTable(entityTable);
                        }
                        if (relationTable != null) {
                            clearTable(relationTable);
                        }

                        File entFile = new File(j.getSelectedFile().getPath() + ".ent");
                        if (entFile.exists()) {
                            FileInputStream in = new FileInputStream(entFile);
                            GlobalCache.currentPath = entFile.getAbsolutePath();
                            BufferedReader br2 = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                            String line2 = null;
                            Entity sudo = new Entity();
                            sudo.setStartPos(0);
                            sudo.setEndPos(textPane.getText().length());
                            clearEntityColor(textPane, sudo);
                            DefaultTableModel model = (DefaultTableModel) entityTable.getModel();
                            ArrayList<Entity> ents = new ArrayList<Entity>();
                            while ((line2 = br2.readLine()) != null) {
                                if (line2.length() > 0) {
                                    Entity ent = Entity.createBySaveStr(line2);
                                    ents.add(ent);
                                }
                            }
                            Collections.sort(ents);
                            int rowno = 0;
                            for (Entity ent : ents) {
                                rowno++;
                                TypeColor assertType = null;
                                TypeColor shengliBingLiType = null;
                                if (ent.getAssertType() != null) {
                                    assertType = TypeColorMap.getType(ent.getAssertType());
                                }
                                if (ent.getShengliBingliType() != null) {
                                    shengliBingLiType = TypeColorMap.getType(ent.getShengliBingliType());
                                }

                                Object[] rowData = new Object[]{rowno, ent.toAnnotation(), TypeColorMap.getType(ent.getEntityType()), assertType, ent.isQst(), shengliBingLiType};
                                model.addRow(rowData);
                                setEntityForeground(textPane, ent, TypeColorMap.getType(ent.getEntityType()));

                            }
                            br2.close();
                        }
                        File relFile = new File(j.getSelectedFile().getPath() + ".rel");
                        if (relFile.exists() && relationTable != null) {
                            FileInputStream relIn = new FileInputStream(relFile);
                            GlobalCache.currentPath = relFile.getAbsolutePath();
                            BufferedReader relBr = new BufferedReader(new InputStreamReader(relIn, "UTF-8"));
                            String relLine = null;
                            DefaultTableModel tableModel = (DefaultTableModel) relationTable.getModel();
                            clearTable(relationTable);
                            while ((relLine = relBr.readLine()) != null) {
                                if (relLine.length() > 0) {
                                    Relation rel = Relation.createBySaveStr(relLine);
                                    Object[] rowData = new Object[]{rel.getEnt1().toAnnotationAndType(), rel.getEnt2().toAnnotationAndType(), TypeColorMap.getType(rel.getRelationType())};
                                    tableModel.addRow(rowData);
                                }
                            }
                            relBr.close();
                        }
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }
                textPane.setCaretPosition(0);

            }
        });

    }

    private static void clearTable(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        for (int row = model.getRowCount() - 1; row >= 0; row--) {
            model.removeRow(row);
        }
    }

    private static void addImportNEButtonListener(JButton buttonInNE, final JTextPane textPane, final JTable table) {
        buttonInNE.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JFileChooser j = new JFileChooser(GlobalCache.currentPath);//文件选择器
                j.setFileFilter(new EmrFileFiller(".ent,.qst"));
                if (j.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    try {
                        clearTable(table);
                        File f = j.getSelectedFile();
                        FileInputStream in = new FileInputStream(f);
                        GlobalCache.currentPath = f.getAbsolutePath();
                        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                        StringBuffer sb = new StringBuffer();
                        String line = null;
                        Entity sudo = new Entity();
                        sudo.setStartPos(0);
                        sudo.setEndPos(textPane.getText().length());
                        clearEntityColor(textPane, sudo);
                        DefaultTableModel model = (DefaultTableModel) table.getModel();
                        ArrayList<Entity> ents = new ArrayList<Entity>();
                        while ((line = br.readLine()) != null) {
                            if (line.length() > 0) {
                                Entity ent = Entity.createBySaveStr(line);
                                ents.add(ent);
                            }
                        }
                        Collections.sort(ents);
                        int rowno = 0;
                        for (Entity ent : ents) {
                            rowno++;
                            TypeColor assertType = null;
                            TypeColor shengliBingLiType = null;
                            if (ent.getAssertType() != null) {
                                assertType = TypeColorMap.getType(ent.getAssertType());
                            }
                            if (ent.getShengliBingliType() != null) {
                                shengliBingLiType = TypeColorMap.getType(ent.getShengliBingliType());
                            }

                            Object[] rowData = new Object[]{rowno, ent.toAnnotation(), TypeColorMap.getType(ent.getEntityType()), assertType, ent.isQst(), shengliBingLiType};
                            model.addRow(rowData);
                            setEntityForeground(textPane, ent, TypeColorMap.getType(ent.getEntityType()));

                        }


                        br.close();

                    } catch (Exception ex) {

                    }
                }

            }
        });
    }

    private static void addAddNEButtonListener(JButton buttonNE, final JTextPane textPane, final JTable table) {
//	    JButton buttonNE;//添加实体按键，通过滑动鼠标选中文中一部分，之后点击添加实体即可
//	    buttonNE = new JButton("添加实体 A");
        buttonNE.setMnemonic(KeyEvent.VK_A);
        buttonNE.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int p0 = textPane.getSelectionStart();//选中内容的起始位置
                int p1 = textPane.getSelectionEnd();//选中内容的终止位置

                hidePopupMenu();

                if (p0 < p1) {

                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    try {
                        Entity entity = new Entity();
                        entity.setEntity(textPane.getText(p0, p1 - p0));
                        entity.setStartPos(p0);
                        entity.setEndPos(p1);
                        entity = EntityCleaner.cleanEntity(entity);
                        String annotationStr = entity.toAnnotation();
                        model.addRow(new Object[]{table.getRowCount() + 1, annotationStr, null, null});
                        int row = model.getRowCount() - 1;
                        table.setRowSelectionInterval(row, row);

                        setEntityBackground(textPane, entity);

                        Rectangle rect = table.getCellRect(table.getRowCount() - 1, 0, true);
                        table.scrollRectToVisible(rect);


                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }
                    table.repaint();
                } else {
                    JOptionPane.showMessageDialog(null, "请先选中一个合法的实体", "提示",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }


    private static void addDeleteNEButtonListener(JButton buttonNO, final JTextPane textPane, final JTable table) {
//	    JButton buttonNO;//删除一个实体
//	    buttonNO = new JButton("删除实体 D");
        buttonNO.setMnemonic(KeyEvent.VK_D);
        buttonNO.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hidePopupMenu();

                int response = JOptionPane.showConfirmDialog(null, "您是否希望清除当前选中实体？", "提示", JOptionPane.YES_NO_OPTION);

                if (response == 0) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        String annoationStr = (String) table.getValueAt(row, table.getColumnModel().getColumnIndex("实体"));
                        Entity ent = Entity.createByAnnotationStr(annoationStr);
                        clearEntityColor(textPane, ent);
                        ((DefaultTableModel) table.getModel()).removeRow(row);
                    } else {
                        JOptionPane.showMessageDialog(null, "请先选中一个已经标注的实体", "提示",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });
    }

    //E=脑梗死 P=p0:p1 T=疾病 A=当前的
    private static void addExportNEButtonToTab1(JButton buttonSave, final JTable table, final JTextField inputFile) {
        buttonSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (table.getRowCount() == 0) {
                    return;
                }
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                Vector rowdatas = model.getDataVector();
                ArrayList<Entity> entities = new ArrayList<Entity>();
                StringBuffer sb = new StringBuffer();
                StringBuffer warning = new StringBuffer();
                boolean existQst = false;
                for (Object obj : rowdatas) {
                    String outStr = "";
                    Vector rowdata = (Vector) obj;
                    String annotation = (String) rowdata.get(1);
                    Entity ent = Entity.createByAnnotationStr(annotation);
                    TypeColor entitytype = (TypeColor) rowdata.get(2);
                    TypeColor shengliBingliType = (TypeColor) rowdata.get(5);
                    if (shengliBingliType != null) {
                        ent.setShengliBingliType(shengliBingliType.getTypeId());
                    }
                    boolean needAssert = false;
                    if (entitytype != null) {
                        ent.setEntityType(entitytype.getTypeId());
                        if (entitytype.getTypeId().equals("disease") ||
                                entitytype.getTypeId().equals("complaintsymptom") ||
                                entitytype.getTypeId().equals("testresult") ||
                                entitytype.getTypeId().equals("treatment")) {
                            needAssert = true;
                        }
                    } else {
                        int rowno = (Integer) rowdata.get(0);
                        sb.append("第" + rowno + "行实体应选择实体类型\n");
                    }

                    if (ent.getEntity().matches(".*\\d+.*")) {
                        int rowno = (Integer) rowdata.get(0);
                        warning.append("第" + rowno + "行实体中包含数字\n");
                    }
                    TypeColor asserttype = (TypeColor) rowdata.get(3);
                    if (asserttype != null) {
                        ent.setAssertType(asserttype.getTypeId());
                    } else {
                        if (needAssert) {
                            int rowno = (Integer) rowdata.get(0);
                            sb.append("第" + rowno + "行需要选择修饰类型\n");
                        }
                    }

                    Boolean qst = (Boolean) rowdata.get(4);
                    if (qst != null && qst) {
                        ent.setQst(qst);
                        existQst = true;
                    }

                    if (ent.getEntityType() != null) {
                        entities.add(ent);
                    }

                }

                String path;
                JFileChooser file = new JFileChooser(GlobalCache.currentPath);
//	    		file.setAcceptAllFileFilterUsed(false);
                File tobedeletedFile = null;
                if (existQst) {
                    file.setSelectedFile(new File(inputFile.getText() + ".ent.qst"));
                } else {
                    file.setSelectedFile(new File(inputFile.getText() + ".ent"));
                    tobedeletedFile = new File(inputFile.getText() + ".ent.qst");
                }


                if (file.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    path = file.getSelectedFile().getAbsolutePath();
                    try {
                        GlobalCache.currentPath = path;

                        String errMsg = sb.toString();
                        if (errMsg.length() == 0) {
                            String warningMsg = warning.toString();
                            boolean resume = true;
                            if (warningMsg.length() > 0) {
                                warningMsg = warningMsg + "\n 是否继续？";
                                int state = JOptionPane.showConfirmDialog(null, warningMsg, "警告", JOptionPane.YES_NO_OPTION);
                                if (state != JOptionPane.YES_OPTION) {
                                    resume = false;
                                }
                            }
                            if (resume) {
                                PrintWriter out = new PrintWriter(path, "UTF-8");
                                Collections.sort(entities);
                                for (Entity ent : entities) {
                                    out.println(ent.toSave());
                                }
                                out.flush();
                                out.close();

                                if (tobedeletedFile != null && tobedeletedFile.exists()) {
                                    tobedeletedFile.delete();
                                }

                                JOptionPane.showMessageDialog(null, "保存成功  路径：" + path, "提示", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null, "标注结果未保存", "提示", JOptionPane.INFORMATION_MESSAGE);
                            }

                        } else {
                            JOptionPane.showMessageDialog(null, errMsg, "错误",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }


                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }


                } else {
                    JOptionPane.showMessageDialog(null, "已取消  未保存", "提示",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

    }


    private static void addTableMouseListener(final JTextPane textPane, final JTable table) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
//				int row = table.getSelectedRow();
//				String entityvalue = (String)table.getValueAt(row, table.getColumnModel().getColumnIndex("实体"));
//				Entity ent = Entity.createByAnnotationStr(entityvalue);
//				setEntityBackground(textPane,ent);
//				GlobalCache.pastSelectedEntity = ent;
//				textPane.setCaretPosition(ent.getStartPos());
                setEntitySelected(textPane, table);
            }
        });
    }

    private static void setEntitySelected(JTextPane textPane, JTable table) {
        int row = table.getSelectedRow();
        String entityvalue = (String) table.getValueAt(row, table.getColumnModel().getColumnIndex("实体"));
        Entity ent = Entity.createByAnnotationStr(entityvalue);
        setEntityBackground(textPane, ent);
        GlobalCache.pastSelectedEntity = ent;
        textPane.setCaretPosition(ent.getStartPos());
    }

    private static void clearEntityColor(JTextPane textPane, Entity ent) {
        int p0 = ent.getStartPos();
        int p1 = ent.getEndPos();
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setBackground(attr, Color.WHITE);
        StyleConstants.setForeground(attr, Color.BLACK);
        StyleConstants.setFontSize(attr, chooseSize);
        ((DefaultStyledDocument) textPane.getDocument()).setCharacterAttributes(p0, p1 - p0, attr, false);
    }

    private static void setEntityForeground(JTextPane textPane, Entity ent, TypeColor tc) {
        if (tc != null) {
            int p0 = ent.getStartPos();
            int p1 = ent.getEndPos();
            SimpleAttributeSet attr = new SimpleAttributeSet();
            StyleConstants.setForeground(attr, tc.getColor());
            StyleConstants.setFontSize(attr, chooseSize);
            ((DefaultStyledDocument) textPane.getDocument()).setCharacterAttributes(p0, p1 - p0, attr, false);
        }
    }

    private static void setEntityBackground(JTextPane textPane, Entity ent) {
        if (GlobalCache.pastSelectedEntity != null) {
            setEntityBackgroundColor(textPane, GlobalCache.pastSelectedEntity, Color.WHITE);
        }
        setEntityBackgroundColor(textPane, ent, TypeColor.SelectedColor);
        GlobalCache.pastSelectedEntity = ent;
    }


    private static void setEntityBackgroundColor(JTextPane textPane, Entity ent, Color color) {
        int p0 = ent.getStartPos();
        int p1 = ent.getEndPos();
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setBackground(attr, color);
        StyleConstants.setFontSize(attr, chooseSize);
        ((DefaultStyledDocument) textPane.getDocument()).setCharacterAttributes(p0, p1 - p0, attr, false);
    }


    private static JTable createEntityTable(final JTextPane textPane, final boolean isForEntity) {
        Object columnNames[] = {"行号", "实体", "类型", "修饰", "不确定", "生理病理"};//表格的4列意义
//		final Object rowData[][] = new Object[maxNum][2];//建立表格中的元素数组
        final JTable table = new JTable(null, columnNames);//建立表格
        DefaultTableModel model = new DefaultTableModel() {
            public boolean isCellEditable(int row, int column) {
                if (!isForEntity) {
                    return false;
                }
                if (getColumnName(column).equals("类型") &&
                        (getValueAt(row, column - 1) != null && getValueAt(row, column - 1).toString().length() > 0)) {

                    return true;
                }
                if (getColumnName(column).equals("修饰")) {
                    TypeColor tc = ((TypeColor) getValueAt(row, column - 1));
                    if (tc != null && (tc.getFlag() == 1)) {
                        return true;
                    }
                }
                if (getColumnName(column).equals("不确定")) {
                    return true;
                }
                if (getColumnName(column).equals("生理病理")) {
                    return true;
                }
                return false;//表格不允许被编辑
            }

            public void setValueAt(Object aValue, int row, int column) {
                super.setValueAt(aValue, row, column);
                if (column == table.getColumnModel().getColumnIndex("类型")) {
                    TypeColor tc = (TypeColor) aValue;
                    String entityvalue = (String) table.getValueAt(row, table.getColumnModel().getColumnIndex("实体"));
                    Entity ent = Entity.createByAnnotationStr(entityvalue);
                    setEntityForeground(textPane, ent, tc);

                    if (tc.getFlag() == 0) {
                        table.setValueAt(null, row, table.getColumnModel().getColumnIndex("修饰"));
                    }
                    if (tc.getFlag() == 1) {
                        DefaultCellEditor editor = (DefaultCellEditor) table.getCellEditor(row, table.getColumnModel().getColumnIndex("修饰"));
                        AssertTypeComboxModel model = (AssertTypeComboxModel) ((JComboBox) editor.getComponent()).getModel();
                        if (tc.getTypeId().equals("treatment")) {
                            model.setCondition("treatment");
                        } else {
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

        final JComboBox combo = new JComboBox();//建立实体分类下拉菜单
        for (TypeColor tc : TypeColorMap.getEntityTypeArray()) {
            combo.addItem(tc);
        }
        combo.setRenderer(new ComboxRender(true));
        combo.setEditable(false);


        DefaultCellEditor typeeditor = new DefaultCellEditor(combo);
        table.getColumn("类型").setCellEditor(typeeditor);//将第3列设为附类下拉选项
        table.getColumn("类型").setCellRenderer(new TypeCellRender(true));
        AssertTypeComboxModel atcm = new AssertTypeComboxModel();
        JComboBox combo2 = new JComboBox(atcm);//建立修饰分类下拉菜单
        AssertTypeMouseListener atml = new AssertTypeMouseListener(table, combo2);
        combo2.getComponent(0).addMouseListener(atml);
//	    combo2.addFocusListener(atfl);
//		for(TypeColor tc : TypeColorMap.getAssertTypeArray()){
//			combo2.addItem(tc);
//		}
        combo2.setEditable(false);
        DefaultCellEditor asserteditor = new DefaultCellEditor(combo2);
        table.getColumn("修饰").setCellEditor(asserteditor);//将第3列设为附类下拉选项
        table.getColumn("修饰").setCellRenderer(new AsserttypeRender());

        table.getColumn("行号").setPreferredWidth(1);

        table.getColumn("不确定").setCellEditor(new DefaultCellEditor(new JCheckBox()));
        addTableMouseListener(textPane, table);
        table.getColumn("不确定").setCellRenderer(new QuestionalRenderer());

        // 生理病理
        // 建立尸体下拉菜单
        final JComboBox<Object> shengliBingli = new JComboBox<>();
        for (TypeColor tc : TypeColorMap.getShengliBingliTypeArray()) {
            shengliBingli.addItem(tc);
        }
        shengliBingli.setRenderer(new ComboxRender(true));
        shengliBingli.setEditable(false);
        DefaultCellEditor shenglibingliSelect = new DefaultCellEditor(shengliBingli);
        table.getColumn("生理病理").setCellEditor(shenglibingliSelect);
        table.getColumn("生理病理").setCellRenderer(new TypeCellRender(true));
        return table;
    }


    public static class QuestionalRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value, boolean isSelected, boolean hasFocus, int row,
                                                       int column) {
            // TODO Auto-generated method stub
            Boolean bjvalue = (Boolean) table.getValueAt(row, table.getColumnModel().getColumnIndex("不确定"));
            JCheckBox jcb = new JCheckBox();
            if (bjvalue != null && bjvalue) {
                jcb.setSelected(true);
                jcb.setBackground(Color.YELLOW);
            } else {
                jcb.setSelected(false);
            }
            return jcb;
        }
    }

    private static JPanel createEntityButtonPanel(JTextPane textPane, JTable table) {
        JPanel btnpanel = new JPanel();

        JTextField inputFile = new JTextField(40);
        inputFile.setEditable(false);

        JButton buttonOpen = new JButton("打开文件");
        JButton buttonInNE = new JButton("导入NE");
        JButton buttonNE = new JButton("添加实体 A");
        JButton buttonNO = new JButton("删除实体 D");
        JButton buttonSave = new JButton("导出结果");
        JPopupMenu popmenu = new JPopupMenu();

        btnpanel.add(buttonOpen);
        btnpanel.add(buttonInNE);
        btnpanel.add(buttonNE);
        btnpanel.add(buttonNO);
        btnpanel.add(inputFile);
        btnpanel.add(buttonSave);

        GlobalComponent.addNEButton = new JButton("添加实体 A");
        GlobalComponent.delNEButton = new JButton("删除实体 D");


        addOpenFileButtonListener(buttonOpen, textPane, inputFile, table, null);
        addAddNEButtonListener(buttonNE, textPane, table);
        addExportNEButtonToTab1(buttonSave, table, inputFile);
        addImportNEButtonListener(buttonInNE, textPane, table);
        addDeleteNEButtonListener(buttonNO, textPane, table);
//	    addPopupMenuListener();

        copyListener(buttonNE, GlobalComponent.addNEButton);
        copyListener(buttonNO, GlobalComponent.delNEButton);

        GlobalComponent.entiyPopupmenu = popmenu;


        return btnpanel;
    }


    private static void copyListener(JButton from, JButton to) {
        ActionListener[] als = from.getActionListeners();
        for (ActionListener al : als) {
            to.addActionListener(al);
        }
    }


    private static void addEntityAnnotationTab(JTabbedPane tabbedPane, String text)//页面一：实体标注
    {
        JPanel entityPanel = new JPanel();//新建一个版面
        entityPanel.setLayout(new BorderLayout());//用BorderLayout对版面进行布局

        final JTextPane entityTextPane = new JTextPane();//新建一个文本编辑框，用来显示文本及进行操作
        entityTextPane.setEditable(false);//该文本是不能由用户在框内编辑的

        final JTable table = createEntityTable(entityTextPane, true);
        addTextPaneListener(entityTextPane, table);

        JPanel btnpanel = createEntityButtonPanel(entityTextPane, table);
        entityPanel.add(btnpanel, BorderLayout.NORTH);


        JSplitPane jSplitPane0 = new JSplitPane();
        jSplitPane0.setSize(1024, 768);
        jSplitPane0.setOrientation(JSplitPane.VERTICAL_SPLIT);
        jSplitPane0.setDividerLocation(0.5);
        entityPanel.add(jSplitPane0, BorderLayout.CENTER);
        jSplitPane0.setTopComponent(new JScrollPane(entityTextPane));


        jSplitPane0.setBottomComponent(new JScrollPane(table));

        tabbedPane.addTab(text, entityPanel);
    }


    private static void addTextPaneListener(final JTextPane entityTextPane, final JTable table) {
        MouseListener listenser = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    boolean existed = false;
                    int pos = entityTextPane.viewToModel(e.getPoint());
                    int rows = table.getRowCount();
                    for (int i = 0; i < rows; i++) {
                        String entityvalue = (String) table.getValueAt(i, table.getColumnModel().getColumnIndex("实体"));
                        Entity ent = Entity.createByAnnotationStr(entityvalue);
                        if (ent.getStartPos() <= pos && ent.getEndPos() >= pos) {
                            existed = true;
                            table.setRowSelectionInterval(i, i);
                            setEntitySelected(entityTextPane, table);
                            Rectangle rect = table.getCellRect(i, 0, true);
                            table.scrollRectToVisible(rect);
                            break;
                        }
                    }
                    String selectedText = entityTextPane.getSelectedText();
                    if (existed || (!existed && selectedText != null && selectedText.length() > 0)) {
                        showMenu(existed).show(entityTextPane, (int) e.getPoint().getX(), (int) e.getPoint().getY());
                    }
                }
            }

        };

        entityTextPane.addMouseListener(listenser);
    }


    private static void hidePopupMenu() {
        if (GlobalComponent.entiyPopupmenu.isVisible()) {
            GlobalComponent.entiyPopupmenu.setVisible(false);
        }
    }

    private static JPopupMenu showMenu(boolean existed) {
        GlobalComponent.entiyPopupmenu.removeAll();
        if (existed) {
            GlobalComponent.entiyPopupmenu.add(GlobalComponent.delNEButton);
        } else {
            GlobalComponent.entiyPopupmenu.add(GlobalComponent.addNEButton);
        }
        return GlobalComponent.entiyPopupmenu;
    }


    private static JTable createRelationTable(final JTextPane textPane) {

        Object columnNames[] = {"实体1", "实体2", "关系类型"};//表格的4列意义
//		final Object rowData[][] = new Object[maxNum][2];//建立表格中的元素数组
        final JTable table = new JTable(null, columnNames);//建立表格
        DefaultTableModel model = new DefaultTableModel() {
            public boolean isCellEditable(int row, int column) {
                if (getColumnName(column).equals("关系类型")) {
                    return true;
                }

                return false;//表格不允许被编辑
            }

            public void setValueAt(Object aValue, int row, int column) {
                super.setValueAt(aValue, row, column);
                if (column == table.getColumnModel().getColumnIndex("关系类型")) {
                    String entity1value = (String) table.getValueAt(row, table.getColumnModel().getColumnIndex("实体1"));
                    Entity ent1 = Entity.createByAnnotationStr(entity1value);
                    String entity2value = (String) table.getValueAt(row, table.getColumnModel().getColumnIndex("实体2"));
                    Entity ent2 = Entity.createByAnnotationStr(entity2value);
                    TypeColor tc = (TypeColor) aValue;
                    if (tc == null) {
                        setEntityPairBackGround(textPane, ent1, ent2, Color.WHITE);
                        return;
                    }

                    if (tc.getFlag() == 0) {
                        JOptionPane.showMessageDialog(null, "请选择具体的关系类型", "提示",
                                JOptionPane.INFORMATION_MESSAGE);
                        setValueAt(null, row, column);
                        setEntityPairBackGround(textPane, ent1, ent2, Color.WHITE);
                    } else {
                        setEntityPairBackGround(textPane, ent1, ent2, tc.getColor());
                    }


                }
            }
        };

        table.setSurrendersFocusOnKeystroke(true);
        model.setColumnIdentifiers(columnNames);
        table.setModel(model);
        table.getColumn("关系类型").setCellRenderer(new TypeCellRender(false));

        table.setRowHeight(25);


        final JComboBox combo = new JComboBox();//建立实体分类下拉菜单
        TypeColor[] tcs = TypeColorMap.getRelationTypeArray();
        combo.setMaximumRowCount(tcs.length);
        combo.setRenderer(new ComboxRender(false));
        for (TypeColor tc : tcs) {
            combo.addItem(tc);
        }
        combo.setEditable(false);


        DefaultCellEditor typeeditor = new DefaultCellEditor(combo);
        table.getColumn("关系类型").setCellEditor(typeeditor);//将第3列设为附类下拉选项


        addRelationTableMouseListener(textPane, table);


        return table;
    }


    private static void setEntityPairBackGround(JTextPane textPane, Entity ent1, Entity ent2, Color color) {
        if (GlobalCache.pastSelectedEntityPair != null) {
            setEntityBackgroundColor(textPane, GlobalCache.pastSelectedEntityPair[0], Color.WHITE);
            setEntityBackgroundColor(textPane, GlobalCache.pastSelectedEntityPair[1], Color.WHITE);
        }
        setEntityBackgroundColor(textPane, ent1, color);
        setEntityBackgroundColor(textPane, ent2, color);

        GlobalCache.pastSelectedEntityPair = new Entity[]{ent1, ent2};
    }


    private static void addRelationTableMouseListener(final JTextPane textPane, final JTable table) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                int row = table.getSelectedRow();
                String entity1value = (String) table.getValueAt(row, table.getColumnModel().getColumnIndex("实体1"));
                Entity ent1 = Entity.createByAnnotationStr(entity1value);
                String entity2value = (String) table.getValueAt(row, table.getColumnModel().getColumnIndex("实体2"));
                Entity ent2 = Entity.createByAnnotationStr(entity2value);
                //置背景色
                TypeColor tc = (TypeColor) table.getValueAt(row, table.getColumnModel().getColumnIndex("关系类型"));
                if (tc != null) {
                    setEntityPairBackGround(textPane, ent1, ent2, tc.getColor());
                } else {
                    setEntityPairBackGround(textPane, ent1, ent2, Color.WHITE);
                }

            }
        });
    }


    private static void addEntityBtn1Listener(JButton entityBtn1, final JTextField entityTxt1, final JTable entityTable) {
        entityBtn1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = entityTable.getSelectedRow();
                if (row >= 0) {
                    final String entityName = (String) entityTable.getValueAt(row, entityTable.getColumnModel().getColumnIndex("实体"));
                    final String entityType = ((TypeColor) entityTable.getValueAt(row, entityTable.getColumnModel().getColumnIndex("类型"))).getTypeId();
                    entityTxt1.setText(entityName + "@" + entityType);
                } else {
                    JOptionPane.showMessageDialog(null, "请先选择实体表中的一行", "提示",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

    private static void addAddrealtionBtnListener(JButton addRelationBtn, final JTextField entityTxt1, final JTextField entityTxt2, final JTable relationTable) {
        addRelationBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (entityTxt1.getText().length() == 0 || entityTxt2.getText().length() == 0
                        || entityTxt1.getText().equals(entityTxt2.getText())) {
                    JOptionPane.showMessageDialog(null, "实体1和实体2都不能为空并且不能相同", "提示",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    Object[] rowData = new Object[]{entityTxt1.getText(), entityTxt2.getText(), null};
                    ((DefaultTableModel) relationTable.getModel()).addRow(rowData);
                    entityTxt1.setText("");
                    entityTxt2.setText("");
                }
            }
        });
    }


    private static void addDeleteRelBtnListener(JButton buttonNORel, final JTextPane entityTextPane, final JTable relationTable) {
        buttonNORel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = relationTable.getSelectedRow();
                String entity1value = (String) relationTable.getValueAt(row, relationTable.getColumnModel().getColumnIndex("实体1"));
                Entity ent1 = Entity.createByAnnotationStr(entity1value);
                String entity2value = (String) relationTable.getValueAt(row, relationTable.getColumnModel().getColumnIndex("实体2"));
                Entity ent2 = Entity.createByAnnotationStr(entity2value);
                setEntityPairBackGround(entityTextPane, ent1, ent2, Color.WHITE);
                ((DefaultTableModel) relationTable.getModel()).removeRow(row);
            }
        });
    }

    private static void addImportRelBtnListener(JButton buttonInRel, final JTable relationTable) {
        buttonInRel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser j = new JFileChooser(GlobalCache.currentPath);//文件选择器
                j.setFileFilter(new EmrFileFiller(".rel"));
                if (j.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    try {
                        File f = j.getSelectedFile();
                        FileInputStream in = new FileInputStream(f);
                        GlobalCache.currentPath = f.getAbsolutePath();
                        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                        String line = null;
                        DefaultTableModel model = (DefaultTableModel) relationTable.getModel();

                        clearTable(relationTable);


                        while ((line = br.readLine()) != null) {
                            if (line.length() > 0) {
                                Relation rel = Relation.createBySaveStr(line);
                                Object[] rowData = new Object[]{rel.getEnt1().toAnnotation(), rel.getEnt2().toAnnotation(), TypeColorMap.getType(rel.getRelationType())};
                                model.addRow(rowData);
                            }
                        }
                        br.close();


                    } catch (Exception ex) {

                    }
                }
            }
        });
    }

    private static void addSaveRelBtnLinstener(JButton buttonSave, final JTable relationTable, final JTextField inputFile) {
        buttonSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String path;
                JFileChooser file = new JFileChooser(GlobalCache.currentPath);
                file.setAcceptAllFileFilterUsed(false);
                file.setSelectedFile(new File(inputFile.getText() + ".rel"));

                if (relationTable.getRowCount() == 0) {
                    return;
                }
                if (file.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    path = file.getSelectedFile().getAbsolutePath();
                    try {
                        GlobalCache.currentPath = path;
                        PrintWriter out = new PrintWriter(path, "UTF-8");
                        DefaultTableModel model = (DefaultTableModel) relationTable.getModel();
                        Vector rowdatas = model.getDataVector();
                        for (Object obj : rowdatas) {
                            Vector rowdata = (Vector) obj;
                            Entity ent1 = Entity.createByAnnotationStr(((String) rowdata.get(0)).split("@"));
                            Entity ent2 = Entity.createByAnnotationStr(((String) rowdata.get(1)).split("@"));
                            TypeColor relationtype = (TypeColor) rowdata.get(2);
                            if (relationtype != null) {
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

                    JOptionPane.showMessageDialog(null, "保存成功  路径：" + path, "提示",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "已取消  未保存", "提示",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

    private static JPanel createRelationButtonPanel(final JTextPane entityTextPane, final JTable entityTable, final JTable relationTable) {
        JPanel btnpanel = new JPanel(new GridLayout(0, 1));

        JPanel btnpanel1 = new JPanel();

        JTextField inputFile = new JTextField(40);
        inputFile.setEditable(false);

        JButton buttonOpen = new JButton("打开文件");
        JButton buttonInNE = new JButton("导入NE");
        JButton buttonInRel = new JButton("导入关系");
        btnpanel1.add(buttonOpen);
        btnpanel1.add(inputFile);
        btnpanel1.add(buttonInNE);
        btnpanel1.add(buttonInRel);

        addOpenFileButtonListener(buttonOpen, entityTextPane, inputFile, entityTable, relationTable);
        addImportNEButtonListener(buttonInNE, entityTextPane, entityTable);
        addImportRelBtnListener(buttonInRel, relationTable);


        JPanel entityBtnPanel = new JPanel();
        entityBtnPanel.setLayout(new GridLayout(1, 0));

        JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout());
        JButton entityBtn1 = new JButton("实体1");
        JTextField entityTxt1 = new JTextField();
        panel1.add(entityBtn1, BorderLayout.WEST);
        panel1.add(entityTxt1, BorderLayout.CENTER);
        addEntityBtn1Listener(entityBtn1, entityTxt1, entityTable);

        JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout());
        JButton entityBtn2 = new JButton("实体2");
        JTextField entityTxt2 = new JTextField();
        panel2.add(entityBtn2, BorderLayout.WEST);
        panel2.add(entityTxt2, BorderLayout.CENTER);
        addEntityBtn1Listener(entityBtn2, entityTxt2, entityTable);

        JButton addRelationBtn = new JButton("添加实体关系");
        JButton buttonNORel = new JButton("删除实体关系");
        JButton buttonSave = new JButton("导出结果");

        addAddrealtionBtnListener(addRelationBtn, entityTxt1, entityTxt2, relationTable);
        addDeleteRelBtnListener(buttonNORel, entityTextPane, relationTable);
        addSaveRelBtnLinstener(buttonSave, relationTable, inputFile);

        entityBtnPanel.add(panel1);
        entityBtnPanel.add(panel2);
        entityBtnPanel.add(addRelationBtn);
        entityBtnPanel.add(buttonNORel);
        entityBtnPanel.add(buttonSave);


        btnpanel.add(btnpanel1);
        btnpanel.add(entityBtnPanel);

        return btnpanel;


    }

    static void addRelationAnnotaionTab(JTabbedPane tabbedPane, String text)//关系标注
    {
        JPanel relationPanel = new JPanel();
        relationPanel.setLayout(new BorderLayout());//用BorderLayout对版面进行布局

        final JTextPane entityTextPane = new JTextPane();
        entityTextPane.setEditable(false);


        JSplitPane entitySplitPane = new JSplitPane();
        entitySplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        entitySplitPane.setSize(1024, 384);
        entitySplitPane.setDividerLocation(0.3);
        final JTable entityTable = createEntityTable(entityTextPane, false);

        entitySplitPane.setLeftComponent(new JScrollPane(entityTable));
        entitySplitPane.setRightComponent(new JScrollPane(entityTextPane));


        JSplitPane mainSplitPane = new JSplitPane();
        mainSplitPane.setSize(1024, 768);
        mainSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setDividerLocation(0.6);

        mainSplitPane.setTopComponent(entitySplitPane);

        final JTable relationTable = createRelationTable(entityTextPane);

        JPanel btnpanel = createRelationButtonPanel(entityTextPane, entityTable, relationTable);
        relationPanel.add(btnpanel, BorderLayout.NORTH);

        mainSplitPane.setBottomComponent(new JScrollPane(relationTable));
        relationPanel.add(mainSplitPane, BorderLayout.CENTER);

        tabbedPane.addTab(text, relationPanel);

    }


    public static void main(String args[])                                            //主函数
    {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame f = new JFrame("WI实验室电子病历实体和实体关系标注工具1.0");
//		f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container content = f.getContentPane();
        JTabbedPane tabbedPane = new JTabbedPane();
        addEntityAnnotationTab(tabbedPane, "实体标注");
        addRelationAnnotaionTab(tabbedPane, "实体关系标注");
        content.add(tabbedPane, BorderLayout.CENTER);
        f.setSize(1024, 768);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}

