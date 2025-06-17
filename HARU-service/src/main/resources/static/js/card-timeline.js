let currentPage = 0;
let isLoading = false;
const PAGE_SIZE = 30;
let currentMonth = null;
let currentFilter = 'all'

document.addEventListener("DOMContentLoaded", () => {
  loadNextPage();

  // 무한 스크롤 감지
  window.addEventListener("scroll", () => {
    if (
        window.innerHeight + window.scrollY >= document.body.offsetHeight - 100 &&
        !isLoading
    ) {
      loadNextPage();
    }
  });

  // 필터 버튼 이벤트
  document.querySelectorAll('.filter-btn').forEach(btn => {
    btn.addEventListener('click', function() {
      // 이미 활성화된 필터를 클릭할 경우 무시
      if (this.classList.contains('active')) {
        return;
      }

      // 모든 버튼에서 active 클래스 제거
      document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
      this.classList.add('active');

      // 필터 타입 설정
      const filterText = this.textContent.trim();
      if (filterText === '전체') {
        currentFilter = 'all';
      } else if (filterText === '이동') {
        currentFilter = 'travel';
      } else if (filterText === '사진 있는 일기') {
        currentFilter = 'with_image';
      }

      console.log('필터 변경: ', currentFilter);

      // 필터 변경 시 초기화 및 리로드
      resetTimeline();
      loadNextPage();
    });
  });
});

// 타임라인 초기화 함수
function resetTimeline() {
  currentPage = 0;
  currentMonth = null;
  isLoading = false;

  // 타임라인 컨테이너 내용 비우기
  const container = document.getElementById("timeline-container")
  container.innerHTML = '';
}

async function loadNextPage() {
  isLoading = true;

  try {
    const response = await fetch(
        `/api/diary/cards?page=${currentPage}&filter=${currentFilter}`
    );
    const data = await response.json();
    const diaries = data.diaryCards || [];

    if (diaries.length > 0) {
      const container = document.getElementById("timeline-container");

      diaries.forEach((diary, index) => {
        // 월별 구분선 체크
        const diaryDate = new Date(diary.date);
        const monthKey = `${diaryDate.getFullYear()}-${diaryDate.getMonth() + 1}`;

        if (currentMonth !== monthKey) {
          currentMonth = monthKey;
          const monthDivider = createMonthDivider(diaryDate);
          container.appendChild(monthDivider);
        }

        const card = renderDiaryCard(diary, currentPage * PAGE_SIZE + index);
        container.appendChild(card);
      });
      currentPage++;
    } else {
      // 더 이상 불러올 일기가 없음
      window.removeEventListener("scroll", handleScroll);
    }
  } catch (error) {
    console.error("일기 불러오기 실패:", error);
  } finally {
    isLoading = false;
  }
}

function createMonthDivider(date) {
  const year = date.getFullYear();
  const month = date.getMonth() + 1;

  const monthDivider = document.createElement('div');
  monthDivider.className = 'month-divider';
  monthDivider.innerHTML = `
    <div class="month-label">${year}년 ${month}월</div>
  `;

  return monthDivider;
}

function renderDiaryCard(diary, index) {
  const template = document.getElementById("diary-card-timeline-template");
  const clone = template.content.cloneNode(true);
  const timelineItem = clone.querySelector(".timeline-item");
  const card = clone.querySelector(".diary-card");

  // 애니메이션 딜레이 설정
  timelineItem.style.animationDelay = `${(index % 10) * 0.1}s`;

  // 날짜 포맷
  const [year, month, day] = diary.date.split("-");
  const dateElem = clone.querySelector(".diary-date-badge");
  dateElem.textContent = `${month}.${day}`;

  // 감정 이미지
  const emotionKey = diary.emotion?.toUpperCase() || "DEFAULT";
  const icon = clone.querySelector(".emotion-icon");
  icon.src = `/images/emotion/weather/${emotionImageMap[emotionKey]}`;
  icon.alt = diary.emotion || "default";

  // 일기 내용
  const contentElem = clone.querySelector(".diary-content");
  contentElem.textContent = diary.content || "(내용 없음)";

  // 이미지 처리
  const imageWrapper = clone.querySelector(".diary-image-wrapper");
  if (!diary.imagePath) {
    card.classList.add("no-image");
    imageWrapper.style.display = "none";
  } else {
    const img = clone.querySelector(".diary-image");
    img.src = diary.imagePath;
    img.alt = "diary image";
  }

  // 액션 버튼 이벤트
  const actionBtns = clone.querySelectorAll('.action-btn');
  actionBtns.forEach(btn => {
    btn.addEventListener('click', function(e) {
      e.stopPropagation();
      this.style.transform = 'scale(0.9)';
      setTimeout(() => {
        this.style.transform = 'scale(1.1)';
      }, 100);
      console.log('액션 버튼 클릭:', this.innerHTML);
    });
  });

  // 상세 페이지로 이동 이벤트 추가
  card.addEventListener("click", (e) => {
    // 액션 버튼 클릭 시에는 이동하지 않음
    if (!e.target.closest('.action-btn')) {
      const dateParam = diary.date;
      window.location.href = `/diary/details?date=${dateParam}`;
    }
  });

  return clone;
}

// 스크롤 핸들러 (무한 스크롤용)
function handleScroll() {
  if (
      window.innerHeight + window.scrollY >= document.body.offsetHeight - 100 &&
      !isLoading
  ) {
    loadNextPage();
  }
}