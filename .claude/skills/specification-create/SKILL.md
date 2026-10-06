---
name: specification-create
description: docs/constitution.md(및 README.md, 대화)를 바탕으로 docs/specification.md(FR/NFR/TR ID가 붙은 기능/비기능/기술 요구사항 명세)를 작성한다. 사용자가 "명세 작성해줘", "요구사항 정리해줘", "FR/NFR/TR 뽑아줘", "specification 문서 만들어줘"라고 하거나, constitution 작성 후 다음 단계로 요구사항 명세화를 요청할 때 반드시 사용한다. docs/constitution.md가 반드시 먼저 있어야 한다.
---

# specification-create

## 목적

이 저장소는 문서를 다음 순서로 만든다: `README.md`(있다면) → `docs/constitution.md` → `docs/specification.md` → `docs/plan.md` → `docs/phaseN-todo.md`. 이 skill은 `docs/specification.md` 작성을 담당한다 — constitution에 담긴 목적/제약을 실제로 추적 가능한 **ID 붙은 요구사항 목록(FR/NFR/TR)**으로 쪼개는 것이 이 문서의 역할이다. 이후 `plan.md`와 커밋/PR 메시지가 이 ID를 참조하므로, 여기서 붙인 ID는 이후 문서의 "단일 진실 소스"가 된다.

## 실행 순서

1. `docs/constitution.md`를 읽는다.
   - 없으면 "docs/constitution.md가 없습니다. constitution-create를 먼저 실행해주세요"라고 안내하고 중단한다. (이 문서는 상위 문서 없이는 시작할 수 없다 — constitution-create와 달리 여기서는 중단 조건을 둔다.)
2. `README.md`가 있으면 읽는다. 특히 "제공하는 기능" 목록은 기능 요구사항(FR) 초안의 좋은 출발점이다.
3. `docs/specification.md`가 이미 있으면 보여주고, 새로 쓸지 / 기존 항목에 이어서 추가할지 사용자에게 확인한다.
4. 아래 순서로 채운다. 정보 출처 우선순위는 constitution-create와 동일하다 — ① 지금까지의 대화에서 이미 나온 내용 ② README/constitution에서 유추 가능한 내용 ③ 그래도 비면 질문하거나 초안을 제안해 확인받는다.
   - **기능 요구사항(FR)**: README의 "제공하는 기능"과 constitution의 목적을 기능 단위로 쪼갠다.
   - **범위 제외(Out of Scope)**: "이번엔 안 만드는 것"은 특히 대화로 확인해야 하는 항목이 많다 — constitution의 제약사항(비용/인원 등)과 연결해서 "이 제약 때문에 범위에서 뺄 기능이 있나요?"처럼 물어본다.
   - **비기능 요구사항(NFR)**: 정합성/멱등성/비용효율성/유지보수성/보안 등, constitution의 제약사항에서 직접 파생되는 항목을 먼저 제안하고 확인받는다.
   - **기술 요구사항(TR)**: 언어/프레임워크/DB/빌드도구/배포방식 등. README의 기술 스택에서 채울 수 있는 항목은 바로 채우고, 나머지(배포 방식, CI/CD, 로깅 등 README에 없는 인프라 성격 항목)는 질문한다.
   - **UI/UX**: 필요 여부를 확인한다. 백엔드 전용 저장소처럼 해당 없는 경우에도 임의로 생략하지 말고, "UI/UX는 별도 프로젝트에서 관리한다" 같은 사유를 명시적으로 확인 후 기록한다.
5. 초안을 사용자에게 보여주고 확인을 받는다.
6. 확인 후 `docs/specification.md`로 저장(또는 기존 파일에 이어쓰기)한다.
7. 저장 후 다음 단계로 자연스럽게 유도한다 — 아래 "완료 후" 참고.

## ID 부여 규칙

- FR/NFR/TR은 서로 독립된 번호 시퀀스다. 각 표 안에서 위에서부터 `FR-01`, `FR-02`... / `NFR-01`... / `TR-01`...로 순번을 매긴다.
- 이미 `docs/specification.md`가 있어 항목을 추가하는 경우, **기존 ID는 절대 바꾸지 않는다** — 커밋/PR/코드 주석에서 이미 그 ID로 참조하고 있을 수 있다. 새 항목은 각 시퀀스의 다음 번호부터 이어 붙인다.

## 템플릿

이 저장소의 실제 `docs/specification.md` 구조를 그대로 따른다:

```markdown
# Specification

각 요구사항은 고유 ID를 부여해 추적한다 (커밋/PR/코드 주석 등에서 ID로 참조 가능).

## 기능 요구사항 (Functional Requirements)

| ID | 요구사항 | 설명 |
|---|---|---|
| FR-01 | [요구사항 이름] | [설명] |

### 범위 제외 (Out of Scope)

- [이번에 만들지 않는 것]

## 비기능 요구사항 (Non-functional Requirements)

| ID | 요구사항 | 설명 |
|---|---|---|
| NFR-01 | [요구사항 이름] | [설명] |

## 기술 요구사항 및 제약사항 (Technical Requirements & Constraints)

| ID | 요구사항 | 설명 |
|---|---|---|
| TR-01 | [요구사항 이름] | [설명] |

## UI/UX

[UI/UX 요구사항, 또는 해당 없는 이유]
```

## 주의사항

- constitution.md, README, 대화에 근거 없는 요구사항을 지어내지 않는다. 특히 NFR/TR은 실제 제약(비용, 인원, 기존 시스템과의 계약 등)에서 파생되지 않은 항목을 그럴듯하게 채우기 쉬우니 주의한다 — 확신 없으면 질문한다.
- 이후 `plan.md`, `phaseN-todo.md`가 이 ID들을 계속 참조하므로, ID 형식(`FR-NN`, `NFR-NN`, `TR-NN`)을 임의로 바꾸지 않는다.

## 완료 후

`docs/specification.md` 저장이 끝나면, 같은 응답에서 바로 다음 단계로 이어갈지 물어본다: "이 명세를 바탕으로 Phase별 구현 계획(`docs/plan.md`)을 작성할까요?" 사용자가 동의하면 `plan-create` skill로 이어간다. 저장했다는 말로 끝내지 않는다.
