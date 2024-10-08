package com.skhu.moodfriend.app.dto.tracker.resDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.skhu.moodfriend.app.domain.tracker.diary.Diary;
import com.skhu.moodfriend.app.domain.tracker.diary.EmotionType;
import com.skhu.moodfriend.app.domain.tracker.diary.WeatherType;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record DiaryResDto(
        Long diaryId,
        EmotionType emotionType,
        WeatherType weatherType,
        String title,
        String content,
        LocalDate createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd - HH:mm", timezone = "Asia/Seoul")
        LocalDateTime updatedAt
) {
    public static DiaryResDto of(Diary diary) {
        return DiaryResDto.builder()
                .diaryId(diary.getDiaryId())
                .emotionType(diary.getEmotionType())
                .weatherType(diary.getWeatherType())
                .title(diary.getTitle())
                .content(diary.getContent())
                .createdAt(diary.getCreatedAt())
                .updatedAt(diary.getUpdatedAt())
                .build();
    }
}
