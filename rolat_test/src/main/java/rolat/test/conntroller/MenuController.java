/*
package rolat.test.conntroller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import rolat.SpringBootApplicationMain;
import rolat.entity.Menu;
import rolat.service.IMenuService;
import rolat.utils.PageUtil;

*/
/**
 * 系统菜单相关设置
 * @author Rolat
 *//*

@RestController
@RequestMapping("/data/menu")
public class MenuController {

	Log log=LogFactory.getLog(SpringBootApplicationMain.class);
	
	@Autowired
	private IMenuService menuService;
	
	*/
/**
	 * 根据实体且查询
	 * @param page
	 * @param limit
	 * @param menu
	 * @return
	 *//*

	@RequestMapping(value="/getMenuList",method={RequestMethod.GET,RequestMethod.POST})
	public Map<String, Object> getMenuList(Integer page,Integer limit,Menu menu,Integer orFlag) {
		Page<Menu> menuData=menuService.getMenuList(menu,PageUtil.getPageRequest(page, limit),orFlag);
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("code", 0);
		map.put("msg", "");
		map.put("data", menuData.getContent());
		map.put("count", menuData.getTotalElements());
		return map;
	}
	*/
/**
	 * 获得所有的菜单
	 * 构建页面左边菜单
	 *//*

	@RequestMapping(value="/findAllByStatus",method=RequestMethod.POST)
	public List<Menu> findAllByStatus(Integer status){
		List<Menu> menuList=menuService.findAllByStatus(status);
		Map<Long, List<Menu>> menuMap=new TreeMap<>();
		for (Menu menu : menuList) {
			Long pid=menu.getPid()==null?0l:menu.getPid();
			List<Menu> list=menuMap.get(pid);
			if(list==null)
				list=new ArrayList<>();
			list.add(menu);
			menuMap.put(pid, list);
		}
		List<Menu> rootList=menuMap.get(0L);
		if(rootList!=null&&rootList.size()>0){
			conversionMenuList(rootList,menuMap);
		}
		menuMap=null;
		return rootList;
	}
	
	//构建上下级关联关系
	private void conversionMenuList(List<Menu> menuList,Map<Long, List<Menu>> menuMap){
		for (Menu menu : menuList) {
			List<Menu> subMenuList=menuMap.get(menu.getId());
			menu.setSubMenuList(subMenuList);
			if(subMenuList!=null&&subMenuList.size()>0){
				Collections.sort(subMenuList, new Comparator<Menu>() {
					@Override
					public int compare(Menu o1, Menu o2) {
						return o1.getSequence().compareTo(o2.getSequence());
					}
				});
				conversionMenuList(subMenuList,menuMap);
			}
		}
	}
	
	@RequestMapping(value="/saveOrUpdateMenu",method=RequestMethod.POST)
	public Map<String, Object> saveOrUpdateMenu(Menu menu_old){
		Map<String, Object> result=new HashMap<>();
		try {
			Menu menu=menuService.saveOrUpdateMenu(menu_old);
			result.put("status", 1);
			result.put("id", menu.getId());
		} catch (Exception e) {
			log.error("",e.getCause());
			result.put("status", 0);
		}
		return result;
	}
	@RequestMapping(value="/deleteMenu",method=RequestMethod.POST)
	public Map<String, Object> deleteMenu(@RequestParam(value = "menuIds[]") Long[] menuIds){
		Map<String, Object> result=new HashMap<>();
		try {
			List<Menu> menuList=new ArrayList<Menu>();
			for (Long id : menuIds) {
				menuList.add(new Menu(id));
			}
			
			menuService.deleteMenu(menuList);
			result.put("status", 1);
			result.put("id", StringUtils.arrayToCommaDelimitedString(menuIds));
		} catch (Exception e) {
			log.error("",e.getCause());
			result.put("status", 0);
		}
		return result;
	}
}

















*/
