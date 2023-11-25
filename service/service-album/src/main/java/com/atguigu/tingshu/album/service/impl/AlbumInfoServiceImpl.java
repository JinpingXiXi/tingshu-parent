package com.atguigu.tingshu.album.service.impl;

import com.atguigu.tingshu.album.mapper.AlbumAttributeValueMapper;
import com.atguigu.tingshu.album.mapper.AlbumInfoMapper;
import com.atguigu.tingshu.album.mapper.AlbumStatMapper;
import com.atguigu.tingshu.album.service.AlbumAttributeValueService;
import com.atguigu.tingshu.album.service.AlbumInfoService;
import com.atguigu.tingshu.common.constant.SystemConstant;
import com.atguigu.tingshu.model.album.AlbumAttributeValue;
import com.atguigu.tingshu.model.album.AlbumInfo;
import com.atguigu.tingshu.model.album.AlbumStat;
import com.atguigu.tingshu.query.album.AlbumInfoQuery;
import com.atguigu.tingshu.vo.album.AlbumAttributeValueVo;
import com.atguigu.tingshu.vo.album.AlbumInfoVo;
import com.atguigu.tingshu.vo.album.AlbumListVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class AlbumInfoServiceImpl extends ServiceImpl<AlbumInfoMapper, AlbumInfo> implements AlbumInfoService {

	@Autowired
	private AlbumInfoMapper albumInfoMapper;
	@Autowired
	private AlbumAttributeValueMapper albumAttributeValueMapper;
	@Autowired
	private AlbumStatMapper albumStatMapper;
	@Autowired
	private AlbumAttributeValueService albumAttributeValueService;
	
	/**
	 * Save Album Info
	 * 1.
	 * 2.
	 * 3.
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveAlbumInfo(AlbumInfoVo albumInfoVo, Long userId) {
		
		AlbumInfo albumInfo = new AlbumInfo();
		
		//just give an default value for userId
		albumInfo.setUserId(1l);
		
		//copy value from VO(passed in)
		BeanUtils.copyProperties(albumInfoVo,albumInfo);
		
		//表示默认审核通过
		albumInfo.setStatus(SystemConstant.ALBUM_STATUS_PASS);
		
		//
		if (!albumInfo.getPayType().equals(SystemConstant.ALBUM_PAY_TYPE_FREE)) {
			//設置免費集數
			albumInfo.setTracksForFree(5);
		}
		
		//invoke mapper to do the insert
		albumInfoMapper.insert(albumInfo);
		
		//insert complete, get 属性值集合
		List<AlbumAttributeValueVo> albumAttributeValueVoList = albumInfoVo.getAlbumAttributeValueVoList();
		System.out.println("LIST: "+albumAttributeValueVoList);
		
		//if ATTRIBUTES are NOT EMPTY
		if (!CollectionUtils.isEmpty(albumAttributeValueVoList)){
			//put data in
			List<AlbumAttributeValue> albumAttributeValueList =
				//iterate through VO
				albumAttributeValueVoList.stream().map(
					//for each VO:
					albumAttributeValueVo -> {
						//new 屬性標籤對象
						AlbumAttributeValue albumAttributeValue = new AlbumAttributeValue();
						//因為之前已經執行了INSERT, 所以主鍵ID已經生成了
						albumAttributeValue.setAlbumId(albumInfo.getId());
						//
						BeanUtils.copyProperties(albumAttributeValueVo,albumAttributeValue);
						return albumAttributeValue;
					}
				).collect(Collectors.toList());
		}
		
	}
	
	/**
	 *Find User Album Page
	 *
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public IPage<AlbumListVo> findUserAlbumPage(Page<AlbumListVo> albumListVoPage, AlbumInfoQuery albumInfoQuery) {
		return null;
	}
	
	/**
	 * Remove AlbumInfo
	 *
	 * require delete 3 item:
	 * 	1.AlbumInfo
	 * 	2.AlbumStat
	 * 	3.AlbumAttribute
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void removeAlbumInfoById(Long albumId) {
		
		albumInfoMapper.deleteById(albumId);
		
		LambdaQueryWrapper<AlbumStat> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(AlbumStat::getAlbumId,albumId);
		albumStatMapper.delete(wrapper);
		
		LambdaQueryWrapper<AlbumAttributeValue> wrapper1 = new LambdaQueryWrapper<>();
		wrapper1.eq(AlbumAttributeValue::getAlbumId,albumId);
		albumAttributeValueMapper.delete(wrapper1);
	}
	
	
	
	
}
