document.addEventListener('DOMContentLoaded', () => {
  const aiContent = document.getElementById('aiContent');

  if (aiContent) {
    fetchAis();
  }

  document.getElementById('add-ai')?.addEventListener('click', () => {
    localStorage.removeItem('editingAiId');
    window.location.href = 'ai/write';
  });

  document.getElementById('selectAll')?.addEventListener('change', function () {
    const checkboxes = document.querySelectorAll('.ai-checkbox');
    checkboxes.forEach(cb => cb.checked = this.checked);
  });

  function fetchAis() {
    fetch(`/api/admin/ai`)
    .then(response => response.json())
    .then(data => {
      renderAis(data.aiInfos);
    })
    .catch(err => {
      console.error('AI 캐릭터 불러오기 실패:', err);
      aiContent.innerHTML = '<p>데이터를 불러오지 못했습니다.</p>';
    });
  }

  function renderAis(ais) {
    aiContent.innerHTML = '';
    if (!ais || ais.length === 0) {
      return;
    }

    ais.forEach((item, index) => {
      const div = document.createElement('div');
      div.className = 'ai-item';
      div.innerHTML = `
        <span id="data-ai-id" style="display: none;">${item}</span>
        <span><input type="checkbox" class="ai-checkbox" data-id="${item.aiId}" id="checkbox-${index}"></span>
        <span>${item.name}</span>
        <span>${item.count}</span>
        <span>${item.isUse ? 'O' : 'X'}</span>
        <span><i class="fa fa-pencil edit-ai" style="cursor: pointer;"></i></span>
    `;
      aiContent.appendChild(div);

      const editIcon = div.querySelector('.edit-ai');
      editIcon.addEventListener('click', () => {
        localStorage.setItem('editingAiId', item.aiId);
        window.location.href = 'ai/write';
      });
    });
  }

  function getCheckedAiIds() {
    const checked = document.querySelectorAll('.ai-checkbox:checked');
    return Array.from(checked).map(cb => {
      return parseInt(cb.getAttribute('data-id'))
    });
  }

  document.getElementById('active-ai')?.addEventListener('click', () => {
    toggleAiActivation(true);
  });

  document.getElementById('non-active-ai')?.addEventListener('click', () => {
    toggleAiActivation(false);
  });

  function toggleAiActivation(isUse) {
    const aiIds = getCheckedAiIds();
    if (aiIds.length === 0) return alert('선택된 캐릭터가 없습니다.');

    const requestBody = {
      aiIds: aiIds,
      isUse: isUse
    };

    fetch('/api/admin/ai/status', {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(requestBody)
    })
    .then(res => {
      if (!res.ok) throw new Error(isUse ? '활성화 실패' : '비활성화 실패');
      return res.json();
    })
    .then(() => {
      alert(isUse ? '활성화 완료' : '비활성화 완료');
      fetchAis();
    })
    .catch(err => alert(err.message));
  }

  document.getElementById('delete-ai')?.addEventListener('click', () => {
    const aiIds = getCheckedAiIds();
    if (aiIds.length === 0) return alert('선택된 캐릭터가 없습니다.');

    fetch('/api/admin/ai/delete', {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(aiIds)
    })
    .then(res => {
      if (!res.ok) throw new Error('삭제 실패');
      return res.json();
    })
    .then(() => {
      alert('삭제 완료');
      fetchAis();
    })
    .catch(err => alert(err.message));
  });

  window.fetchAis = fetchAis;
  window.renderAis = renderAis;
});
