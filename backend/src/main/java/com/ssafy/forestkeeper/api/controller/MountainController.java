package com.ssafy.forestkeeper.api.controller;

import com.ssafy.forestkeeper.application.dto.response.BaseResponseDTO;
import com.ssafy.forestkeeper.application.dto.response.mountain.*;
import com.ssafy.forestkeeper.application.service.mountain.MountainService;
import com.ssafy.forestkeeper.domain.dao.mountain.Mountain;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Api(value = "Mountain API", tags = {"Mountain"})
@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mountain")
public class MountainController {

    private final MountainService mountainService;

    @ApiOperation(value = "등산로")
    @GetMapping("/trail")
    public ResponseEntity<?> getMountainTrail(String mountainCode) {

        JSONObject trail;

        try {
            ClassPathResource cpr = new ClassPathResource("trail/" + mountainCode + ".json");

            byte[] bData = FileCopyUtils.copyToByteArray(cpr.getInputStream());
            String jsonTxt = new String(bData, StandardCharsets.UTF_8);

            trail = (JSONObject) new JSONParser().parse(jsonTxt);

        } catch (Exception e) {
            return ResponseEntity.status(404).body(BaseResponseDTO.of("데이터가 존재하지 않습니다.", 404));
        }

        return ResponseEntity.status(200)
                .body(MountainTrailResponseDTO.of("등산로 불러오기에 성공했습니다.", 200, trail));
    }

    @ApiOperation(value = "산 정보")
    @GetMapping("/{mountainCode}")
    public ResponseEntity<?> getMountainInfo(@PathVariable("mountainCode") String mountainCode) {

        return ResponseEntity.status(200).body(
                MountainResponseDTO.of("산 정보 불러오기에 성공했습니다.", 200, mountainService.getMountainInfo(mountainCode)));

    }

    @ApiOperation(value = "산 검색")
    @GetMapping("")
    public ResponseEntity<?> searchMountain(@RequestParam("keyword") String keyword,
                                            @RequestParam(required = false, value = "page") Integer page) {

        try {
            if (page == null || page < 1) {
                page = 1;
            }

            page -= 1;

            Optional<List<Mountain>> mountainList = mountainService.searchMountain(keyword, page);
            int total = mountainService.totalSearch(keyword);

            if (!mountainList.isPresent() || mountainList.get().size() == 0) {
                return ResponseEntity.status(404).body(BaseResponseDTO.of("데이터가 존재하지 않습니다.", 404));
            }

            return ResponseEntity.status(200).body(
                    MountainSearchResponseDTO.of("산 검색에 성공했습니다.", 200, mountainList.get(), total));
        } catch (Exception e) {
            System.err.println(e);
            return ResponseEntity.status(400).body(BaseResponseDTO.of("올바르지 않은 요청입니다.", 400));
        }
    }

    @ApiOperation(value = "산 랭킹")
    @GetMapping("/rank/{mountainCode}")
    public ResponseEntity<?> getRank(@PathVariable("mountainCode") String mountainCode,
                                     @RequestParam("by") String by) {

        try {
            MountainRankWrapperResponseDTO mountainRankWrapperResponseDTO = null;

            if ("distance".equals(by)) {
                mountainRankWrapperResponseDTO = mountainService.getMountainRankByDistance(
                        mountainCode);
            } else if ("count".equals(by)) {
                mountainRankWrapperResponseDTO = mountainService.getMountainRankByCount(
                        mountainCode);
            }

            return ResponseEntity.status(200).body(
                    MountainRankWrapperResponseDTO.of("산 랭킹 조회에 성공했습니다.", 200,
                            mountainRankWrapperResponseDTO));
        } catch (Exception e) {
            System.err.println(e);
            return ResponseEntity.status(400).body(BaseResponseDTO.of("올바르지 않은 요청입니다.", 400));
        }
    }

    @ApiOperation(value = "산 방문자수 랭킹")
    @GetMapping("/rank")
    public ResponseEntity<?> getVisiterRank() {

        MountainVisitorRankWrapperResponseDTO mountainVisitorRankWrapperResponseDTO = null;

        try {

            mountainVisitorRankWrapperResponseDTO = mountainService.getVisiterRank();

            return ResponseEntity.status(200).body(
                    MountainVisitorRankWrapperResponseDTO.of("산 랭킹 조회에 성공했습니다.", 200, mountainVisitorRankWrapperResponseDTO));
        } catch (Exception e) {
            System.err.println(e);
            return ResponseEntity.status(400).body(BaseResponseDTO.of("올바르지 않은 요청입니다.", 400));
        }
    }

    @ApiOperation(value = "산 추천")
    @GetMapping("/recommend")
    public ResponseEntity<?> getRank(@RequestParam("by") String by,
                                     @RequestParam(name = "lat", required = false) Double lat,
                                     @RequestParam(name = "lng", required = false) Double lng) {

        try {
            MountainRecommendWrapperResponseDTO mountainRecommendWrapperResponseDTO = null;

            if ("distance".equals(by)) {
                mountainRecommendWrapperResponseDTO = mountainService.getRecommendByDistance(lat, lng);
            } else if ("height".equals(by)) {
                mountainRecommendWrapperResponseDTO = mountainService.getRecommendByHeight();
            }

            return ResponseEntity.status(200).body(
                    MountainRecommendWrapperResponseDTO.of("산 랭킹 조회에 성공했습니다.", 200,
                            mountainRecommendWrapperResponseDTO));
        } catch (Exception e) {
            System.err.println(e);
            return ResponseEntity.status(400).body(BaseResponseDTO.of("올바르지 않은 요청입니다.", 400));
        }
    }
}
