import React from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import AppLayout from './components/AppLayout';
import ProtectedRoute from './components/ProtectedRoute';
import DashboardPage from './components/DashboardPage';
import VocabularyPage from './components/VocabularyPage';
import VocabularyImportPage from './components/VocabularyImportPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ProfilePage from './pages/ProfilePage';
import GrammarPage from './pages/GrammarPage';
import PdfScannerPage from './pages/PdfScannerPage';

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />

      <Route element={<ProtectedRoute />}>
        <Route element={<AppLayout />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/vocabulary" element={<VocabularyPage />} />
          <Route path="/vocabulary-import" element={<VocabularyImportPage />} />
          <Route path="/grammar" element={<GrammarPage />} />
          <Route path="/pdf-scanner" element={<PdfScannerPage />} />
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
        </Route>
      </Route>
    </Routes>
  );
}
