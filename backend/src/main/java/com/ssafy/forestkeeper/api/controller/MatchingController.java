package com.ssafy.forestkeeper.api.controller;

import com.ssafy.forestkeeper.application.dto.request.matching.MatchingModifyRequestDTO;
import com.ssafy.forestkeeper.application.dto.request.matching.MatchingRegisterRequestDTO;
import com.ssafy.forestkeeper.application.dto.request.matching.MatchingRequestDTO;
import com.ssafy.forestkeeper.application.dto.response.BaseResponseDTO;
import com.ssafy.forestkeeper.application.dto.response.matching.MatchingGetListWrapperResponseDTO;
import com.ssafy.forestkeeper.application.dto.response.matching.MatchingResponseDTO;
import com.ssafy.forestkeeper.application.service.matching.MatchingService;
import com.ssafy.forestkeeper.application.service.matching.MatchingUserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Api(value = "Matching API", tags = {"Mathcing"})
@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/match")
public class MatchingController {

    private final MatchingService matchingService;
    private final MatchingUserService matchingUserService;

    @ApiOperation(value = "매칭 글 등록")
    @ApiResponses({
            @ApiResponse(code = 201, message = "글 작성에 성공했습니다."),
            @ApiResponse(code = 400, message = "입력된 정보가 유효하지 않습니다."),
            @ApiResponse(code = 404, message = "글 작성에 필요한 정보를 찾을 수 없습니다."),
            @ApiResponse(code = 409, message = "글 작성에 실패했습니다."),
    })
    @PostMapping
    public ResponseEntity<? extends BaseResponseDTO> register(
            @ApiParam(value = "매칭 글 등록", required = true) @RequestBody @Valid MatchingRegisterRequestDTO matchingRegisterRequestDTO
    ) {

        matchingService.registerMatching(matchingRegisterRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponseDTO.of("글 작성에 성공했습니다.", 201));

    }

    @ApiOperation(value = "매칭 글 수정")
    @ApiResponses({
            @ApiResponse(code = 201, message = "글 수정에 성공했습니다."),
            @ApiResponse(code = 400, message = "입력된 정보가 유효하지 않습니다."),
            @ApiResponse(code = 404, message = "글 수정에 필요한 정보를 찾을 수 없습니다."),
            @ApiResponse(code = 409, message = "글 수정에 실패했습니다."),
    })
    @PatchMapping
    public ResponseEntity<? extends BaseResponseDTO> modify(
            @ApiParam(value = "매칭 글 수정", required = true) @RequestBody @Valid MatchingModifyRequestDTO matchingModifyRequestDTO
    ) {

        if (matchingModifyRequestDTO.getTotal() < matchingUserService.getParticipants(matchingModifyRequestDTO.getMatchingId()).size()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(BaseResponseDTO.of("총 인원이 참여 인원보다 적습니다.", 409));
        }

        matchingService.modifyMatching(matchingModifyRequestDTO);

        return ResponseEntity.ok(BaseResponseDTO.of("글 수정에 성공했습니다.", 200));

    }

    @ApiOperation(value = "매칭 합류")
    @ApiResponses({
            @ApiResponse(code = 200, message = "매칭 합류에 성공했습니다."),
            @ApiResponse(code = 400, message = "입력된 정보가 유효하지 않습니다."),
            @ApiResponse(code = 404, message = "필요한 정보를 찾을 수 없습니다."),
            @ApiResponse(code = 409, message = "매칭 합류에 실패했습니다."),
    })
    @PostMapping("/join")
    public ResponseEntity<? extends BaseResponseDTO> joinMatching(
            @ApiParam(value = "매칭 정보", required = true) @Valid @RequestBody MatchingRequestDTO matchingRequestDTO
    ) {

        String matchingId = matchingRequestDTO.getMatchingId();

        if (matchingService.isFull(matchingId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(BaseResponseDTO.of("이미 가득 찬 매칭입니다.", 409));
        }

        if (matchingService.isClose(matchingId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(BaseResponseDTO.of("이미 마감된 매칭입니다.", 409));
        }

        if (matchingService.isDelete(matchingId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(BaseResponseDTO.of("삭제된 매칭입니다.", 409));
        }

        if (matchingUserService.isJoin(matchingId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(BaseResponseDTO.of("이미 참여중인 매칭입니다.", 409));
        }

        matchingUserService.joinMatching(matchingRequestDTO.getMatchingId());

        return ResponseEntity.ok(BaseResponseDTO.of("매칭 합류에 성공했습니다.", 200));

    }

    @ApiOperation(value = "매칭 마감")
    @ApiResponses({
            @ApiResponse(code = 200, message = "매칭 마감에 성공했습니다."),
            @ApiResponse(code = 400, message = "입력된 정보가 유효하지 않습니다."),
            @ApiResponse(code = 404, message = "필요한 정보를 찾을 수 없습니다."),
            @ApiResponse(code = 409, message = "매칭 마감에 실패했습니다."),
    })
    @PatchMapping("/close")
    public ResponseEntity<? extends BaseResponseDTO> closeMatching(
            @ApiParam(value = "매칭 정보", required = true) @Valid @RequestBody MatchingRequestDTO matchingRequestDTO
    ) {

        matchingService.closeMatching(matchingRequestDTO.getMatchingId());

        return ResponseEntity.ok(BaseResponseDTO.of("매칭 마감에 성공했습니다.", 200));

    }


    @ApiOperation(value = "매칭 목록 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "매칭 목록 조회에 성공했습니다."),
            @ApiResponse(code = 404, message = "매칭 목록을 찾을 수 없습니다."),
            @ApiResponse(code = 409, message = "매칭 목록 조회에 실패했습니다.")
    })
    @GetMapping
    public ResponseEntity<? extends BaseResponseDTO> getMatchingList(
            @RequestParam("mountainCode") String mountainCode,
            @ApiParam(value = "페이지 번호") @RequestParam(defaultValue = "1") int page
    ) {

        return ResponseEntity.ok(
                MatchingGetListWrapperResponseDTO.of("매칭 목록 조회에 성공했습니다.", 200,
                        matchingService.getMatchingList(mountainCode, page))
        );

    }

    @ApiOperation(value = "내 매칭 목록 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "매칭 목록 조회에 성공했습니다."),
            @ApiResponse(code = 404, message = "매칭 목록을 찾을 수 없습니다."),
            @ApiResponse(code = 409, message = "매칭 목록 조회에 실패했습니다.")
    })
    @GetMapping("/my")
    public ResponseEntity<? extends BaseResponseDTO> getMyMatchingList(
            @ApiParam(value = "페이지 번호") @RequestParam(defaultValue = "1") int page
    ) {

        return ResponseEntity.ok(
                MatchingGetListWrapperResponseDTO.of("매칭 목록 조회에 성공했습니다.", 200,
                        matchingService.getMyMatching(page))
        );

    }

    @ApiOperation(value = "매칭 글 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "매칭 글 조회에 성공했습니다."),
            @ApiResponse(code = 404, message = "매칭 글을 찾을 수 없습니다."),
            @ApiResponse(code = 409, message = "매칭 글 조회에 실패했습니다.")
    })
    @GetMapping("/{matchingId}")
    public ResponseEntity<? extends BaseResponseDTO> getMatching(
            @ApiParam(value = "페이지 번호") @PathVariable @NotBlank String matchingId
    ) {

        return ResponseEntity.ok(MatchingResponseDTO.of("매칭 글 조회에 성공했습니다.", 200, matchingService.getMatching(matchingId)));

    }


    @ApiOperation(value = "매칭 글 삭제")
    @ApiResponses({
            @ApiResponse(code = 200, message = "매칭 글 조회에 성공했습니다."),
            @ApiResponse(code = 404, message = "매칭 글을 찾을 수 없습니다."),
            @ApiResponse(code = 409, message = "매칭 글 조회에 실패했습니다.")
    })
    @DeleteMapping("/{matchingId}")
    public ResponseEntity<? extends BaseResponseDTO> deleteMatching(
            @ApiParam(value = "페이지 번호") @PathVariable @NotBlank String matchingId
    ) {

        matchingService.deleteMatching(matchingId);

        return ResponseEntity.ok(BaseResponseDTO.of("매칭 글 삭제에 성공했습니다.", 200));

    }

    @ApiOperation(value = "매칭 참여 취소")
    @ApiResponses({
            @ApiResponse(code = 200, message = "매칭 참여 취소에 성공했습니다."),
            @ApiResponse(code = 404, message = "매칭 글을 찾을 수 없습니다."),
            @ApiResponse(code = 409, message = "매칭 글 조회에 실패했습니다.")
    })
    @DeleteMapping("/cancel/{matchingId}")
    public ResponseEntity<? extends BaseResponseDTO> cancelMatching(
            @ApiParam(value = "페이지 번호") @PathVariable @NotBlank String matchingId
    ) {

        matchingUserService.cancelMatching(matchingId);

        return ResponseEntity.ok(BaseResponseDTO.of("매칭 참여 취소에 성공했습니다.", 200));

    }

}
