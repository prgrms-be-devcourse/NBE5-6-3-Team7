# 데브코스 5기 6회차 3차 프로젝트
## 감정일기 "HARU"

![haru](https://github.com/user-attachments/assets/1f738454-4eb3-42eb-82c2-e179db042fc2)

>"오늘, 당신의 하루는 어땠나요?"

"HARU"는, 일기 작성 및 AI캐릭터와의 상호작용이 가능한 웹 서비스입니다.

## 👥 팀원 소개

<table>
  <tbody>
    <tr>
      <td align="center"><b>남다빈</b></td>
      <td align="center"><b>김예원</b></td>
      <td align="center"><b>정성원</b></td>
      <td align="center"><b>정기문</b></td>
      <td align="center"><b>박종욱</b></td>
     <tr/>

<tr>
      <td align="center"><a href="https://github.com/namdragonkiller"><img src="https://github.com/namdragonkiller.png" width="100px;" alt="kdu"/></a></td>
      <td align="center"><a href="https://github.com/eonwy"><img src="https://github.com/eonwy.png" width="100px;" alt="ash"/></a></td>
      <td align="center"><a href="https://github.com/oharang"><img src="https://github.com/oharang.png" width="100px;" alt="lkh"/></a></td>
      <td align="center"><a href="https://github.com/Irreplaceable-j"><img src="https://github.com/Irreplaceable-j.png" width="100px;" alt="lcr"/></a></td>
      <td align="center"><a href="https://github.com/JongWook6"><img src="https://github.com/JongWook6.png" width="100px;" alt="hyj"/></a></td>
     <tr/>

<tr>
      <td align="center"><a href="https://github.com/namdragonkiller"><sub><b>@namdragonkiller</b></sub></a><br /></td>
      <td align="center"><a href="https://github.com/eonwy"><sub><b>@eonwy</b></sub></a><br /></td>
      <td align="center"><a href="https://github.com/oharang"><sub><b>@oharang</b></sub></a><br /></td>
      <td align="center"><a href="https://github.com/Irreplaceable-j"><sub><b>@Irreplaceable-j</b></sub></a><br /></td>
      <td align="center"><a href="https://github.com/JongWook6"><sub><b>@JongWook6</b></sub></a><br /></td>
     <tr/>

  </tbody>
</table>

## 🛠 기술 스택
<div align="center">
<img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&amp;logo=spring&amp;logoColor=white">
<img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&amp;logo=springsecurity&amp;logoColor=white">
<img src="https://img.shields.io/badge/JPA-6DB33F?style=for-the-badge&amp;logo=spring&amp;logoColor=white">
<img src="https://img.shields.io/badge/QueryDSL-0096C7?style=for-the-badge&amp;logo=querydsl&amp;logoColor=white">
<img src="https://img.shields.io/badge/LangChain4j-0056D6?style=for-the-badge&amp;logo=langchain&amp;logoColor=white">
<img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&amp;logo=mysql&amp;logoColor=white">
<img src="https://img.shields.io/badge/Google_Gemini-4285F4?style=for-the-badge&amp;logo=google&amp;logoColor=white">
<img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&amp;logo=redis&amp;logoColor=white">
<img src="https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&amp;logo=thymeleaf&amp;logoColor=white">
<br>
<img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&amp;logo=apachemaven&amp;logoColor=white">
<img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&amp;logo=gradle&amp;logoColor=white">
<br>
<img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&amp;logo=html5&amp;logoColor=white">
<img src="https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&amp;logo=css3&amp;logoColor=white">
<img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&amp;logo=javascript&amp;logoColor=black">
<br>
</div>

## ⚙️ 주요기능

### 일기 조회기능
- 홈화면의 캘린더에서는 일기를 작성한 날짜에 해당 날짜의 일기에서 선택한 기분 이모지를 확인할 수 있습니다.
- 홈화면의 최근일기 카드 또는 타임라인에서 작성된 일기를 카드형식으로 확인가능합니다.
- 캘린더의 날짜 클릭, 혹은 일기 카드 클릭을 통해 해당 날짜의 일기를 조회할 수 있습니다.
- 일기 조회시 해당일기에 AI가 답장을 보냈을 경우, 답장을 확인할 수 있습니다.

### 일기 작성/수정/삭제 기능
- 일기 작성시, 5개의 이모지중 오늘의 기분에 따라 선택할 수 있습니다.
- 감정, 상황, 사람과 관련된 선택가능한 키워드들이 있습니다.
- 일기 작성시 여러장의 사진을 추가할 수 있으며, 해당 일을 대표하는 썸네일 사진선택이 가능합니다.
- 일기의 수정 및 삭제가 가능하며, 수정된 일기에 대한 답장은 받을 수 없습니다.

### 대화기능
- 일기 조회페이지에서 AI의 답장이 있는경우 대화하기 버튼을 통해 추가적으로 캐릭터와 대화가 가능합니다.
- 작성한지 일주일이 지났거나 이미 대화한 경우, 대화는 불가능합니다.

### AI 캐릭터
- 현재 4개의 캐릭터가 존재합니다.
- 신규회원의 경우 가입시 간단한 질문들을 통해 AI캐릭터를 추천받게됩니다.
- 설정 페이지에서 캐릭터와 말투, 일기의 답변길이를 변경할 수 있습니다.

### 대시보드
- 대시보드를 통해 월간/연간 기분의 흐름, 기분 분포, 작성한 일기수, 사용한 키워드 랭킹을 확인할 수 있습니다.

### 관리자
- 사용자 수, 작성 일기 수, 채팅 수, 인기 키워드, 인기 캐릭터 등을 한눈에 확인할 수 있습니다.
- 일기 키워드, AI 캐릭터를 추가/수정/삭제 할 수 있습니다.
- 특정 일기에 대한 답변을 재전송할 수 있습니다.

### AI 감정분석
- 최근 2주간 사용자의 일기로 감정 흐름을 분석합니다.
- 감정 키워드나 반복되는 정서에 대한 피드백과 인사이트를 제공합니다.
- 사용자의 감정 상황에 어울리는 문장으로 끝맺음을 합니다.

## 🔧 시스템 아키텍처
시스템은 다음과 같은 구조로 이루어져있습니다.
![haru3architecture](https://github.com/user-attachments/assets/8f514451-c7fa-42e0-8578-594a1db1eee6)

## ⛓️ ERD 구조
<img width="851" alt="team07erd" src="https://github.com/user-attachments/assets/fbfbcf68-f4f9-4d4a-92da-051850998b42" />

## 기타 작업사항
[![Notion](https://img.shields.io/badge/API명세서-000000?style=plastic&logo=Notion&logoColor=white)](https://www.notion.so/HARU-APIs-1fc3d0a9892d80a0a4f8f87b884c68fb?pvs=4)
[![Figma](https://img.shields.io/badge/UI목업-F24E1E?style=plastic&logo=Figma&logoColor=white)](https://www.figma.com/design/QGDpF4Wlg9it36HoKhp4Un/UI-Design?node-id=0-1&t=JSIrKBkVo2UJT1Nu-1)
[![Jira](https://img.shields.io/badge/일정관리-0052CC?style=plastic&logo=Jira&logoColor=white)](https://chillingcoding.atlassian.net/jira/software/projects/P3/list?atlOrigin=eyJpIjoiOTNkMjI3OWEwZTM0NDA3NTkzZDdlZDc0YTc2MTNiZDciLCJwIjoiaiJ9)
