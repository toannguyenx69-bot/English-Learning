import React, { useEffect, useState } from 'react'
import { getHealth, HealthResponse } from './api/health'

export default function App() {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [status, setStatus] = useState<string | null>(null)

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    setError(null)
    getHealth()
      .then((res: HealthResponse) => {
        if (!cancelled) setStatus(res.status)
      })
      .catch((err) => {
        if (!cancelled) setError(err?.message || 'Unknown error')
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })

    return () => {
      cancelled = true
    }
  }, [])

  return (
    <div style={{ fontFamily: 'Arial, sans-serif', padding: 24 }}>
      <h1>English Learning — Frontend</h1>
      <p>Minimal React + TypeScript app for Milestone 1.</p>

      <section style={{ marginTop: 24 }}>
        <h2>Backend status</h2>
        {loading && <p>Loading...</p>}
        {error && <p style={{ color: 'crimson' }}>Error: {error}</p>}
        {status && <p>Status: <strong>{status}</strong></p>}
      </section>
    </div>
  )
}
