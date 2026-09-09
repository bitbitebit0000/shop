package shopping.shop.domain;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shopping.shop.repository.MemberRepository;

@Component
@RequiredArgsConstructor
public class InitAdminData {

    private final InitService initService;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        initService.dbInit();
    }


    @Component
    @RequiredArgsConstructor
    @Transactional
    static class InitService {

        private final MemberRepository memberRepository;

        public void dbInit() {
            if (memberRepository.findByEmail("admin@shop.com").isEmpty()) {
                Member admin = new Member();
                admin.setEmail("admin@shop.com");
                admin.setPassword("admin123");
                admin.setName("관리자");
                admin.setRole(Role.ADMIN);

                memberRepository.save(admin);
            }
        }
    }
}