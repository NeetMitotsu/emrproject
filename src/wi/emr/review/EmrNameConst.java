package wi.emr.review;

import java.util.ArrayList;

public class EmrNameConst {
	
	public static String discharge_CH_Name = "��ԺС��";
	public static String progress_CH_Name = "���̼�¼";
	public static String discharge_EN_Name = "discharge";
	public static String progress_EN_Name = "progress";
	
	//���̼�¼����
	public static String zhusu = "����";
	public static String binglitedian = "�����ص�";
	public static String linchuangchubuzhenduan = "�ٴ��������";
	public static String zhenduanyiju = "�������";
	public static String jianbiezhenduan = "�������";
	public static String zhenliaojihua = "���Ƽƻ�";
	
	
	//��ԺС�Ჿ��
	
	public static String huanzhexinxi = "������Ϣ";
	public static String zhuyuanqizhiri = "סԺ��ֹ��";
	public static String menzhenshouzhizhenduan = "�����������";
//	public static String linchuangchubuzhenduan = "�ٴ��������";
	public static String linchuangquedingzhenduan = "�ٴ�ȷ�����";
	public static String ruyuanshiqingkuang = "��Ժʱ���";
	public static String zhiliaojingguo = "���ƾ���";
	public static String chuyuanshiqingkuang = "��Ժʱ���";
	public static String zhiliaoxiaoguo = "����Ч��";
	public static String chuyuanyizhu = "��Ժҽ��";
	
	public static String[] progressSectionNames = new String[]{
//		zhusu,
		binglitedian,
		linchuangchubuzhenduan,
		zhenduanyiju,
		jianbiezhenduan,
		zhenliaojihua		
	};
	
	public static String[] dischargeSectionNames = new String[]{
//		huanzhexinxi,
//		zhuyuanqizhiri,
		menzhenshouzhizhenduan,
		linchuangchubuzhenduan,
		linchuangquedingzhenduan,
		ruyuanshiqingkuang,
		zhiliaojingguo,
		chuyuanshiqingkuang,
		zhiliaoxiaoguo,
		chuyuanyizhu
	};
	
	public  static String[] validateSections(String[] sectionNames,ArrayList<String> existNames){
		ArrayList<String> absence = new ArrayList<String>();
		for(String name : sectionNames){
			if(!existNames.contains(name)){
				absence.add(name);
			}
		}
		return absence.toArray(new String[absence.size()]);
	}
	
	
	
	public static String[] sectionNames = new String[]{binglitedian,
														linchuangchubuzhenduan,
														zhenduanyiju,
														jianbiezhenduan,
														zhenliaojihua,
														huanzhexinxi,
														zhuyuanqizhiri,
														menzhenshouzhizhenduan,
														linchuangquedingzhenduan,
														ruyuanshiqingkuang,
														zhiliaojingguo,
														chuyuanshiqingkuang,
														zhiliaoxiaoguo,
														chuyuanyizhu,
														};
}
