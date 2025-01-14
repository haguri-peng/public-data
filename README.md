# public-data

[공공데이터포털](https://www.data.go.kr/)에서 제공하는 <mark>OpenAPI</mark>를 통해 데이터를 가져옵니다.

## 구현된 항목

`고속버스`

- 고속버스 도시코드
- 고속버스 등급
- 고속버스 터미널
- 출/도착지기반 고속버스 정보

`시외버스`

- 시외버스 도시코드
- 시외버스 등급
- 시외버스 터미널
- 출/도착지기반 시외버스 정보

`법정동코드`

- 행정안전부 법정동코드 CSV 파일로 생성
- 국토교통부 법정동코드

## 프로그램 실행 시 필수사항

- [/src/main/resources/myInfo.properties](/src/main/resources/myInfo.properties) 파일 내에 발급받은 Service key가 아래와 같이 세팅되어야 합니다.  
  <u>(MyInfo.serviceKey=your_service_key)</u>

## Libraries

* JDK 17
* WebClient
* Gson

## Logs

- 콘솔에는 DEBUG, 파일에는 INFO 모드로 로그를 세팅합니다.
- 로그는 `logs` 폴더 내에 **logback.log** 파일로 저장됩니다.
- 로그 파일은 매일 rollover 되며, `logFile.{yyyy-MM-dd}.log` 형식으로 이름이 변경됩니다.
- 관련 내용은 [logback-spring.xml](/src/main/resources/logback-spring.xml) 파일의 내용을 참조하면 됩니다.
