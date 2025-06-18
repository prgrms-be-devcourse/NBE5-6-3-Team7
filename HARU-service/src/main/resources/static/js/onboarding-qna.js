// 기존 로직 + UI 개선사항
let ai_id = null;
let is_formal = 0;
let is_long = 0;
let answer = Array(4).fill(0);
let currentQuestion = 1;
const totalQuestions = 4;

// 질문 데이터
const questions = {
  1: {
    title: "감정이 힘들 때<br>어떤 대화 방식을 원하시나요?",
    description: "AI와의 대화 스타일을 맞춤 설정하기 위한 질문입니다.",
    buttons: [1, 2]
  },
  2: {
    title: "갑작스런 일이 생겼을 때<br>나는...",
    description: "예상치 못한 상황에서의 대처 방식을 알려주세요.",
    buttons: [3, 4]
  },
  3: {
    title: "AI가 말을 걸 때<br>어떤 말투가 더 편하신가요?",
    description: "선호하는 대화 톤을 선택해주세요.",
    buttons: [5, 6]
  },
  4: {
    title: "하루의 이야기를 나눌 때<br>AI의 답변은...",
    description: "응답의 길이와 스타일을 선택해주세요.",
    buttons: [7, 8]
  }
};

// 진행률 업데이트
function updateProgress(current) {
  const progressFill = document.getElementById('progressFill');
  const progressText = document.getElementById('progressText');
  const percentage = (current / totalQuestions) * 100;

  progressFill.style.width = percentage + '%';
  progressText.textContent = current + ' / ' + totalQuestions;
}

// 버튼 애니메이션 - 더 빠른 애니메이션
function animateButtons() {
  const buttons = document.querySelectorAll('.option-btn:not([style*="display: none"])');
  buttons.forEach((button, index) => {
    button.style.opacity = '0';
    button.style.transform = 'translateY(20px)';

    setTimeout(() => {
      button.style.transition = 'all 0.3s ease'; // 0.5s에서 0.3s로 단축
      button.style.opacity = '1';
      button.style.transform = 'translateY(0)';
    }, index * 50); // 100ms에서 50ms로 단축
  });
}

// 질문 변경 애니메이션 - 더 빠른 전환
function changeQuestion(questionNumber) {
  const questionElement = document.getElementById('question');
  const descriptionElement = document.getElementById('questionDescription');
  const questionData = questions[questionNumber];

  if (!questionData) return;

  // 페이드 아웃 - 더 빠르게
  questionElement.style.opacity = '0';
  descriptionElement.style.opacity = '0';

  setTimeout(() => {
    questionElement.innerHTML = questionData.title;
    descriptionElement.textContent = questionData.description;

    // 버튼 표시/숨김
    const allButtons = document.querySelectorAll('.option-btn');
    allButtons.forEach(button => button.style.display = 'none');

    questionData.buttons.forEach(buttonIndex => {
      allButtons[buttonIndex - 1].style.display = 'block';
    });

    // 페이드 인 - 더 빠르게
    setTimeout(() => {
      questionElement.style.opacity = '1';
      descriptionElement.style.opacity = '1';
      animateButtons();
    }, 50); // 100ms에서 50ms로 단축

  }, 150); // 300ms에서 150ms로 단축

  updateProgress(questionNumber);

  // 뒤로가기 버튼 표시 (2번째 질문부터)
  const backBtn = document.getElementById('backBtn');
  if (questionNumber > 1) {
    backBtn.style.display = 'inline-flex';
  } else {
    backBtn.style.display = 'none';
  }
}

// 로딩 표시
function showLoading() {
  document.getElementById('loadingOverlay').style.display = 'flex';
}

// 메인 클릭 핸들러 (기존 로직 수정)
function handleClick(option) {
  console.log('handleClick 호출됨:', option); // 디버그용

  const buttons = document.querySelectorAll(".option-btn");

  // 클릭된 버튼에 선택 효과
  const clickedButton = buttons[option - 1];
  if (clickedButton) {
    clickedButton.style.transform = 'scale(0.95)';
    setTimeout(() => {
      clickedButton.style.transform = '';
    }, 150);
  }

  if (option === 1 || option === 2) {
    console.log('1번째 질문 완료, 2번째 질문으로 이동'); // 디버그용
    currentQuestion = 2;
    if (option === 1) {
      answer[0] = 1;
    }
    changeQuestion(2);

  } else if (option === 3 || option === 4) {
    console.log('2번째 질문 완료, 3번째 질문으로 이동'); // 디버그용
    currentQuestion = 3;
    if (option === 3) {
      answer[1] = 1;
    }
    changeQuestion(3);

  } else if (option === 5 || option === 6) {
    console.log('3번째 질문 완료, 4번째 질문으로 이동'); // 디버그용
    currentQuestion = 4;
    if (option === 5) {
      answer[2] = 1;
      is_formal = 1;
    }
    changeQuestion(4);

  } else if (option === 7 || option === 8) {
    console.log('4번째 질문 완료, AI 추천 시작'); // 디버그용
    if (option === 7) {
      answer[3] = 1;
      is_long = 1;
    }

    // AI 추천 로직 (기존과 동일)
    if (answer[0] === 1 && answer[1] === 1) {
      ai_id = 1;
    } else if (answer[0] === 1 && answer[1] === 0) {
      ai_id = 2;
    } else if (answer[0] === 0 && answer[1] === 1) {
      ai_id = 3;
    } else if (answer[0] === 0 && answer[1] === 0) {
      ai_id = 4;
    }

    console.log('추천된 AI ID:', ai_id); // 디버그용

    // 로딩 표시
    showLoading();

    // API 호출 (기존과 동일)
    fetch('/api/member/onboarding-ai', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
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
        // 약간의 지연 후 리다이렉트 (로딩 효과)
        setTimeout(() => {
          window.location.href = data.redirect;
        }, 1000); // 1500ms에서 1000ms로 단축
      } else {
        console.error("리다이렉트 정보 없음", data);
        // 로딩 숨김
        document.getElementById('loadingOverlay').style.display = 'none';
      }
    })
    .catch(error => {
      console.error('AI 추천 요청 실패:', error);
      // 로딩 숨김
      document.getElementById('loadingOverlay').style.display = 'none';

      // 에러 처리 UI (선택사항)
      alert('오류가 발생했습니다. 다시 시도해주세요.');
    });
  }
}

// 뒤로가기 기능
function goBack() {
  if (currentQuestion > 1) {
    currentQuestion--;
    changeQuestion(currentQuestion);

    // 답변 초기화
    if (currentQuestion === 1) {
      answer[0] = 0;
    } else if (currentQuestion === 2) {
      answer[1] = 0;
    } else if (currentQuestion === 3) {
      answer[2] = 0;
      is_formal = 0;
    }
  }
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
  console.log('페이지 로드 완료'); // 디버그용

  updateProgress(1);
  animateButtons();

  // 배경 장식 요소들의 랜덤한 애니메이션
  const decorations = document.querySelectorAll('.bg-decoration');
  decorations.forEach((decoration, index) => {
    const randomDelay = Math.random() * 2;
    decoration.style.animationDelay = `${randomDelay}s`;
  });
});

// 키보드 지원
document.addEventListener('keydown', function(e) {
  // 숫자 키로 옵션 선택
  if (e.key >= '1' && e.key <= '8') {
    const buttonIndex = parseInt(e.key) - 1;
    const buttons = document.querySelectorAll('.option-btn');
    if (buttons[buttonIndex] && buttons[buttonIndex].style.display !== 'none') {
      buttons[buttonIndex].click();
    }
  }

  // ESC 키로 뒤로가기
  if (e.key === 'Escape' && currentQuestion > 1) {
    goBack();
  }
});

// 터치 제스처 지원 (모바일)
let touchStartY = 0;
let touchEndY = 0;

document.addEventListener('touchstart', function(e) {
  touchStartY = e.changedTouches[0].screenY;
});

document.addEventListener('touchend', function(e) {
  touchEndY = e.changedTouches[0].screenY;
  handleSwipe();
});

function handleSwipe() {
  const swipeThreshold = 50;
  const swipeDistance = touchStartY - touchEndY;

  // 아래로 스와이프 (이전 단계)
  if (swipeDistance < -swipeThreshold && currentQuestion > 1) {
    goBack();
  }
}

// 페이지 이탈 시 확인 (선택사항)
window.addEventListener('beforeunload', function(e) {
  if (currentQuestion > 1 && currentQuestion < totalQuestions) {
    e.preventDefault();
    e.returnValue = '설정이 완료되지 않았습니다. 정말 나가시겠습니까?';
  }
});

// 접근성 개선 - 스크린 리더 지원
function announceQuestion(questionNumber) {
  const questionData = questions[questionNumber];
  if (questionData && window.speechSynthesis) {
    const utterance = new SpeechSynthesisUtterance(
        `질문 ${questionNumber}. ${questionData.title.replace(/<br>/g, ' ')}`
    );
    utterance.lang = 'ko-KR';
    utterance.rate = 0.8;
    utterance.volume = 0.8;

    // 음성 안내 사용 여부는 사용자 설정에 따라
    // speechSynthesis.speak(utterance);
  }
}

// 애니메이션 성능 최적화
function optimizeAnimations() {
  // Reduce motion 설정 확인
  const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)');

  if (prefersReducedMotion.matches) {
    // 애니메이션 간소화
    const style = document.createElement('style');
    style.textContent = `
      *, *::before, *::after {
        animation-duration: 0.01ms !important;
        animation-iteration-count: 1 !important;
        transition-duration: 0.01ms !important;
      }
    `;
    document.head.appendChild(style);
  }
}

// 초기화 시 최적화 적용
document.addEventListener('DOMContentLoaded', function() {
  optimizeAnimations();
});

// 디버그 모드 (개발용)
const DEBUG_MODE = true; // 디버그 모드 활성화

if (DEBUG_MODE) {
  console.log('온보딩 QnA 디버그 모드 활성화');

  // 전역 함수로 상태 확인
  window.getOnboardingState = function() {
    return {
      currentQuestion,
      answers: answer,
      ai_id,
      is_formal,
      is_long
    };
  };

  // 특정 질문으로 바로 이동 (테스트용)
  window.goToQuestion = function(questionNumber) {
    if (questionNumber >= 1 && questionNumber <= totalQuestions) {
      currentQuestion = questionNumber;
      changeQuestion(questionNumber);
    }
  };

  // 수동으로 handleClick 테스트
  window.testClick = function(option) {
    console.log('테스트 클릭:', option);
    handleClick(option);
  };
}