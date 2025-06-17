document.addEventListener('DOMContentLoaded', () => {
  const modal = document.getElementById('keywordWriteModal');
  const openModalBtn = document.getElementById('add-keyword');
  const closeModalBtn = document.getElementById('modal-close');
  const submitBtn = document.getElementById('modal-submit');

  let modalTitle = document.getElementById('modal-title');
  let nameInput = document.getElementById('keyword-input-name');
  let typeSelect = document.getElementById('keyword-input-type');
  let specificTypeDiv = document.querySelector('.keyword-input-specific-type');
  let specificRadios = specificTypeDiv.querySelectorAll('input[name="specific-type"]');

  const typeInitial = {
    'EMOTION_GOOD': 'EMOTION',
    'EMOTION_BAD': 'EMOTION',
    'PERSON': 'PERSON',
    'SITUATION': 'SITUATION',
  };

  openModalBtn.addEventListener('click', () => {
    modal.style.display = 'flex';

    modalTitle.textContent = '키워드 등록';
    submitBtn.textContent = '등록';

    const buttons = document.querySelectorAll('.segment-button.active');
    typeSelect.value = buttons[0].dataset.type;
    toggleSpecificType();
  });
  closeModalBtn.addEventListener('click', () => {
    modal.style.display = 'none';
    clearModalFields();
  });
  window.addEventListener('click', (event) => {
    if (event.target === modal) {
      modal.style.display = 'none';
      clearModalFields();
    }
  });

  function toggleSpecificType() {
    if (typeSelect.value === 'EMOTION') {
      specificTypeDiv.style.display = 'block';
    } else {
      specificTypeDiv.style.display = 'none';
    }
  }

  typeSelect.addEventListener('change', () => {
    toggleSpecificType();
  });

  function setActiveKeywordType(type) {
    const buttons = document.querySelectorAll('.segment-button');
    buttons.forEach(button => {
      if (button.dataset.type === type) {
        button.classList.add('active');
      } else {
        button.classList.remove('active');
      }
    });
  }

  submitBtn.addEventListener('click', () => {
    const name = nameInput.value.trim();
    let keywordType = typeSelect.value;

    if (keywordType === 'EMOTION') {
      const specificTypeRadios = document.querySelectorAll('input[name="specific-type"]');
      let specificTypeValue = null;
      specificTypeRadios.forEach(radio => {
        if (radio.checked) specificTypeValue = radio.value;
      });

      if (specificTypeValue === 'GOOD') {
        keywordType = 'EMOTION_GOOD';
      } else if (specificTypeValue === 'BAD') {
        keywordType = 'EMOTION_BAD';
      }
    }

    if (!name) {
      alert('키워드 이름을 입력하세요.');
      return;
    }

    const keywordId = modal.getAttribute('data-keyword-id');
    const isUse = modal.getAttribute('data-keyword-is-use');
    const isEdit = !!keywordId;

    const requestBody = {
      keywordId: isEdit ? Number(keywordId) : null,
      name: name,
      type: keywordType,
      isUse: isEdit ? isUse : true
    };

    const url = '/api/admin/keyword';
    const method = isEdit ? 'PATCH' : 'POST';

    fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json',
      },
      body: isEdit ? JSON.stringify([requestBody]) : JSON.stringify(requestBody)
    })
    .then(res => {
      if (!res.ok) throw new Error(isEdit ? '수정 실패' : '등록 실패');
      const contentType = res.headers.get("content-type");
      if (contentType && contentType.includes("application/json")) {
        return res.json();
      } else {
        return {};
      }
    })
    .then(() => {
      modal.style.display = 'none';
      clearModalFields();
      modal.removeAttribute('data-keyword-id');

      const mainType = typeInitial[keywordType];
      fetchKeywords(mainType);
      setActiveKeywordType(mainType);
    })
    .catch(err => {
      alert((isEdit ? '수정' : '등록') + ' 중 오류가 발생했습니다.');
      console.error(err);
    });
  });

  function clearModalFields() {
    document.getElementById('keyword-input-name').value = '';

    const typeSelect = document.getElementById('keyword-input-type');
    typeSelect.value = 'EMOTION';

    const firstRadio = document.querySelector('input[name="specific-type"][value="GOOD"]');
    if (firstRadio) firstRadio.checked = true;

    toggleSpecificType();
  }

  function openModalWithKeyword(keyword) {
    modal.style.display = 'flex';
    modalTitle.textContent = '키워드 편집';
    submitBtn.textContent = '저장';

    modal.setAttribute('data-keyword-id', keyword.keywordId);
    modal.setAttribute('data-keyword-is-use', keyword.isUse);

    nameInput.value = keyword.name;
    let generalType = typeInitial[keyword.keywordType];
    typeSelect.value = generalType;

    if (generalType === 'EMOTION') {
      specificTypeDiv.style.display = 'block';

      const specificValue = keyword.keywordType.includes('GOOD') ? 'GOOD' : 'BAD';

      specificRadios.forEach(radio => {
        radio.checked = (radio.value === specificValue);
      });
    } else {
      specificTypeDiv.style.display = 'none';

      specificRadios.forEach(radio => {
        radio.checked = false;
      });
    }

    modal.setAttribute('data-keyword-id', keyword.keywordId);
  }

  window.openModalWithKeyword = openModalWithKeyword;
});
