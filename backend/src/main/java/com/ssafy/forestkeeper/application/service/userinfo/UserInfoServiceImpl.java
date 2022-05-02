package com.ssafy.forestkeeper.application.service.userinfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ssafy.forestkeeper.application.dto.response.plogging.PloggingListResponseDTO;
import com.ssafy.forestkeeper.application.dto.response.plogging.PloggingListWrapperResponseDTO;
import com.ssafy.forestkeeper.domain.dao.plogging.Plogging;
import com.ssafy.forestkeeper.domain.repository.mountain.MountainRepository;
import com.ssafy.forestkeeper.domain.repository.plogging.PloggingRepository;
import com.ssafy.forestkeeper.domain.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService{

    private final PloggingRepository ploggingRepository;

    private final UserRepository userRepository;

	private final MountainRepository mountainRepository;

	@Override
	public PloggingListWrapperResponseDTO getPloggingList(int page) {
        List<Plogging> ploggingList = ploggingRepository.findByUserId(userRepository.findByEmailAndDelete(SecurityContextHolder.getContext().getAuthentication().getName(),false).get().getId(),PageRequest.of(page - 1, 10))
                .orElseThrow(() -> new IllegalArgumentException("글을 찾을 수 없습니다."));

    	List<PloggingListResponseDTO> ploggingListResponseDTOList = new ArrayList<>();

    	ploggingList.forEach(plogging ->
    		ploggingListResponseDTOList.add(
                        PloggingListResponseDTO.builder()
                        		.date(plogging.getStartTime().toLocalDate().toString())
                        		.ploggingId(plogging.getId())
                        		.distance(plogging.getDistance())
                        		.time(plogging.getDurationTime())
                        		.exp(plogging.getExp())
                        		.mountainName(plogging.getMountain().getName())
                                .build()
                )
        );

        return PloggingListWrapperResponseDTO.builder()
                .list(ploggingListResponseDTOList)
                .build();
	}

	@Override
	public Optional<List<String>> getMountainList(int page) {
		Set<String> set = new HashSet<>();
        List<Plogging> ploggingList = ploggingRepository.findByUserId(userRepository.findByEmailAndDelete(SecurityContextHolder.getContext().getAuthentication().getName(),false).get().getId())
                .orElseThrow(() -> new IllegalArgumentException("플로깅 기록을 찾을 수 없습니다."));
        ploggingList.forEach(plogging ->
        	set.add(plogging.getMountain().getName())
        );
        List<String> list = new ArrayList<>();
        set.forEach(mntn ->
        	list.add(mntn)
        );
        Optional<List<String>> MountainNameList = Optional.ofNullable(list);
        
	    return MountainNameList;
	}
	
	@Override
	public PloggingListWrapperResponseDTO getPloggingInMountain(String mountainName) {
        List<Plogging> ploggingList = ploggingRepository.findByUserIdAndMountainId(userRepository.findByEmailAndDelete(SecurityContextHolder.getContext().getAuthentication().getName(),false).get().getId(),mountainRepository.findByName(mountainName).getId())
                .orElseThrow(() -> new IllegalArgumentException("글을 찾을 수 없습니다."));

    	List<PloggingListResponseDTO> ploggingListResponseDTOList = new ArrayList<>();

    	ploggingList.forEach(plogging ->
    		ploggingListResponseDTOList.add(
                        PloggingListResponseDTO.builder()
                        		.date(plogging.getStartTime().toLocalDate().toString())
                        		.ploggingId(plogging.getId())
                        		.distance(plogging.getDistance())
                        		.time(plogging.getDurationTime())
                        		.exp(plogging.getExp())
                        		.mountainName(plogging.getMountain().getName())
                                .build()
                )
        );

        return PloggingListWrapperResponseDTO.builder()
                .list(ploggingListResponseDTOList)
                .build();
	}
}