# Node.js 이미지
FROM node:16 AS build

# 애플리케이션 소스 코드 복사
COPY . /app

# 작업 디렉토리 설정
WORKDIR /app

# 의존성 설치
RUN npm install

# 환경 변수 설정
ENV NODE_ENV=production

# Vue.js 애플리케이션 빌드
RUN npm run build

# 실제 런타임 (Nginx 설정)
FROM nginx:alpine

# 빌드된 파일을 Nginx의 기본 디렉토리에 복사
COPY --from=build /app/dist /usr/share/nginx/html

# Nginx 80번 포트로 서비스
EXPOSE 80