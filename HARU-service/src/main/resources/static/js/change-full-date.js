// /js/change-full-date.js

const modal = document.getElementById("full-date-modal");
const yearPicker = document.getElementById("year-picker");
const monthPicker = document.getElementById("month-picker");
const datePicker = document.getElementById("date-picker");

let selectedYear, selectedMonth, selectedDay;

function openDateModal(currentDate = new Date()) {
  modal.classList.add("show");

  selectedYear = currentDate.getFullYear();
  selectedMonth = currentDate.getMonth() + 1;
  selectedDay = currentDate.getDate();

  setupWheelPicker();
}

function closeModal() {
  modal.classList.remove("show");
}

function setupWheelPicker() {
  setupYearPicker();
  setupMonthPicker();
  setupDatePicker();
}

function setupYearPicker() {
  yearPicker.innerHTML = "";

  // 패딩 아이템 추가 (위쪽)
  for (let i = 0; i < 2; i++) {
    const paddingDiv = document.createElement("div");
    paddingDiv.className = "wheel-picker-item padding-item";
    paddingDiv.innerHTML = "&nbsp;";
    yearPicker.appendChild(paddingDiv);
  }

  for (let y = 1995; y <= new Date().getFullYear(); y++) {
    const div = document.createElement("div");
    div.className = "wheel-picker-item";
    div.textContent = y;
    div.dataset.value = y;

    div.addEventListener("click", () => {
      // 기존 선택 해제
      yearPicker.querySelectorAll('.wheel-picker-item').forEach(item => {
        item.classList.remove('selected');
      });
      // 새로운 선택
      div.classList.add('selected');
      selectedYear = y;
      // 선택된 항목을 중앙으로 스크롤
      div.scrollIntoView({ block: 'center', behavior: 'smooth' });
      setupDatePicker(); // 년도 변경시 일자 다시 계산
    });

    yearPicker.appendChild(div);
  }

  // 패딩 아이템 추가 (아래쪽)
  for (let i = 0; i < 2; i++) {
    const paddingDiv = document.createElement("div");
    paddingDiv.className = "wheel-picker-item padding-item";
    paddingDiv.innerHTML = "&nbsp;";
    yearPicker.appendChild(paddingDiv);
  }

  // 초기 선택 표시 및 스크롤
  const selectedItem = yearPicker.querySelector(`[data-value="${selectedYear}"]`);
  if (selectedItem) {
    selectedItem.classList.add('selected');
    selectedItem.scrollIntoView({ block: 'center', behavior: 'instant' });
  }
}

function setupMonthPicker() {
  monthPicker.innerHTML = "";

  // 패딩 아이템 추가 (위쪽)
  for (let i = 0; i < 2; i++) {
    const paddingDiv = document.createElement("div");
    paddingDiv.className = "wheel-picker-item padding-item";
    paddingDiv.innerHTML = "&nbsp;";
    monthPicker.appendChild(paddingDiv);
  }

  for (let m = 1; m <= 12; m++) {
    const div = document.createElement("div");
    div.className = "wheel-picker-item";
    div.textContent = `${m}월`;
    div.dataset.value = m;

    div.addEventListener("click", () => {
      // 기존 선택 해제
      monthPicker.querySelectorAll('.wheel-picker-item').forEach(item => {
        item.classList.remove('selected');
      });
      // 새로운 선택
      div.classList.add('selected');
      selectedMonth = m;
      // 선택된 항목을 중앙으로 스크롤
      div.scrollIntoView({ block: 'center', behavior: 'smooth' });
      setupDatePicker(); // 월 변경시 일자 다시 계산
    });

    monthPicker.appendChild(div);
  }

  // 패딩 아이템 추가 (아래쪽)
  for (let i = 0; i < 2; i++) {
    const paddingDiv = document.createElement("div");
    paddingDiv.className = "wheel-picker-item padding-item";
    paddingDiv.innerHTML = "&nbsp;";
    monthPicker.appendChild(paddingDiv);
  }

  // 초기 선택 표시 및 스크롤
  const selectedItem = monthPicker.querySelector(`[data-value="${selectedMonth}"]`);
  if (selectedItem) {
    selectedItem.classList.add('selected');
    selectedItem.scrollIntoView({ block: 'center', behavior: 'instant' });
  }
}

function setupDatePicker() {
  datePicker.innerHTML = "";

  // 패딩 아이템 추가 (위쪽)
  for (let i = 0; i < 2; i++) {
    const paddingDiv = document.createElement("div");
    paddingDiv.className = "wheel-picker-item padding-item";
    paddingDiv.innerHTML = "&nbsp;";
    datePicker.appendChild(paddingDiv);
  }

  const daysInMonth = new Date(selectedYear, selectedMonth, 0).getDate();

  // 선택된 날이 해당 월의 최대 일수를 초과하면 조정
  if (selectedDay > daysInMonth) {
    selectedDay = daysInMonth;
  }

  for (let d = 1; d <= daysInMonth; d++) {
    const div = document.createElement("div");
    div.className = "wheel-picker-item";
    div.textContent = `${d}일`;
    div.dataset.value = d;

    div.addEventListener("click", () => {
      // 기존 선택 해제
      datePicker.querySelectorAll('.wheel-picker-item').forEach(item => {
        item.classList.remove('selected');
      });
      // 새로운 선택
      div.classList.add('selected');
      selectedDay = d;
      // 선택된 항목을 중앙으로 스크롤
      div.scrollIntoView({ block: 'center', behavior: 'smooth' });
    });

    datePicker.appendChild(div);
  }

  // 패딩 아이템 추가 (아래쪽)
  for (let i = 0; i < 2; i++) {
    const paddingDiv = document.createElement("div");
    paddingDiv.className = "wheel-picker-item padding-item";
    paddingDiv.innerHTML = "&nbsp;";
    datePicker.appendChild(paddingDiv);
  }

  // 초기 선택 표시 및 스크롤
  const selectedItem = datePicker.querySelector(`[data-value="${selectedDay}"]`);
  if (selectedItem) {
    selectedItem.classList.add('selected');
    selectedItem.scrollIntoView({ block: 'center', behavior: 'instant' });
  }
}

document.getElementById("cancel-btn").addEventListener("click", closeModal);

document.getElementById("confirm-btn").addEventListener("click", async () => {
  const yyyy = selectedYear;
  const mm = String(selectedMonth).padStart(2, '0');
  const dd = String(selectedDay).padStart(2, '0');
  const dateStr = `${yyyy}-${mm}-${dd}`;

  // 선택된 날짜가 오늘 날짜를 초과하는지 확인
  const selectedDate = new Date(yyyy, selectedMonth - 1, selectedDay);
  const today = new Date();
  today.setHours(23, 59, 59, 999); // 오늘 끝까지로 설정

  if (selectedDate > today) {
    alert("미래의 일기는 지금 작성하실 수 없습니다.");
    return;
  }

  try {
    const res = await fetch(`/api/diary/check?date=${dateStr}`);
    const exists = await res.json();

    if (exists) {
      alert("이미 작성된 날짜입니다. 다른 날짜를 선택해주세요.");
      return;
    }

    document.getElementById("diary-date").value = dateStr;

    const displayText = new Date(dateStr).toLocaleDateString('ko-KR', {
      year: 'numeric', month: 'long', day: 'numeric', weekday: 'short'
    });
    document.getElementById("date-text").textContent = displayText;

    closeModal();
  } catch (e) {
    alert("날짜 확인 중 오류 발생");
  }
});

// 모달 외부 클릭시 닫기
modal.addEventListener('click', (e) => {
  if (e.target === modal) {
    closeModal();
  }
});

// 전역 함수 등록
window.openFullDateModal = openDateModal;