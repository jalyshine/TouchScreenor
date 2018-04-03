package com.jaly.touchscreenor.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 解析后代码执行器
 * @author Administrator
 *
 */
public class CommandExecution {

	private DataOutputStream dos = null;
	private Process process = null;
	private OutputStream os = null; 

	/**
	 * 初始化
	 * @return
	 * @throws IOException 
	 */
	public CommandExecution() throws IOException { 
		process = Runtime.getRuntime().exec("sh");
		os = process.getOutputStream();
		dos = new DataOutputStream(os); 
	}

	/**
	 * 销毁资源
	 * @return
	 * @throws IOException 
	 */
	public void destroy() throws IOException { 
		os.close(); 
		dos.close(); 
		process.destroy(); 
	}
	
	/**
     * 批量运行shell命令
     * @param cmds
	 * @throws IOException 
	 * @throws InterruptedException 
     */
    public void execShellCmd(String[] cmds) throws IOException, InterruptedException {  
		for(String cmd : cmds){
			dos.writeBytes(cmd);
			dos.writeBytes("\n");
			Thread.sleep(500);   //默认延时
		}
		dos.flush();  
    }
    
    /** 
     * 运行单条shell命令
     * @param command 
     * @param isRoot 
     * @return 
     * @throws IOException 
     * @throws InterruptedException 
     */  
    public void execShellCmd(String command) throws IOException, InterruptedException { 
		dos.writeBytes(command);
		dos.writeBytes("\n");
		Thread.sleep(500);   //默认延时
		dos.flush();  
    } 
    
    /**
     * 执行单条脚本
     * @param command
     * @throws InterruptedException 
     * @throws IOException 
     */
    public void execScript(String cmd) throws InterruptedException, IOException{
    	if(cmd.startsWith("delay")){
    		int idx = cmd.indexOf(' ');
    		int delay = Integer.parseInt(cmd.substring(idx + 1, cmd.length()));
    		Thread.sleep(delay);
    	} else {
    		dos.writeBytes(cmd);
			dos.writeBytes("\n"); 
			dos.flush();
    	}
    }
    
}
