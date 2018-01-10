package com.oracle.cloud.biu.modules.biubiu.citic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.oracle.cloud.biu.Biu;
import com.oracle.cloud.biu.api.CommonAPI;
import com.oracle.cloud.biu.modules.biubiu.shell.OracleOrchDatabaseAutoDeploy;

public class ParamsVaildChecker {

	public static ServiceParams requestSingleCheck(int serviceterm, String serviceid, String shape, int volumnsize,
			String sid, String pdbname, String password, String sshpublickey, String sshprivatekey, String charset,
			List<String> targetshapes, int totalcore) {
		ServiceParams sp = new ServiceParams();
		String tmp = "";
		if ((serviceterm > 4) || (serviceterm < 1)) {
			sp.setResult(false);
			sp.setDescription("服务开通只能允许在1-4年");
			return sp;
		}

		if (StringUtils.isBlank(serviceid)) {
			sp.setResult(false);
			sp.setDescription("没有提供服务项");
			return sp;
		}

		tmp = serviceid.trim();
		if ((!tmp.equals("Oracle11.2")) && (!tmp.equals("Oracle12.1")) && (!tmp.equals("Oracle12.2"))) {
			sp.setResult(false);
			sp.setDescription("提供的数据库版本不合法");
			return sp;
		}

		if (StringUtils.isBlank(shape)) {
			sp.setResult(false);
			sp.setDescription("没有提供配置机型");
			return sp;
		}

		tmp = shape.trim();
		if ((!tmp.toUpperCase().equals("OC3")) && (!tmp.toUpperCase().equals("OC4"))
				&& (!tmp.toUpperCase().equals("OC5")) && (!tmp.toUpperCase().equals("OC6"))
				&& (!tmp.toUpperCase().equals("OC1M")) && (!tmp.toUpperCase().equals("OC2M"))
				&& (!tmp.toUpperCase().equals("OC3M")) && (!tmp.toUpperCase().equals("OC4M"))) {
			sp.setResult(false);
			sp.setDescription("提供的机型不合法");
			return sp;
		}

		if ((volumnsize < 200) || (volumnsize > 2000)) {
			sp.setResult(false);
			sp.setDescription("存储配置为(200G-2000G),且递增为100的倍数");
			return sp;
		}

		if (volumnsize % 100 != 0) {
			sp.setResult(false);
			sp.setDescription("存储配置每次递增为100的倍数");
			return sp;
		}

		if (StringUtils.isBlank(sid)) {
			sp.setResult(false);
			sp.setDescription("没有提供数据库SID");
			return sp;
		}

		tmp = sid.trim().toUpperCase();
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(tmp.charAt(0) + "");
		if (isNum.matches()) {
			sp.setResult(false);
			sp.setDescription("数据库SID不可以使用数字开头");
			return sp;
		}

		if (isSpecialChar(tmp)) {
			sp.setResult(false);
			sp.setDescription("数据库SID不可以包含特殊字符");
			return sp;
		}

		tmp = serviceid.trim();
		if ((tmp.equals("Oracle12.1")) || (!tmp.equals("Oracle12.2"))) {
			if (StringUtils.isBlank(pdbname)) {
				sp.setResult(false);
				sp.setDescription("没有提供PDB数据库名");
				return sp;
			}
		}

		if ((tmp.equals("Oracle12.1")) || (!tmp.equals("Oracle12.2"))) {
			if (StringUtils.isBlank(pdbname)) {
				sp.setResult(false);
				sp.setDescription("没有提供PDB数据库名");
				return sp;
			}
		}

		if (StringUtils.isBlank(password)) {
			sp.setResult(false);
			sp.setDescription("没有提供数据库密码");
			return sp;
		}

		tmp = sid.trim();
		if (passwordRegex(tmp)) {
			sp.setResult(false);
			sp.setDescription("数据库密码必须为8-30位之间，包含大小写及特殊字符");
			return sp;
		}

		if (StringUtils.isBlank(sshpublickey)) {
			sp.setResult(false);
			sp.setDescription("没有提供OpenSSH公钥内容");
			return sp;
		}

		if (!SSHkeyChecker.checkPublicKey(sshpublickey)) {
			sp.setResult(false);
			sp.setDescription("OpenSSH公钥不合法");
			return sp;
		}

		if (StringUtils.isBlank(sshprivatekey)) {
			sp.setResult(false);
			sp.setDescription("没有提供OpenSSH私钥内容");
			return sp;
		}
		
		if (!SSHkeyChecker.checkPrivateKey(sshprivatekey)) {
			sp.setResult(false);
			sp.setDescription("OpenSSH私钥不合法");
			return sp;
		}		

		if (StringUtils.isBlank(charset)) {
			sp.setResult(false);
			sp.setDescription("没有提供数据库字符集");
			return sp;
		}

		tmp = charset.trim();
		if ((!tmp.equals("utf8")) && (!tmp.equals("gbk"))) {
			sp.setResult(false);
			sp.setDescription("数据库字符集只支持utf8和gbk");
			return sp;
		}
		
		if (totalcore == -1)
			totalcore = 0;
		else if(totalcore == 0)
			totalcore = 40;
		if ((null == targetshapes) || (targetshapes.size() == 0)) {
			targetshapes = new ArrayList<>();
			targetshapes.add("oc1m");
			targetshapes.add("oc2m");
			targetshapes.add("oc3m");
			targetshapes.add("oc4m");
		}
		List<String> shapes = getPriorityShapeList(targetshapes, totalcore);
		boolean inp = false;
		for (String s2 : shapes) {
			if (s2.equals(shape))
				inp = true;
		}
		if (!inp) {
			sp.setResult(false);
			sp.setDescription("不在系统优先供应配置列表中");
			return sp;
		}
		OracleOrchDatabaseAutoDeploy db = new OracleOrchDatabaseAutoDeploy(null, String.valueOf(volumnsize));
		try {
			boolean _re = db.checkingSystemQuota(shape);
			if (!_re) {
				sp.setResult(false);
				sp.setDescription("系统当前资源不足,无法受理新业务申请");
				return sp;
			}
		} catch (Exception e) {
			e.printStackTrace();
			sp.setResult(false);
			sp.setDescription("检测系统可用资源时出错");
			return sp;
		}

		sp.setResult(true);
		sp.setDescription("通过检查");
		return sp;
	}

	public static boolean passwordRegex(String str) {
		String sU = str.toUpperCase();
		String sL = str.toLowerCase();
		int lowCount = 0;
		int upCount = 0;
		int numCount = 0;
		int otherCount = 0;
		for (int i = 0; i < str.length(); i++) {
			char charSTR = str.charAt(i);
			char charSU = sU.charAt(i);
			char charSL = sL.charAt(i);
			char charSN = sL.charAt(i);

			// 如果不是字母，是其他字符，则直接用otherCount来计数
			if (Character.isLetter(charSTR)) {
				// 如果原串与转换过后的大写字母串相等，则原来字符为大写字母,
				// 若与小写字母相等，则为小写字母
				if (charSTR == charSU) {
					upCount++;
				} else if (charSTR == charSL) {
					lowCount++;
				}
			} else if (Character.isDigit(charSN)) {
				numCount++;
			} else {
				if ((charSL == '#') || (charSL == '!') || (charSL == '@') || (charSL == '$')) {
					otherCount++;
				}
			}
		}
        if ((upCount > 0) && (lowCount > 0) && (numCount > 0) && (otherCount > 0))
        	return true;
        return false;
	}

	public static ServiceParams requestRACCheck(int serviceterm, String serviceid, String shape, int volumnsize,
			String sid, String pdbname, String username, String password, String charset) {
		ServiceParams sp = new ServiceParams();
		String tmp = "";
		if ((serviceterm > 4) || (serviceterm < 1)) {
			sp.setResult(false);
			sp.setDescription("服务开通只能允许在1-4年");
			return sp;
		}

		if (StringUtils.isBlank(serviceid)) {
			sp.setResult(false);
			sp.setDescription("没有提供服务项");
			return sp;
		}

		if (StringUtils.isBlank(shape)) {
			sp.setResult(false);
			sp.setDescription("没有提供配置信息");
			return sp;
		}

		tmp = serviceid.trim();
		if ((!tmp.equals("Oracle11.2")) && (!tmp.equals("Oracle12.1")) && (!tmp.equals("Oracle12.2"))) {
			sp.setResult(false);
			sp.setDescription("提供的数据库版本不合法");
			return sp;
		}

		if (StringUtils.isBlank(shape)) {
			sp.setResult(false);
			sp.setDescription("没有提供配置机型");
			return sp;
		}

		tmp = shape.trim();
		if ((!tmp.toLowerCase().equals("ecc1")) && (!tmp.toLowerCase().equals("ecc2"))
				&& (!tmp.toLowerCase().equals("ecc3"))) {
			sp.setResult(false);
			sp.setDescription("提供的机型不合法");
			return sp;
		}

		if ((volumnsize < 200) || (volumnsize > 2000)) {
			sp.setResult(false);
			sp.setDescription("存储配置为(200G-2000G),且递增为100的倍数");
			return sp;
		}

		if (volumnsize % 100 != 0) {
			sp.setResult(false);
			sp.setDescription("存储配置每次递增为100的倍数");
			return sp;
		}

		if (StringUtils.isBlank(sid)) {
			sp.setResult(false);
			sp.setDescription("没有提供数据库SID");
			return sp;
		}

		tmp = sid.trim().toUpperCase();
		if (Character.isDigit(tmp.charAt(0))) {
			sp.setResult(false);
			sp.setDescription("数据库SID不可以使用数字开头");
			return sp;
		}

		if (isSpecialChar(tmp)) {
			sp.setResult(false);
			sp.setDescription("数据库SID不可以包含特殊字符");
			return sp;
		}

		tmp = serviceid.trim();
		if ((tmp.equals("Oracle12.1")) || (!tmp.equals("Oracle12.2"))) {
			if (StringUtils.isBlank(pdbname)) {
				sp.setResult(false);
				sp.setDescription("没有提供PDB数据库名");
				return sp;
			}
		}

		if ((tmp.equals("Oracle12.1")) || (!tmp.equals("Oracle12.2"))) {
			if (StringUtils.isBlank(username)) {
				sp.setResult(false);
				sp.setDescription("没有提供用户名");
				return sp;
			}
		}

		if (StringUtils.isBlank(password)) {
			sp.setResult(false);
			sp.setDescription("没有提供数据库密码");
			return sp;
		}

		tmp = sid.trim();
		if (passwordRegex(tmp)) {
			sp.setResult(false);
			sp.setDescription("数据库密码必须为8-30位之间，包含大小写及特殊字符");
			return sp;
		}

		if (StringUtils.isBlank(charset)) {
			sp.setResult(false);
			sp.setDescription("没有提供数据库字符集");
			return sp;
		}

		tmp = charset.trim();
		if ((!tmp.equals("utf8")) && (!tmp.equals("gbk"))) {
			sp.setResult(false);
			sp.setDescription("数据库字符集只支持utf8和gbk");
			return sp;
		}

		tmp = serviceid.trim();
//		if (tmp.equals("Oracle11.2")) {
//			// TODO 检查SID是否存在
//			sp.setResult(false);
//			sp.setDescription("实例名存在，请更换");
//			return sp;
//		}
//
//		if ((tmp.equals("Oracle12.1")) || (!tmp.equals("Oracle12.2"))) {
//			// TODO 检查PDB名是否存在
//			sp.setResult(false);
//			sp.setDescription("PDB名已存在，请更换");
//			return sp;
//		}

		sp.setResult(true);
		sp.setDescription("通过检查");
		return sp;
	}

	private static List<String> getPriorityShapeList(List<String> shapes, int totalcore) {
		List<String> s1 = new ArrayList<String>();
		try {
			JSONObject obj1 = CommonAPI.summary("/instance/{container}_/storage/volume/{container}/", "all", shapes);
			obj1 = obj1.optJSONObject("biureturn");
			Integer used_ocpu = obj1.optInt("used_ocpu");
			Integer used_mem = obj1.optInt("used_mem");
			Integer used_blockvolumn = obj1.optInt("used_blockvolumn");
			if (used_ocpu >= totalcore) {
				// 超出
				Set<String> shapekeys = Biu.SHAPEMAP.keySet();
				for (String newshape : shapekeys) {
					boolean isexists = false;
					for (String oshape : shapes) {
						if (newshape.equals(oshape))
							isexists = true;
					}
					if (!isexists)
						s1.add(newshape);
				}
			} else {
				return shapes;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s1;
	}

	private static boolean isSpecialChar(String str) {
		String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.find();
	}

	private static boolean isAcronym(String word) {
		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);
			if (!Character.isLowerCase(c)) {
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) {
		String c1 = "aWaaa11#1111";
		boolean _re = ParamsVaildChecker.passwordRegex(c1);
		System.out.println(_re);
	}

}
