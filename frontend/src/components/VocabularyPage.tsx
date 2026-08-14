import React, { useEffect, useMemo, useState } from 'react';
import {
  getLearnedProgress,
  getLearnedVocabularies,
  getVocabularies,
  getVocabularyById,
  markVocabularyAsLearned,
  unmarkVocabularyAsLearned,
  Vocabulary,
} from '../api/vocabulary';
import VocabularyList from './VocabularyList';
import VocabularyDetail from './VocabularyDetail';

export default function VocabularyPage() {
  const [items, setItems] = useState<Vocabulary[]>([]);
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [selectedVocabulary, setSelectedVocabulary] = useState<Vocabulary | null>(null);
  const [learnedIds, setLearnedIds] = useState<number[]>([]);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [pageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [loadingList, setLoadingList] = useState(false);
  const [loadingDetail, setLoadingDetail] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [detailError, setDetailError] = useState<string | null>(null);
  const [progress, setProgress] = useState<{ totalLearned: number; totalVocabularies: number; progressPercent: number } | null>(null);

  const loadLearned = async () => {
    try {
      const learned = await getLearnedVocabularies();
      setLearnedIds(learned.map((item) => item.vocabularyId));
    } catch (err) {
      console.error('Failed to load learned vocabularies', err);
    }
  };

  const loadProgress = async () => {
    try {
      const result = await getLearnedProgress();
      setProgress(result);
    } catch (err) {
      console.error('Failed to load progress', err);
    }
  };

  useEffect(() => {
    loadLearned();
    loadProgress();
  }, []);

  useEffect(() => {
    const fetchPage = async () => {
      setLoadingList(true);
      setError(null);
      try {
        const result = await getVocabularies({
          page,
          size: pageSize,
          q: search.trim() || undefined,
          sortBy: 'word',
          sortDir: 'asc',
        });
        setItems(result.content);
        setTotalPages(result.totalPages);
        if (result.content.length > 0 && !selectedId) {
          setSelectedId(result.content[0].id);
        }
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Unable to load vocabulary');
      } finally {
        setLoadingList(false);
      }
    };

    fetchPage();
  }, [page, pageSize, search]);

  useEffect(() => {
    if (!selectedId) {
      setSelectedVocabulary(null);
      return;
    }

    const fetchDetail = async () => {
      setLoadingDetail(true);
      setDetailError(null);
      try {
        const item = await getVocabularyById(selectedId);
        setSelectedVocabulary(item);
      } catch (err) {
        setDetailError(err instanceof Error ? err.message : 'Unable to load vocabulary details');
      } finally {
        setLoadingDetail(false);
      }
    };

    fetchDetail();
  }, [selectedId]);

  const toggleLearned = async (id: number) => {
    const alreadyLearned = learnedIds.includes(id);
    try {
      if (alreadyLearned) {
        await unmarkVocabularyAsLearned(id);
        setLearnedIds((prev) => prev.filter((value) => value !== id));
      } else {
        await markVocabularyAsLearned(id);
        setLearnedIds((prev) => [...prev, id]);
      }

      const updatedProgress = await getLearnedProgress();
      setProgress(updatedProgress);
      if (selectedId === id && selectedVocabulary) {
        setSelectedVocabulary({ ...selectedVocabulary });
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unable to update learned status');
    }
  };

  const totalPagesLabel = useMemo(() => Math.max(totalPages, 1), [totalPages]);

  return (
    <div style={{ display: 'grid', gridTemplateColumns: '1.2fr 1fr', gap: 20, padding: 24 }}>
      <div>
        <h1>Vocabulary</h1>

        <div style={{ display: 'flex', gap: 12, marginBottom: 16, alignItems: 'center' }}>
          <input
            value={search}
            onChange={(event) => {
              setPage(0);
              setSearch(event.target.value);
            }}
            placeholder="Search vocabulary"
            style={{ flex: 1, padding: 10, borderRadius: 6, border: '1px solid #ccc' }}
          />
        </div>

        {progress && (
          <div style={{ marginBottom: 16, padding: 12, border: '1px solid #d9d9d9', borderRadius: 8 }}>
            <strong>Progress:</strong> {progress.totalLearned}/{progress.totalVocabularies} ({progress.progressPercent.toFixed(1)}%)
          </div>
        )}

        <VocabularyList
          vocabularies={items}
          loading={loadingList}
          error={error}
          onSelect={setSelectedId}
          onToggleLearned={toggleLearned}
          learnedIds={learnedIds}
        />

        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 16 }}>
          <button type="button" disabled={page <= 0} onClick={() => setPage((prev) => Math.max(prev - 1, 0))}>
            Previous
          </button>
          <span>
            Page {page + 1} / {totalPagesLabel}
          </span>
          <button type="button" disabled={page >= totalPagesLabel - 1} onClick={() => setPage((prev) => prev + 1)}>
            Next
          </button>
        </div>
      </div>

      <VocabularyDetail
        vocabulary={selectedVocabulary}
        loading={loadingDetail}
        error={detailError}
        learned={selectedId ? learnedIds.includes(selectedId) : false}
        onToggleLearned={toggleLearned}
      />
    </div>
  );
}
