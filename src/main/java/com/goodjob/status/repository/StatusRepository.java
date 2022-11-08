package com.goodjob.status.repository;

import com.goodjob.status.Status;
import org.apache.catalina.LifecycleState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;


/**
 * 박채원 22.10.26 수정
 */

public interface StatusRepository extends JpaRepository<Status,Long> {

    Page<Status> getStatusByStatResumeId_ResumeMemId_MemLoginIdOrderByStatApplyDateDesc(String loginId, Pageable pageable);
    Page<Status> getStatusByStatPostId_PostComId_ComLoginIdAndStatPostId_PostIdOrderByStatApplyDateDesc(String loginId, Long postId, Pageable pageable);
    Page<Status> getStatusByStatPostId_PostComId_ComLoginIdAndAndStatPostId_PostIdAndAndStatPassIn(String loginId, Long postId, String[] statPass, Pageable pageable);
    @Transactional
    @Modifying
    @Query("update Status s set s.statPass =:result where s.statId =:statId")
    void updateStatPass(Long statId, String result);
    @Transactional
    @Modifying
    @Query("update Status s set s.statPass =:result where s.statId =:statId")
    void updateStatUnPass(Long statId, String result);
    @Query("select s from Status s where s.statId =:statId")
    Status findOneApplier(Long statId);
    boolean existsStatusByStatResumeId_ResumeMemId_MemLoginIdAndStatShowIsFalseAndStatPass(String loginId, String pass);
    int countStatusByStatResumeId_ResumeId(Long resumeId);
    int countStatusByStatResumeId_ResumeMemId_MemLoginIdAndStatPostId_PostId(String loginId, Long postId);
    int countStatusByStatResumeId_ResumeMemId_MemLoginIdAndStatPass(String loginId, String pass);
    int countStatusByStatResumeId_ResumeMemId_MemLoginId(String loginId);
    @Transactional
    @Modifying
    @Query("update Status s set s.statShow = true where s.statId =:statId")
    void changeStatShow(Long statId);
    @Query("select s.statId from Status s where s.statResumeId.resumeMemId.memLoginId =:loginId and s.statPass='서류합격' and s.statShow=false")
    List<Long> getStatId(String loginId);
    @Transactional
    @Modifying
    @Query("update Status s set s.statInterviewPlace =:interviewPlace, s.statInterviewDate =:interviewDate where s.statId =:statId")
    void updateInterviewInfo(Long statId, String interviewPlace, LocalDateTime interviewDate);
}
