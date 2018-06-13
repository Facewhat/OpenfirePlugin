package com.fw.module;


import org.xmpp.component.ComponentException;

import com.fw.plugin.FWPlugin;
import com.fw.service.FWOrgModuleService;

// 单个模块管理
public class FWOragnizationModule implements FWModule {
	
	private FWPlugin fwPlugin = null;
	
	
	// 初始化Oragnization 模块，依赖于FWPlugin.
	@Override
	public void initialize(FWPlugin fwPlugin) {
		this.fwPlugin = fwPlugin;
	}
	
	// 开始Oragnization 模块 ，使用 component 组件 对特定子域service进行管理和包处理。
	// 一个插件有多个模块，每个模块有对应的 service 、 dao等，通过component 进行注册管理。
	@Override
	public void start() {
		
		String subdomain = "fwgroup";
		String description = "facewhat group";
		FWOrgModuleService service = new FWOrgModuleService(subdomain, description, false);
		
		try {
			
			fwPlugin.getComponentManager().addComponent(service.getOrgServiceName(), service);
	    
		} catch (ComponentException e) {
	    	System.out.println(" 加入组件失败 : " + e.getMessage());
	    }
	}


	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean isStart() {
		// TODO Auto-generated method stub
		return false;
	}

}
