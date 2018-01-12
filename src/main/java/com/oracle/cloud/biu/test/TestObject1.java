package com.oracle.cloud.biu.test;

import java.util.List;
import java.util.Scanner;

import oracle.cloud.storage.CloudStorage;
import oracle.cloud.storage.CloudStorageConfig;
import oracle.cloud.storage.CloudStorageFactory;
import oracle.cloud.storage.exception.AuthenticationException;
import oracle.cloud.storage.model.Container;
import oracle.cloud.storage.model.Key;

public class TestObject1 {

	public static String domain = "";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.out.println("####################################################################################################");
			System.out.println("####################################################################################################");
			System.out.println("################################### Oracle Storage Cloud Tools v1.0 ################################");
			System.out.println("(*) You can get your Username,password,Identity Domain from email box sent from Oracle active email.");
			System.out.println("(*) You can get service end point url from Oracle Public Cloud my service UI console.");
			System.out.println("(*) Author: China DB SC - Alex Liu, mail:zhiqiang.x.liu@oracle.com          .");
			System.out.println("####################################################################################################");
			System.out.println("####################################################################################################");
			System.out.println("---> Please input your domain, username, password for oracle public cloud");
			Scanner input = new Scanner(System.in);
			String val = null;
			String id_domain = "gse00002004";
			String username = "cloud.admin";
			String password = "aSleEP@0TyKe";
			String service_end_point = "https://em2.storage.oraclecloud.com";
//			System.out.print("Please input Identity Domain:");
//			val = input.next();
//			id_domain = val;
//			System.out.print("Please input Username:");
//			val = input.next();
//			username = val;
//			System.out.print("Please input Password:");
//			val = input.next();
//			password = val;
//			System.out.print("Please input Service Endpoint(input y use https://em2.storage.oraclecloud.com):");
//			val = input.next();
//			if ("y".equals(val))
//				val = "https://em2.storage.oraclecloud.com";
//			service_end_point = val;
			CloudStorageConfig myConfig = new CloudStorageConfig();
			myConfig.setServiceName("Storage-" + id_domain)
					.setUsername(username).setPassword(password.toCharArray())
					.setServiceUrl(service_end_point);
			for (int i = 0; i < 100; i++) {
				System.out.print("#");
				Thread.sleep(10);
			}
			domain = id_domain;
			command(input, myConfig);
			// Container c = myConnection.createContainer("test_container1");
			// myConnection.deleteContainer("test_container1");
			// System.out.println(c.getSize());
			// FileInputStream fis = new FileInputStream("D:\\job\\menu.txt");
			// myConnection.storeObject("test_container1", "menu.txt",
			// "text/plain", fis);
		} catch (AuthenticationException e) {
			System.out.println();
			System.out.println("Validated failed by your provided.");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void command(Scanner input, CloudStorageConfig myConfig) {
		CloudStorage myConnection = CloudStorageFactory
				.getStorage(myConfig);
		listContainer(myConnection);
//		input = new Scanner(System.in);
//        String val = null;
//        do{
//        	System.out.println();
//        	System.out.println("Please input 1 to create Container");
//        	System.out.println("Please input 2 to list Container");
//        	System.out.println("Please input 3 to delete Container");
//        	System.out.println("(*) Please input quit to exit this tools");
////        	System.out.println("Please input 4 to list storage object");
//        	System.out.println("");
//        	System.out.print("Please input your command: ");
//        	val = input.next();
//        	if ("1".equals(val)) {
//        		createContainer(input, myConnection);
//        	} else if ("2".equals(val)) {
//        		listContainer(myConnection);
//        	} else if ("3".equals(val)) {
//        		deleteContainer(input, myConnection);
//        	} else {
//        		if ("quit".equals(val)) {
//        			System.out.println("accepted command.");
//        		} else {
//        			System.out.println("unknown command.");
//        		}
//        	}
//        } while(!val.equals("quit"));   // 濡傛灉杈撳叆鐨勫�涓嶆槸#灏辩户缁緭鍏�
//        input.close(); // 鍏抽棴璧勬簮
	}

	private static void listStorageObject(CloudStorage myConnection) {
		List<Key> klist = myConnection.listObjects("testaaa", null);
		if ((null == klist) || (0 == klist.size())) {
			System.out.println("You have not any Storage object");
			return;
		}
		for (int i = 0; i < klist.size(); i++) {
			System.out.println((i+1) + ". " + klist.get(i).getKey());
		}
	}

	private static void deleteContainer(Scanner input, CloudStorage myConnection) {
		String val;
		System.out.println("Your container list as below:");
		List<Container> clist = myConnection.listContainers();
		for (int i = 0; i < clist.size(); i++) {
			System.out.println((i+1) + ". " + clist.get(i).getName());
		}
		System.out.print("Please input your want to remove the container name:");
		val = input.next();
		myConnection.deleteContainer(val);
		System.out.println("Your container has been removed.");
	}

	private static void listContainer(CloudStorage myConnection) {
		System.out.println("\nYour container list as below:");
		List<Container> clist = myConnection.listContainers();
		if ((null == clist) || (0 == clist.size())) {
			System.out.println("You have not any Container");
			return;
		}
		for (int i = 0; i < clist.size(); i++) {
			Container con = clist.get(i);
			System.out.println("Name:" + con.getName() + "        Size：" + con.getSize());
			if ("archivestorage".equals(clist.get(i).getName())) {
				System.out.println((i+1) + ". " + clist.get(i).getName() + " --- storaged object counts:" + clist.get(i).getCount() + " --- size:" + clist.get(i).getSize());
			}
		}
	}

	private static void createContainer(Scanner input,
			CloudStorage myConnection) {
		String val;
		System.out.print("Please input the container name:");
		val = input.next(); 
		Container c = myConnection.createContainer(val);
		System.out.println("Your Container ["+val+"] has been created.");
		System.out.print("Cloud Storage Container String: Storage-" + domain + "/" + val);
	}

}
