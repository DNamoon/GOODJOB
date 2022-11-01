package com.goodjob.status.repository;

import com.goodjob.member.Member;
import com.goodjob.post.Post;
import com.goodjob.status.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 박채원 22.10.26 수정
 */

public interface StatusRepository extends JpaRepository<Status,Long> {

    Page<Status> getStatusByStatResumeId_ResumeMemId_MemLoginIdOrderByStatApplyDateDesc(String loginId, Pageable pageable);
    Page<Status> getStatusByStatPostId_PostComId_ComLoginIdOrderByStatApplyDateDesc(String loginId, Pageable pageable);
    @Transactional
    @Modifying
    @Query("update Status s set s.statPass='합격' where s.statId =:statId")
    void updateStatPass(Long statId);
    @Transactional
    @Modifying
    @Query("update Status s set s.statPass='불합격' where s.statId =:statId")
    void updateStatUnPass(Long statId);
}
