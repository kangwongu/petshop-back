---
name: plan-create
description: docs/constitution.md와 docs/specification.md(FR/NFR/TR ID)를 바탕으로 docs/plan.md(요구사항을 Phase별 구현 순서로 정리한 계획)를 작성한다. 사용자가 "plan 작성해줘", "phase 나눠줘", "구현 계획 세워줘", "이 요구사항들을 어떤 순서로 구현할지 정리해줘"라고 할 때 반드시 사용한다. docs/constitution.md와 docs/specification.md가 모두 먼저 있어야 한다.
---

# plan-create

## 목적

이 저장소는 문서를 다음 순서로 만든다: `README.md`(있다면) → `docs/constitution.md` → `docs/specification.md` → `docs/plan.md` → `docs/phaseN-todo.md`. 이 skill은 `docs/plan.md` 작성을 담당한다 — specification.md에 나열된 FR/NFR/TR을 **어떤 순서로, 어떤 단위(Phase)로 구현할지**로 조직화하는 것이 이 문서의 역할이다. 이후 `phaseN-todo.md`들이 이 문서의 Phase 구조를 그대로 이어받는다.

## 실행 순서

1. `docs/constitution.md`를 읽는다. 없으면 "docs/constitution.md가 없습니다. constitution-create를 먼저 실행해주세요"라고 안내하고 중단한다.
2. `docs/specification.md`를 읽는다. 없으면 "docs/specification.md가 없습니다. specification-create를 먼저 실행해주세요"라고 안내하고 중단한다.
3. `docs/plan.md`가 이미 있으면 보여주고, 새로 쓸지 / 기존 내용을 수정할지 확인한다.
4. **도메인/기능 의존 순서를 먼저 파악해 제안한다.** specification.md의 FR들을 훑어 "이 기능이 저 기능을 참조하니 저게 먼저 되어야 한다" 같은 자연스러운 의존관계를 스스로 찾아내 제안하고, 사용자에게 맞는지 확인받는다. 그냥 "순서가 뭔가요?"라고만 묻지 않는다 — 이미 요구사항에 단서가 있는 경우가 많다.
5. **Phase 개수와 범위를 함께 정한다.** 정해진 개수는 없다. 요구사항 규모/의존관계를 보고 몇 개 Phase로 나눌지 초안을 제안하고, 사용자와 함께 확정한다.
6. 확정된 Phase 구조에 맞춰 각 Phase의 작업 항목을 적되, 관련된 FR/NFR/TR ID를 반드시 함께 인용한다.
7. 각 Phase 끝에 **완료 기준**을 문장으로 명시한다 — 비워두지 않는다. "무엇이 되면 이 Phase가 끝난 것인지"가 없으면 다음 Phase로 못 넘어간다.
8. 이 프로젝트만의 **구현 원칙**(진행 상황 추적 기준, 순차 진행 여부, "세로 슬라이스 완성 후 다음으로" 같은 방식)을 대화로 확인해 문서 상단에 정리한다.
9. 초안을 사용자에게 보여주고 확인을 받는다.
10. 확인 후 `docs/plan.md`로 저장한다.
11. 저장 후 다음 단계로 자연스럽게 유도한다 — 아래 "완료 후" 참고.

## ID 참조 규칙

- `specification.md`에서 이미 확정된 FR/NFR/TR ID를 그대로 인용한다. 새 ID를 만들거나 번호를 바꾸지 않는다.
- specification.md에 없는 요구사항을 이 단계에서 새로 지어내지 않는다. 계획을 짜다가 빠진 요구사항이 발견되면, 그 자리에서 만들어 채우지 말고 "이 요구사항이 specification.md에 빠진 것 같은데, 먼저 추가할까요?"라고 물어 `specification-create`로 돌아가게 한다 — 상위 문서와 하위 문서가 어긋나는 것을 막기 위해서다.

## 템플릿

이 저장소의 실제 `docs/plan.md` 구조를 그대로 따른다:

```markdown
# Implementation Plan

이 문서는 `constitution.md`(목적/성공 기준/제약사항)와 `specification.md`(요구사항, FR/NFR/TR ID)를 바탕으로 실제 구현 순서와 방식을 정의한다.

## 구현 원칙

- [진행 상황 추적 기준]
- [도메인/기능 의존 순서]
- [Phase 진행 방식 — 순차 진행 여부 등]

## Phase 0. [Phase 이름]

- [작업 항목] — [관련 FR/NFR/TR ID]
- [작업 항목] — [관련 FR/NFR/TR ID]

**완료 기준**: [이 Phase가 끝났다고 판단할 구체적 기준]

## Phase 1. [Phase 이름]
...
```

## 주의사항

- Phase 순서/이름을 사용자 확인 없이 임의로 정하지 않는다. 규모가 작으면 Phase가 2~3개일 수도, 크면 6개 이상일 수도 있다 — 정해진 정답 개수가 없다.
- 완료 기준 없는 Phase를 만들지 않는다.

## 완료 후

`docs/plan.md` 저장이 끝나면, 같은 응답에서 바로 다음 단계로 이어갈지 물어본다: "Phase 0부터 상세 작업 목록(`docs/phase0-todo.md`)을 작성할까요?" 사용자가 동의하면 `phase-todo-create` skill로 이어간다. 저장했다는 말로 끝내지 않는다.
