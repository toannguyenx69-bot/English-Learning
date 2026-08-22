import api from './client';

export type BoldTextResult = {
  page: number;
  text: string;
  x?: number;
  y?: number;
  width?: number;
  height?: number;
};

export type PdfScanMode = 'BOLD' | 'ITALIC' | 'HIGHLIGHT';

export type PdfUploadResponse = {
  fileId: string;
  fileName: string;
  size: number;
  message: string;
};

export type PdfScanResponse = {
  fileId: string;
  fileName: string;
  scanMode: PdfScanMode;
  boldTexts: BoldTextResult[];
};

export async function uploadPdf(file: File): Promise<PdfUploadResponse> {
  const formData = new FormData();
  formData.append('file', file);

  const response = await api.post<PdfUploadResponse>('/api/v1/pdf/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });

  return response.data;
}

export async function scanPdf(fileId: string, scanMode: PdfScanMode = 'BOLD'): Promise<PdfScanResponse> {
  const response = await api.post<PdfScanResponse>(`/api/v1/pdf/${fileId}/scan`, { scanMode });
  return response.data;
}

export async function exportPdfExcel(fileId: string): Promise<Blob> {
  const response = await api.get(`/api/v1/pdf/${fileId}/export-excel`, {
    responseType: 'blob',
  });
  return response.data;
}
