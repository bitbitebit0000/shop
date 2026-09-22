# 🛒 DropFit - Limited Fashion Drop Commerce

> 限定数量のストリートファッション＆人気ブランドのドロップアイテムを最速で手に入れる、先着順タイムディールEコマースプラットフォームです。

---

## 🚀 プロジェクト概要
* **プロジェクト名:** DropFit (ドロップフィット)
* **開発期間:** 個人プロジェクト
* **デプロイURL:** [https://shop-m7va.onrender.com](https://shop-m7va.onrender.com)
* **主な特徴:** 
  * 毎週公開される限定アイテム（フーディー、スニーカーなど）のタイムドロップ構造
  * リアルタイム在庫連動および先着順購入・注文処理ロジックの実装
  * Spring Security 및 세션 기반의 안전한 회원 인증/권한 관리 (一般会員 / 管理者センター権限分離)

---

## 🛠 Tech Stack

### **Backend**
* Java 17+
* Spring Boot
* Spring Data JPA / Hibernate
* MySQL
* Thymeleaf

### **Frontend & UI**
* HTML5, CSS3 (Modern Flexbox & Responsive Layout)
* JavaScript (ES6+)

### **Deployment & Infra**
* Render (Cloud Hosting)
* Git & GitHub

---

## ✨ 主な機能とロジック

1. **ユーザー認証および権限管理 (Session & Security)**
   * 会員登録 / ログイン / ログアウト機能
   * Spring Session 기반 로그인 상태 유지
   * 一般会員(`ROLE_USER`)と管理者(`ROLE_ADMIN`)の権限分離によるメニューおよびアクセス制御 (`/admin` 管理者センター保護)

2. **限定商品およびドロップシステム**
   * メインページにおけるおすすめドロップアイテムのリアルタイム表示
   * 商品検索機能 (キーワードベース: フーディー、スニーカー、Tシャツ、ボールキャップなど)
   * 商品別リアルタイム在庫数(`totalStockQuantity`)連動および**在庫切れ時の自動 `SOLD OUT` ボタン切り替え処理**

3. **注文およびマイページ**
   * 先着順タイムディールアイテムの即時購入(Order)プロセス
   * マイページを通じた個人情報および注文履歴の確認

4. **UI/UX最適化**
   * ページ遷移時のヘッダーの高さおよびレイアウトのガタつき(Layout Shift)を防ぐための固定規格適用 (`height: 73px`)
   * モバイルおよびタブレット画面に対応するレスポンシブウェブデザイン (Responsive Web)

---

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
