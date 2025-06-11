document.addEventListener('DOMContentLoaded', () => {
  const keywordContent = document.getElementById('keywordContent');
  const defaultType = 'EMOTION';

  const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

  fetchKeywords(defaultType);

  // 감정, 사람, 상황 선택
  document.querySelectorAll('.segment-button').forEach(button => {
    button.addEventListener('click', () => {
      const selectedType = button.innerText.trim();
      const keywordType = getKeywordTypeParam(selectedType);

      document.querySelectorAll('.segment-button').forEach(btn => btn.classList.remove('active'));
      button.classList.add('active');

      fetchKeywords(keywordType);
    });
  });

  // 전체 체크박스
  document.getElementById('selectAll')?.addEventListener('change', function () {
    const checkboxes = document.querySelectorAll('.keyword-checkbox');
    checkboxes.forEach(cb => cb.checked = this.checked);
  });

  // 키워드 세부타입 상수
  const keywordString = {
    EMOTION_GOOD: '긍정',
    EMOTION_BAD: '부정',
    PERSON: '사람',
    SITUATION: '상황'
  };

  // 키워드 세그맨트 버튼값 반환 함수
  function getKeywordTypeParam(label) {
    switch (label) {
      case '감정': return 'EMOTION';
      case '사람': return 'PERSON';
      case '상황': return 'SITUATION';
      default: return 'EMOTION';
    }
  }

  // 키워드 타입에 따른 값 반환 함수
  function fetchKeywords(type) {
    fetch(`/api/admin/keyword?type=${type}`)
    .then(response => response.json())
    .then(data => {
      renderKeywords(data.keywordInfos);
    })
    .catch(err => {
      console.error('키워드 불러오기 실패:', err);
      keywordContent.innerHTML = '<p style="color:red;">데이터를 불러오지 못했습니다.</p>';
    });
  }
  window.fetchKeywords = fetchKeywords;
  window.renderKeywords = renderKeywords;

  // 반환 데이터 뿌려주는 용도
  function renderKeywords(keywords) {
    keywordContent.innerHTML = '';
    if (!keywords || keywords.length === 0) {
      keywordContent.innerHTML = '<p>키워드가 없습니다.</p>';
      return;
    }

    keywords.forEach((keyword, index) => {
      const div = document.createElement('div');
      div.className = 'keyword-item';
      div.innerHTML = `
        <span id="data-keyword-id" style="display: none;">${keyword.keywordId}</span>
        <span><input type="checkbox" class="keyword-checkbox" data-id="${keyword.keywordId}" id="checkbox-${index}"></span>
        <span>${keyword.name}</span>
        <span>${keywordString[keyword.keywordType] || keyword.keywordType}</span>
        <span>${keyword.count}</span>
        <span>${keyword.isUse ? 'O' : 'X'}</span>
        <span><i class="fa fa-pencil edit-keyword" style="cursor: pointer;"></i></span>
    `;
      keywordContent.appendChild(div);

      const editIcon = div.querySelector('.edit-keyword');
      editIcon.addEventListener('click', () => {
        window.openModalWithKeyword(keyword);
      });
    });
  }

  // 공통 함수: 체크된 keywordId 목록 추출
  function getCheckedKeywordIds() {
    const checked = document.querySelectorAll('.keyword-checkbox:checked');
    return Array.from(checked).map(cb => {
      return parseInt(cb.getAttribute('data-id'))
    });
  }


// 활성화 버튼
  document.getElementById('active-keyword')?.addEventListener('click', () => {
    const keywordIds = getCheckedKeywordIds();
    if (keywordIds.length === 0) return alert('선택된 키워드가 없습니다.');

    fetch('/api/admin/keyword/active', {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
        [csrfHeader]: csrfToken
      },
      body: JSON.stringify({ keywordIds })
    })
    .then(res => {
      if (!res.ok) throw new Error('활성화 실패');
      return res.json();
    })
    .then(() => {
      alert('활성화 완료');
      const activeType = document.querySelector('.segment-button.active')?.innerText.trim();
      fetchKeywords(getKeywordTypeParam(activeType));
    })
    .catch(err => alert(err.message));
  });

// 비활성화 버튼
  document.getElementById('non-active-keyword')?.addEventListener('click', () => {
    const keywordIds = getCheckedKeywordIds();
    if (keywordIds.length === 0) return alert('선택된 키워드가 없습니다.');

    fetch('/api/admin/keyword/nonactive', {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
        [csrfHeader]: csrfToken
      },
      body: JSON.stringify({ keywordIds })
    })
    .then(res => {
      if (!res.ok) throw new Error('비활성화 실패');
      return res.json();
    })
    .then(() => {
      alert('비활성화 완료');
      const activeType = document.querySelector('.segment-button.active')?.innerText.trim();
      fetchKeywords(getKeywordTypeParam(activeType));
    })
    .catch(err => alert(err.message));
  });

});
