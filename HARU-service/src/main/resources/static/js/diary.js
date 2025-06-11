const form = document.querySelector('form');
const contentTextarea = document.querySelector('textarea[name="content"]');
const charCountSpan = document.getElementById('char-count');
const emotionInputs = document.querySelectorAll('input[type="radio"][name$="emotion"]');
const primaryContainer = document.getElementById('keyword-primary');
const secondaryContainer = document.getElementById('keyword-secondary');
const toggleBtn = document.querySelector('.toggle-btn');

let currentType = 'good';
let expanded = false;

const dateInput = document.getElementById('diary-date');
const dateText = document.getElementById('date-text');
const dateChangeBtn = document.getElementById('date-change-btn');

let prevDateValue = dateInput.value;

// 날짜 변경 버튼 클릭 시 date input 표시
dateChangeBtn.addEventListener('click', () => {
  prevDateValue = dateInput.value; //변경 전 날짜
  dateInput.style.display = 'inline-block';
  dateInput.focus();
});

// 날짜 선택 시 화면에 한글로 표시
dateInput.addEventListener('change', async() => {

  const dateParam = dateInput.value;
  try {
    const res = await fetch(`/api/diary/check?date=${dateParam}`);
    const exists = await res.json();

    if (exists) {
      alert("이미 해당 날짜에 작성된 일기가 있습니다. 다른 날짜를 선택하세요.");
      dateInput.value = prevDateValue; // 날짜 롤백
      // 날짜 텍스트도 원래대로 되돌림
      const prevDate = new Date(prevDateValue);
      const options = { month: 'long', day: 'numeric', weekday: 'short' };
      dateText.textContent = prevDate.toLocaleDateString('ko-KR', options);
    } else {
      // 날짜 텍스트 업데이트
      const date = new Date(dateParam);
      const options = { month: 'long', day: 'numeric', weekday: 'short' };
      dateText.textContent = date.toLocaleDateString('ko-KR', options);
      prevDateValue = dateParam; // prevDateValue도 갱신
    }
    dateInput.style.display = 'none';
  } catch (err) {
    alert("서버와 통신 중 오류가 발생했습니다.");
    dateInput.value = prevDateValue; // 오류 발생 시에도 롤백
    dateInput.style.display = 'none';
  }

});


console.log('emotionInputs:', emotionInputs);
emotionInputs.forEach(input => {
  console.log(input.name, input.checked);
});

form.addEventListener('submit', function (e) {
  const emotionChecked = document.querySelector('input[type="radio"][name="emotion"]:checked');
  console.log('remon', emotionChecked);

  if (!emotionChecked) {
    e.preventDefault();
    alert("오늘의 감정을 선택해주세요.");
    document.getElementById('emotion1').scrollIntoView({ behavior: 'smooth' });
    return;
  }


  if (contentTextarea.value.length > 1000) {
    e.preventDefault();
    alert("일기는 1000자 이내로 작성해주세요.");
  }
});

// DOM 로드 후 기본 감정 키워드 표시
window.addEventListener('DOMContentLoaded', () => {
  renderPrimary('good');
  toggleBtn.innerText = '+ 더보기';
  updateCharCount(); // 초기 글자수 세팅
});

// 감정 선택 시 키워드 그룹 전환
emotionInputs.forEach(input => {
  input.addEventListener('change', function () {
    const value = this.value;
    if (['VERY_GOOD', 'GOOD', 'COMMON'].includes(value)) {
      currentType = 'good';
      renderPrimary('good');
    } else {
      currentType = 'bad';
      renderPrimary('bad');
    }
    hideSecondary();
    toggleBtn.innerText = '+ 더보기';
    expanded = false;
  });
});

// 키워드 렌더링 함수
function renderPrimary(type) {
  primaryContainer.innerHTML = '';
  const items = document.querySelectorAll(`#keyword-${type} .keyword-chip`);
  items.forEach(el => primaryContainer.appendChild(el.cloneNode(true)));
}

function renderSecondary(type) {
  secondaryContainer.innerHTML = '';
  const items = document.querySelectorAll(`#keyword-${type} .keyword-chip`);
  items.forEach(el => secondaryContainer.appendChild(el.cloneNode(true)));
  secondaryContainer.style.display = 'flex';
}

function hideSecondary() {
  secondaryContainer.innerHTML = '';
  secondaryContainer.style.display = 'none';
}

function toggleKeywordExpand(btn) {
  if (expanded) {
    hideSecondary();
    btn.innerText = '+ 더보기';
  } else {
    const opposite = currentType === 'good' ? 'bad' : 'good';
    renderSecondary(opposite);
    btn.innerText = '- 접기';
  }
  expanded = !expanded;
}

// 이미지 미리보기
function previewImages(event) {
  const files = event.target.files;
  const previewArea = document.getElementById('preview-area');

  // 사진기 아이콘 placeholder 아이콘 제거
  const placeholder = previewArea.querySelector('.placeholder-icon');
  if (placeholder) previewArea.removeChild(placeholder);

  Array.from(files).forEach(file => {
    const reader = new FileReader();
    reader.onload = function (e) {
      const img = document.createElement('img');
      img.src = e.target.result;
      img.classList.add('preview-img');
      previewArea.appendChild(img);
    };
    reader.readAsDataURL(file);
  });
}

// 일기 글자 수 체크 + 제한
contentTextarea.addEventListener('input', updateCharCount);

function updateCharCount() {
  const length = contentTextarea.value.length;
  charCountSpan.textContent = `${length} / 1000자`;

  if (length > 1000) {
    charCountSpan.style.color = 'red';
  } else {
    charCountSpan.style.color = '#555';
  }
}