# 🛒 DropFit (의류 쇼핑몰 백엔드 서비스)

> Spring Boot 기반의 확장 가능한 이커머스 쇼핑몰 백엔드 서비스입니다. 사용자 기능과 관리자 기능을 완벽히 분리하여 안정적인 상품 주문과 쇼핑몰 운영 관리를 지원합니다.

---

## 🌐 Service URL
- **Live Demo**: [http://dropfit.duckdns.org](http://dropfit.duckdns.org)

---

## 🛠 Tech Stack
- **Backend**: Java 21, Spring Boot, Spring Data JPA
- **Database**: PostgreSQL 18.6
- **Build Tool**: Gradle
- **Deployment**: AWS EC2 (Ubuntu 26.04), DuckDNS

---

## 📁 Directory Structure

```text
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
