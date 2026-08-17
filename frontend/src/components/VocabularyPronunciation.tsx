import React, { useEffect, useState } from 'react';
import { getVocabularyPronunciation } from '../api/pronunciation';
import type { PronunciationEntry, VocabularyPronunciation as VocabularyPronunciationData } from '../types';

type Props = {
  vocabularyId: number;
  word: string;
};

export default function VocabularyPronunciation({ vocabularyId, word }: Props) {
  const [data, setData] = useState<VocabularyPronunciationData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let mounted = true;

    const loadPronunciation = async () => {
      setLoading(true);
      setError(null);

      try {
        const result = await getVocabularyPronunciation(vocabularyId);
        if (mounted) {
          setData(result);
        }
      } catch (err) {
        if (mounted) {
          setError(err instanceof Error ? err.message : 'Unable to load pronunciation');
          setData(null);
        }
      } finally {
        if (mounted) {
          setLoading(false);
        }
      }
    };

    loadPronunciation();

    return () => {
      mounted = false;
    };
  }, [vocabularyId]);

  if (loading) {
    return (
      <div style={{ margin: '16px 0', color: '#666', fontSize: 14 }}>
        Loading pronunciation...
      </div>
    );
  }

  if (error) {
    return (
      <div style={{ margin: '16px 0', color: '#b42318', fontSize: 14 }}>
        Unable to load pronunciation.
      </div>
    );
  }

  const pronunciations = data?.pronunciations ?? [];

  if (!pronunciations.length) {
    return (
      <div style={{ margin: '16px 0', color: '#666', fontSize: 14 }}>
        No pronunciation available.
      </div>
    );
  }

  return (
    <div style={{ margin: '16px 0', padding: 12, border: '1px solid #e5e7eb', borderRadius: 8, background: '#f9fafb' }}>
      <div style={{ fontSize: 18, fontWeight: 600, marginBottom: 8 }}>{word}</div>
      {pronunciations.map((entry: PronunciationEntry) => (
        <div
          key={`${entry.accent}-${entry.ipa ?? 'no-ipa'}-${entry.audioUrl ?? 'no-audio'}`}
          style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 6 }}
        >
          <span>
            <strong>{entry.accent}</strong> {entry.ipa || '—'}
          </span>
          {(entry.audioUrl || typeof window !== 'undefined') && (
            <button
              type="button"
              onClick={() => {
                if (entry.audioUrl) {
                  const audio = new Audio(entry.audioUrl);
                  audio.play().catch(() => {
                    if (typeof window !== 'undefined' && 'speechSynthesis' in window) {
                      window.speechSynthesis.cancel();
                      const utterance = new SpeechSynthesisUtterance(word);
                      utterance.lang = entry.accent === 'UK' ? 'en-GB' : 'en-US';
                      window.speechSynthesis.speak(utterance);
                    }
                  });
                  return;
                }

                if (typeof window !== 'undefined' && 'speechSynthesis' in window) {
                  window.speechSynthesis.cancel();
                  const utterance = new SpeechSynthesisUtterance(word);
                  utterance.lang = entry.accent === 'UK' ? 'en-GB' : 'en-US';
                  window.speechSynthesis.speak(utterance);
                }
              }}
              aria-label={`Play ${entry.accent} pronunciation`}
              title={`Play ${entry.accent} pronunciation`}
              style={{
                border: '1px solid #d0d7de',
                borderRadius: 999,
                background: '#fff',
                width: 28,
                height: 28,
                cursor: 'pointer',
                fontSize: 14,
              }}
            >
              🔊
            </button>
          )}
        </div>
      ))}
    </div>
  );
}
