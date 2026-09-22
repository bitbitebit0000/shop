package com.dropfit.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.dropfit.domain.Member;
import com.dropfit.domain.Role;
import com.dropfit.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member);
        if (member.getRole() == null) {
            member.setRole(Role.USER);
        }
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByEmail(member.getEmail());
        if (!findMembers.isEmpty()) {
            throw new IllegalArgumentException("Already existing member.");
        }
    }

    public Member findMember(Long id) {
        return memberRepository.findMember(id);
    }

    public List<Member> findMembers() {
        return memberRepository.findAllMembers();
    }

    public Member login(String email, String password) {
        List<Member> members = memberRepository.findByEmail(email);
        if (!members.isEmpty()) {
            Member member = members.get(0);
            if (member.getPassword().equals(password)) {
                return member;
            }
        }
        return null;
    }
}