# TravelPick

> 이 프로젝트는 LLM의 서비스 적용에 대한 학습 및 교내 졸업 작품 심사를 위해 진행되었음
> 프로젝트의 진행은 로컬 서버에서 진행되었으며 스펙은 다음과 같음
> - CPU: AMD Ryzen 5 5600G with Radeon Graphics
> - MEM: 32GB
> - GPU: NVIDIA GeForce GTX 1650 4GB

<br/>

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)

---

## Tech Stack

### Backend (`/demo`)

| Category | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.4.2 |
| API | GraphQL (Spring for GraphQL) |
| ORM | Spring Data JPA + Hibernate Spatial |
| Reactive | Spring WebFlux |
| Security | Spring Security (Session-based) |
| Database | MySQL 8.0, MongoDB, Redis |
| Documentation | SpringDoc OpenAPI (Swagger UI) |
| Build | Maven |

### Bot (`/bot`)

| Category | Technology |
|---|---|
| Language | Python 3.10+ |
| Web Framework | Flask |
| Vector DB | ChromaDB |
| ML Models | CLIP, BLIP, Gemma, Longformer (HuggingFace) |
| Deep Learning | PyTorch, Transformers |
| ORM | SQLAlchemy |
| Crawling | Selenium |

---

## Architecture

```
┌─────────────────────────────────────────────────────┐
│                     Client                          │
│                (GraphQL / REST)                     │
└─────────────────────┬───────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────┐
│              Spring Boot Backend (:8080)            │
│                                                     │
│  Controller → Service → Repository                  │
│                                                     │
│  ┌──────────┐  ┌──────────┐  ┌──────────────────┐  │
│  │  MySQL   │  │ MongoDB  │  │      Redis       │  │
│  │(Entities)│  │  (Data)  │  │(Session / Cache) │  │
│  └──────────┘  └──────────┘  └──────────────────┘  │
│                                                     │
│         ChromaRepository (HTTP)                     │
└─────────────────────┬───────────────────────────────┘
                      │
                      ▼ HTTP (:50000)
┌─────────────────────────────────────────────────────┐
│                  Python Bot                         │
│                                                     │
│  Flask API                                          │
│  ├── /api/search  (text / image semantic search)   │
│  ├── /api/chroma  (store / delete embeddings)      │
│  └── /             (health check)                  │
│                                                     │
│  ┌─────────────────────────────────────────────┐   │
│  │  ML Models                                  │   │
│  │  CLIP · BLIP · Gemma · Longformer           │   │
│  └─────────────────────────────────────────────┘   │
│                                                     │
│  ┌──────────┐   ┌─────────────────────────────┐    │
│  │ ChromaDB │   │ MySQL (SQLAlchemy)           │    │
│  │(Vectors) │   │(Crawler / Indexing results) │    │
│  └──────────┘   └─────────────────────────────┘    │
└─────────────────────────────────────────────────────┘
```

### 설계 방식

**Backend**
- **GraphQL-first** 설계: 모든 데이터 쿼리 및 뮤테이션을 GraphQL 스키마 기반으로 처리합니다.
- **계층형 아키텍처**: Controller → Service → Repository 레이어로 관심사를 분리합니다.
- **다중 데이터베이스**: MySQL(관계형 데이터), MongoDB(비정형 데이터), Redis(세션/캐시)를 용도별로 분리합니다.
- **Spring Security**: CSRF 필터와 세션 기반 인증으로 보안을 처리합니다.
- **Hibernate Spatial**: 위치 기반(공간) 쿼리를 지원합니다.

**Bot**
- **멀티모달 검색**: CLIP(이미지-텍스트), BLIP(이미지 캡셔닝), Longformer(장문 텍스트)를 결합해 텍스트·이미지 혼합 시맨틱 검색을 수행합니다.
- **Singleton 모델 캐싱**: 무거운 ML 모델을 최초 1회만 로드하고 재사용합니다.
- **Flask 마이크로서비스**: 벡터 저장·검색을 독립 HTTP API로 노출해 Backend와 통신합니다.

---

## Project Structure

```
travelPick/
├── demo/                                # Spring Boot Backend
│   ├── src/main/java/com/base/demo/
│   │   ├── MainApplication.java
│   │   ├── controller/                  # GraphQL Controllers
│   │   │   ├── AuthenticationController.java
│   │   │   ├── LocationGraphQLController.java
│   │   │   ├── PhotoGraphQLController.java
│   │   │   ├── ResortGraphQLController.java
│   │   │   └── ReviewGraphQLController.java
│   │   ├── service/                     # Business Logic
│   │   │   ├── LocationService.java
│   │   │   ├── PhotoService.java
│   │   │   ├── ResortService.java
│   │   │   ├── ReviewService.java
│   │   │   └── UserService.java
│   │   ├── entity/                      # JPA Entities
│   │   │   ├── Location.java
│   │   │   ├── Photo.java
│   │   │   ├── Resort.java
│   │   │   ├── Review.java
│   │   │   └── User.java
│   │   ├── repository/                  # Data Access Layer
│   │   │   ├── ChromaRepository.java    # Bot HTTP Client
│   │   │   ├── LocationRepository.java
│   │   │   ├── PhotoRepository.java
│   │   │   ├── ResortRepository.java
│   │   │   ├── ReviewRepository.java
│   │   │   └── UserRepository.java
│   │   ├── dto/                         # API Request/Response DTOs
│   │   ├── config/                      # Spring Configuration
│   │   │   ├── GraphQlConfig.java
│   │   │   ├── SecurityConfig.java
│   │   │   ├── SwaggerConfig.java
│   │   │   └── WebConfig.java
│   │   ├── filter/                      # Security Filters
│   │   │   ├── CsrfHeaderFilter.java
│   │   │   └── SessionAuthenticationFilter.java
│   │   ├── exception/                   # Global Exception Handling
│   │   └── utils/
│   └── src/main/resources/
│       ├── application.properties
│       └── graphql/schema.graphqls      # GraphQL Schema
│
└── bot/                                 # Python Semantic Search Service
    ├── server.py                        # Flask API Entry Point
    ├── main.py
    ├── lib/
    │   ├── chroma.py                    # Vector Store Wrapper
    │   ├── utils.py                     # Utilities
    │   └── models/                      # ML Model Interfaces
    │       ├── clip.py
    │       ├── blip.py
    │       ├── gemma.py
    │       └── longformer.py
    ├── db/                              # Database Layer (SQLAlchemy)
    │   ├── database.py
    │   ├── model.py
    │   ├── schema.py
    │   └── crud.py
    ├── functions/
    │   ├── crawler/                     # Web Scraper (Google Reviews)
    │   └── tripadvisor/                 # TripAdvisor Image Loader
    ├── chroma/                          # ChromaDB Persistent Storage
    ├── models/                          # Downloaded HuggingFace Models
    └── .env                             # Environment Variables
```

---

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- Python 3.10+
- MySQL 8.0, MongoDB, Redis (로컬 또는 원격 서버)
- GPU 환경 권장 (ML 모델 추론)

---

### 1. Backend 실행 (`/demo`)

**환경 설정**

`demo/src/main/resources/application.properties` 파일에서 데이터베이스 접속 정보를 설정합니다.

```properties
spring.datasource.url=jdbc:mysql://<HOST>:3306/travelPick
spring.datasource.username=<USERNAME>
spring.datasource.password=<PASSWORD>

spring.data.mongodb.uri=mongodb://<HOST>:27017/travelPick

spring.data.redis.host=<HOST>
spring.data.redis.port=6379
```

**빌드 및 실행**

```bash
cd demo

# 빌드
./mvnw clean package -DskipTests

# 실행
./mvnw spring-boot:run
```

서버는 `http://localhost:8080` 에서 실행됩니다.

- GraphQL Playground: `http://localhost:8080/graphiql`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

---

### 2. Bot 실행 (`/bot`)

**환경 설정**

`bot/.env` 파일을 생성하고 아래 내용을 채웁니다.

```env
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_ID=<USERNAME>
MYSQL_PW=<PASSWORD>
MYSQL_DATABASE=travelPick

HF_TOKEN=<HuggingFace Token>   # Gemma 모델 접근 시 필요

MODEL_CACHE_DIR=models
CHROMA_PATH=chroma
```

**의존성 설치**

```bash
cd bot

pip install flask chromadb torch transformers \
            pillow sqlalchemy pymysql python-dotenv \
            selenium
```

**실행**

```bash
python server.py
```

서버는 `http://localhost:50000` 에서 실행됩니다.

| Method | Endpoint | 설명 |
|---|---|---|
| GET | `/` | Health check |
| POST | `/api/search` | 텍스트/이미지 시맨틱 검색 |
| POST | `/api/chroma` | 임베딩 저장 |
| DELETE | `/api/chroma` | 임베딩 삭제 |

---

### 실행 순서 요약

```
1. MySQL, MongoDB, Redis 서버 기동
2. bot/  → python server.py    (포트 50000)
3. demo/ → ./mvnw spring-boot:run  (포트 8080)
```
