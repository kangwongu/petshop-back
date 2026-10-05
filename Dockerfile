# ---- 1단계: 빌드 스테이지 ----
# JDK(컴파일에 필요한 도구 포함) 이미지를 build 단계에서만 쓰고,
# 최종 이미지에는 포함하지 않는다(멀티스테이지 빌드) -> 이미지 용량을 줄이기 위함
FROM eclipse-temurin:25-jdk-jammy AS build
WORKDIR /app

# gradle 설정 파일만 먼저 복사해서 의존성을 받아둔다.
# src 코드보다 먼저 복사하는 이유: 소스 코드만 바뀌고 의존성(build.gradle)이 안 바뀌면
# 이 레이어는 Docker 캐시를 그대로 재사용해서 빌드 속도가 빨라진다.
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN ./gradlew dependencies --no-daemon

# 이제 소스 코드를 복사하고 실제 빌드 수행
COPY src ./src
# -x test: 테스트는 CI(GitHub Actions)에서 별도로 돌리므로 이미지 빌드 시에는 생략
RUN ./gradlew clean bootJar -x test --no-daemon

# ---- 2단계: 실행 스테이지 ----
# 실행에는 JDK가 아니라 JRE만 있으면 충분하다 -> 이미지 용량/공격 표면 축소
FROM eclipse-temurin:25-jre-jammy
WORKDIR /app

# 컨테이너 안에서 root로 애플리케이션을 띄우지 않기 위한 전용 유저 생성
# (root로 실행 중 컨테이너가 뚫리면 호스트 침해 범위가 커짐)
RUN useradd --system --no-create-home appuser
USER appuser

# 빌드 스테이지에서 만들어진 jar만 가져온다 (소스/gradle 캐시 등은 최종 이미지에 남지 않음)
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

# prod 프로파일 고정 실행 — 로컬/테스트 프로파일로 뜨는 걸 방지
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
