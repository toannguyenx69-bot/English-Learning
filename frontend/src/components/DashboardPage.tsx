import React, { useEffect, useMemo, useState } from 'react';
import { getLearnedProgress } from '../api/vocabulary';
import { getGrammarStatistics } from '../api/grammar';

type DashboardCardProps = {
  title: string;
  value: string;
  subtitle?: string;
};

function DashboardCard({ title, value, subtitle }: DashboardCardProps) {
  return (
    <div style={{ border: '1px solid #d9d9d9', borderRadius: 12, padding: 20, background: '#fff' }}>
      <div style={{ color: '#666', fontSize: 14, marginBottom: 8 }}>{title}</div>
      <div style={{ fontSize: 28, fontWeight: 700 }}>{value}</div>
      {subtitle && <div style={{ color: '#666', marginTop: 8 }}>{subtitle}</div>}
    </div>
  );
}

export default function DashboardPage() {
  const [vocabularyProgress, setVocabularyProgress] = useState<{ totalLearned: number; totalVocabularies: number; progressPercent: number } | null>(null);
  const [grammarStatistics, setGrammarStatistics] = useState<{ totalAttempts: number; totalCorrect: number; accuracyRate: number } | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadDashboard = async () => {
      setLoading(true);
      setError(null);

      try {
        const [vocabProgress, grammarStats] = await Promise.all([
          getLearnedProgress(),
          getGrammarStatistics(),
        ]);

        setVocabularyProgress(vocabProgress);
        setGrammarStatistics(grammarStats);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Unable to load dashboard data.');
      } finally {
        setLoading(false);
      }
    };

    loadDashboard();
  }, []);

  const learningProgress = useMemo(() => {
    if (!vocabularyProgress) {
      return '0.0%';
    }

    return `${vocabularyProgress.progressPercent.toFixed(1)}%`;
  }, [vocabularyProgress]);

  return (
    <div style={{ maxWidth: 1000, margin: '40px auto', padding: 24, fontFamily: 'Arial, sans-serif' }}>
      <h1 style={{ marginBottom: 24 }}>Dashboard</h1>

      {loading && <p>Loading dashboard...</p>}
      {error && <p style={{ color: 'crimson' }}>Error: {error}</p>}

      {!loading && !error && (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: 16 }}>
          <DashboardCard
            title="Vocabulary learned"
            value={vocabularyProgress ? String(vocabularyProgress.totalLearned) : '0'}
            subtitle={vocabularyProgress ? `${vocabularyProgress.totalVocabularies} total words` : 'No data'}
          />

          <DashboardCard
            title="Grammar questions attempted"
            value={grammarStatistics ? String(grammarStatistics.totalAttempts) : '0'}
            subtitle="Total recorded attempts"
          />

          <DashboardCard
            title="Grammar accuracy"
            value={grammarStatistics ? `${grammarStatistics.accuracyRate.toFixed(1)}%` : '0.0%'}
            subtitle={grammarStatistics ? `${grammarStatistics.totalCorrect} correct answers` : 'No data'}
          />

          <DashboardCard
            title="Learning progress"
            value={learningProgress}
            subtitle={vocabularyProgress ? `${vocabularyProgress.totalLearned}/${vocabularyProgress.totalVocabularies} learned` : 'No data'}
          />
        </div>
      )}
    </div>
  );
}
