package wi.emr.review;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

//		String str = "����: ����ϼ �Ա�: Ů ����: 62�� ��Ժ�Ʊ�: ���ڿ��Ĳ���";
//		String regex = "����:[  ]?[\u4E00-\u9FA5]*[  ]";
		String str = "���ٽ�, ����,47��, �� \"����ͷ��2�����. ����һ��\", ��2012��02��27��������������ҽ�Ʋ���";
		String regex = "^[\u4E00-\u9FA5]*,";
		
//		String regex2 = "^"
		System.out.println(str.replaceAll(regex, ""));
		
		
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		while(m.find()){
			System.out.println(m.group());
			System.out.println(m.replaceFirst(""));
		}
	}

}
