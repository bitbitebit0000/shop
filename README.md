🛒 DropFit

Spring Boot와 JPA를 기반으로 구현한 의류 쇼핑몰 백엔드 서비스입니다.
사용자 쇼핑 기능과 관리자 기능을 분리하고, 상품·회원·주문 도메인을 중심으로 확장 가능한 구조를 설계했습니다.

🌐 Service

Live Demo: http://dropfit.duckdns.org

Backend: Spring Boot

Database: PostgreSQL

Deployment: AWS EC2

Domain: DuckDNS

🛠 Tech Stack
Backend

Java 21

Spring Boot

Spring MVC

Spring Data JPA

Lombok

Thymeleaf

Gradle

Database

PostgreSQL 18.6

Deployment

AWS EC2

Ubuntu 26.04

DuckDNS

✨ 주요 기능
1. 👤 회원 기능

회원가입부터 로그인, 로그아웃, 마이페이지까지 기본적인 쇼핑몰 회원 기능을 구현했습니다.

회원가입

POST /signup

이메일

비밀번호

이름

주소

정보를 입력하여 회원가입할 수 있습니다.

회원가입 시 Member 엔티티와 Address 객체를 생성하여 회원 정보를 저장합니다.

로그인

POST /login

로그인 성공 시 HttpSession에 로그인한 회원 정보를 저장합니다.

HttpSession session = request.getSession();
session.setAttribute("loginMember", loginMember);


로그인에 실패하면 로그인 페이지에 오류 메시지를 전달합니다.

Invalid email or password.


또한 로그인 이전에 접근하려던 페이지가 있다면 redirectURL을 세션에 저장하여 로그인 후 해당 페이지로 이동하도록 구현했습니다.

로그아웃

POST /logout

로그아웃 시 현재 세션을 invalidate하여 로그인 정보를 제거합니다.

session.invalidate();

2. 🛍 상품 조회
상품 목록

GET /items

등록된 상품을 조회할 수 있습니다.

검색어가 존재하는 경우 상품명 또는 옵션 정보를 기준으로 검색하고, 검색어가 없는 경우 전체 상품을 조회합니다.

/items
/items?searchQuery=cap


Controller에서 검색어 유무에 따라 다른 Service 메서드를 호출하도록 구현했습니다.

if (searchQuery != null && !searchQuery.isBlank()) {
    items = itemService.findItemsWithOptionBySearch(searchQuery);
} else {
    items = itemService.findItemsWithOption();
}

🏠 메인 페이지

GET /

전체 상품을 조회하여 메인 화면에 전달합니다.

List<Item> items = itemService.findItems();
model.addAttribute("items", items);

3. 👤 마이페이지

GET /mypage

로그인한 회원의 정보를 조회하고 해당 회원의 주문 내역을 함께 제공합니다.

로그인하지 않은 사용자가 접근하면 로그인 페이지로 이동하며, 로그인 이후 원래 요청했던 /mypage로 돌아올 수 있도록 구현했습니다.

if(loginMember == null) {
    String requestURI = request.getRequestURI();
    session.setAttribute("redirectURL", requestURI);
    return "redirect:/login";
}


마이페이지에서는 다음 정보를 확인할 수 있습니다.

회원 정보

주소

주문 내역

주문 상태

주문 상품 정보

4. 🔐 관리자 기능
관리자 대시보드

GET /admin

관리자 전용 페이지로 다음 통계 정보를 제공합니다.

전체 주문 수

총 매출

등록 상품 수

전체 회원 수

전체 주문 목록

관리자 페이지 접근 시 세션의 로그인 회원을 확인하고 Role.ADMIN 권한을 검증합니다.

Member loginMember =
        (Member) session.getAttribute("loginMember");

if (loginMember == null) {
    return "redirect:/login";
}

if (loginMember.getRole() != Role.ADMIN) {
    return "redirect:/";
}


일반 사용자가 관리자 페이지에 접근할 경우 메인 페이지로 리다이렉트됩니다.

📊 관리자 통계

전체 주문을 조회한 뒤 취소되지 않은 주문을 기준으로 총 매출을 계산합니다.

int totalSales = 0;

for (Order o : orders) {
    if (o.getOrderStatus() != null
            && o.getOrderStatus() != OrderStatus.CANCEL) {
        totalSales += o.getTotalPrice();
    }
}


관리자 대시보드에서는 다음과 같은 데이터를 제공합니다.

총 주문 건수
총 매출액
등록 상품 수
회원 수

5. 🧢 상품 도메인 설계

상품 도메인은 JPA의 Single Table Inheritance 전략을 사용했습니다.

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public abstract class Item


상품의 공통 속성을 Item 추상 클래스에서 관리하고, 상품 종류에 따라 하위 엔티티를 확장할 수 있도록 설계했습니다.

현재 구현된 상품 예시는 다음과 같습니다.

Item
 ├── Cap
 └── Cloth


Single Table 전략을 사용하기 때문에 상품 유형은 하나의 테이블에서 dtype 컬럼으로 구분됩니다.

6. 🧢 Cap 상품

Cap은 Item을 상속받아 모자 상품에 필요한 추가 정보를 관리합니다.

@Entity
@DiscriminatorValue("CAP")
public class Cap extends Item {

    private String adjustType;
    private String capType;
}


기본 상품 정보 외에 다음 정보를 추가로 관리합니다.

조절 방식 (adjustType)

모자 형태 (capType)

예를 들어 새로운 상품 유형이 추가되는 경우 Item을 상속하여 확장할 수 있습니다.

7. 📦 상품 옵션 및 재고

상품 하나에 여러 개의 옵션을 가질 수 있도록 Item과 ItemOption을 1:N 관계로 설계했습니다.

@OneToMany(
    mappedBy = "item",
    cascade = CascadeType.ALL
)
private List<ItemOption> options = new ArrayList<>();


상품에 옵션을 추가할 때는 addOption() 메서드를 사용합니다.

public void addOption(ItemOption option) {
    options.add(option);
    option.setItem(this);
}


양방향 연관관계의 양쪽 값을 함께 설정하도록 구현했습니다.

📦 총 재고 계산

상품의 전체 재고는 각 옵션의 재고를 합산하여 계산합니다.

public int getTotalStockQuantity() {
    int total = 0;

    for (ItemOption itemOption : options) {
        total += itemOption.getStockQuantity();
    }

    return total;
}


예를 들어:

Black / M : 10개
Black / L : 5개
White / M : 8개

----------------
총 재고 : 23개


와 같이 상품 단위의 전체 재고를 계산할 수 있습니다.

8. 💰 상품 가격 관리

상품 가격 변경 시 음수 가격이 입력되지 않도록 검증합니다.

public void changePrice(int price) {
    if (price < 0) {
        throw new IllegalArgumentException(
            "Price must be greater than or equal to 0."
        );
    }

    this.price = price;
}


이를 통해 도메인 객체 내부에서 기본적인 가격 검증을 수행하도록 구현했습니다.

9. ⚙️ 관리자 초기 데이터 자동 생성

애플리케이션이 실행될 때 관리자 계정이 존재하지 않는 경우 기본 관리자 계정을 자동으로 생성합니다.

ApplicationReadyEvent를 활용하여 애플리케이션 준비가 완료된 시점에 초기화 로직을 실행합니다.

@EventListener(ApplicationReadyEvent.class)
public void init() {
    initService.dbInit();
}


관리자 계정이 이미 존재하는지 이메일을 기준으로 확인합니다.

if (memberRepository
        .findByEmail("admin@dropfit.com")
        .isEmpty()) {
    ...
}


기본 관리자 정보:

Email    : admin@dropfit.com
Password : admin123
Role     : ADMIN
Name     : ADMIN


⚠️ 실제 운영 환경에서는 기본 비밀번호를 변경하거나 환경 변수 및 시크릿 관리 시스템을 사용하는 것을 권장합니다.

🏗️ 프로젝트 구조

프로젝트는 Controller → Service → Repository → Domain 구조를 기반으로 구성했습니다.

src
└── main
    ├── java
    │   └── com.dropfit
    │       ├── Controller
    │       │   ├── admin
    │       │   │   └── AdminDashboardController
    │       │   ├── HomeController
    │       │   ├── ItemController
    │       │   ├── MemberController
    │       │   └── MyPageController
    │       │
    │       ├── domain
    │       │   ├── Item
    │       │   ├── Cap
    │       │   ├── Cloth
    │       │   ├── ItemOption
    │       │   ├── Member
    │       │   ├── Order
    │       │   ├── OrderStatus
    │       │   └── Role
    │       │
    │       ├── repository
    │       └── service
    │
    └── resources
        ├── templates
        │   ├── admin
        │   │   └── dashboard.html
        │   ├── item
        │   │   └── item-list.html
        │   ├── member
        │   │   ├── login.html
        │   │   ├── signup.html
        │   │   └── mypage.html
        │   └── index.html
        │
        └── application.yml

🔄 주요 사용자 흐름
회원가입
회원가입 페이지
      ↓
POST /signup
      ↓
MemberController
      ↓
MemberService
      ↓
MemberRepository
      ↓
PostgreSQL

로그인
로그인
  ↓
MemberController
  ↓
MemberService.login()
  ↓
회원 정보 검증
  ↓
HttpSession 저장
  ↓
메인 페이지 이동

상품 검색
GET /items?searchQuery=...
          ↓
ItemController
          ↓
ItemService
          ↓
상품 + 옵션 검색
          ↓
item-list.html

관리자 페이지
/admin
   ↓
로그인 여부 확인
   ↓
Role.ADMIN 확인
   ↓
OrderService
ItemService
MemberService
   ↓
통계 데이터 생성
   ↓
admin/dashboard.html

🔐 권한 처리

현재 관리자 접근 제어는 세션에 저장된 Member의 Role을 기준으로 처리합니다.

                 ┌──────────────┐
                 │   /admin     │
                 └──────┬───────┘
                        ↓
                로그인 여부 확인
                   ↙          ↘
                 NO            YES
                 ↓              ↓
             /login       Role 확인
                              ↓
                     ┌────────┴────────┐
                     ↓                 ↓
                  ADMIN            USER
                     ↓                 ↓
              관리자 대시보드         /


관리자 권한:

Role.ADMIN


일반 사용자가 관리자 URL에 접근하는 경우 /로 이동하도록 구현했습니다.

🚀 Getting Started
1. Repository Clone
git clone https://github.com/your-username/dropfit.git

2. 프로젝트 이동
cd dropfit

3. PostgreSQL 설정

PostgreSQL 데이터베이스를 생성하고 프로젝트의 데이터베이스 설정을 환경에 맞게 변경합니다.

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/dropfit
    username: your-username
