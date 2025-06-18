// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
  // 페이지 애니메이션
  document.querySelector('.container').classList.add('show');

  // 배경 장식 요소들 애니메이션
  const decorations = document.querySelectorAll('.bg-decoration');
  decorations.forEach((decoration, index) => {
    const randomDelay = Math.random() * 2;
    decoration.style.animationDelay = `${randomDelay}s`;
  });

  // 이벤트 리스너 설정
  setupEventListeners();
});

function setupEventListeners() {
  // 시작하기 버튼 - 기존 폼 제출 유지
  const startBtn = document.getElementById('startBtn');
  if (startBtn) {
    startBtn.addEventListener('click', function(e) {
      this.classList.add('clicked');

      // 로딩 표시
      setTimeout(() => {
        showLoading('AI 친구를 준비하고 있어요...', '곧 만나실 수 있어요!');
      }, 300);

      // 폼 제출은 그대로 진행 (기본 동작)
    });
  }

  // 다른 AI 확인하기 버튼 - 단순 페이지 이동
  const checkOtherBtn = document.getElementById('checkOtherBtn');
  if (checkOtherBtn) {
    checkOtherBtn.addEventListener('click', function(e) {
      e.preventDefault(); // 기본 동작 방지

      this.classList.add('clicked');

      // 로딩 표시
      showLoading('다른 AI들을 불러오고 있어요...', '잠시만 기다려주세요');

      // 단순히 페이지 이동
      setTimeout(() => {
        window.location.href = "/app/settings/ai";
      }, 800);
    });
  }
}

// 로딩 표시 함수
function showLoading(title = '처리 중...', subtitle = '잠시만 기다려주세요') {
  const loadingOverlay = document.getElementById('loadingOverlay');
  if (!loadingOverlay) return;

  const loadingTitle = loadingOverlay.querySelector('h3');
  const loadingSubtitle = loadingOverlay.querySelector('p');

  if (loadingTitle) loadingTitle.textContent = title;
  if (loadingSubtitle) loadingSubtitle.textContent = subtitle;

  loadingOverlay.style.display = 'flex';

  // 버튼 비활성화
  const buttons = document.querySelectorAll('.start-btn, .check-other-btn');
  buttons.forEach(button => {
    button.disabled = true;
    button.style.opacity = '0.6';
    button.style.cursor = 'not-allowed';
  });
}

// 키보드 지원
document.addEventListener('keydown', function(e) {
  if (e.key === 'Enter' && !e.ctrlKey && !e.metaKey && e.target === document.body) {
    const startBtn = document.getElementById('startBtn');
    if (startBtn && !startBtn.disabled) {
      startBtn.click();
    }
  }

  if (e.key === ' ' && e.target === document.body) {
    e.preventDefault();
    const checkOtherBtn = document.getElementById('checkOtherBtn');
    if (checkOtherBtn && !checkOtherBtn.disabled) {
      checkOtherBtn.click();
    }
  }
});