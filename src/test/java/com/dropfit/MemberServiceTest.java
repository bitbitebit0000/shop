package com.dropfit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.dropfit.domain.Member;
import com.dropfit.repository.MemberRepository;
import com.dropfit.service.MemberService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    void memberJoinSuccess() {
        Member member = new Member();
        member.setEmail("user1@test.com");
        member.setName("John Doe");

        Long savedId = memberService.join(member);

        Member foundMember = memberRepository.findMember(savedId);
        assertThat(foundMember).isNotNull();
        assertThat(foundMember.getName()).isEqualTo(member.getName());
    }

    @Test
    void duplicateMemberException() {
        Member member1 = new Member();
        member1.setEmail("user1@test.com");
        member1.setName("John Doe");

        Member member2 = new Member();
        member2.setEmail("user1@test.com");
        member2.setName("Jane Smith");

        memberService.join(member1);

        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                memberService.join(member2);
            }
        });
    }
}