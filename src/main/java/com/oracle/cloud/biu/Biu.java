package com.oracle.cloud.biu;

import static org.fusesource.jansi.Ansi.ansi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.fusesource.jansi.AnsiConsole;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.oracle.cloud.biu.api.BasicAuthenticationAPI;
import com.oracle.cloud.biu.api.ComputeAPI;
import com.oracle.cloud.biu.entity.KV;
import com.oracle.cloud.biu.utils.BiuUtils;
import com.thoughtworks.xstream.XStream;

import de.codeshelf.consoleui.elements.ConfirmChoice;
import de.codeshelf.consoleui.prompt.ConfirmResult;
import de.codeshelf.consoleui.prompt.ConsolePrompt;
import de.codeshelf.consoleui.prompt.PromtResultItemIF;
import de.codeshelf.consoleui.prompt.builder.InputValueBuilder;
import de.codeshelf.consoleui.prompt.builder.ListPromptBuilder;
import de.codeshelf.consoleui.prompt.builder.PromptBuilder;
import jline.TerminalFactory;
import lombok.extern.log4j.Log4j;

@Log4j
public class Biu {

	public static final String BIUPROFILE = "./biuaccount";
	public static JSONObject MENU = null;
	public static Map<String, JSONObject> MENUMAP;
	public static Map<String, JSONObject> MENURELATIONMAP;

	public static void main(String[] args) {
		try {
			File file = new File(BIUPROFILE);
			if (!file.exists()) {
				log.warn("biuaccount not exists");
				FileUtils.writeStringToFile(file, "");
			}
			String accountstr = FileUtils.readFileToString(new File(BIUPROFILE));
			initBiu(accountstr);
			loadPrompt();

			Map<String, String> m = BiuUtils.getProps();
			
			//Login to Biu
			BasicAuthenticationAPI.setUserInfoForLogin(m.get("cloud_tenant"), m.get("endpoint"), m.get("cloud_domain"),
					m.get("cloud_username"), m.get("cloud_password"));
			BasicAuthenticationAPI.login(m.get("login"));
//			ComputeAPI.listComputes(m.get("computeinstances"), false);
			showInteractiveMenu(MENU, MENU);
			
			
			
			
			
			
			
			
			
			
			
			
			
//			HashMap<String, ? extends PromtResultItemIF> map = rootPrompt();
//			JSONObject obj = (JSONObject) JSON.toJSON(map);
//			String selectedVal = obj.getJSONObject("rootcmd").get("selectedId").toString();
//			JSONArray fenjie = MENU.getJSONArray("subcommand");
//			//判断用户选择项
//			for (Object tobj : fenjie) {
//				JSONObject t1 = (JSONObject) tobj;
//				String tkey = t1.get("name").toString();
//				if (tkey.equals(selectedVal)) {
//					//hit command
//					log.debug("tkey:" + tkey);
//				}
//			}
//			log.debug("selected:" + selectedVal);
			
//			 ComputeAPI.listComputes(m.get("computeinstances"), true);
//			 ComputeAPI.listSSHKeys(m.get("sshkeys"));
//			 ComputeAPI.listShape(m.get("shapes"));
//			StorageAPI.deleteStorageVolumns(m.get("delete_storage_volumn"), "/vol_a1w");
			// ComputeAPI.listOSContainers(m.get("oscontainers"));
			// ComputeAPI.listOSImages(m.get("osimages"));
			// ComputeAPI.listMyImages(m.get("myimages"));
			// ComputeAPI.listMyConImages(m.get("myconimages"));
			// ComputeAPI.listOracleImages(m.get("oracleimages"));
			// NetworkAPI.listSharedFixIPs(m.get("shared_fixip"));
			// IP ip = NetworkAPI.createSharedFixIP(m.get("create_fixip"));
			// StorageAPI.listStorageVolumns(m.get("storagevolumns"));
			// ComputeAPI.createComputeInstance("oc3",
			// "/oracle/public/OL_7.2_UEKR4_x86_64", m.get("createcompute"));
			//
			// String vcable =
			// ComputeAPI.getVcable(m.get("viewcompute"),
			// "/Compute-gse00000578/cloud.admin/Biu_lt/e982e50b-c042-43f6-ba5c-d3136c80e162");
			// NetworkAPI.createSharedRandomIP(m.get("create_randomip"),
			// vcable);
//			 StorageAPI.createStorageVolumns("12", "false",
//			 m.get("create_storage"));
			// StorageAPI.createStorageAttachments(1, "vol_ny",
			// "/Compute-gse00000578/cloud.admin/Biu_lt/e982e50b-c042-43f6-ba5c-d3136c80e162",
			// m.get("attach_storage"));
			// NetworkAPI.listIPN(m.get("listipnetwork"));
			// NetworkAPI.createIPN(m.get("createipnetwork"),
			// "192.168.100.0/24");
			//
			// HttpResponse<JsonNode> aaa =
			// Unirest.get("https://api-z12.compute.em2.oraclecloud.com/sshkey/").header("Accept",
			// "application/oracle-compute-v3+directory+json").header("Content-Type",
			// "application/json").header("Cookie",
			// hr.getHeaders().getFirst("Set-Cookie")).asJson();
			// System.out.println(aaa.getBody().getObject());

			// HttpResponse<JsonNode> hr =
			// Unirest.post("https://api-z12.compute.em2.oraclecloud.com/authenticate/").header("Accept",
			// "application/json").header("Content-Type",
			// "application/json").basicAuth("/Compute-gse00000578/cloud.admin",
			// "entEraL@1SHIN").body("{ \"password\": \"entEraL@1SHIN\",
			// \"user\": \"/Compute-gse00000578/cloud.admin\"}").asJson();
			// System.out.println(hr.getHeaders().getFirst("Set-Cookie"));
			// HttpResponse<JsonNode> aaa =
			// Unirest.get("https://api-z12.compute.em2.oraclecloud.com/sshkey/").header("Accept",
			// "application/oracle-compute-v3+directory+json").header("Content-Type",
			// "application/json").header("Cookie",
			// hr.getHeaders().getFirst("Set-Cookie")).asJson();
			// System.out.println(aaa.getBody().getObject());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void initBiu(String accountstr) throws Exception {
		AnsiConsole.systemInstall();
		System.out.println(ansi().eraseScreen().render("@|green 你好，欢迎您使用Biu|@\n@|reset "
				+ "在开始使用之前请您确认您已经得到了使用授权，并仔细阅读过使用文档\n" + "Biu的作者为Oracle公有云事业部 Zhqiaing.|@"));
		if (StringUtils.isEmpty(accountstr)) {
			try {
				HashMap<String, ? extends PromtResultItemIF> result = initPrompt();
				String json = JSON.toJSONString(result);
				log.debug("json=" + json);

				ConfirmResult delivery = (ConfirmResult) result.get("delivery");
				if (delivery.getConfirmed() == ConfirmChoice.ConfirmationValue.YES) {
					String out1 = BiuUtils.encrypted(json);
					FileUtils.writeStringToFile(new File(BIUPROFILE), out1);
					System.out.println("您的初始化信息已经保存至当前目录下的.biuaccount文件中，您可以开始使用Biu了");
				} else {
					System.out.println("您选择了否，所以配置信息没有写入");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					TerminalFactory.get().restore();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			log.debug("loaded biu profile:" + accountstr);
			boolean checkresult = BiuUtils.checkAvailable(accountstr);
			if (checkresult) {
				log.info("Account was checked successful.");
			} else {
				log.info("Account was checked failed, please init your Biu profile with command: >>>Biu init.");
			}
		}
		try {
			TerminalFactory.get().restore();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void showInteractiveMenu(JSONObject t, JSONObject t1) throws Exception {
		if (null == t1)
			return;
		JSONArray ary = t1.getJSONArray("subcommand");
		if (null == ary)
			return;
		HashMap<String, ? extends PromtResultItemIF> result = showMenu(t1, ary);
		log.debug("ORGI Result Map:" + result);
		JSONObject obj = (JSONObject) JSON.toJSON(result);
		menuControl(t, t1, ary, obj);
	}

	public static HashMap<String, ? extends PromtResultItemIF> showMenu(JSONObject t1, JSONArray ary)
			throws IOException {
//		log.debug("******************* [Show Menu] ********************");
//		log.debug(new XStream().toXML(ary));
		ConsolePrompt prompt = new ConsolePrompt();
		PromptBuilder promptBuilder = prompt.getPromptBuilder();
		if ((!StringUtils.isEmpty(t1.getString("prompt"))) && (t1.getString("prompt").equals("list"))) {
			ListPromptBuilder lpb = promptBuilder.createListPrompt().name("rootcmd").message("你想要做有关什么的操作?");
			if (StringUtils.isEmpty(t1.getString("endpoint")))
				lpb.newItem("exit").text("退出").add();
			else
				lpb.newItem("back").text("返回上一级").add();
			for (int i = 0; i < ary.size(); i++) {
				JSONObject obj2 = ary.getJSONObject(i);
				if (!obj2.getString("prompt").equals("none")) {
					lpb.newItem(obj2.getString("name")).text(obj2.getString("desc")).add();
					log.debug("added " + obj2.getString("desc"));
				}
			}
			lpb.addPrompt();
			
		} else if ((!StringUtils.isEmpty(t1.getString("prompt"))) && (t1.getString("prompt").equals("input"))) {
			InputValueBuilder lpb = promptBuilder.createInputPrompt().name("rootcmd").message("你想要做有关什么的操作?");
			for (int i = 0; i < ary.size(); i++) {
				JSONObject obj2 = ary.getJSONObject(i);
				log.debug("added " + obj2.getString("desc"));
				lpb = promptBuilder.createInputPrompt().name(obj2.getString("name")).message(obj2.getString("desc"));
				lpb.addPrompt();
			}
			
		} else if ((!StringUtils.isEmpty(t1.getString("prompt"))) && (t1.getString("prompt").equals("view"))) {
			InputValueBuilder lpb = promptBuilder.createInputPrompt().name("rootcmd").message("你想要做有关什么的操作?");
			for (int i = 0; i < ary.size(); i++) {
				JSONObject obj2 = ary.getJSONObject(i);
				log.debug("added " + obj2.getString("desc"));
				lpb = promptBuilder.createInputPrompt().name(obj2.getString("name")).message(obj2.getString("desc"));
			}
			lpb.addPrompt();
		}
		HashMap<String, ? extends PromtResultItemIF> result = prompt.prompt(promptBuilder.build());
		return result;
	}

	public static void menuControl(JSONObject t, JSONObject t1, JSONArray ary, JSONObject obj) throws Exception {
		if ((null != obj.getJSONObject("rootcmd")) && ("back".equals(obj.getJSONObject("rootcmd").getString("selectedId")))) {
			showInteractiveMenu(t, t);
		} else if ((null != obj.getJSONObject("rootcmd")) && ("exit".equals(obj.getJSONObject("rootcmd").getString("selectedId")))) {
			System.exit(0);
		} else {
			if ((t1.getString("prompt").equals("input"))) {
				//TODO 处理三层结构下的菜单交互性
//				if (t1.toJSONString().indexOf("subcommand") > 1) {
//					showInteractiveMenu(t1, t1);
//					return;
//				}
				JSONArray aa1 = t1.getJSONArray("subcommand");
				List<KV> plist = new ArrayList<KV>();
				for (Object to : aa1) {
					KV kv = new KV();
					JSONObject json = (JSONObject) to;
					kv.setKey(json.getString("name"));
					kv.setValue(json.getString("prompt"));
					plist.add(kv);
				}
				log.debug("============== T1 Debug Input =============");
				log.debug(t1);
				log.debug("============== KV List Debug =============");
				log.debug(new XStream().toXML(plist));
				Object objj = BiuConsoleAgent.runCommand(t1, obj, plist);
				if (null == objj)
					showInteractiveMenu(MENURELATIONMAP.get(t.getString("name")), t);
				else
					showInteractiveMenu(t, t1);
			} else if ((t1.getString("prompt").equals("show"))) {
				log.debug("============== T1 Debug Show =============");
				log.debug(t1);
				Object objj = BiuConsoleAgent.runCommand(t1, obj);
				showInteractiveMenu(MENURELATIONMAP.get(t.getString("name")), t);
			}
			log.debug("============== Menu Debug =============");
			for (Object object : ary) {
				log.debug("First Param:" + MENURELATIONMAP.get(((JSONObject) object).getString("name")).getString("desc"));
				log.debug("Second Param:" + MENUMAP.get(obj.getJSONObject("rootcmd").getString("selectedId")).getString("desc"));
				showInteractiveMenu(MENURELATIONMAP.get(((JSONObject) object).getString("name")), MENUMAP.get(obj.getJSONObject("rootcmd").getString("selectedId")));
			}
		}
	}

	public static HashMap<String, ? extends PromtResultItemIF> initPrompt() throws IOException {
		ConsolePrompt prompt = new ConsolePrompt();
		PromptBuilder promptBuilder = prompt.getPromptBuilder();

		promptBuilder.createInputPrompt().name("endpoint").message("请输入您的Endpoint，如果不知道请咨询Oracle售前").defaultValue("")
				// .mask('*')
				.addPrompt();

		promptBuilder.createInputPrompt().name("clouddomain").message("请输入您的云账户domain，如果不知道请咨询Oracle售前")
				.defaultValue("")
				// .mask('*')
				.addPrompt();

		promptBuilder.createInputPrompt().name("clouduser").message("请输入您的云账户用户名").defaultValue("")
				// .mask('*')
				.addPrompt();

		promptBuilder.createInputPrompt().name("cloudpassword").message("请输入您的云账户密码").defaultValue("")
				 .mask('*')
				.addPrompt();

		promptBuilder.createInputPrompt().name("cloudtenant").message("请输入您的租户名").defaultValue("")
				// .mask('*')
				.addPrompt();

		promptBuilder.createConfirmPromp().name("delivery").message("您输入的信息都正确吗？")
				.defaultValue(ConfirmChoice.ConfirmationValue.YES).addPrompt();

		HashMap<String, ? extends PromtResultItemIF> result = prompt.prompt(promptBuilder.build());
		return result;
	}

//	public static HashMap<String, ? extends PromtResultItemIF> rootPrompt() throws Exception {
//		ConsolePrompt prompt = new ConsolePrompt();
//		PromptBuilder promptBuilder = prompt.getPromptBuilder();
//		JSONObject rp = MENU;
//		ListPromptBuilder lpb = promptBuilder.createListPrompt().name("rootcmd").message("你想要做有关什么的操作?");
//		log.debug("loading json menu...");
//		for (int i = 0; i < rp.getJSONArray("subcommand").size(); i++) {
//			if ("true".equals(rp.getJSONArray("subcommand").getJSONObject(i).getString("prompt"))) {
//				ListItemBuilder lib = lpb.newItem(rp.getJSONArray("subcommand").getJSONObject(i).getString("name")).text(rp.getJSONArray("subcommand").getJSONObject(i).getString("desc"));
//				lib.add();
//				log.debug("added " + rp.getJSONArray("subcommand").getJSONObject(i).getString("desc"));
//			}
//		}
//		lpb.addPrompt();
//		HashMap<String, ? extends PromtResultItemIF> result = prompt.prompt(promptBuilder.build());
//		return result;
//	}
	
	public static void loadPrompt() throws Exception {
		String menu = BiuUtils.ConvertStream2Json(BiuUtils.class.getClassLoader().getResourceAsStream("opc_rest.json"));
		MENU = (JSONObject) JSON.parse(menu);
		MENUMAP = new HashMap<String, JSONObject>();
		MENURELATIONMAP = new HashMap<String, JSONObject>();
		loadPromptMap(MENU);
	}
	
	public static void loadPromptMap(JSONObject rootmenu) throws Exception {
		if (null == rootmenu)
			return;
		JSONArray root = rootmenu.getJSONArray("subcommand");
		if (null != root) {
			for (int i = 0; i < root.size(); i++) {
				JSONObject amenu = (JSONObject) root.get(i);
				MENUMAP.put(amenu.getString("name"), amenu);
				MENURELATIONMAP.put(amenu.getString("name"), rootmenu);
				log.debug("saved menu map relation item:" + amenu.getString("name"));
				loadPromptMap(amenu);
			}
		}
	}

}
