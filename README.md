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

## 아키텍쳐

### 전체 흐름 + CI/CD

<img width="1000" height="700" alt="전쳬흐름도 drawio" src="https://github.com/user-attachments/assets/cb2d0cd5-ae59-4a32-b8d7-2b7386dfc8f7" />

- Client 요청은 Frontend(Vercel) → Backend(EC2) → DB(RDS) 순으로 처리되고, Backend는 결제 승인·웹훅 수신을 위해 Toss Payments와 통신.  
- 배포는 GitHub Actions가 이미지를 ECR에 push하면 EC2가 pull해서 재기동하는 방식.

### 인프라 구성 (VPC)

<img width="1000" height="1000" alt="인프라구성도 drawio" src="https://github.com/user-attachments/assets/d03d3513-3819-4e28-bf58-a2daafb3f0b7" />

- EC2(Public Subnet)는 Route53 으로 요청을 받아 처리하고, Private Subnet의 RDS에는 EC2를 통해서 접근 가능.
- 상품 이미지는 DB에 저장된 S3 URL을 API 응답에 그대로 포함하며, 브라우저가 그 URL로 S3에서 이미지를 직접 조회.  
- EC2는 기동 시 Secrets Manager에서 자격증명을 읽고 CloudWatch로 발생하는 로그들을 전송.

❗️NAT, ELB 는 과금 이슈로 제외..
