const dateModal = document.getElementById("date-modal");
let selectedMonth = new Date().getMonth() + 1;
let selectedYear = new Date().getFullYear();

const months = ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"];

function openDateModal(currentDate = new Date()) {
  dateModal.classList.add('show');

  selectedMonth = currentDate.getMonth() + 1;
  selectedYear = currentDate.getFullYear();

  setupWheelPicker();
}

function closeModal() {
  dateModal.classList.remove('show');
}

function setupWheelPicker() {
  setupMonthPicker();
  setupYearPicker();
}

function setupMonthPicker() {
  const monthPicker = document.getElementById("month-picker");
  monthPicker.innerHTML = "";

  // 상단 패딩
  for (let i = 0; i < 5; i++) {
    const item = document.createElement('div');
    item.className = 'wheel-picker-item';
    monthPicker.appendChild(item);
  }

  // 월 아이템들
  months.forEach((month, index) => {
    const item = document.createElement('div');
    item.className = 'wheel-picker-item';
    item.textContent = month;
    item.dataset.value = index + 1;

    if (index + 1 === selectedMonth) {
      item.classList.add('selected');
    }

    item.addEventListener('click', () => {
      selectedMonth = index + 1;
      updateSelection(monthPicker, item);
      scrollToItem(monthPicker, item);
    });

    monthPicker.appendChild(item);
  });

  // 하단 패딩
  for (let i = 0; i < 5; i++) {
    const item = document.createElement('div');
    item.className = 'wheel-picker-item';
    monthPicker.appendChild(item);
  }

  // 초기 위치 설정
  setTimeout(() => {
    const selectedItem = monthPicker.querySelector(`[data-value="${selectedMonth}"]`);
    if (selectedItem) {
      scrollToItem(monthPicker, selectedItem);
    }
  }, 100);

  setupScrollListener(monthPicker, (value) => {
    selectedMonth = parseInt(value);
  });
}

function setupYearPicker() {
  const yearPicker = document.getElementById("year-picker");
  yearPicker.innerHTML = "";

  const currentYear = new Date().getFullYear();
  const startYear = 1995;
  const endYear = currentYear;

  // 상단 패딩
  for (let i = 0; i < 5; i++) {
    const item = document.createElement('div');
    item.className = 'wheel-picker-item';
    yearPicker.appendChild(item);
  }

  // 연도 아이템들
  for (let year = startYear; year <= endYear; year++) {
    const item = document.createElement('div');
    item.className = 'wheel-picker-item';
    item.textContent = year.toString();
    item.dataset.value = year;

    if (year === selectedYear) {
      item.classList.add('selected');
    }

    item.addEventListener('click', () => {
      selectedYear = year;
      updateSelection(yearPicker, item);
      scrollToItem(yearPicker, item);
    });

    yearPicker.appendChild(item);
  }

  // 하단 패딩
  for (let i = 0; i < 5; i++) {
    const item = document.createElement('div');
    item.className = 'wheel-picker-item';
    yearPicker.appendChild(item);
  }

  // 초기 위치 설정
  setTimeout(() => {
    const selectedItem = yearPicker.querySelector(`[data-value="${selectedYear}"]`);
    if (selectedItem) {
      scrollToItem(yearPicker, selectedItem);
    }
  }, 100);

  setupScrollListener(yearPicker, (value) => {
    selectedYear = parseInt(value);
  });
}

function updateSelection(container, selectedItem) {
  container.querySelectorAll('.wheel-picker-item').forEach(item => {
    item.classList.remove('selected');
  });
  selectedItem.classList.add('selected');
}

function scrollToItem(container, item) {
  const containerHeight = container.offsetHeight;
  const itemTop = item.offsetTop;
  const itemHeight = 36;
  const scrollTop = itemTop - (containerHeight / 2) + (itemHeight / 2);

  container.scrollTo({
    top: scrollTop,
    behavior: 'smooth'
  });
}

function setupScrollListener(container, onValueChange) {
  let scrollTimeout;

  container.addEventListener('scroll', () => {
    clearTimeout(scrollTimeout);
    scrollTimeout = setTimeout(() => {
      const containerHeight = container.offsetHeight;
      const scrollTop = container.scrollTop;
      const centerY = scrollTop + containerHeight / 2;

      const items = container.querySelectorAll('.wheel-picker-item[data-value]');
      let closestItem = null;
      let closestDistance = Infinity;

      items.forEach(item => {
        const itemTop = item.offsetTop;
        const itemCenter = itemTop + 18; // item height / 2
        const distance = Math.abs(centerY - itemCenter);

        if (distance < closestDistance) {
          closestDistance = distance;
          closestItem = item;
        }
      });

      if (closestItem) {
        updateSelection(container, closestItem);
        onValueChange(closestItem.dataset.value);
        scrollToItem(container, closestItem);
      }
    }, 150);
  });
}

// 이벤트 리스너
document.getElementById("cancel-btn").addEventListener("click", closeModal);

document.getElementById("confirm-btn").addEventListener("click", () => {
  // 글로벌 콜백 함수가 있다면 호출
  if (typeof window.onDateChange === "function") {
    window.onDateChange(selectedYear, selectedMonth);
  }

  closeModal();
});

// 모달 외부 클릭시 닫기
dateModal.addEventListener('click', (e) => {
  if (e.target === dateModal) {
    closeModal();
  }
});

// 데모용 글로벌 함수
window.onDateChange = function(year, month) {
  console.log(`날짜 변경: ${year}년 ${month}월`);
};

function initDateModal(triggerSelector, options = {}) {
  const triggerElement = document.querySelector(triggerSelector);

  if (triggerElement) {
    triggerElement.addEventListener('click', () => {
      const currentDate = options.getCurrentDate ? options.getCurrentDate() : new Date();
      openDateModal(currentDate);
    });

    triggerElement.style.cursor = 'pointer';
  } else {
    console.warn(`날짜 모달 트리거 요소를 찾을 수 없습니다: ${triggerSelector}`);
  }
}

// 전역 등록
window.initDateModal = initDateModal;
window.openDateModal = openDateModal;