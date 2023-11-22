package com.atguigu.tingshu.album.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.tingshu.album.mapper.BaseCategory1Mapper;
import com.atguigu.tingshu.album.mapper.BaseCategory2Mapper;
import com.atguigu.tingshu.album.mapper.BaseCategory3Mapper;
import com.atguigu.tingshu.album.mapper.BaseCategoryViewMapper;
import com.atguigu.tingshu.album.service.BaseCategoryService;
import com.atguigu.tingshu.model.album.BaseCategory1;
import com.atguigu.tingshu.model.album.BaseCategoryView;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class BaseCategoryServiceImpl extends ServiceImpl<BaseCategory1Mapper, BaseCategory1> implements BaseCategoryService {

	@Autowired
	private BaseCategory1Mapper baseCategory1Mapper;

	@Autowired
	private BaseCategory2Mapper baseCategory2Mapper;

	@Autowired
	private BaseCategory3Mapper baseCategory3Mapper;
	
	@Autowired
	private BaseCategoryViewMapper baseCategoryViewMapper;


@Override
public List<JSONObject> getBaseCategoryList() {
	//create return List
	List<JSONObject> list = new ArrayList<>();
	
	//get all first tier categories
	List<BaseCategoryView> baseCategoryViewList =
		baseCategoryViewMapper.selectList(null);
	
	/**
	 * 使用一級分類進行分組，組成map集合
	 * KEY=	  {category1Id}
	 * VALUE= {List<BaseCategoryView>}(Category2)
	 */
	Map<Long,List<BaseCategoryView>> baseCategory1IdMap =
		baseCategoryViewList.stream().collect(
			Collectors.groupingBy(BaseCategoryView::getCategory1Id)
		);
	
	Iterator<Map.Entry<Long, List<BaseCategoryView>>>
		iterator = baseCategory1IdMap.entrySet().iterator();
	
	//the most outer (return value) list
	while(iterator.hasNext()){
		
		Map.Entry<Long, List<BaseCategoryView>>
			entry = iterator.next();
		
		Long category1Id = entry.getKey();
		
		List<BaseCategoryView> baseCategory1 = entry.getValue();
		
		JSONObject category1 = new JSONObject();
		
		category1.put("categoryId",category1Id);
		category1.put("categoryName",
			baseCategory1.get(0).getCategory1Name());
		
		/**
		 * get second layer of category
		 * KEY =   {category2Id}
		 * VALUE = {List<baseCategoryView>}
		 */
		Map<Long, List<BaseCategoryView>> baseCategory2IdMap =
			baseCategory1.stream().collect(
				Collectors.groupingBy(BaseCategoryView::getCategory2Id)
			);
		
		Iterator<Map.Entry<Long, List<BaseCategoryView>>>
			iterator1 = baseCategory2IdMap.entrySet().iterator();
		
		//create a list for all the 2nd layer categories
		ArrayList<JSONObject> categoryChild2List = new ArrayList<>();
		
		while (iterator1.hasNext()) {
			Map.Entry<Long, List<BaseCategoryView>>
				entry1 = iterator1.next();
			Long category2Id = entry1.getKey();
			List<BaseCategoryView> baseCategory2 = entry1.getValue();
			
			JSONObject category2 = new JSONObject();
			category2.put("categoryId",category2Id);
			category2.put("categoryName",baseCategory2.get(0).getCategory2Name());
			
			/**
			 * get category 3 and turn them into a list
			 */
			List<Object> categoryChild3List =
				baseCategory2.stream().map(
					baseCategoryView -> {
						JSONObject category3 = new JSONObject();
						category3.put("categoryId", baseCategoryView.getCategory3Id());
						category3.put("categoryName", baseCategoryView.getCategory3Name());
						return category3;
					}
					).collect(Collectors.toList());
					
			//put (all 3rd layer categories LIST) to 2nd layer
			category2.put("categoryChild",categoryChild3List);
		}
		//put(all 2nd layer LIST) to 1st layer
		category1.put("categoryChild",categoryChild2List);
		//add (1st layer categories LIST) to the list
		list.add(category1);
	}
	
	return list;
}


}
