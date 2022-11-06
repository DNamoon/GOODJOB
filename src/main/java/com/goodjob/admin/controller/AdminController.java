package com.goodjob.admin.controller;

import com.goodjob.admin.Admin;
import com.goodjob.admin.AdminConst;
import com.goodjob.admin.admindto.AdminDTO;
import com.goodjob.admin.postpaging.AdminPostService;
import com.goodjob.admin.service.AdminService;
import com.goodjob.customerInquiry.service.CustomerInquiryService;
import com.goodjob.post.Post;
import com.goodjob.post.postdto.PostInsertDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * 관리자 컨트롤러 By.OH
 */
@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AdminPostService adminPostService;
    private final CustomerInquiryService customerInquiryService;

    @GetMapping
    public String adminHome(@SessionAttribute(name = AdminConst.ADMIN, required = false) Admin admin, Model model) {
        if (admin != null) {
            return "admin/adminHome";
        }
        return "redirect:login";
    }

    @GetMapping("/login")
    public String adminLoginForm(@ModelAttribute("adminDTO") AdminDTO adminDTO) {
        return "admin/adminLoginForm";
    }

    @PostMapping("/login")
    public String adminLogin(@Validated @ModelAttribute("adminDTO") AdminDTO adminDTO, BindingResult bindingResult, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return "/admin/adminLoginForm";
        }


        Admin loginAdmin = adminService.login(adminDTO.getAdLoginId(), adminDTO.getAdPw());

        if (loginAdmin == null) {
            bindingResult.reject("loginFail", "로그인에 실패하였습니다.");
            return "/admin/adminLoginForm";
        }

        HttpSession session = request.getSession();

        session.setAttribute(AdminConst.ADMIN, loginAdmin);
        session.setMaxInactiveInterval(60 * 100);
        return "redirect:/admin";
    }

    @GetMapping("/postManage/{pageNum}")
    public String postListForm(@PathVariable int pageNum, Model model) {
        pageNum = (pageNum == 0) ? 0 : (pageNum - 1);
        Sort sort = Sort.by("PostId").descending();
        Pageable pageable = PageRequest.of(pageNum, 10, sort);
        Page<Post> postList = adminPostService.findPostList(pageable);
        model.addAttribute("postPage", postList);
        return "/admin/managePage/adminPostManage";
    }

    @GetMapping("/memberManage")
    public String adminMemberPage() {
        return "/admin/managePage/adminMemberManage";
    }

    @GetMapping("/customerInquiry/{pageNum}")
    public String adminCustomerInquiryList(@PathVariable("pageNum") int pageNum, Model model,
                                           @RequestParam("sort") String sortType,
                                           @RequestParam(value = "category", required = false) String category) {
        if (sortType.equals("inquiryPostComId_comId")) {
            Pageable Pageable = PageRequest.of(pageNum - 1, 10, Sort.by("inquiryPostId").descending());
            model.addAttribute("inquiryPostList", customerInquiryService.findAllByMemberType(Pageable, "inquiryPostComId_comId"));
            model.addAttribute("sortType", sortType);
            return "admin/customerInquiry/customerInquiryList";
        }
        if (sortType.equals("inquiryPostMemberId_memId")) {
            Pageable Pageable = PageRequest.of(pageNum - 1, 10, Sort.by("inquiryPostId").descending());
            model.addAttribute("inquiryPostList", customerInquiryService.findAllByMemberType(Pageable, "inquiryPostMemberId_memId"));
            model.addAttribute("sortType", sortType);
            return "admin/customerInquiry/customerInquiryList";
        }
        if (category != null) {
            Pageable Pageable = PageRequest.of(pageNum - 1, 10, Sort.by(sortType).descending());
            model.addAttribute("inquiryPostList", customerInquiryService.findAllByCategory(Pageable, category));
            model.addAttribute("sortType", sortType);
            return "admin/customerInquiry/customerInquiryList";
        }
        Pageable Pageable = PageRequest.of(pageNum - 1, 10, Sort.by(sortType).descending());
        model.addAttribute("sortType", sortType);
        model.addAttribute("inquiryPostList", customerInquiryService.findAll(Pageable));
        return "admin/customerInquiry/customerInquiryList";
    }

    @GetMapping("/customerInquiry/{id}/detail")
    public String inquiryPostView(@PathVariable("id") Long id, Model model) {
        model.addAttribute("findInquiry", customerInquiryService.findOne(id).orElse(null));
        return "admin/customerInquiry/customerInquiryDetailView";
    }

    @PostMapping("/customerInquiry/{id}/detail")
    public String inquiryPostReply(@PathVariable("id") Long id, @RequestParam("requestURL") String requestURL,
                                   @RequestParam("inquiryPostAnswer") String inquiryPostAnswer) {
        customerInquiryService.updateInquiryPostWithAnswer(id,
                inquiryPostAnswer,
                AdminConst.ADMIN,
                LocalDateTime.now(),
                "1"
        );
        return "redirect:" + requestURL;
    }

    /**
     * 22.10.30 오성훈 이하 테스트메소드 차후 삭제예정.
     */
    @GetMapping("/test")
    public String test() {
        return "post/postDetailViewWithMap";
    }

    @PostMapping("/test")
    public String test2(@ModelAttribute PostInsertDTO post) {
        return "admin/adminHome";
    }

    @GetMapping("/test2")
    public String test3() {
        return "post/postDetailView";
    }
}
