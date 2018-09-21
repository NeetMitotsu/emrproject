package wi.annotator;

import javax.swing.*;
import java.util.HashMap;
import java.util.Vector;

/**
 * @Author 李新栋 [lxd3808@163.com]
 * @Date 2018/9/21 11:06
 */
public class TypeComboxModel extends DefaultComboBoxModel {

    private HashMap<String, Vector<String>> content = new HashMap<String, Vector<String>>();

    private String condition = "all";//treatment

    public TypeComboxModel() {
        super();
        setCondition(condition);
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
        removeAllElements();

        TypeColor[] typeColors = null;
        switch (condition) {
            case "all":
                typeColors = TypeColorMap.getEntityTypeArray();
                break;
            case "zhenduan":
                typeColors = TypeColorMap.getEntityTypeArrayZhenDuan();
                break;
            case "zhengzhuang":
                typeColors = TypeColorMap.getEntityTypeArrayZhengZhuang();
                break;
            case "zhengzhuangxiangguan":
                typeColors = TypeColorMap.getEntityTypeArrayZhengZhuangXiangGuan();
                break;
            case "jiancha":
                typeColors = TypeColorMap.getEntityTypeArrayJianCha();
                break;
            case "zhiliao":
                typeColors = TypeColorMap.getEntityTypeArrayZhiLiao();
                break;
            case "shijian":
                typeColors = TypeColorMap.getEntityTypeArrayShiJian();
                break;
            default:
                typeColors = TypeColorMap.getEntityTypeArray();
                break;
        }
        for (TypeColor tc : typeColors) {
            addElement(tc);
        }
    }
}
