package com.ssafy.forestkeeper.api.controller;

import javax.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.forestkeeper.application.dto.response.BaseResponseDTO;
import com.ssafy.forestkeeper.application.dto.response.mountain.MountainUserInfoWrapperResponseDTO;
import com.ssafy.forestkeeper.application.dto.response.plogging.PloggingGetListWrapperResponseDTO;
import com.ssafy.forestkeeper.application.dto.response.user.UserPloggingInfoDTO;
import com.ssafy.forestkeeper.application.service.userinfo.UserInfoService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;

@Api(value = "UserInfo API", tags = {"UserInfo"})
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/userinfo")
public class UserInfoController {
	
	private final UserInfoService userInfoService;

	@ApiOperation(value = "플로깅 목록 조회")
    @GetMapping("/plogging")
    public ResponseEntity<? extends BaseResponseDTO> getPloggingList(@ApiParam(value = "페이지 번호") @RequestParam(defaultValue = "1") int page) {

        PloggingGetListWrapperResponseDTO ploggingGetListWrapperResponseDTO = null;

        try {
        	ploggingGetListWrapperResponseDTO = userInfoService.getPloggingList(page);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(BaseResponseDTO.of(e.getMessage(), 404));
        } catch (Exception e) {
            return ResponseEntity.status(409).body(BaseResponseDTO.of("글 목록 조회에 실패했습니다.", 409));
        }

        return ResponseEntity.ok(PloggingGetListWrapperResponseDTO.of("글 목록 조회에 성공했습니다.", 200, ploggingGetListWrapperResponseDTO));

    }
    
    @ApiOperation(value = "방문한 산 목록 조회")
    @GetMapping("/mountain")
    public ResponseEntity<? extends BaseResponseDTO> getMountainList(@ApiParam(value = "페이지 번호") @RequestParam(defaultValue = "1") int page) {

    	 MountainUserInfoWrapperResponseDTO mountainUserInfoWrapperResponseDTO;

        try {
        	mountainUserInfoWrapperResponseDTO = userInfoService.getMountainList(page);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).body(BaseResponseDTO.of(e.getMessage(), 409));
        } catch (Exception e) {
            return ResponseEntity.status(409).body(BaseResponseDTO.of("산 목록 조회에 실패했습니다.", 409));
        }
        return ResponseEntity.status(200).body(MountainUserInfoWrapperResponseDTO.of("산 목록 조회에 성공했습니다.", 200, mountainUserInfoWrapperResponseDTO));
    }
    
    @ApiOperation(value = "산별 플로깅 목록 조회")
    @GetMapping("/{mountainCode}")
    public ResponseEntity<? extends BaseResponseDTO> getPloggingInMountain(@ApiParam(value = "산 코드", required = true) @PathVariable @NotBlank String mountainCode) {

    	PloggingGetListWrapperResponseDTO ploggingGetListWrapperResponseDTO = null;
    	
        try {
        	ploggingGetListWrapperResponseDTO = userInfoService.getPloggingInMountain(mountainCode);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(BaseResponseDTO.of(e.getMessage(), 404));
        } catch (Exception e) {
            return ResponseEntity.status(409).body(BaseResponseDTO.of("플로깅 목록 조회에 실패했습니다.", 409));
        }
        return ResponseEntity.status(200).body(PloggingGetListWrapperResponseDTO.of("플로깅 목록 조회에 성공했습니다.", 200, ploggingGetListWrapperResponseDTO));
    }
    
    @ApiOperation(value = "유저 누적 플로깅 정보")
    @GetMapping
    public ResponseEntity<? extends BaseResponseDTO> getUserAccumulative() {

    	UserPloggingInfoDTO userPloggingInfoDTO = null;
    	
        try {
        	userPloggingInfoDTO = userInfoService.getUserAccumulative();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(BaseResponseDTO.of(e.getMessage(), 404));
        } catch (Exception e) {
            return ResponseEntity.status(409).body(BaseResponseDTO.of(e.getMessage(), 409));
        }
        return ResponseEntity.status(200).body(UserPloggingInfoDTO.of("유저 누적 플로깅 정보 조회에 성공했습니다.", 200,userPloggingInfoDTO));
    }
}
