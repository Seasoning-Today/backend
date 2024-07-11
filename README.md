## 서비스 소개
24절기에 맞춰 기록장을 작성하고 친구와 공유할 수 있는 일기 플랫폼입니다.

<br>

## 아키텍처
![Untitled](https://github.com/user-attachments/assets/e23fb589-5682-4bf3-b271-10ab0bc8dc43)

<br>

## 프로젝트 진행 내용

#### 1. 웹 서버 스캐닝 차단
- 액세스 로그 분석 과정에서 해킹 공격에 악용될 수 있는 웹 서버 스캐닝 요청이 들어오는 것을 발견했습니다.
- 이에 대응하고자 AWS WAF를 도입하여 API 엔드포인트 외의 요청은 전부 차단하는 화이트리스트 방식의 ACL을 적용했습니다.


#### 2. 일기 내용 암호화
- 서비스 사용자로부터 일기 내용이 개발자에게 노출될까봐 걱정된다는 의견을 전해들었습니다..
- 이에 AES-128 알고리즘을 바탕으로 일기 본문에 암호화를 적용한 후, 공지사항에 해당 내용을 게시함으로써 사용자의 불안감을 완화하고자 노력했습니다.

#### 3. 썸네일 생성을 통한 이미지 로딩 속도 개선
- 네트워크 속도가 느린 모바일 환경에서 프로필 이미지가 끊기면서 로딩되는 현상이 발생했습니다.
- 이에 AWS S3와 Lambda 연동을 통해 썸네일 이미지를 비동기적으로 생성함으로써 이미지 전송 시간을 개선했습니다. (

#### 4. 이벤트를 활용한 트랜잭션 분리
- 스프링 이벤트를 활용하여 클래스간의 결합도를 낮추고, 단일 책임 원칙을 준수하고자 노력했습니다.

<br>

## 관련 포스팅
- [AWS WAF를 활용한 봇 공격 차단 (feat. web ACL)](https://csct3434.tistory.com/202)
- [기록장 암호화 적용 #102](https://github.com/Seasoning-Today/backend/pull/102)
- [Amazon S3 트리거와 Lambda를 활용한 썸네일 이미지 생성 (2) - 코드 구현](https://csct3434.tistory.com/189)
- [회원가입 시 자동으로 공식 계정 친구 추가 #113](https://github.com/Seasoning-Today/backend/pull/113)
- [첫 오픈소스 기여 (feat.AssertJ)]() : 테스트 코드 작성 도중, AssertJ의 Javadoc에서 오타를 발견하여 이를 정정했습니다.
- [페이지네이션 성능 비교 : LIMIT-OFFSET vs NO-OFFSET](https://csct3434.tistory.com/168)
- [중앙 집중식 로깅 구현하기 (feat. Logback, CloudWatch Logs)](https://csct3434.tistory.com/183)

<br>

## 팀원
<table>
    <tr align="center">
        <td><strong><a href='https://github.com/swim-kim' target='_blank'>김수영</a></strong></td>
        <td><strong><a href='https://github.com/seunghyeonkang' target='_blank'>강승현</a></strong></td>
        <td><strong><a href='https://github.com/poodlepoodle' target='_blank'>최어진</a></strong></td>
        <td><strong><a href='https://github.com/sem-git' target='_blank'>이세민</a></strong></td>
        <td><strong><a href='https://github.com/csct3434' target='_blank'>김동철</a></strong></td>
        <td><strong><a href='https://github.com/linavell' target='_blank'>이아린</a></strong></td>
    </tr>
    <tr align="center">
        <td>PM</td>
        <td>Design</td>
        <td>Frontend</td>
        <td>Frontend</td>
        <td>Backend</td>
        <td>Backend</td>
    </tr>
    <tr align="center">
        <td>
            <img src="https://github.com/swim-kim.png" width="100" />
        </td>
        <td>
            <img src="https://github.com/seunghyeonkang.png" width="100" />
        </td>
        <td>
            <img src="https://github.com/poodlepoodle.png" width="100" />
        </td>
        <td>
            <img src="https://github.com/sem-git.png" width="100" />
        </td>
        <td>
            <img src="https://github.com/csct3434.png" width="100" />
        </td>
        <td>
            <img src="https://github.com/linavell.png" width="100" />
        </td>
    </tr>
</table>
