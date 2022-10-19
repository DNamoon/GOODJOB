package com.goodjob.notice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface NoticeService {

    Optional<Notice> findOne(Long noticeId);

    Page<Notice> findNoticeList(Pageable pageable);
    List<Notice> findAll();

    Notice insertNotice(Notice notice);

    void deleteNotice(Long id);
}
