package com.wyb.iocframe.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证输入内容格式的工具类
 * 
 * @author liuj
 */
public class CheckStringUtil {
	private static final String phoneReg = "[1][3578][0-9]{9}";
	private static final String emailReg = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
	private static final String pwdReg = "[0-9a-zA-Z]{4,16}";
	private static final String id_card = "(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])";

	/**
	 * 验证账号密码（手机号或者邮箱）
	 * 
	 * @return
	 */
	public static String checkAccount(String account) {
		if (account.matches(phoneReg) || account.matches(emailReg)) {
			return null;
		}
		return "手机号码格式错误";
	}

	/**
	 * 验证身份在
	 * 
	 * */
	public static boolean ID_card(String account) {
		boolean isIdCard = false;
		Pattern idNumPattern = Pattern.compile(id_card);
		// 通过Pattern获得Matcher
		Matcher idNumMatcher = idNumPattern.matcher(account);
		if (idNumMatcher.matches()) {
			Pattern birthDatePattern = Pattern
					.compile("\\d{6}(\\d{4})(\\d{2})(\\d{2}).*");// 身份证上的前6位以及出生年月日
			Matcher birthDateMather = birthDatePattern.matcher(account);
			if (birthDateMather.find()) {
				String year = birthDateMather.group(1);
				String month = birthDateMather.group(2);
				String date = birthDateMather.group(3);
				String currentStr = DateUtil.longToDateTime(
						System.currentTimeMillis() / 1000, "yyyy-MM-dd");
				String[] currentStrs = currentStr.split("-");
				if (Integer.valueOf(year) > Integer.valueOf(currentStrs[0])
						|| Integer.valueOf(month) > Integer
								.valueOf(currentStrs[1])
						|| Integer.valueOf(date) > Integer
								.valueOf(currentStrs[2])) {
				} else {
					isIdCard = true;
				}
			}
		}
		return isIdCard;
	}

	/**
	 * 验证账号是否是邮箱E-mail
	 * 
	 * */
	public static String checkEmail(String account) {
		if (account.matches(emailReg)) {
			return null;
		}
		return "邮箱格式错误";
	}

	/**
	 * 验证手机号
	 * 
	 * @param phone
	 * @return
	 */
	public static boolean checkPhone(String phone) {
		if (phone.matches(phoneReg)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 验证密码格式
	 * 
	 * @param pwd
	 * @return
	 */
	public static String checkPwd(String pwd) {
		if (!pwd.matches(pwdReg)) {
			return "密码为4-16位数字和字母";
		}
		return null;
	}

	/**
	 * 验证话题字符串，组装成一个可以部分点击且有颜色区分的html字符串
	 * 
	 * @param str
	 * @return
	 */
	public static String postDescriptionReg(String str) {
		if (str == null) {
			return str;
		}

		// String reg = "#[\\u4e00-\\u9fa5aa-zA-z0-9:punct:][^#\\x22]*#";
		String reg = "#[^#\\s]+#";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(str);
		while (m.find()) {
			String s = m.group();
			str = str
					.replace(
							s,
							"<a href='"
									+ s
									+ "' class='text-decoration:none;color:#3598db'><font color='#1b9df8'>"
									+ s + "</font></a>");
		}
		str = getColorCommoner(str);
		return str;
	}

	/**
	 * 生成一个可以在字符串中点击名字的html字符串
	 * 
	 * @return
	 */
	public static String getClickName(String str) {
		if (str == null) {
			return str;
		}
		String reg = "@[^@\\s]+ ";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(str);
		while (m.find()) {
			String s = m.group();
			str = str
					.replace(
							s,
							"<a href='"
									+ s
									+ "' class='text-decoration:none;color:#3598db'><font color='#1b9df8'>"
									+ s + "</font></a>");
		}
		// str = getColorCommoner(str);
		return str;
	}

	/**
	 * 获取图片描述信息
	 * 
	 * @param content
	 *            图片描述
	 * @param petNames
	 *            所属宠物名称，多个“，”号隔开
	 * @param petTypes
	 *            宠物类型 ，多个“，”号隔开
	 * @return
	 */
	// public static String getImgContent(Context context, String content,
	// String petNames, String petTypes) {
	// String contentStr = getColorPost(content);
	// String[] petNameAry = null;
	// if (petNames.contains(",")) {
	// petNameAry = petNames.split(",");
	// } else {
	// petNameAry = new String[] { petNames };
	// }
	// String[] petTypeAry = null;
	// if (petTypes.contains(",")) {
	// petTypeAry = petTypes.split(",");
	// } else {
	// petTypeAry = new String[] { petTypes };
	// }
	// for (int i = 0; i < petNameAry.length; i++) {
	// String html = "";
	// String type = petTypeAry[i];
	// String name = petNameAry[i];
	// if ("狗".equals(type)) {
	// html = "<img src='"
	// + R.drawable.dog_text
	// + "'/><a href='"
	// + name
	// + "' class='text-decoration:none;color:#3598db'><font color='#3598db'>"
	// + name + "</font></a>";
	// } else {
	// html = "<img src='"
	// + R.drawable.cat_text
	// + "'/><a href='"
	// + name
	// + "' class='text-decoration:none;color:#3598db'><font color='#3598db'>"
	// + name + "</font></a>";
	// }
	// // ImageSpan imgSpan = new ImageSpan(context, b);
	// // SpannableString strSpan = new SpannableString("icon");
	// // strSpan.setSpan(imgSpan, 0, 4,
	// // Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	//
	// contentStr += html;
	// }
	//
	// return contentStr;
	// }

	/**
	 * 验证话题字符串，组装成一个有颜色区分的html字符串
	 * 
	 * @param str
	 * @return
	 */
	public static String getColorPost(String str) {
		if (str == null) {
			return str;
		}
		String reg = "#[^#\\s]+#";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(str);
		while (m.find()) {
			String s = m.group();
			str = str.replace(s, "<font color='#3598db'>" + s + "</font>");
		}

		str = getColorCommoner(str);
		return str;
	}

	/**
	 * 验证回复格式
	 * 
	 * @return
	 */
	public static String getColorCommoner(String str) {
		if (str == null) {
			return str;
		}
		String reg = "@[^@\\s]+ ";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(str);
		while (m.find()) {
			String s = m.group();
			str = str.replace(s,
					"<font style='font-weight:bold;font-style:italic;'color='#333333'>"
							+ s + "</font>");
		}
		return str;
	}

	/**
	 * 验证字符串是否为话题形式，1--否；2--是
	 * 
	 * @param str
	 * @return
	 */
	public static int checkPostStr(String str) {
		String reg = "#[^#\\s]+#";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(str);
		while (m.find()) {
			return 2;
		}
		return 1;
	}

	/**
	 * 以第一组匹配的作为 返回的结果 返回值为话题内容
	 * 
	 * @param str
	 *            传入的字符串，mat以什么作为截取
	 * @return
	 */
	public static String getString(String str, String mat) {
		String content = null;
		String regex = "<+" + mat + ">(.*)</" + mat + ">";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			content = matcher.group(1);
		}
		return content;
	}

	/**
	 * 检查字符串中包含的“#”号的个数
	 * 
	 * @return
	 */
	public static int checkJNum(String str) {
		String reg = "#";
		int count = 0;
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(str);
		while (m.find()) {
			++count;
		}
		return count;
	}
	
}