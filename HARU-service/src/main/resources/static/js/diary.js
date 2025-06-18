const form = document.querySelector('form');
const contentTextarea = document.querySelector('textarea[name="content"]');
const charCountSpan = document.getElementById('char-count');
const emotionInputs = document.querySelectorAll('input[type="radio"][name$="emotion"]');
const primaryContainer = document.getElementById('keyword-primary');
const secondaryContainer = document.getElementById('keyword-secondary');
const toggleBtn = document.querySelector('.toggle-btn');

let currentType = 'good';
let expanded = false;

let keywordData = {};

form.addEventListener('submit', function (e) {
  const emotionChecked = document.querySelector(
      'input[type="radio"][name="emotion"]:checked');
  console.log('remon', emotionChecked);

  if (!emotionChecked) {
    e.preventDefault();
    alert("오늘의 감정을 선택해주세요.");
    document.getElementById('emotion1').scrollIntoView({behavior: 'smooth'});
    return;
  }

  if (contentTextarea.value.length > 1000) {
    e.preventDefault();
    alert("일기는 1000자 이내로 작성해주세요.");
  }
});

// 키워드 데이터 가져오기
window.addEventListener('DOMContentLoaded', () => {
  fetch('/api/keyword/group')
  .then(res => res.json())
  .then(data => {
    keywordData = data;
    renderKeywords(data);
    renderPrimary(currentType);
    toggleBtn.innerText = '+ 더보기';
    updateCharCount(); // 초기 글자수 세팅
  })
  .catch(err => console.error('Failed to fetch keywords:', err));
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

// 키워드 렌더링 통합 함수
function renderKeywords(keywordGroups) {
  renderKeywordGroupToHidden(keywordGroups.EMOTION_GOOD, 'keyword-good', 'kw');     // 🔧 숨김 영역
  renderKeywordGroupToHidden(keywordGroups.EMOTION_BAD, 'keyword-bad', 'kw');       // 🔧 숨김 영역
  renderKeywordGroup(keywordGroups.PERSON, 'kw-person-wrapper', 'kw-person');       // 🔧 일반 렌더링
  renderKeywordGroup(keywordGroups.SITUATION, 'kw-situation-wrapper',
      'kw-situation');
}

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

// 숨김 영역에 렌더링 (primary/secondary toggle용)
function renderKeywordGroupToHidden(keywords, containerId, prefix) {
  const container = document.getElementById(containerId);
  container.innerHTML = '';
  if (!keywords) {
    return;
  }

  keywords.forEach(keyword => {
    const chip = createKeywordChip(keyword, prefix);
    container.appendChild(chip);
  });
}

// 일반 렌더링 (직접 표시)
function renderKeywordGroup(keywords, containerId, prefix) {
  const container = document.getElementById(containerId);
  container.innerHTML = '';
  if (!keywords) {
    return;
  }

  keywords.forEach(keyword => {
    const chip = createKeywordChip(keyword, prefix);
    container.appendChild(chip);
  });
}

// 공통 키워드 chip 생성 함수
function createKeywordChip(keyword, prefix) {
  const chip = document.createElement('div');
  chip.className = 'keyword-chip';

  const checkbox = document.createElement('input');
  checkbox.type = 'checkbox';
  checkbox.id = `${prefix}-${keyword.keywordId}`;
  checkbox.name = 'keywords';
  checkbox.value = keyword.name;

  const label = document.createElement('label');
  label.htmlFor = checkbox.id;
  label.innerText = keyword.name;

  chip.appendChild(checkbox);
  chip.appendChild(label);

  return chip;
}

////////////////////////// 이미지 업로드 기능 보완

let selectedFiles = [];
let selectedThumbnailFile = null;

// 이미지 미리보기
function previewImages(event) {
  const files = Array.from(event.target.files);
  const previewArea = document.getElementById('preview-area');

  // 사진 선택시 사진기 아이콘 placeholder 아이콘은 제거
  const placeholder = previewArea.querySelector('.placeholder-icon');
  if (placeholder) {
    previewArea.removeChild(placeholder);
  }

  files.forEach((file) => {
        const reader = new FileReader();
        selectedFiles.push(file); // 배열에 저장

        reader.onload = function (e) {
          const wrapper = document.createElement('div');
          wrapper.classList.add('preview-img-wrapper');
          wrapper.dataset.filename = file.name;

          const img = document.createElement('img');
          img.src = e.target.result;
          img.classList.add('preview-img');

          const deleteBtn = document.createElement('button');
          deleteBtn.className = 'delete-btn';
          deleteBtn.type = 'button';
          deleteBtn.innerText = '✖';
          deleteBtn.onclick = () => {
            const fileToRemove = file;
            selectedFiles = selectedFiles.filter(f => f !== fileToRemove);
            console.log("삭제 후 배열", selectedFiles)
            wrapper.remove()

            if (selectedThumbnailFile === fileToRemove) {
              selectedThumbnailFile = selectedFiles[0] || null;
            }
            syncInputFiles();
            updateThumbnailUI();
          };

          // ⭐ 썸네일 선택 버튼
          const thumbnailBtn = document.createElement('button');
          thumbnailBtn.type = 'button';
          thumbnailBtn.className = 'thumbnail-btn';
          thumbnailBtn.innerText = '🌟';
          thumbnailBtn.onclick = () => {
            selectedThumbnailFile = file;
            updateThumbnailUI();
            syncInputFiles();
          };

          wrapper.appendChild(img);
          wrapper.appendChild(deleteBtn);
          wrapper.appendChild(thumbnailBtn);
          previewArea.appendChild(wrapper);

          if (selectedFiles.length === 1 && !selectedThumbnailFile) {
            selectedThumbnailFile = file;
            updateThumbnailUI();
            syncInputFiles();
          }
        };

        reader.readAsDataURL(file);
      });

  if (!selectedThumbnailFile && selectedFiles.length > 0) {
    selectedThumbnailFile = selectedFiles[0];
    console.log("자동 썸네일 설정: ", selectedThumbnailFile.name);
    updateThumbnailUI(); // UI 업데이트
    syncInputFiles(); // 썸네일 변경 사항을 다시 input에 동기화
  }

}

// <input type="file"> 갱신 함수
function syncInputFiles() {
  const dataTransfer = new DataTransfer();
  selectedFiles.forEach(file => dataTransfer.items.add(file));
  document.getElementById('images').files = dataTransfer.files;

  const thumbnailFileNameInput = document.getElementById('thumbnailFileName');
  if (selectedThumbnailFile) {
    thumbnailFileNameInput.value = selectedThumbnailFile.name;
    console.log("썸네일 파일 이름: ", thumbnailFileNameInput.value)
  } else {
    thumbnailFileNameInput.value = ''; // 썸네일이 없으면 빈 값
  }
}

function updateThumbnailUI() {
  document.querySelectorAll('.preview-img-wrapper').forEach(div =>
      div.classList.remove('selected-thumbnail')
  );

  if (selectedThumbnailFile) {
    const thumbnailWrapper = Array.from(document.querySelectorAll('.preview-img-wrapper'))
    .find(wrapper => wrapper.dataset.filename === selectedThumbnailFile.name);
    if (thumbnailWrapper) {
      thumbnailWrapper.classList.add('selected-thumbnail');
    }
  }
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