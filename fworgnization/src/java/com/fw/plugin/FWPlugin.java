package com.fw.plugin;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.xmpp.component.ComponentManager;
import org.xmpp.component.ComponentManagerFactory;

import com.fw.module.FWModule;
import com.fw.module.FWModuleManager;
import com.fw.module.FWOragnizationModule;

// 自定义插件 要实现  openfire内的 插件接口 Plugin
public class FWPlugin implements Plugin{
	
	private XMPPServer server;
	private InterceptorManager interceptorManager;
	private ComponentManager componentManager;
	private FWModuleManager fwModuleManager;
	
	// openfire 服务器启动时 会从创建 FWPlugin
	public FWPlugin() {
		System.out.println("FWOrgnizationPlugin插件开始了");
		server = XMPPServer.getInstance();
		interceptorManager = InterceptorManager.getInstance();
		componentManager = ComponentManagerFactory.getComponentManager();
		fwModuleManager =  new FWModuleManager(); 
	}
	
	// 创建FWPlugin之后 会调用 initializePlugin() 方法 进行初始化
	@Override 
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		System.out.println("初始化FWPlugin");
		
		fwModuleManager.loadModules();			// 先加载 插件中的模块			
		fwModuleManager.initModules(this);			// 再初始化 插件中的模块
		fwModuleManager.startModules();			// 最后启动 插件中的模块

		System.out.println("FWPlugin初始化结束");
	}
	
	@Override
	public void destroyPlugin() {

		fwModuleManager.stopModules();
		fwModuleManager.destroyModules();
		server = null;
		interceptorManager = null;
		componentManager = null;
		
		System.out.println("FWPlugin插件摧毁结束");
	}

	
	public FWModuleManager getFwModuleManager() {
		return fwModuleManager;
	}


	public XMPPServer getServer() {
		return server;
	}

	public InterceptorManager getInterceptorManager() {
		return interceptorManager;
	}

	public ComponentManager getComponentManager() {
		return componentManager;
	}
	
}
