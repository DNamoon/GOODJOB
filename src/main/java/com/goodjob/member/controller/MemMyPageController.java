package com.goodjob.member.controller;

import com.goodjob.member.Member;
import com.goodjob.member.memDTO.MemberDTO;
import com.goodjob.member.service.MemberService;
import com.goodjob.resume.dto.ResumeListDTO;
import com.goodjob.resume.service.ResumeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.naming.Binding;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;

import java.util.List;

/**
 * 김도현 22.10.12 작성
 * mypage 개인정보 수정 페이지
 * 박채원 22.10.23 수정 - 이력서관리로 이동하는 메소드 추가
 * **/
@Controller
@Slf4j
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemMyPageController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final ResumeService resumeService;

    @RequestMapping("/myPage")
    public String myPageForm(HttpSession session, Model model){
        String id = (String)session.getAttribute("sessionId");
        model.addAttribute("memberInfo",memberService.memInfo(id));
        return "member/myPageInfo";
    }

   //개인정보 수정 시 비밀번호 확인
    @ResponseBody
    @PostMapping("/checkPW")
    public String checkPW(@Param("pw")String pw, @Param("id")String loginId) {
        Optional<Member> mem = memberService.loginIdCheck(loginId);
        if (mem.isPresent()) {
            Member member = mem.get();
            if (passwordEncoder.matches(pw,member.getMemPw())) {
                return "0";
            }
        }
        return "1";
    }
    public String chch(String pw, String loginId) {
        Optional<Member> mem = memberService.loginIdCheck(loginId);
        if (mem.isPresent()) {
            Member member = mem.get();
            if (passwordEncoder.matches(pw,member.getMemPw())) {
                return "0";
            }
        }
        return "1";
    }

    @PostMapping("/myPageInfo")
    public String infoUpdate(@RequestParam("email") String memEmail,@Valid MemberDTO memberDTO, BindingResult result){
        if(memberService.checkEmail(memberDTO.getMemEmail1()+"@"+memberDTO.getMemEmail2()) != "false"){
            result.rejectValue("","emailInCorrect",
                    "이미 가입된 이메일입니다.");
            return "member/signup";
        }
        memberService.updateMemInfo(memberDTO,memEmail);
        return "redirect:/member/myPage";
    }

    //회원탈퇴 (비밀번호 확인 후 회원 정보 삭제,이력서 정보 같이 삭제)
    @ResponseBody
    @RequestMapping("/delete")
    public String deleteMember(@Param("deletePw")String deletePw, @Param("id")String loginId, @Param("memId")Long memId,HttpSession session) {
        Optional<Member> mem = memberService.loginIdCheck(loginId);
        if (mem.isPresent()) {
            Member member = mem.get();
            if (passwordEncoder.matches(deletePw,member.getMemPw())) {
                memberService.deleteById(memId);
                session.invalidate();
                return "0";
            }
        }
        return "1";
    }
    // 비밀번호 변경 전 기존 비밀번호 확인
    @PostMapping("/changePw")
    @ResponseBody
    public String changePwCheck(@RequestParam("checkPw")String checkPw, @RequestParam("id")String loginId){
        System.out.println("pw"+checkPw+"////id"+loginId);
        Optional<Member> mem = memberService.loginIdCheck(loginId);
        if (mem.isPresent()) {
            Member member = mem.get();
            if (passwordEncoder.matches(checkPw,member.getMemPw())){
                return "0";
            }
        }
        return "1";
//        return chch(checkPw,loginId);
    }

    // 비밀번호 변경
    @GetMapping("/changePassword")
    public String changePwForm(MemberDTO memberDTO){
//        memberDTO.setMemId(memberService.memInfo(loginId).getMemId());
        return "member/memChangePassword";
    }

    @RequestMapping(value="/changePassword",method = RequestMethod.POST)
    public String changePw(@Valid MemberDTO memberDTO, BindingResult result, HttpSession session,Model model) {
        if(result.hasErrors()){
            return "member/signup";
        }
        String id = (String)session.getAttribute("sessionId");
        MemberDTO mem = memberService.memInfo(id);
        model.addAttribute("changePwMem",mem.getMemId());
        //비밀번호 일치하지 않을 시 에러 발생
        if(!memberDTO.getPw().equals(memberDTO.getPw2())){
            result.rejectValue("","passwordInCorrect",
                    "입력하신 비밀번호가 일치하지 않습니다.");
            return "member/memChangePassword";
        }
                System.out.println("chchchch"+memberDTO.getPw2());
        memberService.changePassword(memberDTO.getPw2(),mem.getMemId());
        model.addAttribute("memberInfo",mem);
        return "member/myPageInfo";
    }

    @GetMapping("/myPageResume")
    public String myPageResume(){
        return "/member/myPageResumeList";
    }

    //박채원 - restful api 사용해서 리스트 뿌리는 거 해보려고 작성한 메소드
    @ResponseBody
    @GetMapping(value = "/getResumeList", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ResumeListDTO>> getResumeList(HttpSession session){
        return new ResponseEntity<>(resumeService.getResumeList((String) session.getAttribute("sessionId")), HttpStatus.OK);
    }

    @GetMapping("/myPageApply")
    public String myPageApply(){
        return "/member/myPageApply";
    }
}
