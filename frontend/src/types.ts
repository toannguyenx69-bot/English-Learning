export type AuthUser = {
  email: string;
  username: string;
};

export type PronunciationEntry = {
  accent: string;
  ipa?: string | null;
  audioUrl?: string | null;
};

export type VocabularyPronunciation = {
  vocabularyId: number;
  word: string;
  pronunciations: PronunciationEntry[];
};
