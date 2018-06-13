package com.fw.module;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fw.plugin.FWPlugin;

// 总的模块管理
public class FWModuleManager {
	
	@SuppressWarnings("rawtypes")
	private Map<Class, FWModule> fwModules = new ConcurrentHashMap<Class, FWModule>(
			20);
	public void loadModules() {
		// 以 <key,value> 记录所有的模块
		fwModules.put(FWOragnizationModule.class, new FWOragnizationModule());
	} 

	public void initModules(FWPlugin fwPlugin) {
		for (FWModule module : fwModules.values()) {
			if (module != null)
				module.initialize(fwPlugin);
		}
	}

	public void startModules() {
		for (FWModule module : fwModules.values()) {
			if (module != null)
				module.start();
		}
	}

	public void stopModules() {
		for (FWModule module : fwModules.values()) {
			if (module != null)
				module.stop();
		}
	}

	public void destroyModules() {
		for (FWModule module : fwModules.values()) {
			if (module != null)
				module.destroy();
		}
	}
	
	
}
