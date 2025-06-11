const monthlyBtn = document.getElementById('monthly');
const yearlyBtn = document.getElementById('yearly');

monthlyBtn.addEventListener('click', () => {
  monthlyBtn.classList.add('selected');
  yearlyBtn.classList.remove('selected');
  handlePeriodChange('monthly');
});

yearlyBtn.addEventListener('click', () => {
  yearlyBtn.classList.add('selected');
  monthlyBtn.classList.remove('selected');
  handlePeriodChange('yearly');
});

function fetchDiaryCount(period, date = null) {
  let url = `/api/diary/dashboard/count?period=${period}`;
  if (date) {
    url += `&date=${date}`;
  }

  fetch(url)
  .then(res => res.json())
  .then(count => {
    document.querySelector('.diary-count-number').textContent = count;
  })
  .catch(err => {
    console.error("일기 수 조회 실패:", err);
  });
}

function fetchKeywordRank(period, date = null) {
  let url = `/api/keyword/ranking?period=${period}`;
  if (date) {
    url += `&date=${date}`;
  }

  fetch(url)
  .then(res => res.json())
  .then(data => {
    const ranks = data.keywordRankList;
    const cardEls = document.querySelectorAll('.keyword-card');

    for (let i = 0; i < cardEls.length; i++) {
      const card = cardEls[i];
      const rankEl = card.querySelector('.rank');
      const wordEl = card.querySelector('.word');
      const countEl = card.querySelector('.count');

      if (i < ranks.length) {
        rankEl.textContent = `${i + 1}`;
        wordEl.textContent = ranks[i].name;
        countEl.textContent = `${ranks[i].count}회`;
      } else {
        rankEl.textContent = `${i + 1}`;
        wordEl.textContent = "-";
        countEl.textContent = "-회";
      }
    }
  })
  .catch(err => {
    console.error("키워드 랭킹 조회 실패:", err);
  });
}


// 기분 흐름 차트
function formatDate(date) {
  const year = date.getFullYear();
  const month = `${date.getMonth() + 1}`.padStart(2, '0');
  const day = `${date.getDate()}`.padStart(2, '0');
  return `${year}-${month}-${day}`;
}

function getMonthRange(date) {
  const year = date.getFullYear();
  const month = date.getMonth(); // 0-based
  const start = new Date(year, month, 1);
  const end = new Date(year, month + 1, 0);
  return {
    unit: 'day',
    displayFormat: 'M/d',
    min: formatDate(start), // ✅ 로컬 날짜 사용
    max: formatDate(end)
  };
}

function getYearRange(date) {
  const year = date.getFullYear();
  return {
    unit: 'month',
    displayFormat: 'M월',
    min: `${year}-01-01`,
    max: `${year}-12-01`
  };
}

let chart;

async function drawEmotionChart(period = 'monthly', date = null) {
  const emotionImageMap = window.emotionImageMap;
  const ctx = document.getElementById('emotionChart').getContext('2d');
  const emotionOrder = ['VERY_GOOD', 'GOOD', 'COMMON', 'BAD', 'VERY_BAD'];
  const orderedEmotions = emotionOrder.filter(e => emotionImageMap[e]);

  const moodValueMap = {};
  orderedEmotions.forEach((key, idx) => {
    moodValueMap[key] = orderedEmotions.length - idx;
  });

  const baseDate = date ? new Date(date) : new Date();
  const range = period === 'monthly' ? getMonthRange(baseDate) : getYearRange(baseDate);

  let url = period === 'monthly'
      ? `/api/diary/emotion/flow/monthly`
      : `/api/diary/emotion/flow/yearly?year=${baseDate.getFullYear()}`;
  if (date) {
    url += (url.includes('?') ? '&' : '?') + `date=${date}`;
  }

  const response = await fetch(url);
  const json = await response.json();

  // 🟢 월간: 감정 문자열을 숫자로 매핑
  let moodData = [];
  if (period === 'monthly') {
    const moodValueMap = {};
    orderedEmotions.forEach((key, idx) => {
      moodValueMap[key] = orderedEmotions.length - idx;
    });

    const diaryList = json.diaryDailyEmotionList;
    moodData = diaryList.map(item => ({
      x: new Date(item.date),               // YYYY-MM-DD
      y: moodValueMap[item.emotion]
    }));
  }

  // 🔵 연간: 월별 평균값을 1~12월로 가공
  if (period === 'yearly') {
    const monthlyList = json.diaryMonthlyEmotions;
    moodData = monthlyList.map(item => {
      const month = item.month.toString().padStart(2, '0');
      return {
        x: `${baseDate.getFullYear()}-${month}-01`,  // YYYY-MM-01 형식으로 맞춤
        y: item.average
      };
    });
  }

  // 기존 차트 제거
  if (chart) chart.destroy();

  chart = new Chart(ctx, {
    type: 'line',
    data: {
      datasets: [{
        data: moodData,
        borderColor: '#467750',
        tension: 0,
        pointRadius: 3,
        pointBackgroundColor: '#467750',
      }]
    },
    options: {
      layout: { padding: { right: 60 } },
      responsive: true,
      maintainAspectRatio: false,
      devicePixelRatio: window.devicePixelRatio || 1,
      plugins: {
        legend: { display: false },
        tooltip: { enabled: false }
      },
      scales: {
        x: {
          type: 'time',
          time: {
            unit: range.unit,
            displayFormats: { day: 'M/d', month: 'M월' }
          },
          min: range.min,
          max: range.max,
          ticks: {
            color: '#333',
            maxTicksLimit: period === 'monthly' ? 7 : 12,
            font: { size: 12 }
          },
          grid: { color: '#eee', drawBorder: false }
        },
        y: {
          min: 0.5,
          max: 5.5,
          ticks: {
            stepSize: 1,
            display: false
          },
          grid: {
            display: false,
            drawBorder: false
          }
        }
      }
    }
  });

  setTimeout(() => {
    const yAxis = chart.scales.y;
    const yAxisContainer = document.getElementById('yAxisLabels');
    if (!yAxis || !yAxisContainer) return;

    yAxisContainer.innerHTML = '';
    orderedEmotions.forEach((emotionKey) => {
      const yValue = moodValueMap[emotionKey];
      const yPixel = yAxis.getPixelForValue(yValue);
      const img = document.createElement('img');
      img.src = `/images/emotion/weather/${emotionImageMap[emotionKey]}`;
      img.alt = emotionKey;
      img.style.position = 'absolute';
      img.style.left = '0';
      img.style.top = `${yPixel}px`;
      img.style.width = '40px';
      img.style.height = '40px';
      img.style.transform = 'translateY(-50%)';
      yAxisContainer.appendChild(img);
    });
  }, 50);
}

function handlePeriodChange(period) {
  const today = new Date();
  const todayStr = today.toLocaleDateString('sv-SE'); // ✅ '2025-05-17' 형식 유지
  fetchDiaryCount(period, todayStr);
  fetchKeywordRank(period, todayStr);
  drawEmotionChart(period, todayStr);

  const value = period === 'monthly' ? today.getMonth() + 1 : today.getFullYear();
  renderEmotionDistribution(period, value);
}

// 기분 분포 막대 그래프
async function renderEmotionDistribution(period, value) {
  const emotionOrder = ['VERY_GOOD', 'GOOD', 'COMMON', 'BAD', 'VERY_BAD'];
  const emotionImageMap = window.emotionImageMap;

  const emotionColorMap = {
    VERY_GOOD: '#F3AA6C',
    GOOD: '#FFE086',
    COMMON: '#BBCF9A',
    BAD: '#B8CCD5',
    VERY_BAD: '#6A97AE'
  };

  const url = `/api/diary/emotion/count?period=${period}&value=${value}`;

  try {
    const response = await fetch(url);
    const json = await response.json();
    const emotionData = json.diaryEmotionCountList;

    // API에서 받아온 감정별 카운트를 Map으로 변환
    const emotionCountMap = {};
    emotionData.forEach(item => {
      emotionCountMap[item.emotion] = item.emotionCount;
    });

    const maxCount = Math.max(...emotionData.map(e => e.emotionCount));
    const container = document.getElementById('emotionBars');
    container.innerHTML = '';

    emotionOrder.forEach(emotionKey => {
      const count = emotionCountMap[emotionKey] ?? 0;

      const bar = document.createElement('div');
      bar.className = 'emotion-bar';

      const iconWrapper = document.createElement('div');
      iconWrapper.className = 'emotion-bar-icon';

      const img = document.createElement('img');
      img.src = `/images/emotion/weather/${emotionImageMap[emotionKey]}`;
      img.alt = emotionKey;
      img.className = 'emotion-bar-image';

      const track = document.createElement('div');
      track.className = 'emotion-bar-track';

      const fill = document.createElement('div');
      fill.className = 'emotion-bar-fill';
      fill.style.width = maxCount === 0 ? '0%' : `${(count / maxCount) * 100}%`;
      fill.style.backgroundColor = emotionColorMap[emotionKey];

      const countText = document.createElement('div');
      countText.className = 'emotion-bar-count';
      countText.textContent = count;

      iconWrapper.appendChild(img);
      track.appendChild(fill);
      bar.appendChild(iconWrapper);
      bar.appendChild(track);
      bar.appendChild(countText);
      container.appendChild(bar);
    });
  } catch (err) {
    console.error("감정 분포 조회 실패:", err);
  }
}

// DOM
window.addEventListener('DOMContentLoaded', () => {
  handlePeriodChange('monthly');
});

