# 🛒 DropFit (의류 쇼핑몰 백엔드 서비스)

Spring Boot와 JPA를 기반으로 구현한 의류 쇼핑몰 백엔드 서비스입니다. 사용자 쇼핑 기능과 관리자 기능을 분리하고, 상품·회원·주문 도메인을 중심으로 확장 가능한 구조를 설계했습니다.

## 🌐 Service URL
* **Live Demo**: [http://dropfit.duckdns.org](http://dropfit.duckdns.org)

---

## 🛠 Tech Stack
* **Backend**: Java 21, Spring Boot
* **Database**: PostgreSQL 18.6
* **Build Tool**: Gradle
* **Deployment**: AWS EC2 (Ubuntu 26.04), DuckDNS

---

## ✨ 주요 기능

### 👤 회원 기능
회원가입부터 로그인, 로그아웃, 마이페이지까지 기본적인 쇼핑몰 회원 기능을 구현했습니다.

* **회원가입 (`POST /signup`)**
  * 이메일, 비밀번호, 이름, 주소 정보를 입력하여 회원가입할 수 있습니다.
  * 회원가입 시 `Member` 엔티티와 `Address` 객체를 생성하여 회원 정보를 저장합니다.
* **로그인 (`POST /login`)**
  * 로그인 성공 시 `HttpSession`에 로그인한 회원 정보를 저장합니다.
    ```java
    HttpSession session = request.getSession();
    session.setAttribute("loginMember", loginMember);
    ```
  * 로그인에 실패하면 로그인 페이지에 오류 메시지를 전달합니다. (`Invalid email or password.`)
  * 또한 로그인 이전에 접근하려던 페이지가 있다면 `redirectURL`을 세션에 저장하여 로그인 후 해당 페이지로 이동하도록 구현했습니다.
* **로그아웃 (`POST /logout`)**
  * 로그아웃 시 현재 세션을 무효화(`invalidate`)하여 로그인 정보를 제거합니다.
    ```java
    session.invalidate();
    ```

### 🛍 상품 조회
* **상품 목록 (`GET /items`)**
  * 등록된 상품을 조회할 수 있습니다.
  * 검색어가 존재하는 경우 상품명 또는 옵션 정보를 기준으로 검색하고, 검색어가 없는 경우 전체 상품을 조회합니다.
    ```text
    /items
    /items?searchQuery=cap
    ```
  * Controller에서 검색어 유무에 따라 다른 Service 메서드를 호출하도록 구현했습니다.
    ```java
    if (searchQuery != null && !searchQuery.isBlank()) {
        items = itemService.findItemsWithOptionBySearch(searchQuery);
    } else {
        items = itemService.findItemsWithOption();
    }
    ```

### 🏠 메인 페이지 (`GET /`)
* 전체 상품을 조회하여 메인 화면에 전달합니다.
  ```java
  List<Item> items = itemService.findItems();
  model.addAttribute("items", items);


### 👤 마이페이지 (`GET /mypage`)
* 로그인한 회원의 정보를 조회하고 해당 회원의 주문 내역을 함께 제공합니다.
* 로그인하지 않은 사용자가 접근하면 로그인 페이지로 이동하며, 로그인 이후 원래 요청했던 `/mypage`로 돌아올 수 있도록 구현했습니다.
  ```java
  if (loginMember == null) {
      String requestURI = request.getRequestURI();
      session.setAttribute("redirectURL", requestURI);
      return "redirect:/login";
  }
  마이페이지에서는 다음 정보를 확인할 수 있습니다:

회원 정보

주소

주문 내역

주문 상태

주문 상품 정보


### 🔐 관리자 기능
관리자 대시보드 (GET /admin)

관리자 전용 페이지로 다음 통계 정보를 제공합니다.

전체 주문 수

총 매출

등록 상품 수

전체 회원 수

전체 주문 목록

관리자 페이지 접근 시 세션의 로그인 회원을 확인하고 Role.ADMIN 권한을 검증합니다.
Member loginMember = (Member) session.getAttribute("loginMember");
if (loginMember == null) {
    return "redirect:/login";
}
if (loginMember.getRole() != Role.ADMIN) {
    return "redirect:/";
}
일반 사용자가 관리자 페이지에 접근할 경우 메인 페이지로 리다이렉트됩니다.

전체 주문을 조회한 뒤 취소되지 않은 주문을 기준으로 총 매출을 계산합니다.

int totalSales = 0;
for (Order o : orders) {
    if (o.getOrderStatus() != null && o.getOrderStatus() != OrderStatus.CANCEL) {
        totalSales += o.getTotalPrice();
    }
}

관리자 초기 데이터 자동 생성

애플리케이션이 실행될 때 관리자 계정이 존재하지 않는 경우 기본 관리자 계정을 자동으로 생성합니다.

ApplicationReadyEvent를 활용하여 애플리케이션 준비가 완료된 시점에 초기화 로직을 실행합니다.

@EventListener(ApplicationReadyEvent.class)
public void init() {
    initService.dbInit();
}
관리자 계정이 이미 존재하는지 이메일을 기준으로 확인합니다.
if (memberRepository.findByEmail("admin@dropfit.com").isEmpty()) {
    ...
}
관리자 정보
Email: admin@dropfit.com

Password: admin123

Role: ADMIN

Name: ADMIN

📦 도메인 설계 특징상품과 옵션 (Item & ItemOption)

상품 하나에 여러 개의 옵션을 가질 수 있도록 Item과 ItemOption을 1:N 관계로 설계했습니다.
@OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
private List<ItemOption> options = new ArrayList<>();

상품에 옵션을 추가할 때는 addOption() 메서드를 사용합니다.
public void addOption(ItemOption option) {
    options.add(option);
    option.setItem(this);
}
양방향 연관관계의 양쪽 값을 함께 설정하도록 구현했습니다.

상품의 전체 재고는 각 옵션의 재고를 합산하여 계산합니다.

가격 검증 로직

상품 가격 변경 시 음수 가격이 입력되지 않도록 검증합니다.

public void changePrice(int price) {
    if (price < 0) {
        throw new IllegalArgumentException("Price must be greater than or equal to 0.");
    }
    this.price = price;
}

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
    │       ├── domain
    │       │   ├── Item
    │       │   ├── Cap
    │       │   ├── Cloth
    │       │   ├── ItemOption
    │       │   ├── Member
    │       │   ├── Order
    │       │   ├── OrderStatus
    │       │   └── Role
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
  
