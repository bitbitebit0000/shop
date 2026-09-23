# 🛒 DropFit

Spring Boot と JPA をベースに構築した EC バックエンドサービスです。ユーザー用のショッピング機能と管理者機能を分離し、商品・会員・注文ドメインを中心に拡張可能な構造を設計しました。

## 🌐 Service URL
* **Live Demo**: [http://dropfit.duckdns.org](http://dropfit.duckdns.org)

### 🔑 Test Accounts

* **Admin Account(Admin)**
  * **Email**: `admin@dropfit.com`
  * **Password**: `admin123`
  * **Role**: `ADMIN`

* **User Account(User)**
  * **Email**: `user@123`
  * **Password**: `user123`
  * **Role**: `USER`
---

## 🛠 Tech Stack
* **Backend**: Java 21, Spring Boot
* **Database**: PostgreSQL 18.6
* **Build Tool**: Gradle
* **Deployment**: AWS EC2 (Ubuntu 26.04), DuckDNS

---

## ✨ 主な機能

### 🏠 メインページ (`GET /`)
* 全商品を照会してメイン画面に渡します。

  ```java
  List<Item> items = itemService.findItems();
  model.addAttribute("items", items);
  ```

<img width="3016" height="1722" alt="home" src="https://github.com/user-attachments/assets/286fe6ac-7aa3-42c5-966a-bc022b6e8e42" />

### 👤 会員機能
会員登録からログイン、ログアウト、マイページまで、ECサイトの基本的な会員機能を実装しました。

* **会員登録 (`POST /signup`)**
  * メールアドレス、パスワード、氏名、住所情報を入力して会員登録ができます。
  * 会員登録時、`Member` エンティティと `Address` オブジェクトを生成して会員情報を保存します。
* **ログイン (`POST /login`)**
  * ログイン成功時、`HttpSession` にログイン会員情報を保存します。

    ```java
    HttpSession session = request.getSession();
    session.setAttribute("loginMember", loginMember);
    ```
    
* ログインに失敗した場合、ログインページにエラーメッセージを渡します。 (`Invalid email or password.`)
  * また、ログイン前にアクセスしようとしていたページがある場合、`redirectURL` をセッションに保存し、ログイン後にそのページへ遷移するように実装しました。
* **ログアウト (`POST /logout`)**
  * ログアウト時、現在のセッションを無効化 (`invalidate`) してログイン情報を削除します。

    ```java
    session.invalidate();
    ```
    
    <img width="3003" height="1715" alt="sign" src="https://github.com/user-attachments/assets/03d11f62-fd9e-4f57-b4ab-51a4c5b587ea" />

### 🛍 商品照会
* **商品一覧 (`GET /items`)**
  * 登録されている商品を照会できます。
  * 検索キーワードが存在する場合、商品名またはオプション情報を基準に検索し、検索キーワードがない場合は全商品を照会します。

    ```text
    /items
    /items?searchQuery=cap
    ```

     * Controller で検索キーワードの有無に応じて異なる Service メソッドを呼び出すように実装しました。
    ```java
    if (searchQuery != null && !searchQuery.isBlank()) {
        items = itemService.findItemsWithOptionBySearch(searchQuery);
    } else {
        items = itemService.findItemsWithOption();
    }
    ```
    
 <img width="3024" height="1674" alt="ss" src="https://github.com/user-attachments/assets/ea4c0022-e0d0-4868-931b-0c6f0efda4b9" />


### 👤 マイページ (`GET /mypage`)
* ログイン中の会員情報を照会し、該当会員の注文履歴を併せて提供します。
* 未ログインユーザーがアクセスした場合、ログインページへリダイレクトし、ログイン後に元々リクエストしていた `/mypage` へ戻れるように実装しました。

 ```java
  if (loginMember == null) {
      String requestURI = request.getRequestURI();
      session.setAttribute("redirectURL", requestURI);
      return "redirect:/login";
  }
 ```
  
* マイページでは以下の情報を確認できます:
  * **会員情報**: 基本アカウントおよび個人情報
  * **住所**: 登録された配送先情報
  * **注文履歴**: ユーザーのすべての注文履歴
  * **注文ステータス**: 商品準備中、配送中、完了などの注文処理状況
  * **注文商品情報**: 注文した商品の詳細情報およびオプション情報

<img width="3024" height="1690" alt="m" src="https://github.com/user-attachments/assets/3f02d4b5-adc7-4e8d-be6d-4dfb3d4c2322" />

### 🔐 管理者機能
* **管理者ダッシュボード (`GET /admin`)**
  * 管理者専用ページとして、以下の統計情報を提供します。
    * **全注文数**
    * **総売上**
    * **登録商品数**
    * **全会員数**
    * **全注文一覧**


<img width="3024" height="1701" alt="dddd" src="https://github.com/user-attachments/assets/5a7e02ee-bf6e-4803-91fe-b4581b3a6391" />



* 管理者ページへのアクセス時にセッションのログインユーザーを確認し、`Role.ADMIN` 権限を検証します。

 ```java
Member loginMember = (Member) session.getAttribute("loginMember");
if (loginMember == null) {
    return "redirect:/login";
}
if (loginMember.getRole() != Role.ADMIN) {
    return "redirect:/";
}
```

 * 一般ユーザーが管理者ページにアクセスした場合、メインページにリダイレクトされます。

* **管理者初期データの自動生成**
  * アプリケーションの起動時に管理者アカウントが存在しない場合、デフォルトの管理者アカウントを自動的に生成します。
  * `ApplicationReadyEvent` を利用して、アプリケーションの準備が完了した時点で初期化ロジックを実行します。

```java
@EventListener(ApplicationReadyEvent.class)
public void init() {
    initService.dbInit();
}
```
* 管理者アカウントがすでに存在するかどうかをメールアドレスを基準に確認します。

```java
if (memberRepository.findByEmail("admin@dropfit.com").isEmpty()) {
  ...
}
```

### 📦 ドメイン設計の特徴：商品とオプション（`Item` & `ItemOption`）

* 1つの商品が複数のオプションを持てるように、`Item`と`ItemOption`を1:Nの関係として設計しました。

  ```java
  @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
  private List<ItemOption> options = new ArrayList<>();

* 商品にオプションを追加する際は、`addOption()` メソッドを使用します。

  ```java
  public void addOption(ItemOption option) {
      options.add(option);
      option.setItem(this);
  }
  
* 双方向の関連関係における両方の値を同時に設定するように実装しました。

## 💳 注文および決済の主要プロセス（`OrderController`）

### 注文作成および決済承認処理（`POST /order`）
* 検証が完了すると、注文待機状態（`createPendingOrder`）を先に作成します。
* 通常決済（`CARD` など）の場合、決済承認ID（`paymentId`）を検証した後、最終的な決済完了処理（`completePayment`）を実行します。


```java
Long orderId = orderService.createPendingOrder(loginMember.getId(), itemOptionId, count, payType);
if (!"BANK".equals(payType)) {
    if (paymentId == null || paymentId.isBlank()) {
        throw new IllegalStateException("Payment approval ID (paymentId) is missing.");
    }
    orderService.completePayment(orderId, paymentId, merchantUid);
}
```

<img width="3024" height="1712" alt="22" src="https://github.com/user-attachments/assets/54c00f65-864c-42f6-84f0-bd7ab8f513f8" />

### 注文チェックアウトおよび在庫数量の検証（`POST /order/checkout`）
* 選択したオプションが存在するかを確認し、在庫数量（`stockQuantity`）が注文数量より不足していないかを検証します。
* 在庫が不足している場合は、`NotEnoughStockException` をスローし、エラーメッセージとともに前のページへリダイレクトします。

```java
if (selectedOption.getStockQuantity() < count) {
    throw new NotEnoughStockException("Not enough stock. (Current remaining stock: " + selectedOption.getStockQuantity() + " pcs)");
}
```
<img width="3024" height="1599" alt="ㅊㅊㅊ" src="https://github.com/user-attachments/assets/8dc78777-c967-46c1-94df-a665f667fa9c" />

#### 3. 決済完了および注文照会（`GET /order/complete/{orderId}`）
* 決済完了後、注文番号で注文情報を照会し、完了ページに渡します。

```java
Order order = orderService.findOrder(Long.valueOf(orderId));
model.addAttribute("order", order);
model.addAttribute("payType", payType);
return "order/order-complete";
```
  <img width="3024" height="1708" alt="333" src="https://github.com/user-attachments/assets/cbc29898-bd5a-4f7d-ad9c-0d2696db6d4a" />

## 📊 データベースERD（Database ERD）

<img width="1310" height="1444" alt="Diagram" src="https://github.com/user-attachments/assets/b9f16781-8df2-43c7-838a-6dcc845c8e3c" />

## 📂 プロジェクトアーキテクチャ（Project Architecture）

```text
src/main
├── java
│   └── com
│       └── dropfit
│           ├── Controller
│           │   ├── HomeController.java
│           │   ├── ItemController.java
│           │   ├── MemberController.java
│           │   ├── MyPageController.java
│           │   ├── OrderController.java
│           │   └── admin
│           │       ├── AdminDashboardController.java
│           │       ├── AdminItemController.java
│           │       ├── AdminMemberController.java
│           │       └── AdminOrderController.java
│           ├── ShoppingApplication.java
│           ├── domain
│           │   ├── Address.java
│           │   ├── Delivery.java
│           │   ├── DeliveryStatus.java
│           │   ├── InitAdminData.java
│           │   ├── ItemForm.java
│           │   ├── ItemFormDto.java
│           │   ├── LoginForm.java
│           │   ├── Member.java
│           │   ├── MemberForm.java
│           │   ├── Order.java
│           │   ├── OrderItem.java
│           │   ├── OrderStatus.java
│           │   ├── Role.java
│           │   └── item
│           │       ├── Cap.java
│           │       ├── Clothing.java
│           │       ├── Item.java
│           │       └── ItemOption.java
│           ├── dto
│           │   └── MyPageDto.java
│           ├── exception
│           │   └── NotEnoughStockException.java
│           ├── repository
│           │   ├── ItemOptionRepository.java
│           │   ├── ItemRepository.java
│           │   ├── MemberRepository.java
│           │   └── OrderRepository.java
│           └── service
│               ├── InitDb.java
│               ├── InitService.java
│               ├── ItemService.java
│               ├── MemberService.java
│               └── OrderService.java
└── resources
    ├── application.yml
    ├── static
    │   └── css
    │       ├── admin
    │       │   ├── common.css
    │       │   ├── create-item-form.css
    │       │   ├── dashboard.css
    │       │   ├── item-list.css
    │       │   ├── members.css
    │       │   └── orders.css
    │       ├── index.css
    │       ├── item
    │       │   ├── create-item-form.css
    │       │   └── item-list.css
    │       ├── member
    │       │   ├── login.css
    │       │   ├── mypage.css
    │       │   └── signup.css
    │       └── order
    │           ├── cancel.css
    │           ├── checkout.css
    │           ├── complete.css
    │           ├── fail.css
    │           ├── form.css
    │           └── success.css
    └── templates
        ├── admin
        │   ├── create-item.html
        │   ├── dashboard.html
        │   ├── item-list.html
        │   ├── members.html
        │   └── orders.html
        ├── fragments
        │   └── header.html
        ├── index.html
        ├── item
        │   ├── create-item-form.html
        │   └── item-list.html
        ├── member
        │   ├── login.html
        │   ├── mypage.html
        │   └── signup.html
        └── order
            ├── cancel.html
            ├── checkout.html
            ├── fail.html
            ├── form.html
            ├── order-complete.html
            └── success.html
```

## 👨‍💻 Developer
* **GitHub**: [@bitbitebit0000](https://github.com/bitbitebit0000)
* **Email**: taewook591@gmail.com
