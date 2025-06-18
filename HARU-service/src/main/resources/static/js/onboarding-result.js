// 페이지 로드 시 애니메이션 및 초기화
document.addEventListener('DOMContentLoaded', function() {
  initializePage();
  setupEventListeners();
  setupKeyboardSupport();
  setupAccessibility();
});

// 페이지 초기화
function initializePage() {
  // 성공 애니메이션 시퀀스
  showSuccessAnimation();

  // 배경 장식 요소들 애니메이션
  animateBackgroundDecorations();

  // 페이지 성능 최적화
  optimizeAnimations();
}

// 성공 애니메이션 표시
function showSuccessAnimation() {
  const successAnimation = document.getElementById('successAnimation');
  const container = document.querySelector('.container');

  // 성공 애니메이션 표시
  setTimeout(() => {
    successAnimation.style.display = 'flex';
    playSuccessSound(); // 선택사항
  }, 500);

  // 성공 애니메이션 숨김
  setTimeout(() => {
    successAnimation.style.display = 'none';
  }, 2500);

  // 메인 콘텐츠 페이드 인
  setTimeout(() => {
    container.classList.add('show');
  }, 1000);
}

// 배경 장식 요소 애니메이션
function animateBackgroundDecorations() {
  const decorations = document.querySelectorAll('.bg-decoration');
  decorations.forEach((decoration, index) => {
    const randomDelay = Math.random() * 2;
    const randomDuration = 8 + Math.random() * 4; // 8-12초 사이
    decoration.style.animationDelay = `${randomDelay}s`;
    decoration.style.animationDuration = `${randomDuration}s`;
  });
}

// 이벤트 리스너 설정
function setupEventListeners() {
  // 시작 버튼
  const startBtn = document.getElementById('startBtn');
  if (startBtn) {
    startBtn.addEventListener('click', handleStartClick);
  }

  // 다른 AI 확인 버튼
  const checkOtherBtn = document.getElementById('checkOtherBtn');
  if (checkOtherBtn) {
    checkOtherBtn.addEventListener('click', handleCheckOtherClick);
  }

  // AI 카드 호버 효과
  const aiPreviewCard = document.querySelector('.ai-preview-card');
  if (aiPreviewCard) {
    aiPreviewCard.addEventListener('mouseenter', handleCardHover);
    aiPreviewCard.addEventListener('mouseleave', handleCardLeave);
  }

  // 폼 제출 이벤트
  const aiForm = document.getElementById('aiForm');
  if (aiForm) {
    aiForm.addEventListener('submit', handleFormSubmit);
  }
}

// 시작 버튼 클릭 핸들러
function handleStartClick(e) {
  const button = e.currentTarget;

  // 버튼 클릭 효과
  button.classList.add('clicked');

  // 햅틱 피드백 (지원되는 기기에서)
  if (navigator.vibrate) {
    navigator.vibrate(50);
  }

  // 로딩 표시
  setTimeout(() => {
    showLoading('AI 친구를 준비하고 있어요...', '곧 만나실 수 있어요!');
  }, 300);
}

// 다른 AI 확인 버튼 클릭 핸들러
function handleCheckOtherClick(e) {
  e.preventDefault();
  const button = e.currentTarget;

  // 버튼 클릭 효과
  button.classList.add('clicked');

  const form = document.getElementById("aiForm");
  if (!form) {
    console.error('Form not found');
    return;
  }

  const formData = new FormData(form);

  // 로딩 표시
  showLoading('다른 AI들을 불러오고 있어요...', '잠시만 기다려주세요');

  // API 호출
  fetch(form.action, {
    method: "POST",
    body: formData
  })
  .then(response => {
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return response.json ? response.json() : response;
  })
  .then(data => {
    // 성공 시 페이지 이동
    setTimeout(() => {
      window.location.href = "/app/settings/ai";
    }, 800);
  })
  .catch(error => {
    console.error("오류 발생:", error);
    hideLoading();

    // 사용자 친화적 에러 메시지
    showErrorMessage('일시적인 오류가 발생했습니다. 다시 시도해주세요.');

    // 버튼 상태 복구
    button.classList.remove('clicked');
  });
}

// 폼 제출 핸들러
function handleFormSubmit(e) {
  // 기본 제출은 그대로 진행
  // 추가 로직이 필요한 경우 여기에 구현
}

// AI 카드 호버 효과
function handleCardHover(e) {
  const circle = e.currentTarget.querySelector('.circle');
  if (circle) {
    circle.style.transform = 'scale(1.02)';
    circle.style.transition = 'transform 0.3s ease';
  }
}

function handleCardLeave(e) {
  const circle = e.currentTarget.querySelector('.circle');
  if (circle) {
    circle.style.transform = 'scale(1)';
  }
}

// 키보드 지원 설정
function setupKeyboardSupport() {
  document.addEventListener('keydown', function(e) {
    // Enter 키로 시작하기
    if (e.key === 'Enter' && !e.ctrlKey && !e.metaKey && e.target === document.body) {
      e.preventDefault();
      const startBtn = document.getElementById('startBtn');
      if (startBtn && !startBtn.disabled) {
        startBtn.click();
      }
    }

    // Space 키로 다른 AI 확인
    if (e.key === ' ' && e.target === document.body) {
      e.preventDefault();
      const checkOtherBtn = document.getElementById('checkOtherBtn');
      if (checkOtherBtn && !checkOtherBtn.disabled) {
        checkOtherBtn.click();
      }
    }

    // ESC 키로 로딩 취소 (가능한 경우)
    if (e.key === 'Escape') {
      const loadingOverlay = document.getElementById('loadingOverlay');
      if (loadingOverlay && loadingOverlay.style.display === 'flex') {
        hideLoading();
      }
    }
  });
}

// 접근성 설정
function setupAccessibility() {
  // 스크린 리더를 위한 라이브 영역 설정
  const liveRegion = document.createElement('div');
  liveRegion.setAttribute('aria-live', 'polite');
  liveRegion.setAttribute('aria-atomic', 'true');
  liveRegion.className = 'sr-only';
  liveRegion.style.position = 'absolute';
  liveRegion.style.width = '1px';
  liveRegion.style.height = '1px';
  liveRegion.style.padding = '0';
  liveRegion.style.margin = '-1px';
  liveRegion.style.overflow = 'hidden';
  liveRegion.style.clip = 'rect(0, 0, 0, 0)';
  liveRegion.style.whiteSpace = 'nowrap';
  liveRegion.style.border = '0';
  document.body.appendChild(liveRegion);

  // 상태 변경 시 스크린 리더에 알림
  window.announceToScreenReader = function(message) {
    liveRegion.textContent = message;
  };
}

// 로딩 표시
function showLoading(title = 'AI 친구를 준비하고 있어요...', subtitle = '곧 만나실 수 있어요!') {
  const loadingOverlay = document.getElementById('loadingOverlay');
  const loadingTitle = loadingOverlay.querySelector('h3');
  const loadingSubtitle = loadingOverlay.querySelector('p');

  if (loadingTitle) loadingTitle.textContent = title;
  if (loadingSubtitle) loadingSubtitle.textContent = subtitle;

  loadingOverlay.style.display = 'flex';

  // 스크린 리더에 알림
  if (window.announceToScreenReader) {
    window.announceToScreenReader(title);
  }

  // 버튼 비활성화
  disableButtons();
}

// 로딩 숨김
function hideLoading() {
  const loadingOverlay = document.getElementById('loadingOverlay');
  loadingOverlay.style.display = 'none';

  // 버튼 활성화
  enableButtons();
}

// 버튼 비활성화
function disableButtons() {
  const buttons = document.querySelectorAll('.start-btn, .check-other-btn');
  buttons.forEach(button => {
    button.disabled = true;
    button.style.opacity = '0.6';
    button.style.cursor = 'not-allowed';
  });
}

// 버튼 활성화
function enableButtons() {
  const buttons = document.querySelectorAll('.start-btn, .check-other-btn');
  buttons.forEach(button => {
    button.disabled = false;
    button.style.opacity = '1';
    button.style.cursor = 'pointer';
    button.classList.remove('clicked');
  });
}

// 에러 메시지 표시
function showErrorMessage(message) {
  // 기존 에러 메시지 제거
  const existingError = document.querySelector('.error-notification');
  if (existingError) {
    existingError.remove();
  }

  // 새 에러 메시지 생성
  const errorDiv = document.createElement('div');
  errorDiv.className = 'error-notification';
  errorDiv.innerHTML = `
    <div class="error-content">
      <span class="error-icon">⚠️</span>
      <span class="error-text">${message}</span>
      <button class="error-close" onclick="this.parentElement.parentElement.remove()">×</button>
    </div>
  `;

  // CSS 스타일 추가
  errorDiv.style.cssText = `
    position: fixed;
    top: 20px;
    left: 50%;
    transform: translateX(-50%);
    background: rgba(220, 38, 38, 0.95);
    color: white;
    padding: 0;
    border-radius: 12px;
    box-shadow: 0 10px 30px rgba(220, 38, 38, 0.3);
    z-index: 1001;
    animation: slideDown 0.3s ease;
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2);
  `;

  // 에러 내용 스타일
  const errorContent = errorDiv.querySelector('.error-content');
  errorContent.style.cssText = `
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 16px 20px;
  `;

  // 닫기 버튼 스타일
  const closeBtn = errorDiv.querySelector('.error-close');
  closeBtn.style.cssText = `
    background: none;
    border: none;
    color: white;
    font-size: 18px;
    cursor: pointer;
    padding: 0;
    width: 24px;
    height: 24px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: background 0.2s ease;
  `;

  closeBtn.addEventListener('mouseenter', () => {
    closeBtn.style.background = 'rgba(255, 255, 255, 0.2)';
  });

  closeBtn.addEventListener('mouseleave', () => {
    closeBtn.style.background = 'none';
  });

  // DOM에 추가
  document.body.appendChild(errorDiv);

  // 스크린 리더에 알림
  if (window.announceToScreenReader) {
    window.announceToScreenReader(`오류: ${message}`);
  }

  // 3초 후 자동 제거
  setTimeout(() => {
    if (errorDiv.parentNode) {
      errorDiv.style.animation = 'slideUp 0.3s ease';
      setTimeout(() => {
        if (errorDiv.parentNode) {
          errorDiv.remove();
        }
      }, 300);
    }
  }, 3000);
}

// 성공 사운드 재생 (선택사항)
function playSuccessSound() {
  // Web Audio API를 사용한 간단한 사운드 효과
  if (typeof AudioContext !== 'undefined' || typeof webkitAudioContext !== 'undefined') {
    try {
      const audioContext = new (AudioContext || webkitAudioContext)();
      const oscillator = audioContext.createOscillator();
      const gainNode = audioContext.createGain();

      oscillator.connect(gainNode);
      gainNode.connect(audioContext.destination);

      oscillator.frequency.setValueAtTime(800, audioContext.currentTime);
      oscillator.frequency.setValueAtTime(1200, audioContext.currentTime + 0.1);

      gainNode.gain.setValueAtTime(0, audioContext.currentTime);
      gainNode.gain.linearRampToValueAtTime(0.1, audioContext.currentTime + 0.01);
      gainNode.gain.exponentialRampToValueAtTime(0.001, audioContext.currentTime + 0.3);

      oscillator.start(audioContext.currentTime);
      oscillator.stop(audioContext.currentTime + 0.3);
    } catch (error) {
      // 사운드 재생 실패 시 조용히 무시
      console.log('사운드 재생 실패:', error);
    }
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
        animation-duration: 0.1s !important;
        animation-iteration-count: 1 !important;
        transition-duration: 0.1s !important;
        scroll-behavior: auto !important;
      }
      
      .bg-decoration {
        animation: none !important;
      }
    `;
    document.head.appendChild(style);
  }
}

// 페이지 이탈 방지 (필요시)
function preventPageLeave() {
  window.addEventListener('beforeunload', function(e) {
    const loadingOverlay = document.getElementById('loadingOverlay');
    if (loadingOverlay && loadingOverlay.style.display === 'flex') {
      e.preventDefault();
      e.returnValue = '처리 중입니다. 정말 나가시겠습니까?';
    }
  });
}

// 디버그 모드 (개발용)
const DEBUG_MODE = false;

if (DEBUG_MODE) {
  console.log('온보딩 결과 페이지 디버그 모드 활성화');

  // 전역 함수로 상태 제어
  window.debugTools = {
    showLoading: () => showLoading(),
    hideLoading: () => hideLoading(),
    showError: (msg) => showErrorMessage(msg || '테스트 에러 메시지'),
    playSound: () => playSuccessSound(),
    showSuccess: () => showSuccessAnimation()
  };
}

// CSS 애니메이션 추가 (인라인으로 삽입)
const additionalStyles = `
  @keyframes slideDown {
    0% {
      opacity: 0;
      transform: translateX(-50%) translateY(-20px);
    }
    100% {
      opacity: 1;
      transform: translateX(-50%) translateY(0);
    }
  }
  
  @keyframes slideUp {
    0% {
      opacity: 1;
      transform: translateX(-50%) translateY(0);
    }
    100% {
      opacity: 0;
      transform: translateX(-50%) translateY(-20px);
    }
  }
`;

// 스타일 추가
const styleSheet = document.createElement('style');
styleSheet.textContent = additionalStyles;
document.head.appendChild(styleSheet);

// 기존 코드와의 호환성을 위한 추가 함수 (원본 onboarding-result.js 기능 유지)
document.querySelector(".check-other-btn")?.addEventListener("click", function() {
  // 이미 위에서 처리되므로 중복 방지
  if (this.id !== 'checkOtherBtn') {
    handleCheckOtherClick({ currentTarget: this, preventDefault: () => {} });
  }
});

// 원본 스타일의 .check-other 클래스도 지원
document.querySelector(".check-other")?.addEventListener("click", function() {
  const checkOtherBtn = document.getElementById('checkOtherBtn');
  if (checkOtherBtn) {
    checkOtherBtn.click();
  }
});