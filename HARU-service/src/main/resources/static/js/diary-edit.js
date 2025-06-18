const contentTextarea = document.querySelector('textarea[name="content"]');
const charCountSpan = document.getElementById('char-count');
const emotionInputs = document.querySelectorAll('input[type="radio"][name$="emotion"]');
const primaryContainer = document.getElementById('keyword-primary');
const secondaryContainer = document.getElementById('keyword-secondary');
const toggleBtn = document.querySelector('.toggle-btn');

let currentType = 'good';
let expanded = false;



// DOM 로드 후 기본 감정 키워드 표시
window.addEventListener('DOMContentLoaded', () => {
  initExistingImages(existingImages, currentThumbnailName);
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
  if (!keywords) {
    return;
  }
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
  if (!keywords) {
    return;
  }
  keywords.forEach(kw => container.appendChild(createKeywordChip(kw, prefix, selected)));
}

let allImages = []; // 기존 + 새 이미지 포함
let deletedImageIds = [];
let selectedThumbnail = null;

function initExistingImages(existingImages, currentThumbnailName) {
  const previewArea = document.getElementById('preview-area');

  existingImages.forEach(img => {
    const imageObj = {
      ...img,
      isNew: false,
    };

    const wrapper = renderImageWrapper(imageObj);
    previewArea.appendChild(wrapper);
    allImages.push(imageObj);

    if (img.originName === currentThumbnailName) {
      selectedThumbnail = imageObj;
      wrapper.classList.add('selected-thumbnail');
    }
  });
}

function previewImages(event) {
  const files = Array.from(event.target.files);
  const previewArea = document.getElementById('preview-area');

  const placeholder = previewArea.querySelector('.placeholder-icon');
  if (placeholder) {
    placeholder.remove();
  }

  files.forEach(file => {
    const imageObj = {
      file,
      originName: file.name,
      isNew: true
    };

    const wrapper = renderImageWrapper(imageObj);
    previewArea.appendChild(wrapper);
    allImages.push(imageObj);

    if (!selectedThumbnail) {
      selectedThumbnail = imageObj;
      wrapper.classList.add('selected-thumbnail');
    }
  });
}

function renderImageWrapper(image) {
  const wrapper = document.createElement('div');
  wrapper.className = 'preview-img-wrapper';
  wrapper.dataset.filename = image.originName;

  const img = document.createElement('img');
  img.src = image.isNew ? URL.createObjectURL(image.file) : image.path;
  img.className = 'preview-img';

  // 삭제 버튼
  const deleteBtn = document.createElement('button');
  deleteBtn.type = 'button';
  deleteBtn.className = 'delete-btn';
  deleteBtn.innerText = '✖';
  deleteBtn.onclick = () => {
    const index = allImages.indexOf(image);
    if (index !== -1) {
      allImages.splice(index, 1);
      wrapper.remove();

      if (!image.isNew && image.imgId) {
        deletedImageIds.push(image.imgId);
      }

      if (selectedThumbnail === image) {
        selectedThumbnail = allImages[0] || null;
      }

      updateThumbnailUI();
    }
  };

  // 썸네일 선택 버튼
  const thumbnailBtn = document.createElement('button');
  thumbnailBtn.type = 'button';
  thumbnailBtn.className = 'thumbnail-btn';
  thumbnailBtn.innerText = '🌟';
  thumbnailBtn.onclick = () => {
    selectedThumbnail = image;
    updateThumbnailUI();
  };

  wrapper.appendChild(img);
  wrapper.appendChild(deleteBtn);
  wrapper.appendChild(thumbnailBtn);
  return wrapper;
}

function updateThumbnailUI() {
  document.querySelectorAll('.preview-img-wrapper').forEach(div =>
      div.classList.remove('selected-thumbnail')
  );

  const wrappers = document.querySelectorAll('.preview-img-wrapper');
  wrappers.forEach((wrapper, i) => {
    const fileName = wrapper.dataset.filename;
    if (selectedThumbnail && fileName === selectedThumbnail.originName) {
      wrapper.classList.add('selected-thumbnail');
    }
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

  const selectedKeywords = Array.from(document.querySelectorAll(
      '#keyword-primary input[name="keywords"]:checked, ' +
      '#keyword-secondary input[name="keywords"]:checked, ' +
      '#keyword-person input[name="keywords"]:checked, ' +
      '#keyword-situation input[name="keywords"]:checked'))
  .map(input => input.value);

  const thumbnailIndex = allImages.indexOf(selectedThumbnail);
  const thumbnailFileName = selectedThumbnail.originName;

  const requestPayload = {
    diaryId,
    emotion,
    content,
    date,
    keywords: selectedKeywords,
    deletedImageIds,
    thumbnailIndex,
    thumbnailFileName
  };

  const formData = new FormData();
  formData.append("request",
      new Blob([JSON.stringify(requestPayload)], {type: "application/json"}));

  const newImages = allImages.filter(img => img.isNew);
  newImages.forEach(img => {
    formData.append("newImages", img.file);
  });

  try {
    const res = await fetch("/api/diary", {
      method: "PATCH",
      body: formData,
      headers: {
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