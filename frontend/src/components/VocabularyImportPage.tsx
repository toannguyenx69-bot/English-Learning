import React, { ChangeEvent, useMemo, useState } from 'react';
import { importSelectedVocabularyItems, previewVocabularyImport, type ImportRow } from '../api/vocabulary';

export default function VocabularyImportPage() {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [preview, setPreview] = useState<{
    totalRows: number;
    newItems: ImportRow[];
    existingItems: ImportRow[];
    invalidItems: ImportRow[];
  } | null>(null);
  const [loading, setLoading] = useState(false);
  const [importing, setImporting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [selectedNewItems, setSelectedNewItems] = useState<Record<number, boolean>>({});

  const newItems = preview?.newItems ?? [];
  const existingItems = preview?.existingItems ?? [];
  const invalidItems = preview?.invalidItems ?? [];

  const totalSelectedToImport = useMemo(
    () => Object.values(selectedNewItems).filter(Boolean).length,
    [selectedNewItems]
  );

  const handleFileChange = (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0] ?? null;
    setSelectedFile(file);
    setPreview(null);
    setSelectedNewItems({});
    setError(null);
    setSuccess(null);
  };

  const previewImport = async () => {
    if (!selectedFile) {
      setError('Please choose an Excel file first.');
      return;
    }

    setLoading(true);
    setError(null);
    setSuccess(null);

    try {
      const result = await previewVocabularyImport(selectedFile);
      const initialSelection: Record<number, boolean> = {};
      result.newItems.forEach((item) => {
        const key = item.rowNumber ?? 0;
        initialSelection[key] = true;
      });
      setSelectedNewItems(initialSelection);
      setPreview(result);
    } catch (apiError) {
      setError(apiError instanceof Error ? apiError.message : 'Unable to preview the Excel file.');
    } finally {
      setLoading(false);
    }
  };

  const toggleNewItem = (rowNumber: number) => {
    setSelectedNewItems((prev) => ({
      ...prev,
      [rowNumber]: !prev[rowNumber],
    }));
  };

  const handleImport = async () => {
    if (!preview || totalSelectedToImport === 0) {
      setError('Select at least one new word to import.');
      return;
    }

    const selectedItems = preview.newItems.filter((item) => {
      const key = item.rowNumber ?? 0;
      return selectedNewItems[key];
    });

    setImporting(true);
    setError(null);
    setSuccess(null);

    try {
      const result = await importSelectedVocabularyItems(selectedItems);
      setSuccess(result.message || `Imported ${result.importedCount} vocabulary items.`);
      setPreview(null);
      setSelectedFile(null);
      const input = document.getElementById('excel-import-file') as HTMLInputElement | null;
      if (input) {
        input.value = '';
      }
    } catch (apiError) {
      setError(apiError instanceof Error ? apiError.message : 'Import failed.');
    } finally {
      setImporting(false);
    }
  };

  return (
    <div style={{ maxWidth: 1200, margin: '0 auto', padding: 24 }}>
      <h1>Import Vocabulary from Excel</h1>

      <div style={{ background: '#fff', border: '1px solid #dfe3ec', borderRadius: 12, padding: 20, marginBottom: 24 }}>
        <div style={{ display: 'flex', gap: 12, alignItems: 'center', flexWrap: 'wrap' }}>
          <input
            id="excel-import-file"
            type="file"
            accept=".xlsx,.csv"
            onChange={handleFileChange}
          />
          <button type="button" onClick={previewImport} disabled={!selectedFile || loading}>
            {loading ? 'Previewing...' : 'Preview File'}
          </button>
        </div>

        {selectedFile && (
          <div style={{ marginTop: 12, color: '#334155' }}>
            Selected file: {selectedFile.name}
          </div>
        )}
      </div>

      {error && (
        <div style={{ background: '#fee2e2', color: '#991b1b', padding: 12, borderRadius: 8, marginBottom: 16 }}>
          {error}
        </div>
      )}

      {success && (
        <div style={{ background: '#dcfce7', color: '#166534', padding: 12, borderRadius: 8, marginBottom: 16 }}>
          {success}
        </div>
      )}

      {preview && (
        <>
          <div style={{ marginBottom: 12, fontWeight: 700 }}>
            Total rows in file: {preview.totalRows}
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: 20 }}>
            <div style={{ background: '#fff', border: '1px solid #dfe3ec', borderRadius: 12, padding: 16 }}>
              <h3>New words</h3>
              {newItems.length === 0 ? (
                <p>No new words.</p>
              ) : (
                <div style={{ display: 'grid', gap: 8 }}>
                  {newItems.map((item) => {
                    const rowNumber = item.rowNumber ?? 0;
                    const checked = !!selectedNewItems[rowNumber];
                    return (
                      <label key={rowNumber} style={{ display: 'flex', gap: 10, alignItems: 'flex-start', padding: 10, border: '1px solid #e2e8f0', borderRadius: 8 }}>
                        <input type="checkbox" checked={checked} onChange={() => toggleNewItem(rowNumber)} />
                        <div>
                          <div style={{ fontWeight: 700 }}>{item.word}</div>
                          <div style={{ color: '#475569', fontSize: 13 }}>{item.meaning || 'No meaning from dictionary'}</div>
                        </div>
                      </label>
                    );
                  })}
                </div>
              )}
            </div>

            <div style={{ background: '#fff', border: '1px solid #dfe3ec', borderRadius: 12, padding: 16 }}>
              <h3>Already exists</h3>
              {existingItems.length === 0 ? (
                <p>No existing words.</p>
              ) : (
                <div style={{ display: 'grid', gap: 8 }}>
                  {existingItems.map((item) => (
                    <div key={item.rowNumber ?? item.word} style={{ padding: 10, border: '1px solid #e2e8f0', borderRadius: 8 }}>
                      <div style={{ fontWeight: 700 }}>{item.word}</div>
                      <div style={{ color: '#475569', fontSize: 13 }}>{item.message}</div>
                    </div>
                  ))}
                </div>
              )}
            </div>

            <div style={{ background: '#fff', border: '1px solid #dfe3ec', borderRadius: 12, padding: 16 }}>
              <h3>Invalid</h3>
              {invalidItems.length === 0 ? (
                <p>No invalid rows.</p>
              ) : (
                <div style={{ display: 'grid', gap: 8 }}>
                  {invalidItems.map((item) => (
                    <div key={item.rowNumber ?? item.word} style={{ padding: 10, border: '1px solid #e2e8f0', borderRadius: 8 }}>
                      <div style={{ fontWeight: 700 }}>{item.word || '(empty)'}</div>
                      <div style={{ color: '#475569', fontSize: 13 }}>{item.message}</div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>

          <div style={{ marginTop: 20, display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 12, flexWrap: 'wrap' }}>
            <div style={{ fontWeight: 700 }}>
              Selected new vocabulary: {totalSelectedToImport}
            </div>
            <button type="button" onClick={handleImport} disabled={importing || totalSelectedToImport === 0}>
              {importing ? 'Importing...' : 'Import new vocabulary'}
            </button>
          </div>
        </>
      )}
    </div>
  );
}
