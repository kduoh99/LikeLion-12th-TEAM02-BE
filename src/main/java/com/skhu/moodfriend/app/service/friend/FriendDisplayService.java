package com.skhu.moodfriend.app.service.friend;

import com.skhu.moodfriend.app.dto.friend.reqDto.FriendReqDto;
import com.skhu.moodfriend.app.dto.friend.resDto.FriendResDto;
import com.skhu.moodfriend.app.domain.friend.Friend;
import com.skhu.moodfriend.app.domain.friend.Status;
import com.skhu.moodfriend.app.domain.member.Member;
import com.skhu.moodfriend.app.domain.tracker.diary.Diary;
import com.skhu.moodfriend.app.domain.tracker.diary.EmotionType;
import com.skhu.moodfriend.app.repository.FriendRepository;
import com.skhu.moodfriend.app.repository.MemberRepository;
import com.skhu.moodfriend.global.exception.CustomException;
import com.skhu.moodfriend.global.exception.code.ErrorCode;
import com.skhu.moodfriend.global.exception.code.SuccessCode;
import com.skhu.moodfriend.global.template.ApiResponseTemplate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendDisplayService {

    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;

    public ApiResponseTemplate<List<FriendReqDto>> getFriendRequests(Principal principal) {
        Long memberId = Long.parseLong(principal.getName());
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER_EXCEPTION, ErrorCode.NOT_FOUND_MEMBER_EXCEPTION.getMessage()));

        List<Friend> friendRequests = friendRepository.findByRequesterAndStatus(member, Status.WAITING);

        List<FriendReqDto> friendRequestList = friendRequests.stream()
                .map(friend -> new FriendReqDto(friend.getMember().getEmail()))
                .collect(Collectors.toList());

        return ApiResponseTemplate.success(SuccessCode.GET_FRIENDS_REQUEST_SUCCESS, friendRequestList);
    }

    public ApiResponseTemplate<List<FriendResDto>> getFriends(Principal principal) {
        Long memberId = Long.parseLong(principal.getName());
        Member currentMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER_EXCEPTION, ErrorCode.NOT_FOUND_MEMBER_EXCEPTION.getMessage()));

        List<Friend> friends = friendRepository.findByRequesterAndStatus(currentMember, Status.ACCEPTED);

        List<FriendResDto> friendDtos = friends.stream()
                .map(friend -> {
                    EmotionType emotionType = friend.getMember().getDiaries().stream()
                            .max(Comparator.comparing(Diary::getCreatedAt))
                            .map(Diary::getEmotionType)
                            .orElse(EmotionType.SO_SO);

                    return FriendResDto.of(friend, emotionType);
                })
                .collect(Collectors.toList());

        return ApiResponseTemplate.success(SuccessCode.GET_FRIENDS_SUCCESS, friendDtos);
    }

    public ApiResponseTemplate<FriendResDto> getFriend(
            Principal principal,
            String friendEmail) {

        Long memberId = Long.parseLong(principal.getName());
        Member currentMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER_EXCEPTION, ErrorCode.NOT_FOUND_MEMBER_EXCEPTION.getMessage()));

        Member friendMember = memberRepository.findByEmail(friendEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_EMAIL_EXCEPTION, ErrorCode.NOT_FOUND_EMAIL_EXCEPTION.getMessage()));

        Friend friend = friendRepository.findByRequesterAndMemberAndStatus(currentMember, friendMember, Status.ACCEPTED)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_FRIEND_REQUEST_EXCEPTION, ErrorCode.NOT_FOUND_FRIEND_REQUEST_EXCEPTION.getMessage()));

        EmotionType emotionType = friendMember.getDiaries().stream()
                .max(Comparator.comparing(Diary::getCreatedAt))
                .map(Diary::getEmotionType)
                .orElse(EmotionType.SO_SO);

        FriendResDto friendDto = FriendResDto.of(friend, emotionType);

        return ApiResponseTemplate.success(SuccessCode.GET_FRIEND_SUCCESS, friendDto);
    }
}
