package shopping.shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.shop.domain.Member;
import shopping.shop.repository.MemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member){
        List<Member> findMembers = memberRepository.findByEmail(member.getEmail());
        if(!findMembers.isEmpty()) {
            throw new IllegalArgumentException("이미 있는 회원입니다.");
        }
    }

    public Member findMember(Long id) {
        return memberRepository.findMember(id);
    }

    public List<Member> findMembers() {
        return memberRepository.findAllMembers();
    }

    public Member login (String email, String password) {
        List<Member> members = memberRepository.findByEmail(email);
        if(!members.isEmpty()) {
            Member member = members.get(0);
            if(member.getPassword().equals(password)) {
                return member;
            }
        }
        return null;
    }
}

/*
public Member login(String email, String password) {
    return memberRepository.findByEmail(email)
            .filter(m -> m.getPassword().equals(password)) // 비밀번호 맞으면 남김
            .orElse(null); // 회원이 없거나 비밀번호가 틀리면 null 반환
}
 */
