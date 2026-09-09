package shopping.shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shopping.shop.domain.item.Cap;
import shopping.shop.domain.item.Clothing;
import shopping.shop.domain.item.Item;
import shopping.shop.domain.item.ItemOption;

import java.util.List;

@Component
@Transactional
@RequiredArgsConstructor
public class InitService {

    private final ItemService itemService;

    public void dbInit() {
        List<Item> findItems = itemService.findItems();
        if (!findItems.isEmpty()) return;

        // DropFit 의류 & 모자/신발 이미지 URL
        String hoodieImg = "https://images.unsplash.com/photo-1556905055-8f358a7a47b2?auto=format&fit=crop&w=800&q=80";
        String sneakersImg = "https://images.unsplash.com/photo-1552346154-21d32810aba3?auto=format&fit=crop&w=800&q=80";
        String tShirtImg = "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?auto=format&fit=crop&w=800&q=80";
        String capImg = "https://images.unsplash.com/photo-1588850561407-ed78c282e89b?auto=format&fit=crop&w=800&q=80";

        // 1. [Clothing] 한정판 오버핏 후드티
        Clothing hoodie = new Clothing(
                "[LIMITED] 헤비웨이트 오버핏 후드티",
                59000,// 총재고는 0으로 두고 아래 연관관계 편의메서드로 세팅
                hoodieImg,
                "면 100% (700g 헤비웨이트)",
                "오버핏"
        );
        // 💡 사이즈 옵션 추가
        hoodie.addOption(new ItemOption("S", 5));
        hoodie.addOption(new ItemOption("M", 3));
        hoodie.addOption(new ItemOption("L", 4)); // 총 12개
        hoodie.addOption(new ItemOption("XL", 0)); // 품절 테스트용


        // 2. [Clothing] 레트로 하이탑 스니커즈
        Clothing sneakers = new Clothing(
                "[DROP] 레트로 하이탑 스니커즈",
                149000,
                sneakersImg,
                "천연 가죽 & 고무창",
                "정사이즈"
        );
        sneakers.addOption(new ItemOption("250", 2));
        sneakers.addOption(new ItemOption("260", 3));
        sneakers.addOption(new ItemOption("270", 0)); // 품절


        // 3. [Clothing] 시그니처 로고 티셔츠
        Clothing tShirt = new Clothing(
                "시그니처 로고 피그먼트 티셔츠",
                35000,
                tShirtImg,
                "피그먼트 워싱 면",
                "세미오버핏"
        );
        tShirt.addOption(new ItemOption("M", 50));
        tShirt.addOption(new ItemOption("L", 50));


        // 4. [Cap] 워시드 볼캡
        Cap cap = new Cap(
                "워시드 볼캡 - 블랙",
                29000,
                capImg,
                "스트랩 버클",
                "볼캡"
        );
        cap.addOption(new ItemOption("FREE", 50));


        // 영속화 (CascadeType.ALL에 의해 ItemOption도 함께 DB에 save됨)
        itemService.saveItem(hoodie);
        itemService.saveItem(sneakers);
        itemService.saveItem(tShirt);
        itemService.saveItem(cap);
    }
}