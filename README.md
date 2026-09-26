# petshop-back

뚱이요미 샵(굿즈 쇼핑몰)의 백엔드 API 서버. 프론트엔드는 별도 프로젝트로 구성된다.

## 목적

- 상품 조회 → 주문 생성 → Toss Payments 결제까지 이어지는 커머스 백엔드를 구현
- 기능 구현을 넘어 **인프라 구성 실습**까지를 목적으로 하는 프로젝트

## 제공하는 기능

- 상품 목록/상세 조회 (카테고리 필터링)
- 주문 생성 (비회원/게스트 체크아웃, 별도 인증 없음)
- Toss Payments 결제 승인(confirm) 및 웹훅 수신
- 카테고리/상품 초기 데이터를 채우는 시딩용 API

## 기술 스택

- Java 25 (LTS)
- Spring Boot 4.x (최신 안정 버전)
- MySQL (로컬/운영 공통, 운영은 AWS RDS)
- Gradle
- Toss Payments 연동 (Spring RestClient 기반)
