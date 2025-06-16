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

window.addEventListener('DOMContentLoaded', () => {
  fetch('/api/keyword/group')
  .then(res => res.json())
  .then(data => {
    renderKeywords(data);
    renderPrimary(currentType);
    toggleBtn.innerText = '+ 더보기';
    updateCharCount();
  })
  .catch(err => console.error('Failed to fetch keywords:', err));
});

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

function renderKeywords(groups) {
  renderKeywordGroupToHidden(groups.EMOTION_GOOD, 'keyword-good', 'kw', selectedKeywords);
  renderKeywordGroupToHidden(groups.EMOTION_BAD, 'keyword-bad', 'kw', selectedKeywords);
  renderKeywordGroup(groups.PERSON, 'keyword-person', 'kw-person', selectedKeywords);
  renderKeywordGroup(groups.SITUATION, 'keyword-situation', 'kw-situation', selectedKeywords);
}

function renderKeywordGroup(keywords, containerId, prefix, selected) {
  const container = document.getElementById(containerId);
  container.innerHTML = '';
  if (!keywords) return;
  keywords.forEach(kw => container.appendChild(createKeywordChip(kw, prefix, selected)));
}

function createKeywordChip(keyword, prefix, selectedKeywords = []) {
  const chip = document.createElement('div');
  chip.className = 'keyword-chip';

  const checkbox = document.createElement('input');
  checkbox.type = 'checkbox';
  checkbox.id = `${prefix}-${keyword.keywordId}`;
  checkbox.name = 'keywords';
  checkbox.value = keyword.name;

  if (selectedKeywords.includes(keyword.name)) {
    checkbox.checked = true;
  }

  const label = document.createElement('label');
  label.htmlFor = checkbox.id;
  label.innerText = keyword.name;

  chip.appendChild(checkbox);
  chip.appendChild(label);

  return chip;
}

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

function renderKeywordGroupToHidden(keywords, containerId, prefix, selected) {
  const container = document.getElementById(containerId);
  container.innerHTML = '';
  if (!keywords) return;
  keywords.forEach(kw => container.appendChild(createKeywordChip(kw, prefix, selected)));
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

// 수정된 다이어리 전송
async function submitDiaryEdit() {
  const diaryId = document.getElementById('diaryId').value; // hidden input 필요
  const emotion = document.querySelector('input[name="emotion"]:checked')?.value;
  const content = document.querySelector('textarea[name="content"]').value;
  const date = document.getElementById('diary-date').value;

  const selectedKeywords = Array.from(document.querySelectorAll('#keyword-primary input[name="keywords"]:checked, ' +
      '#keyword-secondary input[name="keywords"]:checked, ' +
      '#keyword-person input[name="keywords"]:checked, ' +
      '#keyword-situation input[name="keywords"]:checked'))
  .map(input => input.value);

  const deletedImageIds = window.deletedImageIds || []; // 미리보기에서 삭제된 이미지 id
  const imageInput = document.getElementById('images');
  const newImageFiles = imageInput.files;

  const requestPayload = {
    diaryId,
    emotion,
    content,
    date,
    keywords: selectedKeywords,
    deletedImageIds
  };

  const formData = new FormData();
  formData.append("request", new Blob([JSON.stringify(requestPayload)], { type: "application/json" }));

  for (let file of newImageFiles) {
    formData.append("newImages", file);
  }


  const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

  try {
    const res = await fetch("/api/diary", {
      method: "PATCH",
      body: formData,
      headers: {
        [csrfHeader]: csrfToken
      },
    });
    if (res.ok) {
      alert("일기가 수정되었습니다!");
      window.location.href = `/diary/details?date=${date}`;
    } else {
      alert("수정 중 오류가 발생했습니다.");
      window.location.href = `/diary/details?date=${diaryDate}`;
    }
  } catch (err) {
    console.error("수정 실패", err);
    alert("네트워크 오류 또는 서버 문제");
  }
}