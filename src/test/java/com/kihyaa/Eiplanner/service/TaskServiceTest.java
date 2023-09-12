package com.kihyaa.Eiplanner.service;

import com.kihyaa.Eiplanner.domain.Member;
import com.kihyaa.Eiplanner.repository.MemberRepository;
import com.kihyaa.Eiplanner.repository.SettingRepository;
import com.kihyaa.Eiplanner.repository.TaskRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskServiceTest {
//>>>>>>>95005f0103384b486166657ae8b05f1dded26ea9
//
//    @Test
//
//    void t1() {
//        LocalDate endDate = LocalDate.of(2023, 9, 12);
//        LocalTime endTime = LocalTime.of(13, 0, 0);
//
//        LocalDate localDate = LocalDate.now();
//        LocalTime localTime = LocalTime.now();
//
//<<<<<<<HEAD
//        @Autowired
//        private MemberRepository memberRepository;
//
//        @Autowired
//        private TaskRepository taskRepository;
//        @Autowired
//        private SettingRepository settingRepository;
//
//        private Member member;
//
//        //member.getId(), "제목", "설명", null, null);
//        private String title = "제목1";
//        private String description = "설명2";
//
//        @BeforeAll
//        void setUp () {
//            Setting setting = new Setting();
//
//            settingRepository.save(setting);
//
//            member = Member.builder()
//                    .email("asdf")
//                    .nickname("닉네임")
//                    .password("1234")
//                    .setting(setting)
//                    .build();
//
//            memberRepository.save(member);
//        }
//
////  @DisplayName("일정 등록")
////  @Transactional
////  @Order(1)
////  @Test
////  void makeTask() {
////    MakeTaskRequest makeTaskRequest = new MakeTaskRequest(title, description, null, null);
////    MakeTaskRequest makeTaskRequest2 = new MakeTaskRequest(title, description, null, null);
////    MakeTaskRequest makeTaskRequest3 = new MakeTaskRequest(title, description, null, null);
////    Long taskId = taskService.makeTask(makeTaskRequest, member);
////    Long taskId2 = taskService.makeTask(makeTaskRequest2, member);
////    Long taskId3 = taskService.makeTask(makeTaskRequest3, member);
////
////    Task task = taskRepository.findById(taskId).orElseThrow(() -> new NoSuchElementException());
////
////    assertEquals(title, task.getTitle());
////    assertEquals(member.getId(), task.getMember().getId());
////    assertEquals(description, task.getDescription());
////    assertEquals(null, task.getEndDate());
////    assertEquals(null, task.getEndTime());
////    assertEquals(EIType.PENDING, task.getEiType());
////    assertEquals(null, task.getCompletedAt());
////    assertEquals(false, task.getIsCompleted());
////
////    Task task2 = taskRepository.findById(taskId2).orElseThrow(() -> new NoSuchElementException());
////    //연결고리 확인
////    assertEquals(taskId2, task.getNext().getId());
////    assertEquals(null, task.getPrev());
////    assertEquals(taskId, task2.getPrev().getId());
////    assertEquals(taskId3, task2.getNext().getId());
////  }
//
//        @DisplayName("일정 같은 타입으로 이동: 가장 상위")
//        @Transactional
//        @Order(2)
//        @Test
//        void moveTaskFirstPosition () {
//
//        }
//
//        //일정 같은 타입으로 이동: 가장 하위
//        @DisplayName("일정 같은 타입으로 이동: 가장 하위")
//        @Transactional
//        @Order(2)
//        @Test
//        void moveTaskLastPosition () {
//
//        }
//
//        //일정 같은 타입으로 이동: 중간으로
//
//        //일정 다른 타입으로 이동: 가장 상위
//
//        //일정 다른 타입으로 이동: 가장 하위
//
//        //일정 다른 타입으로 이동: 중간으로
//=======
//        System.out.println("endDate = " + endDate);
//        System.out.println("endTime = " + endTime);
//>>>>>>>95005f 0103384 b486166657ae8b05f1dded26ea9
//
//
//        System.out.println("localDate = " + localDate);
//        System.out.println("localDate = " + localTime);
//
//        long tempDay = ChronoUnit.DAYS.between(localDate, endDate);
//        long tempTime = ChronoUnit.HOURS.between(endTime, localTime);
//
//        System.out.println("tempDay = " + tempDay * 24);
//        System.out.println("tempTime = " + tempTime);
//
//    }
//}
}