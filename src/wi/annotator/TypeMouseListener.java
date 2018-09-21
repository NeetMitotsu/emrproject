package wi.annotator;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @Author 李新栋 [lxd3808@163.com]
 * @Date 2018/9/21 10:35
 */
public class TypeMouseListener extends MouseAdapter {

    private JTable table;
    private JComboBox combox;


    public TypeMouseListener(JTable table,JComboBox combox) {
        this.table = table;
        this.combox = combox;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        int row = table.getSelectedRow();
        int fenleiCoulumn = table.getColumnModel().getColumnIndex("分类");
        TypeColor tc = (TypeColor)table.getValueAt(row, fenleiCoulumn);
        if(tc.getFlag() == 1){
            final DefaultComboBoxModel model = (DefaultComboBoxModel) combox.getModel();
            model.removeAllElements();
            if (tc.getTypeId().equals("zhengzhuang")){
                for(TypeColor tcTemp:TypeColorMap.getEntityTypeArrayZhengZhuang()){
                    model.addElement(tcTemp);
                }
            }
            combox.setModel(model);

//            AssertTypeComboxModel model = (AssertTypeComboxModel)combox.getModel();
//            if(tc.getTypeId().equals("treatment")){
//                model.setCondition("treatment");
//            }else{
//                model.setCondition("problem");
//            }
//            combox.setModel(model);
        }
    }
}
