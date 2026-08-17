import api from './client';
import type { VocabularyPronunciation } from '../types';

export async function getVocabularyPronunciation(id: number): Promise<VocabularyPronunciation> {
  const response = await api.get<VocabularyPronunciation>(`/api/v1/vocabularies/${id}/pronunciation`);
  return response.data;
}
