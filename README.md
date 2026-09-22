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

## ✨ Key Features & Architecture

### 1. 사용자 (Customer) 서비스
- **회원 관리**: 회원가입, 로그인 및 세션 기반 인증 처리 (`/login`, `/signup`)
- **상품 탐색**: 카테고리별 상품 목록 조회 및 실시간 검색 기능 지원 (`/items`)
- **마이페이지**: 개인 주문 내역 확인 및 회원 정보 관리 (`/mypage`)

> 💡 **[스크린샷 삽입 공간: 쇼핑몰 메인 / 상품 목록 페이지]**

---

### 2. 관리자 (Admin) 페이지
- **관리자 전용 대시보드**: 총 주문 건수, 총 매출액, 등록된 상품 및 회원 수를 한눈에 파악할 수 있는 통계 제공 (`/admin`)
- **권한 제어**: `Role.ADMIN` 검증 로직을 통한 일반 사용자의 관리자 페이지 접근 차단 및 보안 강화

> 💡 **[스크린샷 삽입 공간: 관리자 대시보드 화면]**

---

### 3. 데이터베이스 및 도메인 설계
- **상속 매핑 (Single Table Strategy)**: `Item` 추상 클래스를 중심으로 `Cap`, `Cloth` 등 상품 유형별 확장성을 고려한 JPA 엔티티 설계
- **초기 데이터 자동 세팅**: 애플리케이션 구동 시(`ApplicationReadyEvent`) 기본 관리자 계정 자동 생성 (`InitAdminData`)

---

## 🚀 Getting Started (Local Development)

```bash
# Clone the repository
git clone [https://github.com/your-username/dropfit.git](https://github.com/your-username/dropfit.git)

# Build and Run
cd dropfit
./gradlew bootRun

## 📂 プロジェクト構造 (Architecture)

```text
src
 ┣ main
 ┃ ┣ java/com/shop/dropfit
 ┃ ┃ ┣ controller   # Webリクエスト処理 (Member, Item, Order, Admin)
 ┃ ┃ ┣ service      # ビジネスロジックおよびトランザクション管理
 ┃ ┃ ┣ repository   # Spring Data JPA インターフェース
 ┃ ┃ ┗ entity       # ドメインモデル (Member, Item, Order 等)
 ┃ ┗ resources
 ┃   ┣ static/css   # 共通およびページ別スタイルシート (style.css)
 ┃   ┣ templates    # Thymeleaf HTML ビューテンプレート (index, login, signup, items 等)
 ┃   ┗ application.properties
 ┗ test             # 単位および統合テストコード
