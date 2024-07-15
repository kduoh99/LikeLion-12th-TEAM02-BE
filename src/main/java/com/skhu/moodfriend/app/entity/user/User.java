package com.skhu.moodfriend.app.entity.user;

import com.skhu.moodfriend.app.entity.diary.Diary;
import com.skhu.moodfriend.app.entity.diary_ai.DiaryAI;
import com.skhu.moodfriend.app.entity.emotion_tracker.EmotionTracker;
import com.skhu.moodfriend.app.entity.feedback.FeedBack;
import com.skhu.moodfriend.app.entity.friend.Friend;
import com.skhu.moodfriend.app.entity.user_object.UserObject;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "USER_EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "USER_NAME", length = 10, nullable = false)
    private String name;

    @Column(name = "MILEAGE", nullable = false)
    private long mileage;

    @Enumerated(EnumType.STRING)
    @Column(name = "EMOTION_TYPE", nullable = false)
    private EmotionType emotionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "LOGIN_TYPE", nullable = false)
    private LoginType loginType;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE_TYPE", nullable = false)
    private RoleType roleType;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Diary> diaries = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiaryAI> diariesAI = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmotionTracker> emotionTrackers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserObject> userObjects = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Friend> friends = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedBack> feedBacks = new ArrayList<>();
}
