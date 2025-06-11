let ai_id = null; // 추천 ai 넘버링
let is_formal = 0; // 어조
let is_long = 0; // 길이감
let answer = Array(4).fill(0);

function handleClick(option) {

  const buttons = document.querySelectorAll(".option-btn");
   // 질문 4개 저장 할 답변 배열
  // 질문 바꾸기
  if (option === 1 || option === 2) {
    document.getElementById("question").innerHTML = "갑작스런 일이 생겼을 때<br>나는...";
    // 기존 버튼(0, 1) 숨기기
    buttons[0].style.display = "none";
    buttons[1].style.display = "none";

    // 새 버튼(2, 3) 보이기
    buttons[2].style.display = "block";
    buttons[3].style.display = "block";
    if(option === 1) {
      answer[0] = 1;
    }
  } else if(option === 3 || option === 4){
    document.getElementById("question").innerHTML = "AI가 말을 걸 때<br>어떤 말투가 더 편하신가요?";
    buttons[2].style.display = "none";
    buttons[3].style.display = "none";

    buttons[4].style.display = "block";
    buttons[5].style.display = "block";

    if(option === 3) {
      answer[1] = 1;
    }
  } else if(option === 5 || option === 6) {
    document.getElementById("question").innerHTML = "하루의 이야기를 나눌 때<br>AI의 답변은...";
    buttons[4].style.display = "none";
    buttons[5].style.display = "none";

    buttons[6].style.display = "block";
    buttons[7].style.display = "block";

    if(option === 5) {
      answer[2] = 1;
      is_formal = 1;
    }
  } else if(option === 7 || option === 8) {
    if (option === 7) {
      answer[3] = 1;
      is_long = 1;
    }

    // answer boolean 배열 값에 따라 ai 추천
    // 버튼 1, 3 => 다정한오소리 1 , true true
    if (answer[0] === 1 && answer[1] === 1) {
      ai_id = 1;
    } else if (answer[0] === 1 && answer[1] === 0) {
      // 버튼 1, 4 => 친절한 두루미 2  true false
      ai_id = 2;
    } else if (answer[0] === 0 && answer[1] === 1) {
      // 버튼 2, 3 => 용감한 사자 3   false true
      ai_id = 3;
    } else if (answer[0] === 0 && answer[1] === 0) {
      // 버튼 2, 4 => 용맹한 거북이 4  false false
      ai_id = 4;
    }
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

    fetch('/api/member/onboarding-ai', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        [csrfHeader]: csrfToken
      },
      body: JSON.stringify({
        aiId: ai_id,
        isFormal: is_formal,
        isLong: is_long
      })
    })
    .then(response => response.json())
    .then(data => {
      if (data.redirect) {
        window.location.href = data.redirect;
      } else {
        console.error("리다이렉트 정보 없음", data);
      }
    })
    .catch(error => {
      console.error('AI 추천 요청 실패:', error);
    });
  }

}