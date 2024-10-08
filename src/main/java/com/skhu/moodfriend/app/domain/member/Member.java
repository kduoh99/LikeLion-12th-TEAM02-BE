package com.skhu.moodfriend.app.domain.member;

import com.skhu.moodfriend.app.domain.member.attendance.Attendance;
import com.skhu.moodfriend.app.domain.friend.Friend;
import com.skhu.moodfriend.app.domain.member.object.MemberObject;
import com.skhu.moodfriend.app.domain.payment.Order;
import com.skhu.moodfriend.app.domain.tracker.conversation.Conversation;
import com.skhu.moodfriend.app.domain.tracker.diary.Diary;
import com.skhu.moodfriend.app.domain.tracker.diary_ai.DiaryAI;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long memberId;

    @Column(name = "MEMBER_EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "MEMBER_NAME", length = 10, nullable = false)
    private String name;

    @Column(name = "MILEAGE", nullable = false)
    private int mileage;

    @Enumerated(EnumType.STRING)
    @Column(name = "LOGIN_TYPE", nullable = false)
    private LoginType loginType;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE_TYPE", nullable = false)
    private RoleType roleType;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attendance> attendances = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberObject> memberObjects = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Friend> friends = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Diary> diaries = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiaryAI> diaryAIS = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Conversation> conversations = new ArrayList<>();

    @Builder
    private Member(String email, String password, String name, int mileage, LoginType loginType, RoleType roleType) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.mileage = mileage;
        this.loginType = loginType;
        this.roleType = roleType;
    }

    public void updateInfo(String name) {
        this.name = name;
    }

    public void updateMileage(int mileageIncrement) {
        this.mileage += mileageIncrement;
    }
}
