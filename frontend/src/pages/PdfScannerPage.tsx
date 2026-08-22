import React, { ChangeEvent, useRef, useState } from 'react';
import { exportPdfExcel, scanPdf, uploadPdf, type BoldTextResult, type PdfScanMode } from '../api/pdf';

export default function PdfScannerPage() {
  const fileInputRef = useRef<HTMLInputElement | null>(null);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [uploadStatus, setUploadStatus] = useState<'idle' | 'uploading' | 'uploaded' | 'error'>('idle');
  const [scanStatus, setScanStatus] = useState<'idle' | 'scanning' | 'success' | 'error'>('idle');
  const [uploadedFileId, setUploadedFileId] = useState<string | null>(null);
  const [uploadedFileName, setUploadedFileName] = useState<string>('');
  const [results, setResults] = useState<BoldTextResult[]>([]);
  const [scanMode, setScanMode] = useState<PdfScanMode>('BOLD');
  const [errorMessage, setErrorMessage] = useState<string>('');
  const [successMessage, setSuccessMessage] = useState<string>('');

  const resetSelection = () => {
    setSelectedFile(null);
    setUploadedFileId(null);
    setUploadedFileName('');
    setResults([]);
    setScanStatus('idle');
    setUploadStatus('idle');
    setErrorMessage('');
    setSuccessMessage('');
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  const handleFileChange = (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0] ?? null;
    setResults([]);
    setScanStatus('idle');
    setUploadedFileId(null);
    setUploadedFileName('');
    setSuccessMessage('');
    setErrorMessage('');

    if (!file) {
      setSelectedFile(null);
      setUploadStatus('idle');
      return;
    }

    const isPdf = file.type === 'application/pdf' || file.name.toLowerCase().endsWith('.pdf');
    if (!isPdf) {
      setSelectedFile(null);
      setUploadStatus('error');
      setErrorMessage('Only PDF files are allowed.');
      return;
    }

    if (file.size === 0) {
      setSelectedFile(null);
      setUploadStatus('error');
      setErrorMessage('The selected PDF is empty.');
      return;
    }

    setSelectedFile(file);
    setUploadStatus('idle');
    setErrorMessage('');
  };

  const handleUpload = async () => {
    if (!selectedFile) {
      setUploadStatus('error');
      setErrorMessage('Please select a PDF file first.');
      return;
    }

    setUploadStatus('uploading');
    setErrorMessage('');
    setSuccessMessage('');
    setResults([]);

    try {
      const response = await uploadPdf(selectedFile);
      setUploadedFileId(response.fileId);
      setUploadedFileName(response.fileName || selectedFile.name);
      setUploadStatus('uploaded');
      setSuccessMessage(response.message || 'PDF uploaded successfully.');
    } catch (error) {
      setUploadStatus('error');
      setUploadedFileId(null);
      setUploadedFileName('');
      setErrorMessage(error instanceof Error ? error.message : 'PDF upload failed. Please try again.');
    }
  };

  const handleScan = async () => {
    if (!uploadedFileId) {
      setScanStatus('error');
      setErrorMessage('Upload a PDF before scanning.');
      return;
    }

    if (scanStatus === 'scanning') {
      return;
    }

    setScanStatus('scanning');
    setErrorMessage('');
    setSuccessMessage('');

    try {
      const response = await scanPdf(uploadedFileId, scanMode);
      setResults(response.boldTexts ?? []);
      setScanStatus(response.boldTexts && response.boldTexts.length > 0 ? 'success' : 'error');

      if (!response.boldTexts || response.boldTexts.length === 0) {
        const modeLabel = scanMode.toLowerCase();
        setErrorMessage(`No ${modeLabel} text was found in the PDF.`);
      }
    } catch (error) {
      setScanStatus('error');
      setErrorMessage(error instanceof Error ? error.message : 'Unable to scan the PDF.');
    }
  };

  const isUploadDisabled = !selectedFile || uploadStatus === 'uploading';
  const isScanDisabled = !uploadedFileId || scanStatus === 'scanning' || uploadStatus !== 'uploaded';

  const handleExportExcel = async () => {
    if (!uploadedFileId) {
      setErrorMessage('Upload and scan a PDF before exporting to Excel.');
      return;
    }

    try {
      const blob = await exportPdfExcel(uploadedFileId);
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = 'bold-paragraphs.xlsx';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
      setSuccessMessage('Excel file downloaded successfully.');
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : 'Unable to export Excel file.');
    }
  };

  return (
    <div style={{ maxWidth: 900, margin: '40px auto', padding: 24, fontFamily: 'Arial, sans-serif' }}>
      <h1 style={{ marginBottom: 24 }}>PDF Scanner</h1>

      <div style={{ border: '1px solid #dfe3ec', background: '#fff', borderRadius: 12, padding: 24 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 12, flexWrap: 'wrap' }}>
          <input
            ref={fileInputRef}
            type="file"
            accept="application/pdf"
            onChange={handleFileChange}
            style={{ display: 'none' }}
          />

          <button
            type="button"
            onClick={() => fileInputRef.current?.click()}
            style={{
              padding: '10px 16px',
              border: 'none',
              borderRadius: 8,
              background: '#2563eb',
              color: '#fff',
              fontWeight: 700,
              cursor: 'pointer',
            }}
          >
            Upload PDF File
          </button>

          <button
            type="button"
            onClick={handleUpload}
            disabled={isUploadDisabled}
            style={{
              padding: '10px 16px',
              border: 'none',
              borderRadius: 8,
              background: uploadStatus === 'uploading' ? '#94a3b8' : '#16a34a',
              color: '#fff',
              fontWeight: 700,
              cursor: isUploadDisabled ? 'not-allowed' : 'pointer',
              opacity: isUploadDisabled ? 0.7 : 1,
            }}
          >
            {uploadStatus === 'uploading' ? 'Uploading...' : 'Upload'}
          </button>

          <div style={{ display: 'flex', alignItems: 'center', gap: 8, flexWrap: 'wrap' }}>
            <label htmlFor="pdf-scan-mode" style={{ fontWeight: 600 }}>Scan mode:</label>
            <select
              id="pdf-scan-mode"
              value={scanMode}
              onChange={(event) => setScanMode(event.target.value as PdfScanMode)}
              style={{ padding: '10px 12px', borderRadius: 8, border: '1px solid #cbd5e1' }}
            >
              <option value="BOLD">Bold</option>
              <option value="ITALIC">Italic</option>
              <option value="HIGHLIGHT">Highlight</option>
            </select>
          </div>

          <button
            type="button"
            onClick={handleScan}
            disabled={isScanDisabled}
            style={{
              padding: '10px 16px',
              border: 'none',
              borderRadius: 8,
              background: isScanDisabled ? '#94a3b8' : '#0f172a',
              color: '#fff',
              fontWeight: 700,
              cursor: isScanDisabled ? 'not-allowed' : 'pointer',
              opacity: isScanDisabled ? 0.7 : 1,
            }}
          >
            {scanStatus === 'scanning' ? 'Scanning...' : 'Scan PDF File'}
          </button>

          <button
            type="button"
            onClick={handleExportExcel}
            disabled={scanStatus !== 'success' || !uploadedFileId}
            style={{
              padding: '10px 16px',
              border: 'none',
              borderRadius: 8,
              background: scanStatus === 'success' ? '#7c3aed' : '#94a3b8',
              color: '#fff',
              fontWeight: 700,
              cursor: scanStatus === 'success' ? 'pointer' : 'not-allowed',
              opacity: scanStatus === 'success' ? 1 : 0.7,
            }}
          >
            Export Excel
          </button>

          {selectedFile && (
            <button
              type="button"
              onClick={resetSelection}
              style={{
                padding: '10px 16px',
                border: '1px solid #cbd5e1',
                borderRadius: 8,
                background: '#fff',
                cursor: 'pointer',
              }}
            >
              Reset
            </button>
          )}
        </div>

        <div style={{ marginTop: 24 }}>
          <strong>Selected file:</strong>{' '}
          {selectedFile ? `${selectedFile.name} (${(selectedFile.size / 1024 / 1024).toFixed(2)} MB)` : 'No file selected'}
        </div>

        {uploadedFileName && (
          <div style={{ marginTop: 8, color: '#166534' }}>
            Uploaded file: {uploadedFileName}
          </div>
        )}

        {errorMessage && (
          <div style={{ marginTop: 20, color: '#b91c1c', background: '#fee2e2', borderRadius: 8, padding: 12 }}>
            {errorMessage}
          </div>
        )}

        {successMessage && (
          <div style={{ marginTop: 20, color: '#166534', background: '#dcfce7', borderRadius: 8, padding: 12 }}>
            {successMessage}
          </div>
        )}
      </div>

      <div style={{ marginTop: 32 }}>
        <h2>Results</h2>

        {results.length === 0 && scanStatus !== 'scanning' && (
          <p style={{ color: '#475569' }}>No {scanMode.toLowerCase()} text results yet.</p>
        )}

        <div style={{ display: 'grid', gap: 12 }}>
          {results.map((item, index) => (
            <div key={`${item.page}-${item.text}-${index}`} style={{ border: '1px solid #dfe3ec', borderRadius: 10, background: '#fff', padding: 16 }}>
              <div style={{ fontWeight: 700, marginBottom: 6 }}>Page {item.page}</div>
              <div style={{ color: '#0f172a', whiteSpace: 'pre-wrap', lineHeight: 1.7, fontSize: 16 }}>{item.text}</div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
