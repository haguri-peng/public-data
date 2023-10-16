# public-data

### OpenAPI를 통해 [공공데이터포털](https://www.data.go.kr/) 에서 제공하는 데이터를 가져옵니다.

현재 구현된 데이터

* 고속버스 정보
* 시외버스 정보
* 법정동코드

[고속버스]
* 고속버스 도시코드
* 고속버스 등급
* 고속버스 터미널
* 출/도착지기반 고속버스 정보

[시외버스]
* 시외버스 도시코드
* 시외버스 등급
* 시외버스 터미널
* 출/도착지기반 시외버스 정보

[법정동코드]
* 행정안전부 법정동코드 CSV 파일로 생성
* 국토교통부 법정동코드

### 프로그램 실행 시 필수 사항

/src/main/resources/myInfo.properties 파일 내에 개인적으로 발급받은 serviceKey 값이 아래와 같이 세팅되어야 합니다.  
(myInfo-sample.properties 파일 참조)

MyInfo.serviceKey=EOvAuxl...

### Libraries

* JDK 1.8
* WebClient
* JSON.simple

