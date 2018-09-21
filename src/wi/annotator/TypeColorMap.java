package wi.annotator;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class TypeColorMap {
	private  static LinkedHashMap<String, TypeColor> entityMap = new LinkedHashMap<String, TypeColor>();
	private static LinkedHashMap<String, TypeColor> assertMap = new LinkedHashMap<String, TypeColor>();
	private static LinkedHashMap<String, TypeColor> relationMap = new LinkedHashMap<String, TypeColor>();
	private static LinkedHashMap<String, TypeColor> tassertMap = new LinkedHashMap<String, TypeColor>();
	private static LinkedHashMap<String, TypeColor> shengliBingliMap = new LinkedHashMap<>();
	private static LinkedHashMap<String, TypeColor> fenLeiMap = new LinkedHashMap<>();
	static{
		
		try {
			fillMapFromCfg(entityMap,"config/entitytype.properties");
			fillMapFromCfg(assertMap,"config/asserttype.properties");
			fillMapFromCfg(relationMap,"config/relationtype.properties");
			fillMapFromCfg(tassertMap,"config/tasserttype.properties");
			fillMapFromCfg(shengliBingliMap, "config/shengliBingli.properties");
			fillMapFromCfg(fenLeiMap, "config/fenlei.properties");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private static void fillMapFromCfg(HashMap<String, TypeColor> map,String cfgfile)throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(cfgfile),"UTF-8"));
		String line = null;
		while((line = br.readLine())!=null){
			if(line.startsWith("#")){
				continue;
			}
			String[] kv = line.split("=");
			
			String typeid = kv[0];
			String[] values = kv[1].split(",");
			TypeColor tc = new TypeColor();
			tc.setTypeId(typeid);
			tc.setTypeName(values[0]);
			tc.setFlag(Integer.valueOf(values[2]));			
			String colorStr = values[1];
			Color color = new Color(Integer.parseInt(colorStr.substring(0, 2), 16),
						Integer.parseInt(colorStr.substring(2, 4), 16),
						Integer.parseInt(colorStr.substring(4, 6), 16));
			tc.setColor(color);
			map.put(typeid, tc);
		}
	}
	
	public static TypeColor getType(String typeId){
		if(entityMap.containsKey(typeId)){
			return entityMap.get(typeId);
		}else if(assertMap.containsKey(typeId)){
			return assertMap.get(typeId);
		}else if(relationMap.containsKey(typeId)){
			return relationMap.get(typeId);
		}else if(tassertMap.containsKey(typeId)){
			return tassertMap.get(typeId);
		}else if(shengliBingliMap.containsKey(typeId)){
			return shengliBingliMap.get(typeId);
		}
		return null;
	} 
	
	public static TypeColor[] getEntityTypeArray(){
		return entityMap.values().toArray(new TypeColor[0]);
	}
	public static TypeColor[] getShengliBingliTypeArray(){
		return shengliBingliMap.values().toArray(new TypeColor[0]);
	}
	
	public static TypeColor[] getAssertTypeArray(){
		return assertMap.values().toArray(new TypeColor[0]);
	}
	public static TypeColor[] getTAssertTypeArray(){
		return tassertMap.values().toArray(new TypeColor[0]);
	}
	
	public static TypeColor[] getRelationTypeArray(){
		return relationMap.values().toArray(new TypeColor[0]);
	}
	
	public static void main(String[] args) {
		System.out.println(TypeColorMap.getType("DIS"));
	}

	public static TypeColor[] getFenLeiTypeArray() {
		return fenLeiMap.values().toArray(new TypeColor[0]);
	}

	public static TypeColor[] getEntityTypeArrayZhengZhuang() {
		List<TypeColor> list = new ArrayList<>();
		Set<String> keySet = new HashSet<>();
		keySet.add("complaintSymptom");
		keySet.add("singleSymptom");
		keySet.add("body");
		keySet.add("main");
		keySet.add("feel");
		keySet.add("testresult");
		for(Map.Entry<String, TypeColor> entry:entityMap.entrySet()){
			if (keySet.contains(entry.getKey())){
				list.add(entry.getValue());
			}
		}
		return list.toArray(new TypeColor[0]);
	}

	public static TypeColor[] getEntityTypeArrayZhenDuan() {
		List<TypeColor> list = new ArrayList<>();
		Set<String> keySet = new HashSet<>();
		keySet.add("disease");
		keySet.add("diseasetype");
		for(Map.Entry<String, TypeColor> entry:entityMap.entrySet()){
			if (keySet.contains(entry.getKey())){
				list.add(entry.getValue());
			}
		}
		return list.toArray(new TypeColor[0]);
	}

	public static TypeColor[] getEntityTypeArrayZhengZhuangXiangGuan() {
		List<TypeColor> list = new ArrayList<>();
		Set<String> keySet = new HashSet<>();
		keySet.add("symptomNature");
		keySet.add("severity");
		keySet.add("frequency");
		keySet.add("incentive");
		keySet.add("range");
		for(Map.Entry<String, TypeColor> entry:entityMap.entrySet()){
			if (keySet.contains(entry.getKey())){
				list.add(entry.getValue());
			}
		}
		return list.toArray(new TypeColor[0]);
	}

	public static TypeColor[] getEntityTypeArrayJianCha() {
		List<TypeColor> list = new ArrayList<>();
		Set<String> keySet = new HashSet<>();
		keySet.add("checkProject");
		keySet.add("checkResult");
		keySet.add("auxiliaryCheckProject");
		keySet.add("auxiliaytCheckResult");
		keySet.add("check");
		for(Map.Entry<String, TypeColor> entry:entityMap.entrySet()){
			if (keySet.contains(entry.getKey())){
				list.add(entry.getValue());
			}
		}
		return list.toArray(new TypeColor[0]);
	}

	public static TypeColor[] getEntityTypeArrayZhiLiao() {
		List<TypeColor> list = new ArrayList<>();
		Set<String> keySet = new HashSet<>();
		keySet.add("medicalTreatment");
		keySet.add("surgicalTreatment");
		keySet.add("physiotherapyTreatment");
		for(Map.Entry<String, TypeColor> entry:entityMap.entrySet()){
			if (keySet.contains(entry.getKey())){
				list.add(entry.getValue());
			}
		}
		return list.toArray(new TypeColor[0]);
	}

	public static TypeColor[] getEntityTypeArrayShiJian() {
		List<TypeColor> list = new ArrayList<>();
		Set<String> keySet = new HashSet<>();
		keySet.add("startTime");
		keySet.add("stayTime");
		keySet.add("age");
		for(Map.Entry<String, TypeColor> entry:entityMap.entrySet()){
			if (keySet.contains(entry.getKey())){
				list.add(entry.getValue());
			}
		}
		return list.toArray(new TypeColor[0]);
	}
}
