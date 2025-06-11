const grid = document.querySelector(".calendar-grid");
const title = document.getElementById("calendar-title");
let currentDate = new Date();

function renderCalendar(date, emotionMap = {}) {
  // 헤더 날짜 표시
  const year = date.getFullYear();
  const month = date.getMonth();
  title.textContent = `${year}.${String(month + 1).padStart(2, "0")}`;

  // 날짜 계산
  const firstDay = new Date(year, month, 1).getDay();
  const lastDate = new Date(year, month + 1, 0).getDate();

  // 기존 날짜 셀 제거
  document.querySelectorAll(".date-cell").forEach((el) => el.remove());

  // 빈칸 생성
  for (let i = 0; i < firstDay; i++) {
    const empty = document.createElement("div");
    empty.className = "date-cell";
    grid.appendChild(empty);
  }

  // 날짜 셀 생성
  const today = new Date();
  today.setHours(0, 0, 0, 0);  // 오늘 기준 정규화

  for (let d = 1; d <= lastDate; d++) {
    const cell = document.createElement("div");
    cell.className = "date-cell";

    const circle = document.createElement("div");
    circle.className = "date-circle";

    const number = document.createElement("div");
    number.className = "date-number";
    number.textContent = d;

    const dateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(d).padStart(2, '0')}`;
    const emotion = emotionMap[dateStr];

    if (emotion) {
      const img = document.createElement("img");
      //TODO : 혹시 나중에 테마별 이미지셋이 변경되도록 된다면 이 부분 수정할 것
      img.src = `/images/emotion/weather/${emotionImageMap[emotion]}`;
      img.alt = emotion;
      img.className = "emotion-img";
      img.onerror = () => {
        img.style.display = "none"; // 이미지 로드 실패 시 감추기
      };
      circle.innerHTML = ""; // 기존 배경 제거
      circle.style.backgroundColor = "transparent"; // 회색 배경 제거
      circle.appendChild(img);
    }

    if (
        d === today.getDate() &&
        month === today.getMonth() &&
        year === today.getFullYear()
    ) {
      circle.classList.add("today-circle");
      number.classList.add("today-number");
    }

    // 날짜 클릭 이벤트 등록
    cell.addEventListener("click", async () => {
      const clickedDate = new Date(year, month, d);
      clickedDate.setHours(0, 0, 0, 0);

      const tomorrow = new Date(today);
      tomorrow.setDate(today.getDate() + 1);

      if (clickedDate >= tomorrow) {
        return; // 내일 이후는 무시
      }

      const dateParam = `${year}-${String(month + 1).padStart(2, "0")}-${String(d).padStart(2, "0")}`;

      try {
        const res = await fetch(`/api/diary/check?date=${dateParam}`);
        const exists = await res.json();

        if (exists) {
          window.location.href = `/diary/record?date=${dateParam}`; // 일기가 있는 경우
        } else {
          window.location.href = `/diary/writing?date=${dateParam}`; // 일기가 없는 경우
        }
      } catch (err) {
        console.error("날짜 클릭 처리 중 오류:", err);
      }
    });

    cell.appendChild(circle);
    cell.appendChild(number);
    grid.appendChild(cell);
  }
}

async function fetchEmotionData(year, month) {
  const response = await fetch(`/api/diary/calendar?year=${year}&month=${month}`);
  const data = await response.json();

  const emotionMap = {};
  data.diaryEmotions.forEach(({ diaryDate, emotion }) => {
    emotionMap[diaryDate] = emotion;
  });

  renderCalendar(new Date(year, month - 1), emotionMap);
}

document.getElementById("prev-month").addEventListener("click", () => {
  currentDate.setMonth(currentDate.getMonth() - 1);
  fetchEmotionData(currentDate.getFullYear(), currentDate.getMonth() + 1);
});

document.getElementById("next-month").addEventListener("click", () => {
  currentDate.setMonth(currentDate.getMonth() + 1);
  fetchEmotionData(currentDate.getFullYear(), currentDate.getMonth() + 1);
});

document.getElementById("go-today").addEventListener("click", () => {
  currentDate = new Date();
  fetchEmotionData(currentDate.getFullYear(), currentDate.getMonth() + 1);
});

// 초기 렌더링
fetchEmotionData(currentDate.getFullYear(), currentDate.getMonth() + 1);
