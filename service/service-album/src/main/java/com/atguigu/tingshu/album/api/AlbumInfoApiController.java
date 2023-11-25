package com.atguigu.tingshu.album.api;

import com.atguigu.tingshu.album.service.AlbumInfoService;
import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.common.util.AuthContextHolder;
import com.atguigu.tingshu.query.album.AlbumInfoQuery;
import com.atguigu.tingshu.query.search.AlbumIndexQuery;
import com.atguigu.tingshu.vo.album.AlbumInfoVo;
import com.atguigu.tingshu.vo.album.AlbumListVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "专辑管理")
@RestController
@RequestMapping("api/album/albumInfo")
@SuppressWarnings({"unchecked", "rawtypes"})
public class AlbumInfoApiController {

	@Autowired
	private AlbumInfoService albumInfoService;
	
	/**
	 * Save Album
	 */
	@PostMapping("saveAlbumInfo")
	public Result saveAlbumInfo(@RequestBody @Validated AlbumInfoVo albumInfoVo){
		Long userId = AuthContextHolder.getUserId();
		this.albumInfoService.saveAlbumInfo(albumInfoVo,userId);
		return Result.ok();
	}
	
	
	/**
	 * Find Albums of this User
	 */
	@PostMapping("findUserAlbumPage/{page}/{limit}")
	public Result findUserAlbumPage(@PathVariable Long page,
					@PathVariable Long limit,
					@RequestBody AlbumInfoQuery albumInfoQuery){
		//(not null) get userId from AuthContextHolder
		Long userId =
			AuthContextHolder.getUserId() == null ? 1l : AuthContextHolder.getUserId();
		
		//將userId賦值
		albumInfoQuery.setUserId(userId);
		
		//創建分頁對象
		Page<AlbumListVo> albumListVoPage = new Page<>(page,limit);
		
		IPage<AlbumListVo> iPage = this.albumInfoService.findUserAlbumPage(albumListVoPage,albumInfoQuery);
		
		return Result.ok(iPage);
	}
	
	/**
	 * Delete Album (just DELETE, it won't be permanent)
	 */
	@DeleteMapping("removeAlbumInfo/{albumId}")
	public Result removeAlbumInfoById(@PathVariable Long albumId){
		this.albumInfoService.removeAlbumInfoById(albumId);
		return Result.ok();
	}


}

